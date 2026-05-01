package com.cs.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: wwd
 * @date: 11/8/18 14:34
 * @description:
 */
@Schema(description = "分页结果模型")
@Getter
@Setter
public class PageResult<T> {

    @Schema(description = "当前页码")
    private long current = 1;

    @Schema(description = "总记录数")
    private long total;

    @Schema(description = "单页大小")
    private long size = 10;

    @Schema(description = "最后一页面页码")
    private long lastPageNo = total % size == 0 ? total / size : total / size + 1;

    @Schema(description = "页面数据")
    private List<T> list;


}
