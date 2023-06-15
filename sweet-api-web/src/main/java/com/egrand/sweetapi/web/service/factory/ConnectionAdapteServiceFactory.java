package com.egrand.sweetapi.web.service.factory;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.egrand.sweetapi.core.ConnectionAdapteService;
import com.egrand.sweetapi.core.ConnectionBaseInfo;
import com.egrand.sweetapi.core.ConnectionExtendConfigInfo;
import com.egrand.sweetapi.core.TenantService;
import com.egrand.sweetapi.web.exception.OpenException;
import com.egrand.sweetapi.web.model.entity.Connection;
import com.egrand.sweetapi.web.service.ConnectionService;
import com.egrand.sweetplugin.service.impl.PluginServiceFactory;
import org.pf4j.PluginStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动态扫描连接转换接口实现
 */
@Component
public class ConnectionAdapteServiceFactory extends PluginServiceFactory<ConnectionAdapteService> {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private ConnectionService connectionService;

    /**
     * 插件包含的接口实例，由插件启动和停止来维护
     */
    private Map<String, List<ConnectionAdapteService>> pluginBeanList = new HashMap<>();

    public Boolean initialize(String type, List<ConnectionBaseInfo> connectionBaseInfoList) {
        ConnectionAdapteService connectionAdapteService = this.getService(type);
        if (null == connectionAdapteService)
            return false;
        return connectionAdapteService.initialize(connectionBaseInfoList);
    }

    public Boolean destroy(String type) {
        ConnectionAdapteService connectionAdapteService = this.getService(type);
        if (null == connectionAdapteService)
            return false;
        return connectionAdapteService.destroy();
    }

    public Boolean save(ConnectionBaseInfo connectionBaseInfo) {
        String type = connectionBaseInfo.getType();
        if (StrUtil.isEmpty(type))
            return false;
        ConnectionAdapteService connectionAdapteService = this.getService(type);
        if (null == connectionAdapteService)
            return false;
        return connectionAdapteService.save(connectionBaseInfo);
    }

    public Boolean update(ConnectionBaseInfo connectionBaseInfo, String oldKey) {
        String type = connectionBaseInfo.getType();
        if (StrUtil.isEmpty(type))
            return false;
        ConnectionAdapteService connectionAdapteService = this.getService(type);
        if (null == connectionAdapteService)
            return false;
        return connectionAdapteService.update(connectionBaseInfo, oldKey);
    }

    public Boolean delete(String type, String key) {
        ConnectionAdapteService connectionAdapteService = this.getService(type);
        if (null == connectionAdapteService)
            return false;
        return connectionAdapteService.delete(key);
    }

    /**
     * 编码
     * @param connection
     * @param connectionBaseInfo 连接信息
     */
    public void encode(Connection connection, ConnectionBaseInfo connectionBaseInfo) {
        if (null == connection)
            return;
        // 编码公共参数
        connection.setId(connectionBaseInfo.getId());
        connection.setName(connectionBaseInfo.getName());
        connection.setCode(connectionBaseInfo.getKey());
        connection.setType(connectionBaseInfo.getType());
        connection.setTimeout(connectionBaseInfo.getTimeout());
        List<ConnectionExtendConfigInfo> extendConfigList = connectionBaseInfo.getExtendConfigList();
        if (null != extendConfigList && extendConfigList.size() != 0) {
            JSONArray jsonArray = new JSONArray();
            extendConfigList.forEach(connectionExtendConfigInfo -> jsonArray.put(JSONUtil.parseObj(connectionExtendConfigInfo)));
            connection.setExtendConfig(JSONUtil.toJsonStr(jsonArray));
        }
        ConnectionAdapteService connectionAdapteService = this.getService(connectionBaseInfo.getType());
        if (null == connectionAdapteService)
            return;
        connection.setConfig(connectionAdapteService.encode(connectionBaseInfo));
    }

