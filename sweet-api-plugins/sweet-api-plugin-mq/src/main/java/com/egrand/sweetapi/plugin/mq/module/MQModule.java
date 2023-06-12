package com.egrand.sweetapi.plugin.mq.module;

import com.egrand.sweetapi.core.ModuleService;
import com.egrand.sweetapi.core.TenantService;
import com.egrand.sweetapi.starter.mq.service.EgdMQHandler;
import com.egrand.sweetapi.starter.mq.service.EgdMQP2ECallback;
import com.egrand.sweetapi.starter.mq.utils.MQUtils;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MQModule implements ModuleService {

    protected RabbitTemplate rabbitTemplate;

    private TenantService tenantService;

    public MQModule(RabbitTemplate rabbitTemplate, TenantService tenantService) {
        this.rabbitTemplate = rabbitTemplate;
        this.tenantService = tenantService;
    }

    public MQModule() {}

    public BoundMQModule mq(String mqKey) {
        return new BoundMQModule(this.rabbitTemplate, mqKey);
    }

    /**
     * 发送消息
     * @param queueName 队列名称
     * @param msg 消息内容
     */
    public void send(String queueName, Object msg) {
        this.execute((mqKey) -> MQUtils.send(mqKey, queueName, msg));
    }

    /**
     * 发送消息
     * @param queueName 队列名称
     * @param msg 消息内容
     * @param headers 头信息
     */
    public void send(String queueName, Object msg, Map<String, Object> headers) {
        this.execute((mqKey) -> MQUtils.send(mqKey, queueName, msg, message -> {
            if (null == headers || headers.size() == 0)
                return message;
            MessageProperties messageProperties = message.getMessageProperties();
            headers.keySet().forEach(key -> {
                messageProperties.setHeader(key, headers.get(key));
            });
            return message;
        }));
    }

    /**
     * 发送消息
     * @param queueName 队列名称
     * @param msg 消息内容
     * @param mqp2eCallbackSuccessFunction MQ生成者到交换机发送成功后回调接口
     * @param mqp2eCallbackFailureFunction MQ生成者到交换机发送失败后回调接口
     */
    public void send(String queueName, Object msg, Function<Map<String, Object>, Boolean> mqp2eCallbackSuccessFunction,
                     Function<Map<String, Object>, Boolean> mqp2eCallbackFailureFunction) {
        this.execute((mqKey) -> MQUtils.send(mqKey, queueName, msg, new EgdMQP2ECallback() {
            @Override
            public void onSuccess(String id, boolean ack, String cause) {
                Map<String, Object> args = new HashMap<>();
                args.put("id", id);
                args.put("ack", ack);
                args.put("cause", cause);
                mqp2eCallbackSuccessFunction.apply(args);
            }

            @Override
            public void onFailure(String id, boolean ack, String cause) {
                Map<String, Object> args = new HashMap<>();
                args.put("id", id);
                args.put("ack", ack);
                args.put("cause", cause);
                mqp2eCallbackFailureFunction.apply(args);
            }
        }));
    }

    /**
     * 发送消息
     * @param queueName 队列名称
     * @param msg 消息内容
     * @param headers 头信息
     * @param mqp2eCallbackSuccessFunction MQ生成者到交换机发送成功后回调接口
     * @param mqp2eCallbackFailureFunction MQ生成者到交换机发送失败后回调接口
     */
    public void send(String queueName, Object msg, Map<String, Object> headers, Function<Map<String, Object>, Boolean> mqp2eCallbackSuccessFunction,
                     Function<Map<String, Object>, Boolean> mqp2eCallbackFailureFunction) {
        this.execute((mqKey) -> MQUtils.send(mqKey, queueName, msg, message -> {
                if (null == headers || headers.size() == 0)
                    return message;
                MessageProperties messageProperties = message.getMessageProperties();
                headers.keySet().forEach(key -> {
                    messageProperties.setHeader(key, headers.get(key));
                });
                return message;
            }, new EgdMQP2ECallback() {
            @Override
            public void onSuccess(String id, boolean ack, String cause) {
                Map<String, Object> args = new HashMap<>();
                args.put("id", id);
                args.put("ack", ack);
                args.put("cause", cause);
                mqp2eCallbackSuccessFunction.apply(args);
            }

            @Override
            public void onFailure(String id, boolean ack, String cause) {
                Map<String, Object> args = new HashMap<>();
                args.put("id", id);
                args.put("ack", ack);
                args.put("cause", cause);
                mqp2eCallbackFailureFunction.apply(args);
            }
        }));
    }

    /**
     * 发送消息
     * @param queueName 队列名称
     * @param msg 消息内容
     * @param mqp2eCallbackSuccessFunction MQ生成者到交换机发送成功后回调接口
     * @param mqp2eCallbackFailureFunction MQ生成者到交换机发送失败后回调接口
     * @param mqe2qCallbackFunction MQ从交换机到队列失败后的回调函数
     */
    public void send(String queueName, Object msg,
                     Function<Map<String, Object>, Boolean> mqp2eCallbackSuccessFunction,
                     Function<Map<String, Object>, Boolean> mqp2eCallbackFailureFunction,
                     Function<Map<String, Object>, Boolean> mqe2qCallbackFunction) {
        this.execute((mqKey) -> MQUtils.send(mqKey, queueName, msg, new EgdMQP2ECallback() {
            @Override
            public void onSuccess(String id, boolean ack, String cause) {
                Map<String, Object> args = new HashMap<>();
                args.put("id", id);
                args.put("ack", ack);
                args.put("cause", cause);
                mqp2eCallbackSuccessFunction.apply(args);
            }

            @Override
            public void onFailure(String id, boolean ack, String cause) {
                Map<String, Object> args = new HashMap<>();
                args.put("id", id);
                args.put("ack", ack);
                args.put("cause", cause);
                mqp2eCallbackFailureFunction.apply(args);
            }
        }, (message, replyCode, replyText, exchange, routingKey) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("message", message);
            args.put("replyCode", replyCode);
            args.put("replyText", replyText);
            args.put("exchange", exchange);
            args.put("routingKey", routingKey);
            mqe2qCallbackFunction.apply(args);
        }));
    }

    /**
     * 发送消息
     * @param queueName 队列名称
     * @param msg 消息内容
     * @param mqp2eCallbackSuccessFunction MQ生成者到交换机发送成功后回调接口
     * @param mqp2eCallbackFailureFunction MQ生成者到交换机发送失败后回调接口
     * @param mqe2qCallbackFunction MQ从交换机到队列失败后的回调函数
     */
    public void send(String queueName, Object msg, Map<String, Object> headers,
                     Function<Map<String, Object>, Boolean> mqp2eCallbackSuccessFunction,
                     Function<Map<String, Object>, Boolean> mqp2eCallbackFailureFunction,
                     Function<Map<String, Object>, Boolean> mqe2qCallbackFunction) {
        this.execute((mqKey) -> MQUtils.send(mqKey, queueName, msg, message -> {
                if (null == headers || headers.size() == 0)
                    return message;
                MessageProperties messageProperties = message.getMessageProperties();
                headers.keySet().forEach(key -> {
                    messageProperties.setHeader(key, headers.get(key));
                });
                return message;
            }, new EgdMQP2ECallback() {
            @Override
            public void onSuccess(String id, boolean ack, String cause) {
                Map<String, Object> args = new HashMap<>();
                args.put("id", id);
                args.put("ack", ack);
                args.put("cause", cause);
                mqp2eCallbackSuccessFunction.apply(args);
            }

            @Override
            public void onFailure(String id, boolean ack, String cause) {
                Map<String, Object> args = new HashMap<>();
                args.put("id", id);
                args.put("ack", ack);
                args.put("cause", cause);
                mqp2eCallbackFailureFunction.apply(args);
            }
        }, (message, replyCode, replyText, exchange, routingKey) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("message", message);
            args.put("replyCode", replyCode);
            args.put("replyText", replyText);
            args.put("exchange", exchange);
            args.put("routingKey", routingKey);
            mqe2qCallbackFunction.apply(args);
        }));
    }

    /**
     * 添加普通消费模式
     * @param queueName 队列名称
     * @param qos 限流，一次性推送最大消息个数
     * @param handler 监听器
     * @return 返回消费者标识
     */
    public String receive(String queueName, int qos, EgdMQHandler handler, Map<String, Object> arguments) {
        return this.execute((mqKey) -> MQUtils.receive(mqKey, queueName, qos, handler, arguments));
    }

    /**
     * 添加订阅消费模式
     * @param exchangeName 交换机名称
     * @param queueName 队列名称
     * @param qos 限流，一次性推送最大消息个数
     * @param handler 监听器
     * @return 返回消费者标识
     */
    public String receive(String exchangeName, String queueName, int qos, EgdMQHandler handler, Map<String, Object> arguments) {
        return this.execute((mqKey) -> MQUtils.receive(mqKey, exchangeName, queueName, qos, handler, arguments));
    }

    /**
     * 取消订阅
     * @param consumerTag 消费者标识
     */
    public void cancel(String consumerTag) {
        this.execute((mqKey) -> MQUtils.cancel(consumerTag));
    }

    public <T> T execute(Function<String, T> function) {
        return function.apply(this.tenantService.getTenant());
    }

    @Override
    public String getType() {
        return "mq";
    }
}
