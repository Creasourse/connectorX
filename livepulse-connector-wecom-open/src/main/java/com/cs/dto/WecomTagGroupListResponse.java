package com.cs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: WecomTagGroupListResponse
 * @Author: wwd
 * @TODO: 企业微信标签组列表响应
 * @Date: 2026/3/21
 */
@Data
public class WecomTagGroupListResponse {

    @JsonProperty("errcode")
    private Integer errcode;

    @JsonProperty("errmsg")
    private String errmsg;

    @JsonProperty("tag_group")
    private List<WecomTagGroupDto> tagGroup;
}