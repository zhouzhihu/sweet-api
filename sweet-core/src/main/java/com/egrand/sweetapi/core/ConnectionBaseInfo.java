package com.egrand.sweetapi.core;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 连接基础配置
 */
@Data
public class ConnectionBaseInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    protected Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 编码
     */
    private String key;

    /**
     * 类型
     */
    private String type;

    /**
     * 请求超时
     */
    private int timeout;

    /**
     * 扩展配置
     */
    private List<ConnectionExtendConfigInfo> extendConfigList;
}
