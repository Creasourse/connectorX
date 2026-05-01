package com.cs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author wwd
 * @since 2026-03-10
 */
@Getter
@Setter
@ToString
@TableName("sys_user")
@Accessors(chain = true)
@Schema(name = "SysUser", description = "用户表")
public class SysUser extends Model<SysUser> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID主键
     */
    @Schema(description = "用户ID主键")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 用户名称
     */
    @TableField("user_name")
    @Schema(description = "用户名称")
    private String userName;

    /**
     * 昵称
     */
    @TableField("nick_name")
    @Schema(description = "昵称")
    private String nickName;

    /**
     * 密码
     */
    @TableField("password")
    @Schema(description = "密码")
    private String password;

    /**
     * 密码盐值
     */
    @TableField("salt")
    @Schema(description = "密码盐值")
    private String salt;

    /**
     * 1:客户 2:开发者 3:管理者
     */
    @TableField("type")
    @Schema(description = "1:客户 2:开发者 3:管理者")
    private Short type;

    /**
     * 邮箱
     */
    @TableField("email")
    @Schema(description = "邮箱")
    private String email;

    /**
     * 手机号
     */
    @TableField("phone")
    @Schema(description = "手机号")
    private String phone;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 头像地址
     */
    @TableField("avatar")
    @Schema(description = "头像地址")
    private String avatar;

    @Override
    public Serializable pkVal() {
        return this.userId;
    }
}
