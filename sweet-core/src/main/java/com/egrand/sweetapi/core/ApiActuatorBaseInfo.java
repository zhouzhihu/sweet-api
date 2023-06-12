package com.egrand.sweetapi.core;

import lombok.Data;

import java.io.Serializable;

/**
 * 执行器基础配置
 */
@Data
public class ApiActuatorBaseInfo implements Serializable {
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
     * 类型
     */
    private String type;

    /**
     * 编码
     */
    private String key;

    /**
     * 执行用户
     */
    private String userName;

    /**
     * api id
     */
    private Long apiId;

    /**
     * 请求超时
     */
    private int timeout;
}
