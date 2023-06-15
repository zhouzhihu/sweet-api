package com.egrand.sweetapi.web.service.factory;

import cn.hutool.core.util.StrUtil;
import com.egrand.sweetapi.core.ApiActuatorAdapteService;
import com.egrand.sweetapi.core.ApiActuatorBaseInfo;
import com.egrand.sweetapi.core.TenantService;
import com.egrand.sweetapi.web.exception.OpenException;
import com.egrand.sweetapi.web.model.entity.ApiActuator;
import com.egrand.sweetapi.web.service.ApiActuatorService;
import com.egrand.sweetplugin.service.impl.PluginServiceFactory;
import org.pf4j.PluginStateEvent;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ApiActuatorAdapteServiceFactory extends PluginServiceFactory<ApiActuatorAdapteService> {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private ApiActuatorService apiActuatorService;

    /**
     * 插件包含的接口实例，由插件启动和停止来维护
     */
    private Map<String, List<ApiActuatorAdapteService>> pluginBeanList = new HashMap<>();

    public Boolean initialize(String type, List<ApiActuatorBaseInfo> apiActuatorBaseInfoList) {
        ApiActuatorAdapteService apiActuatorAdapteService = this.getService(type);
        if (null == apiActuatorAdapteService)
            return false;
        return apiActuatorAdapteService.initialize(apiActuatorBaseInfoList);
    }

    public Boolean destroy(String type) {
        ApiActuatorAdapteService apiActuatorAdapteService = this.getService(type);
        if (null == apiActuatorAdapteService)
            return false;
        return apiActuatorAdapteService.destroy();
    }

    public Boolean save(ApiActuatorBaseInfo apiActuatorBaseInfo) {
        String type = apiActuatorBaseInfo.getType();
        if (StrUtil.isEmpty(type))
            return false;
        ApiActuatorAdapteService apiActuatorAdapteService = this.getService(type);
        if (null == apiActuatorAdapteService)
            return false;
        return apiActuatorAdapteService.save(apiActuatorBaseInfo);
    }

    public Boolean update(ApiActuatorBaseInfo apiActuatorBaseInfo, ApiActuatorBaseInfo oldApiActuatorBaseInfo) {
        String type = apiActuatorBaseInfo.getType();
        if (StrUtil.isEmpty(type))
            return false;
        ApiActuatorAdapteService apiActuatorAdapteService = this.getService(type);
        if (null == apiActuatorAdapteService)
            return false;
        return apiActuatorAdapteService.update(apiActuatorBaseInfo, oldApiActuatorBaseInfo);
    }

    public Boolean delete(String type, ApiActuatorBaseInfo apiActuatorBaseInfo) {
        ApiActuatorAdapteService apiActuatorAdapteService = this.getService(type);
        if (null == apiActuatorAdapteService)
            return false;
        return apiActuatorAdapteService.delete(apiActuatorBaseInfo);
    }

    /**
     * 编码
     * @param apiActuator 执行器实体
     * @param apiActuatorBaseInfo 执行器信息
     */
    public void encode(ApiActuator apiActuator, ApiActuatorBaseInfo apiActuatorBaseInfo) {
        if (null == apiActuator)
            return;
        // 编码公共参数
        apiActuator.setId(apiActuatorBaseInfo.getId());
        apiActuator.setName(apiActuatorBaseInfo.getName());
        apiActuator.setType(apiActuatorBaseInfo.getType());
        apiActuator.setCode(apiActuatorBaseInfo.getKey());
        apiActuator.setUserName(apiActuatorBaseInfo.getUserName());
        apiActuator.setApiId(apiActuatorBaseInfo.getApiId());
        apiActuator.setTimeout(apiActuatorBaseInfo.getTimeout());
        ApiActuatorAdapteService apiActuatorAdapteService = this.getService(apiActuatorBaseInfo.getType());
        if (null == apiActuatorAdapteService)
            return;
        apiActuator.setConfig(apiActuatorAdapteService.encode(apiActuatorBaseInfo));
    }

    /**
     * 解码
     * @param apiActuator 执行器实体
     * @return
     */
    public ApiActuatorBaseInfo decode(ApiActuator apiActuator) {
        if (null == apiActuator)
            return null;
        // 解码公共参数
        ApiActuatorBaseInfo apiActuatorBaseInfo = new ApiActuatorBaseInfo();
        apiActuatorBaseInfo.setId(apiActuator.getId());
        apiActuatorBaseInfo.setName(apiActuator.getName());
        apiActuatorBaseInfo.setType(apiActuator.getType());
        apiActuatorBaseInfo.setKey(apiActuator.getCode());
        apiActuatorBaseInfo.setUserName(apiActuator.getUserName());
        apiActuatorBaseInfo.setApiId(apiActuator.getApiId());
        apiActuatorBaseInfo.setTimeout(apiActuator.getTimeout());
        ApiActuatorAdapteService apiActuatorAdapteService = this.getService(apiActuator.getType());
        if (null == apiActuatorAdapteService)
            return apiActuatorBaseInfo;
        return apiActuatorAdapteService.decode(apiActuator.getConfig(), apiActuatorBaseInfo);
    }

    public void start(PluginWrapper plugin) {
        List<ApiActuatorAdapteService> beanList = this.getBean(plugin);
        beanList.forEach(bean -> this.initialize(bean.getType(), this.apiActuatorService.listAll(bean.getType().toLowerCase())));
    }

    @Override
    public void onPluginStarted(PluginStateEvent pluginStateEvent, List<ApiActuatorAdapteService> beanList,
                                List<Class<ApiActuatorAdapteService>> extensionClasses) {
        if (null == beanList || beanList.size() == 0)
            return;
        this.pluginBeanList.put(pluginStateEvent.getPlugin().getPluginId(), beanList);
        beanList.forEach(bean -> this.initialize(bean.getType(), this.apiActuatorService.listAll(bean.getType().toLowerCase())));
    }

    @Override
    public void onPluginStopped(PluginStateEvent pluginStateEvent, List<ApiActuatorAdapteService> beanList,
                                List<Class<ApiActuatorAdapteService>> extensionClasses) {
        this.pluginBeanList.remove(pluginStateEvent.getPlugin().getPluginId());
    }

    /**
     * 执行器测试
     * @param type 类型
     * @param actuatorInfo 执行器信息
     * @return
     */
    public ApiActuatorBaseInfo test(String type, String actuatorInfo) {
        if (StrUtil.isEmpty(actuatorInfo))
            return null;
        ApiActuatorAdapteService apiActuatorAdapteService = this.getService(type);
        if (null == apiActuatorAdapteService)
            return null;
        return apiActuatorAdapteService.test(actuatorInfo);
    }

    @Override
    protected Class getClazz() {
        return ApiActuatorAdapteService.class;
    }

    private ApiActuatorAdapteService getService(String type) {
        ApiActuatorAdapteService apiActuatorAdapteService = this.filter(this.getPluginService(type.toUpperCase()), type.toUpperCase());
        if (null == apiActuatorAdapteService)
            apiActuatorAdapteService = this.getServiceFromPlugin(type);
        return apiActuatorAdapteService;
    }

    private ApiActuatorAdapteService getServiceFromPlugin(String type) {
        List<Object> beanList = new ArrayList<>();
        this.pluginBeanList.values().forEach(connectionAdapteServiceList -> beanList.addAll(connectionAdapteServiceList));
        if (null != beanList && beanList.size() != 0) {
            for (Object bean : beanList) {
                ApiActuatorAdapteService apiActuatorAdapteService = (ApiActuatorAdapteService) bean;
                if (apiActuatorAdapteService.getType().equals(type.toUpperCase()))
                    return apiActuatorAdapteService;
            }
        }
        return null;
    }

    private ApiActuatorAdapteService filter(List<ApiActuatorAdapteService> apiActuatorAdapteServiceList, String type) {
        if (null == apiActuatorAdapteServiceList || apiActuatorAdapteServiceList.size() == 0)
            return null;
        if (apiActuatorAdapteServiceList.size() == 1)
            return apiActuatorAdapteServiceList.get(0);
        List<ApiActuatorAdapteService> priTmp = new ArrayList<>();
        List<ApiActuatorAdapteService> tmp = new ArrayList<>();
        for (ApiActuatorAdapteService connectionAdapteService : apiActuatorAdapteServiceList) {
            if (connectionAdapteService.getPrimaryKey().equals(this.getPrimaryKey())) {
                priTmp.add(connectionAdapteService);
            } else {
                tmp.add(connectionAdapteService);
            }
        }
        if (priTmp.size() == 1)
            return priTmp.get(0);

        if (priTmp.size() > 1 || tmp.size() > 1) {
            throw new OpenException("存在多个[" + type + "]类型的ConnectionParseService，请检查！");
        }
        return null;
    }

    @Override
    public String getPrimaryKey() {
        return this.tenantService.getTenant();
    }
}
