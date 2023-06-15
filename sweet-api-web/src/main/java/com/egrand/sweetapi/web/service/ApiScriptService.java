package com.egrand.sweetapi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.egrand.sweetapi.web.model.entity.ApiScript;

import java.util.List;
import java.util.Map;

/**
 * Api脚本服务类
 */
public interface ApiScriptService extends IService<ApiScript> {
    /**
     * 加载ApiScript
     * @param apiId api id
     * @return
     */
    ApiScript loadByApiId(Long apiId);

    /**
     * 根据接口ID删除
     * @param apiId 接口ID
     * @return
     */
    Boolean deleteByApiId(Long apiId);

    /**
     * 根据接口ID更新脚本内容
     * @param apiId 接口ID
     * @param script 脚本内容
     * @return
     */
    Boolean saveOrUpdate(Long apiId, String script);

    /**
     * 搜索API内容
     * @param keyword 关键字
     * @return
     */
    List<Map<String, Object>> search(String keyword);
}
