package com.egrand.sweetapi.starter.es.configuration;

import com.egrand.sweetapi.starter.es.boot.DynamicESStarter;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 动态ES核心自动配置类
 */
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties({MultiESProperties.class})
@ConditionalOnProperty(prefix = MultiESProperties.PREFIX, name = "enableMulti", havingValue = "true", matchIfMissing = true)
public class DynamicESAutoConfiguration {

    private final MultiESProperties multiESProperties;

    @Bean
    @ConditionalOnMissingBean
    public DynamicESStarter dynamicESStarter() {
        return new DynamicESStarter(multiESProperties.getMulti());
    }
}
