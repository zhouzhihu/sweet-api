package com.egrand.sweetapi.plugin.wechat;

import com.egrand.sweetapi.core.LocalFileServiceFactory;
import com.egrand.sweetapi.core.TenantService;
import com.egrand.sweetapi.plugin.wechat.module.WechatModule;
import com.egrand.sweetapi.starter.wechat.core.WxCpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

@ComponentScans(value = {
        @ComponentScan(value = "com.egrand.sweetapi.starter.wechat")
})
@Configuration
public class WechatConfiguration {

    @Autowired
    private LocalFileServiceFactory localFileServiceFactory;

    @Autowired
    private TenantService tenantService;

    @Bean
    @ConditionalOnMissingBean
    public WechatModule wechatModule(WxCpTemplate wxCpTemplate) {
        return new WechatModule(wxCpTemplate, tenantService, localFileServiceFactory);
    }
}
