package com.cs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.entity.WecomCorp;
import com.cs.entity.WecomCorpAgent;
import com.cs.exception.CommonException;
import com.cs.mapper.WecomCorpMapper;
import com.cs.param.WecomCorpAgentParam;
import com.cs.param.WecomCorpPageParam;
import com.cs.param.WecomCorpParam;
import com.cs.resp.PageResult;
import com.cs.service.WecomCorpAgentService;
import com.cs.service.WecomCorpService;
import com.cs.vo.WecomCorpAgentVO;
import com.cs.vo.WecomCorpVO;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 企业微信账户表 服务实现类
 * </p>
 *
 * @author wwd
 * @since 2026-01-30
 */
@Service
public class WecomCorpServiceImpl extends ServiceImpl<WecomCorpMapper, WecomCorp> implements WecomCorpService {

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private WecomCorpAgentService wecomCorpAgentService;

    @Override
    public PageResult<WecomCorpVO> pageList(WecomCorpPageParam pageParam) throws CommonException {
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
        LambdaQueryWrapper<WecomCorp> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(WecomCorp::getWecomeCorpId, 0);
        if (StringUtils.isNotBlank(pageParam.getCompanyName())) {
            queryWrapper.like(WecomCorp::getCompanyName, pageParam.getCompanyName());
        }
        if (StringUtils.isNotBlank(pageParam.getCorpId())) {
            queryWrapper.eq(WecomCorp::getCorpId, pageParam.getCorpId());
        }
        if (Objects.nonNull(pageParam.getSortType())) {
            switch (pageParam.getSortType()) {
                case 1:
                    queryWrapper.orderByDesc(WecomCorp::getCreateTime);
                    break;
                case 2:
                    queryWrapper.orderByAsc(WecomCorp::getCreateTime);
                    break;
                case 3:
                    queryWrapper.orderByDesc(WecomCorp::getUpdateTime);
                    break;
                case 4:
                    queryWrapper.orderByAsc(WecomCorp::getUpdateTime);
                    break;
                default:
                    queryWrapper.orderByDesc(WecomCorp::getCreateTime);
                    break;
            }
        } else {
            queryWrapper.orderByDesc(WecomCorp::getCreateTime);
        }

        Page<WecomCorp> page = new Page<>(current, size);
        IPage<WecomCorp> iPage = page(page, queryWrapper);
        PageResult<WecomCorpVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), WecomCorpVO.class));
        // 根据corpId 查询应用列表
        if (Objects.nonNull(iPage.getRecords()) && !iPage.getRecords().isEmpty()) {
            List<Long> wecomeCorpIds = iPage.getRecords().stream().map(WecomCorp::getWecomeCorpId).toList();
            List<WecomCorpAgent> agents = wecomCorpAgentService.list(Wrappers.<WecomCorpAgent>lambdaQuery().in(WecomCorpAgent::getWecomeCorpId, wecomeCorpIds));
            if (Objects.nonNull(agents) && !agents.isEmpty()) {
                List<WecomCorpAgentVO> agentVOList = mapperFactory.getMapperFacade().mapAsList(agents, WecomCorpAgentVO.class);
                for (WecomCorpVO vo : pageResult.getList()) {
                    List<WecomCorpAgentVO> agentVOList1 = agentVOList.stream().filter(agentVO -> agentVO.getWecomeCorpId().equals(vo.getWecomeCorpId())).toList();
                    if (Objects.nonNull(agentVOList1) && !agentVOList1.isEmpty()) {
                        vo.setAgents(agentVOList1);
                    }
                }
            }
        }
        return pageResult;
    }

    @Override
    public WecomCorpVO getDetailById(Long wecomeCorpId) throws CommonException {
        if (Objects.isNull(wecomeCorpId)) {
            throw new CommonException("wecomeCorpId is null");
        }
        WecomCorp wecomCorp = getById(wecomeCorpId);
        if (Objects.isNull(wecomCorp)) {
            throw new CommonException("企业微信账户不存在");
        }
        WecomCorpVO vo = mapperFactory.getMapperFacade().map(wecomCorp, WecomCorpVO.class);

        // 查询关联的应用列表
        LambdaQueryWrapper<WecomCorpAgent> agentWrapper = new LambdaQueryWrapper<>();
        agentWrapper.eq(WecomCorpAgent::getWecomeCorpId, wecomeCorpId);
        List<WecomCorpAgent> agents = wecomCorpAgentService.list(agentWrapper);
        if (Objects.nonNull(agents) && !agents.isEmpty()) {
            List<WecomCorpAgentVO> agentVOList = mapperFactory.getMapperFacade().mapAsList(agents, WecomCorpAgentVO.class);
            vo.setAgents(agentVOList);
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(WecomCorpParam param) throws CommonException {
        if (Objects.isNull(param)) {
            throw new CommonException("param is null");
        }
        if (StringUtils.isBlank(param.getCompanyName())) {
            throw new CommonException("企业名称不能为空");
        }
        if (StringUtils.isBlank(param.getCorpId())) {
            throw new CommonException("企业ID不能为空");
        }
        if (StringUtils.isBlank(param.getCorpSecret())) {
            throw new CommonException("通讯录密钥不能为空");
        }

        WecomCorp wecomCorp = mapperFactory.getMapperFacade().map(param, WecomCorp.class);
        boolean result = saveOrUpdate(wecomCorp);

        if (result && Objects.nonNull(param.getAgents()) && !param.getAgents().isEmpty()) {
            // 删除旧的应用数据
            LambdaQueryWrapper<WecomCorpAgent> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(WecomCorpAgent::getWecomeCorpId, wecomCorp.getWecomeCorpId());
            wecomCorpAgentService.remove(deleteWrapper);

            // 保存新的应用数据
            for (WecomCorpAgentParam agentParam : param.getAgents()) {
                WecomCorpAgent agent = mapperFactory.getMapperFacade().map(agentParam, WecomCorpAgent.class);
                agent.setWecomeCorpId(wecomCorp.getWecomeCorpId());
                wecomCorpAgentService.save(agent);
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long wecomeCorpId) throws CommonException {
        if (Objects.isNull(wecomeCorpId)) {
            throw new CommonException("wecomeCorpId is null");
        }
        // 删除关联的应用数据
        wecomCorpAgentService.deleteByCorpId(wecomeCorpId);
        // 删除企业账户
        return removeById(wecomeCorpId);
    }
}
