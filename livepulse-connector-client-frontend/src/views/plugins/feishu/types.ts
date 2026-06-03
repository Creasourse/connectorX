/**
 * 飞书插件类型定义
 */

/**
 * 同步日志
 */
export interface SyncLog {
  id: number;
  feishuDataSyncId: number;
  syncName?: string;
  bitableName?: string;
  tableName?: string;
  syncType?: string;
  syncDirection?: string;
  syncStatus: "success" | "failed" | "processing";
  startTime: string;
  endTime?: string;
  recordCount: number;
  successCount: number;
  failedCount: number;
  duration?: number;
  errorMessage?: string;
  retrying?: boolean;
}

/**
 * 同步统计数据
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
  syncStatus?: string;
  dateRange?: string[];
  pageNum: number;
  pageSize: number;
}

/**
 * 多维表格
 */
export interface Bitable {
  id: number;
  feishuAppId: number;
  appToken: string;
  bitableId?: string;
  bitableName?: string;
  bitableUrl?: string;
  revision?: number;
  logo?: string;
  syncMode?: string;
  syncStatus?: string;
  lastSyncTime?: string;
  isEnabled?: number;
}

/**
 * 数据表
 */
export interface DataTable {
  id: number;
  feishuBitableId: number;
  tableId: string;
  tableName: string;
  tableType?: number;
  revision?: number;
  syncMode?: string;
  syncStatus?: string;
  lastSyncTime?: string;
  isEnabled?: number;
}

/**
 * 字段信息
 */
export interface Field {
  id: string;
  fieldId: string;
  fieldName: string;
  fieldType: number;
  uiType?: number;
  property?: any;
  isRequired?: boolean;
  isUnique?: boolean;
  description?: string;
}

/**
 * 同步任务
 */
export interface SyncTask {
  id: number;
  name: string;
  tableName: string;
  direction: string;
  status: "active" | "pending" | "failed" | "paused";
  syncTime: string;
  syncedCount: number;
  syncing?: boolean;
  isEnabled: number;
  _toggling?: boolean;
  _rawData?: any;
}

/**
 * 字段映射
 */
export interface FieldMapping {
  sourceField: string;
  targetField: string;
  rule: string;
  ruleType: string;
}

/**
 * 同步状态映射表
 */
export const SYNC_STATUS_MAP: Record<string, string> = {
  success: "同步成功",
  failed: "同步失败",
  pending: "等待同步",
  running: "同步中",
  paused: "已暂停",
  disabled: "已停用"
};

/**
 * 同步模式映射表
 */
export const SYNC_MODE_MAP: Record<string, string> = {
  manual: "手动同步",
  scheduled: "定时同步",
  realtime: "实时同步"
};

/**
 * 同步方向映射表
 */
export const SYNC_DIRECTION_MAP: Record<string, string> = {
  pull: "单向拉取",
  push: "单向推送",
  bidirectional: "双向同步"
};

/**
 * 连接配置
 */
export interface ConnectionConfig {
  maxConnections: number;
  retryStrategy: "exponential" | "fixed" | "linear";
  maxRetries: number;
  dataFilter: string;
  dataRange: "all" | "custom";
  excludedFields: string[];
}

/**
 * 重试策略选项
 */
export const RETRY_STRATEGY_OPTIONS = [
  { label: "指数退避", value: "exponential" },
  { label: "固定间隔", value: "fixed" },
  { label: "线性递增", value: "linear" }
];
