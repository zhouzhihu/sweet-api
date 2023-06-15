package com.egrand.sweetapi.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.egrand.sweetapi.core.LocalFileInfo;
import com.egrand.sweetapi.core.LocalFileServiceFactory;
import com.egrand.sweetapi.core.TenantService;
import com.egrand.sweetapi.core.config.DynamicAPIProperties;
import com.egrand.sweetapi.modules.servlet.ResponseModule;
import com.egrand.sweetapi.web.mapper.FileMapper;
import com.egrand.sweetapi.web.model.FileSaveDTO;
import com.egrand.sweetapi.web.model.PluginAttachment;
import com.egrand.sweetapi.web.model.entity.File;
import com.egrand.sweetapi.web.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import static com.egrand.sweetapi.web.utils.Assert.isFalse;
import static com.egrand.sweetapi.core.LocalFileService.FILE_SERVICE_TYPE_ATM;

/**
 * 文件管理 服务实现类
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {
    
    @Autowired
    private TenantService tenantService;

    @Autowired
    private ResponseModule responseModule;

    @Autowired
    private LocalFileServiceFactory localFileServiceFactory;

    @Autowired
    private DynamicAPIProperties properties;

    @Override
    public File save(FileSaveDTO fileSaveDTO) {
        if (null == fileSaveDTO.getId() || -1L == fileSaveDTO.getId().longValue()) {
            // 新增
            if (StrUtil.isNotEmpty(fileSaveDTO.getCode()))
                isFalse(null != this.getByKey(fileSaveDTO.getCode()),
                        "存在相同的【" + fileSaveDTO.getCode() + "】关键字,请修改！");
            File file = new File();
            BeanUtil.copyProperties(fileSaveDTO, file, false);
            //--- 添加租户标记
            String tenant = this.tenantService.getTenant();
            if (StrUtil.isNotEmpty(tenant)) {
                file.setTenant(tenant);
            }
            this.save(file);
            return file;
        } else {
            // 更新
            if (StrUtil.isNotEmpty(fileSaveDTO.getCode())) {
                File keyFile = this.getByKey(fileSaveDTO.getCode());
                isFalse(null != keyFile && keyFile.getId().longValue() != fileSaveDTO.getId(),
                        "存在相同的【" + fileSaveDTO.getCode() + "】关键字,请修改！");
            }
            File file = this.getById(fileSaveDTO.getId());
            BeanUtil.copyProperties(fileSaveDTO, file, false);
            //--- 添加租户标记
            if (StrUtil.isEmpty(file.getTenant())) {
                String tenant = this.tenantService.getTenant();
                if (StrUtil.isNotEmpty(tenant)) {
                    file.setTenant(tenant);
                }
            }
            this.updateById(file);
            return file;
        }
    }

    /**
     * 构建File对象
     * @param pluginAttachment 附件上传后信息
     * @param parentId 父ID
     * @param key 文件标识
     * @return
     */
    @Override
    public File save(PluginAttachment pluginAttachment, Long parentId, String key) {
        File entry = new File();
        entry.setUnid(pluginAttachment.getParentUnid());
        entry.setParentId(parentId);
        String fileName = pluginAttachment.getFileName();
        String fileType = pluginAttachment.getFileType();
        if (StrUtil.isNotEmpty(fileName)) {
            fileName = fileName.substring(0, -1 != fileName.lastIndexOf(fileType) ? fileName.lastIndexOf(fileType) - 1 : fileName.length());
        }
        entry.setName(fileName);
        entry.setCode(key);
        entry.setFileId(pluginAttachment.getId());
        entry.setFilePath(pluginAttachment.getFilePath());
        entry.setFileSize(pluginAttachment.getFileSize());
        entry.setFileType(fileType);
        //--- 添加租户标记
        String tenant = this.tenantService.getTenant();
        if (StrUtil.isNotEmpty(tenant)) {
            entry.setTenant(tenant);
        }
        this.save(entry);
        return entry;
    }

    @Override
    public LocalFileInfo upload(MultipartFile file, Long parentId, String code) throws IOException {
        return this.localFileServiceFactory.getService().upload(file, parentId, code);
    }

    @Override
    public ResponseEntity<?> download(String code) throws IOException {
        return this.localFileServiceFactory.getService().download(code);
    }

    @Override
    public List<File> listChildren(Long parentId) {
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        if (-1L == parentId.longValue()) {
            queryWrapper.lambda().isNull(File::getParentId);
        } else {
            queryWrapper.lambda().eq(File::getParentId, parentId);
        }
        String tenant = this.tenantService.getTenant();
        if (StrUtil.isNotEmpty(tenant)) {
            queryWrapper.lambda().eq(File::getTenant, tenant);
        }
        queryWrapper.orderByAsc("unid");
        return this.list(queryWrapper);
    }

    @Override
    public File getByKey(String key) {
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(File::getCode, key);
        String tenant = this.tenantService.getTenant();
        if (StrUtil.isNotEmpty(tenant)) {
            queryWrapper.lambda().eq(File::getTenant, tenant);
        }
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean delete(List<? extends Serializable> ids) {
        isFalse(null == ids || ids.size() == 0, "请指定要删除的对象！");
        ids.forEach(id -> {
            isFalse(this.isHaveChildren((Long) id), "存在子对象，不能删除！");
        });
        return super.removeByIds(ids);
    }

    /**
     * 判断是否具有子对象
     * @param id id
     * @return
     */
    private boolean isHaveChildren(Long id) {
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(File::getParentId, id);
        return this.count(queryWrapper) > 0 ? true : false;
    }
}
