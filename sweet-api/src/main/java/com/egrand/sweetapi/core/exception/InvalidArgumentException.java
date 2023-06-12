package com.egrand.sweetapi.core.exception;


import com.egrand.sweetapi.core.model.JsonCode;

/**
 * 参数错误异常
 *
 */
public class InvalidArgumentException extends RuntimeException {

	private final transient JsonCode jsonCode;

	public InvalidArgumentException(JsonCode jsonCode) {
		super(jsonCode.getMessage());
		this.jsonCode = jsonCode;
	}

	public int getCode() {
		return jsonCode.getCode();
	}
}
