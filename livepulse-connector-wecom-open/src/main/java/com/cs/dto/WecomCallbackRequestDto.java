package com.cs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: WecomCallbackRequestDto
 * @Author: wwd
 * @TODO: 企业微信回调请求数据传输对象
 * @Date: 2026/3/25
 */
@Data
@Schema(description = "企业微信回调请求")
public class WecomCallbackRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "企业微信的CorpID")
    private String toUserName;

    @Schema(description = "成员UserID")
    private String fromUserName;

    @Schema(description = "消息创建时间（时间戳）")
    private Long createTime;

    @Schema(description = "消息类型")
    private String msgType;

    @Schema(description = "事件类型")
    private String event;

    @Schema(description = "变更类型")
    private String changeType;

    @Schema(description = "成员UserID")
    private String userId;

    @Schema(description = "成员名称")
    private String name;

    @Schema(description = "所属部门")
    private String department;

    @Schema(description = "职位")
    private String position;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "激活状态")
    private Integer status;

    @Schema(description = "成员启用状态")
    private Integer enable;

    @Schema(description = "外部联系人ID")
    private String externalUserId;

    @Schema(description = "客户群ID")
    private String chatId;
}