package com.cs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName: WecomSyncLogVO
 * @Author: wwd
 * @TODO: 企业微信同步日志视图对象
 * @Date: 2026/3/27
 */
@Schema
@Data
public class WecomSyncLogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "企业微信账户ID")
    private Long wecomCorpId;

    @Schema(description = "同步类型：DEPARTMENT-部门，EMPLOYEE-员工，EXTERNAL_CONTACT-外部联系人，CUSTOMER_GROUP-客户群，CORP_TAG-企业标签")
    private String syncType;

    @Schema(description = "同步状态：SUCCESS-成功，FAILED-失败，PARTIAL-部分成功")
    private String syncStatus;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "总记录数")
    private Integer totalCount;

    @Schema(description = "成功数量")
    private Integer successCount;

    @Schema(description = "失败数量")
    private Integer failCount;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}