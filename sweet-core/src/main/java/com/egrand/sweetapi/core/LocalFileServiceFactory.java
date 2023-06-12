package com.egrand.sweetapi.core;

import org.springframework.context.ApplicationContextAware;

public interface LocalFileServiceFactory extends ApplicationContextAware {

    /**
     * 获取本地文件服务
     * @param type 类型
     * @return
     */
    LocalFileService getService(String ...type);
}
