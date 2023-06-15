package com.egrand.sweetapi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.egrand.sweetapi.core.LocalFileInfo;
import com.egrand.sweetapi.web.model.FileSaveDTO;
import com.egrand.sweetapi.web.model.PluginAttachment;
import com.egrand.sweetapi.web.model.entity.File;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * 文件管理服务类
 */
public interface FileService extends IService<File> {

    /**
     * 保存/更新File
     * @param fileSaveDTO 文件保存DTO
     * @return
     */
    File save(FileSaveDTO fileSaveDTO);

    /**
     * 保存File
     * @param pluginAttachment 附件信息
     * @param parentId 父ID
     * @param key 关键字
     * @return
     */
    File save(PluginAttachment pluginAttachment, Long parentId, String key);

    /**
     * 批量删除
     * @param ids id集合
     * @return
     */
    boolean delete(List<? extends Serializable> ids);

    /**
     * 上传文件
     * @param file 文件
     * @param parentId 父ID
     * @param code 文件标识
     * @return
     */
    LocalFileInfo upload(MultipartFile file, Long parentId, String code) throws IOException;

    /**
     * 下载文件
     * @param code 关键字
     */
    ResponseEntity<?> download(String code) throws IOException;

    /**
     * 查询子文件对象
     * @param parentId 父ID
     * @return
     */
    List<File> listChildren(Long parentId);

    /**
     * 按关键字查找
     * @param key 关键字
     * @return
     */
    File getByKey(String key);
}
