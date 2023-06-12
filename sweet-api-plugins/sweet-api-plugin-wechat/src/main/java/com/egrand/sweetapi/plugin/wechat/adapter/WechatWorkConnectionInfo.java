package com.egrand.sweetapi.plugin.wechat.adapter;

import com.egrand.sweetapi.core.ConnectionBaseInfo;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * WechatWork连接配置
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class WechatWorkConnectionInfo extends ConnectionBaseInfo {

    public WechatWorkConnectionInfo(ConnectionBaseInfo connectionBaseInfo) {
        this.setId(connectionBaseInfo.getId());
        this.setName(connectionBaseInfo.getName());
        this.setKey(connectionBaseInfo.getKey());
        this.setType(connectionBaseInfo.getType());
        this.setExtendConfigList(connectionBaseInfo.getExtendConfigList());
        this.setTimeout(connectionBaseInfo.getTimeout());
    }

    /**
     * 设置企业微信的corpId
     */
    private String corpId;

    /**
     * 设置企业微信应用的AgentId
     */
    private Integer agentId;

    /**
     * 设置企业微信应用的Secret
     */
    private String secret;

    /**
     * 应用中的 “接受消息” 部分的 “接收消息服务器配置” 里的Token值
     */
    private String token;

    /**
     * 应用中的 “接受消息” 部分的 “接收消息服务器配置” 里的EncodingAESKey值
     */
    private String aesKey;

}
