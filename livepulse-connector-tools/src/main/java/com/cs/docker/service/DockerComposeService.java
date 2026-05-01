package com.cs.docker.service;

import cn.hutool.json.JSONUtil;
import com.cs.docker.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Docker 容器管理服务
 * 支持两种模式：
 * 1. Compose 模式：通过 docker-compose 命令管理
 * 2. Direct 模式：直接通过 docker 命令管理容器
 *
 * @author cs
 * @since 1.0.0
 */
@Slf4j
@Service
public class DockerComposeService {

    /**
     * Docker Compose 命令格式
     * v1: docker-compose
     * v2: docker compose
     */
    @Value("${docker.compose.command:v2}")
    private String dockerComposeCommand;

    /**
     * 管理模式
     * compose: 使用 docker-compose 管理
     * direct: 直接使用 docker 命令管理
     */
    @Value("${docker.compose.mode:direct}")
    private String managementMode;

    /**
     * 要管理的容器名称前缀列表（direct 模式使用）
     * 多个前缀用逗号分隔
     */
    @Value("${docker.compose.managed-prefixes:connector-}")
    private String managedPrefixes;

    /**
     * 是否使用 V2 格式（docker compose）
     */
    private boolean isV2() {
        return "v2".equalsIgnoreCase(dockerComposeCommand) || "docker compose".equalsIgnoreCase(dockerComposeCommand);
    }

    /**
     * 是否使用 Direct 模式
     */
    private boolean isDirectMode() {
        return "direct".equalsIgnoreCase(managementMode);
    }

    /**
     * 获取管理的容器前缀列表
     */
    private List<String> getManagedPrefixes() {
        if (managedPrefixes == null || managedPrefixes.isEmpty()) {
            return Collections.singletonList("connector-");
        }
        return Arrays.asList(managedPrefixes.split(","));
    }

    /**
     * 启动 Docker 容器
     *
     * @param request 请求参数
     * @return 操作结果
     */
    public Map<String, Object> startContainers(ComposeRequest request) {
        log.info("开始启动 Docker 容器");

        try {
            if (isDirectMode()) {
                return startContainersDirect(request);
            } else {
                return startContainersCompose(request);
            }
        } catch (Exception e) {
            log.error("启动容器失败", e);
            throw new RuntimeException("启动容器失败: " + e.getMessage());
        }
    }

