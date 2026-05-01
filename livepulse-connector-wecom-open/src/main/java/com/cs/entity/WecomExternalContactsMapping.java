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
 * 外部联系人映射表
 * </p>
 *
 * @author wwd
 * @since 2026-03-21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("wecom_external_contacts_mapping")
@Schema(name = "WecomExternalContactsMapping", description = "外部联系人映射表")
public class WecomExternalContactsMapping extends Model<WecomExternalContactsMapping> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "企业微信账户ID")
    @TableField("wecom_corp_id")
    private Long wecomCorpId;

    @Schema(description = "添加了外部联系人的成员UserID")
    @TableField("userid")
    private String userid;

    @Schema(description = "外部联系人userid")
    @TableField("external_userid")
    private String externalUserid;

    @Schema(description = "外部联系人昵称")
    @TableField("remark")
    private String remark;

    @Schema(description = "外部联系人描述")
    @TableField("description")
    private String description;

    @Schema(description = "添加外部联系人的时间")
    @TableField("create_time")
    private Long createTime;

    @Schema(description = "外部联系人所属企业名称")
    @TableField("remark_corp_name")
    private String remarkCorpName;

    @Schema(description = "外部联系人手机号")
    @TableField("remark_mobiles")
    private String remarkMobiles;

    @Schema(description = "添加方式")
    @TableField("add_way")
    private Integer addWay;

    @Schema(description = "企业自定义的state参数")
    @TableField("state")
    private String state;

    @Schema(description = "外部联系人在微信开放平台的唯一身份标识")
    @TableField("unionid")
    private String unionid;

    @Schema(description = "操作时间")
    @TableField("operate_time")
    private LocalDateTime operateTime;

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