package com.egrand.sweetapi.plugin.task.adapter;

import com.egrand.sweetapi.core.ApiActuatorBaseInfo;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * Task执行器配置
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class TaskApiActuatorInfo extends ApiActuatorBaseInfo {

    public TaskApiActuatorInfo(ApiActuatorBaseInfo apiActuatorBaseInfo) {
        this.setId(apiActuatorBaseInfo.getId());
        this.setName(apiActuatorBaseInfo.getName());
        this.setType(apiActuatorBaseInfo.getType());
        this.setKey(apiActuatorBaseInfo.getKey());
        this.setUserName(apiActuatorBaseInfo.getUserName());
        this.setApiId(apiActuatorBaseInfo.getApiId());
        this.setTimeout(apiActuatorBaseInfo.getTimeout());
    }

    /**
     * 定时任务表达式
     */
    private String cron;

    /**
     * 参数配置
     */
    private String context;
}
