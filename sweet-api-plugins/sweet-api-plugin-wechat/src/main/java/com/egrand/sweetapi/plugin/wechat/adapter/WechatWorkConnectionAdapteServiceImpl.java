package com.egrand.sweetapi.plugin.wechat.adapter;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.egrand.sweetapi.core.ConnectionAdapteService;
import com.egrand.sweetapi.core.ConnectionBaseInfo;
import com.egrand.sweetapi.starter.wechat.DynamicRoutingWxCp;
import com.egrand.sweetapi.starter.wechat.config.WxCpProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * WechatWork连接转换
 */
@Component
@Slf4j
public class WechatWorkConnectionAdapteServiceImpl implements ConnectionAdapteService {

    @Autowired
    private DynamicRoutingWxCp dynamicRoutingWxCp;

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
        WechatWorkConnectionInfo wechatWorkConnectionDTO = (WechatWorkConnectionInfo) connectionBaseInfo;
        WxCpProperties.AppConfig wxCpProperties = new WxCpProperties.AppConfig();
        wxCpProperties.setCorpId(wechatWorkConnectionDTO.getCorpId());
        wxCpProperties.setAgentId(wechatWorkConnectionDTO.getAgentId());
        wxCpProperties.setSecret(wechatWorkConnectionDTO.getSecret());
        wxCpProperties.setToken(wechatWorkConnectionDTO.getToken());
        wxCpProperties.setAesKey(wechatWorkConnectionDTO.getAesKey());
        this.dynamicRoutingWxCp.addWxCp(connectionBaseInfo.getKey(), wxCpProperties);
        return true;
    }

    @Override
    public Boolean update(ConnectionBaseInfo connectionBaseInfo, String oldKey) {
        this.delete(oldKey);
        return this.save(connectionBaseInfo);
    }

    @Override
    public Boolean delete(String key) {
        if (this.dynamicRoutingWxCp.listAll().containsKey(key))
            this.dynamicRoutingWxCp.removeWxCp(key);
        return true;
    }

    @Override
    public String encode(ConnectionBaseInfo connectionBaseInfo) {
        WechatWorkConnectionInfo wechatWorkConnectionDTO = (WechatWorkConnectionInfo) connectionBaseInfo;
        JSONObject jsonObject = new JSONObject();
        if (StrUtil.isNotEmpty(wechatWorkConnectionDTO.getCorpId())) {
            jsonObject.putOnce("corpId", wechatWorkConnectionDTO.getCorpId());
        }
        jsonObject.putOnce("agentId", wechatWorkConnectionDTO.getAgentId());
        if (StrUtil.isNotEmpty(wechatWorkConnectionDTO.getSecret())) {
            jsonObject.putOnce("secret", wechatWorkConnectionDTO.getSecret());
        }
        if (StrUtil.isNotEmpty(wechatWorkConnectionDTO.getToken())) {
            jsonObject.putOnce("token", wechatWorkConnectionDTO.getToken());
        }
        if (StrUtil.isNotEmpty(wechatWorkConnectionDTO.getAesKey())) {
            jsonObject.putOnce("aesKey", wechatWorkConnectionDTO.getAesKey());
        }
        return JSONUtil.toJsonStr(jsonObject);
    }

    @Override
    public ConnectionBaseInfo decode(String config, ConnectionBaseInfo connectionBaseInfo) {
        WechatWorkConnectionInfo wechatWorkConnectionDTO = new WechatWorkConnectionInfo(connectionBaseInfo);
        if (StrUtil.isNotEmpty(config)) {
            JSONObject jsonObject = JSONUtil.parseObj(config);
            wechatWorkConnectionDTO.setCorpId(jsonObject.getStr("corpId", ""));
            wechatWorkConnectionDTO.setAgentId(jsonObject.getInt("agentId", -1));
            wechatWorkConnectionDTO.setSecret(jsonObject.getStr("secret", ""));
            wechatWorkConnectionDTO.setToken(jsonObject.getStr("token", ""));
            wechatWorkConnectionDTO.setAesKey(jsonObject.getStr("aesKey", ""));
        }
        return wechatWorkConnectionDTO;
    }

    @Override
    public ConnectionBaseInfo test(String connectionInfo) {
        try {
            // TODO 待完善测试功能
            return JSONUtil.toBean(JSONUtil.parseObj(connectionInfo), WechatWorkConnectionInfo.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getType() {
        return "WECHAT";
    }
}
