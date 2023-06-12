package com.egrand.sweetapi.core.exception;

/**
 * api异常对象
 *
 */
public class APIException extends RuntimeException {

	public APIException(String message) {
		super(message);
	}

	public APIException(String message, Throwable cause) {
		super(message, cause);
	}
}
