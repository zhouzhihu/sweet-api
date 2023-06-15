package com.egrand.sweetapi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.egrand.sweetapi.web.model.entity.Field;

import java.util.List;

/**
 * 字段服务类
 */
public interface FieldService extends IService<Field> {


    /**
     * 获取子字段列表
     * @param fieldId 字段ID
     * @return
     */
    List<Field> getChildren(Long fieldId);
}
