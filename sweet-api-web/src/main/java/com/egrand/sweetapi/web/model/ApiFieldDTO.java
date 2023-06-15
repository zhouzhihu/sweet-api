package com.egrand.sweetapi.web.model;

import com.egrand.sweetapi.web.model.entity.Field;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "ApiFieldDTO", description = "Api字段DTO")
public class ApiFieldDTO extends Field implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "资源ID")
    private Long apiId;

    @ApiModelProperty(value = "字段类型")
    private String fieldType;

    @ApiModelProperty(value = "子字段DTO")
    private List<ApiFieldDTO> childs;
}
