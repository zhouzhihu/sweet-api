package com.egrand.sweetapi.modules.db.table;

import com.egrand.sweetapi.core.context.RequestContext;
import com.egrand.sweetapi.core.context.RequestEntity;
import com.egrand.sweetapi.core.exception.APIException;
import com.egrand.sweetapi.modules.db.BoundSql;
import com.egrand.sweetapi.modules.db.DbModule;
import org.apache.commons.lang3.StringUtils;

import java.beans.Transient;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 单表操作API
 */
public class NamedTable extends AbstractTable {

    /**
     * 表名
     */
    String tableName;

    /**
     * 主键列
     */
    String primary;

    /**
     * 逻辑删除列
     */
    String logicDeleteColumn;

    /**
     * 逻辑删除值
     */
    Object logicDeleteValue;

    /**
     * 包含的列
     */
    Map<String, Object> columns = new HashMap<>();

    /**
     * 主键默认值
     */
    Object defaultPrimaryValue;

    /**
     * 是否使用逻辑删除
     */
    boolean useLogic = false;

    /**
     * 是否不过滤空参数
     */
    boolean withBlank = false;

    public NamedTable(String tableName, DbModule dbModule, Function<String, String> rowMapColumnMapper) {
        this.tableName = tableName;
        this.dbModule = dbModule;
        this.rowMapColumnMapper = rowMapColumnMapper;
        this.logicDeleteColumn = dbModule.getLogicDeleteColumn();
        String deleteValue = dbModule.getLogicDeleteValue();
        this.logicDeleteValue = deleteValue;
        if (deleteValue != null) {
            boolean isString = deleteValue.startsWith("'") || deleteValue.startsWith("\"");
            if (isString && deleteValue.length() > 2) {
                this.logicDeleteValue = deleteValue.substring(1, deleteValue.length() - 1);
            } else {
                try {
                    this.logicDeleteValue = Integer.parseInt(deleteValue);
                } catch (NumberFormatException e) {
                    this.logicDeleteValue = deleteValue;
                }
            }
        }
    }

    private NamedTable() {
    }

    @Override
    public NamedTable clone() {
        NamedTable namedTable = new NamedTable();
        namedTable.tableName = this.tableName;
        namedTable.dbModule = this.dbModule;
        namedTable.primary = this.primary;
        namedTable.logicDeleteValue = this.logicDeleteValue;
        namedTable.logicDeleteColumn = this.logicDeleteColumn;
        namedTable.columns = new HashMap<>(this.columns);
        namedTable.fields = new ArrayList<>(fields);
        namedTable.groups = new ArrayList<>(groups);
        namedTable.orders = new ArrayList<>(orders);
        namedTable.excludeColumns = new HashSet<>(excludeColumns);
        namedTable.rowMapColumnMapper = this.rowMapColumnMapper;
        namedTable.defaultPrimaryValue = this.defaultPrimaryValue;
        namedTable.useLogic = this.useLogic;
        namedTable.withBlank = this.withBlank;
        namedTable.where = this.where == null ? null : this.where.clone();
        namedTable.properties = this.properties;
        return namedTable;
    }

    /**
     * 使用逻辑删除
     * @return
     */
    public NamedTable logic() {
        this.useLogic = true;
        return this;
    }

    /**
     * 更新空值
     * @return
     */
    public NamedTable withBlank() {
        this.withBlank = true;
        return this;
    }

    /**
     * 设置主键名，update时使用
     * @param primary 主键列
     * @return
     */
    public NamedTable primary(String primary) {
        this.primary = rowMapColumnMapper.apply(primary);
        return this;
    }

    /**
     * 设置主键名，并设置默认主键值(主要用于insert)
     * @param primary 主键列
     * @param defaultPrimaryValue 默认值
     * @return
     */
    public NamedTable primary(String primary, Serializable defaultPrimaryValue) {
        this.primary = rowMapColumnMapper.apply(primary);
        this.defaultPrimaryValue = defaultPrimaryValue;
        return this;
    }

    /**
     * 设置主键名，并设置默认主键值(主要用于insert)
     * @param primary 主键列
     * @param defaultPrimaryValue 默认值
     * @return
     */
    public NamedTable primary(String primary, Supplier<Object> defaultPrimaryValue) {
        this.primary = rowMapColumnMapper.apply(primary);
        this.defaultPrimaryValue = defaultPrimaryValue;
        return this;
    }

    /**
     * 设置单列的值
     * @param property 列名
     * @param value 值
     * @return
     */
    public NamedTable column(String property, Object value) {
        this.columns.put(rowMapColumnMapper.apply(property), value);
        return this;
    }

    /**
     * 设置查询的列，如`columns('a','b','c')`
     * @param properties
     * @return
     */
    public NamedTable columns(String... properties) {
        if (properties != null) {
            for (String property : properties) {
                column(property);
            }
        }
        return this;
    }

