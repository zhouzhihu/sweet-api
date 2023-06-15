package com.egrand.sweetapi.web.service.factory;

import com.egrand.sweetapi.core.LocalFileService;
import com.egrand.sweetapi.core.LocalFileServiceFactory;
import com.egrand.sweetapi.core.config.DynamicAPIProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 本地文件服务工厂
 */
@Component
public class LocalFileServiceFactoryImpl implements LocalFileServiceFactory {

    protected ApplicationContext applicationContext;

    @Autowired
    private DynamicAPIProperties properties;

    public LocalFileService getService(String ...type) {
        String fileType;
        if (null != type && type.length != 0) {
            fileType = type[0];
        } else{
            fileType = properties.getFileServiceType();
        }
        Map<String, LocalFileService> map = this.applicationContext.getBeansOfType(LocalFileService.class);
        for (String t : map.keySet()) {
            LocalFileService localFileService = map.get(t);
            if (localFileService.getType().equals(fileType))
                return localFileService;
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
