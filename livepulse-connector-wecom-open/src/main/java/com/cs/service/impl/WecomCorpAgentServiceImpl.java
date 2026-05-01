package com.cs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.entity.WecomCorpAgent;
import com.cs.exception.CommonException;
import com.cs.mapper.WecomCorpAgentMapper;
import com.cs.service.WecomCorpAgentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * <p>
 * 企业微信agent账户 服务实现类
 * </p>
 *
 * @author wwd
 * @since 2026-01-30
 */
@Service
public class WecomCorpAgentServiceImpl extends ServiceImpl<WecomCorpAgentMapper, WecomCorpAgent> implements WecomCorpAgentService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByCorpId(Long wecomeCorpId) throws CommonException {
        if (Objects.isNull(wecomeCorpId)) {
            throw new CommonException("wecomeCorpId is null");
        }
        LambdaQueryWrapper<WecomCorpAgent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WecomCorpAgent::getWecomeCorpId, wecomeCorpId);
        return remove(queryWrapper);
    }
}
