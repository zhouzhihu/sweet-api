package com.egrand.sweetapi.starter.db.ds;

import com.egrand.sweetapi.starter.db.strategy.DynamicDataSourceStrategy;
import lombok.Data;

import javax.sql.DataSource;
import java.util.LinkedList;
import java.util.List;

/**
 * 组数据源
 *
 */
@Data
public class GroupDataSource {

    private String groupName;

    private DynamicDataSourceStrategy dynamicDataSourceStrategy;

    private List<DataSource> dataSources = new LinkedList<>();

    public GroupDataSource(String groupName, DynamicDataSourceStrategy dynamicDataSourceStrategy) {
        this.groupName = groupName;
        this.dynamicDataSourceStrategy = dynamicDataSourceStrategy;
    }

    public void addDatasource(DataSource dataSource) {
        dataSources.add(dataSource);
    }

    public void removeDatasource(DataSource dataSource) {
        dataSources.remove(dataSource);
    }

    public DataSource determineDataSource() {
        return dynamicDataSourceStrategy.determineDataSource(dataSources);
    }

    public int size() {
        return dataSources.size();
    }
}