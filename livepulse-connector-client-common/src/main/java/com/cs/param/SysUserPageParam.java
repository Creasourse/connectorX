package com.cs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName: SysUserPageParam
 * @Author: wwd
 * @TODO:
 * @Date: 2026/3/5 18:09
 */
@Schema
@Data
public class SysUserPageParam {

    @Schema(description = "分页参数")
    private PageQueryParam pageQueryParam;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    private String userName;

    /**
     * 1:客户 2:开发者 3:管理者
     */
    @Schema(description = "1:客户 2:开发者 3:管理者")
    private Short type;
}
