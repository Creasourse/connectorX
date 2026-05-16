import { http } from "@/utils/http";

export interface ConnectorItem {
  connectorId?: number;
  connectorName?: string;
  connPluginId?: number;
  connPluginName?: string;
  groupId?: number;
  groupName?: string;
  version?: string;
  locationUrl?: string;
  developUserId?: number;
  developUserName?: string;
  avatar?: string;
  type?: number;
  price?: number;
  staus?: number;
  titile?: string;
  createTime?: string;
  updateTime?: string;
  sketchOut?: string;
  remark?: string;
  del?: number;
  connLabelName?: string;
  icon?: string;
}
export interface AppItem {
  connectorId?: number;
  connectorName?: string;
  connPluginId?: number;
  connPluginName?: string;
  groupId?: number;
  groupName?: string;
  version?: string;
  titile?: string;
  locationUrl?: string;
  localConnectorId?: number;
  developUserId?: number;
  developUserName?: string;
  avatar?: string;
  type?: number;
  price?: number;
  staus?: number;
  createTime?: string;
  updateTime?: string;
  remark?: string;
  sketchOut?: string;
  del?: number;
  connLabelName?: string;
  icon?: string;
  dockerStatus?: string;
  serviceName?: string;
  loading?: boolean;
}
export interface VersionItem {
  connectorVersionId?: number;
  connPluginId?: number;
  connPluginName?: string;
  groupId?: number;
  groupName?: string;
  version?: string;
  locationUrl?: string;
  developUserId?: number;
  developUserName?: string;
  avatar?: string;
  type?: number;
  price?: number;
  staus?: number;
  createTime?: string;
  updateTime?: string;
  remark?: string;
  sketchOut?: string;
  del?: number;
  connLabelName?: string;
  icon?: string;
}

export interface LabelItem {
  connLabelEnumId?: number;
  connLabelName?: string;
}

export interface GroupItem {
  groupId?: number;
  groupName?: string;
}

type ResultTable = {
  responseStatus: number;
  code: number;
  success: boolean;
  errorMsg: string;
  msg: string;
  data?: {
    list: AppItem[];
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
  data?: any;
};

// connector
export const getConnectorList = (data?: object) =>
  http.request<ResultTable>("post", "/platform-api/connector/pageList", {
    data
  });
export const getConnectorVersionList = (data?: object) =>
  http.request<ResultTable>("post", "/platform-api/connector/pageVersionList", {
    data
  });

// 同步插件
export const syncConnector = (data?: object) =>
  http.request<ResultSingle>("post", "/api/local/connector/syncConnector", {
    data
  });
// app列表
export const getAppList = (data?: object) =>
  http.request<ResultTable>("post", "/api/local/connector/pageList", { data });
export const removeApp = (localConnectorId?: string | number) =>
  http.request<ResultSingle>(
    "get",
    `/api/local/connector/remove/${localConnectorId}`
  );
// 标签列表
export const getLabelEnum = (data?: object) =>
  http.request<ResultSingle>("post", "/platform-api/connPlugin/labelEnum", {
    data
  });
// 分类列表
export const getGroupEnum = (data?: object) =>
  http.request<ResultSingle>("post", "/platform-api/connPlugin/groupEnum", {
    data
  });
// app详情
export const getAppDetail = (id: number) =>
  http.request<ResultSingle>("post", `/platform-api/connPlugin/detail/${id}`);

// app容器启用、停用
export const startDockerCompose = (data?: object) =>
  http.request<ResultSingle>("post", "/api/api/docker-compose/start", {
    data
  });
export const stopDockerCompose = (data?: object) =>
  http.request<ResultSingle>("post", "/api/api/docker-compose/stop", {
    data
  });
export const removeDockerCompose = (data?: object) =>
  http.request<ResultSingle>("post", "/api/api/docker-compose/remove", {
    data
  });
export const getServiceName = (params?: object) =>
  http.request<ResultSingle>("get", "/api/api/docker-compose/serviceName", {
    params
  });
export const getDockerStatus = (params?: object) =>
  http.request<ResultSingle>("get", "/api/api/docker-compose/status", {
    params
  });
export const getComposeDir = (data?: object) =>
  http.request<ResultSingle>("get", "/api/api/docker-compose/composeDir", {
    data
  });
