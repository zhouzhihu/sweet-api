package com.egrand.sweetapi.web.service.impl.db;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.egrand.sweetapi.core.ConnectionAdapteService;
import com.egrand.sweetapi.core.ConnectionBaseInfo;
import com.egrand.sweetapi.core.utils.JdbcUtils;
import com.egrand.sweetapi.starter.db.service.DataSourceService;
import com.egrand.sweetapi.starter.db.spring.boot.autoconfigure.DataSourceProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DB连接转换
 */
@Component
@Slf4j
public class DbConnectionAdapteServiceImpl implements ConnectionAdapteService {

    @Autowired
    protected DataSourceService dataSourceService;

    @Override
    public Boolean initialize(List<ConnectionBaseInfo> connectionBaseInfoList) {
        if (null != connectionBaseInfoList && connectionBaseInfoList.size() != 0) {
            connectionBaseInfoList.forEach(connectionBaseDTO -> this.save(connectionBaseDTO));
        }
        return true;
    }

    @Override
    public Boolean destroy() {
        return null;
    }

    @Override
    public Boolean save(ConnectionBaseInfo connectionBaseInfo) {
        DbConnectionInfo dbConnectionDTO = (DbConnectionInfo) connectionBaseInfo;
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        dataSourceProperty.setPoolName(dbConnectionDTO.getKey());
        dataSourceProperty.setUrl(dbConnectionDTO.getUrl());
        dataSourceProperty.setUsername(dbConnectionDTO.getUserName());
        dataSourceProperty.setPassword(dbConnectionDTO.getPassword());
        this.dataSourceService.addDataSource(dataSourceProperty);
        return true;
    }

    @Override
    public Boolean update(ConnectionBaseInfo connectionBaseInfo, String oldKey) {
        this.delete(oldKey);
        return this.save(connectionBaseInfo);
    }

    @Override
    public Boolean delete(String key) {
        if (this.dataSourceService.containsKey(key))
            this.dataSourceService.deleteDataSource(key);
        return true;
    }

    @Override
    public String encode(ConnectionBaseInfo connectionBaseInfo) {
        // 组装Connection的config配置
        DbConnectionInfo dbConnectionDTO = (DbConnectionInfo) connectionBaseInfo;
        JSONObject jsonObject = new JSONObject();
        if (StrUtil.isNotEmpty(dbConnectionDTO.getUserName())) {
            jsonObject.putOnce("userName", dbConnectionDTO.getUserName());
        }
        if (StrUtil.isNotEmpty(dbConnectionDTO.getPassword())) {
            jsonObject.putOnce("password", dbConnectionDTO.getPassword());
        }
        if (StrUtil.isNotEmpty(dbConnectionDTO.getUrl())) {
            jsonObject.putOnce("url", dbConnectionDTO.getUrl());
        }
        if (StrUtil.isNotEmpty(dbConnectionDTO.getDriverClassName())) {
            jsonObject.putOnce("driverClassName", dbConnectionDTO.getDriverClassName());
        }
        if (StrUtil.isNotEmpty(dbConnectionDTO.getDataSourceType())) {
            jsonObject.putOnce("dataSourceType", dbConnectionDTO.getDataSourceType());
        }
        jsonObject.putOnce("maxRow", dbConnectionDTO.getMaxRows());
        return JSONUtil.toJsonStr(jsonObject);
    }

    @Override
    public ConnectionBaseInfo decode(String config, ConnectionBaseInfo connectionBaseInfo) {
        DbConnectionInfo dbConnectionDTO = new DbConnectionInfo(connectionBaseInfo);
        if (StrUtil.isNotEmpty(config)) {
            JSONObject jsonObject = JSONUtil.parseObj(config);
            dbConnectionDTO.setUserName(jsonObject.getStr("userName", ""));
            dbConnectionDTO.setPassword(jsonObject.getStr("password", ""));
            dbConnectionDTO.setUrl(jsonObject.getStr("url", ""));
            dbConnectionDTO.setDriverClassName(jsonObject.getStr("driverClassName", ""));
            dbConnectionDTO.setDataSourceType(jsonObject.getStr("dataSourceType", ""));
            dbConnectionDTO.setMaxRows(jsonObject.getInt("maxRow", -1));
        }
        return dbConnectionDTO;
    }

    @Override
    public ConnectionBaseInfo test(String connectionInfo) {
        try {
            DbConnectionInfo dbConnectionDTO = JSONUtil.toBean(JSONUtil.parseObj(connectionInfo), DbConnectionInfo.class);
            java.sql.Connection connection = JdbcUtils.getConnection(dbConnectionDTO.getDriverClassName(), dbConnectionDTO.getUrl(),
                    dbConnectionDTO.getUserName(), dbConnectionDTO.getPassword());
            JdbcUtils.close(connection);
            return dbConnectionDTO;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getType() {
        return "DB";
    }
}
