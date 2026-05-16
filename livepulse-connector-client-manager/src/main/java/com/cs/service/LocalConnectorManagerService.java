package com.cs.service;

import com.cs.exception.CommonException;

/**
 * 本地连接器管理服务
 * 处理本地连接器的业务逻辑，包括 Docker 容器管理
 *
 * @author livepulse
 * @since 2026-05-09
 */
public interface LocalConnectorManagerService {

    /**
     * 通过本地连接器ID删除本地连接器记录，并停掉对应的Docker容器
     *
     * @param localConnectorId 本地连接器ID
     * @throws CommonException 异常
     */
    Boolean deleteById(Long localConnectorId) throws CommonException;
}
