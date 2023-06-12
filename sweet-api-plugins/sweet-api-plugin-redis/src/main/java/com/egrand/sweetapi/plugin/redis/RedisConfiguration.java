package com.egrand.sweetapi.plugin.redis;

import com.egrand.sweetapi.core.TenantService;
import com.egrand.sweetapi.plugin.redis.module.RedisModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@ComponentScans(value = {
        @ComponentScan(value = "com.egrand.sweetapi.starter.redis")
})
@Configuration
public class RedisConfiguration {

    @Autowired
    private TenantService tenantService;

    @Bean
    @ConditionalOnMissingBean
    public RedisModule redisModule(RedisTemplate redisTemplate) {
        return new RedisModule(redisTemplate, tenantService);
    }
}
