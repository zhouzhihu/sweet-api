package com.egrand.sweetapi.core;

import lombok.Data;

import java.io.Serializable;

/**
 * 连接扩展属性
 */
@Data
public class ConnectionExtendConfigInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 值
     */
    private String value;
}
