package com.egrand.sweetapi.core.web;

import com.egrand.sweetapi.core.config.DynamicConfiguration;
import com.egrand.sweetapi.core.context.CookieContext;
import com.egrand.sweetapi.core.context.RequestContext;
import com.egrand.sweetapi.core.context.RequestEntity;
import com.egrand.sweetapi.core.context.SessionContext;
import com.egrand.sweetapi.core.exception.ValidateException;
import com.egrand.sweetapi.core.interceptor.ResultProvider;
import com.egrand.sweetapi.core.logging.LoggerContext;
import com.egrand.sweetapi.core.model.*;
import com.egrand.sweetapi.core.script.DynamicScriptContext;
import com.egrand.sweetapi.core.script.impl.JsDynamicScriptContext;
import com.egrand.sweetapi.core.service.impl.RequestDynamicRegistry;
import com.egrand.sweetapi.core.utils.BooleanUtils;
import com.egrand.sweetapi.core.utils.Constants;
import com.egrand.sweetapi.core.utils.ObjectConvertExtension;
import com.egrand.sweetapi.core.utils.PatternUtils;
import com.egrand.sweetapi.core.ModuleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.egrand.sweetapi.core.config.JsonCodeConstants.*;
import static com.egrand.sweetapi.core.utils.Constants.*;
import static org.springframework.http.HttpHeaders.*;

@Slf4j
public class RequestHandler {

    private static final List<String> DEFAULT_ALLOW_READ_RESPONSE_HEADERS = Arrays.asList(
            ACCESS_CONTROL_ALLOW_CREDENTIALS, ACCESS_CONTROL_ALLOW_HEADERS, ACCESS_CONTROL_ALLOW_METHODS, ACCESS_CONTROL_ALLOW_METHODS,
            CONTENT_TYPE, DATE, SERVER, SET_COOKIE, CONNECTION, CONTENT_LENGTH, CONTENT_ENCODING, TRANSFER_ENCODING, VARY);

    private static final Map<String, Object> EMPTY_MAP = new HashMap<>();

    private final ResultProvider resultProvider;

    private final RequestDynamicRegistry requestDynamicRegistry;

    private final HttpMessageConverters httpMessageConverters;

    protected DynamicConfiguration configuration;

    public RequestHandler(DynamicConfiguration configuration, RequestDynamicRegistry requestDynamicRegistry,
                          HttpMessageConverters httpMessageConverters) {
        this.configuration = configuration;
        this.requestDynamicRegistry = requestDynamicRegistry;
        this.httpMessageConverters = httpMessageConverters;
        this.resultProvider = configuration.getResultProvider();
    }

