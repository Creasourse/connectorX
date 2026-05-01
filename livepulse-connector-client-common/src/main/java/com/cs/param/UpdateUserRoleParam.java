package com.cs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName: UpdateUserRoleParam
 * @Author: wwd
 * @TODO:
 * @Date: 2026/3/5 11:31
 */
@Schema
@Data
public class UpdateUserRoleParam {

    /**
     * 用户ID主键
     */
    @Schema(description = "用户ID主键")
    private Long userId;

    /**
     * 1:客户 2:开发者 3:管理者
     */
    @Schema(description = "1:客户 2:开发者 3:管理者")
    private Short type;
}
