package com.egrand.sweetapi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.egrand.sweetapi.core.ApiActuatorBaseInfo;
import com.egrand.sweetapi.web.model.entity.ApiActuator;

import java.io.Serializable;
import java.util.List;

/**
 * 执行器服务类
 */
public interface ApiActuatorService extends IService<ApiActuator> {

    /**
     * 测试连接
     * @param actuatorInfo 执行器信息
     * @return
     */
    ApiActuatorBaseInfo test(String actuatorInfo);

    /**
     * 保存执行器
     * @param actuatorInfos 执行器信息
     * @return
     */
    Boolean save(List<String> actuatorInfos);

    /**
     * 保存执行器
     * @param actuatorInfo 执行器信息
     * @return
     */
    ApiActuatorBaseInfo save(String actuatorInfo);

    /**
     * 删除执行器
     * @param id 执行器ID
     * @return
     */
    boolean delete(Serializable id);

    /**
     * 获取指定类型执行器
     * @param apiId API ID
     * @param type 类型
     * @return
     */
    List<ApiActuatorBaseInfo> list(Long apiId, String type);

    /**
     * 获取指定类型执行器
     * @param type 类型
     * @return
     */
    List<ApiActuatorBaseInfo> listAll(String type);
}
