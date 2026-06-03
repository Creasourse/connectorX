/**
 * Mock 服务
 * 在开发环境中模拟后端 API 调用
 */

import { http } from '@/utils/http';
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
import {
  mockConnectionConfig,
  mockSyncTasks,
  mockSyncLogs,
  mockSyncStats,
  mockDataTables,
  mockTableOptions,
  mockApiResponse,
  mockPageResponse,
  generateRandomTask,
  generateRandomLog
} from './data';

/**
 * 是否启用 Mock 模式
 * 在生产环境或需要连接真实后端时设置为 false
 */
const ENABLE_MOCK = true;

/**
 * 检查是否使用 Mock
 */
function shouldUseMock(): boolean {
  return ENABLE_MOCK || import.meta.env.DEV;
}

// ============ 连接配置 API ============

/**
 * 保存连接配置
 */
export async function saveConnectionConfig(config: ConnectionConfig): Promise<Result<boolean>> {
  if (shouldUseMock()) {
    await mockApiResponse(1000);
    return {
      responseStatus: 200,
      code: 0,
      success: true,
      msg: '保存成功',
      data: true,
    };
  }

  // 真实 API 调用
  return http.request<Result<boolean>>(
    'post',
    '/feishu-muti/api/config/save',
    { data: config }
  );
}

/**
 * 获取连接配置
 */
export async function getConnectionConfig(): Promise<Result<ConnectionConfig>> {
  if (shouldUseMock()) {
    await mockApiResponse(300);
    return {
      responseStatus: 200,
      code: 0,
      success: true,
      msg: '获取成功',
      data: mockConnectionConfig,
    };
  }

  return http.request<Result<ConnectionConfig>>(
    'get',
    '/feishu-muti/api/config/get'
  );
}

/**
 * 测试连接
 */
export async function testConnection(config: ConnectionConfig): Promise<Result<boolean>> {
  if (shouldUseMock()) {
    await mockApiResponse(1500);
    return {
      responseStatus: 200,
      code: 0,
      success: true,
      msg: '连接测试成功',
      data: true,
    };
  }

  return http.request<Result<boolean>>(
    'post',
    '/feishu-muti/api/config/test',
    { data: config }
  );
}

// ============ 同步任务 API ============

/**
 * 获取同步任务列表
 */
export async function getSyncTaskList(): Promise<Result<SyncTask[]>> {
  if (shouldUseMock()) {
    const tasks = await mockApiResponse(mockSyncTasks, 500);
    return {
      responseStatus: 200,
      code: 0,
      success: true,
      msg: '获取成功',
      data: tasks,
    };
  }

  return http.request<Result<SyncTask[]>>(
    'get',
    '/feishu-muti/api/sync/task/list'
  );
}

/**
 * 创建同步任务
 */
export async function createSyncTask(task: CreateSyncTaskRequest): Promise<Result<SyncTask>> {
  if (shouldUseMock()) {
    const newTask = await mockApiResponse(
      {
        ...generateRandomTask(),
        name: task.taskName,
        tableName: mockTableOptions.find(t => t.value === task.targetTable)?.label || task.targetTable,
        direction: task.syncDirection,
      },
      1000
    );
    return {
      responseStatus: 200,
      code: 0,
      success: true,
      msg: '创建成功',
      data: newTask,
    };
  }

  return http.request<Result<SyncTask>>(
    'post',
    '/feishu-muti/api/sync/task/create',
    { data: task }
  );
}

/**
 * 更新同步任务
 */
export async function updateSyncTask(task: SyncTask): Promise<Result<boolean>> {
  if (shouldUseMock()) {
    await mockApiResponse(800);
    return {
      responseStatus: 200,
      code: 0,
      success: true,
      msg: '更新成功',
      data: true,
    };
  }

  return http.request<Result<boolean>>(
    'put',
    `/feishu-muti/api/sync/task/update/${task.id}`,
    { data: task }
  );
}

/**
 * 切换任务状态（暂停/启用）
 */
export async function toggleTaskStatus(taskId: number, status: 'active' | 'paused'): Promise<Result<boolean>> {
  if (shouldUseMock()) {
    await mockApiResponse(600);
    return {
      responseStatus: 200,
      code: 0,
      success: true,
      msg: status === 'active' ? '任务已启用' : '任务已暂停',
      data: true,
    };
  }

  return http.request<Result<boolean>>(
    'put',
    `/feishu-muti/api/sync/task/toggle/${taskId}`,
    { data: { status } }
  );
}

