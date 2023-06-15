package com.egrand.sweetapi.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.egrand.sweetapi.core.LocalFileInfo;
import com.egrand.sweetapi.core.LocalFileService;
import com.egrand.sweetapi.web.model.PluginAttachment;
import com.egrand.sweetapi.web.model.entity.File;
import com.egrand.sweetapi.web.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.egrand.sweetapi.web.utils.Assert.isFalse;

/**
 * 本地文件管理
 */
@Service
public class LocalFileServiceImpl implements LocalFileService {

    @Autowired
    private FileService fileService;

    @Override
    public LocalFileInfo upload(MultipartFile file, Long parentId, String key) throws IOException {
        isFalse(null != this.fileService.getByKey(key), "存在相同的【" + key + "】关键字,请修改！");
        PluginAttachment pluginAttachment = new PluginAttachment();
        String filePath = this.uploadTemplateFile(file);
        pluginAttachment.setFileName(file.getOriginalFilename());
        pluginAttachment.setFileType(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1));
        pluginAttachment.setFilePath(filePath);
        pluginAttachment.setFileSize(new BigDecimal(Files.size(Paths.get(filePath))));
        pluginAttachment.setParentUnid(IdUtil.simpleUUID());
        File localFile = this.fileService.save(pluginAttachment, parentId, key);
        localFile.setLocalFilePath(filePath);
        this.fileService.updateById(localFile);
        return BeanUtil.copyProperties(localFile, LocalFileInfo.class);
    }

    @Override
    public String getFilePath(String key) throws IOException {
        File file = this.fileService.getByKey(key);
        isFalse(null == file, "未查找到指定[" + key + "]的文件！");
        String localFilePath = file.getLocalFilePath();
        if (StrUtil.isNotEmpty(localFilePath) && new java.io.File(localFilePath).exists()) {
            return localFilePath;
        } else {
            throw new IOException("未找到[" + localFilePath + "]路径的文件！");
        }
    }

    @Override
    public ResponseEntity<?> download(String key) throws IOException {
        File file = this.fileService.getByKey(key);
        isFalse(null == file, "未查找到指定[" + key + "]的文件！");
        InputStream is = cn.hutool.core.io.FileUtil.getInputStream(file.getLocalFilePath());
        //创建字节数组
        byte[] buffer = new byte[is.available()];
        //将流读到字节数组中
        is.read(buffer);
        is.close();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +
                        URLEncoder.encode(file.getName(), "UTF-8") + (StrUtil.isNotEmpty(file.getFileType()) ? "." + file.getFileType() : ""))
                .body(buffer);
    }

    @Override
    public String getType() {
        return FILE_SERVICE_TYPE_LOCAL;
    }
}
