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
 * 企业微信回调日志表
 * </p>
 *
 * @author wwd
 * @since 2026-03-25
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("wecom_callback_log")
@Schema(name = "WecomCallbackLog", description = "企业微信回调日志表")
public class WecomCallbackLog extends Model<WecomCallbackLog> {

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

    @Schema(description = "接收到的消息内容")
    @TableField("request_body")
    private String requestBody;

    @Schema(description = "处理结果 0-待处理 1-成功 2-失败")
    @TableField("process_status")
    private Integer processStatus;

    @Schema(description = "错误信息")
    @TableField("error_message")
    private String errorMessage;

    @Schema(description = "响应内容")
    @TableField("response_body")
    private String responseBody;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}