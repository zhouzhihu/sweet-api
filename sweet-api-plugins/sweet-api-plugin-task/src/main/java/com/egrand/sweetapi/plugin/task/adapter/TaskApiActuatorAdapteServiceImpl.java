package com.egrand.sweetapi.plugin.task.adapter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.egrand.sweetapi.core.ApiActuatorAdapteService;
import com.egrand.sweetapi.core.ApiActuatorBaseInfo;
import com.egrand.sweetapi.core.ApiActuatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Component;

import javax.script.ScriptException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Component
@Slf4j
public class TaskApiActuatorAdapteServiceImpl implements ApiActuatorAdapteService {

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private ApiActuatorService apiActuatorService;

    /**
     * 消费列表(key:apiID,value:生成的任务标识)
     */
    private final Map<Long, ScheduledFuture<?>> apiTaskMap = new LinkedHashMap<>();

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
        TaskApiActuatorInfo taskApiActuatorDTO = (TaskApiActuatorInfo) apiActuatorBaseInfo;
        if (taskScheduler != null) {
            CronTask cronTask = new CronTask(() -> {
                try {
                    log.info("定时任务:[{}]开始执行", apiActuatorBaseInfo.getApiId());
                    Map<String, Object> contextMap = new HashMap<>();
                    String context = taskApiActuatorDTO.getContext();
                    if (StrUtil.isNotEmpty(context)) {
                        contextMap = JSONUtil.toBean(context, Map.class);
                    }
                    apiActuatorService.execute(apiActuatorBaseInfo.getApiId(), contextMap);
                } catch (ScriptException e) {
                    log.error("定时任务执行出错", e);
                } finally {
                    log.info("定时任务:[{}]执行完毕", apiActuatorBaseInfo.getApiId());
                }
            }, taskApiActuatorDTO.getCron());
            apiTaskMap.put(apiActuatorBaseInfo.getApiId(), taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger()));
        }
        return true;
    }

    @Override
    public Boolean update(ApiActuatorBaseInfo apiActuatorBaseInfo, ApiActuatorBaseInfo oldApiActuatorBaseInfo) {
        this.delete(oldApiActuatorBaseInfo);
        return this.save(apiActuatorBaseInfo);
    }

    @Override
    public Boolean delete(ApiActuatorBaseInfo apiActuatorBaseInfo) {
        TaskApiActuatorInfo taskApiActuatorDTO = (TaskApiActuatorInfo) apiActuatorBaseInfo;
        if (apiTaskMap.containsKey(taskApiActuatorDTO.getApiId())) {
            ScheduledFuture<?> scheduledFuture = apiTaskMap.get(taskApiActuatorDTO.getApiId());
            if (scheduledFuture != null) {
                try {
                    scheduledFuture.cancel(true);
                } catch (Exception e) {
                    log.warn("定时任务:[{}]取消失败", apiActuatorBaseInfo.getApiId(), e);
                }
            }
        }
        return true;
    }

    @Override
    public String encode(ApiActuatorBaseInfo apiActuatorBaseInfo) {
        TaskApiActuatorInfo taskApiActuatorDTO = (TaskApiActuatorInfo) apiActuatorBaseInfo;
        JSONObject jsonObject = new JSONObject();
        if (StrUtil.isNotEmpty(taskApiActuatorDTO.getCron())) {
            jsonObject.putOnce("cron", taskApiActuatorDTO.getCron());
        }
        if (StrUtil.isNotEmpty(taskApiActuatorDTO.getContext())) {
            jsonObject.putOnce("context", taskApiActuatorDTO.getContext());
        }
        return JSONUtil.toJsonStr(jsonObject);
    }

    @Override
    public ApiActuatorBaseInfo decode(String config, ApiActuatorBaseInfo apiActuatorBaseInfo) {
        TaskApiActuatorInfo taskApiActuatorDTO = new TaskApiActuatorInfo(apiActuatorBaseInfo);
        if (StrUtil.isNotEmpty(config)) {
            JSONObject jsonObject = JSONUtil.parseObj(config);
            taskApiActuatorDTO.setCron(jsonObject.getStr("cron", ""));
            taskApiActuatorDTO.setContext(jsonObject.getStr("context", ""));
        }
        return taskApiActuatorDTO;
    }

    @Override
    public ApiActuatorBaseInfo test(String actuatorInfo) {
        try {
            TaskApiActuatorInfo taskApiActuatorDTO = JSONUtil.toBean(JSONUtil.parseObj(actuatorInfo), TaskApiActuatorInfo.class);
            return taskApiActuatorDTO;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getType() {
        return "TASK";
    }
}
