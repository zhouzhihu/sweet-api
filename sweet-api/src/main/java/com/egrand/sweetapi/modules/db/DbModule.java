package com.egrand.sweetapi.modules.db;

import cn.hutool.core.util.StrUtil;
import com.egrand.sweetapi.core.ModuleService;
import com.egrand.sweetapi.core.TenantService;
import com.egrand.sweetapi.core.context.RequestContext;
import com.egrand.sweetapi.core.context.RequestEntity;
import com.egrand.sweetapi.core.exception.APIException;
import com.egrand.sweetapi.core.interceptor.ResultProvider;
import com.egrand.sweetapi.core.model.Page;
import com.egrand.sweetapi.modules.db.dialect.Dialect;
import com.egrand.sweetapi.modules.db.dialect.DialectAdapter;
import com.egrand.sweetapi.modules.db.inteceptor.SQLInterceptor;
import com.egrand.sweetapi.modules.db.table.NamedTable;
import com.egrand.sweetapi.modules.db.table.SQLTable;
import com.egrand.sweetapi.starter.db.DynamicRoutingDataSource;
import com.egrand.sweetapi.starter.db.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.beans.Transient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.egrand.sweetapi.core.utils.Constants.VAR_NAME_MODULE_DB;

@Slf4j
public class DbModule implements ModuleService {

    private final DynamicRoutingDataSource dynamicRoutingDataSource;
    private final TenantService tenantService;
    protected JdbcTemplate jdbcTemplate;

    /**
     * 列名转换适配器
     */
    protected ColumnMapperAdapter columnMapperAdapter;

    private RowMapper<Map<String, Object>> columnMapRowMapper;

    private Function<String, String> rowMapColumnMapper;

    protected List<SQLInterceptor> sqlInterceptors;

    protected DialectAdapter dialectAdapter;

    /**
     * 结果提供者
     */
    private ResultProvider resultProvider;

    /**
     * 逻辑删除列名
     */
    private String logicDeleteColumn;

    /**
     * 逻辑删除列值
     */
    private String logicDeleteValue;

