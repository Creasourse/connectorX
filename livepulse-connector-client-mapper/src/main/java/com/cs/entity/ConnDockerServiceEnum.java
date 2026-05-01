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
 * 插件在docker中的服务名称
 * </p>
 *
 * @author wwd
 * @since 2026-04-27
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName("conn_docker_service_enum")
@Schema(name = "ConnDockerServiceEnum", description = "插件在docker中的服务名称")
public class ConnDockerServiceEnum extends Model<ConnDockerServiceEnum> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "conn_docker_service_enum_id", type = IdType.AUTO)
    private Long connDockerServiceEnumId;

    /**
     * 连接器名称
     */
    @TableField("connector_name")
    @Schema(description = "连接器名称")
    private String connectorName;

    /**
     * 插件在docker compose中的服务名称
     */
    @TableField("service_name")
    @Schema(description = "插件在docker compose中的服务名称")
    private String serviceName;

    @Override
    public Serializable pkVal() {
        return this.connDockerServiceEnumId;
    }
}
