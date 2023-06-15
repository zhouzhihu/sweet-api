package com.egrand.sweetapi.web.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.egrand.sweetapi.core.interceptor.ResultBody;
import com.egrand.sweetplugin.SpringPluginManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.pf4j.PluginAlreadyLoadedException;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Api(value = "插件管理", tags = "插件管理")
@RestController
@RequestMapping("/plugin")
public class PluginController {

    @Autowired
    private SpringPluginManager springPluginManager;

    @ApiOperation(value = "获取所有插件列表", notes = "获取所有插件列表")
    @GetMapping("/list")
    public ResultBody<List<JSONObject>> list(){
        List<JSONObject> pluginDescriptorList = new ArrayList<>();
        List<PluginWrapper> pluginWrapperList = this.springPluginManager.getPlugins();
        for (PluginWrapper pluginWrapper : pluginWrapperList) {
            pluginDescriptorList.add(this.parsePluginWrapper(pluginWrapper));
        }
        return ResultBody.<List<JSONObject>>ok().data(pluginDescriptorList);
    }

    @ApiOperation(value = "获取指定插件信息", notes = "获取指定插件信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "id",
            required = true,
            value = "插件ID",
            paramType = "path")
    })
    @GetMapping("/{id}")
    public ResultBody<JSONObject> get(@PathVariable String id){
        return ResultBody.<JSONObject>ok().data(this.parsePluginWrapper(this.springPluginManager.getPlugin(id)));
    }

    @ApiOperation(value = "删除插件", notes = "删除插件")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "ids",
            value = "插件id，多个用逗号隔开",
            required = true,
            dataType = "array",
            paramType = "form")
    })
    @DeleteMapping
    public ResultBody<Boolean> delete(@RequestParam("ids") List<String> ids) {
        ids.forEach(id -> this.springPluginManager.deletePlugin(id));
        return ResultBody.ok();
    }

    @ApiOperation(value = "加载插件", notes = "加载插件")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "ids",
            value = "插件id，多个用逗号隔开",
            dataType = "array",
            required = false,
            paramType = "form")
    })
    @PostMapping("/load")
    public ResultBody<Boolean> load(@RequestParam(value = "ids", required = false) List<String> ids){
        try {
            if (ids == null || ids.size() == 0) {
                this.springPluginManager.loadPlugins();
            } else {
                List<PluginWrapper> pluginWrapperList = this.springPluginManager.getPlugins();
                for (PluginWrapper pluginWrapper : pluginWrapperList) {
                    for (String id : ids) {
                        if (pluginWrapper.getPluginId().equals(id)) {
                            this.springPluginManager.loadPlugin(pluginWrapper.getPluginPath());
                        }
                    }
                }
            }
            return ResultBody.ok();
        } catch (PluginAlreadyLoadedException e) {
            return ResultBody.<Boolean>failed().msg("already loaded");
        }
    }

    @ApiOperation(value = "卸载插件", notes = "卸载插件")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "ids",
            value = "插件id，多个用逗号隔开",
            dataType = "array",
            paramType = "form")
    })
    @PostMapping("/unload")
    public ResultBody<Boolean> unload(@RequestParam("ids") List<String> ids){
        if (ids == null || ids.size() == 0) {
            this.springPluginManager.unloadPlugins();
        } else {
            ids.forEach(id -> this.springPluginManager.unloadPlugin(id));
        }
        return ResultBody.ok();
    }

    @ApiOperation(value = "启动插件", notes = "启动插件")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "ids",
            value = "插件id，多个用逗号隔开",
            dataType = "array",
            paramType = "form")
    })
    @PostMapping("/start")
    public ResultBody<Boolean> start(@RequestParam("ids") List<String> ids){
        if (ids == null || ids.size() == 0) {
            this.springPluginManager.startPlugins();
        } else {
            ids.forEach(id -> this.springPluginManager.startPlugin(id));
        }
        return ResultBody.ok();
    }

    @ApiOperation(value = "停止插件", notes = "停止插件")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "ids",
            value = "插件id，多个用逗号隔开",
            dataType = "array",
            paramType = "form")
    })
    @PostMapping("/stop")
    public ResultBody<Boolean> stop(@RequestParam("ids") List<String> ids){
        if (ids == null || ids.size() == 0) {
            this.springPluginManager.stopPlugins();
        } else {
            ids.forEach(id -> this.springPluginManager.stopPlugin(id));
        }
        return ResultBody.ok();
    }

    @ApiOperation(value = "插件上传", notes = "插件上传")
    @PostMapping("/upload")
    public ResultBody<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return ResultBody.<String>ok().data(this.springPluginManager.upload(file));
    }

//    @ApiOperation(value = "启用插件", notes = "启用插件")
//    @ApiImplicitParams({@ApiImplicitParam(
//            name = "ids",
//            value = "插件id，多个用逗号隔开",
//            dataType = "array",
//            paramType = "form")
//    })
//    @PostMapping("/enable")
//    public ResultBody<Boolean> enable(@RequestParam("ids") List<String> ids){
//        if (ids == null || ids.size() == 0) {
//            List<PluginWrapper> pluginWrapperList = this.springPluginManager.getPlugins();
//            for (PluginWrapper pluginWrapper : pluginWrapperList) {
//                this.springPluginManager.enablePlugin(pluginWrapper.getPluginId());
//            }
//        } else {
//            ids.forEach(id -> this.springPluginManager.enablePlugin(id));
//        }
//        return ResultBody.ok();
//    }
//
//    @ApiOperation(value = "禁用插件", notes = "禁用插件")
//    @ApiImplicitParams({@ApiImplicitParam(
//            name = "ids",
//            value = "插件id，多个用逗号隔开",
//            dataType = "array",
//            paramType = "form")
//    })
//    @PostMapping("/disable")
//    public ResultBody<Boolean> disable(@RequestParam("ids") List<String> ids){
//        if (ids == null || ids.size() == 0) {
//            List<PluginWrapper> pluginWrapperList = this.springPluginManager.getPlugins();
//            for (PluginWrapper pluginWrapper : pluginWrapperList) {
//                this.springPluginManager.disablePlugin(pluginWrapper.getPluginId());
//            }
//        } else {
//            ids.forEach(id -> this.springPluginManager.disablePlugin(id));
//        }
//        return ResultBody.ok();
//    }

    private JSONObject parsePluginWrapper(PluginWrapper pluginWrapper) {
        if (null == pluginWrapper)
            return new JSONObject();
        JSONObject jsonObject = JSONUtil.parseObj(pluginWrapper.getDescriptor());
        jsonObject.putOnce("pluginState", pluginWrapper.getPluginState().toString());
        jsonObject.putOnce("pluginPath", pluginWrapper.getPluginPath().toUri().getPath());
        return jsonObject;
    }
}
