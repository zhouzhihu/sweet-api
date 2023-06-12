package com.egrand.sweetapi.starter.redis.core.rs;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * 抽象动态获取Redis
 */
public abstract class AbstractRoutingRedis implements RedisConnectionFactory, ReactiveRedisConnectionFactory {

    /**
     * 子类实现决定最终Redis
     *
     * @return Redis
     */
    protected abstract LettuceConnectionFactory determineRedis();

    @Override
    public ReactiveRedisConnection getReactiveConnection() {
        return determineRedis().getReactiveConnection();
    }

    @Override
    public ReactiveRedisClusterConnection getReactiveClusterConnection() {
        return determineRedis().getReactiveClusterConnection();
    }

    @Override
    public RedisConnection getConnection() {
        return determineRedis().getConnection();
    }

    @Override
    public RedisClusterConnection getClusterConnection() {
        return determineRedis().getClusterConnection();
    }

    @Override
    public boolean getConvertPipelineAndTxResults() {
        return determineRedis().getConvertPipelineAndTxResults();
    }

    @Override
    public RedisSentinelConnection getSentinelConnection() {
        return determineRedis().getSentinelConnection();
    }

    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException e) {
        return determineRedis().translateExceptionIfPossible(e);
    }
}
