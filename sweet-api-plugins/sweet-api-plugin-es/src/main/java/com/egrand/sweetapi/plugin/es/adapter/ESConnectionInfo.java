package com.egrand.sweetapi.plugin.es.adapter;

import com.egrand.sweetapi.core.ConnectionBaseInfo;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * ES连接配置
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class ESConnectionInfo extends ConnectionBaseInfo {

    public ESConnectionInfo(ConnectionBaseInfo connectionBaseInfo) {
        this.setId(connectionBaseInfo.getId());
        this.setName(connectionBaseInfo.getName());
        this.setKey(connectionBaseInfo.getKey());
        this.setType(connectionBaseInfo.getType());
        this.setExtendConfigList(connectionBaseInfo.getExtendConfigList());
        this.setTimeout(connectionBaseInfo.getTimeout());
    }

    /**
     * 用户名
     */
    private String elasticUser;

    /**
     * 密码
     */
    private String elasticPassword;

    /**
     * 主机名
     */
    private String hostNames;

}
