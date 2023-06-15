package com.egrand.sweetapi.web.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资源字段
 */
@Data
@NoArgsConstructor
@TableName("egd_esb_api_field")
@ApiModel(value="API字段", description="")
public class ApiField {

    @ApiModelProperty(value = "接口ID")
    private Long apiId;

    @ApiModelProperty(value = "字段ID")
    private Long fieldId;

    @ApiModelProperty(value = "类型")
    private String type;

}
