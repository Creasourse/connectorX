package com.cs.controller;

import com.cs.annotation.TokenSign;
import com.cs.exception.CommonException;
import com.cs.exception.file.InvalidExtensionException;
import com.cs.param.*;
import com.cs.resp.PageResult;
import com.cs.resp.RespResult;
import com.cs.service.LoginService;
import com.cs.vo.SysUserVO;
import com.cs.vo.UserLoginVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @ClassName: LoginController
 * @Author: wwd
 * @TODO:
 * @Date: 2026/3/4 10:44
 */
@RestController
@RequestMapping("/login")
@Tag(name = "登录接口")
public class LoginController {

    @Autowired
    private LoginService loginService;


    @PostMapping("/userLogin")
    @Operation(summary = "用户登录, 返回登录token")
    public RespResult<UserLoginVO> userLogin(@RequestBody UserLoginParam userLoginParam) throws CommonException, JsonProcessingException {
        return RespResult.success(loginService.userLogin(userLoginParam));
    }


    @PostMapping("/editPassword")
    @Operation(summary = "用户修改个人密码")
    @TokenSign
    public RespResult<Boolean> editPassword(@RequestBody UpdateUserPasswordParam updateUserPasswordParam) throws CommonException {
        return RespResult.success(loginService.editPassword(updateUserPasswordParam));
    }


    @PostMapping("/updateUserInfo")
    @Operation(summary = "更新用户信息,昵称+邮箱+备注")
    @TokenSign
    public RespResult<Boolean> updateUserInfo(@RequestBody SysUserParam sysUserParam) throws CommonException {
        return RespResult.success(loginService.updateUserInfo(sysUserParam));
    }

    @PostMapping("/userPageList")
    @Operation(summary = "用户分页列表")
    @TokenSign
    public RespResult<PageResult<SysUserVO>> userPageList(@RequestBody SysUserPageParam sysUserPageParam) throws CommonException {
        return RespResult.success(loginService.userPageList(sysUserPageParam));
    }

    @PostMapping("/avatar")
    @Operation(summary = "用户头像上传")
    @TokenSign
    public RespResult<Boolean> avatar(@RequestParam("avatarfile") MultipartFile file) throws CommonException, IOException, InvalidExtensionException {
        return RespResult.success(loginService.avatar(file));
    }

    @PostMapping("/userInfo")
    @Operation(summary = "用户信息")
    @TokenSign
    public RespResult<SysUserVO> userInfo() throws CommonException, JsonProcessingException {
        return RespResult.success(loginService.userInfo());
    }
}
