package com.cs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName: LocalConnectorPageParam
 * @Author: wwd
 * @TODO:
 * @Date: 2026/3/11 10:26
 */
@Schema
@Data
public class LocalConnectorPageParam {

    @Schema(description = "当前页码")
    private Integer currentPage;

    @Schema(description = "每页显示数量")
    private Integer pageSize;

    @Schema(description = "连接器名称")
    private String connectorName;

    @Schema(description = "排序字段 1:创建时间倒序 2:创建时间正序 3:更新时间倒序 4:更新时间正序")
    private Integer sortType;
}
