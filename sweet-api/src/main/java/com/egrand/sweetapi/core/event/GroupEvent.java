package com.egrand.sweetapi.core.event;

import com.egrand.sweetapi.core.model.ApiInfo;

import java.util.Collections;
import java.util.List;

public class GroupEvent extends SweetEvent {

	/**
	 * 分组信息
	 */
	private ApiInfo group;

	/**
	 * API信息
	 */
	private List<ApiInfo> entities;

	public GroupEvent(String type, EventAction action, ApiInfo group) {
		this(type, action, group, Collections.emptyList());
	}

	public GroupEvent(String type, EventAction action, ApiInfo group, List<ApiInfo> entities) {
		super(type, action);
		this.group = group;
		this.entities = entities;
	}


	public ApiInfo getGroup() {
		return group;
	}

	public void setGroup(ApiInfo group) {
		this.group = group;
	}

	public List<ApiInfo> getEntities() {
		return entities;
	}

	public void setEntities(List<ApiInfo> entities) {
		this.entities = entities;
	}
}
