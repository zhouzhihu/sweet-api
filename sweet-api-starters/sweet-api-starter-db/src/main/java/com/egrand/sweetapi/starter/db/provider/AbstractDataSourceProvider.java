package com.egrand.sweetapi.starter.db.provider;

import com.egrand.sweetapi.starter.db.creator.DataSourceCreator;
import com.egrand.sweetapi.starter.db.spring.boot.autoconfigure.DataSourceProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public abstract class AbstractDataSourceProvider implements DynamicDataSourceProvider {

    @Autowired
    private DataSourceCreator dataSourceCreator;

    protected Map<String, DataSource> createDataSourceMap(
            Map<String, DataSourceProperty> dataSourcePropertiesMap) {
        Map<String, DataSource> dataSourceMap = new HashMap<>(dataSourcePropertiesMap.size() * 2);
        for (Map.Entry<String, DataSourceProperty> item : dataSourcePropertiesMap.entrySet()) {
            DataSourceProperty dataSourceProperty = item.getValue();
            String poolName = dataSourceProperty.getPoolName();
            if (poolName == null || "".equals(poolName)) {
                poolName = item.getKey();
            }
            dataSourceProperty.setPoolName(poolName);
            dataSourceMap.put(poolName, dataSourceCreator.createDataSource(dataSourceProperty));
        }
        return dataSourceMap;
    }
}
