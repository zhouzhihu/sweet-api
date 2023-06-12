package com.egrand.sweetapi.core.initialize;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * 初始化接口，程序启动后额外的初始化动作，需要集成者自行实现
 */
public abstract class SweetInitializer implements ApplicationListener<ApplicationEvent> {

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ApplicationReadyEvent) {
            this.initialize();
        }
    }

    /**
     * 启动后额外的初始化动作
     * @return
     */
    protected abstract void initialize();
}
