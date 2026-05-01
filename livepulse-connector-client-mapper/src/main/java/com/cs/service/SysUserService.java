package com.cs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.entity.SysUser;
import com.cs.exception.CommonException;
import com.cs.param.ForgetPasswordParam;
import com.cs.param.SysUserParam;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author wwd
 * @since 2026-01-30
 */
public interface SysUserService extends IService<SysUser> {

    Boolean register(SysUserParam sysUserParam) throws CommonException;

    Boolean forgetPassword(ForgetPasswordParam forgetPasswordParam) throws CommonException;
}