    /**
     * 指定逻辑删除列名
     * @param logicDeleteColumn
     * @return
     */
    public NamedTable logicDeleteColumn(String logicDeleteColumn) {
        if (logicDeleteColumn != null) {
            this.logicDeleteColumn = logicDeleteColumn;
        }
        return this;
    }

    @Override
    public Object insert() {
        return insert(null);
    }

    @Override
    public Object insert(Map<String, Object> data) {
        if (data != null) {
            data.forEach((key, value) -> this.columns.put(rowMapColumnMapper.apply(key), value));
        }
        if (this.defaultPrimaryValue != null && StringUtils.isBlank(Objects.toString(this.columns.getOrDefault(this.primary, "")))) {
            if (this.defaultPrimaryValue instanceof Supplier) {
                this.columns.put(this.primary, ((Supplier<?>) this.defaultPrimaryValue).get());
            } else {
                this.columns.put(this.primary, this.defaultPrimaryValue);
            }
        }
        Collection<Map.Entry<String, Object>> entries = filterNotBlanks();
        if (entries.isEmpty()) {
            throw new APIException("参数不能为空");
        }
        StringBuilder builder = new StringBuilder();
        builder.append("insert into ");
        builder.append(tableName);
        builder.append("(");
        builder.append(StringUtils.join(entries.stream().map(Map.Entry::getKey).toArray(), ","));
        builder.append(") values (");
        builder.append(StringUtils.join(Collections.nCopies(entries.size(), "?"), ","));
        builder.append(")");
        Object value = dbModule.insert(new BoundSql(builder.toString(), entries.stream().map(Map.Entry::getValue).collect(Collectors.toList()), dbModule), this.primary);
        if(value == null && StringUtils.isNotBlank(this.primary)){
            return this.columns.get(this.primary);
        }
        return value;
    }

    @Override
    public int delete() {
        if (useLogic) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put(logicDeleteColumn, logicDeleteValue);
            return update(dataMap);
        }
        if (where.isEmpty()) {
            throw new APIException("delete语句不能没有条件");
        }
        StringBuilder builder = new StringBuilder();
        builder.append("delete from ");
        builder.append(tableName);
        builder.append(where.getSql());
        return dbModule.update(new BoundSql(builder.toString(), where.getParams(), dbModule));
    }


    @Override
    public Object save() {
        return this.save(null, false);
    }

    @Override
    public Object save(Map<String, Object> data, boolean beforeQuery) {
        if (StringUtils.isBlank(this.primary)) {
            throw new APIException("请设置主键");
        }
        if (data != null) {
            data.forEach((key, value) -> this.columns.put(rowMapColumnMapper.apply(key), value));
        }
        String primaryValue = Objects.toString(this.columns.get(this.primary), "");
        if (StringUtils.isBlank(primaryValue) && data != null) {
            primaryValue = Objects.toString(data.get(this.primary), "");
        }
        if (beforeQuery) {
            if (StringUtils.isNotBlank(primaryValue)) {
                List<Object> params = new ArrayList<>();
                params.add(primaryValue);
                Integer count = dbModule.selectInt(new BoundSql("select count(*) count from " + this.tableName + " where " + this.primary + " = ?", params, dbModule));
                if (count == 0) {
                    return insert(data);
                }
                return update(data);
            } else {
                return insert(data);
            }
        }

        if (StringUtils.isNotBlank(primaryValue)) {
            return update(data);
        }
        return insert(data);
    }

    @Override
    public Object save(boolean beforeQuery) {
        return this.save(null, beforeQuery);
    }

    @Override
    public Object save(Map<String, Object> data) {
        return this.save(data, false);
    }

    /**
     * 批量插入
     * @param collection 各项列和值
     * @return
     */
    public int batchInsert(List<Map<String, Object>> collection) {
        return batchInsert(collection, 100);
    }

    /**
     * 批量插入
     * @param collection 各项列和值
     * @param batchSize batchSize
     * @return
     */
    public int batchInsert(List<Map<String, Object>> collection, int batchSize) {
        Set<String> keys = collection.stream().flatMap(it -> it.keySet().stream()).collect(Collectors.toSet());
        if (keys.isEmpty()) {
            throw new APIException("要插入的列不能为空");
        }
        StringBuilder builder = new StringBuilder();
        builder.append("insert into ");
        builder.append(tableName);
        builder.append("(");
        builder.append(StringUtils.join(keys.stream().map(rowMapColumnMapper).collect(Collectors.toList()), ","));
        builder.append(") values (");
        builder.append(StringUtils.join(Collections.nCopies(keys.size(), "?"), ","));
        builder.append(")");
        return this.dbModule.batchUpdate(builder.toString(), batchSize, collection.stream()
                .map(it -> keys.stream().map(it::get).toArray())
                .collect(Collectors.toList()));
    }

    @Override
    public int update() {
        return update(null);
    }

    @Override
    public int update(Map<String, Object> data, boolean isUpdateBlank) {
        if (null != data) {
            data.forEach((key, value) -> this.columns.put(rowMapColumnMapper.apply(key), value));
        }
        Object primaryValue = null;
        if (StringUtils.isNotBlank(this.primary)) {
            primaryValue = this.columns.remove(this.primary);
        }
        this.withBlank = isUpdateBlank;
        List<Map.Entry<String, Object>> entries = new ArrayList<>(filterNotBlanks());
        if (entries.isEmpty()) {
            throw new APIException("要修改的列不能为空");
        }
        StringBuilder builder = new StringBuilder();
        builder.append("update ");
        builder.append(tableName);
        builder.append(" set ");
        List<Object> params = new ArrayList<>();
        for (int i = 0, size = entries.size(); i < size; i++) {
            Map.Entry<String, Object> entry = entries.get(i);
            builder.append(entry.getKey()).append(" = ?");
            params.add(entry.getValue());
            if (i + 1 < size) {
                builder.append(",");
            }
        }
        if (!where.isEmpty()) {
            builder.append(where.getSql());
            params.addAll(where.getParams());
        } else if (primaryValue != null) {
            builder.append(" where ").append(this.primary).append(" = ?");
            params.add(primaryValue);
        } else {
            throw new APIException("主键值不能为空");
        }
        return dbModule.update(new BoundSql(builder.toString(), params, dbModule));
    }

    @Override
    public int update(Map<String, Object> data) {
        return update(data, this.withBlank);
    }

    /**
     * 执行分页查询
     * @return
     */
