/**
 * 企微插件
 */
import { http } from "@/utils/http";

type ResultTable = {
  responseStatus: number;
  code: number;
  success: boolean;
  errorMsg: string;
  msg: string;
  data?: {
    list: any[];
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
  msg: string;
  errorMsg: string;
  data?: any;
};

// 账户管理
export const saveOrUpdateCorp = (data?: object) =>
  http.request<ResultSingle>("post", "/wecom-open/wecomCorp/saveOrUpdate", {
    data
  });
export const getWecomCorp = (data?: object) =>
  http.request<ResultTable>("post", "/wecom-open/wecomCorp/pageList", {
    data
  });
export const getWecomCorpDetail = (wecomeCorpId?: number | string) =>
  http.request<ResultSingle>(
    "get",
    `/wecom-open/wecomCorp/detail/${wecomeCorpId}`
  );

// 接口同步
export const saveOrUpdateMetaTable = (data?: object) =>
  http.request<ResultSingle>(
    "post",
    `/wecom-open/metaTable/saveOrUpdate`,
    data
  );
export const getMetaTableList = (data?: object) =>
  http.request<ResultTable>("post", `/wecom-open/metaTable/pageList`, { data });
export const getColumnList = (tableId?: number | string) =>
  http.request<ResultSingle>("get", `/wecom-open/metaTable/columns/${tableId}`);

// 事件订阅
export const saveOrUpdateConfig = (data?: object) =>
  http.request<ResultSingle>(
    "post",
    "/wecom-open/wecomCallbackConfig/saveOrUpdate",
    {
      data
    }
  );
export const getCallbackPageList = (data?: object) =>
  http.request<ResultSingle>(
    "post",
    "/wecom-open/wecomCallbackConfig/pageList",
    {
      data
    }
  );
export const getCallbackDetail = (id?: number | string) =>
  http.request<ResultSingle>(
    "get",
    `/wecom-open/wecomCallbackConfig/detail/${id}`
  );

// 事件回调记录
export const getEventLogList = (data?: object) =>
  http.request<ResultTable>("post", "/wecom-open/wecomSync/log/pageList", {
    data
  });

// 接口回调记录
export const getInterfaceLogList = (data?: object) =>
  http.request<ResultTable>("post", "/wecom-open/wecomCallback/log/pageList", {
    data
  });
