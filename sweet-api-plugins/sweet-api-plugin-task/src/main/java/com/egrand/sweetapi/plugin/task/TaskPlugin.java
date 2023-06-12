package com.egrand.sweetapi.plugin.task;

import com.egrand.sweetplugin.SpringPlugin;
import org.pf4j.PluginWrapper;

/**
 * Sweet-API中Spring TaskScheduler插件定义，为Sweet-API提供定时任务操作接口
 */
public class TaskPlugin extends SpringPlugin {

    public TaskPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

}
