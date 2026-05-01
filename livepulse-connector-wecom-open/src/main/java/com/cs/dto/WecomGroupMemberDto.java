package com.cs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @ClassName: WecomGroupMemberDto
 * @Author: wwd
 * @TODO: 企业微信客户群成员DTO
 * @Date: 2026/3/21
 */
@Data
public class WecomGroupMemberDto {

    @JsonProperty("userid")
    private String userid;

    @JsonProperty("type")
    private Integer type;

    @JsonProperty("join_time")
    private Long joinTime;

    @JsonProperty("join_scene")
    private Integer joinScene;

    @JsonProperty("invitor")
    private String invitor;

    @JsonProperty("group_nickname")
    private String groupNickname;

    @JsonProperty("name")
    private String name;

    @JsonProperty("unionid")
    private String unionid;
}