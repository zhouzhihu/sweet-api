package com.egrand.sweetapi.core.model;

/**
 * 路径变量信息
 *
 */
public class Path extends BaseDefinition {

	public Path() {
	}

	public Path(String name, String value) {
		super(name, value);
	}

	@Override
	public boolean isRequired() {
		return true;
	}
}
