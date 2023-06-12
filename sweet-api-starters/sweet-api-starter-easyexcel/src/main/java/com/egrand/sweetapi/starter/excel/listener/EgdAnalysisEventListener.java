package com.egrand.sweetapi.starter.excel.listener;

import com.alibaba.excel.event.AnalysisEventListener;
import com.egrand.sweetapi.starter.excel.model.EgdAnalysisError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class EgdAnalysisEventListener<T> extends AnalysisEventListener<T> {
    /**
     * 错误日志信息
     */
    protected List<EgdAnalysisError> errorMsgList = new ArrayList<>();

    protected void addErrorMsg(int sheetNo, Map<Integer, Map<Integer, String>> errorMsg){
        errorMsg.keySet().forEach(row -> {
            Map<Integer, String> cellValues = errorMsg.get(row);
            cellValues.keySet().forEach(col -> {
                EgdAnalysisError egdAnalysisError = new EgdAnalysisError();
                egdAnalysisError.setSheetNo(sheetNo);
                egdAnalysisError.setRow(row);
                egdAnalysisError.setCol(col);
                egdAnalysisError.setErrorMsg(cellValues.get(col));
                errorMsgList.add(egdAnalysisError);
            });
        });
    }

    public List<EgdAnalysisError> getErrorMsgList() {
        return this.errorMsgList;
    }
}
