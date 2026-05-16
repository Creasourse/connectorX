package com.cs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: MetaTableColumnParam
 * @Author: wwd
 * @TODO: MetaTableColumn 保存/更新参数
 * @Date: 2026/3/25
 */
@Schema
@Data
public class MetaTableColumnParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long tableColumnId;

    @Schema(description = "关联表ID")
    private Long tableId;

    @Schema(description = "列名称")
    private String tableColumnName;

    @Schema(description = "列别名")
    private String tableColumnAlias;

    @Schema(description = "列注释")
    private String comment;

    @Schema(description = "是否主键 1:是")
    private Integer isPk;

    @Schema(description = "数据类型")
    private Integer dataType;

    @Schema(description = "短数据类型")
    private Integer shortDataType;

    @Schema(description = "数据类型名称")
    private String dataTypeName;
}