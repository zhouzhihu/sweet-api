package com.egrand.sweetapi.plugin.task;

import com.egrand.sweetapi.core.ApiActuatorService;
import com.egrand.sweetapi.plugin.task.module.TaskModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableConfigurationProperties(TaskConfig.class)
public class TaskConfiguration {

    private final TaskConfig config;

    @Autowired
    private ApiActuatorService apiActuatorService;

    public TaskConfiguration(TaskConfig config) {
        this.config = config;
    }

    @Bean
    @ConditionalOnMissingBean
    public ThreadPoolTaskScheduler poolTaskScheduler() {
        TaskConfig.Shutdown shutdown = config.getShutdown();
        ThreadPoolTaskScheduler poolTaskScheduler = null;
        if(config.isEnable()){
            poolTaskScheduler = new ThreadPoolTaskScheduler();
            poolTaskScheduler.setPoolSize(config.getPool().getSize());
            poolTaskScheduler.setWaitForTasksToCompleteOnShutdown(shutdown.isAwaitTermination());
            if(shutdown.getAwaitTerminationPeriod() != null){
                poolTaskScheduler.setAwaitTerminationSeconds((int) shutdown.getAwaitTerminationPeriod().getSeconds());
            }
            poolTaskScheduler.setThreadNamePrefix(config.getThreadNamePrefix());
            poolTaskScheduler.initialize();
        }
        return poolTaskScheduler;
    }

    @Bean
    @ConditionalOnMissingBean
    public TaskModule taskModule(ThreadPoolTaskScheduler poolTaskScheduler) {

        return new TaskModule(apiActuatorService, poolTaskScheduler);
    }
}
