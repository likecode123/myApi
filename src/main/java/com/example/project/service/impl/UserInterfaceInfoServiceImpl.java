package com.example.project.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.myApicommon.model.entity.UserInterfaceInfo;


import com.example.project.common.ErrorCode;
import com.example.project.exception.BusinessException;

import com.example.project.mapper.UserInterfaceInfoMapper;

import com.example.project.service.UserInterfaceInfoService;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {
    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {


        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建时进行校验
        if (add) {
            if(userInterfaceInfo.getInterfaceInfoId()<=0||userInterfaceInfo.getUserId()<=0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口调用信息不存在");
            }
        }

        if (userInterfaceInfo.getLeftNum() < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"剩余调用次数不能小于0");
        }



    }

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        if (interfaceInfoId<= 0 || userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口调用信息不存在");
        }
        UpdateWrapper<UserInterfaceInfo> updateWrapper =new UpdateWrapper<>();
        updateWrapper.eq("interfaceInfoId",interfaceInfoId);
        updateWrapper.eq("userId",userId);
        updateWrapper.setSql("leftNum = leftNum - 1,totalNum = totalNum + 1");
        return this.update(updateWrapper);
    }








    }





