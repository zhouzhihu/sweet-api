package com.egrand.sweetapi.modules.db.dialect;

import com.egrand.sweetapi.modules.db.BoundSql;

/**
 * DB2方言
 *
 */
public class DB2Dialect implements Dialect {
	@Override
	public boolean match(String jdbcUrl) {
		return jdbcUrl.contains(":db2:");
	}

	@Override
	public String getPageSql(String sql, BoundSql boundSql, long offset, long limit) {
		boundSql.addParameter(offset + 1);
		boundSql.addParameter(offset + limit);
		return "SELECT * FROM (SELECT TMP_PAGE.*,ROWNUMBER() OVER() AS ROW_ID FROM ( \n" + sql +
				"\n ) AS TMP_PAGE) TMP_PAGE WHERE ROW_ID BETWEEN ? AND ?";
	}
}
