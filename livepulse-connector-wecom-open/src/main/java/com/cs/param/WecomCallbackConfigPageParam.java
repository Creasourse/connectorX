package com.cs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: WecomCallbackConfigPageParam
 * @Author: wwd
 * @TODO: 企业微信回调配置 分页查询参数
 * @Date: 2026/3/25
 */
@Schema
@Data
public class WecomCallbackConfigPageParam implements Serializable {

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

    @Schema(description = "是否启用")
    private Integer enabled;

    @Schema(description = "排序字段 1:创建时间倒序 2:创建时间正序")
    private Integer sortType;
}