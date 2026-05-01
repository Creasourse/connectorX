package com.cs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName: UserLoginVO
 * @Author: wwd
 * @TODO:
 * @Date: 2026/3/4 17:01
 */
@Schema
@Data
public class UserLoginVO {

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
     * 1:客户 2:开发者 3:管理者
     */
    @Schema(description = "1:客户 2:开发者 3:管理者")
    private Short type;

    @Schema(description = "登录成功返回token")
    private String token;

    @Schema(description = "token过期时间")
    private Integer expire;

    @Schema(description = "头像")
    private String avatar;

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
