import { $t } from "@/plugins/i18n";
const Layout = () => import("@/layout/index.vue");
const AppLayout = () => import("@/layout/AppLayout.vue");

export default [
  // ── 应用市场（无需登录，独立顶栏，无侧边栏） ──
  {
    path: "/apps",
    component: AppLayout,
    redirect: "/apps/index",
    meta: { title: "", showLink: false },
    children: [
      {
        path: "/apps/index",
        name: "AppList",
        component: () => import("@/views/app/list/index.vue"),
        meta: { title: $t("menus.appMarket"), showLink: false }
      },
      {
        path: "/apps/:id",
        name: "AppDetail",
        component: () => import("@/views/app/detail/index.vue"),
        meta: { title: $t("menus.pureAppDetail"), showLink: false }
      }
    ]
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/login/index.vue"),
    meta: {
      title: $t("menus.pureLogin"),
      showLink: false
    }
  },
  {
    path: "/access-denied",
    name: "AccessDenied",
    component: () => import("@/views/error/403.vue"),
    meta: {
      title: $t("menus.pureAccessDenied"),
      showLink: false
    }
  },
  {
    path: "/server-error",
    name: "ServerError",
    component: () => import("@/views/error/500.vue"),
    meta: {
      title: $t("menus.pureServerError"),
      showLink: false
    }
  },
  {
    path: "/redirect",
    component: Layout,
    meta: {
      title: $t("status.pureLoad"),
      showLink: false
    },
    children: [
      {
        path: "/redirect/:path(.*)",
        name: "Redirect",
        component: () => import("@/layout/redirect.vue")
      }
    ]
  },
  {
    path: "/account-settings",
    name: "AccountSettings",
    component: () => import("@/views/account-settings/index.vue"),
    meta: {
      title: $t("buttons.pureAccountSettings"),
      showLink: false
    }
  },
  {
    path: "/empty",
    name: "Empty",
    component: () => import("@/views/empty/index.vue"),
    meta: {
      title: $t("menus.pureEmpty"),
      showLink: false
    }
  }
] satisfies Array<RouteConfigsTable>;
