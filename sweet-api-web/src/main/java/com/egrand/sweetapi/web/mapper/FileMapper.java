package com.egrand.sweetapi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.egrand.sweetapi.web.model.entity.File;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件管理Mapper 接口
 */
@Mapper
public interface FileMapper extends BaseMapper<File> {

}
