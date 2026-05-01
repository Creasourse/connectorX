package com.cs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: ConnectorVersionVO
 * @Author: wwd
 * @TODO:
 * @Date: 2026/3/6 19:03
 */
@Schema
@Data
public class ConnectorVersionVO {

    /**
     * 连接器版本ID
     */
    @Schema(description = "连接器版本ID ")
    private Long connectorVersionId;

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
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 存放路径
     */
    @Schema(description = "存放路径")
    private String locationUrl;

    /**
     * 开发者ID
     */
    @Schema(description = "开发者ID")
    private Long developUserId;

    /**
     * 开发者名称
     */
    @Schema(description = "开发者名称")
    private String developUserName;

    /**
     * 头像
     */
    @Schema(description = "头像")
    private String avatar;

    /**
     * 1:免费 2:收费
     */
    @Schema(description = "1:免费 2:收费")
    private Short type;

    /**
     * 价格
     */
    @Schema(description = "价格")
    private Short price;

    /**
     * 标签名称
     */
    @Schema(description = "标签名称")
    private String connLabelName;

    @Schema(description = "简要概述")
    private String sketchOut;
}
