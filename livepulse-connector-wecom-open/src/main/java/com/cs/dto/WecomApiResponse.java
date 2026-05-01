package com.cs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @ClassName: WecomApiResponse
 * @Author: wwd
 * @TODO: 企业微信API统一响应格式
 * @Date: 2026/3/21
 */
@Data
public class WecomApiResponse<T> {

    @JsonProperty("errcode")
    private Integer errcode;

    @JsonProperty("errmsg")
    private String errmsg;

    private T data;

    // 注意：这里的属性名必须与JSON中的键完全一致
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Integer expiresIn;

    public boolean isSuccess() {
        return errcode != null && errcode == 0;
    }
}