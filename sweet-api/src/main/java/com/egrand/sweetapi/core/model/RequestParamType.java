package com.egrand.sweetapi.core.model;

/**
 * 接口选项信息
 *
 */
public enum RequestParamType {

	/**
	 * 请求参数
	 */
	TYPE_PARAMETER("请求参数", "parameter"),

	/**
	 * URL路径参数
	 */
	TYPE_PATH("URL路径参数", "path"),

	/**
	 * 允许拥有该权限的访问
	 */
	TYPE_HEADER("请求头参数", "header"),

	/**
	 * 请求体属性
	 */
	TYPE_REQUEST_BODY("请求体属性", "requestBody"),

	/**
	 * 输出结果属性
	 */
	TYPE_RESPONSE_BODY("输出结果属性", "responseBody");

	private final String name;
	private final String value;

	RequestParamType(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
}
