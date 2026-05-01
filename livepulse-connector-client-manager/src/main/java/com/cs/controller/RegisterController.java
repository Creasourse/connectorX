package com.cs.controller;

import com.cs.exception.CommonException;
import com.cs.param.ForgetPasswordParam;
import com.cs.param.SysUserParam;
import com.cs.resp.RespResult;
import com.cs.service.LoginService;
import com.cs.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: RegisterController
 * @Author: wwd
 * @TODO:
 * @Date: 2026/3/4 11:18
 */
@RestController
@RequestMapping("/register")
@Tag(name = "注册相关接口")
public class RegisterController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private LoginService loginService;

    @PostMapping("/save")
    @Operation(summary = "用户注册")
    public RespResult<Boolean> save(@RequestBody SysUserParam sysUserParam) throws CommonException {
        return RespResult.success(sysUserService.register(sysUserParam));
    }

    @PostMapping("/forgetPassword")
    @Operation(summary = "忘记密码")
    public RespResult<Boolean> forgetPassword(@RequestBody ForgetPasswordParam forgetPasswordParam) throws CommonException {
        return RespResult.success(sysUserService.forgetPassword(forgetPasswordParam));
    }


}
