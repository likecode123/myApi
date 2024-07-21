package com.example.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.example.myApicommon.model.entity.InterfaceInfo;
import com.example.myApicommon.model.entity.User;

import com.example.myapiclientsdk.client.MyAPIClient;
import com.google.gson.Gson;
import com.example.project.annotation.AuthCheck;
import com.example.project.common.BaseResponse;
import com.example.project.common.DeleteRequest;
import com.example.project.common.IdRequest;
import com.example.project.common.ErrorCode;
import com.example.project.common.ResultUtils;
import com.example.project.constant.CommonConstant;
import com.example.project.exception.BusinessException;
import com.example.project.model.dto.interfaceInfo.InterfaceInfoAddRequest;
import com.example.project.model.dto.interfaceInfo.InterfaceInfoInvokeRequest;
import com.example.project.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.example.project.model.dto.interfaceInfo.InterfaceInfoUpdateRequest;


import com.example.project.model.enums.InterfaceInfoStatusEnum;
import com.example.project.service.InterfaceInfoService;
import com.example.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 接口管理
 *
 * @author liukai
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private MyAPIClient myAPIClient;

    // region 增删改查

    /**
     * 创建
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newInterfaceInfoId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param interfaceInfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
                                            HttpServletRequest request) {

        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }


    /**
     * 发布
     *
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/online")
    //必须管理员，权限校验切面注解，它对应的实现方法在这里。
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest,
                                                     HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //检验接口是否存在
        Long id = idRequest.getId();
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        //如果查询结果为空
        if (oldInterfaceInfo==null){
            //抛出异常
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
//       2 .//检验接口是否可以被调用

        // 2.判断该接口是否可以调用
        // 创建一个User对象(这里先模拟一下，搞个假数据)
        com.example.myapiclientsdk.model.User user = new com.example.myapiclientsdk.model.User();
        // 设置user对象的username属性为"test"
        user.setUsername("onlineTest");
        // 通过myApiClient的getUsernameByPost方法传入user对象，并将返回的username赋值给username变量
        String username = myAPIClient.getUserNameByPost(user);
        // 如果username为空或空白字符串
        if (StringUtils.isBlank(username)) {
            // 抛出系统错误的业务异常，表示系统内部异常，并附带错误信息"接口验证失败"
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口验证失败");
        }
        // 创建一个InterfaceInfo对象
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        // 设置interfaceInfo的id属性为id
        interfaceInfo.setId(id);
        // 3.修改接口数据库中的状态字段为 1
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        // 调用service层的方法，传入interfaceInfo对象，并传入user对象
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);

    }



    /**
     * 下线
     *
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/offline")
    //必须管理员，权限校验切面注解，它对应的实现方法在这里。
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest,
                                                                  HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //检验接口是否存在
        Long id = idRequest.getId();
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        //如果查询结果为空
        if (oldInterfaceInfo==null){
            //抛出异常
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
//       2 .//检验接口是否可以被调用,在这里不需要


        // 创建一个InterfaceInfo对象
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        // 设置interfaceInfo的id属性为id
        interfaceInfo.setId(id);
        // 3.修改接口数据库中的状态字段为 1
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());
        // 调用service层的方法，传入interfaceInfo对象，并传入user对象
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }


    /**
     *  调用接口
     *
     * @param invokeRequest
     * @param request
     * @return
     */
    @PostMapping("/invoke")
    //必须管理员，权限校验切面注解，它对应的实现方法在这里。
    public BaseResponse<Object> InterfaceInfoInvokeRequest(@RequestBody InterfaceInfoInvokeRequest invokeRequest,
                                                      HttpServletRequest request) {
        if (invokeRequest == null || invokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //检验接口是否存在  获取接口id
        Long id = invokeRequest.getId();
        //获取用户请求参数
        String userRequestParams = invokeRequest.getUserRequestParams();
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        //如果查询结果为空
        if (oldInterfaceInfo==null){
            //抛出异常
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (oldInterfaceInfo.getStatus()==InterfaceInfoStatusEnum.OFFLINE.getValue()){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"接口未开启");
        }

        // 获取当前登录用户的ak和sk，这样相当于用户自己的这个身份去调用，
        // 也不会担心它刷接口，因为知道是谁刷了这个接口，会比较安全1719189991361474562_0.2555306006468203
        User loginUser = userService.getLoginUser(request);
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();

        //临时新建一个client   要不然始终用的是管理员的账户 密码 来测试接口

        MyAPIClient tempMyAPIClient = new MyAPIClient(accessKey, secretKey);
        // 我们只需要进行测试调用，所以我们需要解析传递过来的参数。
        Gson gson = new Gson();
        // 将用户请求参数转换为com.example.yuapiclientsdk.model.User对象

        com.example.myapiclientsdk.model.User user = gson.fromJson(userRequestParams, com.example.myapiclientsdk.model.User.class);

        String userNameByPost = tempMyAPIClient.getUserNameByPost(user);
        return ResultUtils.success(userNameByPost);

    }




    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceInfoList);
    }

    /**
     * 分页获取列表
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQuery.getDescription();
        // content 需支持模糊搜索
        interfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "content", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfaceInfoPage);
    }

    // endregion

}
