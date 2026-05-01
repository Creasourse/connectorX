package com.cs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: WecomCorpParam
 * @Author: wwd
 * @TODO: 企业微信账户保存/更新参数
 * @Date: 2026/3/21
 */
@Schema
@Data
public class WecomCorpParam {

    @Schema(description = "企业微信账户信息表ID")
    private Long wecomeCorpId;

    @Schema(description = "企业名称")
    private String companyName;

    @Schema(description = "企业ID")
    private String corpId;

    @Schema(description = "通讯录密钥")
    private String corpSecret;

    @Schema(description = "企业logo")
    private String logoUrl;

    @Schema(description = "企业微信应用列表")
    private List<WecomCorpAgentParam> agents;
}