package com.egrand.sweetapi.modules.db.table;

import com.egrand.sweetapi.core.model.Attributes;
import com.egrand.sweetapi.core.model.Page;
import com.egrand.sweetapi.modules.db.BoundSql;
import com.egrand.sweetapi.modules.db.DbModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.beans.Transient;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractTable extends Attributes<Object> {

    DbModule dbModule;

    /**
     * 查询的列
     */
    List<String> fields = new ArrayList<>();

    /**
     * 分组的列
     */
    List<String> groups = new ArrayList<>();

    /**
     * 排序的列
     */
    List<String> orders = new ArrayList<>();

    /**
     * 排除的列
     */
    Set<String> excludeColumns = new HashSet<>();

    /**
     * 列名转换
     */
    Function<String, String> rowMapColumnMapper;

    /**
     * Where查询
     */
    Where where = new Where(this);

    /**
     * 拼接where
     * @return
     */
    public Where where() {
        return where;
    }

    /**
     * 获取设置的fields
     */
    @Transient
    public List<String> getFields() {
        return fields;
    }

    /**
     * 设置 fields
     */
    @Transient
    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    /**
     * 获取设置的group
     */
    @Transient
    public List<String> getGroups() {
        return groups;
    }

    /**
     * 设置 group
     */
    @Transient
    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    /**
     * 获取设置的order
     */
    @Transient
    public List<String> getOrders() {
        return orders;
    }

    /**
     * 设置 order
     */
    @Transient
    public void setOrders(List<String> orders) {
        this.orders = orders;
    }

    /**
     * 获取设置的排除的列
     */
    @Transient
    public Set<String> getExcludeColumns() {
        return excludeColumns;
    }

    /**
     * 设置排除的列
     */
    @Transient
    public void setExcludeColumns(Set<String> excludeColumns) {
        this.excludeColumns = excludeColumns;
    }

    /**
     * 设置查询的列，如`columns(['a','b','c'])`
     * @param properties 查询的列
     * @return
     */
    public AbstractTable columns(Collection<String> properties) {
        if (properties != null) {
            properties.stream().filter(StringUtils::isNotBlank).map(rowMapColumnMapper).forEach(this.fields::add);
        }
        return this;
    }

    /**
     * 设置查询的列，如`column('a')`
     * @param property 查询的列
     * @return
     */
    public AbstractTable column(String property) {
        if (StringUtils.isNotBlank(property)) {
            this.fields.add(this.rowMapColumnMapper.apply(property));
        }
        return this;
    }

    /**
     * 拼接`group by`
     * @param properties 要分组的列
     * @return
     */
    public AbstractTable groupBy(String... properties) {
        this.groups.addAll(Arrays.stream(properties).map(rowMapColumnMapper).collect(Collectors.toList()));
        return this;
    }

    /**
     * 拼接`order by xxx asc`
     * @param property 要排序的列
     * @return
     */
    public AbstractTable orderBy(String property) {
        if (StringUtils.isNotBlank(property)) {
            return orderBy(property, "asc");
        }
        return this;
    }

    /**
     * 拼接`order by xxx asc`
     * @param propertys 要排序的列
     * @return
     */
    public AbstractTable orderBy(String ...propertys) {
        for (String property : propertys) {
            this.orderBy(property, "asc");
        }
        return this;
    }

    /**
     * 拼接`order by xxx desc`
     * @param property 要排序的列
     * @return
     */
    public AbstractTable orderByDesc(String property) {
        if (StringUtils.isNotBlank(property)) {
            return orderBy(property, "desc");
        }
        return this;
    }

    /**
     * 拼接`order by xxx desc`
     * @param propertys 要排序的列
     * @return
     */
    public AbstractTable orderByDesc(String ...propertys) {
        for (String property : propertys) {
            orderBy(property, "desc");
        }
        return this;
    }

    /**
     * 拼接`order by xxx asc/desc`
     * @param property 要排序的列
     * @param sort `asc`或`desc`
     * @return
     */
    public AbstractTable orderBy(String property, String sort) {
        if (StringUtils.isNotBlank(property) && StringUtils.isNotBlank(sort)) {
            this.orders.add(rowMapColumnMapper.apply(property) + " " + sort);
        }
        return this;
    }

    /**
     * 设置要排除的列
     * @param property 排除的列
     * @return
     */
    public AbstractTable exclude(String property) {
        if (property != null) {
            excludeColumns.add(property);
        }
        return this;
    }

    /**
     * 设置要排除的列
     * @param properties 排除的列
     * @return
     */
    public AbstractTable excludes(String... properties) {
        if (null != properties && properties.length != 0)
            excludeColumns.addAll(Arrays.asList(properties));
        return this;
    }

    /**
     * 设置要排除的列
     * @param properties 排除的列
     * @return
     */
    public AbstractTable excludes(List<String> properties) {
        if (null != properties && properties.size() != 0)
            excludeColumns.addAll(properties);
        return this;
    }

    /**
     * 执行`select`查询
     * @return
     */
    public List<Map<String, Object>> select() {
        return dbModule.select(buildSelect());
    }

    /**
     * 执行`selectOne`查询
     * @return
     */
    public Map<String, Object> selectOne() {
        return dbModule.selectOne(buildSelect());
    }

    /**
     * 执行分页查询，分页条件手动传入
     * @param limit 限制条数
     * @param offset 跳过条数
     * @return
     */
    public Object page(long limit, long offset) {
        return dbModule.page(buildSelect(), new Page(limit, offset));
    }

    /**
     * 执行分页查询
     * @param page 当前页
     * @param limit 限制条数
     * @return
     */
    public Object page1(long page, long limit) {
        return dbModule.page(buildSelect(), Page.createPage(page, limit));
    }

    /**
     * 查询条数
     * @return
     */
    public int count() {
        StringBuilder builder = new StringBuilder();
        builder.append("select count(1) from ").append(this.getSql());
        List<Object> params = buildWhere(builder);
        return dbModule.selectInt(new BoundSql(builder.toString(), params, dbModule));
    }

    /**
     * 判断是否存在
     * @return
     */
    public boolean exists() {
        return count() > 0;
    }

    /**
     * 构建Select语句
     * @return
     */
    public BoundSql buildSelect() {
        StringBuilder builder = new StringBuilder();
        builder.append("select ");
        List<String> fields = this.fields.stream()
                .filter(it -> !excludeColumns.contains(it))
                .collect(Collectors.toList());
        if (fields.isEmpty()) {
            builder.append("*");
        } else {
            builder.append(StringUtils.join(fields, ","));
        }
        builder.append(" from ").append(this.getSql());
        List<Object> params = buildWhere(builder);
        if (!groups.isEmpty()) {
            builder.append(" group by ");
            builder.append(String.join(",", groups));
        }
        if (!orders.isEmpty()) {
            builder.append(" order by ");
            builder.append(String.join(",", orders));
        }
        BoundSql boundSql = new BoundSql(builder.toString(), params, dbModule);
        boundSql.setExcludeColumns(excludeColumns);
        return boundSql;
    }

    /**
     * 构建Where语句及参数
     * @param builder
     * @return
     */
    public List<Object> buildWhere(StringBuilder builder) {
        List<Object> params = new ArrayList<>();
        // 添加基础SQL的参数列表
        Object[] sqlParams = this.getParams();
        if (null != sqlParams && sqlParams.length != 0) {
            for (Object val : sqlParams) {
                params.add(val);
            }
        }
        if (!where.isEmpty()) {
            builder.append(where.getSql());
            params.addAll(where.getParams());
        }
        return params;
    }

    /**
     * 获取基础SQL，这里可以返回表名或一个组合查询的SQL
     * @return
     */
    abstract String getSql();

    /**
     * 获取基础SQL参数
     * @return
     */
    abstract Object[] getParams();

    /**
     * 保存到表中，当主键有值时则修改，否则插入
     * @return
     */
    abstract Object save();

    /**
     * 保存到表中，当主键有值时则修改，否则插入
     * @param data 各项列和值
     * @param beforeQuery 是否根据id查询有没有数据
     * @return
     */
    abstract Object save(Map<String, Object> data, boolean beforeQuery);

    /**
     * 保存到表中，当主键有值时则修改，否则插入
     * @param beforeQuery 是否根据id查询有没有数据
     * @return
     */
    abstract Object save(boolean beforeQuery);

    /**
     * 保存到表中，当主键有值时则修改，否则插入
     * @param data 各项列和值
     * @return
     */
    abstract Object save(Map<String, Object> data);

    /**
     * 执行插入,返回主键
     * @return
     */
    abstract Object insert();

    /**
     * 执行插入,返回主键
     * @param data 各项列和值
     * @return
     */
    abstract Object insert(Map<String, Object> data);

    /**
     * 执行update语句
     * @return
     */
    abstract int update();

    /**
     * 执行update语句
     * @param data 各项列和值
     * @param isUpdateBlank 是否更新空值字段
     * @return
     */
    abstract int update(Map<String, Object> data, boolean isUpdateBlank);

    /**
     * 执行update语句
     * @param data 各项列和值
     * @return
     */
    abstract int update(Map<String, Object> data);

    /**
     * 执行delete语句
     * @return
     */
    abstract int delete();
}
