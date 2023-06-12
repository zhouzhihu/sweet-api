package com.egrand.sweetapi.modules.sweet;

import com.egrand.sweetapi.core.ApiActuatorService;
import com.egrand.sweetapi.core.context.RequestEntity;
import com.egrand.sweetapi.core.exception.APIException;
import com.egrand.sweetapi.core.model.ApiInfo;
import com.egrand.sweetapi.core.script.DynamicScriptContext;
import com.egrand.sweetapi.core.script.impl.JsDynamicScriptContext;
import com.egrand.sweetapi.core.service.impl.RequestDynamicRegistry;
import com.egrand.sweetapi.core.ModuleService;
import com.egrand.sweetapi.modules.ModuleServiceFactory;
import lombok.extern.slf4j.Slf4j;

import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.egrand.sweetapi.core.utils.Constants.*;

@Slf4j
public class SweetModule implements ModuleService, ApiActuatorService {

    private final ModuleServiceFactory moduleServiceFactory;

    private final RequestDynamicRegistry requestDynamicRegistry;

    public SweetModule(ModuleServiceFactory moduleServiceFactory, RequestDynamicRegistry requestDynamicRegistry) {
        this.moduleServiceFactory = moduleServiceFactory;
        this.requestDynamicRegistry = requestDynamicRegistry;
    }

    /**
     * 执行SweetAPI中的接口,原始内容，不包含code以及message信息
     *
     * @param method  请求方法
     * @param path    请求路径
     */
    @Override
    public <T> T execute(String method, String path) throws ScriptException {
        return this.execute(method, path, null);
    }

    /**
     * 执行SweetAPI中的接口,原始内容，不包含code以及message信息
     *
     * @param method  请求方法
     * @param path    请求路径
     * @param context 变量信息
     */
    @Override
    public <T> T execute(String method, String path, Map<String, Object> context) throws ScriptException {
        return this.execute(null, method, path, context);
    }

    @Override
    public <T> T execute(Long apiId, Map<String, Object> context) throws ScriptException {
        ApiInfo apiInfo = requestDynamicRegistry.getMapping(String.valueOf(apiId));
        if (null == apiInfo) {
            throw new APIException(String.format("找不到对应接口 [%s]", apiId));
        }
        return this.execute(apiInfo.getMethod(), apiInfo.getPath(), context);
    }

    private <T> T execute(RequestEntity requestEntity, String method, String path, Map<String, Object> context) throws ScriptException {
        String mappingKey = Objects.toString(method, "GET").toUpperCase() + ":" + Objects.toString(path, "");
        ApiInfo apiInfo = requestDynamicRegistry.getMapping(mappingKey);
        if (apiInfo == null) {
            throw new APIException(String.format("找不到对应接口 [%s:%s]", method, path));
        }
        if (context == null) {
            context = new HashMap<>();
        }
        context.put("apiInfo", apiInfo);
        return execute(requestEntity, apiInfo, context);
    }

    @SuppressWarnings({"unchecked"})
    private <T> T execute(RequestEntity requestEntity, ApiInfo info, Map<String, Object> context) throws ScriptException {
        DynamicScriptContext scriptContext = this.createScriptContext(info.getMethod()+"-"+info.getPath(), requestEntity);
        scriptContext.putMapIntoContext(context);
        if (requestEntity != null) {
            requestEntity.setDynamicScriptContext(scriptContext);
        }
        scriptContext.set(VAR_NAME_MODULE_LOG, log);
        setUpModule(scriptContext);
        return (T) scriptContext.execute(info.getScript());
    }

    /**
     * 创建脚本上下文
     * @param scriptName 脚本名
     * @param requestEntity 请求实体
     * @return 上下文
     */
    private DynamicScriptContext createScriptContext(String scriptName, RequestEntity requestEntity) {
        return new JsDynamicScriptContext();
    }

    /**
     * 安装Module模块
     * @param context
     */
    private void setUpModule(DynamicScriptContext context) {
        Map<String, ModuleService> moduleServiceMap = this.moduleServiceFactory.listPluginService();
        if (null != moduleServiceMap && moduleServiceMap.size() != 0) {
            moduleServiceMap.keySet().forEach(key -> context.set(moduleServiceMap.get(key).getType(), moduleServiceMap.get(key)));
        }
    }

    @Override
    public String getType() {
        return VAR_NAME_MODULE_SWEET;
    }
}
