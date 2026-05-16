package com.cs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: LocalConnectorVO
 * @Author: wwd
 * @TODO:
 * @Date: 2026/3/11 10:26
 */
@Schema
@Data
public class LocalConnectorVO {

    /**
     * 我的连接器ID
     */
    @Schema(description = "我的连接器ID")
    private Long localConnectorId;

    /**
     * 连接器主键
     */
    @Schema(description = "连接器主键")
    private Long connectorId;

    /**
     * 连接器名称
     */
    @Schema(description = "连接器名称")
    private String connectorName;

    /**
     * 标题
     */
    @Schema(description = "标题")
    private String titile;

    /**
     * 简介
     */
    @Schema(description = "简介")
    private String remark;

    /**
     * 图标
     */
    @Schema(description = "图标")
    private String icon;

    /**
     * 1:上线 2:下线 3:使用中
     */
    @Schema(description = "用户是开发者 状态只能是0和3 当开发者发布状态为3  0:未上架 1:上架 2:下架 3:审核中,待审核 4:历史版本 5:驳回")
    private Short staus;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    private String userName;

    /**
     * 版本号
     */
    @Schema(description = "版本号")
    private String version;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 存放路径
     */
    @Schema(description = "存放路径")
    private String locationUrl;

    @Schema(description = "简要概述")
    private String sketchOut;
}
