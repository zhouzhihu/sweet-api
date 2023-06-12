package com.egrand.sweetapi.plugin.mq.adapter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.egrand.sweetapi.core.ConnectionAdapteService;
import com.egrand.sweetapi.core.ConnectionBaseInfo;
import com.egrand.sweetapi.starter.mq.DynamicRoutingMQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MQ连接转换
 */
@Component
@Slf4j
public class MQConnectionAdapteServiceImpl implements ConnectionAdapteService {
    @Autowired
    private DynamicRoutingMQ dynamicRoutingMQ;

    @Override
    public Boolean initialize(List<ConnectionBaseInfo> connectionBaseInfoList) {
        if (null != connectionBaseInfoList && connectionBaseInfoList.size() != 0) {
            connectionBaseInfoList.forEach(connectionBaseDTO -> this.save(connectionBaseDTO));
        }
        return true;
    }

    @Override
    public Boolean destroy() {
        return null;
    }

    @Override
    public Boolean save(ConnectionBaseInfo connectionBaseInfo) {
        MQConnectionInfo mqConnectionDTO = (MQConnectionInfo) connectionBaseInfo;
        RabbitProperties rabbitProperties = new RabbitProperties();
        rabbitProperties.setHost(mqConnectionDTO.getHost());
        rabbitProperties.setPort(Integer.valueOf(mqConnectionDTO.getPort()).intValue());
        rabbitProperties.setUsername(mqConnectionDTO.getUsername());
        rabbitProperties.setPassword(mqConnectionDTO.getPassword());
        rabbitProperties.setVirtualHost(mqConnectionDTO.getVirtualHost());
        rabbitProperties.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.valueOf(mqConnectionDTO.getPublisherConfirmType()));
        rabbitProperties.setPublisherReturns(1 == mqConnectionDTO.getPublisherReturns());
        rabbitProperties.getTemplate().setMandatory(1 == mqConnectionDTO.getMandatory());
        rabbitProperties.getListener().getSimple().setAcknowledgeMode(AcknowledgeMode.valueOf(mqConnectionDTO.getAcknowledgeMode()));
        this.dynamicRoutingMQ.addMQ(connectionBaseInfo.getKey(), rabbitProperties);
        return true;
    }

    @Override
    public Boolean update(ConnectionBaseInfo connectionBaseInfo, String oldKey) {
        this.delete(oldKey);
        return this.save(connectionBaseInfo);
    }

    @Override
    public Boolean delete(String key) {
        this.dynamicRoutingMQ.removeMQ(key);
        return true;
    }

    @Override
    public String encode(ConnectionBaseInfo connectionBaseInfo) {
        // 组装Connection的config配置
        MQConnectionInfo mqConnectionDTO = (MQConnectionInfo) connectionBaseInfo;
        JSONObject jsonObject = new JSONObject();
        if (StrUtil.isNotEmpty(mqConnectionDTO.getHost())) {
            jsonObject.putOnce("host", mqConnectionDTO.getHost());
        }
        if (StrUtil.isNotEmpty(mqConnectionDTO.getPort())) {
            jsonObject.putOnce("port", mqConnectionDTO.getPort());
        }
        if (StrUtil.isNotEmpty(mqConnectionDTO.getUsername())) {
            jsonObject.putOnce("username", mqConnectionDTO.getUsername());
        }
        if (StrUtil.isNotEmpty(mqConnectionDTO.getPassword())) {
            jsonObject.putOnce("password", mqConnectionDTO.getPassword());
        }
        if (StrUtil.isNotEmpty(mqConnectionDTO.getVirtualHost())) {
            jsonObject.putOnce("virtualHost", mqConnectionDTO.getVirtualHost());
        }
        if (StrUtil.isNotEmpty(mqConnectionDTO.getPublisherConfirmType())) {
            jsonObject.putOnce("publisherConfirmType", mqConnectionDTO.getPublisherConfirmType());
        }
        jsonObject.putOnce("publisherReturns", mqConnectionDTO.getPublisherReturns());
        jsonObject.putOnce("mandatory", mqConnectionDTO.getMandatory());
        if (StrUtil.isNotEmpty(mqConnectionDTO.getAcknowledgeMode())) {
            jsonObject.putOnce("acknowledgeMode", mqConnectionDTO.getAcknowledgeMode());
        }
        return JSONUtil.toJsonStr(jsonObject);
    }

    @Override
    public ConnectionBaseInfo decode(String config, ConnectionBaseInfo connectionBaseInfo) {
        MQConnectionInfo mqConnectionDTO = new MQConnectionInfo(connectionBaseInfo);
        if (StrUtil.isNotEmpty(config)) {
            JSONObject jsonObject = JSONUtil.parseObj(config);
            mqConnectionDTO.setHost(jsonObject.getStr("host", ""));
            mqConnectionDTO.setPort(jsonObject.getStr("port", ""));
            mqConnectionDTO.setUsername(jsonObject.getStr("username", ""));
            mqConnectionDTO.setPassword(jsonObject.getStr("password", ""));
            mqConnectionDTO.setVirtualHost(jsonObject.getStr("virtualHost", ""));
            mqConnectionDTO.setPublisherConfirmType(jsonObject.getStr("publisherConfirmType", ""));
            mqConnectionDTO.setPublisherReturns(jsonObject.getInt("publisherReturns", 1));
            mqConnectionDTO.setMandatory(jsonObject.getInt("mandatory", 1));
            mqConnectionDTO.setAcknowledgeMode(jsonObject.getStr("acknowledgeMode", ""));
        }
        return mqConnectionDTO;
    }

    @Override
    public ConnectionBaseInfo test(String connectionInfo) {
        try {
            MQConnectionInfo mqConnectionDTO = JSONUtil.toBean(JSONUtil.parseObj(connectionInfo), MQConnectionInfo.class);
            com.rabbitmq.client.ConnectionFactory connectionFactory = new com.rabbitmq.client.ConnectionFactory();
            connectionFactory.setHost(mqConnectionDTO.getHost());
            connectionFactory.setPort(Integer.valueOf(mqConnectionDTO.getPort()).intValue());
            connectionFactory.setUsername(mqConnectionDTO.getUsername());
            connectionFactory.setPassword(mqConnectionDTO.getPassword());
            connectionFactory.newConnection();
            return mqConnectionDTO;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getType() {
        return "MQ";
    }
}
