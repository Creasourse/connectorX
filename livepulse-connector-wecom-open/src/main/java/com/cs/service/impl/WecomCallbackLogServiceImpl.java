package com.cs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.dto.WecomCallbackRequestDto;
import com.cs.entity.WecomCallbackConfig;
import com.cs.entity.WecomCallbackLog;
import com.cs.exception.CommonException;
import com.cs.mapper.WecomCallbackLogMapper;
import com.cs.param.WecomCallbackLogPageParam;
import com.cs.resp.PageResult;
import com.cs.service.WecomCallbackConfigService;
import com.cs.service.WecomCallbackLogService;
import com.cs.vo.WecomCallbackLogVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 企业微信回调日志表 服务实现类
 * </p>
 *
 * @author wwd
 * @since 2026-03-25
 */
@Slf4j
@Service
public class WecomCallbackLogServiceImpl extends ServiceImpl<WecomCallbackLogMapper, WecomCallbackLog> implements WecomCallbackLogService {

    @Autowired
    private WecomCallbackConfigService wecomCallbackConfigService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MapperFactory mapperFactory;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveLog(Long wecomCorpId, String eventCode, String eventName,
                           String requestBody, String responseBody,
                           Integer processStatus, String errorMessage) {
        WecomCallbackLog callbackLog = new WecomCallbackLog();
        callbackLog.setWecomCorpId(wecomCorpId);
        callbackLog.setEventCode(eventCode);
        callbackLog.setEventName(eventName);
        callbackLog.setRequestBody(requestBody);
        callbackLog.setResponseBody(responseBody);
        callbackLog.setProcessStatus(processStatus);
        callbackLog.setErrorMessage(errorMessage);
        callbackLog.setCreateTime(LocalDateTime.now());
        return save(callbackLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleCallback(WecomCallbackRequestDto requestDto, String requestBody) throws CommonException {
        if (Objects.isNull(requestDto)) {
            throw new CommonException("request is null");
        }

        try {
            // 解析企业ID
            String corpId = requestDto.getToUserName();
            if (Objects.isNull(corpId)) {
                throw new CommonException("企业ID为空");
            }

            // 解析事件类型
            String eventCode = buildEventCode(requestDto);
            String eventName = buildEventName(requestDto);

            log.info("接收到企业微信回调: corpId={}, eventCode={}, eventName={}", corpId, eventCode, eventName);

            // 查找配置（这里需要根据实际业务获取 wecomCorpId）
            // 暂时使用默认值，实际应该从 corpId 查询对应的 wecomCorpId
            WecomCallbackConfig config = wecomCallbackConfigService.getByCorpIdAndEventCode(1L, eventCode);

            String responseBody = "success";
            Integer processStatus = 1; // 成功
            String errorMessage = null;

            // 如果有配置回调URL，转发请求
            if (Objects.nonNull(config) && Objects.nonNull(config.getCallbackUrl())) {
                // TODO: 实现HTTP请求转发到配置的回调URL
                log.info("转发回调到: {}", config.getCallbackUrl());
            }

            // 记录日志
            saveLog(1L, eventCode, eventName, requestBody, responseBody, processStatus, errorMessage);

            return true;
        } catch (Exception e) {
            log.error("处理企业微信回调失败", e);
            throw new CommonException("处理回调失败: " + e.getMessage());
        }
    }

    /**
     * 构建事件编码
     */
    private String buildEventCode(WecomCallbackRequestDto requestDto) {
        StringBuilder eventCode = new StringBuilder();
        if (Objects.nonNull(requestDto.getEvent())) {
            eventCode.append(requestDto.getEvent());
        }
        if (Objects.nonNull(requestDto.getChangeType())) {
            eventCode.append("_").append(requestDto.getChangeType());
        }
        return eventCode.toString();
    }

    /**
     * 构建事件名称
     */
    private String buildEventName(WecomCallbackRequestDto requestDto) {
        String event = requestDto.getEvent();
        String changeType = requestDto.getChangeType();

        if (Objects.isNull(event)) {
            return "未知事件";
        }

        String eventName;
        switch (event) {
            case "change_contact":
                eventName = "通讯录变更";
                break;
            case "change_external_contact":
                eventName = "外部联系人变更";
                break;
            case "change_chat":
                eventName = "客户群变更";
                break;
            case "subscribe":
                eventName = "成员关注事件";
                break;
            case "unsubscribe":
                eventName = "成员取消关注事件";
                break;
            default:
                eventName = event;
                break;
        }

        if (Objects.nonNull(changeType)) {
            eventName += " - " + changeType;
        }

        return eventName;
    }

    @Override
    public PageResult<WecomCallbackLogVO> pageList(WecomCallbackLogPageParam pageParam) throws CommonException {
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

        LambdaQueryWrapper<WecomCallbackLog> queryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(pageParam.getWecomCorpId())) {
            queryWrapper.eq(WecomCallbackLog::getWecomCorpId, pageParam.getWecomCorpId());
        }
        if (StringUtils.isNotBlank(pageParam.getEventCode())) {
            queryWrapper.like(WecomCallbackLog::getEventCode, pageParam.getEventCode());
        }
        if (StringUtils.isNotBlank(pageParam.getEventName())) {
            queryWrapper.like(WecomCallbackLog::getEventName, pageParam.getEventName());
        }
        if (Objects.nonNull(pageParam.getProcessStatus())) {
            queryWrapper.eq(WecomCallbackLog::getProcessStatus, pageParam.getProcessStatus());
        }
        if (Objects.nonNull(pageParam.getStartTime())) {
            queryWrapper.ge(WecomCallbackLog::getCreateTime, pageParam.getStartTime());
        }
        if (Objects.nonNull(pageParam.getEndTime())) {
            queryWrapper.le(WecomCallbackLog::getCreateTime, pageParam.getEndTime());
        }
        if (Objects.nonNull(pageParam.getSortType())) {
            switch (pageParam.getSortType()) {
                case 1:
                    queryWrapper.orderByDesc(WecomCallbackLog::getCreateTime);
                    break;
                case 2:
                    queryWrapper.orderByAsc(WecomCallbackLog::getCreateTime);
                    break;
                default:
                    queryWrapper.orderByDesc(WecomCallbackLog::getCreateTime);
                    break;
            }
        } else {
            queryWrapper.orderByDesc(WecomCallbackLog::getCreateTime);
        }

        Page<WecomCallbackLog> page = new Page<>(current, size);
        IPage<WecomCallbackLog> iPage = page(page, queryWrapper);
        PageResult<WecomCallbackLogVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), WecomCallbackLogVO.class));

        return pageResult;
    }

    @Override
    public WecomCallbackLogVO getDetailById(Long id) throws CommonException {
        if (Objects.isNull(id)) {
            throw new CommonException("id is null");
        }
        WecomCallbackLog callbackLog = getById(id);
        if (Objects.isNull(callbackLog)) {
            throw new CommonException("回调日志不存在");
        }
        return mapperFactory.getMapperFacade().map(callbackLog, WecomCallbackLogVO.class);
    }
}
