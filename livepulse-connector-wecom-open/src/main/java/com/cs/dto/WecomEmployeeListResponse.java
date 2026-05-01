package com.cs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: WecomEmployeeListResponse
 * @Author: wwd
 * @TODO: 企业微信员工列表响应
 * @Date: 2026/3/21
 */
@Data
public class WecomEmployeeListResponse {

    @JsonProperty("errcode")
    private Integer errcode;

    @JsonProperty("errmsg")
    private String errmsg;

    @JsonProperty("userlist")
    private List<WecomEmployeeDto> userlist;
}