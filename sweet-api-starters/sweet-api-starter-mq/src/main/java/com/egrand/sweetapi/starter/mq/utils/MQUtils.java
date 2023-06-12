package com.egrand.sweetapi.starter.mq.utils;

import com.egrand.sweetapi.starter.mq.callback.MQConfirmCallback;
import com.egrand.sweetapi.starter.mq.callback.MQReturnCallback;
import com.egrand.sweetapi.starter.mq.service.EgdMQE2QCallback;
import com.egrand.sweetapi.starter.mq.service.EgdMQHandler;
import com.egrand.sweetapi.starter.mq.service.EgdMQP2ECallback;
import com.egrand.sweetapi.starter.mq.toolkit.DynamicMQContextHolder;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

/**
 * 消费者帮助类
 */
@Component
@Slf4j
public class MQUtils {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static MQUtils mqUtils;

    private static Map<String, Channel> channelMap = new HashMap();

    @PostConstruct
    public void init(){
        mqUtils = this;
        mqUtils.rabbitTemplate = this.rabbitTemplate;
    }

    /**
     * 发送消息
     * @param mqKey MQ关键字
     * @param queueName 队列名称
     * @param msg 消息内容
     */
    public static Boolean send(String mqKey, String queueName, Object msg) {
        try {
            DynamicMQContextHolder.push(mqKey);
            mqUtils.rabbitTemplate.convertAndSend(queueName, msg);
        } finally {
            DynamicMQContextHolder.poll();
        }
        return true;
    }

    /**
     * 发送消息
     * @param mqKey MQ关键字
     * @param queueName 队列名称
     * @param msg 消息内容
     * @param messagePostProcessor message发送前处理器
     */
    public static Boolean send(String mqKey, String queueName, Object msg, MessagePostProcessor messagePostProcessor) {
        try {
            DynamicMQContextHolder.push(mqKey);
            mqUtils.rabbitTemplate.convertAndSend(queueName, msg, messagePostProcessor);
        } finally {
            DynamicMQContextHolder.poll();
        }
        return true;
    }

    /**
     * 发送消息
     * @param mqKey MQ关键字
     * @param queueName 队列名称
     * @param msg 消息内容
     * @param egdSendCallback MQ生成者到交换机发送后回调接口
     */
    public static Boolean send(String mqKey, String queueName, Object msg, EgdMQP2ECallback egdSendCallback) {
        try {
            DynamicMQContextHolder.push(mqKey);
            RabbitTemplate newRabbitTemplate = getRabbitTemplate();
            newRabbitTemplate.setConfirmCallback(new MQConfirmCallback(egdSendCallback));
            newRabbitTemplate.convertAndSend(queueName, msg);
        } finally {
            DynamicMQContextHolder.poll();
        }
        return true;
    }

    /**
     * 发送消息
     * @param mqKey MQ关键字
     * @param queueName 队列名称
     * @param msg 消息内容
     * @param messagePostProcessor message发送前处理器
     * @param egdSendCallback MQ生成者到交换机发送后回调接口
     */
    public static Boolean send(String mqKey, String queueName, Object msg, MessagePostProcessor messagePostProcessor, EgdMQP2ECallback egdSendCallback) {
        try {
            DynamicMQContextHolder.push(mqKey);
            RabbitTemplate newRabbitTemplate = getRabbitTemplate();
            newRabbitTemplate.setConfirmCallback(new MQConfirmCallback(egdSendCallback));
            newRabbitTemplate.convertAndSend(queueName, msg, messagePostProcessor);
        } finally {
            DynamicMQContextHolder.poll();
        }
        return true;
    }