    /**
     * 测试入口、实际请求入口
     *
     * @param request       HttpServletRequest
     * @param response      HttpServletResponse
     * @param pathVariables 路径变量
     * @param parameters    表单参数&URL参数
     * @return 返回请求结果
     * @throws Throwable 处理失败抛出的异常
     */
    @ResponseBody
    public Object invoke(HttpServletRequest request, HttpServletResponse response,
                         @PathVariable(required = false) Map<String, Object> pathVariables,
                         @RequestHeader(required = false) Map<String, Object> defaultHeaders,
                         @RequestParam(required = false) Map<String, Object> parameters) throws Throwable {
        String clientId = null;
        Map<String, Object> headers = new LinkedCaseInsensitiveMap<>();
        headers.putAll(defaultHeaders);
        boolean requestedFromTest = (clientId = request.getHeader(HEADER_REQUEST_CLIENT_ID)) != null;
        // 构建请求实体
        ApiInfo apiInfo = requestDynamicRegistry.getApiInfoFromRequest(request);
        RequestEntity requestEntity = RequestEntity.create()
                .info(apiInfo)
                .request(request)
                .response(response)
                .requestedFromTest(requestedFromTest)
                .pathVariables(pathVariables)
                .parameters(parameters);
        ApiInfo info = requestEntity.getApiInfo();
        if (info == null) {
            log.error("{}找不到对应接口", request.getRequestURI());
            return afterCompletion(requestEntity, buildResult(requestEntity, API_NOT_FOUND, "接口不存在"));
        }
        // 设置请求实体头
        requestEntity.setHeaders(headers);
        // 设置请求实体Body
        Object bodyValue = readRequestBody(requestEntity.getRequest());
        requestEntity.setRequestBody(bodyValue);
        // 设置动态脚本上下文
        DynamicScriptContext context = this.createScriptContext(info.getMethod()+"-"+info.getPath(), requestEntity);
        requestEntity.setDynamicScriptContext(context);
        try {
            // 验证参数
            doValidate(context.getScriptName(), "参数", RequestParamType.TYPE_PARAMETER,
                    info.getParameters(), parameters, PARAMETER_INVALID, configuration.isDisabledUnknownParameter());
            // 设置参数
            context.putMapIntoContext(requestEntity.getParameters());
            // 验证 path
            doValidate(context.getScriptName(), "path", RequestParamType.TYPE_PATH,
                    info.getPaths(), requestEntity.getPathVariables(), PATH_VARIABLE_INVALID, configuration.isDisabledUnknownParameter());
            // 设置 cookie 变量
            context.set(VAR_NAME_COOKIE, new CookieContext(requestEntity.getRequest()));
            // 验证请求头
            doValidate(context.getScriptName(), "header", RequestParamType.TYPE_HEADER,
                    info.getHeaders(), headers, HEADER_INVALID, configuration.isDisabledUnknownParameter());
            // 设置 header 变量
            context.set(VAR_NAME_HEADER, headers);
            // 设置 session 变量
            context.set(VAR_NAME_SESSION, new SessionContext(requestEntity.getRequest().getSession()));
            // 设置 path 变量
            context.set(VAR_NAME_PATH_VARIABLE, requestEntity.getPathVariables());
            // 设置 body 变量
            if (bodyValue != null) {
                context.set(VAR_NAME_REQUEST_BODY, bodyValue);
            }
            List<BaseDefinition> requestBodyDefinitionList = info.getRequestBodyDefinition();
            if (null != requestBodyDefinitionList && requestBodyDefinitionList.size() != 0) {
                for (BaseDefinition baseDefinition : requestBodyDefinitionList) {
                    doValidate(context.getScriptName(), VAR_NAME_REQUEST_BODY, RequestParamType.TYPE_REQUEST_BODY,
                            Collections.singletonList(baseDefinition), (Map)bodyValue, BODY_INVALID, configuration.isDisabledUnknownParameter());
                }
            }
            context.set(VAR_NAME_MODULE_LOG, log);
            setUpModule(context);
        } catch (ValidateException e) {
            return afterCompletion(requestEntity, resultProvider.buildResult(requestEntity, RESPONSE_CODE_INVALID, e.getMessage()));
        } catch (Throwable root) {
            return processException(requestEntity, root);
        }
        RequestContext.setRequestEntity(requestEntity);
        if (requestedFromTest) {
            try {
                LoggerContext.SESSION.set(clientId);
                return invokeRequest(requestEntity);
            } finally {
                LoggerContext.remove();
            }
        } else {
            return invokeRequest(requestEntity);
        }
    }

    /**
     * 安装Module模块
     * @param context
     */
    private void setUpModule(DynamicScriptContext context) {
        Map<String, ModuleService> moduleServiceMap = this.configuration.getModuleServiceFactory().listPluginService();
        if (null != moduleServiceMap && moduleServiceMap.size() != 0) {
            moduleServiceMap.keySet().forEach(key -> context.set(moduleServiceMap.get(key).getType(), moduleServiceMap.get(key)));
        }
    }

    /**
     * 去除多余的请求参数
     * @param src request请求参数
     * @param definitions 接口要求的参数信息
     */
    private void removeUnknownKey(Map<String, Object> src, List<? extends BaseDefinition> definitions) {
        if (!src.isEmpty()) {
            Map<String, Object> newMap = new HashMap<>(definitions.size());
            for (BaseDefinition definition : definitions) {
                newMap.put(definition.getName(), src.get(definition.getName()));
            }
            src.clear();
            src.putAll(newMap);
        }
    }

