package com.cs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: WecomTagGroupDto
 * @Author: wwd
 * @TODO: 企业微信标签组DTO
 * @Date: 2026/3/21
 */
@Data
public class WecomTagGroupDto {

    @JsonProperty("group_id")
    private String groupId;

    @JsonProperty("group_name")
    private String groupName;

    @JsonProperty("order")
    private Long order;

    @JsonProperty("tag")
    private List<WecomCorpTagDto> tag;
}