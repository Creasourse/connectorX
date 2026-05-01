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
 * 企业微信数据同步日志表
 * </p>
 *
 * @author wwd
 * @since 2026-03-21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("wecom_sync_logs")
@Schema(name = "WecomSyncLog", description = "企业微信数据同步日志表")
public class WecomSyncLog extends Model<WecomSyncLog> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "企业微信账户ID")
    @TableField("wecom_corp_id")
    private Long wecomCorpId;

    @Schema(description = "同步类型：DEPARTMENT-部门，EMPLOYEE-员工")
    @TableField("sync_type")
    private String syncType;

    @Schema(description = "同步状态：SUCCESS-成功，FAILED-失败，PARTIAL-部分成功")
    @TableField("sync_status")
    private String syncStatus;

    @Schema(description = "开始时间")
    @TableField("start_time")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @TableField("end_time")
    private LocalDateTime endTime;

    @Schema(description = "总记录数")
    @TableField("total_count")
    private Integer totalCount;

    @Schema(description = "成功数量")
    @TableField("success_count")
    private Integer successCount;

    @Schema(description = "失败数量")
    @TableField("fail_count")
    private Integer failCount;

    @Schema(description = "错误信息")
    @TableField("error_message")
    private String errorMessage;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}