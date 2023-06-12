package com.egrand.sweetapi.modules.servlet;

import cn.hutool.core.io.FileUtil;
import com.egrand.sweetapi.core.context.RequestContext;
import com.egrand.sweetapi.core.interceptor.ResultProvider;
import com.egrand.sweetapi.core.utils.ObjectConvertExtension;
import com.egrand.sweetapi.core.ModuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.egrand.sweetapi.core.utils.Constants.VAR_NAME_MODULE_RESPONSE;

/**
 * response模块
 */
public class ResponseModule implements ModuleService {

	private final ResultProvider resultProvider;

	public ResponseModule(ResultProvider resultProvider) {
		this.resultProvider = resultProvider;
	}

	/**
	 * 文件下载
	 *
	 * @param value    文件内容
	 * @param filename 文件名
	 */
	public ResponseEntity<?> download(Object value, String filename) throws UnsupportedEncodingException {
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"))
				.body(value);
	}

	/**
	 * 文件下载
	 *
	 * @param filePath 文件路径
	 * @param filename 文件名
	 */
	public ResponseEntity<?> downloadLocalFile(String filePath, String filename) throws IOException {
		InputStream is = FileUtil.getInputStream(filePath);
		//创建字节数组
		byte[] buffer = new byte[is.available()];
		//将流读到字节数组中
		is.read(buffer);
		is.close();
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("filename", URLEncoder.encode(filename, "UTF-8"))
                .header(HttpHeaders.LAST_MODIFIED, new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss Z", Locale.ENGLISH).format(new Date()) + " GMT")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization,filename")
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"))
				.body(buffer);
	}

	/**
	 * 文件下载
	 *
	 * @param value    文件内容
	 * @param httpHeaders 自定义头
	 */
	public ResponseEntity<?> download(Object value, HttpHeaders httpHeaders) throws UnsupportedEncodingException {
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
				.headers(httpHeaders)
				.body(value);
	}

	/**
	 * 自行构建分页结果
	 *
	 * @param total  条数
	 * @param values 数据内容
	 */
	public Object page(long total, List<Map<String, Object>> values) {
		return resultProvider.buildPageResult(RequestContext.getRequestEntity(), null, total, values);
	}

	/**
	 * 自定义json结果
	 *
	 * @param value json内容
	 */
	public ResponseEntity<Object> json(Object value) {
		return ResponseEntity.ok(value);
	}

	/**
	 * 添加Header
	 * @param key header名
	 * @param value header值
	 * @return
	 */
	public ResponseModule addHeader(String key, String value) {
		if (StringUtils.isNotBlank(key)) {
			HttpServletResponse response = getResponse();
			if (response != null) {
				response.addHeader(key, value);
			}
		}
		return this;
	}

	/**
	 * 设置header
	 * @param key header名
	 * @param value header值
	 * @return
	 */
	public ResponseModule setHeader(String key, String value) {
		if (StringUtils.isNotBlank(key)) {
			HttpServletResponse response = getResponse();
			if (response != null) {
				response.setHeader(key, value);
			}
		}
		return this;
	}

	/**
	 * 添加cookie
	 * @param name cookie名
	 * @param value cookie值
	 * @return
	 */
	public ResponseModule addCookie(String name, String value) {
		if (StringUtils.isNotBlank(name)) {
			addCookie(new Cookie(name, value));
		}
		return this;
	}

	/**
	 * 批量添加cookie
	 * @param cookies Cookies
	 * @param options Cookie选项，如`path`、`httpOnly`、`domain`、`maxAge`"
	 * @return
	 */
	public ResponseModule addCookies(Map<String, String> cookies, Map<String, Object> options) {
		if (cookies != null) {
			for (Map.Entry<String, String> entry : cookies.entrySet()) {
				addCookie(entry.getKey(), entry.getValue(), options);
			}
		}
		return this;
	}

	/**
	 * 批量添加cookie
	 * @param cookies Cookies
	 * @return
	 */
	public ResponseModule addCookies(Map<String, String> cookies) {
		return addCookies(cookies, null);

	}

	/**
	 * 获取OutputStream
	 *
	 * @since 1.2.3
	 */
	public OutputStream getOutputStream() throws IOException {
		HttpServletResponse response = getResponse();
		return response.getOutputStream();
	}

	/**
	 * 添加cookie
	 * @param name Cookie名
	 * @param value Cookie值
	 * @param options Cookie选项，如`path`、`httpOnly`、`domain`、`maxAge`"
	 * @return
	 */
	public ResponseModule addCookie(String name, String value, Map<String, Object> options) {
		if (StringUtils.isNotBlank(name)) {
			Cookie cookie = new Cookie(name, value);
			if (options != null) {
				Object path = options.get("path");
				if (path != null) {
					cookie.setPath(path.toString());
				}
				Object httpOnly = options.get("httpOnly");
				if (httpOnly != null) {
					cookie.setHttpOnly("true".equalsIgnoreCase(httpOnly.toString()));
				}
				Object domain = options.get("domain");
				if (domain != null) {
					cookie.setDomain(domain.toString());
				}
				Object maxAge = options.get("maxAge");
				int age;
				if (maxAge != null && (age = ObjectConvertExtension.asInt(maxAge, Integer.MIN_VALUE)) != Integer.MIN_VALUE) {
					cookie.setMaxAge(age);
				}
			}
			addCookie(cookie);
		}
		return this;
	}

	/**
	 * 终止输出，执行此方法后不会对结果进行任何输出及处理
	 * @return
	 */
	public NullValue end() {
		return NullValue.INSTANCE;
	}

	/**
	 * 添加cookie
	 * @param cookie Cookie对象
	 * @return
	 */
	public ResponseModule addCookie(Cookie cookie) {
		if (cookie != null) {
			HttpServletResponse response = getResponse();
			if (response != null) {
				response.addCookie(cookie);
			}
		}
		return this;
	}

	private HttpServletResponse getResponse() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes instanceof ServletRequestAttributes) {
			return ((ServletRequestAttributes) requestAttributes).getResponse();
		}
		return RequestContext.getHttpServletResponse();
	}

	/**
	 * 展示图片
	 *
	 * @param value 图片内容
	 * @param mime  图片类型，image/png,image/jpeg,image/gif
	 */
	public ResponseEntity image(Object value, String mime) {
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, mime).body(value);
	}

	/**
	 * 输出文本
	 *
	 * @param text 文本内容
	 */
	public ResponseEntity text(String text) {
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE).body(text);
	}

	/**
	 * 重定向
	 *
	 * @param url 目标网址
	 */
	public NullValue redirect(String url) throws IOException {
		getResponse().sendRedirect(url);
		return NullValue.INSTANCE;
	}

	@Override
	public String getType() {
		return VAR_NAME_MODULE_RESPONSE;
	}

	public static class NullValue {
		static final NullValue INSTANCE = new NullValue();
	}
}
