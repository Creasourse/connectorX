import { http } from "@/utils/http";

// ============ 环境配置 ============

/**
 * 检测是否为本地调试模式
 * 本地调试模式：开发环境且通过 localhost 或 127.0.0.1 访问
 */
const isLocalDev = (): boolean => {
  // 方法1: 检查环境变量
  if (import.meta.env.VITE_API_MODE === 'local') {
    return true;
  }

  // 方法2: 检查是否为开发环境
  if (import.meta.env.DEV) {
    const hostname = window.location.hostname;
    // 本地开发通常使用 localhost 或 127.0.0.1
    return hostname === 'localhost' || hostname === '127.0.0.1' || hostname === '';
  }

  return false;
};

/**
 * 构建 API 路径
 * 本地调试模式：保持原路径
 * 非本地模式：添加 /feishu 前缀
 */
const buildApiPath = (path: string): string => {

  // 本地调试模式：直接使用路径
  if (isLocalDev()) {
    return path.startsWith('/') ? `/feishu${path}` : `/${path}`;
  }

  // 非本地模式：添加 /feishu 前缀
  return `${path.startsWith('/') ? `/feishu/feishu${path}` : `/feishu/feishu/${path}`}`;
};

// ============ 类型定义 ============

/**
 * 飞书应用配置
 */
export interface FeishuAppConfig {
  id?: number;
  appName: string;
  appId: string;
  appSecret: string;
  companyName: string;
  appToken?: string;
  description?: string;
  isEnabled?: number;
  createTime?: string;
  updateTime?: string;
}

/**
 * 多维表格配置
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
  syncMode?: string;
  syncStatus?: string;
  lastSyncTime?: string;
  isEnabled?: number;
  createTime?: string;
  updateTime?: string;
}

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
 * 统一响应格式
 */
type Result<T = any> = {
  responseStatus: number;
  code: number;
  success: boolean;
  errorMsg?: string;
  msg: string;
  data?: T;
};

type ResultTable<T> = Result<{
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}>;

type ResultSingle = Result<any>;
type ResultBoolean = Result<boolean>;

// ============ 飞书字段映射 API ============

/**
 * 查询字段映射列表
 */
export const getFeishuFieldMappingList = (feishuBitableId: string | number) => {
  return http.request<ResultSingle>("get", buildApiPath(`/feishuFieldMapping/list/${feishuBitableId}`));
};

/**
 * 保存或更新字段映射
 */
export const saveOrUpdateFeishuFieldMapping = (data: any) => {
  return http.request<ResultSingle>("post", buildApiPath("/feishuFieldMapping/saveOrUpdate"), {
    data
  });
};

/**
 * 删除字段映射
 */
export const deleteFeishuFieldMapping = (id: string | number) => {
  return http.request<ResultSingle>("delete", buildApiPath(`/feishuFieldMapping/delete/${id}`));
};

/**
 * 批量删除字段映射
 */
export const batchDeleteFeishuFieldMapping = (ids: (string | number)[]) => {
  return http.request<ResultSingle>("delete", buildApiPath("/feishuFieldMapping/batchDelete"), {
    data: ids
  });
};

// ============ 飞书应用 API ============

/**
 * 分页查询飞书应用列表
 */
export const getFeishuAppPageList = (params: {
  pageNum: number;
  pageSize: number;
  appName?: string;
  appId?: string;
  companyName?: string;
  isEnabled?: number;
}) => {
  return http.request<ResultTable<FeishuAppConfig>>(
    "post",
    buildApiPath("/app/pageList"),
    { data: params }
  );
};

/**
 * 查询飞书应用详情
 */
export const getFeishuAppDetail = (id: string | number) => {
  return http.request<Result<FeishuAppConfig>>(
    "get",
    buildApiPath(`/app/detail/${id}`)
  );
};

/**
 * 保存或更新飞书应用
 */
export const saveOrUpdateFeishuApp = (data: Partial<FeishuAppConfig>) => {
  return http.request<Result<FeishuAppConfig>>(
    "post",
    buildApiPath("/app/saveOrUpdate"),
    { data }
  );
};

/**
 * 删除飞书应用
 */
export const deleteFeishuApp = (id: string | number) => {
  return http.request<ResultBoolean>("delete", buildApiPath(`/app/delete/${id}`));
};

/**
 * 测试飞书应用连接
 */
export const testFeishuAppConnection = (appId: string, appSecret: string) => {
  return http.request<ResultBoolean>("post", buildApiPath("/app/test"), {
    data: { appId, appSecret }
  });
};

/**
 * 切换飞书应用启用状态
 */
export const toggleFeishuAppEnabled = (id: string | number) => {
  return http.request<ResultBoolean>("post", buildApiPath(`/app/toggleEnabled/${id}`));
};

// ============ 多维表格 API ============

