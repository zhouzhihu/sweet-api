package com.egrand.sweetapi.modules.servlet;

import com.egrand.sweetapi.core.context.RequestContext;
import com.egrand.sweetapi.core.script.ExitValue;
import com.egrand.sweetapi.core.utils.IpUtils;
import com.egrand.sweetapi.core.ModuleService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.MultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import static com.egrand.sweetapi.core.utils.Constants.VAR_NAME_MODULE_REQUEST;

/**
 * request 模块
 *
 */
public class RequestModule implements ModuleService {

	private static MultipartResolver resolver;

	private static final String[] DEFAULT_IP_HEADER = new String[]{"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

	public RequestModule(MultipartResolver resolver) {
		RequestModule.resolver = resolver;
	}

	/**
	 * 获取文件信息
	 *
	 * @param name 参数名
	 */
	public MultipartFile getFile(String name) {
		MultipartRequest request = getMultipartHttpServletRequest();
		if (request == null) {
			return null;
		}
		MultipartFile file = request.getFile(name);
		return file == null || file.isEmpty() ? null : file;
	}

	/**
	 * 获取文件信息
	 *
	 * @param name 参数名
	 */
	public List<MultipartFile> getFiles(String name) {
		MultipartRequest request = getMultipartHttpServletRequest();
		if (request == null) {
			return null;
		}
		return request.getFiles(name).stream().filter(it -> !it.isEmpty()).collect(Collectors.toList());
	}

	/**
	 * 获取原生HttpServletRequest对象
	 */
	public static HttpServletRequest get() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes instanceof ServletRequestAttributes) {
			return ((ServletRequestAttributes) requestAttributes).getRequest();
		}
		return RequestContext.getHttpServletRequest();
	}

	private static MultipartRequest getMultipartHttpServletRequest() {
		HttpServletRequest request = get();
		if (request != null && resolver.isMultipart(request)) {
			return resolver.resolveMultipart(request);
		}
		return null;
	}

	/**
	 * 根据参数名获取参数值集合
	 *
	 * @param name 参数名
	 */
	public List<String> getValues(String name) {
		HttpServletRequest request = get();
		if (request != null) {
			String[] values = request.getParameterValues(name);
			return values == null ? null : Arrays.asList(values);
		}
		return null;
	}

	/**
	 * 根据header名获取header集合
	 *
	 * @param name 参数名
	 */
	public List<String> getHeaders(String name) {
		HttpServletRequest request = get();
		if (request != null) {
			Enumeration<String> headers = request.getHeaders(name);
			return headers == null ? null : Collections.list(headers);
		}
		return null;
	}

	/**
	 * 获取客户端IP
	 * @param otherHeaderNames
	 * @return
	 */
	public String getClientIP(String... otherHeaderNames) {
		HttpServletRequest request = get();
		if (request == null) {
			return null;
		}
		return IpUtils.getRealIP(request.getRemoteAddr(), request::getHeader, otherHeaderNames);
	}

	/**
	 * 退出请求
	 * @param values
	 * @return
	 */
	public ExitValue exit(String ...values) {
		return new ExitValue(values);
	}

	@Override
	public String getType() {
		return VAR_NAME_MODULE_REQUEST;
	}
}
