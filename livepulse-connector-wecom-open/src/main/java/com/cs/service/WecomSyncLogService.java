package com.cs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.entity.WecomSyncLog;
import com.cs.exception.CommonException;
import com.cs.param.WecomSyncLogPageParam;
import com.cs.resp.PageResult;
import com.cs.vo.WecomSyncLogVO;

/**
 * <p>
 * 企业微信数据同步日志表 服务类
 * </p>
 *
 * @author wwd
 * @since 2026/03/27
 */
public interface WecomSyncLogService extends IService<WecomSyncLog> {

    /**
     * 分页查询同步日志
     *
     * @param pageParam 分页参数
     * @return 分页结果
     * @throws CommonException 异常
     */
    PageResult<WecomSyncLogVO> pageList(WecomSyncLogPageParam pageParam) throws CommonException;

    /**
     * 根据ID查询同步日志详情
     *
     * @param id 日志ID
     * @return 日志详情
     * @throws CommonException 异常
     */
    WecomSyncLogVO getDetailById(Long id) throws CommonException;
}