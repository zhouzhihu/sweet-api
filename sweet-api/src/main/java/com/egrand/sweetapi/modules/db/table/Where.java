package com.egrand.sweetapi.modules.db.table;

import com.egrand.sweetapi.core.exception.APIException;
import org.apache.commons.lang3.StringUtils;

import java.beans.Transient;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * 单表API的Where
 */
public class Where {

    private final List<String> tokens = new ArrayList<>();

    private final List<Object> params = new ArrayList<>();

    private final AbstractTable namedTable;

    private final boolean needWhere;

    private boolean notNull = false;

    private boolean notBlank = false;

    public Where(AbstractTable namedTable) {
        this(namedTable, true);
    }

    public Where(AbstractTable namedTable, boolean needWhere) {
        this.namedTable = namedTable;
        this.needWhere = needWhere;
    }

    @Override
    public Where clone() {
        Where where = new Where(this.namedTable, this.needWhere);
        where.tokens.addAll(this.tokens);
        where.params.addAll(this.params);
        where.notNull = this.notNull;
        where.notBlank = this.notBlank;
        return where;
    }

    /**
     * 添加参数
     * @param value
     */
    public void addParams(Object value) {
        params.add(value);
    }

    /**
     * 添加参数
     * @param value
     */
    public void addParams(Object[] value) {
        for (Object val : value) {
            params.add(val);
        }
    }

    /**
     * 过滤`null`的参数
     * @return
     */
    public Where notNull() {
        return notNull(true);
    }

    /**
     * 过滤`blank`的参数
     * @return
     */
    public Where notBlank() {
        return notBlank(true);
    }

    /**
     * 是否过滤`null`的参数
     * @param flag
     * @return
     */
    public Where notNull(boolean flag) {
        this.notNull = flag;
        return this;
    }

    /**
     * 是否过滤`blank`的参数
     * @param flag
     * @return
     */
    public Where notBlank(boolean flag) {
        this.notBlank = flag;
        return this;
    }

    /**
     * 等于`=`,如：`eq('name', '老王') ---> name = '老王'`
     * @param column 数据库中的列名
     * @param value 值
     * @return
     */
    public Where eq(String column, Object value) {
        return eq(true, column, value);
    }

    /**
     * 等于`=`,如：`eq('name', '老王') ---> name = '老王'`
     * @param condition 判断表达式，当为true时拼接条件
     * @param column 数据库中的列名
     * @param value 值
     * @return
     */
    public Where eq(boolean condition, String column, Object value) {
        if (condition && filterNullAndBlank(value)) {
            tokens.add(namedTable.rowMapColumnMapper.apply(column));
            if (value == null) {
                append(" is null");
            } else {
                params.add(value);
                append(" = ?");
            }
            appendAnd();
        }
        return this;
    }

    /**
     * 不等于`<>`,如：`ne('name', '老王') ---> name <> '老王'`
     * @param column 数据库中的列名
     * @param value 值
     * @return
     */
    public Where ne(String column, Object value) {
        return ne(true, column, value);
    }

    /**
     * 不等于`<>`,如：`ne('name', '老王') ---> name <> '老王'`
     * @param condition 判断表达式，当为true时拼接条件
     * @param column 数据库中的列名
     * @param value 值
     * @return
     */
    public Where ne(boolean condition, String column, Object value) {
        if (condition && filterNullAndBlank(value)) {
            append(namedTable.rowMapColumnMapper.apply(column));
            if (value == null) {
                append("is not null");
            } else {
                params.add(value);
                append("<> ?");
            }
            appendAnd();
        }
        return this;
    }

    /**
     * 小于`<`,如：`lt('age', 18) ---> age < 18
     * @param column 数据库中的列名
     * @param value 值
     * @return
     */
    public Where lt(String column, Object value) {
        return lt(true, column, value);
    }

    /**
     * 小于`<`,如：`lt('age', 18) ---> age < 18
     * @param condition 判断表达式，当为true时拼接条件
     * @param column 数据库中的列名
     * @param value 值
     * @return
     */
    public Where lt(boolean condition, String column, Object value) {
        return append(condition, column, " < ?", value);
    }

    /**
     * 小于等于`<=`,如：`lte('age', 18) ---> age <= 18
     * @param column 数据库中的列名
     * @param value 值
     * @return
     */
    public Where lte(String column, Object value) {
        return lte(true, column, value);
    }

