package com.egrand.sweetapi.core.event;

import com.egrand.sweetapi.core.model.ApiInfo;

public class ApiEvent extends SweetEvent {

	private ApiInfo entity;

	public ApiEvent(String type, EventAction action, ApiInfo entity) {
		super(type, action);
		this.entity = entity;
	}

	public ApiEvent(String type, EventAction action, ApiInfo entity, String source) {
		super(type, action, source);
		this.entity = entity;
	}

	public ApiInfo getEntity() {
		return entity;
	}
}
