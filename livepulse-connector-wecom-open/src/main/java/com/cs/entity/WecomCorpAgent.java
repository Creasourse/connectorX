package com.cs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 企业微信agent账户
 * </p>
 *
 * @author wwd
 * @since 2026-01-30
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("wecom_corp_agent")
@Schema(name = "WecomCorpAgent", description = "$!{table.comment}")
public class WecomCorpAgent extends Model<WecomCorpAgent> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "企业微信应用ID")
    @TableId(value = "wecom_corp_agent_id", type = IdType.AUTO)
    private Long wecomCorpAgentId;

    @Schema(description = "企业微信账户信息表ID")
    @TableField("wecome_corp_id")
    private Long wecomeCorpId;

    @Schema(description = "应该密钥")
    @TableField("agent_secret")
    private String agentSecret;

    @Override
    public Serializable pkVal() {
        return this.wecomCorpAgentId;
    }
}
