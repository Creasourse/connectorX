import { http } from "@/utils/http";

export type UserResult = {
  responseStatus: number;
  code: number;
  success: boolean;
  msg: string;
  errorMsg?: string;
  data: {
    /** 头像 */
    avatar?: string;
    /** 用户ID */
    userId: number;
    /** 用户名 */
    userName: string;
    /** 昵称 */
    nickName: string;
    /** 电话 */
    phone?: string;
    /** 当前登录用户的角色 */
    type: number;
    /** 当前登录用户的角色 */
    roles?: Array<string>;
    /** 按钮级别权限 */
    permissions: Array<string>;
    /** `expire`的过期时间（格式'2400000'） */
    expire: number;
    /** `token` */
    token?: string;
    /** 用于调用刷新`accessToken`的接口时所需的`token` */
    refreshToken?: string;
    /** `accessToken`的过期时间（格式'xxxx/xx/xx xx:xx:xx'） */
    expires?: Date;
  };
};

export type BooleanResult = {
  responseStatus: number;
  code: number;
  success: boolean;
  msg: string;
  errorMsg?: string;
  data: boolean;
};

export type RefreshTokenResult = {
  responseStatus: number;
  code: number;
  success: boolean;
  msg: string;
  errorMsg?: string;
  data: {
    /** `token` */
    accessToken: string;
    /** 用于调用刷新`accessToken`的接口时所需的`token` */
    refreshToken: string;
    /** `accessToken`的过期时间（格式'xxxx/xx/xx xx:xx:xx'） */
    expires: Date;
  };
};

export type UserInfo = {
  /** 用户ID */
  userId?: number;
  /** 头像 */
  avatar: string;
  /** 用户名 */
  userName?: string;
  /** 昵称 */
  nickName?: string;
  /** 邮箱 */
  email?: string;
  /** 联系电话 */
  phone?: string;
  /** 简介 */
  description?: string;
  /** 角色列表 */
  roles?: Array<string>;
  type?: number;
  /** 注册时间 */
  createdAt?: string;
};

export type UserInfoResult = {
  responseStatus: number;
  code: number;
  success: boolean;
  msg: string;
  errorMsg: string;
  data: UserInfo;
};

type ResultTable = {
  responseStatus: number;
  code: number;
  success: boolean;
  msg: string;
  errorMsg?: string;
  data?: {
    /** 列表数据 */
    list: Array<any>;
    /** 总条目数 */
    total?: number;
    /** 每页显示条目个数 */
    pageSize?: number;
    /** 当前页数 */
    currentPage?: number;
  };
};

/** 登录 */
export const getLogin = (data?: object) => {
  return http.request<UserResult>("post", "/api/login/userLogin", { data });
};
/** 发送短信 */
export const sendSms = (data?: object) => {
  return http.request<BooleanResult>("post", "/api/login/sendSms", { data });
};
/** 验证短信 */
export const checkSendSms = (data?: object) => {
  return http.request<BooleanResult>("post", "/api/login/checkSendSms", {
    data
  });
};
/** 注册 */
export const registerUser = (data?: object) => {
  return http.request<BooleanResult>("post", "/api/register/save", { data });
};
/** 忘记密码 */
export const forgetPassword = (data?: object) => {
  return http.request<BooleanResult>("post", "/api/register/forgetPassword", {
    data
  });
};

/** 刷新`token` */
export const refreshTokenApi = (data?: object) => {
  return http.request<RefreshTokenResult>("post", "/api/refresh-token", {
    data
  });
};

/** 账户设置-个人信息 */
export const getMine = (data?: object) => {
  return http.request<UserInfoResult>("post", "/api/login/userInfo", { data });
};

/** 账户设置-个人安全日志 */
export const getMineLogs = (data?: object) => {
  return http.request<ResultTable>("get", "/api/mine-logs", { data });
};

/** 账户设置-更新个人信息 */
export const updateMine = (data?: object) => {
  return http.request<BooleanResult>("post", "/api/login/updateUserInfo", {
    data
  });
};

/** 账户设置-上传头像 */
export const uploadAvatar = (data?: FormData) => {
  return http.request<UserInfoResult>("post", "/api/login/avatar", {
    data,
    headers: { "Content-Type": "multipart/form-data" }
  });
};

/** 用户管理-获取用户列表 */
export const getUserList = (data?: object) => {
  return http.request<ResultTable>("post", "/api/login/userPageList", { data });
};

/** 用户管理-编辑用户信息 */
export const updateUser = (data?: object) => {
  return http.request<BooleanResult>("post", "/api/login/updateUserInfo", {
    data
  });
};

/** 用户管理-修改用户密码 */
export const updateUserPassword = (data?: object) => {
  return http.request<BooleanResult>("post", "/api/login/editPassword", {
    data
  });
};

/** 用户管理-分配角色 */
export const assignUserRole = (data?: object) => {
  return http.request<BooleanResult>("post", "/api/login/updateUserRole", {
    data
  });
};
