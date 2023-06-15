package com.egrand.sweetapi.web.controller;

import com.egrand.sweetapi.core.LocalFileInfo;
import com.egrand.sweetapi.core.interceptor.ResultBody;
import com.egrand.sweetapi.web.model.FileSaveDTO;
import com.egrand.sweetapi.web.model.entity.File;
import com.egrand.sweetapi.web.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 文件管理 前端控制器
 */
@Api(value = "File", tags = "File")
@Validated
@RestController
@RequestMapping("file")
public class FileController {
    
    @Autowired
    private FileService targetService;
    
    @ApiOperation(value = "保存实体", notes = "保存实体")
    @ResponseBody
    @PostMapping
    public ResultBody<File> save(@RequestBody @Validated FileSaveDTO fileSaveDTO) {
        return ResultBody.<File>ok().data(this.targetService.save(fileSaveDTO));
    }

    @ApiOperation(value = "批量删除数据", notes = "批量删除数据")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "ids",
            value = "主键id，多个用逗号隔开",
            required = true,
            dataType = "array",
            paramType = "form")
    })
    @DeleteMapping
    public ResultBody<Boolean> delete(@RequestParam("ids") List<Long> ids) {
        return ResultBody.<Boolean>ok().data(this.targetService.delete(ids));
    }

    @ApiOperation(value = "文件上传", notes = "文件上传")
    @PostMapping("/upload")
    public ResultBody<LocalFileInfo> upload(@RequestParam("file") MultipartFile file, @RequestParam("parentId") Long parentId,
                                            @RequestParam("code") String code) throws IOException {
        return ResultBody.<LocalFileInfo>ok().data(this.targetService.upload(file, parentId, code));
    }

    @ApiOperation(value = "下载文件", notes = "下载文件")
    @GetMapping("/download")
    public ResponseEntity<?> download(@RequestParam("code") String code) throws IOException {
        return this.targetService.download(code);
    }

    @ApiOperation(value = "查询子文件对象", notes = "查询子文件对象")
    @GetMapping("/children")
    public ResultBody<List<File>> children(@RequestParam("parentId") Long parentId) {
        return ResultBody.<List<File>>ok().data(this.targetService.listChildren(parentId));
    }
}
