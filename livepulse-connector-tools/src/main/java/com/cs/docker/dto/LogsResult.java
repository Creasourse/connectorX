package com.cs.docker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日志结果
 *
 * @author cs
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogsResult {

    /**
     * 容器名称
     */
    private String containerName;

    /**
     * 日志内容
     */
    private String logs;

    /**
     * 是否被截断
     */
    private Boolean truncated;

    public LogsResult(String containerName, String logs) {
        this.containerName = containerName;
        this.logs = logs;
        this.truncated = false;
    }
}
