package com.cs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cs.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author wwd
 * @since 2026-01-30
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}
