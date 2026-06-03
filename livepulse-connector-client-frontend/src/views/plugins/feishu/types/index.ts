/**
 * 飞书插件类型定义
 */

// ============ 连接配置相关 ============

/**
 * 连接配置
 */
export interface ConnectionConfig {
  maxConnections: number;
  retryStrategy: 'exponential' | 'fixed' | 'linear';
  maxRetries: number;
  dataFilter: string;
  dataRange: 'all' | '7days' | '30days' | 'custom';
  excludedFields: string[];
}

/**
 * 保存连接配置请求
 */
export interface SaveConnectionConfigRequest extends ConnectionConfig {
  feishuAppId?: string;
}

// ============ 同步任务相关 ============

/**
 * 同步方向
 */
export type SyncDirection = 'pull' | 'push' | 'bidirectional';

/**
 * 任务状态
 */
export type TaskStatus = 'active' | 'pending' | 'paused' | 'failed';

/**
 * 同步任务
 */
export interface SyncTask {
  id: number;
  name: string;
  status: TaskStatus;
  tableName: string;
  direction: SyncDirection;
  syncTime: string;
  syncedCount: number;
  syncing?: boolean;
  isEnabled?: number;
  _toggling?: boolean;
  _rawData?: any;  // 保存完整原始数据
}

/**
 * 创建同步任务请求
 */
export interface CreateSyncTaskRequest {
  taskName: string;
  targetTable: string;
  syncDirection: SyncDirection;
  advancedFilter: boolean;
  smartMapping: boolean;
  mappingRules: string;
  updateDetection: 'hybrid' | 'webhook' | 'polling';
  incrementalSync: boolean;
  incrementalField: string;
  firstSync: string;
  recordConflict: string;
  fieldConflict: string;
  timeWindow: string;
  batchSize: number;
  batchInterval: number;
  fieldMappings: FieldMapping[];
}

/**
 * 字段映射
 */
export interface FieldMapping {
  sourceField: string;
  targetField: string;
  rule: string;
  ruleType: 'text' | 'link' | 'purple';
}

// ============ 同步日志相关 ============

/**
 * 同步状态
 */
export type SyncStatus = 'success' | 'failed' | 'processing';

/**
 * 同步模式
 */
export type SyncMode = 'webhook' | 'polling' | 'hybrid';

/**
 * 同步日志
 */
export interface SyncLog {
  id: number;
  bitableId: number;
  bitableName: string;
  syncMode: SyncMode;
  syncStatus: SyncStatus;
  recordCount: number;
  successCount: number;
  failedCount: number;
  startTime: string;
  endTime: string;
  duration: number;
  errorMessage?: string;
}

/**
 * 同步统计
 */
export interface SyncStats {
  successCount: number;
  failedCount: number;
  processingCount: number;
  totalCount: number;
}

/**
 * 日志查询参数
 */
export interface LogQueryParams {
  bitableId?: number;
  syncStatus?: SyncStatus;
  dateRange?: string[];
  pageNum: number;
  pageSize: number;
}

// ============ 多维表格相关 ============

/**
 * 多维表格配置 (后端返回)
 */
export interface BitableConfig {
  id?: number;
  feishuAppId: number;
  appToken: string;
  bitableId?: string;
  bitableName?: string;
  bitableUrl?: string;
  revision?: number;
  logo?: string;
  syncMode?: 'manual' | 'auto' | 'realtime';
  syncStatus?: 'pending' | 'processing' | 'success' | 'failed' | 'disabled';
  lastSyncTime?: string;
  isEnabled?: number;
  createTime?: string;
  updateTime?: string;
}

/**
 * 多维表格 (前端使用)
 */
export interface Bitable {
  id: number;
  bitableName: string;
  appToken: string;
  feishuAppId: number;
  syncStatus?: string;
  lastSyncTime?: string;
  isEnabled?: number;
}

/**
 * 数据表
 */
export interface DataTable {
  tableId: string;
  name: string;
  revision: number;
  fieldCount: number;
}

/**
 * 字段
 */
export interface Field {
  fieldId: string;
  fieldName: string;
  type: number;
  uiType: string;
  description?: string;
  isRequired?: boolean;
}

/**
 * 记录
 */
export interface Record {
  recordId: string;
  fields: Record<string, any>;
  createdTime?: number;
  lastModifiedTime?: number;
}

// ============ 飞书应用相关 ============

/**
 * 飞书应用配置
 */
export interface FeishuApp {
  id?: number;
  appName: string;
  appId: string;
  appSecret: string;
  companyName: string;
  appToken?: string;
  description?: string;
  isEnabled: number;
  feishuAppId?: string;
}

/**
 * 飞书应用配置表单
 */
export interface FeishuAppForm {
  id?: number;
  appName: string;
  appId: string;
  appSecret: string;
  companyName: string;
  appToken?: string;
  description?: string;
  isEnabled: number;
}

// ============ API 响应相关 ============

/**
 * API 响应
 */
export interface ApiResponse<T = any> {
  code: number;
  msg: string;
  data?: T;
}

/**
 * 分页响应
 */
export interface PageResponse<T = any> {
  list: T[];
  total: number;
  current: number;
  size: number;
  lastPageNo: number;
}

/**
 * 统一响应结果
 */
export interface Result<T = any> {
  responseStatus: number;
  code: number;
  success: boolean;
  errorMsg?: string;
  msg: string;
  data?: T;
}

// ============ 工具类型 ============

/**
 * 分页查询参数
 */
export interface PageParams {
  pageNum: number;
  pageSize: number;
}

/**
 * ID 参数
 */
export interface IdParam {
  id: number | string;
}

// ============ 常量 ============

/**
 * 同步模式选项
 */
export const SYNC_MODE_OPTIONS = [
  { label: '手动同步', value: 'manual' },
  { label: '自动同步', value: 'auto' },
  { label: '实时同步', value: 'realtime' }
] as const;

/**
 * 重试策略选项
 */
export const RETRY_STRATEGY_OPTIONS = [
  { label: '指数退避', value: 'exponential' },
  { label: '固定间隔', value: 'fixed' },
  { label: '线性递增', value: 'linear' }
] as const;

/**
 * 同步方向选项
 */
export const SYNC_DIRECTION_OPTIONS = [
  { label: '双向同步', value: 'bidirectional' },
  { label: '拉取数据', value: 'pull' },
  { label: '推送数据', value: 'push' }
] as const;

/**
 * 同步状态映射
 */
export const SYNC_STATUS_MAP: Record<string, string> = {
  pending: '待同步',
  processing: '同步中',
  success: '同步成功',
  failed: '同步失败',
  disabled: '已禁用'
} as const;

/**
 * 同步状态颜色
 */
export const SYNC_STATUS_COLOR: Record<string, string> = {
  pending: '#909399',
  processing: '#409eff',
  success: '#67c23a',
  failed: '#f56c6c',
  disabled: '#c0c4cc'
} as const;

/**
 * 任务状态映射
 */
export const TASK_STATUS_MAP: Record<string, string> = {
  active: '运行中',
  pending: '待运行',
  paused: '已暂停',
  failed: '运行失败'
} as const;
