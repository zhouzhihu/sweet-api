package com.egrand.sweetapi.starter.db.spring.boot.autoconfigure;

import com.egrand.sweetapi.starter.db.spring.boot.autoconfigure.druid.DruidConfig;
import com.egrand.sweetapi.starter.db.spring.boot.autoconfigure.hikari.HikariCpConfig;
import com.egrand.sweetapi.starter.db.strategy.DynamicDataSourceStrategy;
import com.egrand.sweetapi.starter.db.strategy.LoadBalanceDynamicDataSourceStrategy;
import com.egrand.sweetapi.starter.db.toolkit.CryptoUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.Ordered;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DynamicDataSourceProperties
 *
 */
@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = DynamicDataSourceProperties.PREFIX)
public class DynamicDataSourceProperties {

    public static final String PREFIX = "spring.datasource.dynamic";
    public static final String HEALTH = PREFIX + ".health";

    /**
     * 必须设置默认的库,默认master
     */
    private String primary = "master";
    /**
     * 是否启用严格模式,默认不启动. 严格模式下未匹配到数据源直接报错, 非严格模式下则使用默认数据源primary所设置的数据源
     */
    private Boolean strict = false;
    /**
     * 是否使用 spring actuator 监控检查，默认不检查
     */
    private boolean health = false;
    /**
     * 每一个数据源
     */
    private Map<String, DataSourceProperty> datasource = new LinkedHashMap<>();
    /**
     * 多数据源选择算法clazz，默认负载均衡算法
     */
    private Class<? extends DynamicDataSourceStrategy> strategy = LoadBalanceDynamicDataSourceStrategy.class;
    /**
     * aop切面顺序，默认优先级最高
     */
    private Integer order = Ordered.HIGHEST_PRECEDENCE;
    /**
     * Druid全局参数配置
     */
    @NestedConfigurationProperty
    private DruidConfig druid = new DruidConfig();
    /**
     * HikariCp全局参数配置
     */
    @NestedConfigurationProperty
    private HikariCpConfig hikari = new HikariCpConfig();

    /**
     * 全局默认publicKey
     */
    private String publicKey = CryptoUtils.DEFAULT_PUBLIC_KEY_STRING;
}
