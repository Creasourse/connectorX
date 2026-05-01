package com.cs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.config.RestTemplateForHttp;
import com.cs.entity.LocalConnector;
import com.cs.exception.CommonException;
import com.cs.mapper.LocalConnectorMapper;
import com.cs.param.LocalConnectorPageParam;
import com.cs.resp.PageResult;
import com.cs.resp.RespResult;
import com.cs.service.LocalConnectorService;
import com.cs.util.StringUtils;
import com.cs.util.UserContext;
import com.cs.vo.LocalConnectorVO;
import com.cs.vo.SysUserVO;
import ma.glasnost.orika.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 我的连接器 服务实现类
 * </p>
 *
 * @author wwd
 * @since 2026-01-30
 */
@Service
public class LocalConnectorServiceImpl extends ServiceImpl<LocalConnectorMapper, LocalConnector> implements LocalConnectorService {

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private RestTemplateForHttp restTemplate;

    @Value("${localConnUrl}")
    private String url;

    @Override
    public PageResult<LocalConnectorVO> pageList(LocalConnectorPageParam localConnectorPageParam) throws CommonException {
        if (Objects.isNull(localConnectorPageParam)) {
            throw new CommonException("参数异常");
        }
        if (Objects.isNull(localConnectorPageParam.getCurrentPage())) {
            localConnectorPageParam.setCurrentPage(1);
        }
        if (Objects.isNull(localConnectorPageParam.getPageSize())) {
            localConnectorPageParam.setPageSize(10);
        }
        Page<LocalConnector> page = Page.of(localConnectorPageParam.getCurrentPage(), localConnectorPageParam.getPageSize());
        LambdaQueryWrapper<LocalConnector> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(LocalConnector::getConnectorId, 0);
        if (!StringUtils.isEmpty(localConnectorPageParam.getConnectorName())) {
            queryWrapper.like(LocalConnector::getConnectorName, localConnectorPageParam.getConnectorName());
        }
        if (Objects.nonNull(localConnectorPageParam.getSortType())) {
            switch (localConnectorPageParam.getSortType()) {
                case 1:
                    queryWrapper.orderByDesc(LocalConnector::getCreateTime);

                    break;
                case 2:
                    queryWrapper.orderByAsc(LocalConnector::getCreateTime);
                    break;
                case 3:
                    queryWrapper.orderByDesc(LocalConnector::getUpdateTime);
                    break;
                case 4:
                    queryWrapper.orderByAsc(LocalConnector::getUpdateTime);
                    break;
                default:
                    queryWrapper.orderByAsc(LocalConnector::getCreateTime);
                    break;
            }
        }
        IPage<LocalConnector> localConnectorPage = this.page(page, queryWrapper);
        PageResult<LocalConnectorVO> pageResult = mapperFactory.getMapperFacade().map(localConnectorPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(localConnectorPage.getRecords(), LocalConnectorVO.class));
        return pageResult;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Boolean syncConnector() throws CommonException {
        SysUserVO sysUserVO = UserContext.getCurrentUser();
        if (Objects.isNull(sysUserVO)) {
            throw new CommonException("用户信息异常");
        }
        String phone = sysUserVO.getPhone();
        if (StringUtils.isEmpty(phone)) {
            throw new CommonException("用户手机号不存在,请先注册手机号");
        }
        List<LocalConnector> saveList = new ArrayList<>();
        List<LocalConnector> localConnectorList = list(Wrappers.<LocalConnector>lambdaQuery().ge(LocalConnector::getLocalConnectorId, 0));
        if (Objects.isNull(localConnectorList) || localConnectorList.size() == 0) {
            localConnectorList = new ArrayList<>();
        }
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        builder = builder.replaceQueryParam("phone", phone);
        url = builder.toUriString();
        try {
            ResponseEntity<RespResult<List<LocalConnectorVO>>> response = restTemplate.exchange(url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RespResult<List<LocalConnectorVO>>>() {
                    });
            if (response.getStatusCodeValue() == HttpStatus.FORBIDDEN.value()) {
                throw new CommonException("FORBIDDEN: " + response.getBody());
            }
            if (response.getStatusCodeValue() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                throw new CommonException("内部错误:" + response.getBody());
            }
            if (response.getStatusCodeValue() == HttpStatus.OK.value()) {
                List<LocalConnectorVO> localConnectorVOS = response.getBody().getData();
                if (Objects.nonNull(localConnectorVOS) && localConnectorVOS.size() > 0) {
                    if (localConnectorList.size() > 0) {
                        // 判断新增或者修改
                        for (LocalConnectorVO localConnectorVO : localConnectorVOS) {
                            LocalConnector localConnector = localConnectorList.stream().filter(localConnector1 -> localConnector1.getConnectorId().equals(localConnectorVO.getConnectorId()) && localConnector1.getVersion().equals(localConnectorVO.getVersion())).findFirst().orElse(null);
                            if (Objects.isNull(localConnector)) {
                                saveList.add(mapperFactory.getMapperFacade().map(localConnectorVO, LocalConnector.class));
                            }
                        }
                    } else {
                        saveList.addAll(mapperFactory.getMapperFacade().mapAsList(localConnectorVOS, LocalConnector.class));
                    }
                    if (saveList.size() > 0) {
                        if (!saveOrUpdateBatch(saveList)) {
                            throw new CommonException("保存失败");

                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("查询流列表异常:{}", e);
        }
        return Boolean.TRUE;

    }
}
