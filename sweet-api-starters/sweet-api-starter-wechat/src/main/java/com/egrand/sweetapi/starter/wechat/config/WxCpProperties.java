package com.egrand.sweetapi.starter.wechat.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = WxCpProperties.PREFIX)
public class WxCpProperties {

  public static final String PREFIX = "spring.wechat.cp";

  /**
   * 默认连接必须配置，配置 key 为 default
   */
  public static final String DEFAULT = "default";

  /**
   * 是否启用多Redis配置
   */
  private boolean enableMulti = false;

  /**
   * 多Redis配置
   */
  private Map<String, AppConfig> multi;

  @Getter
  @Setter
  public static class AppConfig {

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
     * 设置企业微信应用的token
     */
    private String token;

    /**
     * 设置企业微信应用的EncodingAESKey
     */
    private String aesKey;

  }

}