/**
 * 触发立即同步
 */
export async function triggerSync(taskId: number): Promise<Result<boolean>> {
  if (shouldUseMock()) {
    await mockApiResponse(2000);
    return {
      responseStatus: 200,
      code: 0,
      success: true,
      msg: '同步已触发',
      data: true,
    };
  }

  return http.request<Result<boolean>>(
    'post',
    `/feishu-muti/api/sync/task/trigger/${taskId}`
  );
}

/**
 * 删除同步任务
 */
export async function deleteSyncTask(taskId: number): Promise<Result<boolean>> {
  if (shouldUseMock()) {
    await mockApiResponse(500);
    return {
      responseStatus: 200,
      code: 0,
      success: true,
      msg: '删除成功',
      data: true,
    };
  }

  return http.request<Result<boolean>>(
    'delete',
    `/feishu-muti/api/sync/task/delete/${taskId}`
  );
}

// ============ 同步日志 API ============

/**
 * 获取同步日志列表
 */
export async function getSyncLogList(params: LogQueryParams): Promise<Result<PageResponse<SyncLog>>> {
  if (shouldUseMock()) {
    const pageData = await mockPageResponse(
      mockSyncLogs,
      params.pageNum,
      params.pageSize,
      600
    );
    return {
      responseStatus: 200,
      code: 0,
      success: true,
      msg: '获取成功',
      data: pageData,
    };
  }

  return http.request<Result<PageResponse<SyncLog>>>(
    'post',
    '/feishu-muti/api/sync/log/list',
    { data: params }
  );
}

/**
 * 获取日志详情
 */
export async function getSyncLogDetail(logId: number): Promise<Result<SyncLog>> {
  if (shouldUseMock()) {
    const log = mockSyncLogs.find(l => l.id === logId) || generateRandomLog();
    await mockApiResponse(log, 300);
    return {
      responseStatus: 200,
      code: 0,
      success: true,
      msg: '获取成功',
      data: log,
    };
  }

  return http.request<Result<SyncLog>>(
    'get',
    `/feishu-muti/api/sync/log/detail/${logId}`
  );
}

/**
 * 重试同步
 */
export async function retrySync(logId: number): Promise<Result<boolean>> {
  if (shouldUseMock()) {
    await mockApiResponse(1500);
    return {
      responseStatus: 200,
      code: 0,
      success: true,
      msg: '已触发重试',
      data: true,
    };
  }

  return http.request<Result<boolean>>(
    'post',
    `/feishu-muti/api/sync/log/retry/${logId}`
  );
}

/**
 * 获取同步统计
 */
export async function getSyncStats(): Promise<Result<SyncStats>> {
  if (shouldUseMock()) {
    await mockApiResponse(400);
    return {
      responseStatus: 200,
      code: 0,
      success: true,
      msg: '获取成功',
      data: mockSyncStats,
    };
  }

  return http.request<Result<SyncStats>>(
    'get',
    '/feishu-muti/api/sync/stats'
  );
}

// ============ 多维表格 API ============

/**
 * 获取数据表列表
 */
export async function getTableList(): Promise<Result<typeof mockDataTables>> {
  if (shouldUseMock()) {
    await mockApiResponse(500);
    return {
      responseStatus: 200,
      code: 0,
      success: true,
      msg: '获取成功',
      data: mockDataTables,
    };
  }

  return http.request<Result<typeof mockDataTables>>(
    'get',
    '/feishu-muti/api/bitable/table/list'
  );
}

/**
 * 获取表格选项（用于下拉选择）
 */
export async function getTableOptions(): Promise<Result<typeof mockTableOptions>> {
  if (shouldUseMock()) {
    await mockApiResponse(300);
    return {
      responseStatus: 200,
      code: 0,
      success: true,
      msg: '获取成功',
      data: mockTableOptions,
    };
  }

  return http.request<Result<typeof mockTableOptions>>(
    'get',
    '/feishu-muti/api/bitable/table/options'
  );
}

// ============ 导出 Mock 配置 ============

export { shouldUseMock, ENABLE_MOCK };
