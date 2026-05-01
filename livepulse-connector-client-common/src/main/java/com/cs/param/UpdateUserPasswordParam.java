package com.cs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName: UpdateUserPasswordParam
 * @Author: wwd
 * @TODO:
 * @Date: 2026/3/5 11:31
 */
@Schema
@Data
public class UpdateUserPasswordParam {

    /**
     * 用户ID主键
     */
    @Schema(description = "用户ID主键")
    private Long userId;

    /**
     * 密码
     */
    @Schema(description = "新密码")
    private String password;

    /**
     * 新密码
     */
    @Schema(description = "确认密码")
    private String confirmPassword;
}
