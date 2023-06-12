package com.egrand.sweetapi.starter.es.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(MultiESProperties.PREFIX)
public class MultiESProperties {

    public static final String PREFIX = "spring.elasticsearch";

    /**
     * 默认连接必须配置，配置 key 为 default
     */
    public static final String DEFAULT = "default";

    /**
     * 是否启用多Redis配置
     */
    private boolean enableMulti = false;

    /**
     * 多Redis配置
     */
    private Map<String, ESProperties> multi;
}
