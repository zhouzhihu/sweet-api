package com.egrand.sweetapi.starter.redis.core.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(MultiRedisProperties.PREFIX)
public class MultiRedisProperties {

    public static final String PREFIX = "spring.redis";

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
    private Map<String, RedisProperties> multi;
}
