package com.egrand.sweetapi.starter.redis.core.config;

import com.egrand.sweetapi.starter.redis.DynamicRoutingRedis;
import com.egrand.sweetapi.starter.redis.core.serializer.RedisObjectSerializer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 动态Redis核心自动配置类
 */
@Slf4j
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties({MultiRedisProperties.class})
@ConditionalOnProperty(prefix = MultiRedisProperties.PREFIX, name = "enableMulti", havingValue = "true", matchIfMissing = true)
public class DynamicRedisAutoConfiguration {

    private final MultiRedisProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public DynamicRoutingRedis dynamicRoutingRedis() {
        DynamicRoutingRedis dataSource = new DynamicRoutingRedis(properties.getMulti());
        return dataSource;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(DynamicRoutingRedis dynamicRoutingRedis) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(dynamicRoutingRedis);
        RedisObjectSerializer redisObjectSerializer = new RedisObjectSerializer();
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(redisObjectSerializer);
        redisTemplate.setValueSerializer(redisObjectSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
