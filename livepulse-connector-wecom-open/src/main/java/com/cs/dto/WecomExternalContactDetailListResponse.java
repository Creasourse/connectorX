package com.cs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: WecomExternalContactDetailListResponse
 * @Author: wwd
 * @TODO: 企业微信外部联系人详情列表响应
 * @Date: 2026/3/21
 */
@Data
public class WecomExternalContactDetailListResponse {

    @JsonProperty("errcode")
    private Integer errcode;

    @JsonProperty("errmsg")
    private String errmsg;

    @JsonProperty("external_contact_list")
    private List<ExternalContactItem> externalContactList;

    @Data
    public static class ExternalContactItem {
        @JsonProperty("external_contact")
        private WecomExternalContactDto externalContact;

        @JsonProperty("follow_user")
        private String followUser;

        @JsonProperty("add_time")
        private Long addTime;

        @JsonProperty("remark")
        private String remark;

        @JsonProperty("description")
        private String description;

        @JsonProperty("remark_company")
        private String remarkCompany;

        @JsonProperty("remark_mobiles")
        private List<String> remarkMobiles;

        @JsonProperty("remark_pic_mediaid")
        private String remarkPicMediaid;

        @JsonProperty("tags")
        private List<TagItem> tags;

        @JsonProperty("chat_id")
        private String chatId;

        @JsonProperty("unionid")
        private String unionid;
    }

    @Data
    public static class TagItem {
        @JsonProperty("tag_id")
        private String tagId;

        @JsonProperty("group_name")
        private String groupName;

        @JsonProperty("tag_name")
        private String tagName;

        @JsonProperty("tag_type")
        private String tagType;
    }
}