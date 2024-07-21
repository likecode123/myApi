package com.example.project.service.impl.inner;

import com.example.myApicommon.model.entity.UserInterfaceInfo;
import com.example.myApicommon.service.InnerUserInterfaceInfoService;
import com.example.project.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {
    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean b) {

    }

    @Override
    public boolean invokeCount(long l, long l1) {
        return userInterfaceInfoService.invokeCount(l,l1);
    }
}
