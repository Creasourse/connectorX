package com.cs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: MetaTablePageParam
 * @Author: wwd
 * @TODO: MetaTable 分页查询参数
 * @Date: 2026/3/25
 */
@Schema
@Data
public class MetaTablePageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "当前页码")
    private Integer currentPage;

    @Schema(description = "每页显示数量")
    private Integer pageSize;

    @Schema(description = "表名称")
    private String tableName;

    @Schema(description = "表别名")
    private String tableAlias;

    @Schema(description = "排序字段 1:创建时间倒序 2:创建时间正序 3:更新时间倒序 4:更新时间正序")
    private Integer sortType;
}