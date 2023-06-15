package com.egrand.sweetapi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.egrand.sweetapi.core.model.RequestParamType;
import com.egrand.sweetapi.web.model.ApiFieldDTO;
import com.egrand.sweetapi.web.model.entity.ApiField;

import java.util.List;

/**
 * API字段服务类
 */
public interface ApiFieldService extends IService<ApiField> {
    /**
     * 获取Api包含字段
     * @param apiId ApiId
     * @param fieldType 类型
     * @return
     */
    List<ApiFieldDTO> getApiField(Long apiId, String fieldType);

    /**
     * 获取子字段树
     * @param apiId ApiId
     * @param paramType 类型
     * @param fieldId 字段ID
     * @return
     */
    List<ApiFieldDTO> getChildren(Long apiId, String paramType, Long fieldId);

    /**
     * 保存字段
     * @param paramType 字段类型
     * @param apiId 所属API
     * @param definitionList 字段列表
     * @param isSaveApiField 是否保存API和字段关系表
     * @param parentFieldId 父字段ID
     */
    void save(RequestParamType paramType, Long apiId, List<?> definitionList, boolean isSaveApiField, Long parentFieldId);

    /**
     * 根据API ID删除所有字段
     * @param apiId
     */
    void deleteByApiId(Long apiId, RequestParamType paramType);
}
