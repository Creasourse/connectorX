package com.cs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: WecomCdpTagMappingVO
 * @Author: wwd
 * @TODO: CDP标签映射VO
 * @Date: 2026/3/21
 */
@Schema
@Data
public class WecomCdpTagMappingVO {

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

    @Schema(description = "同步时间")
    private LocalDateTime syncTime;

    @Schema(description = "同步状态：0-未同步，1-已同步，2-同步失败")
    private Integer syncStatus;

    @Schema(description = "同步错误信息")
    private String syncError;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}