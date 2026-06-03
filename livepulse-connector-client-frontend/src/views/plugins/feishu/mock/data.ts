/**
 * Mock 数据
 * 用于前端开发和测试
 */

import type {
  ConnectionConfig,
  SyncTask,
  SyncLog,
  SyncStats,
  Bitable,
  DataTable,
  Field
} from '../types';

// ============ 连接配置 Mock ============

export const mockConnectionConfig: ConnectionConfig = {
  maxConnections: 10,
  retryStrategy: 'exponential',
  maxRetries: 3,
  dataFilter: '{"status": "completed", "amount": {"$gt": 100}}',
  dataRange: 'all',
  excludedFields: ['创建人ID', '最后修改人'],
};

// ============ 同步任务 Mock ============

export const mockSyncTasks: SyncTask[] = [
  {
    id: 1,
    name: '实时线索同步',
    status: 'active',
    tableName: '销售线索池',
    direction: 'bidirectional',
    syncTime: '2分钟前',
    syncedCount: 1245,
  },
  {
    id: 2,
    name: '客户信息增量同步',
    status: 'pending',
    tableName: '客户信息表',
    direction: 'pull',
    syncTime: '5分钟后',
    syncedCount: 458,
  },
  {
    id: 3,
    name: '订单数据同步',
    status: 'paused',
    tableName: '订单表',
    direction: 'push',
    syncTime: '已暂停',
    syncedCount: 3241,
  },
];

// ============ 同步日志 Mock ============

export const mockSyncLogs: SyncLog[] = [
  {
    id: 1,
    bitableId: 1,
    bitableName: '销售线索池',
    syncMode: 'webhook',
    syncStatus: 'success',
    recordCount: 100,
    successCount: 98,
    failedCount: 2,
    startTime: '2026-05-24 10:00:00',
    endTime: '2026-05-24 10:01:23',
    duration: 83,
  },
  {
    id: 2,
    bitableId: 1,
    bitableName: '销售线索池',
    syncMode: 'webhook',
    syncStatus: 'success',
    recordCount: 50,
    successCount: 50,
    failedCount: 0,
    startTime: '2026-05-24 09:55:00',
    endTime: '2026-05-24 09:55:45',
    duration: 45,
  },
  {
    id: 3,
    bitableId: 2,
    bitableName: '客户信息表',
    syncMode: 'polling',
    syncStatus: 'failed',
    recordCount: 200,
    successCount: 150,
    failedCount: 50,
    startTime: '2026-05-24 09:50:00',
    endTime: '2026-05-24 09:52:30',
    duration: 150,
    errorMessage: '部分记录同步失败：字段验证错误',
  },
  {
    id: 4,
    bitableId: 2,
    bitableName: '客户信息表',
    syncMode: 'hybrid',
    syncStatus: 'processing',
    recordCount: 300,
    successCount: 200,
    failedCount: 0,
    startTime: '2026-05-24 10:05:00',
    endTime: '',
    duration: 0,
  },
];

export const mockSyncStats: SyncStats = {
  successCount: 1523,
  failedCount: 12,
  processingCount: 5,
  totalCount: 1540,
};

// ============ 多维表格 Mock ============

export const mockBitables: Bitable[] = [
  {
    id: 1,
    bitableName: '销售多维表格',
    appToken: 'bascnVWXjYZabcdefg',
    feishuAppId: 1,
  },
  {
    id: 2,
    bitableName: '客户管理表',
    appToken: 'bascnQyOobsJataZnens',
    feishuAppId: 1,
  },
  {
    id: 3,
    bitableName: '订单管理',
    appToken: 'bascnXYZ123456789',
    feishuAppId: 1,
  },
];

export const mockDataTables: DataTable[] = [
  {
    tableId: 'tblYIQWl5Cp9CEsl',
    name: '销售线索池',
    revision: 1,
    fieldCount: 15,
  },
  {
    tableId: 'tblBHe4RTnCDbaoz',
    name: '客户信息表',
    revision: 2,
    fieldCount: 20,
  },
  {
    tableId: 'tblCXz5EjKL8mnop',
    name: '订单表',
    revision: 1,
    fieldCount: 25,
  },
];

export const mockFields: Field[] = [
  {
    fieldId: 'fld123456789',
    fieldName: '客户姓名',
    type: 1,
    uiType: 'Text',
    description: '客户姓名',
    isRequired: true,
  },
  {
    fieldId: 'fld987654321',
    fieldName: '手机号',
    type: 13,
    uiType: 'Phone',
    description: '联系电话',
    isRequired: true,
  },
  {
    fieldId: 'fld456789123',
    fieldName: '负责人',
    type: 7,
    uiType: 'MultiPerson',
    description: '销售负责人',
    isRequired: false,
  },
  {
    fieldId: 'fld789123456',
    fieldName: '创建时间',
    type: 5,
    uiType: 'DateTime',
    description: '记录创建时间',
    isRequired: false,
  },
];

