package com.egrand.sweetapi.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.egrand.sweetapi.core.TenantService;
import com.egrand.sweetapi.core.event.ApiEvent;
import com.egrand.sweetapi.core.event.EventAction;
import com.egrand.sweetapi.core.event.GroupEvent;
import com.egrand.sweetapi.core.model.*;
import com.egrand.sweetapi.core.service.impl.RequestDynamicRegistry;
import com.egrand.sweetapi.core.utils.Constants;
import com.egrand.sweetapi.web.mapper.ApiMapper;
import com.egrand.sweetapi.web.model.ApiFieldDTO;
import com.egrand.sweetapi.web.model.ApiSaveDTO;
import com.egrand.sweetapi.web.model.ResourceDTO;
import com.egrand.sweetapi.web.model.entity.Api;
import com.egrand.sweetapi.web.model.entity.ApiScript;
import com.egrand.sweetapi.web.service.ApiFieldService;
import com.egrand.sweetapi.web.service.ApiScriptService;
import com.egrand.sweetapi.web.service.ApiService;
import com.egrand.sweetapi.web.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static com.egrand.sweetapi.web.utils.Assert.isFalse;

/**
 * API 服务实现类
 */
@Service
@Slf4j
public class ApiServiceImpl extends ServiceImpl<ApiMapper, Api> implements ApiService {

    @Autowired
    private TenantService tenantService;

    @Autowired
    public ApplicationContext applicationContext;

    @Autowired
    private ApiFieldService apiFieldService;

    @Autowired
    private ApiScriptService apiScriptService;

    @Autowired
    private RequestDynamicRegistry requestDynamicRegistry;

    protected void pushEvent(Object event) {
        applicationContext.publishEvent(event);
    }

    @Override
    public ApiInfo getApiInfo(Long id, boolean addFolderInfo) {
        Api api = this.getById(id);
        if (null != api) {
            List<ApiFieldDTO> apiFieldDTOList = apiFieldService.getApiField(api.getId(), "");
            return this.buildApiInfo(api, apiFieldDTOList, addFolderInfo);
        }
        return null;
    }

