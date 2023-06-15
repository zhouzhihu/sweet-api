package com.egrand.sweetapi.web.controller;

import com.egrand.sweetapi.core.ApiActuatorBaseInfo;
import com.egrand.sweetapi.core.interceptor.ResultBody;
import com.egrand.sweetapi.web.service.ApiActuatorService;
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
@Api(value = "API执行器", tags = "API执行器")
@Validated
@RestController
@RequestMapping("apiActuator")
public class ApiActuatorController {

    @Autowired
    private ApiActuatorService targetService;

    @ApiOperation(value = "保存或更新实体", notes = "保存或更新实体")
    @ResponseBody
    @PostMapping(value = "/save")
    public ResultBody<Boolean> save(@RequestBody List<String> actuatorInfo) {
        return ResultBody.<Boolean>ok().data(this.targetService.save(actuatorInfo));
    }

    @ApiOperation(value = "获取指定类型执行器列表", notes = "获取指定类型执行器列表")
    @ResponseBody
    @PostMapping(value = "/list/{apiId}")
    public ResultBody<List<ApiActuatorBaseInfo>> list(@PathVariable Long apiId) {
        return ResultBody.<List<ApiActuatorBaseInfo>>ok().data(this.targetService.list(apiId, ""));
    }

    @ApiOperation(value = "删除执行器", notes = "删除执行器")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键id",required = true,paramType = "form")
    })
    @PostMapping(value = "/delete")
    public ResultBody<Boolean> del(@RequestParam(value = "id") Long id) {
        return ResultBody.<Boolean>ok().data(this.targetService.delete(id));
    }

    @ApiOperation(value = "测试执行器", notes = "测试执行器")
    @PostMapping(value = "/test")
    public ResultBody<String> run(@RequestBody String connectionInfo) {
        if (null == this.targetService.test(connectionInfo))
            return ResultBody.<String>ok().data("测试失败！");
        return ResultBody.<String>ok().data("测试成功！");
    }

}
