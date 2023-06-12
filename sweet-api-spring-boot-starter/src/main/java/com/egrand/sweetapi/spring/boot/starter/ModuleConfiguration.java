package com.egrand.sweetapi.spring.boot.starter;

import com.egrand.sweetapi.core.TenantService;
import com.egrand.sweetapi.core.config.DynamicAPIProperties;
import com.egrand.sweetapi.core.interceptor.DefaultResultProvider;
import com.egrand.sweetapi.core.interceptor.ResultProvider;
import com.egrand.sweetapi.core.service.impl.RequestDynamicRegistry;
import com.egrand.sweetapi.modules.ModuleServiceFactory;
import com.egrand.sweetapi.modules.db.ColumnMapperAdapter;
import com.egrand.sweetapi.modules.db.DbModule;
import com.egrand.sweetapi.modules.db.dialect.DialectAdapter;
import com.egrand.sweetapi.modules.db.inteceptor.DefaultSqlInterceptor;
import com.egrand.sweetapi.modules.db.inteceptor.SQLInterceptor;
import com.egrand.sweetapi.modules.http.HttpModule;
import com.egrand.sweetapi.modules.plugin.PluginModule;
import com.egrand.sweetapi.modules.servlet.RequestModule;
import com.egrand.sweetapi.modules.servlet.ResponseModule;
import com.egrand.sweetapi.modules.spring.EnvModule;
import com.egrand.sweetapi.modules.sweet.SweetModule;
import com.egrand.sweetapi.starter.db.DynamicRoutingDataSource;
import com.egrand.sweetplugin.SpringPluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.multipart.MultipartResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * 模块配置
 */
public class ModuleConfiguration {

    @Autowired(required = false)
    private MultipartResolver multipartResolver;

    @Autowired
    private DynamicAPIProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public RequestModule requestModule(){
        return new RequestModule(multipartResolver);
    }

    @Bean
    @ConditionalOnMissingBean(ResultProvider.class)
    public ResultProvider resultProvider() {
        return new DefaultResultProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public ResponseModule responseModule(ResultProvider resultProvider){
        return new ResponseModule(resultProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpModule httpModule() {
        return new HttpModule();
    }

    @Bean
    @ConditionalOnMissingBean
    public PluginModule pluginModule(SpringPluginManager springPluginManager) {
        return new PluginModule(springPluginManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public SweetModule sweetModule(ModuleServiceFactory moduleServiceFactory, RequestDynamicRegistry requestDynamicRegistry) {
        return new SweetModule(moduleServiceFactory, requestDynamicRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    public EnvModule envModule(Environment environment) {
        return new EnvModule(environment);
    }

    @Bean
    @ConditionalOnMissingBean
    public DbModule dbModule(JdbcTemplate jdbcTemplate, ResultProvider resultProvider,
                             DynamicRoutingDataSource dynamicRoutingDataSource, TenantService tenantService) {
        DbModule dbModule = new DbModule(jdbcTemplate, dynamicRoutingDataSource, tenantService);
        dbModule.setResultProvider(resultProvider);
        ColumnMapperAdapter columnMapperAdapter = new ColumnMapperAdapter();
        columnMapperAdapter.setDefault(properties.getSqlColumnCase());
        List<SQLInterceptor> sqlInterceptors = new ArrayList<>();
        if (properties.isShowSql()) {
            sqlInterceptors.add(new DefaultSqlInterceptor());
        }
        dbModule.setSqlInterceptors(sqlInterceptors);
        dbModule.setColumnMapperProvider(columnMapperAdapter);
        dbModule.setColumnMapRowMapper(columnMapperAdapter.getDefaultColumnMapRowMapper());
        dbModule.setRowMapColumnMapper(columnMapperAdapter.getDefaultRowMapColumnMapper());
        DialectAdapter dialectAdapter = new DialectAdapter();
        dbModule.setDialectAdapter(dialectAdapter);
        dbModule.setLogicDeleteColumn(properties.getCrud().getLogicDeleteColumn());
        dbModule.setLogicDeleteValue(properties.getCrud().getLogicDeleteValue());
        return dbModule;
    }
}