    /**
     * 发送消息
     * @param mqKey MQ关键字
     * @param queueName 队列名称
     * @param msg 消息内容
     * @param egdSendCallback MQ生成者到交换机发送后回调接口
     * @param egdMQE2QCallback MQ从交换机到队列失败后的回调函数
     */
    public static Boolean send(String mqKey, String queueName, Object msg,
                            EgdMQP2ECallback egdSendCallback, EgdMQE2QCallback egdMQE2QCallback) {
        try {
            DynamicMQContextHolder.push(mqKey);
            RabbitTemplate newRabbitTemplate = getRabbitTemplate();
            newRabbitTemplate.setConfirmCallback(new MQConfirmCallback(egdSendCallback));
            newRabbitTemplate.setReturnCallback(new MQReturnCallback(egdMQE2QCallback));
            newRabbitTemplate.convertAndSend(queueName, msg);
        } finally {
            DynamicMQContextHolder.poll();
        }
        return true;
    }

    /**
     * 发送消息
     * @param mqKey MQ关键字
     * @param queueName 队列名称
     * @param msg 消息内容
     * @param messagePostProcessor message发送前处理器
     * @param egdSendCallback MQ生成者到交换机发送后回调接口
     * @param egdMQE2QCallback MQ从交换机到队列失败后的回调函数
     */
    public static Boolean send(String mqKey, String queueName, Object msg, MessagePostProcessor messagePostProcessor,
                               EgdMQP2ECallback egdSendCallback, EgdMQE2QCallback egdMQE2QCallback) {
        try {
            DynamicMQContextHolder.push(mqKey);
            RabbitTemplate newRabbitTemplate = getRabbitTemplate();
            newRabbitTemplate.setConfirmCallback(new MQConfirmCallback(egdSendCallback));
            newRabbitTemplate.setReturnCallback(new MQReturnCallback(egdMQE2QCallback));
            newRabbitTemplate.convertAndSend(queueName, msg, messagePostProcessor);
        } finally {
            DynamicMQContextHolder.poll();
        }
        return true;
    }

    /**
     * 添加普通消费模式
     * @param mqKey MQ关键字
     * @param queueName 队列名称
     * @param qos 限流，一次性推送最大消息个数
     * @param handler 监听器
     * @param arguments 参数
     *                  x-max-length:消息条数限制,该参数是非负整数值。限制加入queue中消息的条数。先进先出原则，超过10条后面的消息会顶替前面的消息。
     *                  x-max-length-bytes:消息容量限制,该参数是非负整数值。该参数和x-max-length目的一样限制队列的容量，但是这个是靠队列大小（bytes）来达到限制。
     *                  x-message-ttl:消息存活时间,该参数是非负整数值.创建queue时设置该参数可指定消息在该queue中待多久，可根据x-dead-letter-routing-key和x-dead-letter-exchange生成可延迟的死信队列。
     *                  x-max-priority:消息优先级,创建queue时arguments可以使用x-max-priority参数声明优先级队列 。该参数应该是一个整数，表示队列应该支持的最大优先级。建议使用1到10之间。目前使用更多的优先级将消耗更多的资源（Erlang进程）。
     *                                 设置该参数同时设置死信队列时或造成已过期的低优先级消息会在未过期的高优先级消息后面执行。该参数会造成额外的CPU消耗。
     *                  x-expires:存活时间,创建queue时参数arguments设置了x-expires参数，该queue会在x-expires到期后queue消息，亲身测试直接消失（哪怕里面有未消费的消息）。
     *                  x-dead-letter-exchange和x-dead-letter-routing-key:创建queue时参数arguments设置了x-dead-letter-routing-key和x-dead-letter-exchahttp://nge，
     *                                                                    会在x-message-ttl时间到期后把消息放到x-dead-letter-routing-key和x-dead-letter-exchange指定的队列中达到延迟队列的目的。
     */
    public static String receive(String mqKey, String queueName, int qos, EgdMQHandler handler, Map<String, Object> arguments) {
        return addNormalCounsumer(mqKey, queueName, handler,false, qos, arguments);
    }

