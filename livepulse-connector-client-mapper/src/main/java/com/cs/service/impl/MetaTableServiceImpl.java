package com.cs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cs.entity.MetaTable;
import com.cs.exception.CommonException;
import com.cs.mapper.MetaTableMapper;
import com.cs.param.MetaTablePageParam;
import com.cs.param.MetaTableParam;
import com.cs.resp.PageResult;
import com.cs.service.MetaTableService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.vo.MetaTableVO;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * cdp表 服务实现类
 * </p>
 *
 * @author wwd
 * @since 2026-05-03
 */
@Service
public class MetaTableServiceImpl extends ServiceImpl<MetaTableMapper, MetaTable> implements MetaTableService {

    @Autowired
    private MapperFactory mapperFactory;

    @Override
    public PageResult<MetaTableVO> pageList(MetaTablePageParam pageParam) throws CommonException {
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

        LambdaQueryWrapper<MetaTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(MetaTable::getTableId, 0);
        if (StringUtils.isNotBlank(pageParam.getTableName())) {
            queryWrapper.like(MetaTable::getTableName, pageParam.getTableName());
        }
        if (StringUtils.isNotBlank(pageParam.getTableAlias())) {
            queryWrapper.like(MetaTable::getTableAlias, pageParam.getTableAlias());
        }
        if (Objects.nonNull(pageParam.getSortType())) {
            switch (pageParam.getSortType()) {
                case 1:
                    queryWrapper.orderByDesc(MetaTable::getCreateTime);
                    break;
                case 2:
                    queryWrapper.orderByAsc(MetaTable::getCreateTime);
                    break;
                case 3:
                    queryWrapper.orderByDesc(MetaTable::getUpdateTime);
                    break;
                case 4:
                    queryWrapper.orderByAsc(MetaTable::getUpdateTime);
                    break;
                default:
                    queryWrapper.orderByDesc(MetaTable::getCreateTime);
                    break;
            }
        } else {
            queryWrapper.orderByDesc(MetaTable::getCreateTime);
        }

        Page<MetaTable> page = new Page<>(current, size);
        IPage<MetaTable> iPage = page(page, queryWrapper);
        PageResult<MetaTableVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), MetaTableVO.class));

        return pageResult;
    }

    @Override
    public MetaTableVO getDetailById(Long tableId) throws CommonException {
        if (Objects.isNull(tableId)) {
            throw new CommonException("tableId is null");
        }
        MetaTable metaTable = getById(tableId);
        if (Objects.isNull(metaTable)) {
            throw new CommonException("数据不存在");
        }
        return mapperFactory.getMapperFacade().map(metaTable, MetaTableVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(MetaTableParam param) throws CommonException {
        if (Objects.isNull(param)) {
            throw new CommonException("param is null");
        }
        if (StringUtils.isBlank(param.getTableName())) {
            throw new CommonException("表名称不能为空");
        }
        // 校验表名称唯一性
        MetaTable nameMetaTable = getOne(new LambdaQueryWrapper<MetaTable>().eq(MetaTable::getTableName, param.getTableName()));
        // 新建场景：param.getTableId() 为空
        if (Objects.isNull(param.getTableId())) {
            if (Objects.nonNull(nameMetaTable)) {
                throw new CommonException("表名称已存在");
            }
        }
        // 更新场景：param.getTableId() 不为空
        else {
            if (Objects.nonNull(nameMetaTable) && !Objects.equals(nameMetaTable.getTableId(), param.getTableId())) {
                throw new CommonException("表名称已存在");
            }
        }


        MetaTable metaTable = mapperFactory.getMapperFacade().map(param, MetaTable.class);
        metaTable.setUpdateTime(LocalDateTime.now());
        if (Objects.isNull(metaTable.getTableId())) {
            metaTable.setCreateTime(LocalDateTime.now());
        }
        metaTable.setType(1);
        return saveOrUpdate(metaTable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long tableId) throws CommonException {
        if (Objects.isNull(tableId)) {
            throw new CommonException("tableId is null");
        }
        return removeById(tableId);
    }
}
