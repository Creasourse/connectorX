package com.cs.service;

import com.cs.exception.CommonException;

/**
 * @ClassName: WecomTagSyncService
 * @Author: wwd
 * @TODO: 企业微信标签数据同步服务
 * @Date: 2026/3/21
 */
public interface WecomTagSyncService {

    /**
     * 同步企业标签库
     *
     * @param wecomCorpId 企业微信账户ID
     * @return 同步结果
     * @throws CommonException 异常
     */
    String syncCorpTags(Long wecomCorpId) throws CommonException;
}