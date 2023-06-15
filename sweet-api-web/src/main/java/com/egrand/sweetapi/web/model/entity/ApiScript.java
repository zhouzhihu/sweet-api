package com.egrand.sweetapi.web.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API脚本管理
 */
@Data
@NoArgsConstructor
@TableName("egd_esb_api_script")
@ApiModel(value="ApiScript", description="")
public class ApiScript {

    @ApiModelProperty(value = "api id")
    private Long apiId;

    @ApiModelProperty(value = "脚本")
    private String script;

}
