package com.egrand.sweetapi.spring.boot.starter;

import com.egrand.sweetapi.core.config.DynamicAPIProperties;
import com.egrand.sweetapi.core.config.DynamicConfiguration;
import com.egrand.sweetapi.core.interceptor.ResultProvider;
import com.egrand.sweetapi.core.service.impl.RequestDynamicRegistry;
import com.egrand.sweetapi.core.utils.Mapping;
import com.egrand.sweetapi.core.web.RequestHandler;
import com.egrand.sweetapi.modules.ModuleServiceFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.stream.Collectors;

/**
 * 动态API统一配置
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(DynamicAPIProperties.class)
@Import({ModuleConfiguration.class, DynamicRegistryConfiguration.class})
@AllArgsConstructor
public class DynamicApiAutoConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    @Lazy
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    public DynamicApiAutoConfiguration() {

    }

    @Bean
    public DynamicConfiguration dynamicConfiguration(DynamicAPIProperties properties,
                                                     ResultProvider resultProvider,
                                                     RequestDynamicRegistry requestDynamicRegistry,
                                                     HttpMessageConverters httpMessageConverters,
                                                     ModuleServiceFactory moduleServiceFactory) {
        DynamicConfiguration configuration = new DynamicConfiguration();
        configuration.setResultProvider(resultProvider);
        configuration.setModuleServiceFactory(moduleServiceFactory);
        configuration.setThrowException(properties.isThrowException());
        configuration.setDisabledUnknownParameter(properties.isDisabledUnknownParameter());

        requestDynamicRegistry.setHandler(new RequestHandler(configuration, requestDynamicRegistry,
                httpMessageConverters));
        Mapping mapping = Mapping.create(requestMappingHandlerMapping, "");
        return configuration;
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        return new HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
