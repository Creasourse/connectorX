package com.cs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: MetaTableColumnPageParam
 * @Author: wwd
 * @TODO: MetaTableColumn 分页查询参数
 * @Date: 2026/3/25
 */
@Schema
@Data
public class MetaTableColumnPageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "当前页码")
    private Integer currentPage;

    @Schema(description = "每页显示数量")
    private Integer pageSize;

    @Schema(description = "关联表ID")
    private Long tableId;

    @Schema(description = "列名称")
    private String tableColumnName;

    @Schema(description = "列别名")
    private String tableColumnAlias;

    @Schema(description = "是否主键")
    private Integer isPk;

    @Schema(description = "排序字段 1:创建时间倒序 2:创建时间正序")
    private Integer sortType;
}