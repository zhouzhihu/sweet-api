package com.egrand.sweetapi.plugin.mq.adapter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.egrand.sweetapi.core.ApiActuatorAdapteService;
import com.egrand.sweetapi.core.ApiActuatorBaseInfo;
import com.egrand.sweetapi.core.ApiActuatorService;
import com.egrand.sweetapi.core.TenantService;
import com.egrand.sweetapi.starter.mq.utils.MQUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.ScriptException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class MQApiActuatorAdapteServiceImpl implements ApiActuatorAdapteService {

    /**
     * 消费列表(key:apiID,value:MQ服务器返回的消费者字符串)
     */
    private final Map<Long, String> apiCounsumerMap = new LinkedHashMap<>();

    @Autowired
    private ApiActuatorService apiActuatorService;

    @Autowired
    private TenantService tenantService;

    @Override
    public Boolean initialize(List<ApiActuatorBaseInfo> apiActuatorBaseInfoList) {
        if (null != apiActuatorBaseInfoList && apiActuatorBaseInfoList.size() != 0) {
            apiActuatorBaseInfoList.forEach(apiActuatorBaseDTO -> this.save(apiActuatorBaseDTO));
        }
        return true;
    }

    @Override
    public Boolean destroy() {
        return null;
    }

    @Override
    public Boolean save(ApiActuatorBaseInfo apiActuatorBaseInfo) {
        MQApiActuatorInfo mqApiActuatorDTO = (MQApiActuatorInfo) apiActuatorBaseInfo;
        // 添加新的监听
        String consumerTag;
        if (StrUtil.isNotEmpty(mqApiActuatorDTO.getExchangeName())) {
            consumerTag = MQUtils.receive(mqApiActuatorDTO.getMqKey(), mqApiActuatorDTO.getExchangeName(),
                    mqApiActuatorDTO.getQueueName(), mqApiActuatorDTO.getQos(), (header, message) -> {
                try {
                    initTenant(header);
                    apiActuatorService.execute(mqApiActuatorDTO.getApiId(), initContext(header, message));
                    return true;
                } catch (ScriptException e) {
                    return false;
                } finally {
                    this.tenantService.clear();
                }
            }, mqApiActuatorDTO.getArguments());
        } else {
            consumerTag = MQUtils.receive(mqApiActuatorDTO.getMqKey(), mqApiActuatorDTO.getQueueName(),
                    mqApiActuatorDTO.getQos(), (header, message) -> {
                try {
                    initTenant(header);
                    apiActuatorService.execute(mqApiActuatorDTO.getApiId(), initContext(header, message));
                    return true;
                } catch (ScriptException e) {
                    return false;
                } finally {
                    this.tenantService.clear();
                }
            }, mqApiActuatorDTO.getArguments());
        }
        if (StrUtil.isEmpty(consumerTag)) {
            throw new RuntimeException("注册【" + apiActuatorBaseInfo.getName() + "】监听失败!");
        }
        this.apiCounsumerMap.put(apiActuatorBaseInfo.getApiId(), consumerTag);
        return true;
    }

    private Map<String, Object> initContext(Map<String, Object> headers, String message) {
        Map<String, Object> context = new HashMap<>();
        context.put("headers", headers);
        context.put("message", message);
        return context;
    }

    private void initTenant(Map<String, Object> headers){
        String tenant = MQUtils.getHeader(headers, TenantService.TENANT_HEADER);
        if(StrUtil.isNotEmpty(tenant))
            this.tenantService.setTenant(tenant);
    }

    @Override
    public Boolean update(ApiActuatorBaseInfo apiActuatorBaseInfo, ApiActuatorBaseInfo oldApiActuatorBaseInfo) {
        this.delete(oldApiActuatorBaseInfo);
        return this.save(apiActuatorBaseInfo);
    }

    @Override
    public Boolean delete(ApiActuatorBaseInfo apiActuatorBaseInfo) {
        MQApiActuatorInfo mqApiActuatorDTO = (MQApiActuatorInfo) apiActuatorBaseInfo;
        if (apiCounsumerMap.containsKey(mqApiActuatorDTO.getApiId())) {
            return MQUtils.cancel(apiCounsumerMap.get(mqApiActuatorDTO.getApiId()));
        }
        return true;
    }

    @Override
    public String encode(ApiActuatorBaseInfo apiActuatorBaseInfo) {
        MQApiActuatorInfo mqApiActuatorDTO = (MQApiActuatorInfo) apiActuatorBaseInfo;
        JSONObject jsonObject = new JSONObject();
        if (StrUtil.isNotEmpty(mqApiActuatorDTO.getMqKey())) {
            jsonObject.putOnce("mqKey", mqApiActuatorDTO.getMqKey());
        }
        if (StrUtil.isNotEmpty(mqApiActuatorDTO.getExchangeName())) {
            jsonObject.putOnce("exchangeName", mqApiActuatorDTO.getExchangeName());
        }
        if (StrUtil.isNotEmpty(mqApiActuatorDTO.getQueueName())) {
            jsonObject.putOnce("queueName", mqApiActuatorDTO.getQueueName());
        }
        jsonObject.putOnce("qos", mqApiActuatorDTO.getQos());
        return JSONUtil.toJsonStr(jsonObject);
    }

    @Override
    public ApiActuatorBaseInfo decode(String config, ApiActuatorBaseInfo apiActuatorBaseInfo) {
        MQApiActuatorInfo mqApiActuatorDTO = new MQApiActuatorInfo(apiActuatorBaseInfo);
        if (StrUtil.isNotEmpty(config)) {
            JSONObject jsonObject = JSONUtil.parseObj(config);
            mqApiActuatorDTO.setMqKey(jsonObject.getStr("mqKey", ""));
            mqApiActuatorDTO.setExchangeName(jsonObject.getStr("exchangeName", ""));
            mqApiActuatorDTO.setQueueName(jsonObject.getStr("queueName", ""));
            mqApiActuatorDTO.setQos(jsonObject.getInt("qos", 100));
        }
        return mqApiActuatorDTO;
    }

    @Override
    public ApiActuatorBaseInfo test(String actuatorInfo) {
        try {
            MQApiActuatorInfo mqApiActuatorDTO = JSONUtil.toBean(JSONUtil.parseObj(actuatorInfo), MQApiActuatorInfo.class);
            return mqApiActuatorDTO;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getType() {
        return "MQ";
    }
}
