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
 * 企业微信回调配置表
 * </p>
 *
 * @author wwd
 * @since 2026-03-25
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("wecom_callback_config")
@Schema(name = "WecomCallbackConfig", description = "企业微信回调配置表")
public class WecomCallbackConfig extends Model<WecomCallbackConfig> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "企业微信账户ID")
    @TableField("wecom_corp_id")
    private Long wecomCorpId;

    @Schema(description = "事件编码")
    @TableField("event_code")
    private String eventCode;

    @Schema(description = "事件名称")
    @TableField("event_name")
    private String eventName;

    @Schema(description = "事件说明")
    @TableField("event_description")
    private String eventDescription;

    @Schema(description = "是否启用 0-禁用 1-启用")
    @TableField("enabled")
    private Integer enabled;

    @Schema(description = "回调URL")
    @TableField("callback_url")
    private String callbackUrl;

    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}