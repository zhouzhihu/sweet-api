package com.egrand.sweetapi.starter.excel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 导入错误信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EgdAnalysisError implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页签号
     */
    private int sheetNo;

    /**
     * 行
     */
    private int row;

    /**
     * 列
     */
    private int col;

    /**
     * 错误信息
     */
    private String errorMsg;

}
