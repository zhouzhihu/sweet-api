package com.egrand.sweetapi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.egrand.sweetapi.core.ConnectionBaseInfo;
import com.egrand.sweetapi.web.model.entity.Connection;

import java.io.Serializable;
import java.util.List;

/**
 * 连接服务类
 */
public interface ConnectionService extends IService<Connection> {

    /**
     * 测试连接
     * @param connectionInfo 连接信息
     * @return
     */
    ConnectionBaseInfo test(String connectionInfo);

    /**
     * 保存连接
     * @param connectionInfo 连接信息
     * @return
     */
    ConnectionBaseInfo save(String connectionInfo);

    /**
     * 删除
     * @param id id
     * @return
     */
    boolean delete(Serializable id);

    /**
     * 获取指定类型连接
     * @param type 类型
     * @return
     */
    List<ConnectionBaseInfo> list(String type);

    /**
     * 获取指定类型连接
     * @param type 类型
     * @return
     */
    List<ConnectionBaseInfo> listAll(String type);

    /**
     * 程序启动时，初始化连接
     * @return
     */
    Boolean initialize();

    /**
     * 同步所有数据源连接
     */
    Boolean sync();
}
