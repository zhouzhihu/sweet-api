package com.egrand.sweetapi.core.interceptor;

import com.egrand.sweetapi.core.context.RequestEntity;
import com.egrand.sweetapi.core.model.Page;
import com.egrand.sweetapi.core.model.PageResult;
import com.egrand.sweetapi.core.script.ExitValue;
import com.egrand.sweetapi.core.utils.ObjectConvertExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.egrand.sweetapi.core.utils.Constants.*;

/**
 * 结果构建接口
 *
 */
public interface ResultProvider {

	Logger logger = LoggerFactory.getLogger(ResultProvider.class);

	/**
	 * 根据异常内容构建结果
	 *
	 * @param requestEntity 请求信息
	 * @param root          异常对象
	 */
	default Object buildResult(RequestEntity requestEntity, Throwable root) {
		return buildException(requestEntity, root);
	}

	/**
	 * 构建JSON返回结果，code和message 默认为 1 success
	 *
	 * @param requestEntity 请求相关信息
	 * @param data          返回内容
	 */
	default Object buildResult(RequestEntity requestEntity, Object data) {
		if (data instanceof ExitValue) {
			ExitValue exitValue = (ExitValue) data;
			Object[] values = exitValue.getValues();
			int code = values.length > 0 ? ObjectConvertExtension.asInt(values[0], RESPONSE_CODE_SUCCESS) : RESPONSE_CODE_SUCCESS;
			String message = values.length > 1 ? Objects.toString(values[1], RESPONSE_MESSAGE_SUCCESS) : RESPONSE_MESSAGE_SUCCESS;
			return buildResult(requestEntity, code, message, values.length > 2 ? values[2] : null);
		}
		return buildResult(requestEntity, RESPONSE_CODE_SUCCESS, RESPONSE_MESSAGE_SUCCESS, data);
	}

	/**
	 * 构建JSON返回结果
	 *
	 * @param requestEntity 请求相关信息
	 * @param code          状态码
	 * @param message       状态说明
	 */
	default Object buildResult(RequestEntity requestEntity, int code, String message) {
		return buildResult(requestEntity, code, message, null);
	}

	/**
	 * 构建异常返回结果
	 *
	 * @param requestEntity 请求相关信息
	 * @param throwable     异常信息
	 * @since 1.2.2
	 */
	default Object buildException(RequestEntity requestEntity, Throwable throwable) {
		return buildResult(requestEntity, RESPONSE_CODE_EXCEPTION, "系统内部出现错误");
	}

	/**
	 * 构建JSON返回结果
	 *
	 * @param requestEntity 请求相关信息
	 * @param code          状态码
	 * @param message       状态说明
	 * @param data          数据内容，可以通过data的类型判断是否是分页结果进行区分普通结果集和分页结果集
	 */
	Object buildResult(RequestEntity requestEntity, int code, String message, Object data);

	/**
	 * @param requestEntity 请求相关信息
	 * @param page          分页对象
	 * @param total         总数
	 * @param data          数据内容
	 */
	default Object buildPageResult(RequestEntity requestEntity, Page page, long total, List<Map<String, Object>> data) {
		return new PageResult<>(requestEntity, page, total, data);
	}

}