    @Override
    public List<Api> listByType(String type) {
        QueryWrapper<Api> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Api::getType, type);
        String tenant = this.tenantService.getTenant();
        if (StrUtil.isNotEmpty(tenant)) {
            queryWrapper.lambda().eq(Api::getTenant, tenant);
        }
        return this.list(queryWrapper);
    }

    @Override
    public List<Api> listAllByType(String type) {
        QueryWrapper<Api> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Api::getType, type);
        return this.list(queryWrapper);
    }

    @Override
    public Api save(ApiSaveDTO apiSaveDTO) {
        if (null != apiSaveDTO.getId())
            return update(apiSaveDTO);
        boolean isApi = Api.TYPE_API.equalsIgnoreCase(apiSaveDTO.getType()) ? true : false;
        if (isApi) {
            // 判断是否存在相同路径的API
            isFalse(this.isHaveApi(apiSaveDTO.getParentId(), apiSaveDTO.getMethod(), apiSaveDTO.getPath()),
                    "存在【" + apiSaveDTO.getPath() + "】接口，请核对！");
        }
//        if (!isApi) {
//            // 检查是否同级下存在相同的文件夹路径
//            isFalse(this.isHaveFolder(apiSaveDTO.getParentId(), apiSaveDTO.getPath()),
//                    "存在相同【" + apiSaveDTO.getPath() + "】路径文件夹，请核对！");
//        }
        // 保存API
        Api api = new Api();
        BeanUtil.copyProperties(apiSaveDTO, api, false);
        //--- 添加租户标记
        String tenant = this.tenantService.getTenant();
        if (StrUtil.isNotEmpty(tenant)) {
            api.setTenant(tenant);
        }
        this.save(api);
        if (isApi) {
            // 保存API脚本和字段
            this.saveScriptAndField(api, apiSaveDTO);
        } else {
            // 保存路径字段
            if (null != apiSaveDTO.getPaths() && apiSaveDTO.getPaths().size() != 0)
                this.apiFieldService.save(RequestParamType.TYPE_PATH, api.getId(), apiSaveDTO.getPaths(), true, null);
        }
        if (Api.TYPE_API.equals(api.getType())) {
            this.pushEvent(new ApiEvent(api.getType(), EventAction.CREATE, this.getApiInfo(api.getId(), true)));
        } else {
            List<ApiInfo> childrenList = new ArrayList<>();
            this.getApiByGroup(api.getId(), childrenList);
            this.pushEvent(new GroupEvent(api.getType(), EventAction.CREATE, this.getApiInfo(api.getId(), true), childrenList));
        }
        return api;
    }

    private Api update(ApiSaveDTO apiSaveDTO) {
        Api api = this.getById(apiSaveDTO.getId());
        boolean isApi = Api.TYPE_API.equalsIgnoreCase(api.getType()) ? true : false;
        BeanUtil.copyProperties(apiSaveDTO, api, false);
        // 添加租户标记
        if (StrUtil.isEmpty(api.getTenant())) {
            String tenant = this.tenantService.getTenant();
            if (StrUtil.isNotEmpty(tenant)) {
                api.setTenant(tenant);
            }
        }
        this.updateById(api);
        if (isApi) {
            this.apiFieldService.deleteByApiId(api.getId(), null);
            this.saveScriptAndField(api, apiSaveDTO);
        } else {
            this.apiFieldService.deleteByApiId(api.getId(), null);
            // 保存路径字段
            if (null != apiSaveDTO.getPaths() && apiSaveDTO.getPaths().size() != 0)
                this.apiFieldService.save(RequestParamType.TYPE_PATH, api.getId(), apiSaveDTO.getPaths(), true, null);
        }
        if (isApi) {
            this.pushEvent(new ApiEvent(api.getType(), EventAction.SAVE, this.getApiInfo(apiSaveDTO.getId(), true)));
        } else {
            List<ApiInfo> childrenList = new ArrayList<>();
            this.getApiByGroup(api.getId(), childrenList);
            this.pushEvent(new GroupEvent(api.getType(), EventAction.SAVE, this.getApiInfo(api.getId(), true), childrenList));
        }
        return api;
    }

    @Override
    public Boolean move(Long id, Long folderId) {
        Api api = this.getById(id);
        isFalse(null == api, "不存在接口，请核对！");
        Api folderApi = this.getById(folderId);
        isFalse(null == folderApi, "不存在文件夹，请核对！");
        api.setParentId(folderId);
        boolean result = this.updateById(api);
        if (result) {
            if (Api.TYPE_API.equals(api.getType())) {
                this.pushEvent(new ApiEvent(api.getType(), EventAction.MOVE, this.getApiInfo(api.getId(), true)));
            } else {
                List<ApiInfo> childrenList = new ArrayList<>();
                this.getApiByGroup(api.getId(), childrenList);
                this.pushEvent(new GroupEvent(api.getType(), EventAction.MOVE, this.getApiInfo(api.getId(), true), childrenList));
            }
        }
        return result;
    }

    @Override
    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean delete(List<? extends Serializable> idList){
        isFalse(idList.isEmpty() || idList.size() > 1, "不存在接口，请核对！");
        Long id = (Long) idList.get(0);
        Api api = this.getById(id);
        isFalse(null == api, "不存在接口，请核对！");
        if (Api.TYPE_API.equalsIgnoreCase(api.getType())) {
            // 删除API脚本
            this.apiScriptService.deleteByApiId((Long) id);
            // 删除API字段
            this.apiFieldService.deleteByApiId((Long) id, null);
        } else {
            // 检查是否包含子元素
            isFalse(this.isHaveChildren(api.getId()),
                    "文件夹【" + api.getName() + "】存在元素，不能删除！");
            // 删除API字段
            this.apiFieldService.deleteByApiId((Long) id, null);
        }
        if (Api.TYPE_API.equals(api.getType())) {
            this.pushEvent(new ApiEvent(api.getType(), EventAction.DELETE, this.getApiInfo(api.getId(), true)));
        } else {
            List<ApiInfo> childrenList = new ArrayList<>();
            this.getApiByGroup(api.getId(), childrenList);
            this.pushEvent(new GroupEvent(api.getType(), EventAction.DELETE, this.getApiInfo(api.getId(), true), childrenList));
        }
        return super.removeByIds(idList);
    }

    @Override
    public List<Api> listByParentId(Long parentId) {
        QueryWrapper<Api> queryWrapper = new QueryWrapper<>();
        if (null != parentId) {
            queryWrapper.lambda().eq(Api::getParentId, parentId);
        } else {
            queryWrapper.lambda().isNull(Api::getParentId);
        }
        queryWrapper.lambda().orderByDesc(Api::getType).orderByAsc(Api::getOrderNo);
        return this.list(queryWrapper);
    }

    @Override
    public void export(List<ResourceDTO> resources, ByteArrayOutputStream os) throws IOException {
        if (null == resources || resources.size() == 0)
            return;
        List<ResourceDTO> groupRootList = resources.stream().
                filter(resourceDTO -> "api".equalsIgnoreCase(resourceDTO.getParentId())).collect(Collectors.toList());
        if (null == groupRootList || groupRootList.size() == 0)
            return;
        // 创建导出目录
        ClassPathResource tmpResource = new ClassPathResource("/tmp");
        String tmpPath = tmpResource.getFile().getPath();
        String exportPath = tmpPath + File.separator + System.currentTimeMillis();
        // 导出API
        String apiPath = exportPath + File.separator + "api";
        File exportFolder = new File(apiPath);
        if (!exportFolder.exists())
            exportFolder.mkdirs();
        for (ResourceDTO resourceDTO : groupRootList) {
            this.exportResource(exportFolder, resourceDTO, resources);
        }
        // TODO 导出连接
        this.createzipFile(exportPath, os);
    }

    @Override
    public Boolean upload(InputStream inputStream, String mode) throws IOException {
        Boolean full = Constants.UPLOAD_MODE_FULL.equals(mode);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry entry;
        JSONArray groupJSONArray = new JSONArray();
        JSONArray apiJSONArray = new JSONArray();
        while ((entry = zipInputStream.getNextEntry())!= null) {
            String name = entry.getName();
            log.info("name = " + name);
            // 导入API
            if (name.startsWith("api/") || name.startsWith("api\\")) {
                if (!name.endsWith("\\") && !name.endsWith("/")) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] byte_s = new byte[1024];
                    int num = -1;
                    while ((num = zipInputStream.read(byte_s, 0, byte_s.length)) > -1) {
                        byteArrayOutputStream.write(byte_s, 0, num);
                    }
                    String fileContent = new String(byteArrayOutputStream.toByteArray(), "UTF-8");
                    if (name.endsWith("group.json")) {
                        groupJSONArray.put(JSONUtil.parseObj(fileContent));
                    } else {
                        apiJSONArray.put(JSONUtil.parseObj(fileContent));
                    }
                }
            }
            // TODO 导入连接
            if (name.startsWith("connection/") || name.startsWith("connection\\")) {
                log.info("待处理连接导入");
            }
        }
        uploadApi(groupJSONArray, apiJSONArray, full);
        zipInputStream.close();
        return true;
    }

    @Override
    public List<Map<String, Object>> search(String keyword) {
        return this.apiScriptService.search(keyword);
    }

    @Override
    public Boolean initialize() {
        List<Api> apiList = this.listAllByType(Api.TYPE_API);
        apiList.forEach(api -> {
            ApiInfo apiInfo = this.getApiInfo(api.getId(), true);
            if (null != apiInfo)
                requestDynamicRegistry.register(apiInfo);
        });
        return true;
    }

    @Override
    public Boolean sync() {
        return this.initialize();
    }

    private void uploadApi(JSONArray groupJSONArray, JSONArray apiJSONArray, Boolean full) {
        List<ApiSaveDTO> groupList = new LinkedList<>();
        List<ApiSaveDTO> apiList = new ArrayList<>();
        groupJSONArray.forEach(o -> groupList.add(this.parseApiSaveDTO((JSONObject) o, Api.TYPE_FOLDER)));
        apiJSONArray.forEach(o -> apiList.add(this.parseApiSaveDTO((JSONObject) o, Api.TYPE_API)));
        // 记录分组ID对照关系(key:旧ID，value：新ID)
        Map<String, String> groupIdMap = new HashMap<>();
        if (groupList.size() != 0) {
            for (ApiSaveDTO apiSaveDTO : groupList) {
                Long uploadId = apiSaveDTO.getId();
                // 判断分组ID对照关系中是否存在当前ParentId，如果存在则替换为新的ParentId
                if (null != apiSaveDTO.getParentId() && groupIdMap.containsKey(String.valueOf(apiSaveDTO.getParentId()))) {
                    apiSaveDTO.setParentId(Long.valueOf(String.valueOf(groupIdMap.get(String.valueOf(apiSaveDTO.getParentId())))));
                }
                apiSaveDTO.setId(null);
                Api api = this.save(apiSaveDTO);
                groupIdMap.put(String.valueOf(uploadId), String.valueOf(api.getId()));
            }
        }
        if (apiList.size() != 0) {
            for (ApiSaveDTO apiSaveDTO : apiList) {
                // 判断分组ID对照关系中是否存在当前ParentId，如果存在则替换为新的ParentId
                if (null != apiSaveDTO.getParentId() && groupIdMap.containsKey(String.valueOf(apiSaveDTO.getParentId()))) {
                    apiSaveDTO.setParentId(Long.valueOf(groupIdMap.get(String.valueOf(apiSaveDTO.getParentId()))));
                }
                apiSaveDTO.setId(null);
                this.save(apiSaveDTO);
            }
        }
    }

    private ApiSaveDTO parseApiSaveDTO(JSONObject jsonObject, String type) {
        ApiSaveDTO apiSaveDTO = new ApiSaveDTO();
        apiSaveDTO.setId(jsonObject.getLong("id", -1L));
        apiSaveDTO.setParentId(jsonObject.getLong("groupId", null));
        apiSaveDTO.setType(type);
        apiSaveDTO.setName(jsonObject.getStr("name", ""));
        apiSaveDTO.setMethod(jsonObject.getStr("method", null));
        apiSaveDTO.setPath(jsonObject.getStr("path", null));
        apiSaveDTO.setRequestBody(jsonObject.getStr("requestBody", null));
        apiSaveDTO.setResponseBody(jsonObject.getStr("responseBody", null));
        apiSaveDTO.setScript(jsonObject.getStr("script", null));
        apiSaveDTO.setDescription(jsonObject.getStr("description", null));
        // 分析参数
        List<Parameter> parameterList = new ArrayList<>();
        JSONArray parameterJSONArray = jsonObject.getJSONArray("parameters");
        if (null != parameterJSONArray && parameterJSONArray.size() != 0) {
            parameterJSONArray.forEach(parameter -> parameterList.add(BeanUtil.toBean(parameter, Parameter.class)));
        }
        apiSaveDTO.setParameters(parameterList);
        // 分析头参数
        List<Header> headerList = new ArrayList<>();
        JSONArray headerJSONArray = jsonObject.getJSONArray("headers");
        if (null != headerJSONArray && headerJSONArray.size() != 0){
            headerJSONArray.forEach(parameter -> headerList.add(BeanUtil.toBean(parameter, Header.class)));
        }
        apiSaveDTO.setHeaders(headerList);
        // 分析路径参数
        List<Path> pathList = new ArrayList<>();
        JSONArray pathJSONArray = jsonObject.getJSONArray("paths");
        if (null != pathJSONArray && pathJSONArray.size() != 0) {
            pathJSONArray.forEach(parameter -> pathList.add(BeanUtil.toBean(parameter, Path.class)));
        }
        apiSaveDTO.setPaths(pathList);
        if (null != jsonObject.getStr("requestBodyDefinition")) {
            JSONArray requestBodyDefinitionJSONArray = jsonObject.getJSONArray("requestBodyDefinition");
            if (null != requestBodyDefinitionJSONArray && requestBodyDefinitionJSONArray.size() != 0) {
                BaseDefinition requestBodyDefinition = new BaseDefinition();
                requestBodyDefinition.setDataType(DataType.Object);
                List<BaseDefinition> children = new ArrayList<>();
                requestBodyDefinitionJSONArray.forEach(parameter -> children.add(BeanUtil.toBean(parameter, BaseDefinition.class)));
                requestBodyDefinition.setChildren(children);
                apiSaveDTO.setRequestBodyDefinition(requestBodyDefinition);
            }
        }
        // TODO 未处理响应体定义
        return apiSaveDTO;
    }

    /**
     * 创建zip文件
     *
     * @param source 被打包的文件目录
     * @param os 打包后存放的目录及文件名
     * @throws FileNotFoundException
     */
    private void createzipFile(String source, ByteArrayOutputStream os) {
        File file = new File(source);
        try {
            ZipOutputStream out = new ZipOutputStream(os);
            getZipFile(out, file, "");
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("创建zip文件失败", e);
        }
    }

    /**
     * 将对应文件夹下的文件及文件夹写入对应文件夹目录下
     *
     * @param out
     * @param file
     * @param pareFileName
     */
    private void getZipFile(ZipOutputStream out, File file, String pareFileName) {
        try {
            File files[] = file.listFiles();
            for (File dirFile : files) {
                String temPath = pareFileName;
                if (dirFile.isDirectory()) {
                    pareFileName += dirFile.getName() + File.separator;
                    ZipEntry zipEntry = new ZipEntry(pareFileName);
                    out.putNextEntry(zipEntry);
                    getZipFile(out, dirFile, pareFileName);
                } else {
                    pareFileName += dirFile.getName();
                    FileInputStream fi = new FileInputStream(dirFile);
                    BufferedInputStream origin = new BufferedInputStream(fi);
                    ZipEntry entry = new ZipEntry(pareFileName);
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read()) != -1) {
                        out.write(count);
                    }
                    origin.close();
                }
                pareFileName = temPath;
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new RuntimeException("压缩文件异常", e);
        }
    }

    private void exportResource(File exportFolder, ResourceDTO resource, List<ResourceDTO> resources) throws IOException {
        ApiInfo resourceApiInfo = this.getApiInfo(Long.valueOf(resource.getId()), false);
        if ("group".equalsIgnoreCase(resource.getType())) {
            // 创建分组文件夹
            File groupFolder = new File(exportFolder.getPath() + File.separator + resourceApiInfo.getName());
            if (!groupFolder.exists())
                groupFolder.mkdirs();
            // 创建分组描述文件
            File groupFile = new File(exportFolder.getPath() + File.separator + resourceApiInfo.getName() + File.separator + "group.json");
            if (!groupFile.exists())
                groupFile.createNewFile();
            FileUtil.writeUtf8String(JsonUtil.getInstance().writeValueAsString(resourceApiInfo), groupFile);
            // 查找子元素
            List<ResourceDTO> childrenList = resources.stream().
                    filter(resourceDTO -> resource.getId().equalsIgnoreCase(resourceDTO.getParentId())).collect(Collectors.toList());
            for (ResourceDTO resourceDTO : childrenList) {
                this.exportResource(groupFolder, resourceDTO, resources);
            }
        } else {
            // 创建API文件
            File apiFile = new File(exportFolder.getPath() + File.separator + resourceApiInfo.getName());
            if (!apiFile.exists())
                apiFile.createNewFile();
            FileUtil.writeUtf8String(JsonUtil.getInstance().writeValueAsString(resourceApiInfo), apiFile);
        }
    }

    /**
     * 查找指定文件夹下所有API
     * @param groupId 文件夹ID
     * @param apiInfoList API列表
     */
    private void getApiByGroup(Long groupId, List<ApiInfo> apiInfoList) {
        List<Api> apiList = this.listByParentId(groupId);
        apiList.forEach(api -> {
            if (Api.TYPE_FOLDER.equals(api.getType())) {
                this.getApiByGroup(api.getId(), apiInfoList);
            } else {
                apiInfoList.add(this.getApiInfo(api.getId(), true));
            }
        });
    }

    /**
     * 判断是否存在Api
     * @param parentId 文件夹ID
     * @param method 请求方式
     * @param path 请求路径
     * @return
     */
    private boolean isHaveApi(Long parentId, String method, String path) {
        // TODO 如果有文件夹，但是文件夹上没有设置路径，这里没有判断
        QueryWrapper<Api> queryWrapper = new QueryWrapper<>();
        if (null != parentId)
            queryWrapper.lambda().eq(Api::getParentId, parentId);
        else
            queryWrapper.lambda().isNull(Api::getParentId);
        queryWrapper.lambda().eq(Api::getMethod, method);
        queryWrapper.lambda().eq(Api::getPath, path);
        return this.count(queryWrapper) > 0;
    }

    /**
     * 判断是否存在文件夹
     * @param parentId 文件夹ID
     * @param path 文件夹路径
     * @return
     */
    private boolean isHaveFolder(Long parentId, String path) {
        QueryWrapper<Api> queryWrapper = new QueryWrapper<>();
        if (null != parentId)
            queryWrapper.lambda().eq(Api::getParentId, parentId);
        else
            queryWrapper.lambda().isNull(Api::getParentId);
        queryWrapper.lambda().eq(Api::getPath, path);
        return this.count(queryWrapper) > 0;
    }

    /**
     * 判断是否存在子元素
     * @param id ID
     * @return
     */
    private boolean isHaveChildren(Long id) {
        QueryWrapper<Api> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Api::getParentId, id);
        return this.count(queryWrapper) > 0;
    }

    /**
     * 保存运行脚本和字段
     * @param api api信息
     * @param apiSaveDTO 保存/更新实体
     */
    private void saveScriptAndField(Api api, ApiSaveDTO apiSaveDTO) {
        // 保存ApiScript
        this.apiScriptService.saveOrUpdate(api.getId(), apiSaveDTO.getScript());
        // 保存参数字段
        if (null != apiSaveDTO.getParameters() && apiSaveDTO.getParameters().size() != 0)
            this.apiFieldService.save(RequestParamType.TYPE_PARAMETER, api.getId(), apiSaveDTO.getParameters(), true, null);
        // 保存请求头字段
        if (null != apiSaveDTO.getHeaders() && apiSaveDTO.getHeaders().size() != 0)
            this.apiFieldService.save(RequestParamType.TYPE_HEADER, api.getId(), apiSaveDTO.getHeaders(), true, null);
        // 保存路径字段
        if (null != apiSaveDTO.getPaths() && apiSaveDTO.getPaths().size() != 0)
            this.apiFieldService.save(RequestParamType.TYPE_PATH, api.getId(), apiSaveDTO.getPaths(), true, null);
        // 保存请求字段
        if (null != apiSaveDTO.getRequestBodyDefinition() &&
                null != apiSaveDTO.getRequestBodyDefinition().getChildren() &&
                apiSaveDTO.getRequestBodyDefinition().getChildren().size() != 0)
            this.apiFieldService.save(RequestParamType.TYPE_REQUEST_BODY, api.getId(), apiSaveDTO.getRequestBodyDefinition().getChildren(), true, null);
        // 保存响应字段
        if (null != apiSaveDTO.getResponseBodyDefinition() && apiSaveDTO.getResponseBodyDefinition().size() != 0)
            this.apiFieldService.save(RequestParamType.TYPE_RESPONSE_BODY, api.getId(), apiSaveDTO.getResponseBodyDefinition(), true, null);
    }

    /**
     * 获取API文件夹信息
     * @param api api信息
     * @param path 路径
     * @param baseDefinitionList 文件夹路径上Path参数设置集合
     * @return 数组，第一个为文件夹全路径，第二个为文件夹路径上Path参数设置集合
     */
    private Object[] getApiFolder(Api api, String path, List<BaseDefinition> baseDefinitionList) {
        if (null != api.getParentId()) {
            Api parentApi = this.getById(api.getParentId());
            if (null != parentApi) {
                if (null != parentApi.getPath() && StrUtil.isNotEmpty(parentApi.getPath().replaceAll("\\s*", "")))
                    path = parentApi.getPath().replaceAll("\\s*", "") + path;
                List<ApiFieldDTO> apiFieldDTOList = apiFieldService.getApiField(parentApi.getId(), "");
                if (null != apiFieldDTOList && apiFieldDTOList.size() != 0) {
                    Map<String, List<BaseDefinition>> baseDefinitionMap = this.buildBaseDefinitions(apiFieldDTOList);
                    List<BaseDefinition> pathBaseDefinitionList = baseDefinitionMap.get(RequestParamType.TYPE_PATH.getValue());
                    if (null != pathBaseDefinitionList && pathBaseDefinitionList.size() != 0) {
                        baseDefinitionList.addAll(pathBaseDefinitionList);
                    }
                }
                return this.getApiFolder(parentApi, path, baseDefinitionList);
            }
        }
        return new Object[]{path, baseDefinitionList};
    }

    /**
     * 构建API的ApiInfo信息
     * @param api api信息
     * @param apiFieldDTOList Api包含字段数据
     * @param addFolderInfo 是否添加文件夹信息（路径和请求路径参数）
     * @return
     */
    private ApiInfo buildApiInfo(Api api, List<ApiFieldDTO> apiFieldDTOList, boolean addFolderInfo) {
        // 构建ApiInfo
        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setId(String.valueOf(api.getId()));
        apiInfo.setGroupId(null == api.getParentId() || 0L == api.getParentId().longValue() ? null : String.valueOf(api.getParentId()));
        apiInfo.setName(api.getName());
        if (addFolderInfo) {
            // 获取文件夹
            String parentPath = "";
            List<BaseDefinition> parentDefinition = new ArrayList<>();
            Object[] objects = this.getApiFolder(api, parentPath, parentDefinition);
            if (null != objects) {
                parentPath = (String) objects[0];
                parentDefinition = (List<BaseDefinition>) objects[1];
            }
            // 增加文件夹路径，构建完整的API路径
            apiInfo.setPath(StrUtil.isEmpty(parentPath) ? api.getPath() : parentPath + api.getPath());
            // 添加文件夹的Path设置
            for (BaseDefinition baseDefinition : parentDefinition) {
                List<Path> pathList = apiInfo.getPaths();
                if (null == pathList || pathList.size() == 0)
                    pathList = new ArrayList<>();
                pathList.add(BeanUtil.toBean(baseDefinition, Path.class));
                apiInfo.setPaths(pathList);
            }
        } else {
            apiInfo.setPath(api.getPath());
        }
        // 设置API原始路径
        apiInfo.setOriginalPath(api.getPath());
        ApiScript apiScript = this.apiScriptService.loadByApiId(api.getId());
        if (null != apiScript)
            apiInfo.setScript(apiScript.getScript());
        apiInfo.setMethod(api.getMethod());
        apiInfo.setRequestBody(null == api.getRequestBody() ? "{}" : api.getRequestBody());
        apiInfo.setResponseBody(null == api.getResponseBody() ? "{}" : api.getResponseBody());
        if (null != apiFieldDTOList && apiFieldDTOList.size() != 0) {
            Map<String, List<BaseDefinition>> baseDefinitionMap = this.buildBaseDefinitions(apiFieldDTOList);
            if (null != baseDefinitionMap && baseDefinitionMap.size() != 0) {
                // 构建请求参数
                List<BaseDefinition> parameterBaseDefinitionList = baseDefinitionMap.get(RequestParamType.TYPE_PARAMETER.getValue());
                if (null != parameterBaseDefinitionList && parameterBaseDefinitionList.size() != 0) {
                    List<Parameter> parameterList = new ArrayList<>();
                    for (BaseDefinition baseDefinition : parameterBaseDefinitionList) {
                        parameterList.add(BeanUtil.toBean(baseDefinition, Parameter.class));
                    }
                    apiInfo.setParameters(parameterList);
                }
                // 构建请求头参数
                List<BaseDefinition> headerBaseDefinitionList = baseDefinitionMap.get(RequestParamType.TYPE_HEADER.getValue());
                if (null != headerBaseDefinitionList && headerBaseDefinitionList.size() != 0) {
                    List<Header> headerList = new ArrayList<>();
                    for (BaseDefinition baseDefinition : headerBaseDefinitionList) {
                        headerList.add(BeanUtil.toBean(baseDefinition, Header.class));
                    }
                    apiInfo.setHeaders(headerList);
                }
                // 构建路径参数
                List<BaseDefinition> pathBaseDefinitionList = baseDefinitionMap.get(RequestParamType.TYPE_PATH.getValue());
                if (null != pathBaseDefinitionList && pathBaseDefinitionList.size() != 0) {
                    List<Path> pathList = apiInfo.getPaths();
                    if (null == pathList || pathList.size() == 0)
                        pathList = new ArrayList<>();
                    for (BaseDefinition baseDefinition : pathBaseDefinitionList) {
                        pathList.add(BeanUtil.toBean(baseDefinition, Path.class));
                    }
                    apiInfo.setPaths(pathList);
                }
                // 构建请求体参数
                List<BaseDefinition> requestBodyBaseDefinitionList = baseDefinitionMap.get(RequestParamType.TYPE_REQUEST_BODY.getValue());
                if (null != requestBodyBaseDefinitionList && requestBodyBaseDefinitionList.size() != 0) {
                    List<BaseDefinition> baseDefinitionList = new ArrayList<>();
                    for (BaseDefinition baseDefinition : requestBodyBaseDefinitionList) {
                        // 获取字段下面所有子字段树
                        List<ApiFieldDTO> childrenList = this.apiFieldService.getChildren(api.getId(),
                                RequestParamType.TYPE_REQUEST_BODY.getValue(), baseDefinition.getFieldId());
                        if (null != childrenList && childrenList.size() != 0) {
                            baseDefinition.setChildren(this.getChildren(childrenList));
                        }
                        baseDefinitionList.add(baseDefinition);
                    }
                    apiInfo.setRequestBodyDefinition(baseDefinitionList);
                }
                // 构建响应体参数
                List<BaseDefinition> responseBodyBaseDefinitionList = baseDefinitionMap.get(RequestParamType.TYPE_RESPONSE_BODY.getValue());
                if (null != responseBodyBaseDefinitionList && responseBodyBaseDefinitionList.size() != 0) {
                    List<BaseDefinition> baseDefinitionList = new ArrayList<>();
                    for (BaseDefinition baseDefinition : responseBodyBaseDefinitionList) {
                        // 获取字段下面所有子字段树
                        List<ApiFieldDTO> childrenList = this.apiFieldService.getChildren(api.getId(),
                                RequestParamType.TYPE_RESPONSE_BODY.getValue(), baseDefinition.getFieldId());
                        if (null != childrenList && childrenList.size() != 0) {
                            baseDefinition.setChildren(this.getChildren(childrenList));
                        }
                        baseDefinitionList.add(baseDefinition);
                    }
                    apiInfo.setResponseBodyDefinition(baseDefinitionList);
                }
            }
        }
        apiInfo.setDescription(api.getDescription());
        return apiInfo;
    }

    /**
     * 将子字段树转换为BaseDefinition树结构
     * @param childrenList 子字段树
     * @return
     */
    private List<BaseDefinition> getChildren(List<ApiFieldDTO> childrenList) {
        List<BaseDefinition> baseDefinitionList = new ArrayList<>();
        for (ApiFieldDTO apiFieldDTO : childrenList) {
            BaseDefinition baseDefinition = this.buildBaseDefinition(apiFieldDTO);
            if (apiFieldDTO.getChilds() != null && apiFieldDTO.getChilds().size() != 0) {
                baseDefinition.setChildren(this.getChildren(apiFieldDTO.getChilds()));
            }
            baseDefinitionList.add(baseDefinition);
        }
        return baseDefinitionList;
    }

    /**
     * 按参数请求类型（请求参数、路径参数、请求头...）分组将ApiField转换为BaseDefinition
     * @param apiFieldDTOList
     * @return
     */
    private Map<String, List<BaseDefinition>> buildBaseDefinitions(List<ApiFieldDTO> apiFieldDTOList) {
        Map<String, List<BaseDefinition>> baseDefinitionMap = new HashMap<>();
        for (ApiFieldDTO apiFieldDTO : apiFieldDTOList) {
            String fieldType = apiFieldDTO.getFieldType();
            BaseDefinition baseDefinition = this.buildBaseDefinition(apiFieldDTO);
            if (baseDefinitionMap.containsKey(fieldType)) {
                List<BaseDefinition> baseDefinitionList = baseDefinitionMap.get(fieldType);
                baseDefinitionList.add(baseDefinition);
                baseDefinitionMap.put(fieldType, baseDefinitionList);
            } else {
                List<BaseDefinition> baseDefinitionList = new ArrayList<>();
                baseDefinitionList.add(baseDefinition);
                baseDefinitionMap.put(fieldType, baseDefinitionList);
            }
        }
        return baseDefinitionMap;
    }

    /**
     * 将ApiField转换为BaseDefinition
     * @param apiFieldDTO
     * @return
     */
    private BaseDefinition buildBaseDefinition(ApiFieldDTO apiFieldDTO) {
        BaseDefinition baseDefinition = new BaseDefinition();
        baseDefinition.setFieldId(apiFieldDTO.getId());
        baseDefinition.setName(apiFieldDTO.getName());
        baseDefinition.setValue(apiFieldDTO.getValue());
        baseDefinition.setDataType(Arrays.stream(DataType.values()).filter(v -> v.getValue().equals(apiFieldDTO.getType())).findFirst().get());
        baseDefinition.setRequired(apiFieldDTO.getRequired() == 1 ? true : false);
        baseDefinition.setDefaultValue(apiFieldDTO.getDefaultValue());
        baseDefinition.setValidateType(apiFieldDTO.getValidateType());
        baseDefinition.setExpression(apiFieldDTO.getExpression());
        baseDefinition.setError(apiFieldDTO.getError());
        baseDefinition.setDescription(apiFieldDTO.getDescription());
        return baseDefinition;
    }



}
