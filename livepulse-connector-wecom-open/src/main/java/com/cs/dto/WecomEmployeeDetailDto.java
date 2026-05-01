package com.cs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: WecomEmployeeDetailDto
 * @Author: wwd
 * @TODO: 企业微信员工详细信息DTO
 * @Date: 2026/3/21
 */
@Data
public class WecomEmployeeDetailDto {

    @JsonProperty("userid")
    private String userid;

    @JsonProperty("english_name")
    private String englishName;

    @JsonProperty("address")
    private String address;

    @JsonProperty("direct_leader")
    private List<String> directLeader;

    @JsonProperty("main_department")
    private Long mainDepartment;

    @JsonProperty("hide")
    private Integer hide;

    @JsonProperty("senior")
    private Integer senior;

    @JsonProperty("jointime")
    private Long jointime;

    @JsonProperty("first_party")
    private Integer firstParty;
}