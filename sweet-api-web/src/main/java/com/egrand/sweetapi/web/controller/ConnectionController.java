package com.egrand.sweetapi.web.controller;

import com.egrand.sweetapi.core.ConnectionBaseInfo;
import com.egrand.sweetapi.core.interceptor.ResultBody;
import com.egrand.sweetapi.web.service.ConnectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 连接 前端控制器
 */
@Api(value = "连接", tags = "连接")
@Validated
@RestController
@RequestMapping("connection")
public class ConnectionController {
    
    @Autowired
    private ConnectionService targetService;

    @ApiOperation(value = "保存或更新实体", notes = "保存或更新实体")
    @ResponseBody
    @PostMapping(value = "/save")
    public ResultBody<ConnectionBaseInfo> save(@RequestBody String connectionInfo){
        return ResultBody.<ConnectionBaseInfo>ok().data(this.targetService.save(connectionInfo));
    }

    @ApiOperation(value = "获取指定类型连接列表", notes = "获取指定类型连接列表")
    @ResponseBody
    @PostMapping(value = "/list")
    public ResultBody<List<ConnectionBaseInfo>> list(){
        return ResultBody.<List<ConnectionBaseInfo>>ok().data(this.targetService.list(""));
    }

    @ApiOperation(value = "删除连接", notes = "删除连接")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键id",required = true,paramType = "form")
    })
    @PostMapping(value = "/delete")
    public ResultBody<Boolean> del(@RequestParam(value = "id") Long id) {
        return ResultBody.<Boolean>ok().data(this.targetService.delete(id));
    }

    @ApiOperation(value = "测试连接", notes = "测试连接")
    @PostMapping(value = "/test")
    public ResultBody<String> run(@RequestBody String connectionInfo) {
        if (null == this.targetService.test(connectionInfo))
            return ResultBody.<String>ok().data("测试失败！");
        return ResultBody.<String>ok().data("测试成功！");
    }

    @ApiOperation(value = "同步所有数据源到本地缓存中", notes = "同步所有数据源到本地缓存中")
    @GetMapping(value = "/sync")
    public ResultBody<Boolean> sync() {
        return ResultBody.<Boolean>ok().data(this.targetService.sync());
    }

}