//    public Object page() {
//        return dbModule.page(buildSelect());
//    }

    private Collection<Map.Entry<String, Object>> filterNotBlanks() {
        if (this.withBlank) {
            return this.columns.entrySet()
                    .stream()
                    .filter(it -> !excludeColumns.contains(it.getKey()))
                    .collect(Collectors.toList());
        }
        return this.columns.entrySet()
                .stream()
                .filter(it -> StringUtils.isNotBlank(Objects.toString(it.getValue(), "")))
                .filter(it -> !excludeColumns.contains(it.getKey()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Object> buildWhere(StringBuilder builder) {
        List<Object> params = new ArrayList<>();
        if (!where.isEmpty()) {
            where.and();
            where.ne(useLogic, logicDeleteColumn, logicDeleteValue);
            builder.append(where.getSql());
            params.addAll(where.getParams());
        } else if (useLogic) {
            where.ne(logicDeleteColumn, logicDeleteValue);
            builder.append(where.getSql());
            params.addAll(where.getParams());
        }
        return params;
    }

    @Override
    String getSql() {
        return this.tableName;
    }

    @Override
    Object[] getParams() {
        return null;
    }

    /**
     * 获取查询的表名
     *
     * @return 表名
     */
    @Transient
    public String getTableName() {
        return tableName;
    }

    /**
     * 设置表名
     *
     * @param tableName 表名
     */
    @Transient
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * 获取SQL模块
     */
    @Transient
    public DbModule getDbModule() {
        return dbModule;
    }

    /**
     * 获取主键列
     */
    @Transient
    public String getPrimary() {
        return primary;
    }

    /**
     * 获取逻辑删除列
     */
    @Transient
    public String getLogicDeleteColumn() {
        return logicDeleteColumn;
    }

    /**
     * 获取逻辑删除值
     */
    @Transient
    public Object getLogicDeleteValue() {
        return logicDeleteValue;
    }

    /**
     * 获取设置的columns
     */
    @Transient
    public Map<String, Object> getColumns() {
        return columns;
    }

    /**
     * 设置columns
     */
    @Transient
    public void setColumns(Map<String, Object> columns) {
        this.columns = columns;
    }

    /**
     * 主键默认值
     *
     * @return
     */
    @Transient
    public Object getDefaultPrimaryValue() {
        return defaultPrimaryValue;
    }

    /**
     * 是否设逻辑了逻辑删除
     */
    @Transient
    public boolean isUseLogic() {
        return useLogic;
    }

    /**
     * 设置是否使用逻辑删除
     */
    @Transient
    public void setUseLogic(boolean useLogic) {
        this.useLogic = useLogic;
    }

    /**
     * 获取是否不过滤空参数
     */
    @Transient
    public boolean isWithBlank() {
        return withBlank;
    }

    /**
     * 设置是否不过滤空参数
     */
    @Transient
    public void setWithBlank(boolean withBlank) {
        this.withBlank = withBlank;
    }

    /**
     * 获取where
     */
    @Transient
    public Where getWhere() {
        return where;
    }

    /**
     * 获取RequestEntity
     */
    @Transient
    public RequestEntity getRequestEntity() {
        return RequestContext.getRequestEntity();
    }
}
