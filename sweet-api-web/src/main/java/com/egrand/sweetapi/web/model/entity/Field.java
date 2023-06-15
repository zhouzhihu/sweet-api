package com.egrand.sweetapi.web.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资源管理
 */
@Data
@NoArgsConstructor
@TableName("egd_esb_field")
@ApiModel(value="字段", description="")
public class Field {

    @TableId(value = "id")
    @ApiModelProperty("主键")
    private Long id;

    @JsonIgnore
    public boolean isNew(){
        if(null == id || -1L == id)
            return true;
        return false;
    }

    @ApiModelProperty(value = "父ID")
    private Long parentId;

    @ApiModelProperty(value = "字段名")
    private String name;

    @ApiModelProperty(value = "字段值")
    private String value;

    @ApiModelProperty(value = "字段类型")
    private String type;

    @ApiModelProperty(value = "是否必填(0:非必填/1:必填)")
    private int required;

    @ApiModelProperty(value = "默认值")
    private String defaultValue;

    @ApiModelProperty(value = "验证类型(0:不验证;1:表达式验证;2:正则验证)")
    private String validateType;

    @ApiModelProperty(value = "验证表达式")
    private String expression;

    @ApiModelProperty(value = "验证说明")
    private String error;

    @ApiModelProperty(value = "字段是否数组(0：否/1：是)")
    private int isArray;

    @ApiModelProperty(value = "字段描述")
    private String description;

    public boolean arrayField() {
        if (this.getIsArray() == 0)
            return false;
        return true;
    }
}
