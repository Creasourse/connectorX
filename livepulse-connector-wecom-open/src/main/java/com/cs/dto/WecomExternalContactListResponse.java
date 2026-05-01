package com.cs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: WecomExternalContactListResponse
 * @Author: wwd
 * @TODO: 企业微信外部联系人列表响应
 * @Date: 2026/3/21
 */
@Data
public class WecomExternalContactListResponse {

    @JsonProperty("errcode")
    private Integer errcode;

    @JsonProperty("errmsg")
    private String errmsg;

    @JsonProperty("external_userid_list")
    private List<String> externalUseridList;
}