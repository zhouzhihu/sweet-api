package com.egrand.sweetapi.starter.wechat.config;

import com.egrand.sweetapi.starter.wechat.DynamicRoutingWxCp;
import com.egrand.sweetapi.starter.wechat.core.WxCpTemplate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(WxCpProperties.class)
@ConditionalOnProperty(prefix = WxCpProperties.PREFIX, name = "enableMulti", havingValue = "true", matchIfMissing = true)
public class WxCpConfiguration {

    private final WxCpProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public DynamicRoutingWxCp dynamicRoutingWxCp() {
        DynamicRoutingWxCp dynamicRoutingRedis = new DynamicRoutingWxCp(properties.getMulti());
        return dynamicRoutingRedis;
    }

    @Bean
    @ConditionalOnMissingBean
    public WxCpTemplate wxCpTemplate(DynamicRoutingWxCp dynamicRoutingWxCp) {
        WxCpTemplate wxCpTemplate = new WxCpTemplate();
        wxCpTemplate.setWxCpServiceFactory(dynamicRoutingWxCp);
        return wxCpTemplate;
    }
}
