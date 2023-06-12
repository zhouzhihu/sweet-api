package com.egrand.sweetapi.core;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 文件信息
 */
@Data
public class LocalFileInfo {

    /**
     * 主键
     */
    protected Long id;

    /**
     * UNID
     */
    private String unid;

    /**
     * 父ID
     */
    private Long parentId;

    /**
     * 名称
     */
    private String name;

    /**
     * 文件标识
     */
    private String code;

    /**
     * 文件服务器附件ID
     */
    private Long fileId;

    /**
     * 文件服务器附件类型
     */
    private String fileType;

    /**
     * 文件大小(KB)
     */
    private BigDecimal fileSize;

    /**
     * 文件服务器文件物理存储路径
     */
    private String filePath;

    /**
     * 本地存储路径
     */
    private String localFilePath;

    /**
     * 租户编码
     */
    private String tenant;
}
