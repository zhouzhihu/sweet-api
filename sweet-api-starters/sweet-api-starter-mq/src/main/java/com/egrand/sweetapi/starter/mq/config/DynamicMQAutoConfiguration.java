package com.egrand.sweetapi.starter.mq.config;

import com.egrand.sweetapi.starter.mq.DynamicRoutingMQ;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.TimeZone;

/**
 * 动态MQ核心自动配置类
 */
@Slf4j
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties({MultiMQProperties.class})
@ConditionalOnProperty(prefix = MultiMQProperties.PREFIX, name = "enableMulti", havingValue = "true", matchIfMissing = true)
public class DynamicMQAutoConfiguration {

    private final MultiMQProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public DynamicRoutingMQ dynamicRoutingMQ() {
        DynamicRoutingMQ dynamicRoutingMQ = new DynamicRoutingMQ(properties.getMulti());
        return dynamicRoutingMQ;
    }

    /**
     * 构建egdRabbitTemplate，每次调用都返回新的RabbitTemplate对象
     * 主要考虑是在RabbitTemplate增加了自己的回调，不污染公共RabbitTemplate
     * @param dynamicRoutingMQ
     * @return
     */
    @Bean
    @Scope("prototype")
    public RabbitTemplate rabbitTemplate(DynamicRoutingMQ dynamicRoutingMQ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(dynamicRoutingMQ);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        // TODO 默认设置为true，这里需要考虑配置后初始化
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

    /**
     * 消息转换配置
     * @return
     */
    private Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        objectMapper.registerModule(javaTimeModule);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        //设置转换的实体类可见性[避免属性访问权问题所导致的缺少字段]
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter(objectMapper);
        return jackson2JsonMessageConverter;
    }
}