    /**
     * 小于等于`<=`,如：`lte('age', 18) ---> age <= 18
     * @param condition 判断表达式，当为true时拼接条件
     * @param column 数据库中的列名
     * @param value 值
     * @return
     */
    public Where lte(boolean condition, String column, Object value) {
        return append(condition, column, " <= ?", value);
    }

    /**
     * 大于`>`,如：`get('age', 18) ---> age > 18
     * @param column 数据库中的列名
     * @param value 值
     * @return
     */
    public Where gt(String column, Object value) {
        return gt(true, column, value);
    }

    /**
     * 大于`>`,如：`get('age', 18) ---> age > 18
     * @param condition 判断表达式，当为true时拼接条件
     * @param column 数据库中的列名
     * @param value 值
     * @return
     */
    public Where gt(boolean condition, String column, Object value) {
        return append(condition, column, " > ?", value);
    }

    /**
     * 大于等于`>=`,如：`get('age', 18) ---> age >= 18
     * @param column 数据库中的列名
     * @param value 值
     * @return
     */
    public Where gte(String column, Object value) {
        return gte(true, column, value);
    }

    /**
     * 大于等于`>=`,如：`get('age', 18) ---> age >= 18
     * @param condition 判断表达式，当为true时拼接条件
     * @param column 数据库中的列名
     * @param value 值
     * @return
     */
    public Where gte(boolean condition, String column, Object value) {
        return append(condition, column, " >= ?", value);
    }

    /**
     * in`,如：`in('age', [1,2,3]) ---> age in (1,2,3)
     * @param column 数据库中的列名
     * @param value 值
     * @return
     */
    public Where in(String column, Object value) {
        return in(true, column, value);
    }

    /**
     * `in`,如：`in('age', [1,2,3]) ---> age in (1,2,3)
     * @param condition 判断表达式，当为true时拼接条件
     * @param column 数据库中的列名
     * @param value 值
     * @return
     */
    public Where in(boolean condition, String column, Object value) {
        if (condition && value != null) {
            List<Object> objects = arrayLikeToList(value);
            if (objects.size() > 0) {
                append(namedTable.rowMapColumnMapper.apply(column));
                append(" in (");
                append(String.join(",", Collections.nCopies(objects.size(), "?")));
                append(")");
                appendAnd();
                params.addAll(objects);
            }
        }
        return this;
    }

    /**
     * `not in`,如：`notIn('age', [1,2,3]) ---> age not in (1,2,3)
     * @param column 数据库中的列名
     * @param value 值
     * @return
     */
    public Where notIn(String column,Object value) {
        return notIn(true, column, value);
    }

    /**
     * `not in`,如：`notIn('age', [1,2,3]) ---> age not in (1,2,3)
     * @param condition 判断表达式，当为true时拼接条件
     * @param column 数据库中的列名
     * @param value 值
     * @return
     */
    public Where notIn(boolean condition, String column, Object value) {
        if (condition && value != null) {
            List<Object> objects = arrayLikeToList(value);
            if (objects.size() > 0) {
                append(namedTable.rowMapColumnMapper.apply(column));
                append("not in (");
                append(String.join(",", Collections.nCopies(objects.size(), "?")));
                append(")");
                appendAnd();
                params.addAll(objects);
            }
        }
        return this;
    }

    /**
     * `like`,如：`like('name', '%王%') ---> name like '%王%'
     * @param column 数据库中的列名
     * @param value 值
     * @return
     */
    public Where like(String column, Object value) {
        return like(true, column, value);
    }

    /**
     * `like`,如：`like('name', '%王%') ---> name like '%王%'
     * @param condition 判断表达式，当为true时拼接条件
     * @param column 数据库中的列名
     * @param value 值
     * @return
     */
    public Where like(boolean condition, String column, Object value) {
        return append(condition, column, "like ?", value);
    }

    /**
     * `not like`,如：`notLike('name', '%王%') ---> name not like '%王%'
     * @param column 数据库中的列名
     * @param value 值
     * @return
     */
    public Where notLike(String column, Object value) {
        return notLike(true, column, value);
    }

    /**
     * `not like` ,如：`notLike('name', '%王%') ---> name not like '%王%'
     * @param condition 判断表达式，当为true时拼接条件
     * @param column 数据库中的列名
     * @param value 值
     * @return
     */
    public Where notLike(boolean condition, String column, Object value) {
        return append(condition, column, "not like ?", value);
    }

