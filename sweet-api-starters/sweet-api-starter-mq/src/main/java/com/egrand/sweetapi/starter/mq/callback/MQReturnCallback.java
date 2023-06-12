package com.egrand.sweetapi.starter.mq.callback;

import com.egrand.sweetapi.starter.mq.service.EgdMQE2QCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j
public class MQReturnCallback implements RabbitTemplate.ReturnCallback {
    private EgdMQE2QCallback egdMQE2QCallback;

    public MQReturnCallback(EgdMQE2QCallback egdMQE2QCallback){
        this.egdMQE2QCallback = egdMQE2QCallback;
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.info("返回消息回调:{} 应答代码:{} 回复文本:{} 交换器:{} 路由键:{}", message, replyCode, replyText, exchange, routingKey);
        this.egdMQE2QCallback.returnedMessage(message, replyCode, replyText, exchange, routingKey);
    }
}