    /**
     * 添加订阅消费模式
     * @param mqKey MQ关键字
     * @param exchangeName 交换机名称
     * @param queueName 队列名称
     * @param qos 限流，一次性推送最大消息个数
     * @param handler 监听器
     */
    public static String receive(String mqKey, String exchangeName, String queueName, int qos, EgdMQHandler handler, Map<String, Object> arguments){
        return addFanoutCounsumer(mqKey, exchangeName, queueName, handler, false, qos, arguments);
    }

    /**
     * 取消订阅
     * @param consumerTag 消息者标识
     * @return
     */
    public static Boolean cancel(String consumerTag) {
        try {
            if (channelMap.containsKey(consumerTag)) {
                Channel channel = channelMap.get(consumerTag);
                channel.basicCancel(consumerTag);
                channel.close();
            }
        } catch (Exception e) {
            log.error("mq cancel consumer error", e);
        }
        return true;
    }

    /**
     * 获取新的RabbitTemplate
     * @return
     */
    private static RabbitTemplate getRabbitTemplate(){
        RabbitTemplate rabbitTemplate = mqUtils.rabbitTemplate;
        RabbitTemplate newRabbitTemplate = new RabbitTemplate(rabbitTemplate.getConnectionFactory());
        newRabbitTemplate.setMessageConverter(rabbitTemplate.getMessageConverter());
        // TODO 默认设置为true，这里需要考虑配置后初始化
        newRabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

    /**
     * 注册队列
     * @param name 队列名称
     * @param durable 是否持久化
     * @param exclusive 是否排外
     * @param autoDelete 是否自动删除
     */
    private static void declareQueue(String name, boolean durable, boolean exclusive, boolean autoDelete) {
        Connection connection = mqUtils.rabbitTemplate.getConnectionFactory().createConnection();
        Channel channel = connection.createChannel(false);
        try {
            channel.queueDeclare(name, Boolean.valueOf(durable), Boolean.valueOf(exclusive)
                    , Boolean.valueOf(autoDelete), null);
            log.info("注册队列[{}]成功！", name);
        } catch (IOException e) {
            log.warn("队列注册失败", e);
        }
    }

    /**
     * 注册交互机
     * @param name 注册机名称
     * @param type 类型
     */
    private void declareExchange(String name, String type, boolean durable, boolean autoDelete) {
        Connection connection = mqUtils.rabbitTemplate.getConnectionFactory().createConnection();
        Channel channel = connection.createChannel(false);
        try {
            channel.exchangeDeclare(name, type, durable, autoDelete, null);
            log.info("注册交互机成功！");
        } catch (IOException e) {
            log.warn("队列交互机失败", e);
        }
    }

    private static String addNormalCounsumer(String mqKey, String queueName, EgdMQHandler handler,
                                    boolean isAutoAck, int qos, Map<String, Object> arguments) {
        try {
            DynamicMQContextHolder.push(mqKey);
            // 创建队列
            declareQueue(queueName, true, false, false);
            Channel channel = mqUtils.rabbitTemplate.getConnectionFactory().createConnection().createChannel(false);
            /**  设置限流机制
             *  param1: prefetchSize，消息本身的大小 如果设置为0  那么表示对消息本身的大小不限制
             *  param2: prefetchCount，告诉rabbitmq不要一次性给消费者推送大于N个消息
             *  param3：global，是否将上面的设置应用于整个通道，false表示只应用于当前消费者
             */
            channel.basicQos(0, qos, false);
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    try {
                        Map<String, Object> headers = properties.getHeaders();
                        long deliveryTag = envelope.getDeliveryTag();
                        boolean isMessageHandled = handler.receive(headers, message);
                        // 如果是自动应答的，不需要进行手动ack，这样会导致消息丢失
                        if (!isAutoAck) {
                            // 消息处理成功，进行处理成功后的流程
                            if (isMessageHandled) {
                                channel.basicAck(deliveryTag, false);
                                // 判断是否需要重试
                            } else {
                                // 消息处理失败，进行处理失败后的流程(失败后，重回队列，阻塞下一个消息的处理)
                                // channel.basicReject(deliveryTag, true);

                                // 消息处理失败，重新投递消息(不阻塞下一个消息的处理)
                                channel.basicAck(deliveryTag, false);
                                channel.basicPublish("", queueName, properties, body);
                                log.info("消息消费失败，等待自动超时后处理");
                            }
                        }
                    } catch (RejectedExecutionException e) {
                        if (!isAutoAck) {
                            // 只有没有开启ack时，才能重新入队列(阻塞下一个消息的处理)
                            // channel.basicReject(envelope.getDeliveryTag(), true);

                            // 只有没有开启ack时，重新投递消息(不阻塞下一个消息的处理)
                            channel.basicAck(envelope.getDeliveryTag(), false);
                            channel.basicPublish("", queueName, properties, body);
                        }
                        log.info("Mq delay message handle queue full");
                    } catch (Exception e) {
                        log.error("mq consumer message error", e);
                    }
                }

                @Override
                public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {

                }
            };
            String consumerTag = channel.basicConsume(queueName, isAutoAck, arguments, consumer);
            channelMap.put(consumerTag, channel);
            channel.close();
            return consumerTag;
        } catch (Exception e) {
            log.error("mq consumer registe error", e);
        } finally {
            DynamicMQContextHolder.poll();
        }
        return null;
    }

    private static String addFanoutCounsumer (String mqKey, String exchangeName, String queueName, EgdMQHandler handler,
                                     boolean isAutoAck, int qos, Map<String, Object> arguments) {
        try {
            DynamicMQContextHolder.push(mqKey);
            // 创建队列
            declareQueue(queueName, true, false, false);
            Channel channel = mqUtils.rabbitTemplate.getConnectionFactory().createConnection().createChannel(false);
            /**  设置限流机制
             *  param1: prefetchSize，消息本身的大小 如果设置为0  那么表示对消息本身的大小不限制
             *  param2: prefetchCount，告诉rabbitmq不要一次性给消费者推送大于N个消息
             *  param3：global，是否将上面的设置应用于整个通道，false表示只应用于当前消费者
             */
            channel.basicQos(0, qos, false);
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    try {
                        Map<String, Object> headers = properties.getHeaders();
                        long deliveryTag = envelope.getDeliveryTag();
                        boolean isMessageHandled = handler.receive(headers, message);
                        // 如果是自动应答的，不需要进行手动ack，这样会导致消息丢失
                        if (!isAutoAck) {
                            // 消息处理成功，进行处理成功后的流程
                            if (isMessageHandled) {
                                channel.basicAck(deliveryTag, false);
                                // 判断是否需要重试
                            } else {
                                // 消息处理失败，进行处理失败后的流程
                                channel.basicReject(deliveryTag, true);
                                log.info("消息消费失败，等待自动超时后处理");
                            }
                        }
                    } catch (RejectedExecutionException e) {
                        if (!isAutoAck) {
                            // 只有没有开启ack时，才能重新入队列
                            channel.basicReject(envelope.getDeliveryTag(), true);
                        }
                        log.info("Mq delay message handle queue full");
                    } catch (Exception e) {
                        log.error("mq consumer message error", e);
                    }
                }

                @Override
                public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {

                }
            };
            channel.queueDeclare(queueName, true, false
                    , false, null);
            channel.queueBind(queueName, exchangeName, "");
            return channel.basicConsume(queueName, isAutoAck, arguments, consumer);
        } catch (Exception e) {
            log.error("mq consumer registe error", e);
        } finally {
            DynamicMQContextHolder.poll();
        }
        return null;
    }

    public static String getHeader(Map<String, Object> headers, String key){
        String returnValue = null;
        try {
            Object value = headers.get(key);
            if(null == value)
                return null;
            byte[] valueAsByteArray = ((LongString) value).getBytes();
            returnValue = new String(valueAsByteArray, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.warn("extracted correlationId, but unable to decode it",e);
        }
        return returnValue;
    }
}
