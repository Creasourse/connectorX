package com.cs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.entity.LocalConnector;
import com.cs.exception.CommonException;
import com.cs.param.LocalConnectorPageParam;
import com.cs.resp.PageResult;
import com.cs.vo.LocalConnectorVO;

/**
 * <p>
 * 我的连接器 服务类
 * </p>
 *
 * @author wwd
 * @since 2026-01-30
 */
public interface LocalConnectorService extends IService<LocalConnector> {

    PageResult<LocalConnectorVO> pageList(LocalConnectorPageParam localConnectorPageParam) throws CommonException;

    Boolean syncConnector() throws CommonException;
}
