/**
 * API 适配层
 * 处理前后端路径映射和数据转换
 */

import type {
  ConnectionConfig,
  SyncTask,
  CreateSyncTaskRequest,
  SyncLog,
  SyncStats,
  LogQueryParams,
  Bitable,
  FeishuApp,
  ApiResponse,
  Result
} from '../types';

// ============ 路径配置 ============

/**
 * API 基础路径前缀
 * 根据部署环境修改
 */
const API_PREFIX = '/feishu-muti';

/**
 * 路径映射配置
 */
const PATH_MAPPING = {
  // 飞书应用
  feishuApp: {
    saveOrUpdate: '/feishu/app/saveOrUpdate',
    detail: '/feishu/app/detail',
    delete: '/feishu/app/delete',
    test: '/feishu/app/test',
    toggle: '/feishu/app/toggleEnabled',
  },

  // 连接配置
  connection: {
    save: `${API_PREFIX}/api/config/save`,
    get: `${API_PREFIX}/api/config/get`,
    test: `${API_PREFIX}/api/config/test`,
  },

  // 同步任务
  syncTask: {
    list: `${API_PREFIX}/api/sync/task/list`,
    create: `${API_PREFIX}/api/sync/task/create`,
    update: `${API_PREFIX}/api/sync/task/update`,
    delete: `${API_PREFIX}/api/sync/task/delete`,
    toggle: `${API_PREFIX}/api/sync/task/toggle`,
    trigger: `${API_PREFIX}/api/sync/task/trigger`,
  },

  // 同步日志
  syncLog: {
    list: `${API_PREFIX}/api/sync/log/list`,
    detail: `${API_PREFIX}/api/sync/log/detail`,
    retry: `${API_PREFIX}/api/sync/log/retry`,
  },

  // 多维表格
  bitable: {
    list: `${API_PREFIX}/api/bitable/table/list`,
    fields: `${API_PREFIX}/api/bitable/field/list`,
    saveOrUpdate: '/feishu/bitable/saveOrUpdate',
    detail: '/feishu/bitable/detail',
    delete: '/feishu/bitable/delete',
  },
};

// ============ 数据转换函数 ============

/**
 * 转换前端连接配置为后端格式
 */
export function transformConnectionConfigForApi(config: ConnectionConfig) {
  return {
    maxConnections: config.maxConnections,
    retryStrategy: config.retryStrategy,
    maxRetries: config.maxRetries,
    dataFilter: config.dataFilter,
    dataRange: config.dataRange,
    excludedFields: config.excludedFields.join(','),
  };
}

/**
 * 转换后端连接配置为前端格式
 */
export function transformConnectionConfigFromApi(data: any): ConnectionConfig {
  return {
    maxConnections: data.maxConnections || 10,
    retryStrategy: data.retryStrategy || 'exponential',
    maxRetries: data.maxRetries || 3,
    dataFilter: data.dataFilter || '',
    dataRange: data.dataRange || 'all',
    excludedFields: data.excludedFields ? data.excludedFields.split(',') : [],
  };
}

/**
 * 转换前端同步任务为后端格式
 */
export function transformSyncTaskForApi(task: CreateSyncTaskRequest) {
  return {
    taskName: task.taskName,
    targetTable: task.targetTable,
    syncDirection: task.syncDirection,
    advancedFilter: task.advancedFilter,
    smartMapping: task.smartMapping,
    mappingRules: task.mappingRules,
    updateDetection: task.updateDetection,
    incrementalSync: task.incrementalSync,
    incrementalField: task.incrementalField,
    firstSync: task.firstSync,
    recordConflict: task.recordConflict,
    fieldConflict: task.fieldConflict,
    timeWindow: task.timeWindow,
    batchSize: task.batchSize,
    batchInterval: task.batchInterval,
    fieldMappings: JSON.stringify(task.fieldMappings),
  };
}

/**
 * 转换后端同步任务为前端格式
 */
export function transformSyncTaskFromApi(data: any): SyncTask {
  return {
    id: data.id,
    name: data.taskName || data.name,
    status: data.status,
    tableName: data.tableName || data.targetTable,
    direction: data.direction || data.syncDirection,
    syncTime: data.syncTime || data.lastSyncTime || data.nextSyncTime,
    syncedCount: data.syncedCount || data.recordCount || 0,
    syncing: false,
  };
}

/**
 * 转换后端同步日志为前端格式
 */
export function transformSyncLogFromApi(data: any): SyncLog {
  return {
    id: data.id,
    bitableId: data.bitableId,
    bitableName: data.bitableName,
    syncMode: data.syncMode,
    syncStatus: data.syncStatus,
    recordCount: data.recordCount || 0,
    successCount: data.successCount || 0,
    failedCount: data.failedCount || 0,
    startTime: data.startTime,
    endTime: data.endTime,
    duration: data.duration || 0,
    errorMessage: data.errorMessage,
  };
}

/**
 * 转换前端查询参数为后端格式
 */
export function transformLogQueryParamsForApi(params: LogQueryParams) {
  return {
    bitableId: params.bitableId,
    syncStatus: params.syncStatus,
    startTime: params.dateRange?.[0],
    endTime: params.dateRange?.[1],
    pageNum: params.pageNum,
    pageSize: params.pageSize,
  };
}

/**
 * 处理 API 响应
 */
export function handleApiResponse<T>(response: Result<T>): T {
  if (response.success && response.data) {
    return response.data;
  }
  throw new Error(response.msg || response.errorMsg || '请求失败');
}

/**
 * 处理分页响应
 */
export function handlePageResponse<T>(response: Result<PageResponse<T>>) {
  const data = handleApiResponse(response);
  return {
    list: data.list || [],
    total: data.total || 0,
    current: data.current || 1,
    size: data.size || 20,
    lastPageNo: data.lastPageNo || 1,
  };
}

// ============ 导出路径映射 ============

export { PATH_MAPPING };

/**
 * 获取完整 API 路径
 */
export function getApiPath(module: keyof typeof PATH_MAPPING, action: string): string {
  const modulePaths = PATH_MAPPING[module];
  if (!modulePaths) {
    console.warn(`API module not found: ${module}`);
    return '';
  }

  const path = (modulePaths as any)[action];
  if (!path) {
    console.warn(`API action not found: ${module}.${action}`);
    return '';
  }

  return path;
}
