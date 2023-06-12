package com.egrand.sweetapi.modules.db.inteceptor;

import com.egrand.sweetapi.core.context.RequestEntity;
import com.egrand.sweetapi.modules.db.BoundSql;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 默认打印SQL实现
 *
 */
@Slf4j
public class DefaultSqlInterceptor implements SQLInterceptor {

	@Override
	public void preHandle(BoundSql boundSql, RequestEntity requestEntity) {
		String parameters = Arrays.stream(boundSql.getParameters()).map(it -> {
			if (it == null) {
				return "null";
			}
			return it + "(" + it.getClass().getSimpleName() + ")";
		}).collect(Collectors.joining(", "));
		String dataSourceName = boundSql.getDbModule().getDataSourceName();
		log.info("执行SQL：{}", boundSql.getSql().trim());
		if (dataSourceName != null) {
			log.info("数据源：{}", dataSourceName);
		}
		if (parameters.length() > 0) {
			log.info("SQL参数：{}", parameters);
		}
	}
}
