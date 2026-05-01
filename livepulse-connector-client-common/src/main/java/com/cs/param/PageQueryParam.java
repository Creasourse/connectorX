package com.cs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName: PageQueryParam
 * @Author: wwd
 * @TODO:
 * @Date: 2026/3/5 17:10
 */
@Schema
@Data
public class PageQueryParam {

    @Schema(description = "当前页码")
    private Integer pageNo = 1;

    @Schema(description = "单页大小")
    private Integer pageSize = 10;
}
