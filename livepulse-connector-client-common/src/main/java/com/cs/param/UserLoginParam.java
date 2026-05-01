package com.cs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName: UserLoginParam
 * @Author: wwd
 * @TODO:
 * @Date: 2026/3/4 16:56
 */
@Schema
@Data
public class UserLoginParam {

    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    private String userName;

    /**
     * 密码
     */
    @Schema(description = "密码")
    private String password;
}