    private boolean doValidateBody(String comment, RequestParamType requestParamType, BaseDefinition parameter,
                                   Map<String, Object> parameters, JsonCode jsonCode, Class<?> target) {
        // 参数非必填并且为空则直接返回true
        if (!parameter.isRequired() && parameters.isEmpty()) {
            return true;
        }
        // 参数必填并且为空则提示必填错误信息
        if (parameter.isRequired() && !BooleanUtils.isTrue(parameters.get(parameter.getName()))) {
            throw new ValidateException(jsonCode, StringUtils.defaultIfBlank(parameter.getError(), String.format("%s[%s]为必填项", comment, parameter.getName())));
        }
        // 获取参数值，将参数值数据类型与目标数据类型对比
        Object value = parameters.get(parameter.getName());
        if (value != null && !target.isAssignableFrom(value.getClass())) {
            throw new ValidateException(jsonCode, StringUtils.defaultIfBlank(parameter.getError(), String.format("%s[%s]数据类型错误", comment, parameter.getName())));
        }
        return false;
    }

    /**
     * 验证参数
     * @param scriptName 脚本名称
     * @param comment 验证说明
     * @param requestParamType 验证请求参数类型
     * @param validateParameters 接口要求的参数信息
     * @param parameters request请求参数
     * @param jsonCode 验证JsonCode
     * @param disabledUnknownParameter 不接收未经定义的参数
     * @return
     */
    private Map<String, Object> doValidate(String scriptName, String comment, RequestParamType requestParamType,
                                           List<? extends BaseDefinition> validateParameters, Map<String, Object> parameters,
                                           JsonCode jsonCode, boolean disabledUnknownParameter) {
        parameters = parameters != null ? parameters : EMPTY_MAP;
        // 接口检查参数为空，则直接返回参数
        if (CollectionUtils.isEmpty(validateParameters)) {
            return parameters;
        }
        // 判断是否需要去掉未定义的参数
        if (disabledUnknownParameter) {
            removeUnknownKey(parameters, validateParameters);
        }
        // 检查参数
        for (BaseDefinition parameter : validateParameters) {
            if (parameter.getDataType() == DataType.Any) {
                continue;
            }
            // 针对requestBody多层级的情况
            if (DataType.Object == parameter.getDataType()) {
                if (doValidateBody(comment, requestParamType, parameter, parameters, jsonCode, Map.class)) {
                    continue;
                }
                List<BaseDefinition> requestBodyDefinitionList = parameter.getChildren();
                if (null != requestBodyDefinitionList && requestBodyDefinitionList.size() != 0) {
                    for (BaseDefinition baseDefinition : requestBodyDefinitionList) {
                        doValidate(scriptName, VAR_NAME_REQUEST_BODY, requestParamType,
                                Collections.singletonList(baseDefinition), (Map) parameters.get(parameter.getName()),
                                jsonCode, disabledUnknownParameter);
                    }
                }
            } else if (parameter.getDataType().getValue().equalsIgnoreCase(DataType.Array.getValue())) {
                if (doValidateBody(comment, requestParamType, parameter, parameters, jsonCode, List.class)) {
                    continue;
                }
                List<Object> list = (List) parameters.get(parameter.getName());
                if (list != null) {
                    if (null != parameter.getChildren() && parameter.getChildren().size() != 0) {
                        DataType childDataType = parameter.getChildren().get(0).getDataType();
                        if (childDataType.getValue().equalsIgnoreCase(DataType.Object.getValue())) {
                            // 对象类型数组处理
                            List<Map<String, Object>> newList = list.stream().map(it -> doValidate(scriptName, VAR_NAME_REQUEST_BODY, requestParamType,
                                    parameter.getChildren().get(0).getChildren(), (LinkedHashMap<String, Object>) it,
                                    jsonCode, disabledUnknownParameter)).collect(Collectors.toList());
                            for (int i = 0, size = newList.size(); i < size; i++) {
                                list.set(i, newList.get(i));
                            }
                        } else {
                            // 基础类型数组处理
                            String name = parameter.getChildren().get(0).getName();
                            List<Map<String, Object>> newList = list.stream().map(it -> doValidate(scriptName, VAR_NAME_REQUEST_BODY, requestParamType,
                                    parameter.getChildren(), new HashMap<String, Object>() {{
                                        put(name, it);
                                    }},
                                    jsonCode, disabledUnknownParameter)).collect(Collectors.toList());
                            for (int i = 0, size = newList.size(); i < size; i++) {
                                list.set(i, newList.get(i).get(name));
                            }
                        }
                    }
                }
            } else if (StringUtils.isNotBlank(parameter.getName()) || parameters.containsKey(parameter.getName())) {
                // 判断是否为文件
                boolean isFile = parameter.getDataType() == DataType.MultipartFile || parameter.getDataType() == DataType.MultipartFiles;
                // 请求参数值
                String requestValue = StringUtils.defaultIfBlank(Objects.toString(parameters.get(parameter.getName()), Constants.EMPTY),
                        Objects.toString(parameter.getDefaultValue(), Constants.EMPTY));
                // 请求参数必填校验
                if (StringUtils.isBlank(requestValue) && !isFile) {
                    if (!parameter.isRequired()) {
                        continue;
                    }
                    throw new ValidateException(jsonCode, StringUtils.defaultIfBlank(parameter.getError(),
                            String.format("%s[%s]为必填项", comment, parameter.getName())));
                }
                try {
                    // 根据类型转换请求参数值
                    Object value = convertValue(parameter.getDataType(), requestValue);
                    // 文件参数的必填校验
                    if (isFile && parameter.isRequired()) {
                        if (value == null || (parameter.getDataType() == DataType.MultipartFiles && ((List<?>) value).isEmpty())) {
                            throw new ValidateException(jsonCode, StringUtils.defaultIfBlank(parameter.getError(), String.format("%s[%s]为必填项", comment, parameter.getName())));
                        }
                    }
                    // 正则验证
                    if (VALIDATE_TYPE_PATTERN.equals(parameter.getValidateType())) {
                        String expression = parameter.getExpression();
                        if (StringUtils.isNotBlank(expression) && !PatternUtils.match(Objects.toString(value, Constants.EMPTY).replaceAll("\\s*|\r|\n|\t",""), expression)) {
                            throw new ValidateException(jsonCode, StringUtils.defaultIfBlank(parameter.getError(), String.format("%s[%s]不满足正则表达式", comment, parameter.getName())));
                        }
                    }
                    parameters.put(parameter.getName(), value);
                } catch (ValidateException ve) {
                    throw ve;
                } catch (Exception e) {
                    throw new ValidateException(jsonCode, StringUtils.defaultIfBlank(parameter.getError(), String.format("%s[%s]不合法", comment, parameter.getName())));
                }
            }
        }
        // 取出表达式验证的参数
        List<BaseDefinition> validates = validateParameters.stream().filter(it -> VALIDATE_TYPE_EXPRESSION.equals(it.getValidateType()) && StringUtils.isNotBlank(it.getExpression())).collect(Collectors.toList());
        for (BaseDefinition parameter : validates) {
            DynamicScriptContext dynamicScriptContext = this.createScriptContext(null, null);
            // 将其他参数也放置脚本中，以实现“依赖”的情况
            dynamicScriptContext.putMapIntoContext(parameters);
            Object value = parameters.get(parameter.getName());
            if (value != null) {
                dynamicScriptContext.setScriptName(scriptName);
                // 设置自身变量
                dynamicScriptContext.set(EXPRESSION_DEFAULT_VAR_NAME, value);
                try {
                    if (!BooleanUtils.isTrue(dynamicScriptContext.executeExpression(parameter.getExpression()))) {
                        throw new ValidateException(jsonCode, StringUtils.defaultIfBlank(parameter.getError(), String.format("%s[%s]不满足表达式", comment, parameter.getName())));
                    }
                } catch (ScriptException e) {
                    throw new ValidateException(jsonCode, StringUtils.defaultIfBlank(parameter.getError(), String.format("%s[%s]不满足表达式", comment, parameter.getName())));
                }
            }
        }
        return parameters;
    }

