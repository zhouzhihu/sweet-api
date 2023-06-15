package com.egrand.sweetapi.web.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 文件管理
 */
@Data
@NoArgsConstructor
@TableName("egd_esb_file")
@ApiModel(value="文件管理", description="")
public class File {

    @TableId(value = "id")
    @ApiModelProperty("主键")
    private Long id;

    @JsonIgnore
    public boolean isNew(){
        if(null == id || -1L == id)
            return true;
        return false;
    }

    @ApiModelProperty(value = "UNID")
    private String unid;

    @ApiModelProperty(value = "父ID")
    private Long parentId;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "文件标识")
    private String code;

    @ApiModelProperty(value = "文件服务器附件ID")
    private Long fileId;

    @ApiModelProperty(value = "文件服务器附件类型")
    private String fileType;

    @ApiModelProperty(value = "文件大小(KB)")
    private BigDecimal fileSize;

    @ApiModelProperty(value = "文件服务器文件物理存储路径")
    private String filePath;

    @ApiModelProperty(value = "本地存储路径")
    private String localFilePath;

    @ApiModelProperty(value = "租户编码")
    private String tenant;

}
