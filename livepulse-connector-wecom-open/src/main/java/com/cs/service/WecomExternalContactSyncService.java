package com.cs.service;

import com.cs.exception.CommonException;

/**
 * @ClassName: WecomExternalContactSyncService
 * @Author: wwd
 * @TODO: 企业微信外部联系人数据同步服务
 * @Date: 2026/3/21
 */
public interface WecomExternalContactSyncService {

    /**
     * 同步外部联系人列表
     *
     * @param wecomCorpId 企业微信账户ID
     * @return 同步结果
     * @throws CommonException 异常
     */
    String syncExternalContacts(Long wecomCorpId) throws CommonException;

    /**
     * 同步外部联系人详情
     *
     * @param wecomCorpId 企业微信账户ID
     * @return 同步结果
     * @throws CommonException 异常
     */
    String syncExternalContactDetails(Long wecomCorpId) throws CommonException;

    /**
     * 全量同步外部联系人
     *
     * @param wecomCorpId 企业微信账户ID
     * @return 同步结果
     * @throws CommonException 异常
     */
    String syncAllExternalContacts(Long wecomCorpId) throws CommonException;
}