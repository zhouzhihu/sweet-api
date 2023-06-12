package com.egrand.sweetapi.modules;

import com.egrand.sweetapi.core.ModuleService;
import com.egrand.sweetapi.core.TenantService;
import com.egrand.sweetplugin.service.impl.PluginServiceFactory;
import org.pf4j.PluginStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ModuleServiceFactory extends PluginServiceFactory<ModuleService> {

    @Autowired
    private TenantService tenantService;

    /**
     * 插件包含的接口实例，由插件启动和停止来维护
     */
    private Map<String, List<ModuleService>> pluginBeanList = new HashMap<>();

    @Override
    protected Class getClazz() {
        return ModuleService.class;
    }

    @Override
    public Map<String, ModuleService> listPluginService() {
        Map<String, ModuleService> moduleServiceMap = super.listPluginService();
        Map<String, ModuleService> allModuleService = new HashMap<>();
        if (null != moduleServiceMap && moduleServiceMap.size() != 0)
            allModuleService.putAll(moduleServiceMap);
        pluginBeanList.values().forEach(moduleServiceList ->
                moduleServiceList.forEach(moduleService ->
                        allModuleService.put(moduleService.getType(), moduleService)));
        return allModuleService;
    }

    @Override
    public String getPrimaryKey() {
        return this.tenantService.getTenant();
    }

    @Override
    public void onPluginStarted(PluginStateEvent pluginStateEvent, List<ModuleService> beanList,
                                List<Class<ModuleService>> extensionClasses) {
        if (null == beanList || beanList.size() == 0)
            return;
        this.pluginBeanList.put(pluginStateEvent.getPlugin().getPluginId(), beanList);
    }

    @Override
    public void onPluginStopped(PluginStateEvent pluginStateEvent, List<ModuleService> beanList,
                                List<Class<ModuleService>> extensionClasses) {
        this.pluginBeanList.remove(pluginStateEvent.getPlugin().getPluginId());
    }

}
