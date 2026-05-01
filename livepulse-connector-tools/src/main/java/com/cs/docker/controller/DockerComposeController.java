package com.cs.docker.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cs.annotation.TokenSign;
import com.cs.docker.dto.*;
import com.cs.docker.service.DockerComposeService;
import com.cs.entity.ConnDockerServiceEnum;
import com.cs.resp.RespResult;
import com.cs.service.ConnDockerServiceEnumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Docker Compose 管理接口
 *
 * @author cs
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/docker-compose")
@RequiredArgsConstructor
@Tag(name = "插件操作docker容器的相关接口")
public class DockerComposeController {

    private final DockerComposeService dockerComposeService;

    @Value("${docker.compose.dir}")
    private String composeDir;

    @Autowired
    private ConnDockerServiceEnumService connDockerServiceEnumService;

    /**
     * 启动容器
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @PostMapping("/start")
    @Operation(summary = "启动容器")
    @TokenSign
    public RespResult<Map<String, Object>> startContainers(@Valid @RequestBody ComposeRequest request) {
        log.info("收到启动容器请求，目录: {}", request.getComposeDir());
        try {
            Map<String, Object> result = dockerComposeService.startContainers(request);
            return RespResult.success("容器启动成功", result);
        } catch (Exception e) {
            log.error("启动容器失败", e);
            return RespResult.failure("启动容器失败: " + e.getMessage());
        }
    }

    /**
     * 暂停容器
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @PostMapping("/pause")
    @Operation(summary = "暂停容器")
    @TokenSign
    public RespResult<Map<String, Object>> pauseContainers(@Valid @RequestBody ComposeRequest request) {
        log.info("收到暂停容器请求，目录: {}", request.getComposeDir());
        try {
            Map<String, Object> result = dockerComposeService.pauseContainers(request);
            return RespResult.success("容器暂停成功", result);
        } catch (Exception e) {
            log.error("暂停容器失败", e);
            return RespResult.failure("暂停容器失败: " + e.getMessage());
        }
    }

    /**
     * 恢复容器
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @PostMapping("/unpause")
    @Operation(summary = "恢复容器")
    @TokenSign
    public RespResult<Map<String, Object>> unpauseContainers(@Valid @RequestBody ComposeRequest request) {
        log.info("收到恢复容器请求，目录: {}", request.getComposeDir());
        try {
            Map<String, Object> result = dockerComposeService.unpauseContainers(request);
            return RespResult.success("容器恢复成功", result);
        } catch (Exception e) {
            log.error("恢复容器失败", e);
            return RespResult.failure("恢复容器失败: " + e.getMessage());
        }
    }

    /**
     * 停止容器
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @PostMapping("/stop")
    @Operation(summary = "停止容器")
    @TokenSign
    public RespResult<Map<String, Object>> stopContainers(@Valid @RequestBody ComposeRequest request) {
        log.info("收到停止容器请求，目录: {}", request.getComposeDir());
        try {
            Map<String, Object> result = dockerComposeService.stopContainers(request);
            return RespResult.success("容器停止成功", result);
        } catch (Exception e) {
            log.error("停止容器失败", e);
            return RespResult.failure("停止容器失败: " + e.getMessage());
        }
    }

    /**
     * 停止并删除容器
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @PostMapping("/remove")
    @Operation(summary = "停止并删除容器")
    @TokenSign
    public RespResult<Map<String, Object>> removeContainers(@Valid @RequestBody ComposeRequest request) {
        log.info("收到删除容器请求，目录: {}", request.getComposeDir());
        try {
            Map<String, Object> result = dockerComposeService.removeContainers(request);
            return RespResult.success("容器删除成功", result);
        } catch (Exception e) {
            log.error("删除容器失败", e);
            return RespResult.failure("删除容器失败: " + e.getMessage());
        }
    }

    /**
     * 查看容器状态
     *
     * @param composeDir     docker-compose 目录
     * @param projectName    项目名称
     * @param composeFile    docker-compose 文件名
     * @return 容器状态列表
     */
    @GetMapping("/status")
    @Operation(summary = "查看容器状态")
    @TokenSign
    public RespResult<List<ContainerInfo>> getContainerStatus(
            @RequestParam String composeDir,
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false, defaultValue = "docker-compose.yml") String composeFile) {

        log.info("查看容器状态，目录: {}", composeDir);

        ComposeRequest request = new ComposeRequest();
        request.setComposeDir(composeDir);
        request.setProjectName(projectName);
        request.setComposeFile(composeFile);
        request.setServiceName(serviceName);

        try {
            List<ContainerInfo> status = dockerComposeService.getContainerStatus(request);
            return RespResult.success("获取状态成功", status);
        } catch (Exception e) {
            log.error("获取容器状态失败", e);
            return RespResult.failure("获取容器状态失败: " + e.getMessage());
        }
    }

    /**
     * 查看容器日志
     *
     * @param composeDir     docker-compose 目录
     * @param projectName    项目名称
     * @param composeFile    docker-compose 文件名
     * @param tail           日志行数
     * @return 日志结果列表
     */
    @GetMapping("/logs")
    @Operation(summary = "查看容器日志")
    @TokenSign
    public RespResult<List<LogsResult>> getContainerLogs(
            @RequestParam String composeDir,
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false, defaultValue = "docker-compose.yml") String composeFile,
            @RequestParam(required = false, defaultValue = "100") Integer tail) {

        log.info("查看容器日志，目录: {}, 行数: {}", composeDir, tail);

        ComposeRequest request = new ComposeRequest();
        request.setComposeDir(composeDir);
        request.setProjectName(projectName);
        request.setComposeFile(composeFile);

        try {
            List<LogsResult> logs = dockerComposeService.getContainerLogs(request, tail);
            return RespResult.success("获取日志成功", logs);
        } catch (Exception e) {
            log.error("获取容器日志失败", e);
            return RespResult.failure("获取容器日志失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查接口
     *
     * @return 服务状态
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查接口")
    @TokenSign
    public RespResult<String> health() {
        return RespResult.success("Docker Compose 管理服务运行正常", "OK");
    }


    @GetMapping("/composeDir")
    @Operation(summary = "获取Docker Compose目录")
    @TokenSign
    public RespResult<String> getComposeDir() {
        return RespResult.success("Docker Compose 目录", composeDir);
    }

    @GetMapping("/serviceName")
    @Operation(summary = "获取服务名称")
    @TokenSign
    public RespResult<String> getServiceName(@RequestParam("connectorName") String connectorName) {
        if (connectorName == null || connectorName.isEmpty()) {
            return RespResult.failure("请输入插件名称");
        }
        ConnDockerServiceEnum connDockerServiceEnum = connDockerServiceEnumService.getOne(Wrappers.<ConnDockerServiceEnum>lambdaQuery().eq(ConnDockerServiceEnum::getConnectorName, connectorName));
        if (connDockerServiceEnum == null) {
            return RespResult.failure("未找到该插件对应的服务名称");
        }
        return RespResult.success("服务名称", connDockerServiceEnum.getServiceName());
    }
}