    /**
     * 使用 Docker 命令启动容器
     */
    private Map<String, Object> startContainersDirect(ComposeRequest request) {
        log.info("使用 Direct 模式启动容器，服务名称: {}", request.getServiceName());

        try {
            List<String> containerIds = getManagedContainerIds(request.getServiceName());
            List<ContainerInfo> startedContainers = new ArrayList<>();

            for (String containerId : containerIds) {
                try {
                    // 检查容器状态
                    ContainerInfo info = getContainerInfo(containerId);

                    if (info.getState() == ContainerInfo.State.STOPPED ||
                        info.getState() == ContainerInfo.State.UNKNOWN) {

                        // 启动容器
                        List<String> command = Arrays.asList("docker", "start", containerId);
                        executeCommand(command, null);

                        log.info("容器已启动: {}", info.getName());
                        startedContainers.add(getContainerInfo(containerId));
                    } else {
                        log.info("容器已在运行: {}", info.getName());
                        startedContainers.add(info);
                    }

                } catch (Exception e) {
                    log.error("启动容器失败: {}", containerId, e);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "容器启动成功");
            response.put("containers", startedContainers);
            response.put("count", startedContainers.size());

            return response;

        } catch (Exception e) {
            log.error("启动容器失败", e);
            throw new RuntimeException("启动容器失败: " + e.getMessage());
        }
    }

    /**
     * 使用 Docker Compose 启动容器
     */
    private Map<String, Object> startContainersCompose(ComposeRequest request) {
        log.info("使用 Compose 模式启动容器，目录: {}", request.getComposeDir());

        try {
            File composeDir = new File(request.getComposeDir());
            if (!composeDir.exists()) {
                throw new RuntimeException("Docker Compose 目录不存在: " + request.getComposeDir());
            }

            File composeFile = new File(composeDir, request.getComposeFile());
            if (!composeFile.exists()) {
                throw new RuntimeException("Docker Compose 文件不存在: " + composeFile.getAbsolutePath());
            }

            // 执行 docker-compose up -d
            List<String> command = buildDockerComposeCommand(request, "up", "-d");

            // 如果指定了服务名称，添加服务名
            if (request.getServiceName() != null && !request.getServiceName().isEmpty()) {
                command.add(request.getServiceName());
            }

            ProcessResult result = executeCommand(command, composeDir);

            if (result.getExitCode() != 0) {
                throw new RuntimeException("启动容器失败: " + result.getError());
            }

            // 获取容器状态
            List<ContainerInfo> containers = getContainerStatus(request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "容器启动成功");
            response.put("containers", containers);
            response.put("count", containers.size());
            response.put("output", result.getOutput());

            return response;

        } catch (Exception e) {
            log.error("启动容器失败", e);
            throw new RuntimeException("启动容器失败: " + e.getMessage());
        }
    }

    /**
     * 暂停 Docker 容器
     *
     * @param request 请求参数
     * @return 操作结果
     */
    public Map<String, Object> pauseContainers(ComposeRequest request) {
        log.info("开始暂停容器");

        try {
            if (isDirectMode()) {
                return pauseContainersDirect(request);
            } else {
                return pauseContainersCompose(request);
            }
        } catch (Exception e) {
            log.error("暂停容器失败", e);
            throw new RuntimeException("暂停容器失败: " + e.getMessage());
        }
    }

    private Map<String, Object> pauseContainersDirect(ComposeRequest request) {
        log.info("使用 Direct 模式暂停容器，服务名称: {}", request.getServiceName());

        List<String> containerIds = getManagedContainerIds(request.getServiceName());
        List<ContainerInfo> pausedContainers = new ArrayList<>();

        for (String containerId : containerIds) {
            try {
                List<String> command = Arrays.asList("docker", "pause", containerId);
                executeCommand(command, null);

                ContainerInfo info = getContainerInfo(containerId);
                pausedContainers.add(info);
                log.info("容器已暂停: {}", info.getName());
            } catch (Exception e) {
                log.error("暂停容器失败: {}", containerId, e);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "容器暂停成功");
        response.put("containers", pausedContainers);
        response.put("count", pausedContainers.size());

        return response;
    }

    private Map<String, Object> pauseContainersCompose(ComposeRequest request) {
        log.info("使用 Compose 模式暂停容器，目录: {}", request.getComposeDir());

        try {
            File composeDir = new File(request.getComposeDir());
            List<String> command = buildDockerComposeCommand(request, "pause");

            if (request.getServiceName() != null && !request.getServiceName().isEmpty()) {
                command.add(request.getServiceName());
            }

            ProcessResult result = executeCommand(command, composeDir);

            if (result.getExitCode() != 0) {
                throw new RuntimeException("暂停容器失败: " + result.getError());
            }

            List<ContainerInfo> containers = getContainerStatus(request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "容器暂停成功");
            response.put("containers", containers);
            response.put("count", containers.size());

            return response;

        } catch (Exception e) {
            log.error("暂停容器失败", e);
            throw new RuntimeException("暂停容器失败: " + e.getMessage());
        }
    }

    /**
     * 恢复 Docker 容器
     *
     * @param request 请求参数
     * @return 操作结果
     */
    public Map<String, Object> unpauseContainers(ComposeRequest request) {
        log.info("开始恢复容器");

        try {
            if (isDirectMode()) {
                return unpauseContainersDirect(request);
            } else {
                return unpauseContainersCompose(request);
            }
        } catch (Exception e) {
            log.error("恢复容器失败", e);
            throw new RuntimeException("恢复容器失败: " + e.getMessage());
        }
    }

    private Map<String, Object> unpauseContainersDirect(ComposeRequest request) {
        log.info("使用 Direct 模式恢复容器，服务名称: {}", request.getServiceName());

        List<String> containerIds = getManagedContainerIds(request.getServiceName());
        List<ContainerInfo> unpausedContainers = new ArrayList<>();

        for (String containerId : containerIds) {
            try {
                List<String> command = Arrays.asList("docker", "unpause", containerId);
                executeCommand(command, null);

                ContainerInfo info = getContainerInfo(containerId);
                unpausedContainers.add(info);
                log.info("容器已恢复: {}", info.getName());
            } catch (Exception e) {
                log.error("恢复容器失败: {}", containerId, e);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "容器恢复成功");
        response.put("containers", unpausedContainers);
        response.put("count", unpausedContainers.size());

        return response;
    }

    private Map<String, Object> unpauseContainersCompose(ComposeRequest request) {
        log.info("使用 Compose 模式恢复容器，目录: {}", request.getComposeDir());

        try {
            File composeDir = new File(request.getComposeDir());
            List<String> command = buildDockerComposeCommand(request, "unpause");

            if (request.getServiceName() != null && !request.getServiceName().isEmpty()) {
                command.add(request.getServiceName());
            }

            ProcessResult result = executeCommand(command, composeDir);

            if (result.getExitCode() != 0) {
                throw new RuntimeException("恢复容器失败: " + result.getError());
            }

            List<ContainerInfo> containers = getContainerStatus(request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "容器恢复成功");
            response.put("containers", containers);
            response.put("count", containers.size());

            return response;

        } catch (Exception e) {
            log.error("恢复容器失败", e);
            throw new RuntimeException("恢复容器失败: " + e.getMessage());
        }
    }

    /**
     * 停止 Docker 容器
     *
     * @param request 请求参数
     * @return 操作结果
     */
    public Map<String, Object> stopContainers(ComposeRequest request) {
        log.info("开始停止容器");

        try {
            if (isDirectMode()) {
                return stopContainersDirect(request);
            } else {
                return stopContainersCompose(request);
            }
        } catch (Exception e) {
            log.error("停止容器失败", e);
            throw new RuntimeException("停止容器失败: " + e.getMessage());
        }
    }

    private Map<String, Object> stopContainersDirect(ComposeRequest request) {
        log.info("使用 Direct 模式停止容器，服务名称: {}", request.getServiceName());

        List<String> containerIds = getManagedContainerIds(request.getServiceName());
        List<ContainerInfo> stoppedContainers = new ArrayList<>();

        for (String containerId : containerIds) {
            try {
                List<String> command = Arrays.asList("docker", "stop", containerId);
                executeCommand(command, null);

                ContainerInfo info = getContainerInfo(containerId);
                stoppedContainers.add(info);
                log.info("容器已停止: {}", info.getName());
            } catch (Exception e) {
                log.error("停止容器失败: {}", containerId, e);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "容器停止成功");
        response.put("containers", stoppedContainers);
        response.put("count", stoppedContainers.size());

        return response;
    }

    private Map<String, Object> stopContainersCompose(ComposeRequest request) {
        log.info("使用 Compose 模式停止容器，目录: {}", request.getComposeDir());

        try {
            File composeDir = new File(request.getComposeDir());
            List<String> command = buildDockerComposeCommand(request, "stop");

            if (request.getServiceName() != null && !request.getServiceName().isEmpty()) {
                command.add(request.getServiceName());
            }

            ProcessResult result = executeCommand(command, composeDir);

            if (result.getExitCode() != 0) {
                throw new RuntimeException("停止容器失败: " + result.getError());
            }

            List<ContainerInfo> containers = getContainerStatus(request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "容器停止成功");
            response.put("containers", containers);
            response.put("count", containers.size());

            return response;

        } catch (Exception e) {
            log.error("停止容器失败", e);
            throw new RuntimeException("停止容器失败: " + e.getMessage());
        }
    }

    /**
     * 停止并删除 Docker 容器
     *
     * @param request 请求参数
     * @return 操作结果
     */
    public Map<String, Object> removeContainers(ComposeRequest request) {
        log.info("开始删除容器");

        try {
            if (isDirectMode()) {
                return removeContainersDirect(request);
            } else {
                return removeContainersCompose(request);
            }
        } catch (Exception e) {
            log.error("删除容器失败", e);
            throw new RuntimeException("删除容器失败: " + e.getMessage());
        }
    }

    private Map<String, Object> removeContainersDirect(ComposeRequest request) {
        log.info("使用 Direct 模式删除容器，服务名称: {}", request.getServiceName());

        List<String> containerIds = getManagedContainerIds(request.getServiceName());
        List<String> removedContainers = new ArrayList<>();

        for (String containerId : containerIds) {
            try {
                // 先停止
                try {
                    List<String> stopCmd = Arrays.asList("docker", "stop", containerId);
                    executeCommand(stopCmd, null);
                } catch (Exception e) {
                    log.warn("停止容器超时或失败: {}", containerId);
                }

                // 删除容器
                List<String> rmCmd = Arrays.asList("docker", "rm", "-f", containerId);
                executeCommand(rmCmd, null);

                String name = getContainerName(containerId);
                removedContainers.add(name != null ? name : containerId);
                log.info("容器已删除: {}", name);
            } catch (Exception e) {
                log.error("删除容器失败: {}", containerId, e);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "容器删除成功");
        response.put("containers", removedContainers);
        response.put("count", removedContainers.size());

        return response;
    }

    private Map<String, Object> removeContainersCompose(ComposeRequest request) {
        log.info("使用 Compose 模式删除容器，目录: {}", request.getComposeDir());

        try {
            File composeDir = new File(request.getComposeDir());
            List<String> command = buildDockerComposeCommand(request, "down");

            if (request.getServiceName() != null && !request.getServiceName().isEmpty()) {
                // down 命令不支持指定服务，需要先 stop 再 rm
                List<String> stopCmd = buildDockerComposeCommand(request, "stop");
                stopCmd.add(request.getServiceName());
                executeCommand(stopCmd, composeDir);

                List<String> rmCmd = buildDockerComposeCommand(request, "rm", "-f");
                rmCmd.add(request.getServiceName());
                ProcessResult result = executeCommand(rmCmd, composeDir);

                if (result.getExitCode() != 0) {
                    throw new RuntimeException("删除容器失败: " + result.getError());
                }
            } else {
                ProcessResult result = executeCommand(command, composeDir);

                if (result.getExitCode() != 0) {
                    throw new RuntimeException("删除容器失败: " + result.getError());
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "容器删除成功");
            response.put("containers", Collections.emptyList());
            response.put("count", 0);

            return response;

        } catch (Exception e) {
            log.error("删除容器失败", e);
            throw new RuntimeException("删除容器失败: " + e.getMessage());
        }
    }

    /**
     * 查看容器状态
     *
     * @param request 请求参数
     * @return 容器状态列表
     */
    public List<ContainerInfo> getContainerStatus(ComposeRequest request) {
        log.info("查看容器状态");

        try {
            if (isDirectMode()) {
                return getContainerStatusDirect(request.getServiceName());
            } else {
                return getContainerStatusCompose(request);
            }
        } catch (Exception e) {
            log.error("获取容器状态失败", e);
            throw new RuntimeException("获取容器状态失败: " + e.getMessage());
        }
    }

    /**
     * 使用 Docker 命令获取容器状态
     */
    private List<ContainerInfo> getContainerStatusDirect() {
        return getContainerStatusDirect(null);
    }

    /**
     * 使用 Docker 命令获取容器状态
     *
     * @param serviceName 服务名称（可选）
     */
    private List<ContainerInfo> getContainerStatusDirect(String serviceName) {
        log.info("使用 Direct 模式获取容器状态，服务名称: {}", serviceName);

        try {
            List<String> containerIds = getManagedContainerIds(serviceName);
            List<ContainerInfo> containers = new ArrayList<>();

            for (String containerId : containerIds) {
                try {
                    ContainerInfo info = getContainerInfo(containerId);
                    containers.add(info);
                } catch (Exception e) {
                    log.warn("获取容器信息失败: {}", containerId);
                }
            }

            return containers;

        } catch (Exception e) {
            log.error("获取容器状态失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 使用 Docker Compose 获取容器状态
     */
    private List<ContainerInfo> getContainerStatusCompose(ComposeRequest request) {
        log.info("使用 Compose 模式获取容器状态，目录: {}", request.getComposeDir());

        try {
            File composeDir = new File(request.getComposeDir());
            List<String> command = buildDockerComposeCommand(request, "ps");
            log.info("执行命令: {}", JSONUtil.toJsonStr(command));

            ProcessResult result = executeCommand(command, composeDir);
            log.info("命令执行结果: {}", JSONUtil.toJsonStr(result));
            if (result.getExitCode() != 0) {
                log.warn("获取容器状态失败: {}", result.getError());
                return Collections.emptyList();
            }

            // 解析 docker-compose ps 输出
            return parseComposePsOutput(result.getOutput());

        } catch (Exception e) {
            log.error("获取容器状态失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 查看容器日志
     *
     * @param request 请求参数
     * @param tail    日志行数
     * @return 日志结果列表
     */
    public List<LogsResult> getContainerLogs(ComposeRequest request, Integer tail) {
        log.info("查看容器日志，行数: {}", tail);

        if (tail == null || tail <= 0) {
            tail = 100;
        }

        try {
            if (isDirectMode()) {
                return getContainerLogsDirect(request, tail);
            } else {
                return getContainerLogsCompose(request, tail);
            }
        } catch (Exception e) {
            log.error("获取容器日志失败", e);
            throw new RuntimeException("获取容器日志失败: " + e.getMessage());
        }
    }

    /**
     * 使用 Docker 命令获取日志
     */
    private List<LogsResult> getContainerLogsDirect(ComposeRequest request, Integer tail) {
        log.info("使用 Direct 模式获取容器日志，服务名称: {}", request.getServiceName());

        try {
            List<String> containerIds = getManagedContainerIds(request.getServiceName());
            List<LogsResult> logsResults = new ArrayList<>();

            for (String containerId : containerIds) {
                try {
                    String name = getContainerName(containerId);

                    // 获取日志
                    List<String> command = Arrays.asList(
                        "docker", "logs",
                        "--tail=" + tail,
                        containerId
                    );

                    ProcessResult result = executeCommand(command, null);

                    if (result.getExitCode() == 0) {
                        logsResults.add(new LogsResult(name, result.getOutput()));
                    } else {
                        logsResults.add(new LogsResult(name, "获取日志失败: " + result.getOutput()));
                    }

                } catch (Exception e) {
                    log.error("获取容器日志失败: {}", containerId, e);
                    logsResults.add(new LogsResult(containerId, "获取日志失败: " + e.getMessage()));
                }
            }

            return logsResults;

        } catch (Exception e) {
            log.error("获取容器日志失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 使用 Docker Compose 获取日志
     */
    private List<LogsResult> getContainerLogsCompose(ComposeRequest request, Integer tail) {
        log.info("使用 Compose 模式获取容器日志，目录: {}", request.getComposeDir());

        try {
            File composeDir = new File(request.getComposeDir());

            // 获取服务列表
            List<String> services = getServiceNames(request);

            List<LogsResult> logsResults = new ArrayList<>();

            for (String service : services) {
                try {
                    List<String> command = buildDockerComposeCommand(request, "logs", "--tail=" + tail);

                    if (request.getServiceName() != null && !request.getServiceName().isEmpty()) {
                        if (!service.equals(request.getServiceName())) {
                            continue;
                        }
                    }

                    command.add(service);

                    ProcessResult result = executeCommand(command, composeDir);

                    if (result.getExitCode() == 0) {
                        logsResults.add(new LogsResult(service, result.getOutput()));
                    } else {
                        logsResults.add(new LogsResult(service, "获取日志失败: " + result.getError()));
                    }

                } catch (Exception e) {
                    log.error("获取服务日志失败: {}", service, e);
                    logsResults.add(new LogsResult(service, "获取日志失败: " + e.getMessage()));
                }
            }

            return logsResults;

        } catch (Exception e) {
            log.error("获取容器日志失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取服务名称列表
     */
    private List<String> getServiceNames(ComposeRequest request) {
        try {
            File composeDir = new File(request.getComposeDir());
            List<String> command = buildDockerComposeCommand(request, "ps", "--services");

            ProcessResult result = executeCommand(command, composeDir);

            if (result.getExitCode() != 0) {
                return Collections.emptyList();
            }

            String[] lines = result.getOutput().split("\n");
            List<String> services = new ArrayList<>();

            for (String line : lines) {
                String service = line.trim();
                if (!service.isEmpty()) {
                    services.add(service);
                }
            }

            return services;

        } catch (Exception e) {
            log.error("获取服务列表失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 构建 docker-compose 命令
     */
    private List<String> buildDockerComposeCommand(ComposeRequest request, String... args) {
        List<String> command = new ArrayList<>();

        // 根据配置使用不同的命令格式
        if (isV2()) {
            // V2: docker compose
            command.add("docker");
            command.add("compose");
            log.debug("使用 Docker Compose V2 格式");
        } else {
            // V1: docker-compose
            command.add("docker-compose");
            log.debug("使用 Docker Compose V1 格式");
        }

        // 添加项目名称
        if (request.getProjectName() != null && !request.getProjectName().isEmpty()) {
            command.add("-p");
            command.add(request.getProjectName());
        }

        // 指定 compose 文件
        if (request.getComposeFile() != null && !request.getComposeFile().equals("docker-compose.yml")) {
            command.add("-f");
            command.add(request.getComposeFile());
        }

        // 添加环境变量
        if (request.getEnvironment() != null && !request.getEnvironment().isEmpty()) {
            // 环境变量需要在执行命令时设置，这里暂不处理
        }

        // 添加命令参数
        command.addAll(Arrays.asList(args));

        return command;
    }

    /**
     * 执行命令
     */
    private ProcessResult executeCommand(List<String> command, File workingDir) throws IOException, InterruptedException {
        log.debug("执行命令: {}，工作目录: {}", command, workingDir);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(workingDir);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();
        StringBuilder error = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();

        log.debug("命令执行完成，退出码: {}", exitCode);
        log.debug("输出: {}", output);

        return new ProcessResult(exitCode, output.toString(), error.toString());
    }

    /**
     * 解析 docker-compose ps 输出
     */
    private List<ContainerInfo> parseComposePsOutput(String output) {
        List<ContainerInfo> containers = new ArrayList<>();

        if (output == null || output.trim().isEmpty()) {
            return containers;
        }

        String[] lines = output.split("\n");
        boolean headerSkipped = false;

        for (String line : lines) {
            line = line.trim();

            // 跳过标题行
            if (!headerSkipped) {
                headerSkipped = true;
                continue;
            }

            if (line.isEmpty()) {
                continue;
            }

            try {
                ContainerInfo info = parsePsLine(line);
                if (info != null) {
                    containers.add(info);
                }
            } catch (Exception e) {
                log.warn("解析容器信息失败: {}", line, e);
            }
        }

        return containers;
    }

    /**
     * 解析单行 ps 输出
     */
    private ContainerInfo parsePsLine(String line) {
        // docker-compose ps 输出格式：
        // NAME                    IMAGE                     STATUS                    PORTS
        // myproject-nginx-1       nginx:latest              Up 2 minutes              0.0.0.0:8080->80/tcp

        String[] parts = line.split("\\s{2,}"); // 使用2个或更多空格分割
        if (parts.length < 3) {
            return null;
        }

        ContainerInfo info = new ContainerInfo();
        info.setName(parts[0].trim());
        info.setImage(parts[1].trim());
        info.setStatus(parts[2].trim());

        // 解析状态
        info.setState(parseState(info.getStatus()));

        // 解析端口（如果有）
        if (parts.length > 3) {
            info.setPorts(parts[3].trim());
        } else {
            info.setPorts("");
        }

        return info;
    }

    /**
     * 解析容器状态
     */
    private ContainerInfo.State parseState(String status) {
        if (status == null) {
            return ContainerInfo.State.UNKNOWN;
        }

        String lowerStatus = status.toLowerCase();

        if (lowerStatus.contains("up")) {
            if (lowerStatus.contains("paused")) {
                return ContainerInfo.State.PAUSED;
            }
            return ContainerInfo.State.RUNNING;
        } else if (lowerStatus.contains("exited")) {
            return ContainerInfo.State.STOPPED;
        } else if (lowerStatus.contains("paused")) {
            return ContainerInfo.State.PAUSED;
        }

        return ContainerInfo.State.UNKNOWN;
    }

    /**
     * 命令执行结果
     */
    private static class ProcessResult {
        private final int exitCode;
        private final String output;
        private final String error;

        public ProcessResult(int exitCode, String output, String error) {
            this.exitCode = exitCode;
            this.output = output;
            this.error = error;
        }

        public int getExitCode() {
            return exitCode;
        }

        public String getOutput() {
            return output;
        }

        public String getError() {
            return error;
        }
    }

    /**
     * 获取管理的容器 ID 列表
     *
     * @param serviceName 服务名称（可选），如果指定则只返回匹配该服务的容器
     */
    private List<String> getManagedContainerIds(String serviceName) {
        try {
            List<String> containerIds = new ArrayList<>();
//            List<String> prefixes = getManagedPrefixes();

            // 获取所有容器
            List<String> command = Arrays.asList("docker", "ps", "-a");
            ProcessResult result = executeCommand(command, null);
            log.info("获取容器列表命令执行结果: {}", JSONUtil.toJsonStr(result));

            if (result.getExitCode() == 0) {
                String[] lines = result.getOutput().split("\n");
                for (String line : lines) {
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length >= 2) {
                        String containerId = parts[0];

                        // 获取容器名称进行过滤
                        try {
                            String name = getContainerName(containerId);

//                            // 检查是否匹配前缀
//                            if (!matchesAnyPrefix(name, prefixes)) {
//                                continue;
//                            }

                            // 如果指定了服务名，检查是否匹配
                            if (serviceName != null && !serviceName.isEmpty()) {
                                if (name != null && name.contains(serviceName)) {
                                    containerIds.add(containerId);
                                    log.debug("匹配容器: {} -> {}", serviceName, name);
                                }
                            } else {
                                // 没有指定服务名，添加所有匹配前缀的容器
                                containerIds.add(containerId);
                            }

                        } catch (Exception e) {
                            log.debug("检查容器失败: {}", containerId);
                        }
                    }
                }
            }

            log.info("筛选后的容器列表: {}", containerIds);
            return containerIds;
        } catch (Exception e) {
            log.error("获取容器列表失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取管理的容器 ID 列表（无服务名过滤）
     */
    private List<String> getManagedContainerIds() {
        return getManagedContainerIds(null);
    }

    /**
     * 检查容器名称是否匹配任何前缀
     */
    private boolean matchesAnyPrefix(String containerName, List<String> prefixes) {
        if (prefixes == null || prefixes.isEmpty()) {
            return true;
        }

        for (String prefix : prefixes) {
            if (containerName.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取容器名称
     */
    private String getContainerName(String containerId) {
        try {
            List<String> command = Arrays.asList("docker", "inspect", "-f", "{{.Name}}", containerId);
            ProcessResult result = executeCommand(command, null);
            if (result.getExitCode() == 0) {
                return result.getOutput().trim();
            }
        } catch (Exception e) {
            log.debug("获取容器名称失败: {}", containerId);
        }
        return null;
    }

    /**
     * 根据容器 ID 获取容器信息
     */
    private ContainerInfo getContainerInfo(String containerId) {
        try {
            // 获取容器名称
            String name = getContainerName(containerId);

            // 获取容器状态
            List<String> command = Arrays.asList("docker", "inspect",
                "-f", "{{.State.Status}}", containerId);
            ProcessResult result = executeCommand(command, null);

            String status = "";
            if (result.getExitCode() == 0) {
                status = result.getOutput().trim();
            }

            // 获取镜像
            command = Arrays.asList("docker", "inspect", "-f", "{{.Config.Image}}", containerId);
//            result = executeCommand(command, null);

            String image = "";
            if (result.getExitCode() == 0) {
                image = result.getOutput().trim();
            }

            // 获取端口
            command = Arrays.asList("docker", "inspect", "-f", "{{range $p, $conf := .NetworkSettings.Ports}}{{$p}}:{{(index $conf 0).HostPort}}{{end}}", containerId);
//            result = executeCommand(command, null);

            String ports = "";
            if (result.getExitCode() == 0) {
                ports = result.getOutput().trim();
            }

            ContainerInfo info = new ContainerInfo();
            info.setContainerId(containerId.substring(0, Math.min(12, containerId.length())));
            info.setName(name != null ? name : containerId);
            info.setImage(image);
            info.setStatus(status);
            info.setState(parseState(status));
            info.setPorts(ports);

            return info;

        } catch (Exception e) {
            log.error("获取容器信息失败: {}", containerId, e);
            ContainerInfo info = new ContainerInfo();
            info.setContainerId(containerId.substring(0, Math.min(12, containerId.length())));
            info.setName(containerId);
            info.setState(ContainerInfo.State.UNKNOWN);
            return info;
        }
    }
}
