package com.egrand.sweetapi.core.service;

import com.egrand.sweetapi.core.model.Entity;

/**
 * 动态注册信息
 * @param <T>
 */
public interface DynamicRegistry<T extends Entity> {

	/**
	 * 注册
	 */
	boolean register(T entity);

	/**
	 * 取消注册
	 */
	boolean unregister(T entity);

	/**
	 * 获取实体
	 * @param mappingKey
	 * @return
	 */
	T getMapping(String mappingKey);

}
