package com.egrand.sweetapi.modules.db.dialect;

import com.egrand.sweetapi.modules.db.BoundSql;

/**
 * Oracle方言
 *
 */
public class OracleDialect implements Dialect {

	@Override
	public boolean match(String jdbcUrl) {
		return jdbcUrl.contains(":oracle:");
	}

	@Override
	public String getPageSql(String sql, BoundSql boundSql, long offset, long limit) {
		limit = (offset >= 1) ? (offset + limit) : limit;
		boundSql.addParameter(limit);
		boundSql.addParameter(offset);
		return "SELECT * FROM ( SELECT TMP.*, ROWNUM ROW_ID FROM ( \n" +
				sql + "\n ) TMP WHERE ROWNUM <= ? ) WHERE ROW_ID > ?";
	}
}
