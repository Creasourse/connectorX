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
 * CDP标签映射表
 * </p>
 *
 * @author wwd
 * @since 2026-03-21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("wecom_cdp_tag_mapping")
@Schema(name = "WecomCdpTagMapping", description = "CDP标签映射表")
public class WecomCdpTagMapping extends Model<WecomCdpTagMapping> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "企业微信账户ID")
    @TableField("wecom_corp_id")
    private Long wecomCorpId;

    @Schema(description = "CDP标签名称")
    @TableField("cdp_tag_name")
    private String cdpTagName;

    @Schema(description = "CDP标签值")
    @TableField("cdp_tag_value")
    private String cdpTagValue;

    @Schema(description = "企业微信标签组ID")
    @TableField("wecom_tag_group_id")
    private String wecomTagGroupId;

    @Schema(description = "企业微信标签组名称")
    @TableField("wecom_tag_group_name")
    private String wecomTagGroupName;

    @Schema(description = "企业微信标签ID")
    @TableField("wecom_tag_id")
    private String wecomTagId;

    @Schema(description = "企业微信标签名称")
    @TableField("wecom_tag_name")
    private String wecomTagName;

    @Schema(description = "同步时间")
    @TableField("sync_time")
    private LocalDateTime syncTime;

    @Schema(description = "同步状态：0-未同步，1-已同步，2-同步失败")
    @TableField("sync_status")
    private Integer syncStatus;

    @Schema(description = "同步错误信息")
    @TableField("sync_error")
    private String syncError;

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