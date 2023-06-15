package com.egrand.sweetapi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.egrand.sweetapi.core.model.ApiInfo;
import com.egrand.sweetapi.web.model.ApiSaveDTO;
import com.egrand.sweetapi.web.model.ResourceDTO;
import com.egrand.sweetapi.web.model.entity.Api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Api服务类
 */
public interface ApiService extends IService<Api> {

    /**
     * 获得ApiInfo
     * @param id 接口ID
     * @param addFolderInfo 是否添加文件夹信息（路径和请求路径参数）
     * @return
     */
    ApiInfo getApiInfo(Long id, boolean addFolderInfo);

    /**
     * 根据类型加载API
     * @param type 类型（api:api；folder：文件夹）
     * @return
     */
    List<Api> listByType(String type);

    /**
     * 根据类型加载API
     * @param type 类型（api:api；folder：文件夹）
     * @return
     */
    List<Api> listAllByType(String type);

    /**
     * 保存实体
     * @param apiSaveDTO 实体保存对象
     * @return
     */
    Api save(ApiSaveDTO apiSaveDTO);

    /**
     * 删除
     * @param idList id集合
     * @return
     */
    boolean delete(List<? extends Serializable> idList);

    /**
     * 根据父ID加载API
     * @param parentId 父ID
     * @return
     */
    List<Api> listByParentId(Long parentId);

    /**
     * 移动
     * @param id
     * @param folderId
     * @return
     */
    Boolean move(Long id, Long folderId);

    /**
     * 导出
     * @param resources 选择资源
     * @param os
     */
    void export(List<ResourceDTO> resources, ByteArrayOutputStream os) throws IOException;

    /**
     * 导入
     * @param inputStream 输入流
     * @param mode 模式
     * @return
     * @throws IOException
     */
    Boolean upload(InputStream inputStream, String mode) throws IOException;

    /**
     * 搜索API内容
     * @param keyword 关键字
     * @return
     */
    List<Map<String, Object>> search(String keyword);

    /**
     * 程序启动时，初始化连接
     * @return
     */
    Boolean initialize();

    /**
     * 同步API接口
     * @return
     */
    Boolean sync();
}
