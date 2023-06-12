package com.egrand.sweetapi.starter.db.spring.boot.autoconfigure;

import com.egrand.sweetapi.starter.db.DynamicRoutingDataSource;
import com.egrand.sweetapi.starter.db.provider.DynamicDataSourceProvider;
import com.egrand.sweetapi.starter.db.provider.YmlDynamicDataSourceProvider;
import com.egrand.sweetapi.starter.db.spring.boot.autoconfigure.druid.DruidDynamicDataSourceConfiguration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

/**
 * 动态数据源核心自动配置类
 *
 */
@Slf4j
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties({DynamicDataSourceProperties.class})
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@Import(value = {DruidDynamicDataSourceConfiguration.class, DynamicDataSourceCreatorAutoConfiguration.class})
@ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class DynamicDataSourceAutoConfiguration {

    private final DynamicDataSourceProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public DynamicDataSourceProvider dynamicDataSourceProvider() {
        Map<String, DataSourceProperty> datasourceMap = properties.getDatasource();
        return new YmlDynamicDataSourceProvider(datasourceMap);
    }

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource(DynamicDataSourceProvider dynamicDataSourceProvider) {
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
        dataSource.setPrimary(properties.getPrimary());
        dataSource.setStrict(properties.getStrict());
        dataSource.setStrategy(properties.getStrategy());
        dataSource.setProvider(dynamicDataSourceProvider);
        return dataSource;
    }

}
