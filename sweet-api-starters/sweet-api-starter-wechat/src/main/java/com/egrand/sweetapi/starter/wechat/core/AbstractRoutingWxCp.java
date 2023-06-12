package com.egrand.sweetapi.starter.wechat.core;

import me.chanjar.weixin.cp.api.WxCpService;

public abstract class AbstractRoutingWxCp implements WxCpServiceFactory {

    /**
     * 子类实现决定最终WxCpService
     * @return
     */
    protected abstract WxCpService determineWxCp();

    @Override
    public WxCpService getWxCpService() {
        return determineWxCp();
    }
}
