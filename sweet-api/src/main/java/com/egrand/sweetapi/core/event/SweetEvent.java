package com.egrand.sweetapi.core.event;

public class SweetEvent {

	/**
	 * 消息类型
	 */
	private final String type;

	/**
	 * 消息动作
	 */
	private final EventAction action;

	/**
	 * 消息来源
	 */
	private String source;

	public SweetEvent(String type, EventAction action, String source) {
		this.type = type;
		this.action = action;
		this.source = source;
	}

	public SweetEvent(String type, EventAction action) {
		this(type, action, null);
	}

	public String getType() {
		return type;
	}

	public EventAction getAction() {
		return action;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}
