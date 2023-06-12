package com.egrand.sweetapi.core.config;

/**
 * CRUD 配置
 *
 */
public class Crud {
	/**
	 * 逻辑删除列
	 */
	private String logicDeleteColumn = "is_deleted";
	/**
	 * 逻辑删除值
	 */
	private String logicDeleteValue = "1";

	public String getLogicDeleteColumn() {
		return logicDeleteColumn;
	}

	public void setLogicDeleteColumn(String logicDeleteColumn) {
		this.logicDeleteColumn = logicDeleteColumn;
	}

	public String getLogicDeleteValue() {
		return logicDeleteValue;
	}

	public void setLogicDeleteValue(String logicDeleteValue) {
		this.logicDeleteValue = logicDeleteValue;
	}

}