    /**
     * 转换参数类型
     */
    private Object convertValue(DataType dataType, String value) {
        if (dataType == null) {
            return value;
        }
        try {
            if (dataType.isNumber()) {
                BigDecimal decimal = ObjectConvertExtension.asDecimal(value, null);
                if (decimal == null) {
                    throw new IllegalArgumentException();
                }
                return dataType.getConvert().apply(value);
            } else {
                Function convert = dataType.getConvert();
                if (convert != null) {
                    return dataType.getConvert().apply(value);
                }
            }
            return value;
        } catch (Throwable throwable) {
            throw new IllegalArgumentException(throwable);
        }
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
     * 执行请求
     * @param requestEntity 请求实体
     * @return 执行结果
     * @throws Throwable
     */
    private Object invokeRequest(RequestEntity requestEntity) throws Throwable {
        try {
            DynamicScriptContext context = requestEntity.getDynamicScriptContext();
            Object result = context.execute(requestEntity.getApiInfo().getScript());
            return afterCompletion(requestEntity, response(requestEntity, result));
        } catch (Throwable root) {
            return processException(requestEntity, root);
        } finally {
            RequestContext.remove();
        }
    }

    private Object processException(RequestEntity requestEntity, Throwable root) throws Throwable {
        if (configuration.isThrowException()) {
            afterCompletion(requestEntity, null, root);
            throw root;
        }
        log.error("接口{}请求出错", requestEntity.getRequest().getRequestURI(), root);
        return afterCompletion(requestEntity, resultProvider.buildException(requestEntity, root), root);
    }

    /**
     * 包装返回结果
     */
    private Object response(RequestEntity requestEntity, Object value) {
//        if (value == null) {
//            return null;
//        }
        if (value instanceof ResponseEntity) {
            return value;
        }
        return resultProvider.buildResult(requestEntity, value);
    }

    private Object afterCompletion(RequestEntity requestEntity, Object returnValue) {
        return afterCompletion(requestEntity, returnValue, null);
    }

    private Object afterCompletion(RequestEntity requestEntity, Object returnValue, Throwable throwable) {
        Set<String> exposeHeaders = new HashSet<>(16);
        if (returnValue instanceof ResponseEntity) {
            exposeHeaders.addAll(((ResponseEntity<?>) returnValue).getHeaders().keySet());
        }
        if (requestEntity.isRequestedFromTest()) {
            HttpServletResponse response = requestEntity.getResponse();
            exposeHeaders.addAll(response.getHeaderNames());
            exposeHeaders.addAll(DEFAULT_ALLOW_READ_RESPONSE_HEADERS);
        }
        if (!exposeHeaders.isEmpty()) {
            requestEntity.getResponse().setHeader(ACCESS_CONTROL_EXPOSE_HEADERS, String.join(",", exposeHeaders));
        }
        return returnValue;
    }

    private Object buildResult(RequestEntity requestEntity, JsonCode code, Object data) {
        return resultProvider.buildResult(requestEntity, code.getCode(), code.getMessage(), data);
    }

    /**
     * 读取RequestBody
     */
    private Object readRequestBody(HttpServletRequest request) throws IOException {
        if (this.httpMessageConverters != null && request.getContentType() != null) {
            MediaType mediaType = MediaType.valueOf(request.getContentType());
            Class clazz = Object.class;
            try {
                for (HttpMessageConverter<?> converter : this.httpMessageConverters) {
                    if (converter.canRead(clazz, mediaType)) {
                        return converter.read(clazz, new ServletServerHttpRequest(request));
                    }
                }
            } catch (HttpMessageNotReadableException ignored) {
                return null;
            }
        }
        return null;
    }
}
