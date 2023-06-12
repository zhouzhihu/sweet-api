package com.egrand.sweetapi.core.config;

import com.egrand.sweetapi.core.interceptor.ResultProvider;
import com.egrand.sweetapi.modules.ModuleServiceFactory;

/**
 * 动态API配置参数
 */
public class DynamicConfiguration {
    /**
     * 结果处理器
     */
    private ResultProvider resultProvider;

    /**
     * 动态API模块工厂
     */
    private ModuleServiceFactory moduleServiceFactory;

    /**
     * 请求出错时，是否抛出异常
     */
    private boolean throwException = false;

    /**
     * 不接收未经定义的参数
     */
    private boolean disabledUnknownParameter = false;

    public ResultProvider getResultProvider() {
        return resultProvider;
    }

    public void setResultProvider(ResultProvider resultProvider) {
        this.resultProvider = resultProvider;
    }

    public ModuleServiceFactory getModuleServiceFactory() {
        return moduleServiceFactory;
    }

    public void setModuleServiceFactory(ModuleServiceFactory moduleServiceFactory) {
        this.moduleServiceFactory = moduleServiceFactory;
    }

    public boolean isThrowException() {
        return throwException;
    }

    public void setThrowException(boolean throwException) {
        this.throwException = throwException;
    }

    public boolean isDisabledUnknownParameter() {
        return disabledUnknownParameter;
    }

    public void setDisabledUnknownParameter(boolean disabledUnknownParameter) {
        this.disabledUnknownParameter = disabledUnknownParameter;
    }
}
