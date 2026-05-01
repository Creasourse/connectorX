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
 * 企业微信员工详细信息表
 * </p>
 *
 * @author wwd
 * @since 2026-03-21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("wecom_employee_details")
@Schema(name = "WecomEmployeeDetail", description = "企业微信员工详细信息表")
public class WecomEmployeeDetail extends Model<WecomEmployeeDetail> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "员工基本信息表ID")
    @TableField("employee_id")
    private Long employeeId;

    @Schema(description = "成员UserID")
    @TableField("userid")
    private String userid;

    @Schema(description = "英文名")
    @TableField("english_name")
    private String englishName;

    @Schema(description = "地址")
    @TableField("address")
    private String address;

    @Schema(description = "直属上级UserID列表")
    @TableField("direct_leader")
    private String directLeader;

    @Schema(description = "主部门")
    @TableField("main_department")
    private String mainDepartment;

    @Schema(description = "是否隐藏：0-否，1-是")
    @TableField("hide")
    private Integer hide;

    @Schema(description = "是否为高管：0-否，1-是")
    @TableField("senior")
    private Integer senior;

    @Schema(description = "加入企业时间")
    @TableField("join_time")
    private Long joinTime;

    @Schema(description = "是否为党务：0-否，1-是")
    @TableField("first_party")
    private Integer firstParty;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    @Schema(description = "是否删除 0-否 1-是")
    @TableField("deleted")
    private Integer deleted;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}