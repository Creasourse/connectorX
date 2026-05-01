package com.cs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 企业微信员工基本信息表
 * </p>
 *
 * @author wwd
 * @since 2026-03-21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("wecom_employees")
@Schema(name = "WecomEmployee", description = "企业微信员工基本信息表")
public class WecomEmployee extends Model<WecomEmployee> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "企业微信账户ID")
    @TableField("wecom_corp_id")
    private Long wecomCorpId;

    @Schema(description = "成员UserID")
    @TableField("userid")
    private String userid;

    @Schema(description = "成员名称")
    @TableField("name")
    private String name;

    @Schema(description = "成员别名")
    @TableField("alias")
    private String alias;

    @Schema(description = "手机号码")
    @TableField("mobile")
    private String mobile;

    @Schema(description = "邮箱")
    @TableField("email")
    private String email;

    @Schema(description = "所属部门ID列表")
    @TableField("department")
    private String department;

    @Schema(description = "部门内的排序值")
    @TableField("order_in_dept")
    private String orderInDept;

    @Schema(description = "职位信息")
    @TableField("position")
    private String position;

    @Schema(description = "性别，0表示未定义，1表示男性，2表示女性")
    @TableField("gender")
    private String gender;

    @Schema(description = "头像url")
    @TableField("avatar")
    private String avatar;

    @Schema(description = "座机")
    @TableField("telephone")
    private String telephone;

    @Schema(description = "激活状态: 1表示已激活，2表示已禁用，4表示未激活")
    @TableField("enable")
    private Integer enable;

    @Schema(description = "是否为部门负责人：0-否，1-是")
    @TableField("is_leader")
    private Integer isLeader;

    @Schema(description = "扩展属性")
    @TableField("extattr")
    private String extattr;

    @Schema(description = "激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业")
    @TableField("status")
    private Integer status;

    @Schema(description = "员工个人二维码")
    @TableField("qr_code")
    private String qrCode;

    @Schema(description = "成员对外属性，JSON格式")
    @TableField("external_profile")
    private String externalProfile;

    @Schema(description = "对外职务")
    @TableField("external_position")
    private String externalPosition;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    @Schema(description = "是否删除 0-否 1-是")
    @TableField("deleted")
    private Integer deleted;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}