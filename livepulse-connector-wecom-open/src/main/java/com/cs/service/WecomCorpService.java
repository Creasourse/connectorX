package com.cs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.entity.WecomCorp;
import com.cs.exception.CommonException;
import com.cs.param.WecomCorpPageParam;
import com.cs.param.WecomCorpParam;
import com.cs.resp.PageResult;
import com.cs.vo.WecomCorpVO;

/**
 * <p>
 * 企业微信账户表 服务类
 * </p>
 *
 * @author wwd
 * @since 2026-01-30
 */
public interface WecomCorpService extends IService<WecomCorp> {

    /**
     * 分页查询企业微信账户列表
     *
     * @param pageParam 分页参数
     * @return 分页结果
     * @throws CommonException 异常
     */
    PageResult<WecomCorpVO> pageList(WecomCorpPageParam pageParam) throws CommonException;

    /**
     * 根据ID查询企业微信账户详情
     *
     * @param wecomeCorpId 企业微信账户ID
     * @return 企业微信账户详情
     * @throws CommonException 异常
     */
    WecomCorpVO getDetailById(Long wecomeCorpId) throws CommonException;

    /**
     * 保存或更新企业微信账户
     *
     * @param param 企业微信账户参数
     * @return 是否成功
     * @throws CommonException 异常
     */
    boolean saveOrUpdate(WecomCorpParam param) throws CommonException;

    /**
     * 删除企业微信账户
     *
     * @param wecomeCorpId 企业微信账户ID
     * @return 是否成功
     * @throws CommonException 异常
     */
    boolean deleteById(Long wecomeCorpId) throws CommonException;
}
