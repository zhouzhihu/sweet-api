package com.egrand.sweetapi.starter.db.strategy;

import javax.sql.DataSource;
import java.util.List;

/**
 * The interface of dynamic datasource switch strategy
 *
 */
public interface DynamicDataSourceStrategy {

    /**
     * determine a database from the given dataSources
     *
     * @param dataSources given dataSources
     * @return final dataSource
     */
    DataSource determineDataSource(List<DataSource> dataSources);
}
