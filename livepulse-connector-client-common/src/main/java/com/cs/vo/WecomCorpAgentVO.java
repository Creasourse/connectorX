package com.cs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: WecomCorpAgentVO
 * @Author: wwd
 * @TODO: 企业微信应用信息 VO
 * @Date: 2026/3/21
 */
@Schema
@Data
public class WecomCorpAgentVO {

    @Schema(description = "企业微信应用ID")
    private Long wecomCorpAgentId;

    @Schema(description = "企业微信账户信息表ID")
    private Long wecomeCorpId;

    @Schema(description = "应用密钥")
    private String agentSecret;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}