package com.cs.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: RespResult
 * @Author: wwd
 * @TODO:
 * @Date: 2026/1/30 19:43
 */
@Data
@Schema
public class RespResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "返回数据")
    private T data;

    @Schema(description = "操作标识")
    private boolean success;

    @Schema(description = "错误码")
    private String code = "";

    @Schema(description = "消息提示信息")
    private String msg;

    @Schema(description = "请求路径")
    private String requestUrl;

    @Schema(description = "错误简短信息",
            hidden = true
    )
    private String errorMsg;

    @Schema(description = "错误堆栈信息",
            hidden = true
    )
    private String[] errorDetails;

    @Schema(description = "请求响应耗时")
    private long elapsed = 0L;

    @Schema(description = "HTTP响应码",
            hidden = true
    )
    private int responseStatus = 200;

    public RespResult() {
    }

    public RespResult(T data) {
        this.data = data;
    }

    public static <T> RespResult<T> success() {
        return success("操作成功", null);
    }

    public static <T> RespResult<T> success(T data) {
        return success("操作成功", data);
    }

    public static <T> RespResult<T> success(String msg, T data) {
        RespResult<T> rs = new RespResult<T>(data);
        rs.setSuccess(true);
        rs.setMsg(msg);
        return rs;
    }

    public static <T> RespResult<T> failure() {
        return failure("error");
    }

    public static <T> RespResult<T> failure(String msg) {
        return failure(msg, null);
    }

    public static <T> RespResult<T> failure(String msg, T data) {
        RespResult<T> rs = new RespResult<T>(data);
        rs.setSuccess(false);
        rs.setMsg(msg);
        return rs;
    }
}
