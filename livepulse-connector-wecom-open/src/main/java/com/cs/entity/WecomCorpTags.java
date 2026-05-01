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
 * 企业标签表
 * </p>
 *
 * @author wwd
 * @since 2026-03-21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("wecom_corp_tags")
@Schema(name = "WecomCorpTags", description = "企业标签表")
public class WecomCorpTags extends Model<WecomCorpTags> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "企业微信账户ID")
    @TableField("wecom_corp_id")
    private Long wecomCorpId;

    @Schema(description = "标签ID")
    @TableField("tag_id")
    private String tagId;

    @Schema(description = "标签名称")
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

    @Schema(description = "标签次序")
    @TableField("tag_order")
    private Integer tagOrder;

    @Schema(description = "标签创建时间")
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