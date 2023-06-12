package com.egrand.sweetapi.plugin.excel;

import com.egrand.sweetapi.core.LocalFileServiceFactory;
import com.egrand.sweetapi.plugin.excel.module.ExcelModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExcelConfiguration {

    @Autowired
    private LocalFileServiceFactory localFileServiceFactory;

    @Bean
    @ConditionalOnMissingBean
    public ExcelModule excelModule() {
        return new ExcelModule(this.localFileServiceFactory);
    }
}
