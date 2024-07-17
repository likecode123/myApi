package com.yupi.project.aop;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.yupi.project.annotation.AuthCheck;
import com.yupi.project.common.ErrorCode;
import com.yupi.project.exception.BusinessException;
import com.yupi.project.model.entity.User;
import com.yupi.project.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 权限校验 AOP
 *
 * @author yupi
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    //这是一个 AOP 的注解，表示该方法会在所有带有 @AuthCheck 注解的方法调用前后执行。
    //authCheck 参数表示该注解的实例。

//    在方法执行前检查用户的权限，根据注解 AuthCheck 中的配置决定用户是否有权限执行该方法。
//    如果用户没有相应的权限，则抛出 BusinessException 异常，否则允许方法继续执行。
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        //拦截方法的签名，joinPoint 表示当前执行的方法，authCheck 是方法上的注解实例。

//        获取 @AuthCheck 注解中定义的 anyRole 数组，并过滤掉空值，最终收集成一个 List<String>。
        List<String> anyRole = Arrays.stream(authCheck.anyRole()).filter(StringUtils::isNotBlank).collect(Collectors.toList());
//        获取 @AuthCheck 注解中定义的 mustRole 字符串。
        String mustRole = authCheck.mustRole();
//        获取当前请求的 RequestAttributes，这是 Spring 提供的一种机制，用于在当前线程中访问请求相关的属性。
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        User user = userService.getLoginUser(request);
        // 拥有任意权限即通过
        if (CollectionUtils.isNotEmpty(anyRole)) {
            String userRole = user.getUserRole();
            if (!anyRole.contains(userRole)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        // 必须有所有权限才通过
        if (StringUtils.isNotBlank(mustRole)) {
            String userRole = user.getUserRole();
            if (!mustRole.equals(userRole)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}

