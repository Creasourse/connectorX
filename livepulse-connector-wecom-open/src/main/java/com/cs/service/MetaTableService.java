package com.cs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.entity.MetaTable;
import com.cs.exception.CommonException;
import com.cs.param.MetaTablePageParam;
import com.cs.param.MetaTableParam;
import com.cs.resp.PageResult;
import com.cs.vo.MetaTableVO;

/**
 * <p>
 * cdp表 服务类
 * </p>
 *
 * @author wwd
 * @since 2026-03-25
 */
public interface MetaTableService extends IService<MetaTable> {

    /**
     * 分页查询列表
     *
     * @param pageParam 分页参数
     * @return 分页结果
     * @throws CommonException 异常
     */
    PageResult<MetaTableVO> pageList(MetaTablePageParam pageParam) throws CommonException;

    /**
     * 根据ID查询详情
     *
     * @param tableId ID
     * @return 详情
     * @throws CommonException 异常
     */
    MetaTableVO getDetailById(Long tableId) throws CommonException;

    /**
     * 保存或更新
     *
     * @param param 参数
     * @return 是否成功
     * @throws CommonException 异常
     */
    boolean saveOrUpdate(MetaTableParam param) throws CommonException;

    /**
     * 删除
     *
     * @param tableId ID
     * @return 是否成功
     * @throws CommonException 异常
     */
    boolean deleteById(Long tableId) throws CommonException;
}
