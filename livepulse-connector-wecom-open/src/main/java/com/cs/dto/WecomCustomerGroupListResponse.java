package com.cs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: WecomCustomerGroupListResponse
 * @Author: wwd
 * @TODO: 企业微信客户群列表响应
 * @Date: 2026/3/21
 */
@Data
public class WecomCustomerGroupListResponse {

    @JsonProperty("errcode")
    private Integer errcode;

    @JsonProperty("errmsg")
    private String errmsg;

    @JsonProperty("chat_list")
    private List<String> chatList;

    @JsonProperty("chat_data")
    private List<GroupChatData> chatData;

    @Data
    public static class GroupChatData {
        @JsonProperty("chat_id")
        private String chatId;

        @JsonProperty("name")
        private String name;

        @JsonProperty("owner")
        private String owner;

        @JsonProperty("create_time")
        private Long createTime;

        @JsonProperty("notice")
        private String notice;

        @JsonProperty("member_count")
        private Integer memberCount;

        @JsonProperty("admin_list")
        private List<String> adminList;

        @JsonProperty("chat_announcement")
        private String chatAnnouncement;

        @JsonProperty("group_qrcode")
        private String groupQrcode;
    }
}