package com.egrand.sweetapi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.egrand.sweetapi.web.mapper.ApiScriptMapper;
import com.egrand.sweetapi.web.model.entity.ApiScript;
import com.egrand.sweetapi.web.service.ApiScriptService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * API脚本 服务实现类
 */
@Service
public class ApiScriptServiceImpl extends ServiceImpl<ApiScriptMapper, ApiScript> implements ApiScriptService {

    @Override
    public ApiScript loadByApiId(Long apiId) {
        QueryWrapper<ApiScript> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApiScript::getApiId, apiId);
        return this.getOne(queryWrapper);
    }

    @Override
    public Boolean deleteByApiId(Long apiId) {
        QueryWrapper<ApiScript> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApiScript::getApiId, apiId);
        return this.remove(queryWrapper);
    }

    @Override
    public Boolean saveOrUpdate(Long apiId, String script) {
        ApiScript apiScript = new ApiScript();
        apiScript.setApiId(apiId);
        apiScript.setScript(script);
        this.deleteByApiId(apiId);
        this.save(apiScript);
        return true;
    }

    @Override
    public List<Map<String, Object>> search(String keyword) {
        QueryWrapper<ApiScript> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(ApiScript::getScript, "%" + keyword + "%");
        List<ApiScript> apiScriptList = this.list(queryWrapper);
        return apiScriptList.stream()
                .filter(it -> it.getScript().contains(keyword))
                .map(it -> {
                    String script = it.getScript();
                    int index = script.indexOf(keyword);
                    int endIndex = script.indexOf("\n", index + keyword.length());
                    String text = script.substring(index, endIndex == -1 ? script.length() : endIndex);
                    index = script.lastIndexOf("\n", index) + 1;
                    return new HashMap<String, Object>() {
                        {
                            put("id", it.getApiId());
                            put("text", text);
                            put("line", 9);
                        }
                    };
                }).collect(Collectors.toList());
    }
}
