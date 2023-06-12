package com.egrand.sweetapi.core.utils;

public class Constants {

	/**
	 * 空值
	 */
	public static final String EMPTY = "";

	/**
	 * 脚本中Context的变量名
	 */
	public static final String VAR_NAME_MODULE_CONTEXT = "context";

	/**
	 * 表达式验证
	 */
	public static final String VALIDATE_TYPE_EXPRESSION = "expression";

	/**
	 * 正则验证
	 */
	public static final String VALIDATE_TYPE_PATTERN = "pattern";

	/**
	 * 表达式验证中变量的默认名称
	 */
	public static final String EXPRESSION_DEFAULT_VAR_NAME = "value";

	/**
	 * 脚本中session的变量名
	 */
	public static final String VAR_NAME_SESSION = "session";

	/**
	 * 脚本中cookie的变量名
	 */
	public static final String VAR_NAME_COOKIE = "cookie";

	/**
	 * 脚本中路径变量的变量名
	 */
	public static final String VAR_NAME_PATH_VARIABLE = "path";

	/**
	 * 脚本中header的变量名
	 */
	public static final String VAR_NAME_HEADER = "header";
	/**

	/**
	 * 脚本中RequestBody的变量名
	 */
	public static final String VAR_NAME_REQUEST_BODY = "body";

	 /**
	 * 脚本中Http的变量名
	 */
	public static final String VAR_NAME_MODULE_HTTP = "http";

	/**
	 * 脚本中DB的变量名
	 */
	public static final String VAR_NAME_MODULE_DB = "db";

	/**
	 * 脚本中Request的变量名
	 */
	public static final String VAR_NAME_MODULE_REQUEST = "request";

	/**
	 * 脚本中Response的变量名
	 */
	public static final String VAR_NAME_MODULE_RESPONSE = "response";

	/**
	 * 脚本中Word的变量名
	 */
	public static final String VAR_NAME_MODULE_WORD = "word";

	/**
	 * 脚本中日志的变量名
	 */
	public static final String VAR_NAME_MODULE_LOG = "log";

	/**
	 * 脚本中插件的变量名
	 */
	public static final String VAR_NAME_MODULE_PLUGIN = "plugin";

	/**
	 * 脚本中环境变量的变量名
	 */
	public static final String VAR_NAME_MODULE_ENV = "env";

	/**
	 * 脚本中Sweet的变量名
	 */
	public static final String VAR_NAME_MODULE_SWEET = "sweet";

	public static final String HEADER_REQUEST_CLIENT_ID = "Sweet-Request-Client-Id";

	public static final String UPLOAD_MODE_FULL = "full";

	/**
	 * 执行成功的code值
	 */
	public static int RESPONSE_CODE_SUCCESS = 0;

	/**
	 * 执行成功的message值
	 */
	public static final String RESPONSE_MESSAGE_SUCCESS = "success";

	/**
	 * 执行出现异常的code值
	 */
	public static int RESPONSE_CODE_EXCEPTION = -1;

	/**
	 * 参数验证未通过的code值
	 */
	public static int RESPONSE_CODE_INVALID = -404;


}
