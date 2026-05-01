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
 * 外部联系人详情表
 * </p>
 *
 * @author wwd
 * @since 2026-03-21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("wecom_external_contacts_details")
@Schema(name = "WecomExternalContactsDetails", description = "外部联系人详情表")
public class WecomExternalContactsDetails extends Model<WecomExternalContactsDetails> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "对外属性信息，JSON格式")
    @TableField("external_profile")
    private String externalProfile;

    @Schema(description = "外部联系人userid")
    @TableField("external_userid")
    private String externalUserid;

    @Schema(description = "外部联系人的昵称")
    @TableField("name")
    private String name;

    @Schema(description = "外部联系人的职位")
    @TableField("position")
    private String position;

    @Schema(description = "外部联系人头像")
    @TableField("avatar")
    private String avatar;

    @Schema(description = "外部联系人的类型")
    @TableField("type")
    private Integer type;

    @Schema(description = "外部联系人性别")
    @TableField("gender")
    private Integer gender;

    @Schema(description = "外部联系人在微信开放平台的唯一身份标识")
    @TableField("unionid")
    private String unionid;

    @Schema(description = "外部联系人所属企业的简称")
    @TableField("corp_name")
    private String corpName;

    @Schema(description = "外部联系人所属企业的全称")
    @TableField("corp_full_name")
    private String corpFullName;

    @Schema(description = "添加此外部联系人的成员userid")
    @TableField("follow_user")
    private String followUser;

    @Schema(description = "添加此外部联系人的时间")
    @TableField("add_time")
    private Long addTime;

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