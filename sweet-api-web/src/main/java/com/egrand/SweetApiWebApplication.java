package com.egrand;

import com.egrand.sweetapi.web.config.SweetInitializerImpl;
import com.egrand.sweetplugin.spring.boot.starter.annotations.EnableEgdPlugin;
import org.frameworkset.elasticsearch.boot.BBossESAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude={
        RedisAutoConfiguration.class,
        RedisRepositoriesAutoConfiguration.class,
        BBossESAutoConfiguration.class,
        RabbitAutoConfiguration.class
})
@EnableEgdPlugin
@Import({SweetInitializerImpl.class})
@MapperScan(basePackages = "com.egrand.sweetapi.web.mapper")
public class SweetApiWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(SweetApiWebApplication.class, args);
    }
}