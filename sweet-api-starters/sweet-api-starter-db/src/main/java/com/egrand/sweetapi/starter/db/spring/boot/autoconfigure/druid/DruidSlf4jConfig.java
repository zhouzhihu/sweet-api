package com.egrand.sweetapi.starter.db.spring.boot.autoconfigure.druid;

import lombok.Data;

/**
 * Druid日志配置
 *
 */
@Data
public class DruidSlf4jConfig {

    private Boolean enable = true;

    private Boolean statementExecutableSqlLogEnable = false;
}