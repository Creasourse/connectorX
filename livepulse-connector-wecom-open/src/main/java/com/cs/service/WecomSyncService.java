package com.cs.service;

import com.cs.exception.CommonException;

/**
 * @ClassName: WecomSyncService
 * @Author: wwd
 * @TODO: 企业微信数据同步服务
 * @Date: 2026/3/21
 */
public interface WecomSyncService {

    /**
     * 同步部门列表
     *
     * @param wecomCorpId 企业微信账户ID
     * @return 同步结果
     * @throws CommonException 异常
     */
    String syncDepartments(Long wecomCorpId) throws CommonException;

    /**
     * 同步员工列表
     *
     * @param wecomCorpId 企业微信账户ID
     * @return 同步结果
     * @throws CommonException 异常
     */
    String syncEmployees(Long wecomCorpId) throws CommonException;

    /**
     * 同步员工详细信息
     *
     * @param wecomCorpId 企业微信账户ID
     * @return 同步结果
     * @throws CommonException 异常
     */
    String syncEmployeeDetails(Long wecomCorpId) throws CommonException;

    /**
     * 全量同步
     *
     * @param wecomCorpId 企业微信账户ID
     * @return 同步结果
     * @throws CommonException 异常
     */
    String syncAll(Long wecomCorpId) throws CommonException;
}