    /**
     * `is null`,如：`isNull('name') ---> name is null
     * @param column 数据库中的列名
     * @return
     */
    public Where isNull(String column) {
        return isNull(true, column);
    }

    /**
     * `is null`,如：`isNull('name') ---> name is null
     * @param condition 判断表达式，当为true时拼接条件
     * @param column 数据库中的列名
     * @return
     */
    public Where isNull(boolean condition, String column) {
        if (condition) {
            append(namedTable.rowMapColumnMapper.apply(column));
            append("is null");
            appendAnd();
        }
        return this;
    }

    /**
     * `is not null`,如：`isNotNull('name') ---> name is not null
     * @param column 数据库中的列名
     * @return
     */
    public Where isNotNull(String column) {
        return isNotNull(true, column);
    }

    /**
     * `is not null`,如：`isNotNull('name') ---> name is not null
     * @param condition 判断表达式，当为true时拼接条件
     * @param column 数据库中的列名
     * @return
     */
    public Where isNotNull(boolean condition, String column) {
        if (condition) {
            append(namedTable.rowMapColumnMapper.apply(column));
            append("is not null");
            appendAnd();
        }
        return this;
    }

    /**
     * 拼接`or`
     * @return
     */
    public Where or() {
        appendOr();
        return this;
    }

    /**
     * 拼接`and`
     * @return
     */
    public Where and() {
        appendAnd();
        return this;
    }

    /**
     * `and`嵌套，如and(it => it.eq('name','李白').ne('status','正常') --> and (name = '李白' and status <> '正常')
     * @param function 回调函数
     * @return
     */
    public Where and(Function<Object[], Where> function) {
        return and(true, function);
    }

    /**
     * `and`嵌套，如and(it => it.eq('name','李白').ne('status','正常') --> and (name = '李白' and status <> '正常')
     * @param condition
     * @param function 回调函数
     * @return
     */
    public Where and(boolean condition, Function<Object[], Where> function) {
        if (condition) {
            Where expr = function.apply(new Object[]{new Where(this.namedTable, false)});
            this.params.addAll(expr.params);
            append("(");
            append(expr.getSql());
            append(")");
            appendAnd();
        }
        return this;
    }

    /**
     * 拼接`order by xxx asc`
     * @param column 要排序的列
     * @return
     */
    public Where orderBy(String column) {
        return orderBy(column, "asc");
    }

    /**
     * 拼接`order by xxx asc`
     * @param columns 要排序的列
     * @return
     */
    public Where orderBy(String ...columns) {
        for (String column : columns) {
            orderBy(column, "asc");
        }
        return this;
    }

    /**
     * 拼接`order by xxx desc`
     * @param column 要排序的列
     * @return
     */
    public Where orderByDesc(String column) {
        return orderBy(column, "desc");
    }

    /**
     * 拼接`order by xxx desc`
     * @param columns 要排序的列
     * @return
     */
    public Where orderByDesc(String ...columns) {
        for (String column : columns) {
            orderBy(column, "desc");
        }
        return this;
    }

    /**
     * 拼接`order by xxx asc/desc`
     * @param column 要排序的列
     * @param sort `asc`或`desc`
     * @return
     */
    public Where orderBy(String column, String sort) {
        this.namedTable.orderBy(column, sort);
        return this;
    }

    /**
     * 拼接`group by`
     * @param columns
     * @return
     */
    public Where groupBy(String... columns) {
        this.namedTable.groupBy(columns);
        return this;
    }

    /**
     * 保存到表中，当主键有值时则修改，否则插入
     * @param 
     * @return
     */
    public Object save() {
        return namedTable.save();
    }

    /**
     * 保存到表中，当主键有值时则修改，否则插入
     * @param beforeQuery 是否根据id查询有没有数据
     * @return
     */
    public Object save(boolean beforeQuery) {
        return namedTable.save(beforeQuery);
    }

    /**
     * 保存到表中，当主键有值时则修改，否则插入
     * @param data 各项列和值
     * @return
     */
    public Object save(Map<String, Object> data) {
        return namedTable.save(data);
    }

    /**
     * 保存到表中，当主键有值时则修改，否则插入
     * @param data 各项列和值
     * @param beforeQuery 是否根据id查询有没有数据
     * @return
     */
    public Object save(Map<String, Object> data, boolean beforeQuery) {
        return namedTable.save(data, beforeQuery);
    }

    /**
     * 执行插入语句，返回主键
     * @return
     */
    public Object insert() {
        return namedTable.insert();
    }

