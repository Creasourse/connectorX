package com.cs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName: WecomCallbackLogPageParam
 * @Author: wwd
 * @TODO: 企业微信回调日志分页查询参数
 * @Date: 2026/3/27
 */
@Schema
@Data
public class WecomCallbackLogPageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "当前页码")
    private Integer currentPage;

    @Schema(description = "每页显示数量")
    private Integer pageSize;

    @Schema(description = "企业微信账户ID")
    private Long wecomCorpId;

    @Schema(description = "事件编码")
    private String eventCode;

    @Schema(description = "事件名称")
    private String eventName;

    @Schema(description = "处理结果 0-待处理 1-成功 2-失败")
    private Integer processStatus;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "排序字段 1:创建时间倒序 2:创建时间正序")
    private Integer sortType;
}