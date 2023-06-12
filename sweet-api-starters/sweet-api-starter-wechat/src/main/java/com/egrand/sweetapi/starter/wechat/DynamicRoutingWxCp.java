package com.egrand.sweetapi.starter.wechat;

import com.egrand.sweetapi.starter.wechat.config.WxCpProperties;
import com.egrand.sweetapi.starter.wechat.core.AbstractRoutingWxCp;
import com.egrand.sweetapi.starter.wechat.toolkit.DynamicWxCpContextHolder;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 核心动态WxCp组件
 */
@Slf4j
public class DynamicRoutingWxCp extends AbstractRoutingWxCp implements InitializingBean, DisposableBean {

    /**
     * 所有WxCp服务
     */
    private final Map<String, WxCpService> cpServiceMap = new LinkedHashMap<>();

    /**
     * WxCp配置
     */
    private final Map<String, WxCpProperties.AppConfig> wxCpConfig;

    public DynamicRoutingWxCp(Map<String, WxCpProperties.AppConfig> wxCpConfig) {
        this.wxCpConfig = wxCpConfig;
    }

    /**
     * 获取当前WxCpService
     * @return
     */
    public WxCpService determineWxCp() {
        return getWxCp(DynamicWxCpContextHolder.peek());
    }

    /**
     * 获取所有WxCp服务
     * @return
     */
    public Map<String, WxCpService> listAll() {
        return this.cpServiceMap;
    }

    /**
     * 获取WxCpService
     *
     * @param cp WxCp名称
     * @return WxCpService
     */
    public WxCpService getWxCp(String cp) {
        if (StringUtils.isEmpty(cp)) {
            return determinePrimaryWxCp(cp);
        } else if (cpServiceMap.containsKey(cp)) {
            log.info("WxCp数据源：{}", cp);
            return cpServiceMap.get(cp);
        }
        return determinePrimaryWxCp(cp);
    }

    /**
     * 添加WxCp
     *
     * @param cp                WxCp名称
     * @param wxCpProperties    WxCp属性
     */
    public synchronized void addWxCp(String cp, WxCpProperties.AppConfig wxCpProperties) {
        if (!cpServiceMap.containsKey(cp)) {
            WxCpDefaultConfigImpl config = new WxCpDefaultConfigImpl();
            config.setCorpId(wxCpProperties.getCorpId());
            config.setAgentId(wxCpProperties.getAgentId());
            config.setCorpSecret(wxCpProperties.getSecret());
            config.setToken(wxCpProperties.getToken());
            config.setAesKey(wxCpProperties.getAesKey());
            WxCpServiceImpl service = new WxCpServiceImpl();
            service.setWxCpConfigStorage(config);
            cpServiceMap.put(cp, service);
            log.info("dynamic-wxcp - load a wxcp named [{}] success", cp);
        } else {
            log.warn("dynamic-wxcp - load a wxcp named [{}] failed, because it already exist", cp);
        }
    }

    /**
     * 删除WxCp
     *
     * @param cp WxCp名称
     */
    public synchronized void removeWxCp(String cp) {
        if (!StringUtils.hasText(cp)) {
            throw new RuntimeException("remove parameter could not be empty");
        }
        if (cpServiceMap.containsKey(cp)) {
            cpServiceMap.remove(cp);
            log.info("dynamic-wxcp - remove the wxcp named [{}] success", cp);
        } else {
            log.warn("dynamic-wxcp - could not find a wxcp named [{}]", cp);
        }
    }

    @Override
    public void destroy() throws Exception {
        cpServiceMap.clear();
        if (null != wxCpConfig && wxCpConfig.size() != 0)
            wxCpConfig.clear();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (null == wxCpConfig || wxCpConfig.size() == 0)
            return;
        for (Map.Entry<String, WxCpProperties.AppConfig> wxCpItem : wxCpConfig.entrySet()) {
            addWxCp(wxCpItem.getKey(), wxCpItem.getValue());
        }
    }

    private WxCpService determinePrimaryWxCp(String cp) {
        if (StringUtils.isEmpty(cp))
            log.info("WxCp数据源未指定，切换至默认default数据源");
        else
            log.info("WxCp数据源：[{}]不存在，切换至默认default数据源", cp);
        if (!cpServiceMap.containsKey(WxCpProperties.DEFAULT))
            throw new RuntimeException("未配置默认[default]的数据源！");
        return cpServiceMap.get(WxCpProperties.DEFAULT);
    }
}
