package com.cs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: WecomDepartmentDto
 * @Author: wwd
 * @TODO: 企业微信部门DTO
 * @Date: 2026/3/21
 */
@Data
public class WecomDepartmentDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("name_en")
    private String nameEn;

    @JsonProperty("parentid")
    private String parentId;

    @JsonProperty("order")
    private Long order;

    @JsonProperty("department_leader")
    private List<String> departmentLeader;
}