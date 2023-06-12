package com.egrand.sweetapi.starter.mq.service;

/**
 * MQ从交换机到队列失败后的回调函数
 */
public interface EgdMQE2QCallback {
    /**
     * 返回消息
     * @param message
     * @param replyCode
     * @param replyText
     * @param exchange
     * @param routingKey
     */
    void returnedMessage(Object message, int replyCode, String replyText,
                         String exchange, String routingKey);
}
