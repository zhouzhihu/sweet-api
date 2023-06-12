package com.egrand.sweetapi.modules.http;

import com.egrand.sweetapi.core.ModuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.egrand.sweetapi.core.utils.Constants.VAR_NAME_MODULE_HTTP;

/**
 * http 模块
 *
 */
public class HttpModule implements ModuleService {

    private final RestTemplate template;
    private final HttpHeaders httpHeaders = new HttpHeaders();
    private Class<?> responseType = Object.class;
    private final MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
    private final MultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
    private final Map<String, ?> variables = new HashMap<>();
    private String url;
    private HttpMethod method = HttpMethod.GET;
    private HttpEntity<Object> entity = null;
    private Object requestBody;

    public HttpModule() {
        this.template = this.createRestTemplate();
    }

    private RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter(StandardCharsets.UTF_8) {
            {
                setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
            }

            @Override
            public boolean supports(Class<?> clazz) {
                return true;
            }
        });
        return restTemplate;
    }

    public HttpModule(RestTemplate template) {
        this.template = template;
    }

    public HttpModule(RestTemplate template, String url) {
        this.template = template;
        this.url = url;
    }

    /**
     * 创建连接
     * @param url 目标URL
     * @return
     */
    public HttpModule connect(String url) {
        return new HttpModule(template, url);
    }

    /**
     * 设置URL参数
     * @param key 参数名
     * @param values 参数值
     * @return
     */
    public HttpModule param(String key, Object... values) {
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
    public HttpModule param(Map<String, Object> values) {
        values.forEach((key, value) -> param(key, Objects.toString(value, "")));
        return this;
    }

    /**
     * 设置form参数
     * @param key 参数名
     * @param values 参数值
     * @return
     */
    public HttpModule data(String key, Object... values) {
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
    public HttpModule data(Map<String, Object> values) {
        values.forEach((key, value) -> data(key, Objects.toString(value, "")));
        return this;
    }

    /**
     * 设置header
     * @param key header名
     * @param value header值
     * @return
     */
    public HttpModule header(String key, String value) {
        httpHeaders.add(key, value);
        return this;
    }

    /**
     * 批量设置header
     * @param values header值
     * @return
     */
    public HttpModule header(Map<String, Object> values) {
        values.entrySet()
                .stream()
                .filter(it -> it.getValue() != null)
                .forEach(entry -> header(entry.getKey(), entry.getValue().toString()));
        return this;
    }

    /**
     * 设置请求方法，默认GET
     * @param method 请求方法
     * @return
     */
    public HttpModule method(HttpMethod method) {
        this.method = method;
        return this;
    }

    /**
     * 设置`RequestBody`
     * @param requestBody RequestBody
     * @return
     */
    public HttpModule body(Object requestBody) {
        this.requestBody = requestBody;
        this.contentType(MediaType.APPLICATION_JSON);
        return this;
    }

    /**
     * 自定义`HttpEntity`
     * @param entity HttpEntity
     * @return
     */
    public HttpModule entity(HttpEntity<Object> entity) {
        this.entity = entity;
        return this;
    }

    /**
     * 设置`ContentType`
     * @param contentType Content-Type值
     * @return
     */
    public HttpModule contentType(String contentType) {
        return contentType(MediaType.parseMediaType(contentType));
    }

    /**
     * 设置`ContentType`
     * @param mediaType Content-Type值
     * @return
     */
    public HttpModule contentType(MediaType mediaType) {
        this.httpHeaders.setContentType(mediaType);
        return this;
    }

    /**
     * 设置返回值为`byte[]`
     * @return
     */
    public HttpModule expectBytes() {
        this.responseType = byte[].class;
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
        if (!this.data.isEmpty()) {
            this.entity = new HttpEntity<>(this.data, this.httpHeaders);
        } else if (this.entity == null && this.requestBody != null) {
            this.entity = new HttpEntity<>(this.requestBody, this.httpHeaders);
        } else {
            this.entity = new HttpEntity<>(null, this.httpHeaders);
        }
        return template.exchange(url, this.method, entity, responseType, variables);
    }

    @Override
    public String getType() {
        return VAR_NAME_MODULE_HTTP;
    }
}
