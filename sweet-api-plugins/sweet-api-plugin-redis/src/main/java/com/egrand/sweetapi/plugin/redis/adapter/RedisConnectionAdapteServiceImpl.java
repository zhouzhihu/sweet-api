package com.egrand.sweetapi.plugin.redis.adapter;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.egrand.sweetapi.core.ConnectionAdapteService;
import com.egrand.sweetapi.core.ConnectionBaseInfo;
import com.egrand.sweetapi.starter.redis.DynamicRoutingRedis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.time.Duration;
import java.util.List;

/**
 * Redis连接转换
 */
@Component
@Slf4j
public class RedisConnectionAdapteServiceImpl implements ConnectionAdapteService {

    @Autowired
    private DynamicRoutingRedis dynamicRoutingRedis;

    @Override
    public Boolean initialize(List<ConnectionBaseInfo> connectionBaseInfoList) {
        if (null != connectionBaseInfoList && connectionBaseInfoList.size() != 0) {
            connectionBaseInfoList.forEach(connectionBaseDTO -> this.save(connectionBaseDTO));
        }
        return true;
    }

    @Override
    public Boolean destroy() {
        return true;
    }

    @Override
    public Boolean save(ConnectionBaseInfo connectionBaseInfo) {
        RedisConnectionInfo redisConnectionDTO = (RedisConnectionInfo) connectionBaseInfo;
        RedisProperties redisProperties = new RedisProperties();
        redisProperties.setHost(redisConnectionDTO.getHost());
        redisProperties.setPort(Integer.valueOf(redisConnectionDTO.getPort()).intValue());
        redisProperties.setPassword(redisConnectionDTO.getPassword());
        redisProperties.setDatabase(Integer.valueOf(redisConnectionDTO.getDatabase()).intValue());
        redisProperties.setTimeout(Duration.ofMinutes(redisConnectionDTO.getTimeout()));
        this.dynamicRoutingRedis.addRedis(connectionBaseInfo.getKey(), redisProperties);
        return true;
    }

    @Override
    public Boolean update(ConnectionBaseInfo connectionBaseInfo, String oldKey) {
        this.delete(oldKey);
        return this.save(connectionBaseInfo);
    }

    @Override
    public Boolean delete(String key) {
        if (this.dynamicRoutingRedis.getCurrentRedis().containsKey(key))
            this.dynamicRoutingRedis.removeRedis(key);
        return true;
    }

    @Override
    public String encode(ConnectionBaseInfo connectionBaseInfo) {
        // 组装Connection的config配置
        RedisConnectionInfo redisConnectionDTO = (RedisConnectionInfo) connectionBaseInfo;
        JSONObject jsonObject = new JSONObject();
        if (StrUtil.isNotEmpty(redisConnectionDTO.getHost())) {
            jsonObject.putOnce("host", redisConnectionDTO.getHost());
        }
        if (StrUtil.isNotEmpty(redisConnectionDTO.getPort())) {
            jsonObject.putOnce("port", redisConnectionDTO.getPort());
        }
        if (StrUtil.isNotEmpty(redisConnectionDTO.getDatabase())) {
            jsonObject.putOnce("database", redisConnectionDTO.getDatabase());
        }
        if (StrUtil.isNotEmpty(redisConnectionDTO.getPassword())) {
            jsonObject.putOnce("password", redisConnectionDTO.getPassword());
        }
        return JSONUtil.toJsonStr(jsonObject);
    }

    @Override
    public ConnectionBaseInfo decode(String config, ConnectionBaseInfo connectionBaseInfo) {
        RedisConnectionInfo redisConnectionDTO = new RedisConnectionInfo(connectionBaseInfo);
        if (StrUtil.isNotEmpty(config)) {
            JSONObject jsonObject = JSONUtil.parseObj(config);
            redisConnectionDTO.setHost(jsonObject.getStr("host", ""));
            redisConnectionDTO.setPort(jsonObject.getStr("port", ""));
            redisConnectionDTO.setDatabase(jsonObject.getStr("database", ""));
            redisConnectionDTO.setPassword(jsonObject.getStr("password", ""));
        }
        return redisConnectionDTO;
    }

    @Override
    public ConnectionBaseInfo test(String connectionInfo) {
        try {
            RedisConnectionInfo redisConnectionDTO = JSONUtil.toBean(JSONUtil.parseObj(connectionInfo), RedisConnectionInfo.class);
            Jedis jedis = new Jedis(redisConnectionDTO.getHost(), Integer.valueOf(redisConnectionDTO.getPort()));
            if (StrUtil.isNotEmpty(redisConnectionDTO.getPassword()))
                jedis.auth(redisConnectionDTO.getPassword());
            jedis.ping();
            return redisConnectionDTO;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getType() {
        return "REDIS";
    }
}
