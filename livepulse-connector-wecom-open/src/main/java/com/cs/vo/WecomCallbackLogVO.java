package com.cs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName: WecomCallbackLogVO
 * @Author: wwd
 * @TODO: 企业微信回调日志 视图对象
 * @Date: 2026/3/25
 */
@Schema
@Data
public class WecomCallbackLogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "企业微信账户ID")
    private Long wecomCorpId;

    @Schema(description = "事件编码")
    private String eventCode;

    @Schema(description = "事件名称")
    private String eventName;

    @Schema(description = "接收到的消息内容")
    private String requestBody;

    @Schema(description = "处理结果 0-待处理 1-成功 2-失败")
    private Integer processStatus;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "响应内容")
    private String responseBody;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
