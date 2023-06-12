package com.egrand.sweetapi.plugin.task.module;

import com.egrand.sweetapi.core.ApiActuatorService;
import com.egrand.sweetapi.core.ModuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;

import javax.script.ScriptException;
import java.util.Map;

@Slf4j
public class TaskModule implements ModuleService {

    private final TaskScheduler taskScheduler;

    private final ApiActuatorService apiActuatorService;

    public TaskModule(ApiActuatorService apiActuatorService, TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
        this.apiActuatorService = apiActuatorService;
    }

    /**
     * 注册定时任务
     * @param cron 定时任务表达式
     * @param method API请求方法
     * @param path API路径
     * @return 是否成功
     * @throws ScriptException
     */
    public Boolean register(String cron, String method, String path) throws ScriptException {
        return this.register(cron, method, path, null);
    }

    /**
     * 注册定时任务
     * @param cron 定时任务表达式
     * @param method API请求方法
     * @param path API路径
     * @param context API参数
     * @return 是否成功
     * @throws ScriptException
     */
    public Boolean register(String cron, String method, String path, Map<String, Object> context) throws ScriptException {
        if (taskScheduler != null) {
            CronTask cronTask = new CronTask(() -> {
                try {
                    log.info("定时任务:[{},{}]开始执行", method, path);
                    apiActuatorService.execute(method, path, context);
                } catch (ScriptException e) {
                    log.error("定时任务执行出错", e);
                } finally {
                    log.info("定时任务:[{}, {}]执行完毕", method, path);
                }
            }, cron);
            taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());
            log.info("注册定时任务:[{},{},{}]", method, path, cron);
        }
        return true;
    }

    @Override
    public String getType() {
        return "task";
    }
}
