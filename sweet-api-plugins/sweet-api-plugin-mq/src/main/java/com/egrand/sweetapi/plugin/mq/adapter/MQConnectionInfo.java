package com.egrand.sweetapi.plugin.mq.adapter;

import com.egrand.sweetapi.core.ConnectionBaseInfo;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * MQ连接配置
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class MQConnectionInfo extends ConnectionBaseInfo {

    public MQConnectionInfo(ConnectionBaseInfo connectionBaseInfo) {
        this.setId(connectionBaseInfo.getId());
        this.setName(connectionBaseInfo.getName());
        this.setKey(connectionBaseInfo.getKey());
        this.setType(connectionBaseInfo.getType());
        this.setExtendConfigList(connectionBaseInfo.getExtendConfigList());
        this.setTimeout(connectionBaseInfo.getTimeout());
    }

    /**
     * 主机
     */
    private String host;

    /**
     * 端口
     */
    private String port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 虚拟主机
     */
    private String virtualHost;

    /**
     * 发布确认属性配置(NONE/SIMPLE/CORRELATED)
     * confirm-type有none、correlated、simple这三种类型
     * none：表示禁用发布确认模式，默认值，使用此模式之后，不管消息有没有发送到Broker都不会触发 ConfirmCallback回调。
     * correlated：表示消息成功到达Broker后触发ConfirmCalllBack回调
     * simple：simple模式下如果消息成功到达Broker后一样会触发
     */
    private String publisherConfirmType;

    /**
     * 确保消息在未被队列接收时返回
     * spring.rabbitmq.template.mandatory属性的优先级高于spring.rabbitmq.publisher-returns的优先级
     * spring.rabbitmq.template.mandatory属性可能会返回三种值null、false、true,
     * spring.rabbitmq.template.mandatory结果为true、false时会忽略掉spring.rabbitmq.publisher-returns属性的值
     * spring.rabbitmq.template.mandatory结果为null（即不配置）时结果由spring.rabbitmq.publisher-returns确定
     */
    private int publisherReturns;

    /**
     * 指定消息在没有被队列接收时是否强行退回还是直接丢弃
     */
    private int mandatory;

    /**
     * none(自动模式（默认开启）);
     * manual(手动模式);
     * auto(自动模式（根据侦听器检测是正常返回、还是抛出异常来发出 ack/nack）)
     */
    private String acknowledgeMode;

}
