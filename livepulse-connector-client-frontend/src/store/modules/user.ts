import { defineStore } from "pinia";
import {
  type userType,
  store,
  router,
  resetRouter,
  routerArrays,
  storageLocal
} from "../utils";
import {
  type UserResult,
  type BooleanResult,
  getLogin,
  registerUser,
  sendSms,
  checkSendSms,
  forgetPassword
} from "@/api/user";
import { useMultiTagsStoreHook } from "./multiTags";
import { type DataInfo, setToken, removeToken, userKey } from "@/utils/auth";

export const useUserStore = defineStore("pure-user", {
  state: (): userType => ({
    // 头像
    avatar: storageLocal().getItem<DataInfo<number>>(userKey)?.avatar ?? "",
    // 用户id
    userId: storageLocal().getItem<DataInfo<number>>(userKey)?.userId ?? null,
    // 用户名
    userName: storageLocal().getItem<DataInfo<number>>(userKey)?.userName ?? "",
    // 昵称
    nickName: storageLocal().getItem<DataInfo<number>>(userKey)?.nickName ?? "",
    // 电话
    phone: storageLocal().getItem<DataInfo<number>>(userKey)?.phone ?? "",
    // 邮箱
    email: storageLocal().getItem<DataInfo<number>>(userKey)?.email ?? "",
    // 角色类型
    type: storageLocal().getItem<DataInfo<number>>(userKey)?.type ?? null,
    // 页面级别权限
    roles: storageLocal().getItem<DataInfo<number>>(userKey)?.roles ?? [],
    // 按钮级别权限
    permissions:
      storageLocal().getItem<DataInfo<number>>(userKey)?.permissions ?? [],
    // 前端生成的验证码（按实际需求替换）
    verifyCode: "",
    // 判断登录页面显示哪个组件（0：登录（默认）、1：手机登录、2：二维码登录、3：注册、4：忘记密码）
    currentPage: 0,
    // 是否勾选了登录页的免登录
    isRemembered: false,
    // 登录页的免登录存储几天，默认7天
    loginDay: 7
  }),
  actions: {
    /** 存储头像 */
    SET_AVATAR(avatar: string) {
      this.avatar = avatar;
    },
    /** 存储用户名 */
    SET_USERID(userId: number) {
      this.userId = userId;
    },
    /** 存储用户名 */
    SET_USERNAME(userName: string) {
      this.userName = userName;
    },
    /** 存储昵称 */
    SET_NICKNAME(nickName: string) {
      this.nickName = nickName;
    },
    /** 存储电话 */
    SET_PHONE(phone: string) {
      this.phone = phone;
    },
    /** 存储邮箱 */
    SET_EMAIL(email: string) {
      this.email = email;
    },
    /** 存储角色 */
    SET_TYPE(type: number) {
      this.type = type;
    },
    /** 存储角色 */
    SET_ROLES(roles: Array<string>) {
      this.roles = roles;
    },
    /** 存储按钮级别权限 */
    SET_PERMS(permissions: Array<string>) {
      this.permissions = permissions;
    },
    /** 存储前端生成的验证码 */
    SET_VERIFYCODE(verifyCode: string) {
      this.verifyCode = verifyCode;
    },
    /** 存储登录页面显示哪个组件 */
    SET_CURRENTPAGE(value: number) {
      this.currentPage = value;
    },
    /** 存储是否勾选了登录页的免登录 */
    SET_ISREMEMBERED(bool: boolean) {
      this.isRemembered = bool;
    },
    /** 设置登录页的免登录存储几天 */
    SET_LOGINDAY(value: number) {
      this.loginDay = Number(value);
    },
    /** 登入 */
    async loginByUsername(data) {
      return new Promise<UserResult>((resolve, reject) => {
        getLogin(data)
          .then(res => {
            if (res.responseStatus === 200) {
              const {
                userId,
                userName,
                nickName,
                phone,
                type,
                token,
                expire,
                avatar
              } = res.data;
              const userData = {
                accessToken: token,
                expires: Date.now() + expire * 1000,
                avatar,
                userId,
                userName,
                nickName,
                phone,
                type,
                roles: [type === 3 ? "admin" : "common"],
                permissions: []
              };
              setToken(userData);
              resolve(res);
            } else {
              reject(res.msg);
            }
          })
          .catch(error => {
            reject(error);
          });
      });
    },
    /** 注册 */
    async register(data) {
      return new Promise<BooleanResult>((resolve, reject) => {
        registerUser(data)
          .then(res => {
            if (res.responseStatus === 200) {
              resolve(res);
            } else {
              reject(res.msg);
            }
          })
          .catch(error => {
            reject(error);
          });
      });
    },
    /** 获取验证码 */
    async getSmsByPhone(data) {
      return new Promise<BooleanResult>((resolve, reject) => {
        sendSms(data)
          .then(res => {
            if (res.responseStatus === 200) {
              resolve(res);
            } else {
              reject(res.msg);
            }
          })
          .catch(error => {
            reject(error);
          });
      });
    },
    /** 校验验证码 */
    async checkSmsForPhone(data) {
      return new Promise<BooleanResult>((resolve, reject) => {
        checkSendSms(data)
          .then(res => {
            if (res.responseStatus === 200) {
              resolve(res);
            } else {
              reject(res.msg);
            }
          })
          .catch(error => {
            reject(error);
          });
      });
    },
    /** 忘记密码 */
    async resetPassword(data) {
      return new Promise<BooleanResult>((resolve, reject) => {
        forgetPassword(data)
          .then(res => {
            if (res.responseStatus === 200) {
              resolve(res);
            } else {
              reject(res.msg);
            }
          })
          .catch(error => {
            reject(error);
          });
      });
    },

    /** 前端登出（不调用接口） */
    logOut() {
      this.userName = "";
      this.roles = [];
      this.permissions = [];
      removeToken();
      useMultiTagsStoreHook().handleTags("equal", [...routerArrays]);
      resetRouter();
      router.push("/login");
    }
    /** 刷新`token` */
    // async handRefreshToken(data) {
    //   return new Promise<RefreshTokenResult>((resolve, reject) => {
    //     refreshTokenApi(data)
    //       .then(res => {
    //         if (res.responseStatus === 200) {
    //           setToken(res.data);
    //           resolve(res);
    //         } else {
    //           reject(res.msg);
    //         }
    //       })
    //       .catch(error => {
    //         reject(error);
    //       });
    //   });
    // }
  }
});

export function useUserStoreHook() {
  return useUserStore(store);
}
