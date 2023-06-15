package com.egrand.sweetapi.web.model;

import com.egrand.sweetapi.web.model.entity.Field;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "FieldDTO", description = "字段DTO")
public class FieldDTO extends Field implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "资源ID")
    private Long resourceId;

    @ApiModelProperty(value = "类型(listener|request|return|response|variable)")
    private String resourceType;
}
