package com.cs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.entity.MetaTableColumn;
import com.cs.exception.CommonException;
import com.cs.mapper.MetaTableColumnMapper;
import com.cs.param.MetaTableColumnPageParam;
import com.cs.param.MetaTableColumnParam;
import com.cs.resp.PageResult;
import com.cs.service.MetaTableColumnService;
import com.cs.vo.MetaTableColumnVO;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wwd
 * @since 2026-03-25
 */
@Service
public class MetaTableColumnServiceImpl extends ServiceImpl<MetaTableColumnMapper, MetaTableColumn> implements MetaTableColumnService {

    @Autowired
    private MapperFactory mapperFactory;

    @Override
    public PageResult<MetaTableColumnVO> pageList(MetaTableColumnPageParam pageParam) throws CommonException {
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

        LambdaQueryWrapper<MetaTableColumn> queryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(pageParam.getTableId())) {
            queryWrapper.eq(MetaTableColumn::getTableId, pageParam.getTableId());
        }
        if (StringUtils.isNotBlank(pageParam.getTableColumnName())) {
            queryWrapper.like(MetaTableColumn::getTableColumnName, pageParam.getTableColumnName());
        }
        if (StringUtils.isNotBlank(pageParam.getTableColumnAlias())) {
            queryWrapper.like(MetaTableColumn::getTableColumnAlias, pageParam.getTableColumnAlias());
        }
        if (Objects.nonNull(pageParam.getIsPk())) {
            queryWrapper.eq(MetaTableColumn::getIsPk, pageParam.getIsPk());
        }
        if (Objects.nonNull(pageParam.getSortType())) {
            switch (pageParam.getSortType()) {
                case 1:
                    queryWrapper.orderByDesc(MetaTableColumn::getTableColumnId);
                    break;
                case 2:
                    queryWrapper.orderByAsc(MetaTableColumn::getTableColumnId);
                    break;
                default:
                    queryWrapper.orderByDesc(MetaTableColumn::getTableColumnId);
                    break;
            }
        } else {
            queryWrapper.orderByDesc(MetaTableColumn::getTableColumnId);
        }

        Page<MetaTableColumn> page = new Page<>(current, size);
        IPage<MetaTableColumn> iPage = page(page, queryWrapper);
        PageResult<MetaTableColumnVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), MetaTableColumnVO.class));

        return pageResult;
    }

    @Override
    public MetaTableColumnVO getDetailById(Long tableColumnId) throws CommonException {
        if (Objects.isNull(tableColumnId)) {
            throw new CommonException("tableColumnId is null");
        }
        MetaTableColumn metaTableColumn = getById(tableColumnId);
        if (Objects.isNull(metaTableColumn)) {
            throw new CommonException("数据不存在");
        }
        return mapperFactory.getMapperFacade().map(metaTableColumn, MetaTableColumnVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(MetaTableColumnParam param) throws CommonException {
        if (Objects.isNull(param)) {
            throw new CommonException("param is null");
        }
        if (Objects.isNull(param.getTableId())) {
            throw new CommonException("关联表ID不能为空");
        }
        if (StringUtils.isBlank(param.getTableColumnName())) {
            throw new CommonException("列名称不能为空");
        }

        MetaTableColumn metaTableColumn = mapperFactory.getMapperFacade().map(param, MetaTableColumn.class);
        return saveOrUpdate(metaTableColumn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long tableColumnId) throws CommonException {
        if (Objects.isNull(tableColumnId)) {
            throw new CommonException("tableColumnId is null");
        }
        return removeById(tableColumnId);
    }

    @Override
    public List<MetaTableColumnVO> listByTableId(Long tableId) throws CommonException {
        if (Objects.isNull(tableId)) {
            throw new CommonException("tableId is null");
        }

        LambdaQueryWrapper<MetaTableColumn> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MetaTableColumn::getTableId, tableId);
        queryWrapper.orderByAsc(MetaTableColumn::getTableColumnId);

        List<MetaTableColumn> columns = list(queryWrapper);
        return mapperFactory.getMapperFacade().mapAsList(columns, MetaTableColumnVO.class);
    }
}
