package com.egrand.sweetapi.plugin.mq;

import com.egrand.sweetapi.core.TenantService;
import com.egrand.sweetapi.plugin.mq.module.MQModule;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

@ComponentScans(value = {
        @ComponentScan(value = "com.egrand.sweetapi.starter.mq")
})
@Configuration
public class MQConfiguration {

    @Autowired
    private TenantService tenantService;

    @Bean
    @ConditionalOnMissingBean
    public MQModule mqModule(RabbitTemplate rabbitTemplate) {
        return new MQModule(rabbitTemplate, tenantService);
    }
}
