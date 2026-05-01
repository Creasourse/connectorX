package com.cs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: WecomEmployeeDetailResponse
 * @Author: wwd
 * @TODO: 企业微信员工详情响应
 * @Date: 2026/3/21
 */
@Data
public class WecomEmployeeDetailResponse {

    @JsonProperty("errcode")
    private Integer errcode;

    @JsonProperty("errmsg")
    private String errmsg;

    @JsonProperty("userid")
    private String userid;

    @JsonProperty("name")
    private String name;

    @JsonProperty("english_name")
    private String englishName;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("email")
    private String email;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("telephone")
    private String telephone;

    @JsonProperty("alias")
    private String alias;

    @JsonProperty("address")
    private String address;

    @JsonProperty("open_userid")
    private String openUserid;

    @JsonProperty("main_department")
    private Long mainDepartment;

    @JsonProperty("department")
    private List<Long> department;

    @JsonProperty("order_in_dept")
    private List<Integer> orderInDept;

    @JsonProperty("position")
    private String position;

    @JsonProperty("direct_leader")
    private List<String> directLeader;

    @JsonProperty("hide")
    private Integer hide;

    @JsonProperty("senior")
    private Integer senior;

    @JsonProperty("telephone")
    private String telephoneShow;

    @JsonProperty("external_position")
    private String externalPosition;

    @JsonProperty("external_profile")
    private WecomEmployeeDto.ExternalProfile externalProfile;

    @JsonProperty("jointime")
    private Long jointime;

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("qr_code")
    private String qrCode;

    @JsonProperty("first_party")
    private Integer firstParty;
}