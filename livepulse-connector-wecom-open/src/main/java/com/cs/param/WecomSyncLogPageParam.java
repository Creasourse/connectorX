package com.cs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName: WecomSyncLogPageParam
 * @Author: wwd
 * @TODO: 企业微信同步日志分页查询参数
 * @Date: 2026/3/27
 */
@Schema
@Data
public class WecomSyncLogPageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "当前页码")
    private Integer currentPage;

    @Schema(description = "每页显示数量")
    private Integer pageSize;

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

    @Schema(description = "排序字段 1:创建时间倒序 2:创建时间正序 3:开始时间倒序 4:开始时间正序")
    private Integer sortType;
}