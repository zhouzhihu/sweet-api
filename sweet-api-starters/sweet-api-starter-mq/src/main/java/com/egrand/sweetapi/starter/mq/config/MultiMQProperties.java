package com.egrand.sweetapi.starter.mq.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(MultiMQProperties.PREFIX)
public class MultiMQProperties {

    public static final String PREFIX = "spring.rabbitmq";

    /**
     * 默认连接必须配置，配置 key 为 default
     */
    public static final String DEFAULT = "default";

    /**
     * 是否启用多MQ配置
     */
    private boolean enableMulti = false;

    /**
     * 多MQ配置
     */
    private Map<String, RabbitProperties> multi;
}
