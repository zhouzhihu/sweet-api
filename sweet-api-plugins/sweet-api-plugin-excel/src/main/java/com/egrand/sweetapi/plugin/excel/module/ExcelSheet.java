package com.egrand.sweetapi.plugin.excel.module;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.egrand.sweetapi.core.IPageResult;
import com.egrand.sweetapi.core.LocalFileServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class ExcelSheet {

    public static final Integer EXCEL_SHEET_ROW_MAX_SIZE = 1000001;

    /**
     * 页签名称
     */
    private String name;

    /**
     * 头行数
     */
    private Integer headRowNumber = 1;

    /**
     * excelModule
     */
    private ExcelModule excelModule;

    /**
     * Excel头
     */
    private LinkedList<ExcelHead> headList = new LinkedList<>();

    /**
     * Excel 模板头
     */
    private LinkedList<ExcelHead> tplHeadList = new LinkedList<>();

    /**
     * Excel数据(key:行号;value:列(列号，列值))
     */
    Map<Integer, Map<Integer, Object>> rowMap = new HashMap<>();

    /**
     * 本地文件服务
     */
    private final LocalFileServiceFactory localFileServiceFactory;

    /**
     * 模板文件路径
     */
    private String templateFilePath = "";

    public ExcelSheet(LocalFileServiceFactory localFileServiceFactory, String name, ExcelModule excelModule) {
        this.name = name;
        this.excelModule = excelModule;
        this.localFileServiceFactory = localFileServiceFactory;
    }

    /**
     * 导出Excel
     * @param fileName 文件名
     * @return
     * @throws IOException
     */
    public ResponseEntity<?> execute(String fileName) throws IOException {
        if (StrUtil.isEmpty(fileName))
            fileName = "noName" + System.currentTimeMillis() + ".xlsx";
        else
            fileName = fileName + ".xlsx";
        String tmpFilePath = this.localFileServiceFactory.getService().getTmpFilePath(fileName);
        if (StrUtil.isNotEmpty(this.templateFilePath)) {
            EasyExcelConfig.getExcelWriterBuilder(tmpFilePath, this.templateFilePath).sheet(this.name).doWrite(this.getDataList());
        } else {
            EasyExcelConfig.getExcelWriterBuilder(tmpFilePath).head(this.getHead()).sheet(this.name).doWrite(this.getDataList());
        }
        return this.excelModule.export(fileName, tmpFilePath);
    }

    /**
     * 分页导出Excel
     * @param fileName 文件名
     * @return
     * @throws IOException
     */
    public ResponseEntity<?> execute(String fileName, Function<Consumer<IPageResult>, Integer> pageFun) throws IOException {
        if (StrUtil.isEmpty(fileName))
            fileName = "noName" + System.currentTimeMillis() + ".xlsx";
        else
            fileName = fileName + ".xlsx";
        String tmpFilePath = this.localFileServiceFactory.getService().getTmpFilePath(fileName);
        ExcelWriter excelWriter;
        if (StrUtil.isNotEmpty(this.templateFilePath)) {
            excelWriter = EasyExcel.write(tmpFilePath).withTemplate(this.templateFilePath).head(this.getHead()).build();
        } else {
            excelWriter = EasyExcel.write(tmpFilePath).head(this.getHead()).build();
        }
        pageFun.apply(page -> {
            WriteSheet writeSheet = EasyExcel.writerSheet(1).build();
            writeSheet.setSheetNo((int) (page.getPage() * page.getLimit() / EXCEL_SHEET_ROW_MAX_SIZE + 1));
            writeSheet.setSheetName(this.name + writeSheet.getSheetNo());
            this.row(page.getRecords());
            excelWriter.write(this.getDataList(), writeSheet);
            this.rowMap.clear();
        });
        excelWriter.finish();
        return this.excelModule.export(fileName, tmpFilePath);
    }

    /**
     * 导入Excel
     * @param fun 回调函数
     */
    public void execute(Function<List<Map<String, Object>>, Integer> fun) {
        ExcelAnalysisEventListener listener = new ExcelAnalysisEventListener() {
            /**
             * 处理数据
             * @param dataMap
             * @return 是否成功
             */
            @Override
            Integer process(List<Map<Integer, String>> dataMap) {
                try {
                    List<Map<String, Object>> rowList = this.getRowList(dataMap);
                    if (null == rowList || rowList.size() == 0)
                        return 0;
                    return fun.apply(rowList);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    return 0;
                }
            }

            /**
             * 将Excel识别的行列数据转换为适合插入数据库的字段和值的数据结构
             * @param dataList
             * @return
             */
            public List<Map<String, Object>> getRowList(List<Map<Integer, String>> dataList) {
                if (null == dataList || dataList.size() == 0)
                    return null;
                setTplHeadFieldFromHead();
                List<Map<String, Object>> rowList = new ArrayList<>();
                dataList.forEach(dataMap -> {
                    // 构建行数据
                    Map<String, Object> rowMap = new HashMap<>();
                    dataMap.keySet().forEach(colNo -> {
                        // 构建列数据
                        // TODO 这里可以扩展一些在Excel模板中没有的，但是必须的数据。例如：创建人信息、UNID等，也可以考虑用方法扩展数据
                        // TODO 这里可以考虑字段类型转换
                        ExcelHead excelHead = getExcelHeadFromTplHead(colNo);
                        if (null != excelHead) {
                            rowMap.put(excelHead.getField(), dataMap.get(colNo));
                        }
                    });
                    rowList.add(rowMap);
                });
                return rowList;
            }
        };
        EasyExcel.read(this.templateFilePath, listener).autoTrim(true).autoCloseStream(true).headRowNumber(headRowNumber)
                .sheet(name).doRead();
    }

    /**
     * 上传模板文件，并获取模板头信息
     * @param tplFile
     * @return
     * @throws IOException
     */
    public ExcelSheet file(MultipartFile tplFile) throws IOException {
        this.templateFilePath = this.localFileServiceFactory.getService().uploadTemplateFile(tplFile);
        this.initTplHead();
        return this;
    }

    /**
     * 指定本地文件key，从远程文件服务器下载文件
     * @param key 关键字
     * @return
     * @throws IOException
     */
    public ExcelSheet file(String key) throws IOException {
        this.templateFilePath = this.localFileServiceFactory.getService().getFilePath(key);
        this.initTplHead();
        return this;
    }

    /**
     * 指定本地文件，并获取模板头信息
     * @param path 路径
     * @return
     * @throws IOException
     */
    public ExcelSheet localFile(String path) throws IOException {
        if (StrUtil.isNotEmpty(path) && new java.io.File(path).exists()) {
            this.templateFilePath = path;
        } else {
            throw new IOException("未找到[" + path + "]路径的文件！");
        }
        this.initTplHead();
        return this;
    }

    /**
     * 获取模板头
     * @return
     */
    public LinkedList<ExcelHead> getTplHeadList() {
        return tplHeadList;
    }

    /**
     * 设置Sheet名称
     * @param name 名称
     * @return
     */
    public ExcelSheet name(String name) {
        this.name = name;
        return this;
    }

    /**
     * 设置Sheet头行数
     * @param headRowNumber 头行号
     * @return
     */
    public ExcelSheet headRowNumber(Integer headRowNumber) {
        this.headRowNumber = headRowNumber;
        return this;
    }

    /**
     * 增加头
     * @param field 字段
     * @param value 列值
     * @return
     */
    public ExcelSheet head(String field, String value) {
        headList.add(ExcelHead.builder().field(field).value(value).build());
        return this;
    }

    /**
     * 增加头
     * @param heads 头集合
     * @return
     */
    public ExcelSheet head(Map<String, String> heads) {
        heads.entrySet()
                .stream()
                .filter(it -> it.getValue() != null)
                .forEach(entry -> head(entry.getKey(), entry.getValue()));
        return this;
    }

    /**
     * 增加行数据
     * @param rowNumber 行号
     * @param row 行数据
     * @return
     */
    public ExcelSheet row(Integer rowNumber, Map<String, Object> row) {
        if (null == row && row.size() == 0)
            return this;
        if (this.tplHeadList.size() != 0) {
            // 有模板
            if (this.headList.size() == 0) {
                // 没有头信息则直接组装行数据
                Map<Integer, Object> rowData = new HashMap<>();
                int col = 1;
                for (String key : row.keySet()) {
                    rowData.put(col, row.get(key));
                    col++;
                }
                this.rowMap.put(rowNumber, rowData);
            } else {
                // 有头则按模板头组装数据(有字段则取数据中值，没有字段则直接空字符串）
                this.setTplHeadFieldFromHead();
                Map<Integer, Object> rowData = new HashMap<>();
                int colNo = 0;
                for (ExcelHead excelHead : this.tplHeadList) {
                    colNo++;
                    if (row.containsKey(excelHead.getField())) {
                        rowData.put(colNo, row.get(excelHead.getField()));
                    } else {
                        rowData.put(colNo, "");
                    }
                }
                this.rowMap.put(rowNumber, rowData);
            }
        } else {
            // 无模板
            if (this.headList.size() == 0) {
                // 没有头信息则直接组装行数据
                Map<Integer, Object> rowData = new HashMap<>();
                int col = 1;
                for (String key : row.keySet()) {
                    rowData.put(col, row.get(key));
                    col++;
                }
                this.rowMap.put(rowNumber, rowData);
            } else {
                // 有头则按头的顺序组装行数据
                Map<Integer, Object> rowData = new HashMap<>();
                for (ExcelHead excelHead : headList) {
                    if (row.containsKey(excelHead.getField())) {
                        rowData.put(this.getColNumberFromHead(excelHead.getField()), row.get(excelHead.getField()));
                    }
                }
                this.rowMap.put(rowNumber, rowData);
            }
        }

        return this;
    }

    /**
     * 批量增加行数据
     * @param rows 批量行数据
     * @return
     */
    public ExcelSheet row(List<Map<String, Object>> rows) {
        if (null == rows || rows.size() == 0)
            return this;
        for (int i = 0; i < rows.size(); i++) {
            int j = i;
            row(j++, rows.get(i));
        }
        return this;
    }

    private void initTplHead() {
        ExcelAnalysisEventListener listener = new ExcelAnalysisEventListener() {
            @Override
            Integer process(List<Map<Integer, String>> dataMap) {
                // doNothing
                return 0;
            }
        };
        listener.setReadHeadOnly(true);
        EasyExcel.read(this.templateFilePath, listener).autoTrim(true).autoCloseStream(true).headRowNumber(headRowNumber)
                .doReadAll();
        // 获取头信息
        Map<Integer, Map<Integer, ExcelHead>> headMap = listener.getHeadMap();
        if (null != headMap && headMap.size() != 0) {
            // 获取最后一行，当excel存在多行头只取最后一行
            Map<Integer, ExcelHead> excelHeadMap = headMap.get(headMap.size() - 1);
            excelHeadMap.keySet().forEach(value -> this.tplHeadList.add(excelHeadMap.get(value)));
        }
    }

    /**
     * 获取头数据
     * @return
     */
    private List<List<String>> getHead() {
        // 1、没有给出头信息则返回空
        // 2、模板头不为空也返回空，以模板头为准
        if (this.headList == null || this.headList.size() == 0 || this.tplHeadList.size() != 0)
            return new ArrayList<>();
        List<List<String>> head = new ArrayList<>();
        if (null != headList && headList.size() != 0) {
            for (ExcelHead excelHead : headList) {
                List<String> col = new ArrayList<>();
                col.add(excelHead.getValue());
                head.add(col);
            }
        }
        return head;
    }

    /**
     * 获取数据
     * @return
     */
    private List<List<Object>> getDataList() {
        if (null == this.rowMap || this.rowMap.size() == 0)
            return new ArrayList<>();
        List<List<Object>> dataList = new ArrayList<>();
        this.rowMap.forEach((rowNo, row) -> {
            List<Object> rowData = new ArrayList<>();
            row.forEach((colNo, col) -> rowData.add(col));
            dataList.add(rowData);
        });
        return dataList;
    }

    /**
     * 根据给出的头信息，设置模板头字段
     */
    private void setTplHeadFieldFromHead() {
        if (this.headList.size() == 0 || this.tplHeadList.size() == 0)
            return;
        this.tplHeadList.forEach(tplHead -> {
            this.headList.forEach(head -> {
                if (head.getValue().equals(tplHead.getValue()))
                    tplHead.setField(head.getField());
            });
        });
    }

    private ExcelHead getExcelHeadFromTplHead(Integer colNo) {
        if (this.tplHeadList.size() == 0)
            return null;
        for (int i = 0; i < this.tplHeadList.size(); i++) {
            if (i == colNo.intValue()) {
                return this.tplHeadList.get(i);
            }
        }
        return null;
    }

    /**
     * 从头中获取字段所属列号
     * @param field 列名
     * @return 返回列号
     */
    private Integer getColNumberFromHead(String field) {
        int colNo = 0;
        boolean isHave = false;
        for (ExcelHead excelHead : this.headList) {
            colNo++;
            if(excelHead.getField().equals(field)) {
                isHave = true;
                break;
            }
        }
        return isHave ? colNo : 0;
    }
}
