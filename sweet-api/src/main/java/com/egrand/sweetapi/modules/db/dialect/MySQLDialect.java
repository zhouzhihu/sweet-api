package com.egrand.sweetapi.modules.db.dialect;

import com.egrand.sweetapi.modules.db.BoundSql;

/**
 * mysql 方言
 *
 */
public class MySQLDialect implements Dialect {

	@Override
	public boolean match(String jdbcUrl) {
		return jdbcUrl.contains(":mysql:") || jdbcUrl.contains(":mariadb:") || jdbcUrl.contains(":cobar:");
	}

	@Override
	public String getPageSql(String sql, BoundSql boundSql, long offset, long limit) {
		boundSql.addParameter(offset);
		boundSql.addParameter(limit);
		return sql + "\n limit ?,?";
	}
}
