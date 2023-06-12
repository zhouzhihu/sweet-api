package com.egrand.sweetapi.starter.mq;

import com.egrand.sweetapi.starter.mq.config.MultiMQProperties;
import com.egrand.sweetapi.starter.mq.factory.AbstractRoutingMQ;
import com.egrand.sweetapi.starter.mq.toolkit.DynamicMQContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.util.StringUtils;

import java.util.Map;

@Slf4j
public class DynamicRoutingMQ extends AbstractRoutingMQ implements InitializingBean, DisposableBean {

    private final Map<String, RabbitProperties> mqConfig;

    public DynamicRoutingMQ(Map<String, RabbitProperties> mqConfig) {
        this.mqConfig = mqConfig;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return null;
    }

    /**
     * 添加MQ
     *
     * @param mq MQ名称
     * @param rabbitProperties MQ属性
     */
    public synchronized void addMQ(String mq, RabbitProperties rabbitProperties) {
        ConnectionFactory connectionFactory = this.getTargetConnectionFactory(mq);
        if (null == connectionFactory) {
            connectionFactory = connectionFactory(rabbitProperties);
            this.addTargetConnectionFactory(mq, connectionFactory);
            log.info("dynamic-MQ - load a MQ named [{}] success", mq);
            return;
        }
        log.warn("dynamic-MQ - load a MQ named [{}] failed, because it already exist", mq);
    }

    /**
     * 删除MQ
     *
     * @param mq mq名称
     */
    public synchronized void removeMQ(String mq) {
        if (!StringUtils.hasText(mq)) {
            throw new RuntimeException("remove parameter could not be empty");
        }
        ConnectionFactory connectionFactory = this.getTargetConnectionFactory(mq);
        if (null != connectionFactory) {
            this.removeTargetConnectionFactory(mq);
            this.mqConfig.remove(mq);
            log.info("dynamic-MQ - remove the MQ named [{}] success", mq);
        } else {
            log.warn("dynamic-MQ - could not find a MQ named [{}]", mq);
        }
    }

    /**
     * 获取MQ
     *
     * @param mq mq名称
     * @return
     */
    public ConnectionFactory getMQ(String mq) {
        if (StringUtils.isEmpty(mq)) {
            return determinePrimaryMQ(mq);
        }
        ConnectionFactory connectionFactory = this.getTargetConnectionFactory(mq);
        if (null != connectionFactory)
            return connectionFactory;
        return determinePrimaryMQ(mq);
    }

    @Override
    protected ConnectionFactory determineTargetConnectionFactory() {
        return getMQ(DynamicMQContextHolder.peek());
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (null == mqConfig || mqConfig.size() == 0)
            return;
        for (Map.Entry<String, RabbitProperties> dsItem : mqConfig.entrySet()) {
            addMQ(dsItem.getKey(), dsItem.getValue());
        }
    }

    private ConnectionFactory determinePrimaryMQ(String mq) {
        if (StringUtils.isEmpty(mq))
            log.info("MQ数据源未指定，切换至默认default数据源");
        else
            log.info("MQ数据源：[{}]不存在，切换至默认default数据源", mq);
        ConnectionFactory connectionFactory = this.getTargetConnectionFactory(MultiMQProperties.DEFAULT);
        if (null != connectionFactory)
            return connectionFactory;
        throw new RuntimeException("未配置默认[default]的MQ数据源！");
    }

    private ConnectionFactory connectionFactory(RabbitProperties rabbitProperties) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitProperties.getHost());
        connectionFactory.setPort(rabbitProperties.getPort());
        connectionFactory.setUsername(rabbitProperties.getUsername());
        connectionFactory.setPassword(rabbitProperties.getPassword());
        connectionFactory.setPublisherConfirmType(rabbitProperties.getPublisherConfirmType());
        connectionFactory.setPublisherReturns(rabbitProperties.isPublisherReturns());
        connectionFactory.setVirtualHost(rabbitProperties.getVirtualHost());
        return connectionFactory;
    }
}
