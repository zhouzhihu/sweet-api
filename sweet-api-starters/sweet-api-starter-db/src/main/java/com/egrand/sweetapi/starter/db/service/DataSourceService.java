package com.egrand.sweetapi.starter.db.service;

import com.egrand.sweetapi.starter.db.spring.boot.autoconfigure.DataSourceProperty;

import javax.sql.DataSource;
import java.util.Map;

public interface DataSourceService {
    /**
     * 添加数据源
     * @param dataSourceProperty 数据源信息
     * @return 所有数据源
     */
    Map<String, DataSource> addDataSource(DataSourceProperty dataSourceProperty);

    /**
     * 删除数据源
     * @param name 数据源名称
     * @return 所有数据源
     */
    Map<String, DataSource> deleteDataSource(String name);

    /**
     * 根据name获取数据源
     * @param name 数据源名称
     * @return
     */
    DataSource getDataSource(String name);

    /**
     * 获取所有数据源
     * @return
     */
    Map<String, DataSource> getDataSources();

    /**
     * 判断是否存在数据源
     * @param key
     * @return
     */
    boolean containsKey(String key);

    /**
     * 获取主数据源
     * @return
     */
    DataSource getPrimaryDataSource();

}
