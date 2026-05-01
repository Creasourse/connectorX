package com.cs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.entity.WecomCallbackConfig;
import com.cs.entity.WecomCorp;
import com.cs.exception.CommonException;
import com.cs.mapper.WecomCallbackConfigMapper;
import com.cs.mapper.WecomCorpMapper;
import com.cs.param.WecomCallbackConfigPageParam;
import com.cs.param.WecomCallbackConfigParam;
import com.cs.resp.PageResult;
import com.cs.service.WecomCallbackConfigService;
import com.cs.vo.WecomCallbackConfigVO;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 企业微信回调配置表 服务实现类
 * </p>
 *
 * @author wwd
 * @since 2026-03-25
 */
@Service
public class WecomCallbackConfigServiceImpl extends ServiceImpl<WecomCallbackConfigMapper, WecomCallbackConfig> implements WecomCallbackConfigService {

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private WecomCorpMapper wecomCorpMapper;

    @Value("${callback.token}")
    private String token;

    @Value("${callback.encodeAesKey}")
    private String encodingAesKey;

    @Override
    public PageResult<WecomCallbackConfigVO> pageList(WecomCallbackConfigPageParam pageParam) throws CommonException {
        if (Objects.isNull(pageParam)) {
            throw new CommonException("param is null");
        }
        Integer current = pageParam.getCurrentPage();
        Integer size = pageParam.getPageSize();
        if (Objects.isNull(current)) {
            current = 1;
        }
        if (Objects.isNull(size)) {
            size = 10;
        }

        LambdaQueryWrapper<WecomCallbackConfig> queryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(pageParam.getWecomCorpId())) {
            queryWrapper.eq(WecomCallbackConfig::getWecomCorpId, pageParam.getWecomCorpId());
        }
        if (StringUtils.isNotBlank(pageParam.getEventCode())) {
            queryWrapper.like(WecomCallbackConfig::getEventCode, pageParam.getEventCode());
        }
        if (StringUtils.isNotBlank(pageParam.getEventName())) {
            queryWrapper.like(WecomCallbackConfig::getEventName, pageParam.getEventName());
        }
        if (Objects.nonNull(pageParam.getEnabled())) {
            queryWrapper.eq(WecomCallbackConfig::getEnabled, pageParam.getEnabled());
        }
        if (Objects.nonNull(pageParam.getSortType())) {
            switch (pageParam.getSortType()) {
                case 1:
                    queryWrapper.orderByDesc(WecomCallbackConfig::getCreateTime);
                    break;
                case 2:
                    queryWrapper.orderByAsc(WecomCallbackConfig::getCreateTime);
                    break;
                default:
                    queryWrapper.orderByDesc(WecomCallbackConfig::getCreateTime);
                    break;
            }
        } else {
            queryWrapper.orderByDesc(WecomCallbackConfig::getCreateTime);
        }

        Page<WecomCallbackConfig> page = new Page<>(current, size);
        IPage<WecomCallbackConfig> iPage = page(page, queryWrapper);
        PageResult<WecomCallbackConfigVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), WecomCallbackConfigVO.class));

        return pageResult;
    }

    @Override
    public WecomCallbackConfigVO getDetailById(Long id) throws CommonException {
        if (Objects.isNull(id)) {
            throw new CommonException("id is null");
        }
        WecomCallbackConfig config = getById(id);
        if (Objects.isNull(config)) {
            throw new CommonException("配置不存在");
        }
        return mapperFactory.getMapperFacade().map(config, WecomCallbackConfigVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(WecomCallbackConfigParam param) throws CommonException {
        if (Objects.isNull(param)) {
            throw new CommonException("param is null");
        }
        if (Objects.isNull(param.getWecomCorpId())) {
            throw new CommonException("企业微信账户ID不能为空");
        }
        if (StringUtils.isBlank(param.getEventCode())) {
            throw new CommonException("事件编码不能为空");
        }
        if (StringUtils.isBlank(param.getEventName())) {
            throw new CommonException("事件名称不能为空");
        }

        WecomCallbackConfig config = mapperFactory.getMapperFacade().map(param, WecomCallbackConfig.class);
        config.setUpdateTime(LocalDateTime.now());
        if (Objects.isNull(config.getId())) {
            config.setCreateTime(LocalDateTime.now());
        }
        if (Objects.isNull(config.getEnabled())) {
            config.setEnabled(1);
        }
        return saveOrUpdate(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) throws CommonException {
        if (Objects.isNull(id)) {
            throw new CommonException("id is null");
        }
        return removeById(id);
    }

    @Override
    public WecomCallbackConfig getByCorpIdAndEventCode(Long wecomCorpId, String eventCode) {
        LambdaQueryWrapper<WecomCallbackConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WecomCallbackConfig::getWecomCorpId, wecomCorpId);
        queryWrapper.eq(WecomCallbackConfig::getEventCode, eventCode);
        queryWrapper.eq(WecomCallbackConfig::getEnabled, 1);
        return getOne(queryWrapper);
    }

    @Override
    public String verifyUrl(String msgSign, Integer timestamp, String nonce, String echostr) throws CommonException {
        try {
            WecomCorp corp = wecomCorpMapper.selectOne(new LambdaQueryWrapper<WecomCorp>().ge(WecomCorp::getWecomeCorpId, 0));
            if (corp == null) {
                throw new CommonException("未找到企业微信账户信息");
            }
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAesKey, corp.getCorpId());
            return wxcpt.VerifyURL(msgSign, timestamp.toString(), nonce, echostr);
        } catch (Exception e) {
            throw new CommonException("验证URL失败: " + e.getMessage());
        }
    }
}