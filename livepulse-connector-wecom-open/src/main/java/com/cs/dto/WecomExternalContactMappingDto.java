package com.cs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: WecomExternalContactMappingDto
 * @Author: wwd
 * @TODO: 企业微信外部联系人映射DTO
 * @Date: 2026/3/21
 */
@Data
public class WecomExternalContactMappingDto {

    @JsonProperty("external_userid")
    private String externalUserid;

    @JsonProperty("userid")
    private String userid;

    @JsonProperty("remark")
    private String remark;

    @JsonProperty("description")
    private String description;

    @JsonProperty("remark_crop_name")
    private String remarkCropName;

    @JsonProperty("remark_mobiles")
    private List<String> remarkMobiles;

    @JsonProperty("add_way")
    private Integer addWay;

    @JsonProperty("state")
    private String state;

    @JsonProperty("oper_userid")
    private String operUserid;

    @JsonProperty("unix_timestamp")
    private Long unixTimestamp;
}