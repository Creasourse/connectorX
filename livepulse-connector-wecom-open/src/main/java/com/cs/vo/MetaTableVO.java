package com.cs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName: MetaTableVO
 * @Author: wwd
 * @TODO: MetaTable 视图对象
 * @Date: 2026/3/25
 */
@Schema
@Data
public class MetaTableVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long tableId;

    @Schema(description = "表名称")
    private String tableName;

    @Schema(description = "表别名")
    private String tableAlias;

    @Schema(description = "表详情")
    private String comment;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}