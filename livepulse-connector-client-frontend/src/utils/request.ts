import { http } from "./http";

export interface RequestConfig {
  url: string;
  method: string;
  data?: any;
  params?: any;
  headers?: any;
}

/**
 * 封装请求方法，适配原有的 request 调用方式
 */
const request = (config: RequestConfig) => {
  const { url, method, data, params, headers } = config;

  return http.request(
    method as any,
    url,
    { data, params },
    { headers }
  );
};

export default request;