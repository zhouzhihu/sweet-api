package com.egrand.sweetapi.plugin.redis;

import com.egrand.sweetplugin.SpringPlugin;
import org.pf4j.PluginWrapper;

/**
 * Sweet-API中Redis插件定义，为Sweet-API提供Redis操作接口
 */
public class RedisPlugin extends SpringPlugin {

    public RedisPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

}
