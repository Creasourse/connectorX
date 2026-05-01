import { http } from "@/utils/http";

type Result = {
  responseStatus: number;
  code: number;
  success: boolean;
  msg: string;
  errorMsg?: string;
  data: Array<any>;
};

export const getAsyncRoutes = () => {
  return http.request<Result>("get", "/get-async-routes");
};
