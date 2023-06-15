package com.egrand.sweetapi.web.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@ApiModel(value="插件Attachment对象", description="")
public class PluginAttachment implements Serializable {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty(value = "所属文件的unid")
    private String parentUnid;

    @ApiModelProperty(value = "所属文件的type")
    private String parentType;

    @ApiModelProperty(value = "创建人ID")
    private Long authorId;

    @ApiModelProperty(value = "创建人名称")
    private String authorName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "文件类型")
    private String fileType;

    @ApiModelProperty(value = "文件大小(KB)")
    private BigDecimal fileSize;

    @ApiModelProperty(value = "文件物理存储路径")
    private String filePath;

    @ApiModelProperty(value = "文件访问URL")
    private String fileUrl;

    @ApiModelProperty(value = "文件MD5标识")
    private String fileIdentifier;

    @ApiModelProperty(value = "图片格式文件:宽度")
    private double imageWidth;

    @ApiModelProperty(value = "图片格式文件:高度")
    private double imageHeight;
}
