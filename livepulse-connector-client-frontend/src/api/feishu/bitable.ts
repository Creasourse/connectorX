import { http } from "@/utils/http";

// ============ 类型定义 ============

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
  syncMode?: "manual" | "auto" | "realtime";
  syncStatus?: "pending" | "processing" | "success" | "failed" | "disabled";
  lastSyncTime?: string;
  isEnabled?: boolean;
  createTime?: string;
  updateTime?: string;
}

/**
 * 分页查询参数
 */
export interface BitablePageParam {
  pageNum: number;
  pageSize: number;
  feishuAppId?: number;
  appToken?: string;
  bitableId?: string;
  bitableName?: string;
  syncMode?: string;
  syncStatus?: string;
  isEnabled?: boolean;
}

/**
 * 分页响应
 */
export interface PageResponse<T> {
  list: T[];
  total: number;
  current: number;
  size: number;
  lastPageNo: number;
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

type ResultTable = Result<{
  list: BitableConfig[];
  total: number;
  current: number;
  size: number;
  lastPageNo: number;
}>;

type ResultSingle = Result<BitableConfig>;

type ResultBoolean = Result<boolean>;

// ============ 多维表格管理 API ============

/**
 * 分页查询多维表格列表
 */
export const getBitablePageList = (params: BitablePageParam) => {
  return http.request<ResultTable>("post", "/feishu/bitable/pageList", {
    data: params
  });
};

/**
 * 根据应用ID查询多维表格列表
 */
export const getBitableListByAppId = (feishuAppId: number) => {
  return http.request<Result<BitableConfig[]>>(
    "get",
    `/feishu/bitable/listByFeishuAppId/${feishuAppId}`
  );
};

/**
 * 根据app_token查询多维表格
 */
export const getBitableByToken = (feishuAppId: number, appToken: string) => {
  return http.request<ResultSingle>("get", "/feishu/feishu/bitable/getByAppToken", {
    params: { feishuAppId, appToken }
  });
};

/**
 * 查询多维表格详情
 */
export const getBitableDetail = (id: number | string) => {
  return http.request<ResultSingle>("get", `/feishu/feishu/bitable/detail/${id}`);
};

/**
 * 保存或更新多维表格配置
 */
export const saveOrUpdateBitable = (data: Partial<BitableConfig>) => {
  return http.request<ResultSingle>("post", "/feishu/bitable/saveOrUpdate", {
    data
  });
};

/**
 * 删除多维表格配置
 */
export const deleteBitable = (id: number | string) => {
  return http.request<ResultSingle>("delete", `/feishu/feishu/bitable/delete/${id}`);
};

/**
 * 同步多维表格数据
 */
export const syncBitableData = (id: number | string) => {
  return http.request<ResultBoolean>("post", `/feishu/feishu/bitable/sync/${id}`);
};

/**
 * 切换多维表格启用状态
 */
export const toggleBitableEnabled = (id: number | string) => {
  return http.request<ResultBoolean>("post", `/feishu/feishu/bitable/toggleEnabled/${id}`);
};

export default {
  // 查询
  getBitablePageList,
  getBitableListByAppId,
  getBitableByToken,
  getBitableDetail,

  // 操作
  saveOrUpdateBitable,
  deleteBitable,
  syncBitableData,
  toggleBitableEnabled
};
