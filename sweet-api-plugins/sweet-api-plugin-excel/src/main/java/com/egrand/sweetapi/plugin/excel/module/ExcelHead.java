package com.egrand.sweetapi.plugin.excel.module;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ExcelHead implements Serializable {
    // TODO 待完善字段类型设置，还有字段校验

    /**
     * 字段名
     */
    private String field;

    /**
     * 中文描述
     */
    private String value;
}
