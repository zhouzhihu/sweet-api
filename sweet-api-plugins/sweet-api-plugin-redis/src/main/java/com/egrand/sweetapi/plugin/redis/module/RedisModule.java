package com.egrand.sweetapi.plugin.redis.module;

import cn.hutool.core.util.StrUtil;
import com.egrand.sweetapi.core.ModuleService;
import com.egrand.sweetapi.core.TenantService;
import com.egrand.sweetapi.starter.redis.core.toolkit.DynamicRedisContextHolder;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.function.Supplier;

public class RedisModule implements ModuleService {

    protected RedisTemplate redisTemplate;

    private TenantService tenantService;

    public RedisModule(RedisTemplate redisTemplate, TenantService tenantService) {
        this.redisTemplate = redisTemplate;
        this.tenantService = tenantService;
    }

    public RedisModule(){}

    public BoundRedisModule rs(String rsKey) {
        return new BoundRedisModule(this.redisTemplate, rsKey);
    }

    public Boolean set(Object key, Object value) {
        return this.execute(() -> {
            redisTemplate.opsForValue().set(key, value);
            return true;
        });
    }

    public Object get(Object key) {
        return this.execute(() -> redisTemplate.opsForValue().get(key));
    }

    public <T> T execute(Supplier<T> supplier) {
        try {
            String tenant = this.tenantService.getTenant();
            if (StrUtil.isNotEmpty(tenant)) {
                DynamicRedisContextHolder.push(tenant);
            }
            return supplier.get();
        } finally {
            DynamicRedisContextHolder.poll();
        }
    }

    @Override
    public String getType() {
        return "redis";
    }
}
