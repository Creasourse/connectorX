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
import java.time.LocalDateTime;

/**
 * <p>
 * 企业微信账户表
 * </p>
 *
 * @author wwd
 * @since 2026-01-30
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("wecom_corp")
@Schema(name = "WecomCorp", description = "$!{table.comment}")
public class WecomCorp extends Model<WecomCorp> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "企业微信账户信息表")
    @TableId(value = "wecome_corp_id", type = IdType.AUTO)
    private Long wecomeCorpId;

    @Schema(description = "企业名称")
    @TableField("company_name")
    private String companyName;

    @Schema(description = "企业ID")
    @TableField("corp_id")
    private String corpId;

    @Schema(description = "通讯录密钥")
    @TableField("corp_secret")
    private String corpSecret;

    @Schema(description = "企业logo")
    @TableField("logo_url")
    private String logoUrl;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Override
    public Serializable pkVal() {
        return this.wecomeCorpId;
    }
}
