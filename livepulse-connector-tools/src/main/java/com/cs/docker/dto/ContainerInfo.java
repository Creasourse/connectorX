package com.cs.docker.dto;

import lombok.Data;

/**
 * 容器信息
 *
 * @author cs
 * @since 1.0.0
 */
@Data
public class ContainerInfo {

    /**
     * 容器 ID
     */
    private String containerId;

    /**
     * 容器名称
     */
    private String name;

    /**
     * 镜像名称
     */
    private String image;

    /**
     * 状态
     */
    private String status;

    /**
     * 状态类型
     */
    private State state;

    /**
     * 创建时间
     */
    private Long created;

    /**
     * 端口映射
     */
    private String ports;

    /**
     * 容器状态枚举
     */
    public enum State {
        /**
         * 运行中
         */
        RUNNING,

        /**
         * 已暂停
         */
        PAUSED,

        /**
         * 已停止
         */
        STOPPED,

        /**
         * 未知状态
         */
        UNKNOWN
    }
}
