package com.cs.util;

import com.cs.constant.CommonConstants;
import com.cs.vo.SysUserVO;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @ClassName: UserContext
 * @Author: wwd
 * @TODO:
 * @Date: 2026/3/6 17:30
 */
public class UserContext {


    /**
     * 设置当前用户
     */
    public static void setCurrentUser(SysUserVO userInfo) {
        RequestContextHolder.currentRequestAttributes()
                .setAttribute(CommonConstants.CURRENT_USER_KEY, userInfo, RequestAttributes.SCOPE_REQUEST);
    }

    /**
     * 获取当前用户
     */
    public static SysUserVO getCurrentUser() {
        return (SysUserVO) RequestContextHolder.currentRequestAttributes()
                .getAttribute(CommonConstants.CURRENT_USER_KEY, RequestAttributes.SCOPE_REQUEST);
    }

    /**
     * 清除当前用户
     */
    public static void clearCurrentUser() {
        RequestContextHolder.currentRequestAttributes()
                .removeAttribute(CommonConstants.CURRENT_USER_KEY, RequestAttributes.SCOPE_REQUEST);
    }
}
