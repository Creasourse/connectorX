package com.cs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cs.entity.WecomSyncLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 企业微信数据同步日志表 Mapper 接口
 * </p>
 *
 * @author wwd
 * @since 2026-03-21
 */
@Mapper
public interface WecomSyncLogMapper extends BaseMapper<WecomSyncLog> {

}