    public DbModule(JdbcTemplate jdbcTemplate, DynamicRoutingDataSource dynamicRoutingDataSource, TenantService tenantService) {
        this.jdbcTemplate = jdbcTemplate;
        this.dynamicRoutingDataSource = dynamicRoutingDataSource;
        this.tenantService = tenantService;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transient
    public void setResultProvider(ResultProvider resultProvider) {
        this.resultProvider = resultProvider;
    }

    @Transient
    public void setColumnMapRowMapper(RowMapper<Map<String, Object>> columnMapRowMapper) {
        this.columnMapRowMapper = columnMapRowMapper;
    }

    @Transient
    public void setColumnMapperProvider(ColumnMapperAdapter columnMapperAdapter) {
        this.columnMapperAdapter = columnMapperAdapter;
    }

    @Transient
    public void setRowMapColumnMapper(Function<String, String> rowMapColumnMapper) {
        this.rowMapColumnMapper = rowMapColumnMapper;
    }

    @Transient
    public void setSqlInterceptors(List<SQLInterceptor> sqlInterceptors) {
        this.sqlInterceptors = sqlInterceptors;
    }

    public void setDialectAdapter(DialectAdapter dialectAdapter) {
        this.dialectAdapter = dialectAdapter;
    }

    @Transient
    public String getLogicDeleteColumn() {
        return logicDeleteColumn;
    }

    @Transient
    public void setLogicDeleteColumn(String logicDeleteColumn) {
        this.logicDeleteColumn = logicDeleteColumn;
    }

    @Transient
    public String getLogicDeleteValue() {
        return logicDeleteValue;
    }

    @Transient
    public void setLogicDeleteValue(String logicDeleteValue) {
        this.logicDeleteValue = logicDeleteValue;
    }

    /**
     * 开启事务，在一个回调中进行操作
     *
     * @param function 回调函数，如：()=>{....}
     */
    public Object transaction(Function<DbModule, Object> function) {
        // 创建事务
        Transaction transaction = transaction();
        try {
            Object val = function.apply(this);
            transaction.commit();    //提交事务
            return val;
        } catch (Throwable throwable) {
            transaction.rollback();    //回滚事务
            throw throwable;
        }
    }

    /**
     * 开启事务，手动提交和回滚
     * @return
     */
    public Transaction transaction() {
        try {
            String tenant = this.tenantService.getTenant();
            if (StrUtil.isNotEmpty(tenant)) {
                DynamicDataSourceContextHolder.push(tenant);
            }
            return new Transaction(new DataSourceTransactionManager(this.jdbcTemplate.getDataSource()));
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    /**
     * 查询SQL，返回List类型结果
     * @param sql `SQL`语句
     */
    public List<Map<String, Object>> select(String sql) {
        return select(sql, null);
    }

    /**
     * 查询SQL，并传入变量信息，返回List类型结果
     * @param sql `SQL`语句
     * @param params 变量信息
     * @return
     */
    public List<Map<String, Object>> select(String sql, Map<String, Object> params) {
        return select(new BoundSql(sql, params, this));
    }

    @Transient
    public List<Map<String, Object>> select(BoundSql boundSql) {
        return queryForList(boundSql);
    }

    private List<Map<String, Object>> queryForList(BoundSql boundSql) {
        return this.execute(boundSql, () -> boundSql.execute(() -> {
            List<Map<String, Object>> list = this.jdbcTemplate.query(boundSql.getSql(), this.columnMapRowMapper, boundSql.getParameters());
            if (boundSql.getExcludeColumns() != null) {
                list.forEach(row -> boundSql.getExcludeColumns().forEach(row::remove));
            }
            return list;
        }));
    }

    /**
     * 执行update操作，返回受影响行数
     * @param sql `SQL`语句
     * @return
     */
    public int update(String sql) {
        return update(sql, null);
    }

    /**
     * 执行update操作，并传入变量信息，返回受影响行数
     * @param sql `SQL`语句
     * @param params
     * @return
     */
    public int update(String sql, Map<String, Object> params) {
        return update(new BoundSql(sql, params, this));
    }

    @Transient
    public int update(BoundSql boundSql) {
        return this.execute(boundSql, () -> {
            Object value = this.jdbcTemplate.update(boundSql.getSql(), boundSql.getParameters());
            return (int) value;
        });
    }

    /**
     * 执行insert操作，返回插入主键
     * @param sql `SQL`语句
     * @return
     */
    public Object insert(String sql) {
        return insert(sql, null, null);
    }

    /**
     * 执行insert操作，并传入变量信息，返回插入主键
     * @param sql `SQL`语句
     * @param params 变量信息
     * @return
     */
    public Object insert(String sql, Map<String, Object> params) {
        return insert(sql, null, params);
    }

    /**
     * 执行insert操作，返回插入主键
     * @param sql `SQL`语句
     * @param primary 主键列
     * @return
     */
    public Object insert(String sql, String primary) {
        return insert(sql, primary, null);
    }

    /**
     * 执行insert操作，并传入主键和变量信息，返回插入主键
     * @param sql `SQL`语句
     * @param primary 主键列
     * @param params 变量信息
     * @return
     */
    public Object insert(String sql, String primary, Map<String, Object> params) {
        return insert(new BoundSql(sql, params, this), primary);
    }

    @Transient
    public Object insert(BoundSql boundSql, String primary) {
        return this.execute(boundSql, () -> {
            KeyHolder keyHolder = new KeyHolder(primary);
            this.jdbcTemplate.update(con -> {
                PreparedStatement ps = keyHolder.createPrepareStatement(con, boundSql.getSql());
                new ArgumentPreparedStatementSetter(boundSql.getParameters()).setValues(ps);
                return ps;
            }, keyHolder);
            Object value = keyHolder.getObjectKey();
            return value;
        });
    }

    /**
     * 批量执行操作，返回受影响的行数
     * @param sql `SQL`语句
     * @param args 参数
     * @return
     */
    public int batchUpdate(String sql, List<Object[]> args) {
        return this.execute(null, () -> {
            int[] values = this.jdbcTemplate.batchUpdate(sql, args);
            return Arrays.stream(values).sum();
        });
    }

    /**
     * 批量执行操作，返回受影响的行数
     * @param sql `SQL`语句
     * @param batchSize 批量插入每次数量
     * @param args 参数
     * @return
     */
    public int batchUpdate(String sql, int batchSize, List<Object[]> args) {
        return this.execute(null, () -> {
            int[][] values = this.jdbcTemplate.batchUpdate(sql, args, batchSize, (ps, arguments) -> {
                int colIndex = 1;
                for (Object value : arguments) {
                    if (value instanceof SqlParameterValue) {
                        SqlParameterValue paramValue = (SqlParameterValue) value;
                        StatementCreatorUtils.setParameterValue(ps, colIndex++, paramValue, paramValue.getValue());
                    } else {
                        StatementCreatorUtils.setParameterValue(ps, colIndex++, StatementCreatorUtils.javaTypeToSqlParameterType(value == null ? null : value.getClass()), value);
                    }
                }
            });
            int count = 0;
            for (int[] value : values) {
                count += Arrays.stream(value).sum();
            }
            return count;
        });
    }

    /**
     * 批量执行操作，返回受影响的行数
     * @param sqls `SQL`语句
     * @return
     */
    public int batchUpdate(List<String> sqls) {
        return this.execute(null, () -> {
            int[] values = this.jdbcTemplate.batchUpdate(sqls.toArray(new String[0]));
            return Arrays.stream(values).sum();
        });
    }

    /**
     * 执行分页查询，分页条件手动传入
     * @param sql `SQL`语句
     * @param limit 限制条数
     * @param offset 跳过条数
     * @return
     */
    public Object page(String sql, long limit, long offset) {
        return page(sql, limit, offset, null);
    }

    /**
     * 执行分页查询，并传入变量信息，分页条件手动传入
     * @param sql `SQL`语句
     * @param limit 限制条数
     * @param offset 跳过条数
     * @param params 变量信息
     * @return
     */
    public Object page(String sql, long limit, long offset, Map<String, Object> params) {
        BoundSql boundSql = new BoundSql(sql, params, this);
        return page(boundSql, new Page(limit, offset));
    }

    /**
     * 执行分页查询
     * @param sql `SQL`语句
     * @param page 当前页
     * @param limit 限制条数
     * @return
     */
    public Object page1(String sql, long page, long limit) {
        return page1(sql, page, limit, null);
    }

    /**
     * 执行分页查询，并传入变量信息，分页条件手动传入
     * @param sql `SQL`语句
     * @param page 当前页
     * @param limit 限制条数
     * @param params 变量信息
     * @return
     */
    public Object page1(String sql, long page, long limit, Map<String, Object> params) {
        BoundSql boundSql = new BoundSql(sql, params, this);
        return page(boundSql, Page.createPage(page, limit));
    }

    @Transient
    public Object page(BoundSql boundSql, Page page) {
        return this.execute(boundSql, () -> {
            Dialect dialect = this.getDialect();
            BoundSql countBoundSql = boundSql.copy(dialect.getCountSql(boundSql.getSql()));
            int count = countBoundSql.execute(() -> this.jdbcTemplate.query(
                    countBoundSql.getSql(),
                    new SingleRowResultSetExtractor<>(Integer.class),
                    countBoundSql.getParameters()
            ));
            List<Map<String, Object>> list = null;
            if (count > 0) {
                BoundSql pageBoundSql = buildPageBoundSql(dialect, boundSql, page.getOffset(), page.getLimit());
                list = pageBoundSql.execute(() -> {
                    List<Map<String, Object>> result = this.jdbcTemplate.query(pageBoundSql.getSql(),
                            this.columnMapRowMapper, pageBoundSql.getParameters());
                    if (pageBoundSql.getExcludeColumns() != null) {
                        result.forEach(row -> pageBoundSql.getExcludeColumns().forEach(row::remove));
                    }
                    return result;
                });
            }
            RequestEntity requestEntity = RequestContext.getRequestEntity();
            return resultProvider.buildPageResult(requestEntity, page, count, list);
        });
    }

    private BoundSql buildPageBoundSql(Dialect dialect, BoundSql boundSql, long offset, long limit) {
        String pageSql = dialect.getPageSql(boundSql.getSql(), boundSql, offset, limit);
        return boundSql.copy(pageSql);
    }

    /**
     * 查询int值，适合单行单列int的结果
     * @param sql `SQL`语句
     * @return
     */
    public Integer selectInt(String sql) {
        return selectInt(sql, null);
    }

    /**
     * 查询int值，并传入变量信息，适合单行单列int的结果
     * @param sql `SQL`语句
     * @param params 变量信息
     * @return
     */
    public Integer selectInt(String sql, Map<String, Object> params) {
        return selectInt(new BoundSql(sql, params, this));
    }

    @Transient
    public Integer selectInt(BoundSql boundSql) {
        return this.execute(boundSql, () -> boundSql.execute(() -> this.jdbcTemplate.query(boundSql.getSql(),
                new SingleRowResultSetExtractor<>(Integer.class), boundSql.getParameters())));
    }

    /**
     * 查询单行单列的值
     * @param sql sql语句
     * @return
     */
    public Object selectValue(String sql) {
        return selectValue(sql, null);
    }

    /**
     * 查询单行单列的值，并传入变量信息
     * @param sql sql语句
     * @param params 变量信息
     * @return
     */
    public Object selectValue(String sql, Map<String, Object> params) {
        BoundSql boundSql = new BoundSql(sql, params, this);
        return this.execute(boundSql, () -> boundSql.execute(() -> this.jdbcTemplate.query(
                boundSql.getSql(),
                new SingleRowResultSetExtractor<>(Object.class),
                boundSql.getParameters()
        )));
    }

    /**
     * 查询单条结果，查不到返回null
     * @param sql sql语句
     * @return
     */
    public Map<String, Object> selectOne(String sql) {
        return this.selectOne(sql, null);
    }

    /**
     * 查询单条结果，并传入变量信息，查不到返回null
     * @param sql sql语句
     * @param params 参数
     * @return
     */
    public Map<String, Object> selectOne(String sql, Map<String, Object> params) {
        return this.selectOne(new BoundSql(sql, params, this));
    }

    /**
     * 查询单条结果
     * @param boundSql boundSql
     * @return
     */
    @Transient
    public Map<String, Object> selectOne(BoundSql boundSql) {
        return this.execute(boundSql, () -> boundSql.execute(() -> {
            Map<String, Object> row = this.jdbcTemplate.query(boundSql.getSql(), new SingleRowResultSetExtractor<>(this.columnMapRowMapper),
                    boundSql.getParameters());
            if (row != null && boundSql.getExcludeColumns() != null) {
                boundSql.getExcludeColumns().forEach(row::remove);
            }
            return row;
        }));
    }

    /**
     * 指定table，进行单表操作
     * @param tableName 表名
     * @return
     */
    public NamedTable table(String tableName) {
        return new NamedTable(tableName, this, rowMapColumnMapper);
    }

    public BoundDbModule ds(String dsKey) {
        return new BoundDbModule(dynamicRoutingDataSource, tenantService,
                sqlInterceptors, dialectAdapter,
                resultProvider, columnMapperAdapter,
                columnMapRowMapper, rowMapColumnMapper,
                logicDeleteColumn, logicDeleteValue,
                dsKey);
    }

    /**
     * 指定SQL，进行SQL相关操作
     * @param sql 子SQL
     * @return
     */
    public SQLTable sql(String sql) {
        return this.sql(sql, null);
    }

    /**
     * 指定SQL，进行SQL相关操作
     * @param sql 子SQL
     * @param params 参数
     * @return
     */
    public SQLTable sql(String sql, Map<String, Object> params) {
        return new SQLTable(new BoundSql(sql, params, this), this, rowMapColumnMapper);
    }

    @SuppressWarnings("unchecked")
    public <T> T execute(BoundSql boundSql, Supplier<T> supplier) {
        try {
            String tenant = this.tenantService.getTenant();
            if (StrUtil.isNotEmpty(tenant)) {
                DynamicDataSourceContextHolder.push(tenant);
            }
            if (null != boundSql)
                this.sqlInterceptors.forEach(interceptor -> interceptor.preHandle(boundSql, RequestContext.getRequestEntity()));
            Object result = supplier.get();
            return (T) result;
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    /**
     * 复制新的
     * @return
     */
    @Transient
    public DbModule cloneDBModule() {
        DbModule dbModule = new DbModule(this.jdbcTemplate, this.dynamicRoutingDataSource, this.tenantService);
        dbModule.setColumnMapperProvider(this.columnMapperAdapter);
        dbModule.setColumnMapRowMapper(this.columnMapRowMapper);
        dbModule.setRowMapColumnMapper(this.rowMapColumnMapper);
        dbModule.setSqlInterceptors(this.sqlInterceptors);
        dbModule.setDialectAdapter(this.dialectAdapter);
        dbModule.setResultProvider(this.resultProvider);
        dbModule.setLogicDeleteColumn(this.logicDeleteColumn);
        dbModule.setLogicDeleteValue(this.logicDeleteValue);
        return dbModule;
    }

    /**
     * 指定列名转换
     * @param name
     * @return
     */
    public DbModule columnCase(String name) {
        DbModule dbModule = cloneDBModule();
        dbModule.setColumnMapRowMapper(this.columnMapperAdapter.getColumnMapRowMapper(name));
        dbModule.setRowMapColumnMapper(this.columnMapperAdapter.getRowMapColumnMapper(name));
        return dbModule;
    }

    @Transient
    public String getDataSourceName() {
        String tenant = this.tenantService.getTenant();
        return StrUtil.isNotEmpty(tenant) ? tenant : "默认数据源";
    }

    public Dialect getDialect() {
        DataSource dataSource = null;
        try {
            String tenant = this.tenantService.getTenant();
            if (StrUtil.isNotEmpty(tenant)) {
                DynamicDataSourceContextHolder.push(tenant);
            }
            dataSource = this.jdbcTemplate.getDataSource();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
        if (null == dataSource)
            throw new APIException("自动获取数据库方言失败");
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            Dialect dialect = dialectAdapter.getDialectFromConnection(connection);
            if (dialect == null) {
                throw new APIException("自动获取数据库方言失败");
            }
            return dialect;
        } catch (Exception e) {
            throw new APIException("自动获取数据库方言失败", e);
        } finally {
            if (null != connection)
                DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    /**
     * 采用驼峰列名
     * @return
     */
    public DbModule camel() {
        return columnCase("camel");
    }

    /**
     * 采用帕斯卡列名
     * @return
     */
    public DbModule pascal() {
        return columnCase("pascal");
    }

    /**
     * 采用全小写列名
     * @return
     */
    public DbModule lower() {
        return columnCase("lower");
    }

    /**
     *采用全大写列名
     * @return
     */
    public DbModule upper() {
        return columnCase("upper");
    }

    /**
     * 列名保持原样
     * @return
     */
    public DbModule normal() {
        return columnCase("default");
    }

    @Override
    public String getType() {
        return VAR_NAME_MODULE_DB;
    }

    static class KeyHolder extends GeneratedKeyHolder {

        private final boolean useGeneratedKeys;

        private final String primary;

        public KeyHolder() {
            this(null);
        }

        public KeyHolder(String primary) {
            this.primary = primary;
            this.useGeneratedKeys = StringUtils.isBlank(primary);
        }

        PreparedStatement createPrepareStatement(Connection connection, String sql) throws SQLException {
            if (useGeneratedKeys) {
                return connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            }
            return connection.prepareStatement(sql, new String[]{primary});
        }

        public Object getObjectKey() {
            List<Map<String, Object>> keyList = getKeyList();
            if (keyList.isEmpty()) {
                return null;
            }
            Iterator<Object> keyIterator = keyList.get(0).values().iterator();
            Object key = keyIterator.hasNext() ? keyIterator.next() : null;
//            if (key != null && "oracle.sql.ROWID".equals(key.getClass().getName())) {
//                return ScriptManager.executeExpression("row.stringValue()", Collections.singletonMap("row", key));
//            }
            return key;
        }
    }
}
