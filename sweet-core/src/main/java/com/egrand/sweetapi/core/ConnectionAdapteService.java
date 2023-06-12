package com.egrand.sweetapi.core;

import com.egrand.sweetplugin.service.PluginService;

import java.util.List;

/**
 * 连接适应服务,用于扩展不同数据源配置接口
 */
public interface ConnectionAdapteService extends PluginService {

    /**
     * 启动应用程序时运行，用于初始化连接到连接池
     * @return
     */
    Boolean initialize(List<ConnectionBaseInfo> connectionBaseInfoList);

    /**
     * 删除连接时运行，用于执行删除后的清理动作
     * @return
     */
    Boolean destroy();

    /**
     * 新增连接时运行
     * @param connectionBaseInfo
     * @return
     */
    Boolean save(ConnectionBaseInfo connectionBaseInfo);

    /**
     * 更新连接时运行
     * @param connectionBaseInfo
     * @param oldKey
     * @return
     */
    Boolean update(ConnectionBaseInfo connectionBaseInfo, String oldKey);

    /**
     * 删除连接时运行
     * @param key
     * @return
     */
    Boolean delete(String key);

    /**
     * 编码连接信息
     * @param connectionBaseInfo 连接基本信息
     * @return 编码后的JSONObject字符串
     */
    String encode(ConnectionBaseInfo connectionBaseInfo);

    /**
     * 解码连接信息
     * @param config 需要解码的配置字符串
     * @param connectionBaseInfo 连接基本信息
     */
    ConnectionBaseInfo decode(String config, ConnectionBaseInfo connectionBaseInfo);

    /**
     * 测试连接
     * @param connectionInfo
     * @return
     */
    ConnectionBaseInfo test(String connectionInfo);

    /**
     * 同步连接信息
     * @return
     */
    default void sync(List<ConnectionBaseInfo> connectionBaseInfoList) {
        if (null != connectionBaseInfoList && connectionBaseInfoList.size() != 0) {
            connectionBaseInfoList.forEach(connectionBaseDTO -> {
                this.delete(connectionBaseDTO.getKey());
                this.save(connectionBaseDTO);
            });
        }
    }
}
