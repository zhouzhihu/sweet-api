package com.egrand.sweetapi.modules.db.dialect;

/**
 * ClickHouse方言
 *
 */
public class ClickhouseDialect extends MySQLDialect {

	@Override
	public boolean match(String jdbcUrl) {
		return jdbcUrl.contains(":clickhouse:");
	}
}
