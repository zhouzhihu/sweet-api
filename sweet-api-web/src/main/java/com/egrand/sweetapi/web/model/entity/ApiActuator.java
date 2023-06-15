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
@TableName("egd_esb_api_actuator")
@ApiModel(value="API执行器", description="API执行器")
public class ApiActuator {

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

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "执行用户")
    private String userName;

    @ApiModelProperty(value = "api id")
    private Long apiId;

    @ApiModelProperty(value = "执行参数(JSON格式)")
    private String config;

    @ApiModelProperty(value = "请求超时(单位：秒，0/-1代表不超时)")
    private int timeout;

}
