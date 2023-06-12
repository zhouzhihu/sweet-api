package com.egrand.sweetapi.core.model;

public class PathEntity extends Entity {

	/**
	 * 路径
	 */
	protected String path;

	/**
	 * 原始路径
	 */
	private String originalPath;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getOriginalPath() {
		return originalPath;
	}

	public void setOriginalPath(String originalPath) {
		this.originalPath = originalPath;
	}

	protected void copyTo(PathEntity entity) {
		super.copyTo(entity);
		entity.setPath(this.path);
	}

	protected void simple(PathEntity entity) {
		super.simple(entity);
		entity.setPath(this.path);
	}
}
