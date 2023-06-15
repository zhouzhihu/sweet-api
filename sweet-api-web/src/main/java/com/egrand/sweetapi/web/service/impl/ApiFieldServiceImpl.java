package com.egrand.sweetapi.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.egrand.sweetapi.core.model.BaseDefinition;
import com.egrand.sweetapi.core.model.RequestParamType;
import com.egrand.sweetapi.web.mapper.ApiFieldMapper;
import com.egrand.sweetapi.web.model.ApiFieldDTO;
import com.egrand.sweetapi.web.model.entity.ApiField;
import com.egrand.sweetapi.web.model.entity.Field;
import com.egrand.sweetapi.web.service.ApiFieldService;
import com.egrand.sweetapi.web.service.FieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API字段 服务实现类
 */
@Service
public class ApiFieldServiceImpl extends ServiceImpl<ApiFieldMapper, ApiField> implements ApiFieldService {

    @Autowired
    private FieldService fieldService;

    @Override
    public List<ApiFieldDTO> getApiField(Long apiId, String fieldType) {
        QueryWrapper<ApiField> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApiField::getApiId, apiId);
        if (StrUtil.isNotEmpty(fieldType)) {
            queryWrapper.lambda().eq(ApiField::getType, fieldType);
        }
        List<ApiField> apiFieldList = this.list(queryWrapper);
        if (null == apiFieldList || apiFieldList.size() == 0)
            return null;
        List<ApiFieldDTO> fieldList = new ArrayList<>();
        apiFieldList.forEach(apiField -> {
            Field field = this.fieldService.getById(apiField.getFieldId());
            if (null != field) {
                ApiFieldDTO fieldDTO = new ApiFieldDTO();
                BeanUtil.copyProperties(field, fieldDTO, true);
                fieldDTO.setApiId(apiField.getApiId());
                fieldDTO.setFieldType(apiField.getType());
                fieldList.add(fieldDTO);
            }
        });
        return fieldList;
    }

    @Override
    public List<ApiFieldDTO> getChildren(Long apiId, String paramType, Long fieldId) {
        List<Field> fieldList = this.fieldService.getChildren(fieldId);
        if (null == fieldList || fieldList.size() == 0)
            return null;
        List<ApiFieldDTO> apiFieldDTOList = new ArrayList<>();
        fieldList.forEach(field -> {
            ApiFieldDTO fieldDTO = new ApiFieldDTO();
            BeanUtil.copyProperties(field, fieldDTO, true);
            fieldDTO.setApiId(apiId);
            fieldDTO.setFieldType(paramType);
            List<ApiFieldDTO> childApiFieldDTOList = new ArrayList<>();
            this.getChild(apiId, paramType, field.getId(), childApiFieldDTOList);
            fieldDTO.setChilds(childApiFieldDTOList);
            apiFieldDTOList.add(fieldDTO);
        });
        return apiFieldDTOList;
    }

    @Override
    public void save(RequestParamType paramType, Long apiId, List<?> definitionList, boolean isSaveApiField, Long parentFieldId) {
        // 待处理子字段
        Map<String, List<BaseDefinition>> toDoBaseDefinitionMap = new HashMap<>();
        // 分析字段并保存
        List<Field> fieldList = new ArrayList<>();
        definitionList.forEach(definition -> {
            BaseDefinition baseDefinition = (BaseDefinition) definition;
            fieldList.add(this.buildField(baseDefinition, parentFieldId));
            List<BaseDefinition> childs = baseDefinition.getChildren();
            if (null != childs && childs.size() != 0) {
                toDoBaseDefinitionMap.put(baseDefinition.getName(), childs);
            }
        });
        this.fieldService.saveBatch(fieldList);
        if (isSaveApiField) {
            // 保存API和字段关系
            List<ApiField> apiFieldList = new ArrayList<>();
            fieldList.forEach(field -> {
                ApiField apiField = new ApiField();
                apiField.setApiId(apiId);
                apiField.setFieldId(field.getId());
                apiField.setType(paramType.getValue());
                apiFieldList.add(apiField);
            });
            this.saveBatch(apiFieldList);
        }
        // 处理待处理子字段
        if (toDoBaseDefinitionMap.size() != 0) {
            toDoBaseDefinitionMap.keySet().forEach(key -> {
                Long newApiId = null;
                Long parentId = null;
                for (Field field : fieldList) {
                    if (field.getName().equalsIgnoreCase(key)) {
                        newApiId = field.getId();
                        parentId = field.getId();
                    }
                }
                if (null != newApiId)
                    save(paramType, newApiId, toDoBaseDefinitionMap.get(key), false, parentId);
            });
        }
    }

    @Override
    public void deleteByApiId(Long apiId, RequestParamType paramType) {
        QueryWrapper<ApiField> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApiField::getApiId, apiId);
        if (null != paramType) {
            queryWrapper.lambda().eq(ApiField::getType, paramType.getValue());
        }
        List<ApiField> apiFieldList = this.list(queryWrapper);
        if (apiFieldList == null || apiFieldList.size() == 0)
            return;
        // 查找Field，并删除
        List<Long> fieldIdList = new ArrayList<>();
        apiFieldList.forEach(apiField -> {
            fieldIdList.add(apiField.getFieldId());
            List<ApiFieldDTO> apiFieldDTOList = this.getChildren(apiId, null, apiField.getFieldId());
            if (null != apiFieldDTOList && apiFieldDTOList.size() != 0)
                this.getApiFieldIds(apiFieldDTOList, fieldIdList);
        });
        // 删除ApiField
        this.remove(queryWrapper);
        this.fieldService.removeByIds(fieldIdList);
    }

    private void getApiFieldIds(List<ApiFieldDTO> apiFieldDTOList, List<Long> fieldIdList) {
        apiFieldDTOList.forEach(apiFieldDTO -> {
            fieldIdList.add(apiFieldDTO.getId());
            if (null != apiFieldDTO.getChilds() && apiFieldDTO.getChilds().size() != 0) {
                this.getApiFieldIds(apiFieldDTO.getChilds(), fieldIdList);
            }
        });
    }

    private Field buildField(BaseDefinition baseDefinition, Long parentId) {
        Field field = new Field();
        if (null != parentId)
            field.setParentId(parentId);
        field.setName(baseDefinition.getName());
        field.setValue(String.valueOf(baseDefinition.getValue()));
        field.setType(baseDefinition.getDataType().getValue());
        field.setRequired(baseDefinition.isRequired() ? 1 : 0);
        field.setDefaultValue(baseDefinition.getDefaultValue());
        field.setValidateType(baseDefinition.getValidateType());
        field.setExpression(baseDefinition.getExpression());
        field.setError(baseDefinition.getError());
        field.setDescription(baseDefinition.getDescription());
        return field;
    }

    private void getChild(Long apiId, String paramType, Long fieldId, List<ApiFieldDTO> childs) {
        List<Field> fieldList = this.fieldService.getChildren(fieldId);
        if (null == fieldList || fieldList.size() == 0)
            return;
        fieldList.forEach(field -> {
            ApiFieldDTO fieldDTO = new ApiFieldDTO();
            BeanUtil.copyProperties(field, fieldDTO, true);
            fieldDTO.setApiId(apiId);
            fieldDTO.setFieldType(paramType);
            List<ApiFieldDTO> childApiFieldDTOList = new ArrayList<>();
            this.getChild(apiId, paramType, field.getId(), childApiFieldDTOList);
            fieldDTO.setChilds(childApiFieldDTOList);
            childs.add(fieldDTO);
        });
    }
}
