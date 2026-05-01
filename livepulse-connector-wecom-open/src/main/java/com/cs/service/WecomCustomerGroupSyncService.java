package com.cs.service;

import com.cs.exception.CommonException;

/**
 * @ClassName: WecomCustomerGroupSyncService
 * @Author: wwd
 * @TODO: 企业微信客户群数据同步服务
 * @Date: 2026/3/21
 */
public interface WecomCustomerGroupSyncService {

    /**
     * 同步客户群列表
     *
     * @param wecomCorpId 企业微信账户ID
     * @return 同步结果
     * @throws CommonException 异常
     */
    String syncCustomerGroups(Long wecomCorpId) throws CommonException;

    /**
     * 同步客户群详情
     *
     * @param wecomCorpId 企业微信账户ID
     * @return 同步结果
     * @throws CommonException 异常
     */
    String syncCustomerGroupDetails(Long wecomCorpId) throws CommonException;

    /**
     * 全量同步客户群
     *
     * @param wecomCorpId 企业微信账户ID
     * @return 同步结果
     * @throws CommonException 异常
     */
    String syncAllCustomerGroups(Long wecomCorpId) throws CommonException;
}