package com.egrand.sweetapi.web.controller;

import com.egrand.sweetapi.core.ApiActuatorService;
import com.egrand.sweetapi.core.interceptor.ResultBody;
import com.egrand.sweetapi.core.model.ApiInfo;
import com.egrand.sweetapi.modules.servlet.ResponseModule;
import com.egrand.sweetapi.web.model.ApiSaveDTO;
import com.egrand.sweetapi.web.model.ResourceDTO;
import com.egrand.sweetapi.web.model.entity.ApiScript;
import com.egrand.sweetapi.web.service.ApiScriptService;
import com.egrand.sweetapi.web.service.ApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.script.ScriptException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Api 前端控制器
 */
@Api(value = "API", tags = "API")
@Validated
@RestController
@RequestMapping("api")
public class ApiController {

    @Autowired
    private ApiService targetService;

    @Autowired
    private ApiScriptService apiScriptService;

    @Autowired
    private ResponseModule responseModule;

    @Autowired
    private ApiActuatorService apiActuatorService;

    @ApiOperation(value = "保存实体", notes = "保存实体")
    @ResponseBody
    @PostMapping
    public ResultBody<com.egrand.sweetapi.web.model.entity.Api> save(@RequestBody @Validated ApiSaveDTO apiSaveDTO) {
        return ResultBody.<com.egrand.sweetapi.web.model.entity.Api>ok().data(this.targetService.save(apiSaveDTO));
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

    @ApiOperation(value = "加载API信息", notes = "加载API信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "实体ID", paramType = "path")
    })
    @ResponseBody
    @GetMapping("/list/{type}")
    public ResultBody<List<com.egrand.sweetapi.web.model.entity.Api>> getApiInfo(@PathVariable String type) {
        return ResultBody.<List<com.egrand.sweetapi.web.model.entity.Api>>ok().data(this.targetService.listByType(type));
    }

    @ApiOperation(value = "根据ID查找子API数据", notes = "根据ID查找子API数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", paramType = "form")
    })
    @ResponseBody
    @GetMapping("/children")
    public ResultBody<List<com.egrand.sweetapi.web.model.entity.Api>> listByParentId(@RequestParam(value = "id", required = false) Long id) {
        return ResultBody.<List<com.egrand.sweetapi.web.model.entity.Api>>ok().data(this.targetService.listByParentId(id));
    }

    @ApiOperation(value = "加载API信息", notes = "加载API信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "实体ID", paramType = "path")
    })
    @ResponseBody
    @GetMapping("/{id}/info")
    public ResultBody<ApiInfo> getApiInfo(@PathVariable Long id) {
        return ResultBody.<ApiInfo>ok().data(this.targetService.getApiInfo(id, false));
    }

    @ApiOperation(value = "加载API执行脚本", notes = "加载API执行脚本")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "实体ID", paramType = "path")
    })
    @ResponseBody
    @GetMapping("/{id}/script")
    public ResultBody<ApiScript> getScript(@PathVariable Long id) {
        return ResultBody.<ApiScript>ok().data(this.apiScriptService.loadByApiId(id));
    }

    @ApiOperation(value = "更新/保存API执行脚本", notes = "更新/保存API执行脚本")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "实体ID", paramType = "path")
    })
    @ResponseBody
    @PostMapping("/{id}/script")
    public ResultBody<Boolean> updateScript(@PathVariable Long id, @RequestBody String script) {
        return ResultBody.<Boolean>ok().data(this.apiScriptService.saveOrUpdate(id, script));
    }

    @ApiOperation(value = "移动API", notes = "移动API")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "实体ID", paramType = "path")
    })
    @ResponseBody
    @PostMapping("/{id}/move")
    public ResultBody<Boolean> move(@PathVariable Long id, @RequestParam(value = "groupId") Long groupId) {
        return ResultBody.<Boolean>ok().data(this.targetService.move(id, groupId));
    }

    @ApiOperation(value = "导出", notes = "导出")
    @ResponseBody
    @PostMapping("/export")
    public ResponseEntity<?> export(@RequestBody(required = false) List<ResourceDTO> resources) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        this.targetService.export(resources, os);
        return responseModule.download(os.toByteArray(), "sweet-api-group.zip");
    }

    @ApiOperation(value = "导入", notes = "导入")
    @PostMapping("/upload")
    public ResultBody<Boolean> upload(@RequestParam("file") MultipartFile file, @RequestParam(value = "mode") String mode) throws IOException {
        return ResultBody.<Boolean>ok().data(this.targetService.upload(file.getInputStream(), mode));
    }

    @ApiOperation(value = "全文检索API", notes = "全文检索API")
    @PostMapping("/search")
    public ResultBody<List<Map<String, Object>>> search(@RequestParam(value = "keyword") String keyword) {
        return ResultBody.<List<Map<String, Object>>>ok().data(this.targetService.search(keyword));
    }

    @ApiOperation(value = "同步API接口", notes = "同步API接口")
    @GetMapping("/sync")
    public ResultBody<Boolean> sync() {
        return ResultBody.<Boolean>ok().data(this.targetService.sync());
    }
}
