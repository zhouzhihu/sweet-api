package com.egrand.sweetapi.modules.db.table;

import com.egrand.sweetapi.core.exception.APIException;
import com.egrand.sweetapi.modules.db.BoundSql;
import com.egrand.sweetapi.modules.db.DbModule;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;

/**
 * 自定义SQL组装表
 */
@Slf4j
public class SQLTable extends AbstractTable {

    /**
     * 自定义SQL
     */
    BoundSql sql;

    public SQLTable(BoundSql sql, DbModule dbModule, Function<String, String> rowMapColumnMapper) {
        this.sql = sql;
        this.dbModule = dbModule;
        this.rowMapColumnMapper = rowMapColumnMapper;
    }

    private SQLTable() {
    }

    @Override
    public SQLTable clone() {
        SQLTable sqlTable = new SQLTable();
        sqlTable.sql = this.sql;
        sqlTable.dbModule = this.dbModule;
        sqlTable.fields = new ArrayList<>(fields);
        sqlTable.groups = new ArrayList<>(groups);
        sqlTable.orders = new ArrayList<>(orders);
        sqlTable.excludeColumns = new HashSet<>(excludeColumns);
        sqlTable.rowMapColumnMapper = this.rowMapColumnMapper;
        sqlTable.where = this.where == null ? null : this.where.clone();
        sqlTable.properties = this.properties;
        return sqlTable;
    }

    @Override
    String getSql() {
        return "(" + sql.getSql() + ") alias";
    }

    @Override
    Object[] getParams() {
        return this.sql.getParameters();
    }

    @Override
    Object save() {
        throw new APIException("不支持！");
    }

    @Override
    Object save(Map<String, Object> data, boolean beforeQuery) {
        throw new APIException("不支持！");
    }

    @Override
    Object save(boolean beforeQuery) {
        throw new APIException("不支持！");
    }

    @Override
    Object save(Map<String, Object> data) {
        throw new APIException("不支持！");
    }

    @Override
    Object insert() {
        throw new APIException("不支持！");
    }

    @Override
    Object insert(Map<String, Object> data) {
        throw new APIException("不支持！");
    }

    @Override
    int update() {
        throw new APIException("不支持！");
    }

    @Override
    int update(Map<String, Object> data, boolean isUpdateBlank) {
        throw new APIException("不支持！");
    }

    @Override
    int update(Map<String, Object> data) {
        throw new APIException("不支持！");
    }

    @Override
    int delete() {
        throw new APIException("不支持！");
    }
}
