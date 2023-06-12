package com.egrand.sweetapi.modules.db;

import com.egrand.sweetapi.modules.db.parse.TextSqlNode;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * SQL参数处理
 *
 */
public class BoundSql {

	private static final Pattern REPLACE_MULTI_WHITE_LINE = Pattern.compile("(\r?\n(\\s*\r?\n)+)");

	private String sql;

	private List<Object> parameters = new ArrayList<>();

	private Set<String> excludeColumns;

	private DbModule dbModule;

	private Map<String, Object> bindParameters;

	public BoundSql(String sql, List<Object> parameters, DbModule dbModule) {
		this.sql = sql;
		this.parameters = parameters;
		this.dbModule = dbModule;
	}

	public BoundSql(String sql, Map<String, Object> parameters, DbModule dbModule) {
		this.sql = sql;
		this.bindParameters = parameters;
		this.dbModule = dbModule;
		this.init();
	}

	private BoundSql(String sql) {
		this.sql = sql;
		this.init();
	}

	BoundSql(String sql, DbModule dbModule) {
		this(sql);
		this.dbModule = dbModule;
	}

	private BoundSql() {

	}

	private void init() {
		Map<String, Object> varMap = new HashMap<>();
		if (null != this.bindParameters)
			varMap.putAll(this.bindParameters);
		normal(varMap);
	}

	private void normal(Map<String, Object> varMap) {
		this.sql = TextSqlNode.parseSql(this.sql, varMap, parameters);
		this.sql = this.sql == null ? null : REPLACE_MULTI_WHITE_LINE.matcher(this.sql.trim()).replaceAll("\r\n");
	}

	public DbModule getDbModule() {
		return dbModule;
	}

	BoundSql copy(String newSql) {
		BoundSql boundSql = new BoundSql();
		boundSql.parameters = this.parameters;
		boundSql.bindParameters = this.bindParameters;
		boundSql.sql = newSql;
		boundSql.excludeColumns = this.excludeColumns;
		boundSql.dbModule = this.dbModule;
		return boundSql;
	}

	public Set<String> getExcludeColumns() {
		return excludeColumns;
	}

	public void setExcludeColumns(Set<String> excludeColumns) {
		this.excludeColumns = excludeColumns;
	}

	/**
	 * 添加SQL参数
	 */
	public void addParameter(Object value) {
		parameters.add(value);
	}

	/**
	 * 获取要执行的SQL
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * 设置要执行的SQL
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

	/**
	 * 获取要执行的参数
	 */
	public Object[] getParameters() {
		return parameters.toArray();
	}

	/**
	 * 设置要执行的参数
	 */
	public void setParameters(List<Object> parameters) {
		this.parameters = parameters;
	}

	/**
	 * 获取缓存值
	 */
	@SuppressWarnings("unchecked")
	<T> T execute(Supplier<T> supplier) {
		Object result = supplier.get();
		return (T) result;
	}
}
