package com.cs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName: ScheduledTaskProperties
 * @Author: wwd
 * @TODO: 调度任务配置属性
 * @Date: 2026/3/26
 */
@Data
@Component
@ConfigurationProperties(prefix = "wecom.scheduled")
public class ScheduledTaskProperties {

    /**
     * 是否启用调度任务
     */
    private Boolean enabled = true;

    /**
     * 部门+员工信息同步配置
     */
    private SyncConfig departmentAndEmployee;

    /**
     * 外部联系人同步配置
     */
    private SyncConfig externalContact;

    /**
     * 客户群同步配置
     */
    private SyncConfig customerGroup;

    /**
     * 企业标签库同步配置
     */
    private SyncConfig corpTag;

    /**
     * 同步配置
     */
    @Data
    public static class SyncConfig {
        /**
         * 是否启用
         */
        private Boolean enabled = true;

        /**
         * Cron表达式
         */
        private String cron;

        /**
         * 初始延迟（秒）
         */
        private Integer initialDelay = 60;

        /**
         * 固定延迟（秒）- 如果cron为空则使用此配置
         */
        private Integer fixedDelay;
    }
}