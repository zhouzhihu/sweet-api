package com.egrand.sweetapi.modules.db.provider;

/**
 * 全小写命名
 *
 */
public class LowerColumnMapperProvider implements ColumnMapperProvider {

	@Override
	public String name() {
		return "lower";
	}

	@Override
	public String mapping(String columnName) {
		return columnName == null ? null : columnName.toLowerCase();
	}
}
