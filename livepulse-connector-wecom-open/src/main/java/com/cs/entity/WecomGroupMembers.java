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
 * 客户群成员表
 * </p>
 *
 * @author wwd
 * @since 2026-03-21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("wecom_group_members")
@Schema(name = "WecomGroupMembers", description = "客户群成员表")
public class WecomGroupMembers extends Model<WecomGroupMembers> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "客户群ID")
    @TableField("chat_id")
    private String chatId;

    @Schema(description = "成员ID")
    @TableField("member_id")
    private String memberId;

    @Schema(description = "成员类型，1-企业成员，2-外部联系人")
    @TableField("type")
    private Integer type;

    @Schema(description = "微信unionid")
    @TableField("unionid")
    private String unionid;

    @Schema(description = "加入群聊时间")
    @TableField("join_time")
    private Long joinTime;

    @Schema(description = "加入群聊的途径")
    @TableField("join_scene")
    private Integer joinScene;

    @Schema(description = "邀请者的userid")
    @TableField("invitor")
    private String invitor;

    @Schema(description = "在群里的昵称")
    @TableField("group_nickname")
    private String groupNickname;

    @Schema(description = "成员名称")
    @TableField("name")
    private String name;

    @Schema(description = "企业成员的userid")
    @TableField("user_id")
    private String userId;

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