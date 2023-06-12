package com.egrand.sweetapi.starter.mq.callback;

import com.egrand.sweetapi.starter.mq.service.EgdMQP2ECallback;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class MQConfirmCallback implements RabbitTemplate.ConfirmCallback {
    private EgdMQP2ECallback egdMQP2ECallback;

    public MQConfirmCallback(EgdMQP2ECallback egdMQP2ECallback){
        this.egdMQP2ECallback = egdMQP2ECallback;
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = "-1";
        if(null != correlationData)
            id = correlationData.getId();
        if(ack)
            egdMQP2ECallback.onSuccess(id, ack, cause);
        else
            egdMQP2ECallback.onFailure(id, ack, cause);
    }
}
