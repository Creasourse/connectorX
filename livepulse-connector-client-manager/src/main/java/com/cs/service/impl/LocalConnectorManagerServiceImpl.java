package com.cs.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cs.docker.dto.ComposeRequest;
import com.cs.docker.service.DockerComposeService;
import com.cs.entity.ConnDockerServiceEnum;
import com.cs.entity.LocalConnector;
import com.cs.exception.CommonException;
import com.cs.service.ConnDockerServiceEnumService;
import com.cs.service.LocalConnectorManagerService;
import com.cs.service.LocalConnectorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 本地连接器管理服务实现
 *
 * @author livepulse
 * @since 2026-05-09
 */
@Slf4j
@Service
public class LocalConnectorManagerServiceImpl implements LocalConnectorManagerService {

    @Autowired
    private LocalConnectorService localConnectorService;

    @Autowired
    private DockerComposeService dockerComposeService;

    @Autowired
    private ConnDockerServiceEnumService connDockerServiceEnumService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Boolean deleteById(Long localConnectorId) throws CommonException {
        if (Objects.isNull(localConnectorId)) {
            throw new CommonException("本地连接器ID不能为空");
        }

        // 查询本地连接器信息
        LocalConnector localConnector = localConnectorService.getById(localConnectorId);
        if (Objects.isNull(localConnector)) {
            throw new CommonException("本地连接器不存在");
        }

        String connectorName = localConnector.getConnectorName();
        log.info("开始删除本地连接器，ID: {}, 名称: {}", localConnectorId, connectorName);

        try {
            // 停止并删除 Docker 容器
            ConnDockerServiceEnum connDockerServiceEnum = connDockerServiceEnumService.getOne(Wrappers.<ConnDockerServiceEnum>lambdaQuery().eq(ConnDockerServiceEnum::getServiceName, connectorName));
            if (Objects.isNull(connDockerServiceEnum)) {
                log.error("未找到对应的 Docker 服务名称，请检查数据库中是否存在该连接器");
            } else {
                ComposeRequest composeRequest = new ComposeRequest();
                composeRequest.setServiceName(connectorName);
                dockerComposeService.removeContainers(composeRequest);
                log.info("Docker 容器已停止并删除，服务名称: {}", connectorName);
            }
        } catch (Exception e) {
            log.error("停止 Docker 容器失败，连接器名称: {}", connectorName, e);
            // 即使 Docker 容器停止失败，也继续删除数据库记录
        }

        // 删除数据库记录 调整插件为逻辑删除
        LocalConnector deleted = new LocalConnector();
        deleted.setLocalConnectorId(localConnectorId);
        deleted.setIsDel(1);
        boolean removed = localConnectorService.updateById(deleted);
        if (!removed) {
            throw new CommonException("删除本地连接器失败");
        }

        log.info("本地连接器删除成功，ID: {}", localConnectorId);
        return true;
    }
}
