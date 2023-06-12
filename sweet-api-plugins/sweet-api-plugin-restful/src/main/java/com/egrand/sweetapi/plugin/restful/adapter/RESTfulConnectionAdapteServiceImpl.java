package com.egrand.sweetapi.plugin.restful.adapter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.egrand.sweetapi.core.ConnectionAdapteService;
import com.egrand.sweetapi.core.ConnectionBaseInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * RESTful连接转换
 */
@Component
@Slf4j
public class RESTfulConnectionAdapteServiceImpl implements ConnectionAdapteService, RESTfulConnectionStore {

    /**
     * 所有数据库
     */
    private final Map<String, RESTfulConnectionInfo> restfulMap = new LinkedHashMap<>();

    @Override
    public Map<String, RESTfulConnectionInfo> getRestfulMap() {
        return this.restfulMap;
    }

    @Override
    public Boolean containsKey(String key) {
        return this.restfulMap.containsKey(key);
    }

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
        this.restfulMap.put(connectionBaseInfo.getKey(), (RESTfulConnectionInfo) connectionBaseInfo);
        return true;
    }

    @Override
    public Boolean update(ConnectionBaseInfo connectionBaseInfo, String oldKey) {
        this.delete(oldKey);
        return this.save(connectionBaseInfo);
    }

    @Override
    public Boolean delete(String key) {
        if(this.restfulMap.containsKey(key))
            this.restfulMap.remove(key);
        return true;
    }

    @Override
    public String encode(ConnectionBaseInfo connectionBaseInfo) {
        RESTfulConnectionInfo resTfulConnectionDTO = new RESTfulConnectionInfo(connectionBaseInfo);
        JSONObject jsonObject = new JSONObject();
        if (StrUtil.isNotEmpty(resTfulConnectionDTO.getBaseUrl())) {
            jsonObject.putOnce("baseUrl", resTfulConnectionDTO.getBaseUrl());
        }
        if (StrUtil.isNotEmpty(resTfulConnectionDTO.getRequestFormat())) {
            jsonObject.putOnce("requestFormat", resTfulConnectionDTO.getRequestFormat());
        }
        if (StrUtil.isNotEmpty(resTfulConnectionDTO.getRequestEncode())) {
            jsonObject.putOnce("requestEncode", resTfulConnectionDTO.getRequestEncode());
        }
        if (StrUtil.isNotEmpty(resTfulConnectionDTO.getResponseFormat())) {
            jsonObject.putOnce("responseFormat", resTfulConnectionDTO.getResponseFormat());
        }
        if (StrUtil.isNotEmpty(resTfulConnectionDTO.getResponseEncode())) {
            jsonObject.putOnce("responseEncode", resTfulConnectionDTO.getResponseEncode());
        }
        if (null != resTfulConnectionDTO.getUrlParams() && resTfulConnectionDTO.getUrlParams().size() != 0) {
            jsonObject.putOnce("urlParams", this.parse(resTfulConnectionDTO.getUrlParams()));
        }
        if (null != resTfulConnectionDTO.getHttpHeaders() && resTfulConnectionDTO.getHttpHeaders().size() != 0) {
            jsonObject.putOnce("httpHeaders", this.parse(resTfulConnectionDTO.getHttpHeaders()));
        }
        if (null != resTfulConnectionDTO.getFormData() && resTfulConnectionDTO.getFormData().size() != 0) {
            jsonObject.putOnce("formData", this.parse(resTfulConnectionDTO.getFormData()));
        }
        return JSONUtil.toJsonStr(jsonObject);
    }

    @Override
    public ConnectionBaseInfo decode(String config, ConnectionBaseInfo connectionBaseInfo) {
        RESTfulConnectionInfo resTfulConnectionDTO = new RESTfulConnectionInfo(connectionBaseInfo);
        if (StrUtil.isNotEmpty(config)) {
            JSONObject jsonObject = JSONUtil.parseObj(config);
            resTfulConnectionDTO.setBaseUrl(jsonObject.getStr("baseUrl", ""));
            resTfulConnectionDTO.setRequestFormat(jsonObject.getStr("requestFormat", ""));
            resTfulConnectionDTO.setRequestEncode(jsonObject.getStr("requestEncode", ""));
            resTfulConnectionDTO.setResponseFormat(jsonObject.getStr("responseFormat", ""));
            resTfulConnectionDTO.setResponseEncode(jsonObject.getStr("responseEncode", ""));
            if (jsonObject.containsKey("urlParams")) {
                resTfulConnectionDTO.setUrlParams(this.parseToMap(jsonObject.getJSONArray("urlParams")));
            }
            if (jsonObject.containsKey("httpHeaders")) {
                resTfulConnectionDTO.setHttpHeaders(this.parseToMap(jsonObject.getJSONArray("httpHeaders")));
            }
            if (jsonObject.containsKey("formData")) {
                resTfulConnectionDTO.setFormData(this.parseToMap(jsonObject.getJSONArray("formData")));
            }
        }
        return resTfulConnectionDTO;
    }

    @Override
    public ConnectionBaseInfo test(String connectionInfo) {
        try {
            JSONObject jsonObject = JSONUtil.parseObj(connectionInfo);
            RESTfulConnectionInfo resTfulConnectionDTO = JSONUtil.toBean(JSONUtil.parseObj(connectionInfo), RESTfulConnectionInfo.class);
            if (jsonObject.containsKey("urlParams")) {
                resTfulConnectionDTO.setUrlParams(this.parseToMap(jsonObject.getJSONArray("urlParams")));
            }
            if (jsonObject.containsKey("httpHeaders")) {
                resTfulConnectionDTO.setHttpHeaders(this.parseToMap(jsonObject.getJSONArray("httpHeaders")));
            }
            if (jsonObject.containsKey("formData")) {
                resTfulConnectionDTO.setFormData(this.parseToMap(jsonObject.getJSONArray("formData")));
            }
            // TODO 待完善测试
            return resTfulConnectionDTO;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getType() {
        return "RESTFUL";
    }

    private JSONArray parse(Map<String, String> valueMap) {
        JSONArray jsonArray = new JSONArray();
        valueMap.forEach((key, value) -> {
            JSONObject urlParamJson = new JSONObject();
            urlParamJson.putOnce("key", key);
            urlParamJson.putOnce("value", value);
            jsonArray.put(urlParamJson);
        });
        return jsonArray;
    }

    private Map<String, String> parseToMap(JSONArray jsonArray) {
        Map<String, String> valueMap = new HashMap<>();
        if (null != jsonArray && jsonArray.size() != 0) {
            jsonArray.forEach(json -> {
                JSONObject tmp = (JSONObject) json;
                if (tmp.containsKey("key") && tmp.containsKey("value")) {
                    valueMap.put(tmp.getStr("key"), tmp.getStr("value"));
                }
            });
        }
        return valueMap;
    }
}
