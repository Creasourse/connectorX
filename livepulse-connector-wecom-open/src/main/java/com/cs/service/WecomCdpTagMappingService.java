package com.cs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.entity.WecomCdpTagMapping;
import com.cs.exception.CommonException;
import com.cs.param.WecomCdpTagMappingPageParam;
import com.cs.param.WecomCdpTagMappingParam;
import com.cs.resp.PageResult;
import com.cs.vo.WecomCdpTagMappingVO;

/**
 * <p>
 * CDP标签映射 服务类
 * </p>
 *
 * @author wwd
 * @since 2026-03-21
 */
public interface WecomCdpTagMappingService extends IService<WecomCdpTagMapping> {

    /**
     * 分页查询CDP标签映射
     *
     * @param pageParam 分页参数
     * @return 分页结果
     * @throws CommonException 异常
     */
    PageResult<WecomCdpTagMappingVO> pageList(WecomCdpTagMappingPageParam pageParam) throws CommonException;

    /**
     * 根据ID查询CDP标签映射详情
     *
     * @param id 主键ID
     * @return CDP标签映射详情
     * @throws CommonException 异常
     */
    WecomCdpTagMappingVO getByIdVO(Long id) throws CommonException;

    /**
     * 保存或更新CDP标签映射
     *
     * @param param CDP标签映射参数
     * @return 是否成功
     * @throws CommonException 异常
     */
    boolean saveOrUpdate(WecomCdpTagMappingParam param) throws CommonException;

    /**
     * 删除CDP标签映射
     *
     * @param id 主键ID
     * @return 是否成功
     * @throws CommonException 异常
     */
    boolean deleteById(Long id) throws CommonException;

    /**
     * 同步CDP标签到企业微信
     *
     * @param id 主键ID
     * @return 同步结果
     * @throws CommonException 异常
     */
    String syncCdpTagsToWecom(Long id) throws CommonException;
}