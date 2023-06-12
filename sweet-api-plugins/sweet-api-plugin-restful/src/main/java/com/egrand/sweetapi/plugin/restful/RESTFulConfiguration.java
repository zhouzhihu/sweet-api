package com.egrand.sweetapi.plugin.restful;

import com.egrand.sweetapi.plugin.restful.adapter.RESTfulConnectionStore;
import com.egrand.sweetapi.plugin.restful.module.RESTfulModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Configuration
public class RESTFulConfiguration {
    private static final Charset UTF_8;

    @Bean
    @ConditionalOnMissingBean
    public RESTfulModule resTfulModule(RestTemplate restTemplate, RESTfulConnectionStore restfulConnectionStore) {
        return new RESTfulModule(restTemplate, restfulConnectionStore);
    }

    @Bean
    public OkHttp3ClientHttpRequestFactory httpRequestFactory() {
        OkHttp3ClientHttpRequestFactory requestFactory = new OkHttp3ClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(8000);
        requestFactory.setWriteTimeout(8000);
        return requestFactory;
    }

    @Bean
    @ConditionalOnMissingBean({RestTemplate.class})
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
        this.configMessageConverters(restTemplate.getMessageConverters());
        return restTemplate;
    }

    private void configMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8) {
            {
                setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
            }

            @Override
            public boolean supports(Class<?> clazz) {
                return true;
            }
        });
    }

    static {
        UTF_8 = StandardCharsets.UTF_8;
    }
}
