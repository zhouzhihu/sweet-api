package com.egrand.sweetapi.web.model;

import com.egrand.sweetapi.core.model.BaseDefinition;
import com.egrand.sweetapi.core.model.Header;
import com.egrand.sweetapi.core.model.Parameter;
import com.egrand.sweetapi.core.model.Path;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ApiSaveDTO", description = "API保存DTO")
public class ApiSaveDTO implements Serializable {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "父ID")
    private Long parentId;

    @ApiModelProperty(value = "类型(api:api;folder:文件夹)")
    private String type;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "请求方法")
    private String method;

    @ApiModelProperty(value = "路径")
    protected String path;

    @ApiModelProperty(value = "请求体")
    private String requestBody;

    @ApiModelProperty(value = "输出结果")
    private String responseBody;

    @ApiModelProperty(value = "脚本")
    protected String script;

    @ApiModelProperty(value = "接口描述")
    private String description;

    @ApiModelProperty(value = "设置的请求参数")
    private List<Parameter> parameters;

    @ApiModelProperty(value = "请求头")
    private List<Header> headers;

    @ApiModelProperty(value = "路径变量")
    private List<Path> paths;

    @ApiModelProperty(value = "请求体属性")
    private BaseDefinition requestBodyDefinition;

    @ApiModelProperty(value = "输出结果属性")
    private List<BaseDefinition> responseBodyDefinition;
}
