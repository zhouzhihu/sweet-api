package com.egrand.sweetapi.core.config;

import com.egrand.sweetapi.core.exception.InvalidArgumentException;
import com.egrand.sweetapi.core.model.JsonCode;
import org.apache.commons.lang3.StringUtils;

/**
 * JsonCode常量
 */
public interface JsonCodeConstants {

	JsonCode PARAMETER_INVALID = new JsonCode(1028, "参数验证失败");

	JsonCode HEADER_INVALID = new JsonCode(1029, "header验证失败");

	JsonCode PATH_VARIABLE_INVALID = new JsonCode(1030, "路径变量验证失败");

	JsonCode BODY_INVALID = new JsonCode(1031, "body验证失败");

	JsonCode REQUEST_PATH_CONFLICT = new JsonCode(1016, "接口[%s(%s)]与应用冲突，无法注册");

	JsonCode API_NOT_FOUND = new JsonCode(1035, "找不到接口");

	default void notNull(Object value, JsonCode jsonCode) {
		if (value == null) {
			throw new InvalidArgumentException(jsonCode);
		}
	}

	default void isTrue(boolean value, JsonCode jsonCode) {
		if (!value) {
			throw new InvalidArgumentException(jsonCode);
		}
	}

	default void notBlank(String value, JsonCode jsonCode) {
		isTrue(StringUtils.isNotBlank(value), jsonCode);
	}
}
