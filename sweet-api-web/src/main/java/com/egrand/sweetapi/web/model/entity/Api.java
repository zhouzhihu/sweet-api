package com.egrand.sweetapi.web.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API管理
 */
@Data
@NoArgsConstructor
@TableName("egd_esb_api")
@ApiModel(value="API", description="")
public class Api {

    /**
     * API类型
     */
    public static String TYPE_API = "api";

    /**
     * 文件夹类型
     */
    public static String TYPE_FOLDER = "folder";

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

    @ApiModelProperty(value = "类型(api:api;folder:文件夹)")
    private String type;

    @ApiModelProperty(value = "租户编码")
    private String tenant;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "请求方法")
    private String method;

    @ApiModelProperty(value = "请求体")
    private String requestBody;

    @ApiModelProperty(value = "输出结果")
    private String responseBody;

    @ApiModelProperty(value = "路径")
    protected String path;

    @ApiModelProperty(value = "接口描述")
    private String description;

    @ApiModelProperty(value = "排序号")
    private int orderNo;

}
