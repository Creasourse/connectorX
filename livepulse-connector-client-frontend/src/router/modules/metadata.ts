import { $t } from "@/plugins/i18n";
import { metadata } from "@/router/enums";

export default {
  path: "/metadata",
  redirect: "/metadata/list",
  meta: {
    icon: "ri/database-2-line",
    title: "元数据管理",
    rank: metadata,
    roles: ["common", "admin"]
  },
  children: [
    {
      path: "/metadata/list",
      name: "MetadataList",
      component: () => import("@/views/metadata/index.vue"),
      meta: {
        icon: "ri/list-check",
        title: "元数据列表"
      }
    },
    {
      path: "/metadata/detail",
      name: "MetadataDetail",
      component: () => import("@/views/metadata/detail.vue"),
      meta: {
        title: "元数据详情",
        showLink: false
      }
    }
  ]
} satisfies RouteConfigsTable;
