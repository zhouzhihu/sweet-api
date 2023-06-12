package com.egrand.sweetapi.spring.boot.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.SimpleDateFormat;

@Configuration
@AutoConfigureBefore(DynamicApiAutoConfiguration.class)
public class JacksonConfig {

    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder)
    {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        // 全局配置序列化返回 JSON 处理
        SimpleModule simpleModule = new SimpleModule();
        //JSON Long ==> String
        //simpleModule.addSerializer(Long.class, ToStringSerializer.instance);

        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        //simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        //objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }

}
