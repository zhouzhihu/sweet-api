package com.egrand.sweetapi.starter.mq.service;

import java.util.Map;

public interface EgdMQHandler {
    /**
     * 这里写业务处理逻辑
     * @param headers 请求头
     * @param message 消息内容
     * @return 若声明消费者时关闭autoAck：返回true表示消息成功消费，返回false表示消息处理失败，会自动重新排队重试
     *          若声明消费者时开启autoAck，所有消息都会自动确认，这个返回值无效
     */
    boolean receive(Map<String, Object> headers, String message);
}