// ============ 表格列表选项 Mock ============

export const mockTableOptions = [
  { label: '销售线索池 (table_sales_xxxxx)', value: 'table_sales' },
  { label: '客户信息表 (table_customer_xxxxx)', value: 'table_customer' },
  { label: '订单表 (table_order_xxxxx)', value: 'table_order' },
  { label: '产品表 (table_product_xxxxx)', value: 'table_product' },
];

// ============ 字段映射 Mock ============

export const mockFieldMappings = [
  {
    sourceField: '客户姓名',
    targetField: 'name',
    rule: '文本直传',
    ruleType: 'text' as const,
  },
  {
    sourceField: '手机号',
    targetField: 'phone',
    rule: '手机号格式化',
    ruleType: 'link' as const,
  },
  {
    sourceField: '负责人',
    targetField: 'owner',
    rule: '人员→用户ID',
    ruleType: 'purple' as const,
  },
  {
    sourceField: '创建时间',
    targetField: 'created_at',
    rule: '时间戳转换',
    ruleType: 'text' as const,
  },
];

// ============ Mock 延迟函数 ============

/**
 * 模拟 API 延迟
 */
export function mockDelay(ms: number = 500) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

/**
 * 模拟 API 响应
 */
export async function mockApiResponse<T>(
  data: T,
  delay: number = 500
): Promise<T> {
  await mockDelay(delay);
  return data;
}

/**
 * 模拟分页响应
 */
export async function mockPageResponse<T>(
  list: T[],
  pageNum: number = 1,
  pageSize: number = 20,
  delay: number = 500
) {
  await mockDelay(delay);

  const start = (pageNum - 1) * pageSize;
  const end = start + pageSize;
  const paginatedList = list.slice(start, end);

  return {
    list: paginatedList,
    total: list.length,
    current: pageNum,
    size: pageSize,
    lastPageNo: Math.ceil(list.length / pageSize),
  };
}

// ============ Mock 数据生成器 ============

/**
 * 生成随机 ID
 */
export function generateId(): number {
  return Math.floor(Math.random() * 1000000);
}

/**
 * 生成随机时间
 */
export function randomTime(offsetMinutes: number = 0): string {
  const now = new Date();
  now.setMinutes(now.getMinutes() - offsetMinutes);
  return now.toISOString().replace('T', ' ').substring(0, 19);
}

/**
 * 生成随机同步任务
 */
export function generateRandomTask(): SyncTask {
  const statuses: Array<'active' | 'pending' | 'paused'> = ['active', 'pending', 'paused'];
  const directions: Array<'pull' | 'push' | 'bidirectional'> = ['pull', 'push', 'bidirectional'];
  const tables = mockDataTables.map(t => t.name);

  return {
    id: generateId(),
    name: `自动生成任务 ${Math.floor(Math.random() * 1000)}`,
    status: statuses[Math.floor(Math.random() * statuses.length)],
    tableName: tables[Math.floor(Math.random() * tables.length)],
    direction: directions[Math.floor(Math.random() * directions.length)],
    syncTime: randomTime(Math.floor(Math.random() * 60)),
    syncedCount: Math.floor(Math.random() * 5000),
  };
}

/**
 * 生成随机同步日志
 */
export function generateRandomLog(): SyncLog {
  const statuses: Array<'success' | 'failed' | 'processing'> = ['success', 'failed', 'processing'];
  const modes: Array<'webhook' | 'polling' | 'hybrid'> = ['webhook', 'polling', 'hybrid'];
  const tables = mockDataTables.map(t => t.name);

  const status = statuses[Math.floor(Math.random() * statuses.length)];
  const recordCount = Math.floor(Math.random() * 500) + 10;

  return {
    id: generateId(),
    bitableId: Math.floor(Math.random() * 10) + 1,
    bitableName: tables[Math.floor(Math.random() * tables.length)],
    syncMode: modes[Math.floor(Math.random() * modes.length)],
    syncStatus: status,
    recordCount,
    successCount: status === 'failed' ? Math.floor(recordCount * 0.7) : Math.floor(recordCount * 0.95),
    failedCount: status === 'success' ? Math.floor(recordCount * 0.05) : Math.floor(recordCount * 0.3),
    startTime: randomTime(Math.floor(Math.random() * 120)),
    endTime: status === 'processing' ? '' : randomTime(Math.floor(Math.random() * 60)),
    duration: status === 'processing' ? 0 : Math.floor(Math.random() * 300),
    errorMessage: status === 'failed' ? '同步失败：网络超时' : undefined,
  };
}