    /**
     * 执行插入语句，返回主键
     * @param data 各项列和值
     * @return
     */
    public Object insert(Map<String, Object> data) {
        return namedTable.insert(data);
    }

    /**
     * 执行update语句
     * @return
     */
    public int update() {
        return namedTable.update();
    }

    /**
     * 执行delete语句
     * @return
     */
    public int delete() {
        return namedTable.delete();
    }

    /**
     * 执行update语句
     * @param data 各项列和值
     * @return
     */
    public int update(Map<String, Object> data) {
        return namedTable.update(data);
    }

    /**
     * 执行update语句
     * @param data 各项列和值
     * @param isUpdateBlank 是否更新空值字段
     * @return
     */
    public int update(Map<String, Object> data, boolean isUpdateBlank) {
        return namedTable.update(data, isUpdateBlank);
    }

    /**
     * 执行分页查询
     * @return
     */
//    public Object page() {
//        return namedTable.page();
//    }

    /**
     * 执行分页查询，分页条件手动传入
     * @param limit 限制条数
     * @param offset 跳过条数
     * @return
     */
    public Object page(long limit, long offset) {
        return namedTable.page(limit, offset);
    }

    /**
     * 执行分页查询
     * @param page 当前页
     * @param limit 限制条数
     * @return
     */
    public Object page1(long page, long limit) {
        return namedTable.page1(page, limit);
    }

    /**
     * 执行select查询
     * @return
     */
    public List<Map<String, Object>> select() {
        return namedTable.select();
    }

    /**
     * 执行selectOne查询
     * @return
     */
    public Map<String, Object> selectOne() {
        return namedTable.selectOne();
    }

    /**
     * 查询条数
     * @return
     */
    public int count() {
        return namedTable.count();
    }

    /**
     * 查询是否存在
     * @return
     */
    public boolean exists() {
        return namedTable.exists();
    }

    @Transient
    public void appendAnd() {
        remove();
        tokens.add("and");
    }

    @Transient
    public void appendOr() {
        remove();
        tokens.add("or");
    }

    List<Object> getParams() {
        return params;
    }

    void remove() {
        int size = tokens.size();
        while (size > 0) {
            String token = tokens.get(size - 1);
            if ("and".equalsIgnoreCase(token) || "or".equalsIgnoreCase(token)) {
                tokens.remove(size - 1);
                size--;
            } else {
                break;
            }
        }
        while (size > 0) {
            String token = tokens.get(0);
            if ("and".equalsIgnoreCase(token) || "or".equalsIgnoreCase(token)) {
                tokens.remove(0);
                size--;
            } else {
                break;
            }
        }
    }

    boolean isEmpty() {
        return tokens.isEmpty();
    }

    @Transient
    public void append(String value) {
        tokens.add(value);
    }

    @Transient
    public void append(String sql, Object value) {
        tokens.add(sql);
        params.add(value);
    }

    String getSql() {
        remove();
        if (isEmpty()) {
            return "";
        }
        return (needWhere ? " where " : "") + String.join(" ", tokens);
    }

    boolean filterNullAndBlank(Object value) {
        if (notNull && value == null) {
            return false;
        }
        return !notBlank || !StringUtils.isEmpty(Objects.toString(value, ""));
    }

    private Where append(boolean append, String column, String condition, Object value) {
        if (append && filterNullAndBlank(value)) {
            append(namedTable.rowMapColumnMapper.apply(column));
            append(condition);
            appendAnd();
            params.add(value);
        }
        return this;
    }

    public static List<Object> arrayLikeToList(Object arrayLike) {
        if (arrayLike == null) {
            return new ArrayList();
        } else if (arrayLike instanceof Collection) {
            return new ArrayList((Collection)arrayLike);
        } else {
            ArrayList list;
            if (arrayLike.getClass().isArray()) {
                list = new ArrayList(Array.getLength(arrayLike));
                IntStream.range(0, Array.getLength(arrayLike)).forEach((i) -> {
                    list.add(Array.get(arrayLike, i));
                });
                return list;
            } else if (arrayLike instanceof Iterator) {
                list = new ArrayList();
                Iterator<Object> it = (Iterator)arrayLike;
                it.forEachRemaining(list::add);
                return list;
            } else if (arrayLike instanceof Enumeration) {
                Enumeration<Object> en = (Enumeration)arrayLike;
                return Collections.list(en);
            } else {
                throw new APIException("不支持的类型:" + arrayLike.getClass());
            }
        }
    }
}
