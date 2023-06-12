package com.egrand.sweetapi.plugin.excel.module;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.metadata.holder.ReadSheetHolder;
import com.egrand.sweetapi.starter.excel.listener.EgdAnalysisEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel分析时间监听器
 */
public abstract class ExcelAnalysisEventListener extends EgdAnalysisEventListener<Map<Integer, String>> {

    /**
     * 是否有错误
     */
    private boolean isError = false;

    /**
     * 是否只读取头数据
     */
    private boolean isReadHeadOnly = false;

    /**
     * 头Map
     */
    private final Map<Integer, Map<Integer, ExcelHead>> headMap = new HashMap<>();

    /**
     * 数据Map
     */
    private List<Map<Integer, String>> dataMap = new ArrayList<>();

    /**
     * 每隔100条存储数据库，实际使用中可以3000条，然后清理list ,方便内存回收
     */
    private static final int BATCH_COUNT = 100;

    @Override
    public void invokeHeadMap(Map<Integer,String> headMap, AnalysisContext context) {
        ReadSheetHolder readSheetHolder = ((ReadSheetHolder) context.currentReadHolder());
        Integer rowIndex = readSheetHolder.getRowIndex();
        Map<Integer, ExcelHead> excelHeadMap = new HashMap<>();
        headMap.forEach((colNo, value) -> {
            excelHeadMap.put(colNo, ExcelHead.builder().value(value).build());
        });
        this.headMap.put(rowIndex, excelHeadMap);
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext analysisContext) {
        if (this.isReadHeadOnly())
            return;
        this.dataMap.add(data);
        if (this.dataMap.size() >= BATCH_COUNT) {
            this.process(this.dataMap);
            this.dataMap.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (this.isReadHeadOnly())
            return;
        if (!this.dataMap.isEmpty()) {
            this.process(this.dataMap);
            this.dataMap.clear();
        }
    }

    /**
     * 处理数据
     * @param dataMap 数据
     * @return 返回处理成功条数
     */
    abstract Integer process(List<Map<Integer, String>> dataMap);

    /**
     * 获取头
     * @return
     */
    public Map<Integer, Map<Integer, ExcelHead>> getHeadMap() {
        return this.headMap;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public boolean isReadHeadOnly() {
        return isReadHeadOnly;
    }

    public void setReadHeadOnly(boolean readHeadOnly) {
        isReadHeadOnly = readHeadOnly;
    }
}
