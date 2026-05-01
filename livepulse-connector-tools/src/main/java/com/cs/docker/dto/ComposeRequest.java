package com.cs.docker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Docker Compose 操作请求
 *
 * @author cs
 * @since 1.0.0
 */
@Data
public class ComposeRequest {

    /**
     * docker-compose.yml 文件所在目录
     */
    @NotBlank(message = "compose 文件目录不能为空")
    private String composeDir;

    /**
     * docker-compose 文件名（默认: docker-compose.yml）
     */
    private String composeFile = "docker-compose.yml";

    /**
     * 项目名称（默认使用目录名）
     */
    private String projectName;

    /**
     * 环境变量（可选）
     */
    private java.util.Map<String, String> environment;

    /**
     * 服务名称（用于操作单个服务）
     */
    private String serviceName;
}
