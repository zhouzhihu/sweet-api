package com.egrand.sweetapi.core.interceptor;

import com.egrand.sweetapi.core.context.RequestEntity;

/**
 * 默认结果封装实现
 *
 */
public class DefaultResultProvider implements ResultProvider {

	private final String responseScript;

	public DefaultResultProvider() {
		this.responseScript = null;
	}

	public DefaultResultProvider(String responseScript) {
		this.responseScript = responseScript;
	}

	@Override
	public Object buildResult(RequestEntity requestEntity, int code, String message, Object data) {
		return new ResultBody<>(code, data, message);
	}
}
