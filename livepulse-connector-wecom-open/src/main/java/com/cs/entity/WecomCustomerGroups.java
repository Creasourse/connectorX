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
 * 客户群基本信息表
 * </p>
 *
 * @author wwd
 * @since 2026-03-21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("wecom_customer_groups")
@Schema(name = "WecomCustomerGroups", description = "客户群基本信息表")
public class WecomCustomerGroups extends Model<WecomCustomerGroups> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "企业微信账户ID")
    @TableField("wecom_corp_id")
    private Long wecomCorpId;

    @Schema(description = "客户群ID")
    @TableField("chat_id")
    private String chatId;

    @Schema(description = "群名称")
    @TableField("name")
    private String name;

    @Schema(description = "群主ID")
    @TableField("owner")
    private String owner;

    @Schema(description = "群创建时间")
    @TableField("create_time")
    private Long createTime;

    @Schema(description = "群公告")
    @TableField("notice")
    private String notice;

    @Schema(description = "群成员数")
    @TableField("member_count")
    private Integer memberCount;

    @Schema(description = "群管理员ID列表")
    @TableField("admin_list")
    private String adminList;

    @Schema(description = "群公告")
    @TableField("chat_announcement")
    private String chatAnnouncement;

    @Schema(description = "群二维码")
    @TableField("group_qrcode")
    private String groupQrcode;

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