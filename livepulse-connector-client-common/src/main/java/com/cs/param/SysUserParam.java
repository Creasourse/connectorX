package com.cs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName: SysUserParam
 * @Author: wwd
 * @TODO:
 * @Date: 2026/3/4 16:08
 */
@Schema
@Data
public class SysUserParam {

    /**
     * 用户ID主键
     */
    @Schema(description = "用户ID主键")
    private Long userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    private String userName;

    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String nickName;

    /**
     * 密码
     */
    @Schema(description = "密码")
    private String password;

    /**
     * 密码盐值
     */
    @Schema(description = "密码盐值")
    private String salt;

    /**
     * 1:客户 2:开发者 3:管理者
     */
    @Schema(description = "1:客户 2:开发者 3:管理者")
    private Short type;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;
}
