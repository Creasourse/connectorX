package com.cs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @ClassName: WecomExternalContactDto
 * @Author: wwd
 * @TODO: 企业微信外部联系人DTO
 * @Date: 2026/3/21
 */
@Data
public class WecomExternalContactDto {

    @JsonProperty("external_userid")
    private String externalUserid;

    @JsonProperty("name")
    private String name;

    @JsonProperty("position")
    private String position;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("type")
    private Integer type;

    @JsonProperty("gender")
    private Integer gender;

    @JsonProperty("unionid")
    private String unionid;

    @JsonProperty("corp_name")
    private String corpName;

    @JsonProperty("corp_full_name")
    private String corpFullName;
}