package com.cs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: ConnPluginVO
 * @Author: wwd
 * @TODO:
 * @Date: 2026/3/5 17:38
 */
@Schema
@Data
public class ConnPluginVO {

    /**
     * 连接器插件ID
     */
    @Schema(description = "连接器插件ID")
    private Long connPluginId;

    /**
     * 插件名称
     */
    @Schema(description = "插件名称")
    private String connPluginName;

    /**
     * 连接器分组ID
     */
    @Schema(description = "连接器分组ID")
    private Long groupId;

    /**
     * 分组名称
     */
    @Schema(description = "分组名称")
    private String groupName;

    /**
     * 版本号
     */
    @Schema(description = "版本号")
    private String version;

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
     * 1:上架 2:下架 3:审核中
     */
    @Schema(description = "0:未上架 1:上架 2:下架 3:审核中,待审核 4:历史版本 5:驳回")
    private Short staus;

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

    @Schema(description = "删除标识 1:删除 0:未删除")
    private Short del;

    @Schema(description = "标签名称")
    private String connLabelName;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "简要概述")
    private String sketchOut;
}
