package com.egrand.sweetapi.spring.boot.starter;

import com.egrand.sweetapi.core.config.DynamicAPIProperties;
import com.egrand.sweetapi.core.service.impl.RequestDynamicRegistry;
import com.egrand.sweetapi.core.utils.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * 动态注册配置
 */
@Configuration
@AutoConfigureAfter(ModuleConfiguration.class)
public class DynamicRegistryConfiguration {

    @Autowired
    private DynamicAPIProperties properties;

    @Autowired
    @Lazy
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Bean
    @ConditionalOnMissingBean
    public RequestDynamicRegistry requestDynamicRegistry() throws NoSuchMethodException {
        return new RequestDynamicRegistry(Mapping.create(requestMappingHandlerMapping, ""), properties.isAllowOverride(), "");
    }
}
