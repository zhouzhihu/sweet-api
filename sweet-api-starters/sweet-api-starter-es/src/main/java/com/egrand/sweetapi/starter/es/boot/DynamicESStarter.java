package com.egrand.sweetapi.starter.es.boot;

import com.egrand.sweetapi.starter.es.configuration.ESProperties;
import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.boot.ElasticSearchBoot;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DynamicESStarter implements InitializingBean, DisposableBean {

    /**
     * Redis配置
     */
    private final Map<String, ESProperties> esConfig;

    public DynamicESStarter(Map<String, ESProperties> esConfig) {
        if (null == esConfig)
            this.esConfig = new HashMap<>();
        else
            this.esConfig = esConfig;
    }

    @Override
    public void destroy() throws Exception {
        if (null != esConfig && esConfig.size() != 0) {
            for (Map.Entry<String, ESProperties> esItem : esConfig.entrySet()) {
                ElasticSearchHelper.stopElasticsearch(esItem.getKey());
            }
            this.esConfig.clear();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (null == esConfig || esConfig.size() == 0)
            return;
        Map properties = new HashMap();
        List<String> serverList = new ArrayList<>();
        for (Map.Entry<String, ESProperties> esItem : esConfig.entrySet()) {
            serverList.add(esItem.getKey());
            Map property = this.buildProperty(esItem.getKey(), esItem.getValue());
            property.forEach((key, value) -> properties.put(key, value));
        }
        properties.put("elasticsearch.serverNames", serverList.stream().collect(Collectors.joining(",")));
        ElasticSearchBoot.boot(properties);
    }

    /**
     * 添加ES配置
     * @param serverName 服务名
     * @param esProperties 配置
     * @return
     */
    public Boolean addES(String serverName, ESProperties esProperties) {
        Map properties = this.buildProperty(serverName, esProperties);
        properties.put("elasticsearch.serverNames", serverName);
        ElasticSearchBoot.boot(properties);
        this.esConfig.put(serverName, esProperties);
        return true;
    }

    /**
     * 删除ES配置
     * @param serverName 服务名
     * @return
     */
    public Boolean delES(String serverName) {
        ElasticSearchHelper.stopElasticsearch(serverName);
        return true;
    }

    private Map buildProperty(String serverName, ESProperties esProperties) {
        Map properties = new HashMap();

        properties.put(serverName + ".name", esProperties.getName());
        properties.put(serverName + ".elasticUser", esProperties.getElasticUser());
        properties.put(serverName + ".elasticPassword", esProperties.getElasticPassword());
        if (null != esProperties.getElasticsearch()) {
            if (null != esProperties.getElasticsearch().getRest())
                properties.put(serverName + ".elasticsearch.rest.hostNames", esProperties.getElasticsearch().getRest().getHostNames());
            if (null != esProperties.getElasticsearch().getDateFormat())
                properties.put(serverName + ".elasticsearch.dateFormat", esProperties.getElasticsearch().getDateFormat());
            if (null != esProperties.getElasticsearch().getTimeZone())
                properties.put(serverName + ".elasticsearch.timeZone", esProperties.getElasticsearch().getTimeZone());
            if (null != esProperties.getElasticsearch().getTtl())
                properties.put(serverName + ".elasticsearch.ttl", esProperties.getElasticsearch().getTtl());
            if (null != esProperties.getElasticsearch().getShowTemplate())
                properties.put(serverName + ".elasticsearch.showTemplate", esProperties.getElasticsearch().getShowTemplate());
            if (null != esProperties.getElasticsearch().getSliceScrollThreadCount())
                properties.put(serverName + ".elasticsearch.sliceScrollThreadCount", esProperties.getElasticsearch().getSliceScrollThreadCount());
            if (null != esProperties.getElasticsearch().getSliceScrollThreadQueue())
                properties.put(serverName + ".elasticsearch.sliceScrollThreadQueue", esProperties.getElasticsearch().getSliceScrollThreadQueue());
            if (null != esProperties.getElasticsearch().getSliceScrollBlockedWaitTimeout())
                properties.put(serverName + ".elasticsearch.sliceScrollBlockedWaitTimeout", esProperties.getElasticsearch().getSliceScrollBlockedWaitTimeout());
            if (null != esProperties.getElasticsearch().getHealthCheckInterval())
                properties.put(serverName + ".elasticsearch.healthCheckInterval", esProperties.getElasticsearch().getHealthCheckInterval());
            if (null != esProperties.getElasticsearch().getSlowDslThreshold())
                properties.put(serverName + ".elasticsearch.slowDslThreshold", esProperties.getElasticsearch().getSlowDslThreshold());
            if (null != esProperties.getElasticsearch().getUseHttps())
                properties.put(serverName + ".elasticsearch.useHttps", esProperties.getElasticsearch().getUseHttps());
            if (null != esProperties.getElasticsearch().getSlowDslCallback())
                properties.put(serverName + ".elasticsearch.slowDslCallback", esProperties.getElasticsearch().getSlowDslCallback());
            if (null != esProperties.getElasticsearch().getIncludeTypeName())
                properties.put(serverName + ".elasticsearch.includeTypeName", esProperties.getElasticsearch().getIncludeTypeName());
            if (null != esProperties.getElasticsearch().getScrollThreadCount())
                properties.put(serverName + ".elasticsearch.scrollThreadCount", esProperties.getElasticsearch().getScrollThreadCount());
            if (null != esProperties.getElasticsearch().getScrollThreadQueue())
                properties.put(serverName + ".elasticsearch.scrollThreadQueue", esProperties.getElasticsearch().getScrollThreadQueue());
            if (null != esProperties.getElasticsearch().getScrollBlockedWaitTimeout())
                properties.put(serverName + ".elasticsearch.scrollBlockedWaitTimeout", esProperties.getElasticsearch().getScrollBlockedWaitTimeout());
            if (null != esProperties.getElasticsearch().getDiscoverHost())
                properties.put(serverName + ".elasticsearch.discoverHost", esProperties.getElasticsearch().getDiscoverHost());
        }
        if (null != esProperties.getDslfile()) {
            if (null != esProperties.getDslfile().getRefreshInterval())
                properties.put(serverName + ".dslfile.refreshInterval", esProperties.getDslfile().getRefreshInterval());
            if (null != esProperties.getDslfile().getDslMappingDir())
                properties.put(serverName + ".dslfile.dslMappingDir", esProperties.getDslfile().getDslMappingDir());
        }
        if (null != esProperties.getHttp()) {
            if (null != esProperties.getHttp().getKeystore())
                properties.put(serverName + ".http.keystore", esProperties.getHttp().getKeystore());
            if (null != esProperties.getHttp().getKeyPassword())
                properties.put(serverName + ".http.keyPassword", esProperties.getHttp().getKeyPassword());
            if (null != esProperties.getHttp().getHostnameVerifier())
                properties.put(serverName + ".http.hostnameVerifier", esProperties.getHttp().getHostnameVerifier());
            if (null != esProperties.getHttp().getTimeoutConnection())
                properties.put(serverName + ".http.timeoutConnection", esProperties.getHttp().getTimeoutConnection());
            if (null != esProperties.getHttp().getTimeoutSocket())
                properties.put(serverName + ".http.timeoutSocket", esProperties.getHttp().getTimeoutSocket());
            if (null != esProperties.getHttp().getConnectionRequestTimeout())
                properties.put(serverName + ".http.connectionRequestTimeout", esProperties.getHttp().getConnectionRequestTimeout());
            if (null != esProperties.getHttp().getAutomaticRetriesDisabled())
                properties.put(serverName + ".http.automaticRetriesDisabled", esProperties.getHttp().getAutomaticRetriesDisabled());
            if (null != esProperties.getHttp().getBackoffAuth())
                properties.put(serverName + ".http.backoffAuth", esProperties.getHttp().getBackoffAuth());
            if (null != esProperties.getHttp().getEncodedAuthCharset())
                properties.put(serverName + ".http.encodedAuthCharset", esProperties.getHttp().getEncodedAuthCharset());
            if (null != esProperties.getHttp().getRetryTime())
                properties.put(serverName + ".http.retryTime", esProperties.getHttp().getRetryTime());
            if (null != esProperties.getHttp().getRetryInterval())
                properties.put(serverName + ".http.retryInterval", esProperties.getHttp().getRetryInterval());
            if (null != esProperties.getHttp().getMaxLineLength())
                properties.put(serverName + ".http.maxLineLength", esProperties.getHttp().getMaxLineLength());
            if (null != esProperties.getHttp().getMaxHeaderCount())
                properties.put(serverName + ".http.maxHeaderCount", esProperties.getHttp().getMaxHeaderCount());
            if (null != esProperties.getHttp().getMaxTotal())
                properties.put(serverName + ".http.maxTotal", esProperties.getHttp().getMaxTotal());
            if (null != esProperties.getHttp().getDefaultMaxPerRoute())
                properties.put(serverName + ".http.defaultMaxPerRoute", esProperties.getHttp().getDefaultMaxPerRoute());
            if (null != esProperties.getHttp().getSoReuseAddress())
                properties.put(serverName + ".http.soReuseAddress", esProperties.getHttp().getSoReuseAddress());
            if (null != esProperties.getHttp().getSoKeepAlive())
                properties.put(serverName + ".http.soKeepAlive", esProperties.getHttp().getSoKeepAlive());
            if (null != esProperties.getHttp().getTimeToLive())
                properties.put(serverName + ".http.timeToLive", esProperties.getHttp().getTimeToLive());
            if (null != esProperties.getHttp().getValidateAfterInactivity())
                properties.put(serverName + ".http.validateAfterInactivity", esProperties.getHttp().getValidateAfterInactivity());
            if (null != esProperties.getHttp().getKeystoreAlias())
                properties.put(serverName + ".http.keystoreAlias", esProperties.getHttp().getKeystoreAlias());
            if (null != esProperties.getHttp().getTrustAlias())
                properties.put(serverName + ".http.trustAlias", esProperties.getHttp().getTrustAlias());
            if (null != esProperties.getHttp().getSupportedProtocols())
                properties.put(serverName + ".http.supportedProtocols", esProperties.getHttp().getSupportedProtocols());
            if (null != esProperties.getHttp().getTruststore())
                properties.put(serverName + ".http.truststore", esProperties.getHttp().getTruststore());
            if (null != esProperties.getHttp().getTrustPassword())
                properties.put(serverName + ".http.trustPassword", esProperties.getHttp().getTrustPassword());
            if (null != esProperties.getHttp().getHttpClientBuilderCallback())
                properties.put(serverName + ".http.httpClientBuilderCallback", esProperties.getHttp().getHttpClientBuilderCallback());
            if (null != esProperties.getHttp().isStaleConnectionCheckEnabled())
                properties.put(serverName + ".http.staleConnectionCheckEnabled", esProperties.getHttp().isStaleConnectionCheckEnabled());
            if (null != esProperties.getHttp().getEvictExpiredConnections())
                properties.put(serverName + ".http.evictExpiredConnections", esProperties.getHttp().getEvictExpiredConnections());
            if (null != esProperties.getHttp().getCustomHttpRequestRetryHandler())
                properties.put(serverName + ".http.customHttpRequestRetryHandler", esProperties.getHttp().getCustomHttpRequestRetryHandler());
            if (null != esProperties.getHttp().getKeepAlive())
                properties.put(serverName + ".http.keepAlive", esProperties.getHttp().getKeepAlive());
        }
        if (null != esProperties.getDb()) {
            if (null != esProperties.getDb().getName())
                properties.put(serverName + ".db.name", esProperties.getDb().getName());
            if (null != esProperties.getDb().getUser())
                properties.put(serverName + ".db.user", esProperties.getDb().getUser());
            if (null != esProperties.getDb().getPassword())
                properties.put(serverName + ".db.password", esProperties.getDb().getPassword());
            if (null != esProperties.getDb().getDriver())
                properties.put(serverName + ".db.driver", esProperties.getDb().getDriver());
            if (null != esProperties.getDb().getUrl())
                properties.put(serverName + ".db.url", esProperties.getDb().getUrl());
            if (null != esProperties.getDb().getUsePool())
                properties.put(serverName + ".db.usePool", esProperties.getDb().getUsePool());
            if (null != esProperties.getDb().getValidateSQL())
                properties.put(serverName + ".db.validateSQL", esProperties.getDb().getValidateSQL());
            if (null != esProperties.getDb().getMaxSize())
                properties.put(serverName + ".db.maxSize", esProperties.getDb().getMaxSize());
            if (null != esProperties.getDb().getMinIdleSize())
                properties.put(serverName + ".db.minIdleSize", esProperties.getDb().getMinIdleSize());
            if (null != esProperties.getDb().getInitSize())
                properties.put(serverName + ".db.initSize", esProperties.getDb().getInitSize());
            if (null != esProperties.getDb().getShowSql())
                properties.put(serverName + ".db.showSql", esProperties.getDb().getShowSql());
            if (null != esProperties.getDb().getDbtype())
                properties.put(serverName + ".db.dbtype", esProperties.getDb().getDbtype());
            if (null != esProperties.getDb().getDbAdaptor())
                properties.put(serverName + ".db.dbAdaptor", esProperties.getDb().getDbAdaptor());
            if (null != esProperties.getDb().getJdbcFetchSize())
                properties.put(serverName + ".db.jdbcFetchSize", esProperties.getDb().getJdbcFetchSize());
            if (null != esProperties.getDb().getDatasources())
                properties.put(serverName + ".db.datasources", esProperties.getDb().getDatasources());
        }
        if (null != esProperties.getIp()) {
            if (null != esProperties.getIp().getServiceUrl())
                properties.put(serverName + ".ip.serviceUrl", esProperties.getIp().getServiceUrl());
            if (null != esProperties.getIp().getCachesize())
                properties.put(serverName + ".ip.cachesize", esProperties.getIp().getCachesize());
            if (null != esProperties.getIp().getDatabase())
                properties.put(serverName + ".ip.database", esProperties.getIp().getDatabase());
            if (null != esProperties.getIp().getAsnDatabase())
                properties.put(serverName + ".ip.asnDatabase", esProperties.getIp().getAsnDatabase());
        }
        
        return properties;
    }


}
