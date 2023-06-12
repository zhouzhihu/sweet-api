package com.egrand.sweetapi.starter.db.provider;

import com.egrand.sweetapi.starter.db.spring.boot.autoconfigure.DataSourceProperty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.Map;

/**
 * YML数据源提供者
 *
 */
@Slf4j
@AllArgsConstructor
public class YmlDynamicDataSourceProvider extends AbstractDataSourceProvider implements DynamicDataSourceProvider {

    /**
     * 所有数据源
     */
    private Map<String, DataSourceProperty> dataSourcePropertiesMap;

    @Override
    public Map<String, DataSource> loadDataSources() {
        return createDataSourceMap(dataSourcePropertiesMap);
    }

    @Override
    public Map<String, DataSourceProperty> loadDataSourceProperty() {
        return null;
    }
}
