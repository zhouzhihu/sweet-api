package com.egrand.sweetapi.web.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 连接管理
 */
@Data
@NoArgsConstructor
@TableName("egd_esb_connection")
@ApiModel(value="连接管理", description="")
public class Connection {

    @TableId(value = "id")
    @ApiModelProperty("主键")
    private Long id;

    @JsonIgnore
    public boolean isNew(){
        if(null == id || -1L == id)
            return true;
        return false;
    }

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "连接配置(JSON格式)")
    private String config;

    @ApiModelProperty(value = "扩展配置(JSON格式)")
    private String extendConfig;

    @ApiModelProperty(value = "请求超时(单位：秒，0/-1代表不超时)")
    private int timeout;

    @ApiModelProperty(value = "租户编码")
    private String tenant;

}
