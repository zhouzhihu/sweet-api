package com.egrand.sweetapi.web.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.egrand.sweetapi.core.ApiActuatorBaseInfo;
import com.egrand.sweetapi.web.mapper.ApiActuatorMapper;
import com.egrand.sweetapi.web.model.entity.ApiActuator;
import com.egrand.sweetapi.web.service.ApiActuatorService;
import com.egrand.sweetapi.web.service.factory.ApiActuatorAdapteServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.egrand.sweetapi.web.utils.Assert.isFalse;

/**
 * 执行器 服务实现类
 */
@Service
public class ApiActuatorServiceImpl extends ServiceImpl<ApiActuatorMapper, ApiActuator> implements ApiActuatorService {

    @Autowired
    private ApiActuatorAdapteServiceFactory apiActuatorAdapteServiceFactory;

    @Override
    public ApiActuatorBaseInfo test(String actuatorInfo) {
        isFalse(StrUtil.isEmpty(actuatorInfo), "输入信息为空，请核对！");
        JSONObject jsonObject = JSONUtil.parseObj(actuatorInfo);
        isFalse(!jsonObject.containsKey("id"), "ID不能为空，请核对！");
        isFalse(!jsonObject.containsKey("name"), "名称不能为空，请核对！");
        isFalse(!jsonObject.containsKey("type"), "未指定执行器类型，请核对！");
        isFalse(!jsonObject.containsKey("key"), "编码不能为空，请核对！");
        isFalse(!jsonObject.containsKey("apiId"), "接口不能为空，请核对！");
        return apiActuatorAdapteServiceFactory.test(jsonObject.getStr("type"), actuatorInfo);
    }

    @Override
    public Boolean save(List<String> actuatorInfos) {
        if (null != actuatorInfos && actuatorInfos.size() != 0) {
            actuatorInfos.forEach(actuatorInfo -> this.save(actuatorInfo));
        }
        return true;
    }

    @Override
    public ApiActuatorBaseInfo save(String actuatorInfo) {
        ApiActuatorBaseInfo apiActuatorBaseInfo = this.test(actuatorInfo);
        isFalse(null == apiActuatorBaseInfo, "测试执行器时失败，请核对！");
        ApiActuator apiActuator = new ApiActuator();
        this.apiActuatorAdapteServiceFactory.encode(apiActuator, apiActuatorBaseInfo);
        if (apiActuator.isNew()) {
            isFalse(this.isHaveActuator(apiActuator.getCode()),
                    "存在【" + apiActuator.getCode() + "】执行器，请核对！");
            this.apiActuatorAdapteServiceFactory.save(apiActuatorBaseInfo);
            // 保存执行器
            this.save(apiActuator);
        } else {
            ApiActuator oldApiActuator = this.getById(apiActuator.getId());
            isFalse(null == oldApiActuator, "未找到对应执行器，无法更新，请核对！");
            this.apiActuatorAdapteServiceFactory.update(apiActuatorBaseInfo, this.apiActuatorAdapteServiceFactory.decode(oldApiActuator));
            // 更新执行器
            this.updateById(apiActuator);
        }
        return this.apiActuatorAdapteServiceFactory.decode(apiActuator);
    }

    @Override
    public List<ApiActuatorBaseInfo> list(Long apiId, String type) {
        QueryWrapper<ApiActuator> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApiActuator::getApiId, apiId);
        if (StrUtil.isNotEmpty(type))
            queryWrapper.lambda().eq(ApiActuator::getType, type);
        List<ApiActuator> apiActuatorList = this.list(queryWrapper);
        List<ApiActuatorBaseInfo> apiActuatorBaseInfoList = new ArrayList<>();
        apiActuatorList.forEach(apiActuator -> apiActuatorBaseInfoList.add(this.apiActuatorAdapteServiceFactory.decode(apiActuator)));
        return apiActuatorBaseInfoList;
    }

    @Override
    public List<ApiActuatorBaseInfo> listAll(String type) {
        QueryWrapper<ApiActuator> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(type))
            queryWrapper.lambda().eq(ApiActuator::getType, type);
        List<ApiActuator> apiActuatorList = this.list(queryWrapper);
        List<ApiActuatorBaseInfo> apiActuatorBaseInfoList = new ArrayList<>();
        apiActuatorList.forEach(apiActuator -> apiActuatorBaseInfoList.add(this.apiActuatorAdapteServiceFactory.decode(apiActuator)));
        return apiActuatorBaseInfoList;
    }

    @Override
    public boolean delete(Serializable id) {
        ApiActuator apiActuator = this.getById(id);
        isFalse(null == apiActuator, "未找到对应执行器，无法删除，请核对！");
        this.apiActuatorAdapteServiceFactory.delete(apiActuator.getType(), this.apiActuatorAdapteServiceFactory.decode(apiActuator));
        return super.removeById(id);
    }

    /**
     * 判断是否存在相同的key
     * @param key key
     * @return
     */
    private Boolean isHaveActuator(String key) {
        QueryWrapper<ApiActuator> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApiActuator::getCode, key);
        return this.count(queryWrapper) > 0;
    }
}
