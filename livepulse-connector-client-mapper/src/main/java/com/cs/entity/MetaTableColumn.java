package com.cs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author wwd
 * @since 2026-05-03
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName("meta_table_column")
@Schema(name = "MetaTableColumn", description = "")
public class MetaTableColumn extends Model<MetaTableColumn> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "table_column_id", type = IdType.AUTO)
    private Long tableColumnId;

    /**
     * 关联表ID
     */
    @TableField("table_id")
    @Schema(description = "关联表ID")
    private Long tableId;

    /**
     * 列名称
     */
    @Schema(description = "列名称")
    @TableField("table_column_name")
    private String tableColumnName;

    /**
     * 列别名
     */
    @Schema(description = "列别名")
    @TableField("table_column_alias")
    private String tableColumnAlias;

    /**
     * 列注释
     */
    @TableField("comment")
    @Schema(description = "列注释")
    private String comment;

    /**
     * 是否主键 1:是
     */
    @TableField("is_pk")
    @Schema(description = "是否主键 1:是")
    private Integer isPk;

    /**
     * 数据类型
     */
    @TableField("data_type")
    @Schema(description = "数据类型")
    private Integer dataType;

    /**
     * 短数据类型 
     */
    @TableField("short_data_type")
    @Schema(description = "短数据类型 ")
    private Integer shortDataType;

    /**
     * 数据类型名称
     */
    @TableField("data_type_name")
    @Schema(description = "数据类型名称")
    private String dataTypeName;

    @Override
    public Serializable pkVal() {
        return this.tableColumnId;
    }
}
