package com.cs.service;

import com.cs.entity.MetaTableColumn;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.exception.CommonException;
import com.cs.param.MetaTableColumnPageParam;
import com.cs.param.MetaTableColumnParam;
import com.cs.resp.PageResult;
import com.cs.vo.MetaTableColumnVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wwd
 * @since 2026-05-03
 */
public interface MetaTableColumnService extends IService<MetaTableColumn> {

    /**
     * 分页查询列表
     *
     * @param pageParam 分页参数
     * @return 分页结果
     * @throws CommonException 异常
     */
    PageResult<MetaTableColumnVO> pageList(MetaTableColumnPageParam pageParam) throws CommonException;

    /**
     * 根据ID查询详情
     *
     * @param tableColumnId ID
     * @return 详情
     * @throws CommonException 异常
     */
    MetaTableColumnVO getDetailById(Long tableColumnId) throws CommonException;

    /**
     * 保存或更新
     *
     * @param param 参数
     * @return 是否成功
     * @throws CommonException 异常
     */
    boolean saveOrUpdate(MetaTableColumnParam param) throws CommonException;

    /**
     * 删除
     *
     * @param tableColumnId ID
     * @return 是否成功
     * @throws CommonException 异常
     */
    boolean deleteById(Long tableColumnId) throws CommonException;

    /**
     * 根据表ID查询列信息列表
     *
     * @param tableId 表ID
     * @return 列信息列表
     * @throws CommonException 异常
     */
    List<MetaTableColumnVO> listByTableId(Long tableId) throws CommonException;

}
