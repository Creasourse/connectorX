package com.cs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.entity.WecomSyncLog;
import com.cs.exception.CommonException;
import com.cs.mapper.WecomSyncLogMapper;
import com.cs.param.WecomSyncLogPageParam;
import com.cs.resp.PageResult;
import com.cs.service.WecomSyncLogService;
import com.cs.vo.WecomSyncLogVO;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 企业微信数据同步日志表 服务实现类
 * </p>
 *
 * @author wwd
 * @since 2026/03/27
 */
@Slf4j
@Service
public class WecomSyncLogServiceImpl extends ServiceImpl<WecomSyncLogMapper, WecomSyncLog> implements WecomSyncLogService {

    @Autowired
    private MapperFactory mapperFactory;

    @Override
    public PageResult<WecomSyncLogVO> pageList(WecomSyncLogPageParam pageParam) throws CommonException {
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

        LambdaQueryWrapper<WecomSyncLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.gt(WecomSyncLog::getId, 0);
        if (Objects.nonNull(pageParam.getWecomCorpId())) {
            queryWrapper.eq(WecomSyncLog::getWecomCorpId, pageParam.getWecomCorpId());
        }
        if (StringUtils.isNotBlank(pageParam.getSyncType())) {
            queryWrapper.eq(WecomSyncLog::getSyncType, pageParam.getSyncType());
        }
        if (StringUtils.isNotBlank(pageParam.getSyncStatus())) {
            queryWrapper.eq(WecomSyncLog::getSyncStatus, pageParam.getSyncStatus());
        }
        if (Objects.nonNull(pageParam.getStartTime())) {
            queryWrapper.ge(WecomSyncLog::getStartTime, pageParam.getStartTime());
        }
        if (Objects.nonNull(pageParam.getEndTime())) {
            queryWrapper.le(WecomSyncLog::getEndTime, pageParam.getEndTime());
        }
        if (Objects.nonNull(pageParam.getSortType())) {
            switch (pageParam.getSortType()) {
                case 1:
                    queryWrapper.orderByDesc(WecomSyncLog::getCreateTime);
                    break;
                case 2:
                    queryWrapper.orderByAsc(WecomSyncLog::getCreateTime);
                    break;
                case 3:
                    queryWrapper.orderByDesc(WecomSyncLog::getStartTime);
                    break;
                case 4:
                    queryWrapper.orderByAsc(WecomSyncLog::getStartTime);
                    break;
                default:
                    queryWrapper.orderByDesc(WecomSyncLog::getCreateTime);
                    break;
            }
        } else {
            queryWrapper.orderByDesc(WecomSyncLog::getCreateTime);
        }

        Page<WecomSyncLog> page = new Page<>(current, size);
        IPage<WecomSyncLog> iPage = page(page, queryWrapper);
        PageResult<WecomSyncLogVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), WecomSyncLogVO.class));

        return pageResult;
    }

    @Override
    public WecomSyncLogVO getDetailById(Long id) throws CommonException {
        if (Objects.isNull(id)) {
            throw new CommonException("id is null");
        }
        WecomSyncLog syncLog = getById(id);
        if (Objects.isNull(syncLog)) {
            throw new CommonException("同步日志不存在");
        }
        return mapperFactory.getMapperFacade().map(syncLog, WecomSyncLogVO.class);
    }
}