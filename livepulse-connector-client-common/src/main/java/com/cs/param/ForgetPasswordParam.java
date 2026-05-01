package com.cs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName: ForgetPasswordParam
 * @Author: wwd
 * @TODO:
 * @Date: 2026/3/4 16:34
 */
@Schema
@Data
public class ForgetPasswordParam {

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "新密码")
    private String password;

    @Schema(description = "确认密码")
    private String confirmPassword;
}
