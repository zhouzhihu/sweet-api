package com.egrand.sweetapi.plugin.es;

import com.egrand.sweetapi.core.TenantService;
import com.egrand.sweetapi.plugin.es.module.ESModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

@ComponentScans(value = {
        @ComponentScan(value = "com.egrand.sweetapi.starter.es")
})
@Configuration
public class ESConfiguration {

    @Autowired
    private TenantService tenantService;

    @Bean
    @ConditionalOnMissingBean
    public ESModule esModule() {
        return new ESModule(tenantService);
    }
}
