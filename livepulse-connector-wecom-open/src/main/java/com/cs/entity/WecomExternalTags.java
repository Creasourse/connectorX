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
 * 外部联系人标签关联表
 * </p>
 *
 * @author wwd
 * @since 2026-03-21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("wecom_external_tags")
@Schema(name = "WecomExternalTags", description = "外部联系人标签关联表")
public class WecomExternalTags extends Model<WecomExternalTags> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "外部联系人userid")
    @TableField("external_userid")
    private String externalUserid;

    @Schema(description = "成员userid")
    @TableField("user_id")
    private String userId;

    @Schema(description = "企业标签的id")
    @TableField("tag_id")
    private String tagId;

    @Schema(description = "企业标签的名称")
    @TableField("tag_name")
    private String tagName;

    @Schema(description = "标签类型")
    @TableField("tag_type")
    private String tagType;

    @Schema(description = "标签组id")
    @TableField("group_id")
    private String groupId;

    @Schema(description = "标签组名称")
    @TableField("group_name")
    private String groupName;

    @Schema(description = "标签打上/移除的时间")
    @TableField("create_time")
    private Long createTime;

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