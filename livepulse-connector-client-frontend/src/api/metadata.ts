import { http } from "@/utils/http";

export interface MetadataItem {
  tableId?: number;
  tableName: string;
  tableAlias: string;
  type: string;
  comment: string;
  source: number;
  createTime: string;
  updateTime?: string;
}
export interface ColumnItem {
  tableColumnId: number;
  tableId: number;
  tableColumnName: string;
  tableColumnAlias: string;
  comment: string;
  isPk: number;
  dataType: number;
  shortDataType: number;
  dataTypeName: string;
}

type ResultTable = {
  responseStatus: number;
  code: number;
  success: boolean;
  errorMsg?: string;
  msg: string;
  data?: {
    list: MetadataItem[];
    total: number;
    current: number;
    size: number;
    lastPageNo: number;
  };
};
type ResultColumnTable = {
  responseStatus: number;
  code: number;
  success: boolean;
  errorMsg?: string;
  msg: string;
  data?: {
    list: ColumnItem[];
    total: number;
    current: number;
    size: number;
    lastPageNo: number;
  };
};

type ResultSingle = {
  responseStatus: number;
  code: number;
  success: boolean;
  errorMsg?: string;
  msg: string;
  data?: any;
};

/**
 * 获取元数据列表
 */
export const getMetadataList = (data?: object) => {
  return http.request<ResultTable>("post", "/api/metaTable/pageList", {
    data
  });
};

/**
 * 新增/编辑元数据
 */
export const saveOrUpdateMetadata = (data?: object) => {
  return http.request<ResultSingle>("post", "/api/metaTable/saveOrUpdate", {
    data
  });
};

/**
 * 删除元数据
 */
export const deleteMetadata = (tableId?: number | string) => {
  return http.request<ResultSingle>(
    "delete",
    `/api/metaTable/delete/${tableId}`
  );
};

/**
 * 获取元数据详情
 */
export const getMetadataDetail = (tableId: number) => {
  return http.request<ResultSingle>("get", `/api/metaTable/detail/${tableId}`);
};
/**
 * 获取元数据列信息
 */
export const getMetadataColumns = (tableId: number) => {
  return http.request<ResultSingle>("get", `/api/metaTable/columns/${tableId}`);
};

/**
 * 列管理
 */
export const getMetaTableColumnList = (data: object) => {
  return http.request<ResultColumnTable>(
    "get",
    "/api/metaTableColumn/pageList",
    { data }
  );
};

/**
 * 新增/编辑列
 */
export const saveOrUpdateMetaTableColumn = (data?: object) => {
  return http.request<ResultSingle>(
    "post",
    "/api/metaTableColumn/saveOrUpdate",
    {
      data
    }
  );
};
/**
 * 列详情
 */
export const getMetaTableColumn = (tableColumnId?: number | string) => {
  return http.request<ResultSingle>(
    "get",
    `/api/metaTableColumn/detail/${tableColumnId}`
  );
};

/**
 * 删除列
 */
export const deleteMetaTableColumn = (tableColumnId?: number | string) => {
  return http.request<ResultSingle>(
    "delete",
    `/api/metaTableColumn/delete/${tableColumnId}`
  );
};
