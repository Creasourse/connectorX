package com.cs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cs.entity.ConnDockerServiceEnum;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 插件在docker中的服务名称 Mapper 接口
 * </p>
 *
 * @author wwd
 * @since 2026-04-27
 */
@Mapper
public interface ConnDockerServiceEnumMapper extends BaseMapper<ConnDockerServiceEnum> {

}
