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
 * 企业微信部门信息表
 * </p>
 *
 * @author wwd
 * @since 2026-03-21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("wecom_departments")
@Schema(name = "WecomDepartment", description = "企业微信部门信息表")
public class WecomDepartment extends Model<WecomDepartment> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "企业微信账户ID")
    @TableField("wecom_corp_id")
    private Long wecomCorpId;

    @Schema(description = "部门ID")
    @TableField("department_id")
    private String departmentId;

    @Schema(description = "部门名称")
    @TableField("name")
    private String name;

    @Schema(description = "部门英文名称")
    @TableField("name_en")
    private String nameEn;

    @Schema(description = "父部门ID")
    @TableField("parent_id")
    private String parentId;

    @Schema(description = "在父部门中的次序值")
    @TableField(value = "\"order\"")
    private Integer order;

    @Schema(description = "部门负责人列表")
    @TableField("department_leader")
    private String departmentLeader;

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