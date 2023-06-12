package com.egrand.sweetapi.plugin.excel.module;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.egrand.sweetapi.starter.excel.converters.LocalDateConverter;
import com.egrand.sweetapi.starter.excel.converters.LocalDateTimeConverter;
import com.egrand.sweetapi.starter.excel.converters.TimestampConverter;

/**
 * easyExcel数据写入对象数据封装
 */
public class EasyExcelConfig {

    /**
     * 获取ExcelWriterBuilder
     * @param writePath 写入文件路径
     * @return
     */
    public static ExcelWriterBuilder getExcelWriterBuilder(String writePath) {
        ExcelWriterBuilder write = null;
        try {
            // 数据导出
            write = EasyExcel.write(writePath);
            // LocalDate 解析对象
            write.registerConverter(new LocalDateConverter());
            // LocalDateTime 解析对象
            write.registerConverter(new LocalDateTimeConverter());
            // TimeStamp 解析对象
            write.registerConverter(new TimestampConverter());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return write;
    }

    /**
     * 获取带模板的ExcelWriterBuilder
     * @param writePath 写入文件路径
     * @param tplPath 模板文件路径
     * @return
     */
    public static ExcelWriterBuilder getExcelWriterBuilder(String writePath, String tplPath) {
        return getExcelWriterBuilder(writePath).withTemplate(tplPath);
    }
}
