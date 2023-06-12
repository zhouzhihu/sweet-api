package com.egrand.sweetapi.core;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface LocalFileService {

    /**
     * 文件服务-本地文件类型
     */
    String FILE_SERVICE_TYPE_LOCAL = "LOCAL";

    /**
     * 文件服务-ATM类型
     */
    String FILE_SERVICE_TYPE_ATM = "ATM";

    /**
     * 获取临时目录文件
     * @param fileName 文件名
     * @return 路径
     * @throws IOException
     */
    default String getTmpFilePath(String fileName) throws IOException {
        return this.getPath("/tmp", fileName);
    }

    /**
     * 获取模板目录文件
     * @param fileName 文件名
     * @return 路径
     * @throws IOException
     */
    default String getTplFilePath(String fileName) throws IOException {
        return this.getPath("/tpl", fileName);
    }

    /**
     * 上传模板文件
     * @param file 文件
     * @return 上传后路径
     * @throws IOException
     */
    default String uploadTemplateFile(MultipartFile file) throws IOException {
        String tplFilePath = this.getTplFilePath(file.getOriginalFilename());
        java.io.File targetFile = new java.io.File(tplFilePath);
        file.transferTo(targetFile);
        return tplFilePath;
    }

    /**
     * 获取ClassPathResource指定文件夹文件路径
     * @param folderName 文件夹名称
     * @param fileName 文件名称
     * @return 路径
     * @throws IOException
     */
    default String getPath(String folderName, String fileName) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(folderName);
        String tmpPath = classPathResource.getFile().getPath();
        String extendName = "";
        String name = "";
        if (fileName.lastIndexOf(".") != -1) {
            extendName = fileName.substring(fileName.lastIndexOf(".") + 1);
            name = fileName.substring(0, fileName.lastIndexOf("."));
        } else {
            name = fileName;
        }
        return tmpPath + java.io.File.separator + name + System.currentTimeMillis() + "." + extendName;
    }

    /**
     * 上传文件
     * @param file 文件
     * @param parentId 父文件夹ID
     * @param key 关键字
     * @return
     */
    LocalFileInfo upload(MultipartFile file, Long parentId, String key) throws IOException;

    /**
     * 根据关键字获取文件路径
     * @param key 关键字
     * @return
     */
    String getFilePath(String key) throws IOException;

    /**
     * 根据关键字下载文件
     * @param key 关键字
     * @return
     * @throws IOException
     */
    ResponseEntity<?> download(String key) throws IOException;

    /**
     * 获取类型
     * @return
     */
    abstract String getType();
}
