package com.cs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.entity.WecomCallbackConfig;
import com.cs.exception.CommonException;
import com.cs.param.WecomCallbackConfigPageParam;
import com.cs.param.WecomCallbackConfigParam;
import com.cs.resp.PageResult;
import com.cs.vo.WecomCallbackConfigVO;

/**
 * <p>
 * 企业微信回调配置表 服务类
 * </p>
 *
 * @author wwd
 * @since 2026-03-25
 */
public interface WecomCallbackConfigService extends IService<WecomCallbackConfig> {

    /**
     * 分页查询列表
     *
     * @param pageParam 分页参数
     * @return 分页结果
     * @throws CommonException 异常
     */
    PageResult<WecomCallbackConfigVO> pageList(WecomCallbackConfigPageParam pageParam) throws CommonException;

    /**
     * 根据ID查询详情
     *
     * @param id ID
     * @return 详情
     * @throws CommonException 异常
     */
    WecomCallbackConfigVO getDetailById(Long id) throws CommonException;

    /**
     * 保存或更新
     *
     * @param param 参数
     * @return 是否成功
     * @throws CommonException 异常
     */
    boolean saveOrUpdate(WecomCallbackConfigParam param) throws CommonException;

    /**
     * 删除
     *
     * @param id ID
     * @return 是否成功
     * @throws CommonException 异常
     */
    boolean deleteById(Long id) throws CommonException;

    /**
     * 根据企业ID和事件编码查询配置
     *
     * @param wecomCorpId 企业微信账户ID
     * @param eventCode   事件编码
     * @return 配置信息
     */
    WecomCallbackConfig getByCorpIdAndEventCode(Long wecomCorpId, String eventCode);

    String verifyUrl(String msgSign, Integer timestamp, String nonce, String echostr) throws CommonException;
}