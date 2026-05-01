package com.cs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: WecomCallbackConfigParam
 * @Author: wwd
 * @TODO: 企业微信回调配置 保存/更新参数
 * @Date: 2026/3/25
 */
@Schema
@Data
public class WecomCallbackConfigParam implements Serializable {

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
}