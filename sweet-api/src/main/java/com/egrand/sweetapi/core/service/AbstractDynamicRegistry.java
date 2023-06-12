package com.egrand.sweetapi.core.service;

import com.egrand.sweetapi.core.event.ApiEvent;
import com.egrand.sweetapi.core.event.EventAction;
import com.egrand.sweetapi.core.event.GroupEvent;
import com.egrand.sweetapi.core.exception.APIException;
import com.egrand.sweetapi.core.model.Entity;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 可缓存的动态注册
 * @param <T>
 */
public abstract class AbstractDynamicRegistry<T extends Entity> implements DynamicRegistry<T> {

	/**
	 * 已缓存的映射信息
	 */
	private final Map<String, MappingNode<T>> mappings = new ConcurrentHashMap<>();

	public AbstractDynamicRegistry() {
	}

	@Override
	public boolean register(T entity) {
		MappingNode<T> mappingNode = buildMappingNode(entity);
		String newMappingKey = mappingNode.getMappingKey();
		MappingNode<T> oldMappingNode = mappings.get(entity.getId());
		if (oldMappingNode != null) {
			String oldMappingKey = oldMappingNode.getMappingKey();
			// mappingKey一致时 刷新即可
			if (Objects.equals(oldMappingKey, newMappingKey)) {
				if (!entity.equals(oldMappingNode.getEntity())) {
					// 刷新
					oldMappingNode.setEntity(entity);
				}
				return true;
			}
			// 不一致时，需要取消注册旧的，重新注册当前的
			mappings.remove(oldMappingKey);
			mappings.remove(entity.getId());
			unregister(oldMappingNode);
		} else if (mappings.containsKey(newMappingKey)) {
			throw new APIException(newMappingKey + " 已注册，请更换名称或路径");
		}
		if (register(mappingNode)) {
			mappings.put(entity.getId(), mappingNode);
			mappings.put(newMappingKey, mappingNode);
			return true;
		}
		return false;
	}

	protected void processEvent(ApiEvent event) {
		T info = (T) event.getEntity();
		if (event.getAction() == EventAction.DELETE) {
			unregister(info);
		} else {
			register(info);
		}
	}

	protected void processEvent(GroupEvent event) {
		if (event.getAction() == EventAction.DELETE) {
			event.getEntities().forEach(entity -> unregister((T) entity));
		} else {
			event.getEntities().forEach(entity -> register((T) entity));
		}
	}

	@Override
	public T getMapping(String mappingKey) {
		MappingNode<T> node = mappings.get(mappingKey);
		return node == null ? null : node.getEntity();
	}

	@Override
	public boolean unregister(T entity) {
		MappingNode<T> mappingNode = mappings.remove(entity.getId());
		if (mappingNode != null) {
			mappings.remove(mappingNode.getMappingKey());
			unregister(mappingNode);
			return true;
		}
		return false;
	}

	protected boolean register(MappingNode<T> mappingNode) {
		return true;
	}

	protected void unregister(MappingNode<T> mappingNode) {

	}

	public List<T> mappings(){
		return this.mappings.values().stream().map(MappingNode::getEntity).collect(Collectors.toList());
	}

	protected MappingNode<T> buildMappingNode(T entity) {
		MappingNode<T> mappingNode = new MappingNode<>(entity);
		mappingNode.setMappingKey(this.buildKey(entity));
		return mappingNode;
	}

	public abstract String buildKey(T entity);

	protected static class MappingNode<T extends Entity> {

		private T entity;

		private String mappingKey = "";

		private Object mappingData;

		public MappingNode(T entity) {
			this.entity = entity;
		}

		public T getEntity() {
			return entity;
		}

		public void setEntity(T entity) {
			this.entity = entity;
		}

		public String getMappingKey() {
			return mappingKey;
		}

		public void setMappingKey(String mappingKey) {
			this.mappingKey = mappingKey;
		}

		public Object getMappingData() {
			return mappingData;
		}

		public void setMappingData(Object mappingData) {
			this.mappingData = mappingData;
		}
	}
}
