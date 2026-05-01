package com.cs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName: WecomCorpVO
 * @Author: wwd
 * @TODO: 企业微信账户信息 VO
 * @Date: 2026/3/21
 */
@Schema
@Data
public class WecomCorpVO {

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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "企业微信应用列表")
    private List<WecomCorpAgentVO> agents;
}