package com.egrand.sweetapi.plugin.restful.module;

import cn.hutool.json.JSONObject;
import com.egrand.sweetapi.core.ModuleService;
import com.egrand.sweetapi.plugin.restful.adapter.RESTfulConnectionInfo;
import com.egrand.sweetapi.plugin.restful.adapter.RESTfulConnectionStore;
import com.egrand.sweetplugin.bootstrap.annotation.AutowiredType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RESTfulModule implements ModuleService {

    @AutowiredType(AutowiredType.Type.MAIN)
    @Autowired
    private RestTemplate mainRestTemplate;

    /**
     * RestTemplate
     */
    private final RestTemplate template;

    private final RESTfulConnectionStore restfulConnectionStore;

    /**
     * Http请求方法，默认为GET
     */
    private HttpMethod method = HttpMethod.GET;

    /**
     * 请求实体
     */
    private HttpEntity<Object> entity = null;

    /**
     * 请求Body
     */
    private Object requestBody;

    /**
     * Http请求头
     */
    private final HttpHeaders httpHeaders = new HttpHeaders();

    /**
     * URL参数
     */
    private final MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

    /**
     * Form参数
     */
    private final MultiValueMap<String, Object> data = new LinkedMultiValueMap<>();

    private final Map<String, ?> variables = new HashMap<>();

    /**
     * 响应类型
     */
    private Class<?> responseType = String.class;

    /**
     * RESTFul关键字
     */
    private String key;

    /**
     * 连接url
     */
    private String url;

    public RESTfulModule(RestTemplate template, RESTfulConnectionStore restfulConnectionStore) {
        this.template = template;
        this.restfulConnectionStore = restfulConnectionStore;
    }

    public RESTfulModule(RestTemplate template, RESTfulConnectionStore restfulConnectionStore,
                         String url, String key) {
        this.template = template;
        this.restfulConnectionStore = restfulConnectionStore;
        this.url = url;
        this.key = key;
    }

    /**
     * 创建连接
     * @param url 目标URL
     * @return
     */
    public RESTfulModule connect(String url) {
        return new RESTfulModule(this.template, this.restfulConnectionStore, url, "");
    }

    /**
     * 创建连接
     * @param url 目标URL
     * @return
     */
    public RESTfulModule connect(String key, String url) {
        return new RESTfulModule(this.template, this.restfulConnectionStore, url, key);
    }

    /**
     * 本地服务调用
     * @param serviceName 服务名
     * @param path 路径
     * @return
     */
    public RESTfulModule connectService(String serviceName, String path) {
        return new RESTfulModule(this.mainRestTemplate, this.restfulConnectionStore, "http://" + serviceName + path, "");
    }

    /**
     * 设置请求方法，默认GET
     * @param method 请求方法
     * @return
     */
    public RESTfulModule method(HttpMethod method) {
        this.method = method;
        return this;
    }

    /**
     * 设置`ContentType`
     * @param contentType Content-Type值
     * @return
     */
    public RESTfulModule contentType(String contentType) {
        return contentType(MediaType.parseMediaType(contentType));
    }

    /**
     * 设置`ContentType`
     * @param mediaType Content-Type值
     * @return
     */
    public RESTfulModule contentType(MediaType mediaType) {
        this.httpHeaders.setContentType(mediaType);
        return this;
    }

    /**
     * 设置`RequestBody`
     * @param requestBody RequestBody
     * @return
     */
    public RESTfulModule body(Object requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    /**
     * 设置header
     * @param key header名
     * @param value header值
     * @return
     */
    public RESTfulModule header(String key, String value) {
        httpHeaders.add(key, value);
        return this;
    }

    /**
     * 批量设置header
     * @param values
     * @return
     */
    public RESTfulModule header(Map<String, Object> values) {
        values.entrySet()
                .stream()
                .filter(it -> it.getValue() != null)
                .forEach(entry -> header(entry.getKey(), entry.getValue().toString()));
        return this;
    }

    /**
     * 设置URL参数
     * @param key 参数名
     * @param values 参数值
     * @return
     */
    public RESTfulModule param(String key, Object... values) {
        if (values != null) {
            for (Object value : values) {
                this.params.add(key, value);
            }
        }
        return this;
    }

    /**
     *批量设置URL参数
     * @param values 参数值
     * @return
     */
    public RESTfulModule param(Map<String, Object> values) {
        values.forEach((key, value) -> param(key, Objects.toString(value, "")));
        return this;
    }

    /**
     * 设置form参数
     * @param key 参数名
     * @param values 参数值
     * @return
     */
    public RESTfulModule data(String key, Object... values) {
        if (values != null) {
            for (Object value : values) {
                this.data.add(key, value);
            }
        }
        return this;
    }

    /**
     * 批量设置form参数
     * @param values 参数值
     * @return
     */
    public RESTfulModule data(Map<String, Object> values) {
        values.forEach((key, value) -> data(key, Objects.toString(value, "")));
        return this;
    }

    /**
     * 设置返回值为`byte[]`
     * @return
     */
    public RESTfulModule expectBytes() {
        this.responseType = byte[].class;
        return this;
    }

    /**
     * 设置返回值为`JSONObject`
     * @return
     */
    public RESTfulModule expectJson() {
        this.responseType = JSONObject.class;
        return this;
    }

    /**
     * 发送`POST`请求
     * @return
     */
    public ResponseEntity<?> post() {
        this.method(HttpMethod.POST);
        return this.execute();
    }

    /**
     * "发送`GET`请求"
     * @return
     */
    public ResponseEntity<?> get() {
        this.method(HttpMethod.GET);
        return this.execute();
    }

    /**
     * "发送`PUT`请求"
     * @return
     */
    public ResponseEntity<?> put() {
        this.method(HttpMethod.PUT);
        return this.execute();
    }

    /**
     * "发送`DELETE`请求"
     * @return
     */
    public ResponseEntity<?> delete() {
        this.method(HttpMethod.DELETE);
        return this.execute();
    }

    /**
     * 发送`HEAD`请求
     * @return
     */
    public ResponseEntity<?> head() {
        this.method(HttpMethod.HEAD);
        return this.execute();
    }

    /**
     * 发送`OPTIONS`请求
     * @return
     */
    public ResponseEntity<?> options() {
        this.method(HttpMethod.OPTIONS);
        return this.execute();
    }

    /**
     * "发送`TRACE`请求"
     * @return
     */
    public ResponseEntity<?> trace() {
        this.method(HttpMethod.TRACE);
        return this.execute();
    }

    /**
     * 发送`PATCH`请求
     * @return
     */
    public ResponseEntity<?> patch() {
        this.method(HttpMethod.PATCH);
        return this.execute();
    }

    /**
     * 执行请求
     * @return
     */
    public ResponseEntity<?> execute() {
        RESTfulConnectionInfo restfulConnectionDTO = this.restfulConnectionStore.getRestfulMap().get(this.key);
        if (null != restfulConnectionDTO) {
            // 组装URL
            String baseUrl = restfulConnectionDTO.getBaseUrl();
            if (!baseUrl.isEmpty())
                this.url = baseUrl + this.url;
            // 组装参数
            Map<String, String> urlParams = restfulConnectionDTO.getUrlParams();
            if (null != urlParams && urlParams.size() != 0) {
                urlParams.forEach((k, v) -> this.params.add(k, v));
            }
        }
        // 拼装URL参数
        if (!this.params.isEmpty()) {
            String queryString = this.params.entrySet().stream()
                    .map(it -> it.getValue().stream()
                            .map(value -> it.getKey() + "=" + value)
                            .collect(Collectors.joining("&"))
                    ).collect(Collectors.joining("&"));
            if (StringUtils.isNotBlank(queryString)) {
                this.url += (this.url.contains("?") ? "&" : "?") + queryString;
            }
        }
        if (null != restfulConnectionDTO) {
            // 设置请求格式和编码
            String requestFormat = restfulConnectionDTO.getRequestFormat();
            String requestEncode = restfulConnectionDTO.getRequestEncode();
            if (requestFormat.equalsIgnoreCase("json"))
                this.contentType(MediaType.APPLICATION_JSON);
            if (requestFormat.equalsIgnoreCase("xml"))
                this.contentType(MediaType.APPLICATION_XML);
            if (requestEncode.equalsIgnoreCase("utf-8")) {
                this.httpHeaders.set(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.toString());
            } else if (requestEncode.equalsIgnoreCase("ASCII")) {
                this.httpHeaders.set(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.US_ASCII.toString());
            } else if (requestEncode.equalsIgnoreCase("GB2312")) {
                this.httpHeaders.set(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.toString());
            } else if (requestEncode.equalsIgnoreCase("BIG5")) {
                this.httpHeaders.set(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.toString());
            } else if (requestEncode.equalsIgnoreCase("JIS")) {
                this.httpHeaders.set(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.toString());
            } else if (requestEncode.equalsIgnoreCase("Unicode")) {
                this.httpHeaders.set(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.toString());
            }
            // 设置请求头
            Map<String, String> httpHeaders = restfulConnectionDTO.getHttpHeaders();
            if (null != httpHeaders && httpHeaders.size() != 0) {
                httpHeaders.forEach((k, v) -> this.httpHeaders.add(k, v));
            }
            // 设置Form数据
            Map<String, String> formData = restfulConnectionDTO.getFormData();
            if (null != formData && formData.size() != 0) {
                formData.forEach((k, v) -> this.data.add(k, v));
            }
        }
        // 发送请求
        if (!this.data.isEmpty()) {
            // FormData 请求
            this.entity = new HttpEntity<>(this.data, this.httpHeaders);
        } else if (this.entity == null && this.requestBody != null) {
            // requestBody 请求
            this.entity = new HttpEntity<>(this.requestBody, this.httpHeaders);
        } else {
            // 无body 请求
            this.entity = new HttpEntity<>(null, this.httpHeaders);
        }
        return template.exchange(url, this.method, this.entity, responseType, variables);
    }

    @Override
    public String getType() {
        return "RESTful";
    }
}
