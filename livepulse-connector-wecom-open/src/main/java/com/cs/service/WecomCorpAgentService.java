package com.cs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.entity.WecomCorpAgent;
import com.cs.exception.CommonException;

/**
 * <p>
 * 企业微信agent账户 服务类
 * </p>
 *
 * @author wwd
 * @since 2026-01-30
 */
public interface WecomCorpAgentService extends IService<WecomCorpAgent> {

    /**
     * 根据企业账户ID删除应用列表
     *
     * @param wecomeCorpId 企业微信账户ID
     * @return 是否成功
     * @throws CommonException 异常
     */
    boolean deleteByCorpId(Long wecomeCorpId) throws CommonException;
}
