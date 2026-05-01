import { $t } from "@/plugins/i18n";
import { appList } from "@/router/enums";

export default {
  path: "/appManage",
  redirect: "/appManage/list",
  meta: {
    icon: "ri/layout-grid-line",
    title: "插件管理",
    rank: appList,
    menuGroup: "DATA MANAGEMENT",
    roles: ["common", "admin"]
  },
  children: [
    {
      path: "/appManage/list",
      name: "AppManageList",
      component: () => import("@/views/appManage/list/index.vue"),
      meta: {
        icon: "ri/bank-card-line",
        title: "插件列表",
        showParent: true
      }
    },
    {
      path: "/appManage/detail",
      name: "AppManageDetail",
      component: () => import("@/views/appManage/detail/index.vue"),
      meta: {
        title: "插件详情",
        showLink: false
      }
    }
  ]
} satisfies RouteConfigsTable;
