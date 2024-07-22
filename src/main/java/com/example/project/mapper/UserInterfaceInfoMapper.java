package com.example.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.myApicommon.model.entity.UserInterfaceInfo;

import java.util.List;


/**
 * @Entity generator.domain.UserInterfaceInfo
 */
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);
}




