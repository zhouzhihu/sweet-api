package com.egrand.sweetapi.starter.db.spring.boot.autoconfigure.druid;

import lombok.Data;

/**
 * Druid监控配置
 *
 */
@Data
public class DruidStatConfig {

    private Long slowSqlMillis;

    private Boolean logSlowSql;

    private Boolean mergeSql;
}