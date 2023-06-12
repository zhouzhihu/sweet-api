package com.egrand.sweetapi.starter.db.creator;

import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.sql.DataSource;

/**
 * JNDI数据源创建器
 *
 */
public class JndiDataSourceCreator {

    private static final JndiDataSourceLookup LOOKUP = new JndiDataSourceLookup();

    /**
     * 创建基础数据源
     *
     * @param name 数据源参数
     * @return 数据源
     */
    public DataSource createDataSource(String name) {
        return LOOKUP.getDataSource(name);
    }

}
