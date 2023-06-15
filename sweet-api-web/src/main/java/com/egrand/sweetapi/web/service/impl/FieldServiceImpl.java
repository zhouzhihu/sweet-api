package com.egrand.sweetapi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.egrand.sweetapi.web.mapper.FieldMapper;
import com.egrand.sweetapi.web.model.entity.Field;
import com.egrand.sweetapi.web.service.FieldService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字段 服务实现类
 */
@Service
public class FieldServiceImpl extends ServiceImpl<FieldMapper, Field> implements FieldService {

    @Override
    public List<Field> getChildren(Long fieldId) {
        QueryWrapper<Field> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Field::getParentId, fieldId);
        return this.list(queryWrapper);
    }
}
