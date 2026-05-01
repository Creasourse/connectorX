package com.cs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName: WecomCorpAgentParam
 * @Author: wwd
 * @TODO: 企业微信应用参数
 * @Date: 2026/3/21
 */
@Schema
@Data
public class WecomCorpAgentParam {

    @Schema(description = "企业微信应用ID")
    private Long wecomCorpAgentId;

    @Schema(description = "企业微信账户信息表ID")
    private Long wecomeCorpId;

    @Schema(description = "应用密钥")
    private String agentSecret;
}