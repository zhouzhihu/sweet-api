package com.egrand.sweetapi.core.service.impl;

import com.egrand.sweetapi.core.event.ApiEvent;
import com.egrand.sweetapi.core.event.GroupEvent;
import com.egrand.sweetapi.core.exception.InvalidArgumentException;
import com.egrand.sweetapi.core.model.ApiInfo;
import com.egrand.sweetapi.core.service.AbstractDynamicRegistry;
import com.egrand.sweetapi.core.utils.Mapping;
import com.egrand.sweetapi.core.web.RequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

import static com.egrand.sweetapi.core.config.JsonCodeConstants.REQUEST_PATH_CONFLICT;

/**
 * 动态请求注册
 */
@Slf4j
public class RequestDynamicRegistry extends AbstractDynamicRegistry<ApiInfo> {

    private final Mapping mapping;

    private Object handler;

    private final Method method = RequestHandler.class.getDeclaredMethod("invoke", HttpServletRequest.class, HttpServletResponse.class, Map.class, Map.class, Map.class);

    private final boolean allowOverride;

    private final String prefix;

    public RequestDynamicRegistry(Mapping mapping, boolean allowOverride, String prefix) throws NoSuchMethodException {
        this.mapping = mapping;
        this.allowOverride = allowOverride;
        this.prefix = StringUtils.defaultIfBlank(prefix, "") + "/";
    }

    public void setHandler(Object handler) {
        this.handler = handler;
    }

    @EventListener(condition = "#event.type == 'api'")
    public void onFileEvent(ApiEvent event) {
        processEvent(event);
    }

    @EventListener(condition = "#event.type == 'folder'")
    public void onFileEvent(GroupEvent event) {
        processEvent(event);
    }

    /**
     * 根据Request路径获取注册的API信息
     * @param request 请求信息
     * @return
     */
    public ApiInfo getApiInfoFromRequest(HttpServletRequest request) {
        String mappingKey = Objects.toString(request.getMethod(), "GET").toUpperCase() + ":" + request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return getMapping(mappingKey);
    }

    public boolean register(MappingNode<ApiInfo> mappingNode) {
        String mappingKey = mappingNode.getMappingKey();
        int index = mappingKey.indexOf(":");
        String requestMethod = mappingKey.substring(0, index);
        String path = mappingKey.substring(index + 1);
        RequestMappingInfo requestMappingInfo = mapping.paths(path).methods(RequestMethod.valueOf(requestMethod.toUpperCase())).build();
        if (mapping.getHandlerMethods().containsKey(requestMappingInfo)) {
            if (!allowOverride) {
                log.error("接口[{}({})]与应用冲突，无法注册", mappingNode.getEntity().getName(), mappingKey);
                throw new InvalidArgumentException(REQUEST_PATH_CONFLICT.format(mappingNode.getEntity().getName(),mappingKey));
            }
            log.warn("取消注册应用接口:{}", requestMappingInfo);
            // 取消注册原接口
            mapping.unregister(requestMappingInfo);
        }
        log.debug("注册接口[{}({})]", mappingNode.getEntity().getName(), mappingKey);
        mapping.register(requestMappingInfo, handler, method);
        mappingNode.setMappingData(requestMappingInfo);
        return true;
    }

    @Override
    protected void unregister(MappingNode<ApiInfo> mappingNode) {
        log.debug("取消注册接口[{}({})]", mappingNode.getEntity().getName(), mappingNode.getMappingKey());
        mapping.unregister((RequestMappingInfo) mappingNode.getMappingData());
    }

    @Override
    public String buildKey(ApiInfo info) {
        return info.getMethod().toUpperCase() + ":" + info.getPath();
    }
}
