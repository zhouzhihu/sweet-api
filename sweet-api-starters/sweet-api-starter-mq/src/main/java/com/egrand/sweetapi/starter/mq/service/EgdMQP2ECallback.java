package com.egrand.sweetapi.starter.mq.service;

/**
 * MQ生成者到交换机发送后回调接口
 */
public interface EgdMQP2ECallback {

    /**
     * 成功
     */
    void onSuccess(String id, boolean ack, String cause);

    /**
     * 失败
     */
    void onFailure(String id, boolean ack, String cause);
}