/**
 * 分页查询多维表格列表
 */
export const getFeishuBitablePageList = (params: {
  pageNum: number;
  pageSize: number;
  feishuAppId?: number;
  appToken?: string;
  bitableId?: string;
  bitableName?: string;
  syncMode?: string;
  syncStatus?: string;
  isEnabled?: number;
}) => {
  return http.request<ResultTable<BitableConfig>>(
    "post",
    buildApiPath("/bitable/pageList"),
    { data: params }
  );
};

/**
 * 查询多维表格详情
 */
export const getFeishuBitableDetail = (id: string | number) => {
  return http.request<Result<BitableConfig>>(
    "get",
    buildApiPath(`/bitable/detail/${id}`)
  );
};

/**
 * 保存或更新多维表格配置
 */
export const saveOrUpdateFeishuBitable = (data: Partial<BitableConfig>) => {
  return http.request<Result<BitableConfig>>(
    "post",
    buildApiPath("/bitable/saveOrUpdate"),
    { data }
  );
};

/**
 * 删除多维表格配置
 */
export const deleteFeishuBitable = (id: string | number) => {
  return http.request<ResultBoolean>("delete", buildApiPath(`/bitable/delete/${id}`));
};

/**
 * 同步多维表格数据
 * 设置较长超时时间（5分钟），因为需要同步所有数据表的字段数和记录数
 */
export const syncFeishuBitableData = (id: string | number) => {
  return http.request<ResultBoolean>(
    "post",
    buildApiPath(`/bitable/sync/${id}`),
    {}, // params
    { timeout: 300000 } // 5分钟超时
  );
};

/**
 * 切换多维表格启用状态
 */
export const toggleFeishuBitableEnabled = (id: string | number) => {
  return http.request<ResultBoolean>(
    "post",
    buildApiPath(`/bitable/toggleEnabled/${id}`)
  );
};

/**
 * 根据应用ID查询多维表格列表
 */
export const getFeishuBitableListByAppId = (feishuAppId: number) => {
  return http.request<Result<BitableConfig[]>>(
    "get",
    buildApiPath(`/bitable/listByFeishuAppId/${feishuAppId}`)
  );
};

// ============ 数据同步 API ============

/**
 * 飞书数据表 API
 */

/**
 * 分页查询飞书数据表列表
 */
export const getFeishuDataTablePageList = (params: {
  pageNum: number;
  pageSize: number;
  feishuBitableId?: number;
  tableName?: string;
  isEnabled?: number;
}) => {
  return http.request<ResultTable<any>>("post", buildApiPath("/feishuDataTable/pageList"), {
    data: params
  });
};

/**
 * 查询飞书数据表详情
 */
export const getFeishuDataTableDetail = (id: string | number) => {
  return http.request<ResultSingle>("get", buildApiPath(`/feishuDataTable/detail/${id}`));
};

/**
 * 同步数据表信息（字段数和记录数）
 */
export const syncFeishuDataTableInfo = (id: string | number) => {
  return http.request<ResultSingle>("post", buildApiPath(`/feishuDataTable/syncInfo/${id}`));
};

// ============ 飞书字段 API ============

/**
 * 分页查询飞书字段列表
 */
export const getFeishuFieldPageList = (params: {
  pageNum: number;
  pageSize: number;
  feishuDataTableId?: number;
  fieldName?: string;
  fieldType?: string;
}) => {
  return http.request<ResultTable<any>>("post", buildApiPath("/feishuField/pageList"), {
    data: params
  });
};

/**
 * 查询飞书字段详情
 */
export const getFeishuFieldDetail = (id: string | number) => {
  return http.request<ResultSingle>("get", buildApiPath(`/feishuField/detail/${id}`));
};

/**
 * 从飞书API实时查询字段列表
 */
export const fetchFieldsFromFeishu = (tableId: string) => {
  return http.request<ResultSingle>("get", buildApiPath("/feishuField/fetchFromFeishu"), {
    params: { tableId }
  });
};

/**
 * 批量保存字段信息
 */
export const batchSaveFeishuFields = (data: {
  feishuDataTableId: number;
  fields: Array<{
    fieldId: string;
    fieldName: string;
    fieldType: string;
    property?: string;
    isRequired?: number;
    isUnique?: number;
    description?: string;
    sortOrder?: number;
  }>;
}) => {
  return http.request<ResultSingle>("post", buildApiPath("/feishuField/batchSave"), {
    data
  });
};

// ============ 数据同步 API ============

/**
 * 分页查询数据同步任务列表
 */
export const getFeishuDataSyncPageList = (params: any) => {
  return http.request<ResultTable<any>>("post", buildApiPath("/feishuDataSync/pageList"), {
    data: params
  });
};

/**
 * 查询数据同步任务详情
 */
