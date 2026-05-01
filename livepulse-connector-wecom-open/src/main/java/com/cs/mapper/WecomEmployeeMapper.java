package com.cs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cs.entity.WecomEmployee;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 企业微信员工基本信息表 Mapper 接口
 * </p>
 *
 * @author wwd
 * @since 2026-03-21
 */
@Mapper
public interface WecomEmployeeMapper extends BaseMapper<WecomEmployee> {

}