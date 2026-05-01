package com.cs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: WecomEmployeeDto
 * @Author: wwd
 * @TODO: 企业微信员工DTO
 * @Date: 2026/3/21
 */
@Data
public class WecomEmployeeDto {

    @JsonProperty("userid")
    private String userid;

    @JsonProperty("name")
    private String name;

    @JsonProperty("alias")
    private String alias;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("email")
    private String email;

    @JsonProperty("department")
    private List<Long> department;

    @JsonProperty("order_in_dept")
    private List<Integer> orderInDept;

    @JsonProperty("position")
    private String position;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("telephone")
    private String telephone;

    @JsonProperty("enable")
    private Integer enable;

    @JsonProperty("is_leader")
    private Integer isLeader;

    @JsonProperty("extattr")
    private Map<String, Object> extattr;

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("qr_code")
    private String qrCode;

    @JsonProperty("external_profile")
    private ExternalProfile externalProfile;

    @JsonProperty("external_position")
    private String externalPosition;

    @Data
    public static class ExternalProfile {
        @JsonProperty("external_corp_name")
        private String externalCorpName;

        @JsonProperty("wechat_channel")
        private WechatChannel wechatChannel;

        @JsonProperty("external_attr")
        private List<ExternalAttr> externalAttr;
    }

    @Data
    public static class WechatChannel {
        @JsonProperty("type")
        private Integer type;
    }

    @Data
    public static class ExternalAttr {
        @JsonProperty("type")
        private Integer type;

        @JsonProperty("name")
        private String name;

        @JsonProperty("text")
        private TextAttr text;

        @JsonProperty("web")
        private WebAttr web;

        @JsonProperty("miniprogram")
        private MiniProgramAttr miniprogram;
    }

    @Data
    public static class TextAttr {
        @JsonProperty("value")
        private String value;
    }

    @Data
    public static class WebAttr {
        @JsonProperty("url")
        private String url;

        @JsonProperty("title")
        private String title;
    }

    @Data
    public static class MiniProgramAttr {
        @JsonProperty("appid")
        private String appid;

        @JsonProperty("pagepath")
        private String pagepath;

        @JsonProperty("title")
        private String title;
    }
}