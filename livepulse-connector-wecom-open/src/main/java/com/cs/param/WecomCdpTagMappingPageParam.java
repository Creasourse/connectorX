package com.cs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName: WecomCdpTagMappingPageParam
 * @Author: wwd
 * @TODO: CDP标签映射分页查询参数
 * @Date: 2026/3/21
 */
@Schema
@Data
public class WecomCdpTagMappingPageParam {

    @Schema(description = "当前页码")
    private Integer currentPage;

    @Schema(description = "每页显示数量")
    private Integer pageSize;

    @Schema(description = "企业微信账户ID")
    private Long wecomCorpId;

    @Schema(description = "CDP标签名称")
    private String cdpTagName;

    @Schema(description = "同步状态")
    private Integer syncStatus;
}