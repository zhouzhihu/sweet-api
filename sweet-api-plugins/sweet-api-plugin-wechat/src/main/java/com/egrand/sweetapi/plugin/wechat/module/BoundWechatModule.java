package com.egrand.sweetapi.plugin.wechat.module;

import cn.hutool.core.util.StrUtil;
import com.egrand.sweetapi.starter.wechat.core.WxCpTemplate;
import com.egrand.sweetapi.starter.wechat.toolkit.DynamicWxCpContextHolder;

import java.util.function.Supplier;

public class BoundWechatModule extends WechatModule {

    private String cpKey = "";

    public BoundWechatModule(WxCpTemplate wxCpTemplate, String cpKey) {
        this.wxCpTemplate = wxCpTemplate;
        this.cpKey = cpKey;
    }

    @Override
    public <T> T execute(Supplier<T> supplier) {
        try {
            if (StrUtil.isNotEmpty(cpKey)) {
                DynamicWxCpContextHolder.push(cpKey);
            }
            return supplier.get();
        } finally {
            DynamicWxCpContextHolder.poll();
        }
    }
}
