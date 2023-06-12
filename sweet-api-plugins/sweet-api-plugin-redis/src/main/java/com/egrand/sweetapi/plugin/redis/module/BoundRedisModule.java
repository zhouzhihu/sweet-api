package com.egrand.sweetapi.plugin.redis.module;

import cn.hutool.core.util.StrUtil;
import com.egrand.sweetapi.starter.redis.core.toolkit.DynamicRedisContextHolder;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.function.Supplier;

public class BoundRedisModule extends RedisModule {

    private String rsKey = "";

    public BoundRedisModule(RedisTemplate redisTemplate, String rsKey) {
        this.redisTemplate = redisTemplate;
        this.rsKey = rsKey;
    }

    @Override
    public <T> T execute(Supplier<T> supplier) {
        try {
            if (StrUtil.isNotEmpty(rsKey)) {
                DynamicRedisContextHolder.push(rsKey);
            }
            return supplier.get();
        } finally {
            DynamicRedisContextHolder.poll();
        }
    }
}