export const getFeishuDataSyncDetail = (id: string | number) => {
  return http.request<ResultSingle>("get", buildApiPath(`/feishuDataSync/detail/${id}`));
};

/**
 * 保存或更新数据同步任务
 */
export const saveOrUpdateFeishuDataSync = (data: any) => {
  return http.request<ResultSingle>("post", buildApiPath("/feishuDataSync/saveOrUpdate"), {
    data
  });
};

/**
 * 删除数据同步任务
 */
export const deleteFeishuDataSync = (id: string | number) => {
  return http.request<ResultSingle>("delete", buildApiPath(`/feishuDataSync/delete/${id}`));
};

/**
 * 立即执行同步
 */
export const executeFeishuDataSync = (id: string | number) => {
  return http.request<ResultSingle>("post", buildApiPath(`/feishuDataSync/sync/${id}`));
};

/**
 * 根据数据表ID立即同步
 */
export const executeFeishuDataSyncByTable = (feishuDataTableId: number) => {
  return http.request<ResultSingle>("post", buildApiPath("/feishuDataSync/syncByTable"), {
    params: { feishuDataTableId }
  });
};

/**
 * 切换数据同步任务启用状态
 */
export const toggleFeishuDataSyncEnabled = (id: string | number) => {
  return http.request<ResultSingle>("post", buildApiPath(`/feishuDataSync/toggleEnabled/${id}`));
};

// ============ 同步日志 API ============

/**
 * 分页查询同步日志列表
 */
export const getFeishuSyncLogPageList = (params: {
  pageNum: number;
  pageSize: number;
  feishuDataSyncId?: number;
  syncStatus?: string;
  syncType?: string;
  syncDirection?: string;
  startTime?: string;
  endTime?: string;
}) => {
  return http.request<ResultTable<any>>("post", buildApiPath("/feishuSyncLog/pageList"), {
    data: params
  });
};

/**
 * 查询同步日志详情
 */
export const getFeishuSyncLogDetail = (id: string | number) => {
  return http.request<ResultSingle>("get", buildApiPath(`/feishuSyncLog/detail/${id}`));
};

/**
 * 获取同步统计数据
 */
export const getFeishuSyncLogStatistics = (params?: {
  feishuDataSyncId?: number;
  startTime?: string;
  endTime?: string;
}) => {
  return http.request<ResultSingle>("post", buildApiPath("/feishuSyncLog/statistics"), {
    data: params || {}
  });
};

// 导出所有API
export default {
  // 飞书应用
  getFeishuAppPageList,
  getFeishuAppDetail,
  saveOrUpdateFeishuApp,
  deleteFeishuApp,
  testFeishuAppConnection,
  toggleFeishuAppEnabled,

  // 多维表格
  getFeishuBitablePageList,
  getFeishuBitableDetail,
  saveOrUpdateFeishuBitable,
  deleteFeishuBitable,
  syncFeishuBitableData,
  toggleFeishuBitableEnabled,
  getFeishuBitableListByAppId,

  // 字段映射
  getFeishuFieldMappingList,
  saveOrUpdateFeishuFieldMapping,
  deleteFeishuFieldMapping,
  batchDeleteFeishuFieldMapping,

  // 数据表
  getFeishuDataTablePageList,
  getFeishuDataTableDetail,
  syncFeishuDataTableInfo,

  // 字段
  getFeishuFieldPageList,
  getFeishuFieldDetail,
  fetchFieldsFromFeishu,
  batchSaveFeishuFields,

  // 数据同步
  getFeishuDataSyncPageList,
  getFeishuDataSyncDetail,
  saveOrUpdateFeishuDataSync,
  deleteFeishuDataSync,
  executeFeishuDataSync,
  executeFeishuDataSyncByTable,
  toggleFeishuDataSyncEnabled,

  // 同步日志
  getFeishuSyncLogPageList,
  getFeishuSyncLogDetail,
  getFeishuSyncLogStatistics
};

// ============ 使用说明 ============

/**
 * 环境检测说明：
 *
 * 本地调试模式判定规则（满足任一即视为本地模式）：
 * 1. 环境变量 VITE_API_MODE='local'
 * 2. 开发环境 且 hostname 为 localhost/127.0.0.1
 *
 * API 路径规则：
 * - 本地调试模式：保持原路径不变
 *   示例：/app/pageList -> /app/pageList
 *
 * - 非本地模式（生产/测试环境）：自动添加 /feishu 前缀
 *   示例：/app/pageList -> /feishu/app/pageList
 *
 * 配置方式：
 * 1. 在 .env.development 中添加：VITE_API_MODE=local
 * 2. 或者不配置，自动根据 hostname 判断
 *
 * 示例配置：
 * # .env.development
 * VITE_API_MODE=local  # 强制使用本地模式（不添加前缀）
 *
 * # .env.production
 * # 不需要配置，自动使用 /feishu 前缀
 */
