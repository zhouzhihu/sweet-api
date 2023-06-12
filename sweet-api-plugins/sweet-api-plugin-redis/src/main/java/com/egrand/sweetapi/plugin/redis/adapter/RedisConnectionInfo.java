package com.egrand.sweetapi.plugin.redis.adapter;

import com.egrand.sweetapi.core.ConnectionBaseInfo;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * Redis连接配置
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class RedisConnectionInfo extends ConnectionBaseInfo {

    public RedisConnectionInfo(ConnectionBaseInfo connectionBaseInfo) {
        this.setId(connectionBaseInfo.getId());
        this.setName(connectionBaseInfo.getName());
        this.setKey(connectionBaseInfo.getKey());
        this.setType(connectionBaseInfo.getType());
        this.setExtendConfigList(connectionBaseInfo.getExtendConfigList());
        this.setTimeout(connectionBaseInfo.getTimeout());
    }

    /**
     * 主机
     */
    private String host;

    /**
     * 端口
     */
    private String port;

    /**
     * 数据库
     */
    private String database;

    /**
     * 密码
     */
    private String password;

}
