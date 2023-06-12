package com.egrand.sweetapi.starter.wechat.core;

import me.chanjar.weixin.cp.api.WxCpService;

public interface WxCpServiceFactory {
    /**
     * 获取WxCpService
     * @return
     */
    WxCpService getWxCpService();
}
