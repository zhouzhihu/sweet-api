package com.egrand.sweetapi.modules.spring;

import com.egrand.sweetapi.core.ModuleService;
import org.springframework.core.env.Environment;

import static com.egrand.sweetapi.core.utils.Constants.VAR_NAME_MODULE_ENV;

/**
 * env模块
 *
 */
public class EnvModule implements ModuleService {

	private final Environment environment;

	public EnvModule(Environment environment) {
		this.environment = environment;
	}

	/**
	 * 获取配置
	 * @param key 配置项
	 * @return
	 */
	public String get(String key) {
		return environment.getProperty(key);
	}

	/**
	 * 获取配置
	 * @param key 配置项
	 * @param defaultValue 未配置时的默认值
	 * @return
	 */
	public String get(String key, String defaultValue) {
		return environment.getProperty(key, defaultValue);
	}

	@Override
	public String getType() {
		return VAR_NAME_MODULE_ENV;
	}
}
