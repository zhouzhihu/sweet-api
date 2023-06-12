package com.egrand.sweetapi.core;

import com.egrand.sweetplugin.service.PluginService;

import java.util.List;

/**
 * API执行器适应服务，用于扩展不同的执行器接口
 */
public interface ApiActuatorAdapteService extends PluginService {
    /**
     * 启动应用程序时运行，用于初始化执行器
     * @param apiActuatorBaseInfoList API执行器DTO
     * @return
     */
    Boolean initialize(List<ApiActuatorBaseInfo> apiActuatorBaseInfoList);

    /**
     * 删除执行器时运行，用于执行删除后的清理动作
     * @return
     */
    Boolean destroy();

    /**
     * 新增执行器时运行
     * @param apiActuatorBaseInfo API执行器DTO
     * @return
     */
    Boolean save(ApiActuatorBaseInfo apiActuatorBaseInfo);

    /**
     * 更新执行器时运行
     * @param apiActuatorBaseInfo API执行器DTO
     * @param oldApiActuatorBaseInfo 旧的API执行器DTO
     * @return
     */
    Boolean update(ApiActuatorBaseInfo apiActuatorBaseInfo, ApiActuatorBaseInfo oldApiActuatorBaseInfo);

    /**
     * 删除执行器时运行
     * @param apiActuatorBaseInfo API执行器DTO
     * @return
     */
    Boolean delete(ApiActuatorBaseInfo apiActuatorBaseInfo);

    /**
     * 编码执行器信息
     * @param apiActuatorBaseInfo 执行器基本信息
     * @return 编码后的配置信息
     */
    String encode(ApiActuatorBaseInfo apiActuatorBaseInfo);

    /**
     * 解码执行器信息
     * @param config 配置信息
     * @param apiActuatorBaseInfo 执行器DTO
     * @return
     */
    ApiActuatorBaseInfo decode(String config, ApiActuatorBaseInfo apiActuatorBaseInfo);

    /**
     * 测试执行器
     * @param actuatorInfo
     * @return
     */
    ApiActuatorBaseInfo test(String actuatorInfo);

}
