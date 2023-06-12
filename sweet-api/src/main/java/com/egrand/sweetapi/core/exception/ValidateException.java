package com.egrand.sweetapi.core.exception;

import com.egrand.sweetapi.core.model.JsonCode;

/**
 * 接口验证异常
 *
 */
public class ValidateException extends RuntimeException {

	private final JsonCode jsonCode;

	public ValidateException(JsonCode jsonCode, String message) {
		super(message);
		this.jsonCode = jsonCode;
	}

	public JsonCode getJsonCode() {
		return jsonCode;
	}
}
