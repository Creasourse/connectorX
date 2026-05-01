package com.cs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: WecomDepartmentListResponse
 * @Author: wwd
 * @TODO: 企业微信部门列表响应
 * @Date: 2026/3/21
 */
@Data
public class WecomDepartmentListResponse {

    @JsonProperty("errcode")
    private Integer errcode;

    @JsonProperty("errmsg")
    private String errmsg;

    @JsonProperty("department")
    private List<WecomDepartmentDto> department;
}