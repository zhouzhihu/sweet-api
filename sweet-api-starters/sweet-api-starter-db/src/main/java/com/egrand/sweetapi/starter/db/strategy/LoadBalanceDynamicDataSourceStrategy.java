package com.egrand.sweetapi.starter.db.strategy;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * LoadBalance strategy to switch a database
 *
 */
public class LoadBalanceDynamicDataSourceStrategy implements DynamicDataSourceStrategy {

    /**
     * 负载均衡计数器
     */
    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public DataSource determineDataSource(List<DataSource> dataSources) {
        return dataSources.get(Math.abs(index.getAndAdd(1) % dataSources.size()));
    }
}
