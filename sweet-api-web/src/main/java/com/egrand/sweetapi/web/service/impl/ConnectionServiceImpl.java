package com.egrand.sweetapi.web.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.egrand.sweetapi.core.ConnectionBaseInfo;
import com.egrand.sweetapi.core.TenantService;
import com.egrand.sweetapi.web.mapper.ConnectionMapper;
import com.egrand.sweetapi.web.model.entity.Connection;
import com.egrand.sweetapi.web.service.ConnectionService;
import com.egrand.sweetapi.web.service.factory.ConnectionAdapteServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.egrand.sweetapi.web.utils.Assert.isFalse;

/**
 * 连接 服务实现类
 */
@Service
public class ConnectionServiceImpl extends ServiceImpl<ConnectionMapper, Connection> implements ConnectionService {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private ConnectionAdapteServiceFactory connectionAdapteServiceFactory;

    @Override
    public ConnectionBaseInfo test(String connectionInfo) {
        isFalse(StrUtil.isEmpty(connectionInfo), "输入信息为空，请核对！");
        JSONObject jsonObject = JSONUtil.parseObj(connectionInfo);
        isFalse(!jsonObject.containsKey("id"), "ID不能为空，请核对！");
        isFalse(!jsonObject.containsKey("name"), "名称不能为空，请核对！");
        isFalse(!jsonObject.containsKey("key"), "编码不能为空，请核对！");
        isFalse(!jsonObject.containsKey("type"), "未指定数据源类型，请核对！");
        return connectionAdapteServiceFactory.test(jsonObject.getStr("type"), connectionInfo);
    }

    @Override
    public ConnectionBaseInfo save(String connectionInfo) {
        ConnectionBaseInfo connectionBaseInfo = this.test(connectionInfo);
        isFalse(null == connectionBaseInfo, "测试连接时失败，请核对！");
        Connection connection = new Connection();
        this.connectionAdapteServiceFactory.encode(connection, connectionBaseInfo);
        if (connection.isNew()) {
            isFalse(this.isHaveConnection(connection.getCode()),
                    "存在【" + connection.getCode() + "】连接，请核对！");
            this.connectionAdapteServiceFactory.save(connectionBaseInfo);
            //--- 添加租户标记
            String tenant = this.tenantService.getTenant();
            if (StrUtil.isNotEmpty(tenant)) {
                connection.setTenant(tenant);
            }
            // 保存连接
            this.save(connection);
        } else {
            Connection oldConnection = this.getById(connection.getId());
            isFalse(null == oldConnection, "未找到对应连接，无法更新，请核对！");
            String oldKey = oldConnection.getCode();
            this.connectionAdapteServiceFactory.update(connectionBaseInfo, oldKey);
            if(StrUtil.isEmpty(oldConnection.getTenant())) {
                String tenant = this.tenantService.getTenant();
                if (StrUtil.isNotEmpty(tenant)) {
                    connection.setTenant(tenant);
                }
            }
            // 更新连接
            this.updateById(connection);
        }
        return this.connectionAdapteServiceFactory.decode(connection);
    }

    @Override
    public List<ConnectionBaseInfo> list(String type) {
        QueryWrapper<Connection> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(type))
            queryWrapper.lambda().eq(Connection::getType, type);
        String tenant = this.tenantService.getTenant();
        if (StrUtil.isNotEmpty(tenant)) {
            queryWrapper.lambda().eq(Connection::getTenant, tenant);
        }
        List<Connection> connectionList = this.list(queryWrapper);
        List<ConnectionBaseInfo> connectionBaseInfoList = new ArrayList<>();
        connectionList.forEach(connection -> connectionBaseInfoList.add(this.connectionAdapteServiceFactory.decode(connection)));
        return connectionBaseInfoList;
    }

    @Override
    public List<ConnectionBaseInfo> listAll(String type) {
        QueryWrapper<Connection> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(type))
            queryWrapper.lambda().eq(Connection::getType, type);
        List<Connection> connectionList = this.list(queryWrapper);
        List<ConnectionBaseInfo> connectionBaseInfoList = new ArrayList<>();
        connectionList.forEach(connection -> connectionBaseInfoList.add(this.connectionAdapteServiceFactory.decode(connection)));
        return connectionBaseInfoList;
    }

    @Override
    public Boolean initialize() {
        this.connectionAdapteServiceFactory.initialize("db",this.listAll("db"));
        return true;
    }

    @Override
    public Boolean sync() {
        List<ConnectionBaseInfo> connectionBaseInfoList = this.list("");
        if (null != connectionBaseInfoList && connectionBaseInfoList.size() != 0) {
            Map<String, List<ConnectionBaseInfo>> connectionMap = connectionBaseInfoList.stream().collect(Collectors.groupingBy(ConnectionBaseInfo::getType));
            connectionMap.forEach((type, connectionList) -> this.connectionAdapteServiceFactory.sync(type, connectionList));
        }
        return true;
    }

    @Override
    public boolean delete(Serializable id) {
        Connection connection = this.getById(id);
        isFalse(null == connection, "未找到对应连接，无法删除，请核对！");
        this.connectionAdapteServiceFactory.delete(connection.getType(), connection.getCode());
        return super.removeById(id);
    }

    /**
     * 判断是否存在相同的key
     * @param key key
     * @return
     */
    private Boolean isHaveConnection(String key) {
        QueryWrapper<Connection> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Connection::getCode, key);
        return this.count(queryWrapper) > 0;
    }
}
