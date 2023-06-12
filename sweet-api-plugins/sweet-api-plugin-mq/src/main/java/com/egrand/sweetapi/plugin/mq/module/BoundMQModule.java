package com.egrand.sweetapi.plugin.mq.module;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.function.Function;

public class BoundMQModule extends MQModule {

    private String mqKey = "";

    public BoundMQModule(RabbitTemplate rabbitTemplate, String mqKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.mqKey = mqKey;
    }

    @Override
    public <T> T execute(Function<String, T> function) {
        return function.apply(mqKey);
    }
}
