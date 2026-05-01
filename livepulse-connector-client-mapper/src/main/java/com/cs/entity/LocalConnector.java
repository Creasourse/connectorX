package com.cs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
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
 * 我的连接器
 * </p>
 *
 * @author wwd
 * @since 2026-03-11
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName("local_connector")
@Schema(name = "LocalConnector", description = "我的连接器")
public class LocalConnector extends Model<LocalConnector> {

    private static final long serialVersionUID = 1L;

    /**
     * 我的连接器ID
     */
    @Schema(description = "我的连接器ID")
    @TableId(value = "local_connector_id", type = IdType.AUTO)
    private Long localConnectorId;

    /**
     * 连接器主键
     */
    @TableField("connector_id")
    @Schema(description = "连接器主键")
    private Long connectorId;

    /**
     * 连接器名称
     */
    @TableField("connector_name")
    @Schema(description = "连接器名称")
    private String connectorName;

    /**
     * 标题
     */
    @TableField("titile")
    @Schema(description = "标题")
    private String titile;

    /**
     * 简介
     */
    @TableField("remark")
    @Schema(description = "简介")
    private String remark;

    /**
     * 图标
     */
    @TableField("icon")
    @Schema(description = "图标")
    private String icon;

    /**
     * 1:上线 2:下线 3:使用中
     */
    @TableField("staus")
    @Schema(description = "1:上线 2:下线 3:使用中")
    private Short staus;

    /**
     * 用户ID
     */
    @TableField("user_id")
    @Schema(description = "用户ID")
    private Integer userId;

    /**
     * 用户名称
     */
    @TableField("user_name")
    @Schema(description = "用户名称")
    private String userName;

    /**
     * 版本号
     */
    @Version
    @TableField("version")
    @Schema(description = "版本号")
    private String version;

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
     * 存放路径
     */
    @TableField("location_url")
    @Schema(description = "存放路径")
    private String locationUrl;

    @Override
    public Serializable pkVal() {
        return this.localConnectorId;
    }
}
