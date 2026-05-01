package com.cs.service;

import com.cs.exception.CommonException;
import com.cs.param.SysUserPageParam;
import com.cs.param.SysUserParam;
import com.cs.param.UpdateUserPasswordParam;
import com.cs.param.UserLoginParam;
import com.cs.resp.PageResult;
import com.cs.vo.SysUserVO;
import com.cs.vo.UserLoginVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName: LoginService
 * @Author: wwd
 * @TODO:
 * @Date: 2026/3/4 16:39
 */
public interface LoginService {

    UserLoginVO userLogin(UserLoginParam userLoginParam) throws CommonException, JsonProcessingException;

    PageResult<SysUserVO> userPageList(SysUserPageParam sysUserPageParam) throws CommonException;

    Boolean editPassword(UpdateUserPasswordParam updateUserPasswordParam) throws CommonException;

    Boolean updateUserInfo(SysUserParam sysUserParam) throws CommonException;

    Boolean avatar(MultipartFile file);

    SysUserVO userInfo() throws CommonException;
}
