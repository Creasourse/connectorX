package com.cs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @ClassName: WecomCorpTagDto
 * @Author: wwd
 * @TODO: 企业微信标签DTO
 * @Date: 2026/3/21
 */
@Data
public class WecomCorpTagDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("order")
    private Long order;

    @JsonProperty("group_id")
    private String groupId;

    @JsonProperty("group_name")
    private String groupName;

    @JsonProperty("type")
    private Integer type;
}