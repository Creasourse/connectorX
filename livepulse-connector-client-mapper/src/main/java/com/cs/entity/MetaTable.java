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
import java.time.LocalDateTime;

/**
 * <p>
 * cdp表
 * </p>
 *
 * @author wwd
 * @since 2026-05-03
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName("meta_table")
@Schema(name = "MetaTable", description = "cdp表")
public class MetaTable extends Model<MetaTable> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "table_id", type = IdType.AUTO)
    private Long tableId;

    /**
     * 表名称
     */
    @TableField("table_name")
    @Schema(description = "表名称")
    private String tableName;

    /**
     * 表别名
     */
    @TableField("table_alias")
    @Schema(description = "表别名")
    private String tableAlias;

    /**
     * 表详情
     */
    @TableField("comment")
    @Schema(description = "表详情")
    private String comment;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 1.企业微信
     */
    @TableField("type")
    @Schema(description = "1.企业微信")
    private Integer type;

    @Override
    public Serializable pkVal() {
        return this.tableId;
    }
}
