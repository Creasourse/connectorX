/**
 * 飞书插件 API 服务
 * 统一导出所有 API 调用
 */

import type {
  ConnectionConfig,
  SyncTask,
  CreateSyncTaskRequest,
  SyncLog,
  SyncStats,
  LogQueryParams,
  PageResponse,
  Result
} from '../types';

// 导入 Mock 服务
import {
  saveConnectionConfig as mockSaveConfig,
  getConnectionConfig as mockGetConfig,
  testConnection as mockTestConnection,
  getSyncTaskList as mockGetTaskList,
  createSyncTask as mockCreateTask,
  updateSyncTask as mockUpdateTask,
  toggleTaskStatus as mockToggleTask,
  triggerSync as mockTriggerSync,
  deleteSyncTask as mockDeleteTask,
  getSyncLogList as mockGetLogList,
  getSyncLogDetail as mockGetLogDetail,
  retrySync as mockRetrySync,
  getSyncStats as mockGetStats,
  getTableList as mockGetTableList,
  getTableOptions as mockGetTableOptions,
  shouldUseMock
} from '../mock/service';

/**
 * 是否使用 Mock 数据
 * 在 .env 中设置 VITE_USE_MOCK=true 启用
 */
const USE_MOCK = import.meta.env.VITE_USE_MOCK === 'true' || shouldUseMock();

// ============ 连接配置 API ============

/**
 * 保存连接配置
 */
export async function saveConnectionConfig(config: ConnectionConfig): Promise<Result<boolean>> {
  if (USE_MOCK) {
    return mockSaveConfig(config);
  }

  // TODO: 实现真实 API 调用
  return mockSaveConfig(config);
}

/**
 * 获取连接配置
 */
export async function getConnectionConfig(): Promise<Result<ConnectionConfig>> {
  if (USE_MOCK) {
    return mockGetConfig();
  }

  // TODO: 实现真实 API 调用
  return mockGetConfig();
}

/**
 * 测试连接
 */
export async function testConnection(config: ConnectionConfig): Promise<Result<boolean>> {
  if (USE_MOCK) {
    return mockTestConnection(config);
  }

  // TODO: 实现真实 API 调用
  return mockTestConnection(config);
}

// ============ 同步任务 API ============

/**
 * 获取同步任务列表
 */
export async function getSyncTaskList(): Promise<Result<SyncTask[]>> {
  if (USE_MOCK) {
    return mockGetTaskList();
  }

  // TODO: 实现真实 API 调用
  return mockGetTaskList();
}

/**
 * 创建同步任务
 */
export async function createSyncTask(task: CreateSyncTaskRequest): Promise<Result<SyncTask>> {
  if (USE_MOCK) {
    return mockCreateTask(task);
  }

  // TODO: 实现真实 API 调用
  return mockCreateTask(task);
}

/**
 * 更新同步任务
 */
export async function updateSyncTask(task: SyncTask): Promise<Result<boolean>> {
  if (USE_MOCK) {
    return mockUpdateTask(task);
  }

  // TODO: 实现真实 API 调用
  return mockUpdateTask(task);
}

/**
 * 切换任务状态（暂停/启用）
 */
export async function toggleTaskStatus(taskId: number, status: 'active' | 'paused'): Promise<Result<boolean>> {
  if (USE_MOCK) {
    return mockToggleTask(taskId, status);
  }

  // TODO: 实现真实 API 调用
  return mockToggleTask(taskId, status);
}

/**
 * 触发立即同步
 */
export async function triggerSync(taskId: number): Promise<Result<boolean>> {
  if (USE_MOCK) {
    return mockTriggerSync(taskId);
  }

  // TODO: 实现真实 API 调用
  return mockTriggerSync(taskId);
}

/**
 * 删除同步任务
 */
export async function deleteSyncTask(taskId: number): Promise<Result<boolean>> {
  if (USE_MOCK) {
    return mockDeleteTask(taskId);
  }

  // TODO: 实现真实 API 调用
  return mockDeleteTask(taskId);
}

// ============ 同步日志 API ============

/**
 * 获取同步日志列表
 */
export async function getSyncLogList(params: LogQueryParams): Promise<Result<PageResponse<SyncLog>>> {
  if (USE_MOCK) {
    return mockGetLogList(params);
  }

  // TODO: 实现真实 API 调用
  return mockGetLogList(params);
}

/**
 * 获取日志详情
 */
export async function getSyncLogDetail(logId: number): Promise<Result<SyncLog>> {
  if (USE_MOCK) {
    return mockGetLogDetail(logId);
  }

  // TODO: 实现真实 API 调用
  return mockGetLogDetail(logId);
}

/**
 * 重试同步
 */
export async function retrySync(logId: number): Promise<Result<boolean>> {
  if (USE_MOCK) {
    return mockRetrySync(logId);
  }

  // TODO: 实现真实 API 调用
  return mockRetrySync(logId);
}

/**
 * 获取同步统计
 */
export async function getSyncStats(): Promise<Result<SyncStats>> {
  if (USE_MOCK) {
    return mockGetStats();
  }

  // TODO: 实现真实 API 调用
  return mockGetStats();
}

// ============ 多维表格 API ============

/**
 * 获取数据表列表
 */
export async function getTableList() {
  if (USE_MOCK) {
    return mockGetTableList();
  }

  // TODO: 实现真实 API 调用
  return mockGetTableList();
}

/**
 * 获取表格选项（用于下拉选择）
 */
export async function getTableOptions() {
  if (USE_MOCK) {
    return mockGetTableOptions();
  }

  // TODO: 实现真实 API 调用
  return mockGetTableOptions();
}

// ============ 导出配置 ============

export { USE_MOCK };