    /**
     * 解码
     * @param connection
     * @return
     */
    public ConnectionBaseInfo decode(Connection connection) {
        if (null == connection)
            return null;
        // 解码公共参数
        ConnectionBaseInfo connectionBaseInfo = new ConnectionBaseInfo();
        connectionBaseInfo.setId(connection.getId());
        connectionBaseInfo.setName(connection.getName());
        connectionBaseInfo.setKey(connection.getCode());
        connectionBaseInfo.setType(connection.getType());
        connectionBaseInfo.setTimeout(connection.getTimeout());
        String extendConfig = connection.getExtendConfig();
        if (StrUtil.isNotEmpty(extendConfig)) {
            List<ConnectionExtendConfigInfo> extendConfigList = new ArrayList<>();
            JSONArray jsonArray = JSONUtil.parseArray(extendConfig);
            jsonArray.forEach(json -> extendConfigList.add(JSONUtil.toBean((JSONObject) json, ConnectionExtendConfigInfo.class)));
            if (extendConfigList.size() != 0) {
                connectionBaseInfo.setExtendConfigList(extendConfigList);
            }
        }
        ConnectionAdapteService connectionAdapteService = this.getService(connection.getType());
        if (null == connectionAdapteService)
            return connectionBaseInfo;
        return connectionAdapteService.decode(connection.getConfig(), connectionBaseInfo);
    }

    @Override
    public void onPluginStarted(PluginStateEvent pluginStateEvent, List<ConnectionAdapteService> beanList,
                                List<Class<ConnectionAdapteService>> extensionClasses) {
        if (null == beanList || beanList.size() == 0)
            return;
        this.pluginBeanList.put(pluginStateEvent.getPlugin().getPluginId(), beanList);
        beanList.forEach(bean -> this.initialize(bean.getType(), this.connectionService.listAll(bean.getType().toLowerCase())));
    }

    @Override
    public void onPluginStopped(PluginStateEvent pluginStateEvent, List<ConnectionAdapteService> beanList,
                                List<Class<ConnectionAdapteService>> extensionClasses) {
        this.pluginBeanList.remove(pluginStateEvent.getPlugin().getPluginId());
    }

    /**
     * 连接测试
     * @param type 连接类型
     * @param connectionInfo 连接信息
     * @return
     */
    public ConnectionBaseInfo test(String type, String connectionInfo) {
        if (StrUtil.isEmpty(connectionInfo))
            return null;
        ConnectionAdapteService connectionAdapteService = this.getService(type);
        if (null == connectionAdapteService)
            return null;
        return connectionAdapteService.test(connectionInfo);
    }

    /**
     * 连接同步，用于同步各数据源缓存
     * @param type 数据源类型
     * @param connectionBaseInfoList 连接信息集合
     */
    public void sync(String type, List<ConnectionBaseInfo> connectionBaseInfoList) {
        ConnectionAdapteService connectionAdapteService = this.getService(type);
        if (null == connectionAdapteService)
            return;
        connectionAdapteService.sync(connectionBaseInfoList);
    }

    @Override
    protected Class getClazz() {
        return ConnectionAdapteService.class;
    }

    private ConnectionAdapteService getService(String type) {
        ConnectionAdapteService connectionAdapteService = this.filter(this.getPluginService(type.toUpperCase()), type.toUpperCase());
        if (null == connectionAdapteService)
            connectionAdapteService = this.getServiceFromPlugin(type);
        return connectionAdapteService;
    }

    private ConnectionAdapteService getServiceFromPlugin(String type) {
        List<Object> beanList = new ArrayList<>();
        this.pluginBeanList.values().forEach(connectionAdapteServiceList -> beanList.addAll(connectionAdapteServiceList));
        if (null != beanList && beanList.size() != 0) {
            for (Object bean : beanList) {
                ConnectionAdapteService connectionAdapteService = (ConnectionAdapteService) bean;
                if (connectionAdapteService.getType().equals(type.toUpperCase()))
                    return connectionAdapteService;
            }
        }
        return null;
    }

    private ConnectionAdapteService filter(List<ConnectionAdapteService> connectionAdapteServiceList, String type) {
        if (null == connectionAdapteServiceList || connectionAdapteServiceList.size() == 0)
            return null;
        if (connectionAdapteServiceList.size() == 1)
            return connectionAdapteServiceList.get(0);
        List<ConnectionAdapteService> priTmp = new ArrayList<>();
        List<ConnectionAdapteService> tmp = new ArrayList<>();
        for (ConnectionAdapteService connectionAdapteService : connectionAdapteServiceList) {
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
