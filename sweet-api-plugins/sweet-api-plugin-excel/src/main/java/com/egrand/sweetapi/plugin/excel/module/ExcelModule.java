package com.egrand.sweetapi.plugin.excel.module;

import cn.hutool.core.io.FileUtil;
import com.egrand.sweetapi.core.LocalFileServiceFactory;
import com.egrand.sweetapi.core.ModuleService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExcelModule implements ModuleService {

    private final LocalFileServiceFactory localFileServiceFactory;

    public ExcelModule(LocalFileServiceFactory localFileServiceFactory) {
        this.localFileServiceFactory = localFileServiceFactory;
    }

    public ExcelSheet sheet(String sheetName) {
        return new ExcelSheet(localFileServiceFactory, sheetName, this);
    }

    public ResponseEntity<?> export(String fileName, String filePath) throws IOException {
        InputStream is = FileUtil.getInputStream(filePath);
        //创建字节数组
        byte[] buffer = new byte[is.available()];
        //将流读到字节数组中
        is.read(buffer);
        is.close();
        // 删除临时文件
        FileUtil.del(filePath);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("filename", URLEncoder.encode(fileName, "UTF-8"))
                .header(HttpHeaders.LAST_MODIFIED, new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss Z", Locale.ENGLISH).format(new Date()) + " GMT")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization,filename")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"))
                .body(buffer);
    }

    @Override
    public String getType() {
        return "excel";
    }
}
