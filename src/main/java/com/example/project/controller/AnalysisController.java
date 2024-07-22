package com.example.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.example.myApicommon.model.entity.InterfaceInfo;
import com.example.myApicommon.model.entity.UserInterfaceInfo;
import com.example.project.annotation.AuthCheck;
import com.example.project.common.BaseResponse;
import com.example.project.common.ErrorCode;
import com.example.project.common.ResultUtils;
import com.example.project.exception.BusinessException;
import com.example.project.mapper.InterfaceInfoMapper;
import com.example.project.mapper.UserInterfaceInfoMapper;
import com.example.project.model.vo.InterfaceInfoVO;
import com.example.project.service.InterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
    分析控制器
     */
@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {

    @Resource
    private InterfaceInfoService interfaceInfoService;
    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;
    @GetMapping("/top/interface/invoke")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<InterfaceInfoVO>> listTopInvokeInterfaceInfo() {
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(3);
        //将接口信息按照接口ID分组  以便于关联查询
        Map<Long, List<UserInterfaceInfo>> interfaceInfoIdObjMap = userInterfaceInfoList.stream()
                .collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));
        //{1=[UserInterfaceInfo(id=null, userId=null, interfaceInfoId=1, totalNum=8, leftNum=null,
        // status=null, createTime=null, updateTime=null, isDelete=null)],
        // 2=[UserInterfaceInfo(id=null, userId=null, interfaceInfoId=2, totalNum=26, leftNum=null
        // , status=null, createTime=null, updateTime=null, isDelete=null)],
        // 4=[UserInterfaceInfo(id=null, userId=null, interfaceInfoId=4, totalNum=46, leftNum=null, status=null, createTime=null, updateTime=null, isDelete=null)]}
//        interfaceInfoIdObjMap.keySet()         1,2,3
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper();
        queryWrapper.in("id",  interfaceInfoIdObjMap.keySet()  );
        //根据id在InterfaceInfo表查询
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
        if (CollectionUtils.isEmpty(interfaceInfoList)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"系统错误");
        }
//        List<UserInterfaceInfo> userInterfaceInfoList1 = interfaceInfoIdObjMap.get(interfaceInfoList.get(1).getId());
//        UserInterfaceInfo info = userInterfaceInfoList1.get(0);
//        System.out.println("Asdasdasdasd"+userInterfaceInfoList1);
//        System.out.println("Asdasdasdasd"+info);
        //将相关的接口信息InterfaceINfo  映射为VO对象
        List<InterfaceInfoVO> interfaceInfoVOList = interfaceInfoList.stream().map(interfaceInfo -> {
            //创建一个新的VO对象
            InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
            BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
            //找到UserInterfaceInfo表中对应的totalNum,还是得找到具备userInterface信息的参数
            //get(0)的意义在于将 1={}的map对象取值。
            Integer totalNum = interfaceInfoIdObjMap.get(interfaceInfo.getId()).get(0).getTotalNum();
            interfaceInfoVO.setTotalNum(totalNum);
            return interfaceInfoVO;
        }).collect(Collectors.toList());
        //构建接口信息VO列表
        return ResultUtils.success(interfaceInfoVOList);
    }
}
