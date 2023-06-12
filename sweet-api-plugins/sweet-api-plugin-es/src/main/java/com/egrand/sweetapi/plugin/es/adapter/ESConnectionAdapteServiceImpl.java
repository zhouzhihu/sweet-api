package com.egrand.sweetapi.plugin.es.adapter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.egrand.sweetapi.core.ConnectionAdapteService;
import com.egrand.sweetapi.core.ConnectionBaseInfo;
import com.egrand.sweetapi.starter.es.boot.DynamicESStarter;
import com.egrand.sweetapi.starter.es.configuration.ESProperties;
import lombok.extern.slf4j.Slf4j;
import org.frameworkset.elasticsearch.boot.BaseESProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ES连接转换
 */
@Component
@Slf4j
public class ESConnectionAdapteServiceImpl implements ConnectionAdapteService {

    @Autowired
    private DynamicESStarter dynamicESStarter;

    @Override
    public Boolean initialize(List<ConnectionBaseInfo> connectionBaseInfoList) {
        if (null != connectionBaseInfoList && connectionBaseInfoList.size() != 0) {
            connectionBaseInfoList.forEach(connectionBaseDTO -> this.save(connectionBaseDTO));
        }
        return true;
    }

    @Override
    public Boolean destroy() {
        return true;
    }

    @Override
    public Boolean save(ConnectionBaseInfo connectionBaseInfo) {
        ESConnectionInfo esConnectionDTO = (ESConnectionInfo) connectionBaseInfo;
        ESProperties esProperties = new ESProperties();
        esProperties.setName(esConnectionDTO.getKey());
        esProperties.setElasticUser(esConnectionDTO.getElasticUser());
        esProperties.setElasticPassword(esConnectionDTO.getElasticPassword());

        // 设置Elasticsearch
        BaseESProperties.Elasticsearch elasticsearch = new BaseESProperties.Elasticsearch();
        // ---- 设置Rest
        BaseESProperties.Rest rest = new BaseESProperties.Rest();
        rest.setHostNames(esConnectionDTO.getHostNames());
        elasticsearch.setRest(rest);

        esProperties.setElasticsearch(elasticsearch);

        this.dynamicESStarter.addES(connectionBaseInfo.getKey(), esProperties);
        return true;
    }

    @Override
    public Boolean update(ConnectionBaseInfo connectionBaseInfo, String oldKey) {
        this.delete(oldKey);
        return this.save(connectionBaseInfo);
    }

    @Override
    public Boolean delete(String key) {
        return this.dynamicESStarter.delES(key);
    }

    @Override
    public String encode(ConnectionBaseInfo connectionBaseInfo) {
        // 组装Connection的config配置
        ESConnectionInfo esConnectionDTO = (ESConnectionInfo) connectionBaseInfo;
        JSONObject jsonObject = new JSONObject();
        if (StrUtil.isNotEmpty(esConnectionDTO.getElasticUser())) {
            jsonObject.putOnce("elasticUser", esConnectionDTO.getElasticUser());
        }
        if (StrUtil.isNotEmpty(esConnectionDTO.getElasticPassword())) {
            jsonObject.putOnce("elasticPassword", esConnectionDTO.getElasticPassword());
        }
        if (StrUtil.isNotEmpty(esConnectionDTO.getHostNames())) {
            jsonObject.putOnce("hostNames", esConnectionDTO.getHostNames());
        }
        return JSONUtil.toJsonStr(jsonObject);
    }

    @Override
    public ConnectionBaseInfo decode(String config, ConnectionBaseInfo connectionBaseInfo) {
        ESConnectionInfo esConnectionDTO = new ESConnectionInfo(connectionBaseInfo);
        if (StrUtil.isNotEmpty(config)) {
            JSONObject jsonObject = JSONUtil.parseObj(config);
            esConnectionDTO.setElasticUser(jsonObject.getStr("elasticUser", ""));
            esConnectionDTO.setElasticPassword(jsonObject.getStr("elasticPassword", ""));
            esConnectionDTO.setHostNames(jsonObject.getStr("hostNames", ""));
        }
        return esConnectionDTO;
    }

    @Override
    public ConnectionBaseInfo test(String connectionInfo) {
        try {
            ESConnectionInfo esConnectionDTO = JSONUtil.toBean(JSONUtil.parseObj(connectionInfo), ESConnectionInfo.class);
            // TODO 待完善测试
            return esConnectionDTO;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getType() {
        return "ES";
    }
}
