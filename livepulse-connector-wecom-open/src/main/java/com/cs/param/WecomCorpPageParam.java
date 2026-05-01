package com.cs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName: WecomCorpPageParam
 * @Author: wwd
 * @TODO: 企业微信账户分页查询参数
 * @Date: 2026/3/21
 */
@Schema
@Data
public class WecomCorpPageParam {

    @Schema(description = "当前页码")
    private Integer currentPage;

    @Schema(description = "每页显示数量")
    private Integer pageSize;

    @Schema(description = "企业名称")
    private String companyName;

    @Schema(description = "企业ID")
    private String corpId;

    @Schema(description = "排序字段 1:创建时间倒序 2:创建时间正序 3:更新时间倒序 4:更新时间正序")
    private Integer sortType;
}