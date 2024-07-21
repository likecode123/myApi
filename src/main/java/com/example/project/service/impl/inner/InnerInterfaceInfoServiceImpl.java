package com.example.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.myApicommon.model.entity.InterfaceInfo;
import com.example.myApicommon.service.InnerInterfaceInfoService;
import com.example.project.common.ErrorCode;
import com.example.project.exception.BusinessException;
import com.example.project.mapper.InterfaceInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 根据url和请求方法获取内部接口信息,参考示例 添加DubboService注解
 */
@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {
    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;
    @Override
    public InterfaceInfo getInterfaceInfo(String path, String method) {
        if (StringUtils.isAnyBlank(path,method)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper queryWrapper =new QueryWrapper();
        queryWrapper.eq("url",path);
        queryWrapper.eq("method",method);

        return interfaceInfoMapper.selectOne(queryWrapper);
    }
}
