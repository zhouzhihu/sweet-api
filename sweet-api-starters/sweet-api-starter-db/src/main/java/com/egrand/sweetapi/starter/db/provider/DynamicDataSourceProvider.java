package com.egrand.sweetapi.starter.db.provider;

import com.egrand.sweetapi.starter.db.spring.boot.autoconfigure.DataSourceProperty;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 多数据源加载接口，默认的实现为从yml信息中加载所有数据源 你可以自己实现从其他地方加载所有数据源
 *
 */
public interface DynamicDataSourceProvider {

    /**
     * 加载所有数据源
     *
     * @return 所有数据源，key为数据源名称
     */
    Map<String, DataSource> loadDataSources();

    /**
     * 只加载数据源，不初始化连接
     * @return
     */
    Map<String, DataSourceProperty> loadDataSourceProperty();
}
