package com.egrand.sweetapi.starter.redis;

import com.egrand.sweetapi.starter.redis.core.config.MultiRedisProperties;
import com.egrand.sweetapi.starter.redis.core.rs.AbstractRoutingRedis;
import com.egrand.sweetapi.starter.redis.core.toolkit.DynamicRedisContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 核心动态Redis组件
 */
@Slf4j
public class DynamicRoutingRedis extends AbstractRoutingRedis implements InitializingBean, DisposableBean {

    /**
     * 所有数据库
     */
    private final Map<String, LettuceConnectionFactory> redisMap = new LinkedHashMap<>();

    /**
     * Redis配置
     */
    private final Map<String, RedisProperties> redisConfig;

    public DynamicRoutingRedis(Map<String, RedisProperties> redisConfig) {
        this.redisConfig = redisConfig;
    }

    /**
     * 获取当前Redis
     * @return
     */
    public LettuceConnectionFactory determineRedis() {
        return getRedis(DynamicRedisContextHolder.peek());
    }

    /**
     * 获取当前所有的Redis
     *
     * @return 当前所有Redis
     */
    public Map<String, LettuceConnectionFactory> getCurrentRedis() {
        return redisMap;
    }

    /**
     * 添加Redis
     *
     * @param rs         redis名称
     * @param redisProperties Redis属性
     */
    public synchronized void addRedis(String rs, RedisProperties redisProperties) {
        if (!redisMap.containsKey(rs)) {
            LettuceConnectionFactory lettuceConnectionFactory = lettuceConnectionFactory(redisProperties);
            lettuceConnectionFactory.afterPropertiesSet();
            redisMap.put(rs, lettuceConnectionFactory);
            log.info("dynamic-redis - load a redis named [{}] success", rs);
        } else {
            log.warn("dynamic-redis - load a redis named [{}] failed, because it already exist", rs);
        }
    }

    /**
     * 删除Redis
     *
     * @param rs Redis名称
     */
    public synchronized void removeRedis(String rs) {
        if (!StringUtils.hasText(rs)) {
            throw new RuntimeException("remove parameter could not be empty");
        }
//        if (MultiRedisProperties.DEFAULT.equals(rs)) {
//            throw new RuntimeException("could not remove primary redis");
//        }
        if (redisMap.containsKey(rs)) {
            LettuceConnectionFactory lettuceConnectionFactory = redisMap.get(rs);
            try {
                closeRedis(lettuceConnectionFactory);
            } catch (Exception e) {
                log.error("dynamic-redis - remove the redis named [{}]  failed", rs, e);
            }
            redisMap.remove(rs);
            log.info("dynamic-redis - remove the database named [{}] success", rs);
        } else {
            log.warn("dynamic-redis - could not find a database named [{}]", rs);
        }
    }

    /**
     * 获取Redis
     *
     * @param rs 数据源名称
     * @return Redis
     */
    public LettuceConnectionFactory getRedis(String rs) {
        if (StringUtils.isEmpty(rs)) {
            return determinePrimaryRedis(rs);
        } else if (redisMap.containsKey(rs)) {
            log.info("Redis数据源：{}", rs);
            return redisMap.get(rs);
        }
        return determinePrimaryRedis(rs);
    }

    @Override
    public void destroy() throws Exception {
        log.info("dynamic-redis start closing ....");
        for (Map.Entry<String, LettuceConnectionFactory> item : redisMap.entrySet()) {
            closeRedis(item.getValue());
        }
        redisMap.clear();
        if (null != redisConfig && redisConfig.size() != 0)
            redisConfig.clear();
        log.info("dynamic-redis all closed success,bye");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (null == redisConfig || redisConfig.size() == 0)
            return;
        for (Map.Entry<String, RedisProperties> dsItem : redisConfig.entrySet()) {
            addRedis(dsItem.getKey(), dsItem.getValue());
        }
    }

    /**
     * 创建LettuceConnectionFactory
     * @param redisProperties
     * @return
     */
    private LettuceConnectionFactory lettuceConnectionFactory(RedisProperties redisProperties) {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        // 设置选用的数据库号码
        redisConfiguration.setDatabase(redisProperties.getDatabase());
        // 设置 redis 数据库密码
        redisConfiguration.setPassword(redisProperties.getPassword());
        // 根据配置和客户端配置创建连接
        LettuceConnectionFactory factory = new LettuceConnectionFactory((RedisConfiguration) redisConfiguration);
        return factory;
    }

    private LettuceConnectionFactory determinePrimaryRedis(String rs) {
        if (StringUtils.isEmpty(rs))
            log.info("Redis数据源未指定，切换至默认default数据源");
        else
            log.info("Redis数据源：[{}]不存在，切换至默认default数据源", rs);
        if (!redisMap.containsKey(MultiRedisProperties.DEFAULT))
            throw new RuntimeException("未配置默认[default]的数据源！");
        return redisMap.get(MultiRedisProperties.DEFAULT);
    }

    private void closeRedis(LettuceConnectionFactory lettuceConnectionFactory) {
        lettuceConnectionFactory.destroy();
    }
}
