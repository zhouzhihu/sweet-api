package com.egrand.sweetapi.plugin.mq.adapter;

import com.egrand.sweetapi.core.ApiActuatorBaseInfo;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * MQ执行器配置
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class MQApiActuatorInfo extends ApiActuatorBaseInfo {

    public MQApiActuatorInfo(ApiActuatorBaseInfo apiActuatorBaseInfo) {
        this.setId(apiActuatorBaseInfo.getId());
        this.setName(apiActuatorBaseInfo.getName());
        this.setType(apiActuatorBaseInfo.getType());
        this.setKey(apiActuatorBaseInfo.getKey());
        this.setUserName(apiActuatorBaseInfo.getUserName());
        this.setApiId(apiActuatorBaseInfo.getApiId());
        this.setTimeout(apiActuatorBaseInfo.getTimeout());
    }

    /**
     * MQ关键字
     */
    private String mqKey;

    /**
     * 交换机名称
     */
    private String exchangeName;

    /**
     * 队列名称
     */
    private String queueName;

    /**
     * 一次性推送最大消息个数
     */
    private int qos;

    /**
     * 参数配置
     */
    private Map<String, Object> arguments;
}
