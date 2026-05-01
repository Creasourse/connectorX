package com.cs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: MetaTableParam
 * @Author: wwd
 * @TODO: MetaTable 保存/更新参数
 * @Date: 2026/3/25
 */
@Schema
@Data
public class MetaTableParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long tableId;

    @Schema(description = "表名称")
    private String tableName;

    @Schema(description = "表别名")
    private String tableAlias;

    @Schema(description = "表详情")
    private String comment;
}