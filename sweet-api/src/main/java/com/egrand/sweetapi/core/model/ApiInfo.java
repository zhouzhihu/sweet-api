package com.egrand.sweetapi.core.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 接口信息
 */
public class ApiInfo extends PathEntity {

	/**
	 * 请求方法
	 */
	private String method = "GET";

	/**
	 * 设置的请求参数
	 */
	private List<Parameter> parameters = Collections.emptyList();

	/**
	 * 请求头
	 */
	private List<Header> headers = Collections.emptyList();

	/**
	 * 路径变量
	 */
	private List<Path> paths = Collections.emptyList();

	/**
	 * 请求体属性
	 */
	private List<BaseDefinition> requestBodyDefinition;

	/**
	 * 输出结果属性
	 */
	private List<BaseDefinition> responseBodyDefinition;

	/**
	 * 请求体
	 */
	private String requestBody;

	/**
	 * 输出结果
	 */
	private String responseBody;

	/**
	 * 接口描述
	 */
	private String description;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public List<Header> getHeaders() {
		return headers;
	}

	public void setHeaders(List<Header> headers) {
		this.headers = headers;
	}

	public List<Path> getPaths() {
		return paths;
	}

	public void setPaths(List<Path> paths) {
		this.paths = paths;
	}

	public List<BaseDefinition> getRequestBodyDefinition() {
		return requestBodyDefinition;
	}

	public void setRequestBodyDefinition(List<BaseDefinition> requestBodyDefinition) {
		this.requestBodyDefinition = requestBodyDefinition;
	}

	public List<BaseDefinition> getResponseBodyDefinition() {
		return responseBodyDefinition;
	}

	public void setResponseBodyDefinition(List<BaseDefinition> responseBodyDefinition) {
		this.responseBodyDefinition = responseBodyDefinition;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ApiInfo simple() {
		ApiInfo target = new ApiInfo();
		super.simple(target);
		target.setMethod(this.getMethod());
		return target;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ApiInfo apiInfo = (ApiInfo) o;
		return Objects.equals(id, apiInfo.id) &&
				Objects.equals(method, apiInfo.method) &&
				Objects.equals(path, apiInfo.path) &&
				Objects.equals(script, apiInfo.script) &&
				Objects.equals(name, apiInfo.name) &&
				Objects.equals(paths, apiInfo.paths) &&
				Objects.equals(groupId, apiInfo.groupId) &&
				Objects.equals(parameters, apiInfo.parameters) &&
				Objects.equals(requestBody, apiInfo.requestBody) &&
				Objects.equals(headers, apiInfo.headers) &&
				Objects.equals(description, apiInfo.description) &&
				Objects.equals(requestBodyDefinition, apiInfo.requestBodyDefinition) &&
				Objects.equals(responseBodyDefinition, apiInfo.responseBodyDefinition);
	}


	@Override
	public int hashCode() {
		return Objects.hash(id, method, path, script, name, groupId, parameters, requestBody, headers, description, requestBodyDefinition, responseBodyDefinition);
	}

	@Override
	public ApiInfo copy() {
		ApiInfo info = new ApiInfo();
		copyTo(info);
		info.setMethod(this.method);
		info.setParameters(this.parameters);
		info.setRequestBody(this.requestBody);
		info.setHeaders(this.headers);
		info.setResponseBody(this.responseBody);
		info.setDescription(this.description);
		info.setPaths(this.paths);
		info.setRequestBodyDefinition(this.requestBodyDefinition);
		info.setResponseBodyDefinition(this.responseBodyDefinition);
		return info;
	}
}
