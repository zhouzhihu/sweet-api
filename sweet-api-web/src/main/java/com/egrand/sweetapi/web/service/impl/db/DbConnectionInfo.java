package com.egrand.sweetapi.web.service.impl.db;

import com.egrand.sweetapi.core.ConnectionBaseInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "DbConnectionDTO", description = "DB连接配置")
public class DbConnectionInfo extends ConnectionBaseInfo {

    public DbConnectionInfo(ConnectionBaseInfo connectionBaseInfo) {
        this.setId(connectionBaseInfo.getId());
        this.setName(connectionBaseInfo.getName());
        this.setKey(connectionBaseInfo.getKey());
        this.setType(connectionBaseInfo.getType());
        this.setExtendConfigList(connectionBaseInfo.getExtendConfigList());
        this.setTimeout(connectionBaseInfo.getTimeout());
    }

    @ApiModelProperty(value = "账号")
    private String userName;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "数据库连接URL")
    private String url;

    @ApiModelProperty(value = "驱动类")
    private String driverClassName;

    @ApiModelProperty(value = "数据源类型")
    private String dataSourceType;

    @ApiModelProperty(value = "最大行")
    private int maxRows;

}
