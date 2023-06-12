package com.egrand.sweetapi.plugin.restful.adapter;

import com.egrand.sweetapi.core.ConnectionBaseInfo;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * RESTful连接配置
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class RESTfulConnectionInfo extends ConnectionBaseInfo {

    public RESTfulConnectionInfo(ConnectionBaseInfo connectionBaseInfo) {
        this.setId(connectionBaseInfo.getId());
        this.setName(connectionBaseInfo.getName());
        this.setKey(connectionBaseInfo.getKey());
        this.setType(connectionBaseInfo.getType());
        this.setExtendConfigList(connectionBaseInfo.getExtendConfigList());
        this.setTimeout(connectionBaseInfo.getTimeout());
    }

    /**
     * 服务基础地址
     */
    private String baseUrl;

    /**
     * 请求格式(JSON/XML)
     */
    private String requestFormat;

    /**
     * 请求编码(ASCII/GB2312/BIG5/JIS/UTF-8/Unicode)
     */
    private String requestEncode;

    /**
     * 响应格式(JSON/XML)
     */
    private String responseFormat;

    /**
     * 响应编码(ASCII/GB2312/BIG5/JIS/UTF-8/Unicode)
     */
    private String responseEncode;

    /**
     * URL参数
     */
    private Map<String, String> urlParams;

    /**
     * HTTP头
     */
    private Map<String, String> httpHeaders;

    /**
     * Form数据
     */
    private Map<String, String> formData;
}
