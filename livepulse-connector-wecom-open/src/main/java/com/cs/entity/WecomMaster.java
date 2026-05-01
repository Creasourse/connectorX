package com.cs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author wwd
 * @since 2026-03-26
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName("wecom_master")
@Schema(name = "WecomMaster", description = "")
public class WecomMaster extends Model<WecomMaster> {

    private static final long serialVersionUID = 1L;

    /**
     * 企业微信主表ID
     */
    @Schema(description = "企业微信主表ID")
    @TableId(value = "wecom_mater_id", type = IdType.AUTO)
    private Long wecomMaterId;

    /**
     * 用户ID
     */
    @TableField("userId")
    @Schema(description = "用户ID")
    private String userId;

    /**
     * 微信唯一ID
     */
    @TableField("unionId")
    @Schema(description = "微信唯一ID")
    private String unionId;

    /**
     * 外部联系人userid
     */
    @TableField("external_userid")
    @Schema(description = "外部联系人userid")
    private String externalUserid;

    @Override
    public Serializable pkVal() {
        return this.wecomMaterId;
    }
}
