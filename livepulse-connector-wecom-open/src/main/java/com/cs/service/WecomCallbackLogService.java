package com.cs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.dto.WecomCallbackRequestDto;
import com.cs.entity.WecomCallbackLog;
import com.cs.exception.CommonException;
import com.cs.param.WecomCallbackLogPageParam;
import com.cs.resp.PageResult;
import com.cs.vo.WecomCallbackLogVO;

/**
 * <p>
 * 企业微信回调日志表 服务类
 * </p>
 *
 * @author wwd
 * @since 2026-03-25
 */
public interface WecomCallbackLogService extends IService<WecomCallbackLog> {

    /**
     * 记录回调日志
     *
     * @param wecomCorpId  企业微信账户ID
     * @param eventCode    事件编码
     * @param eventName    事件名称
     * @param requestBody  请求内容
     * @param responseBody 响应内容
     * @param processStatus 处理状态
     * @param errorMessage 错误信息
     * @return 是否成功
     */
    boolean saveLog(Long wecomCorpId, String eventCode, String eventName,
                    String requestBody, String responseBody,
                    Integer processStatus, String errorMessage);

    /**
     * 处理回调请求
     *
     * @param requestDto 回调请求数据
     * @param requestBody 原始请求内容
     * @return 是否成功
     * @throws CommonException 异常
     */
    boolean handleCallback(WecomCallbackRequestDto requestDto, String requestBody) throws CommonException;

    /**
     * 分页查询回调日志
     *
     * @param pageParam 分页参数
     * @return 分页结果
     * @throws CommonException 异常
     */
    PageResult<WecomCallbackLogVO> pageList(WecomCallbackLogPageParam pageParam) throws CommonException;

    /**
     * 根据ID查询回调日志详情
     *
     * @param id 日志ID
     * @return 日志详情
     * @throws CommonException 异常
     */
    WecomCallbackLogVO getDetailById(Long id) throws CommonException;
}