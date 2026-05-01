package com.cs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName: WecomCdpTagMappingParam
 * @Author: wwd
 * @TODO: CDP标签映射参数
 * @Date: 2026/3/21
 */
@Schema
@Data
public class WecomCdpTagMappingParam {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "企业微信账户ID")
    private Long wecomCorpId;

    @Schema(description = "CDP标签名称")
    private String cdpTagName;

    @Schema(description = "CDP标签值")
    private String cdpTagValue;

    @Schema(description = "企业微信标签组ID")
    private String wecomTagGroupId;

    @Schema(description = "企业微信标签组名称")
    private String wecomTagGroupName;

    @Schema(description = "企业微信标签ID")
    private String wecomTagId;

    @Schema(description = "企业微信标签名称")
    private String wecomTagName;
}