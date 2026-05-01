package com.cs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName: WecomCallbackConfigVO
 * @Author: wwd
 * @TODO: 企业微信回调配置 视图对象
 * @Date: 2026/3/25
 */
@Schema
@Data
public class WecomCallbackConfigVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "企业微信账户ID")
    private Long wecomCorpId;

    @Schema(description = "事件编码")
    private String eventCode;

    @Schema(description = "事件名称")
    private String eventName;

    @Schema(description = "事件说明")
    private String eventDescription;

    @Schema(description = "是否启用 0-禁用 1-启用")
    private Integer enabled;

    @Schema(description = "回调URL")
    private String callbackUrl;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}