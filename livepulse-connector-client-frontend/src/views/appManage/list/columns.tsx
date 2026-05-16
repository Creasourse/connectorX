import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useDebounceFn } from "@vueuse/core";
import {
  type AppItem,
  syncConnector,
  getAppList,
  removeApp,
  getComposeDir,
  getServiceName,
  startDockerCompose,
  stopDockerCompose,
  getDockerStatus
} from "@/api/app";
import { getUrl } from "@/utils/format";
import dayjs from "dayjs";
import { ElMessage, ElMessageBox } from "element-plus";

const STATUS_MAP: Record<number, { label: string; dotColor: string }> = {
  0: { label: "未上架", dotColor: "#c0c4cc" },
  1: { label: "已上架", dotColor: "#67c23a" },
  2: { label: "已下架", dotColor: "#c0c4cc" },
  3: { label: "审核中", dotColor: "#e6a23c" },
  5: { label: "驳回", dotColor: "#f56c6c" }
};

const ICON_COLORS = [
  { bg: "#dbeafe", color: "#3b82f6" },
  { bg: "#ffedd5", color: "#f97316" },
  { bg: "#ede9fe", color: "#7c3aed" },
  { bg: "#dcfce7", color: "#16a34a" },
  { bg: "#fee2e2", color: "#dc2626" },
  { bg: "#fce7f3", color: "#db2777" },
  { bg: "#fef9c3", color: "#ca8a04" },
  { bg: "#f1f5f9", color: "#475569" }
];

export function useColumns() {
  const router = useRouter();
  const loading = ref(false);
  const searchKeyword = ref("");
  const statusFilter = ref(undefined);
  const typeFilter = ref(undefined);
  const sortType = ref(2);
  const currentPage = ref(1);
  const pageSize = ref(20);
  const total = ref(0);
  const pagedData = ref<AppItem[]>([]);

  const handleDetail = (row: AppItem) => {
    sessionStorage.setItem("plugin-detail", JSON.stringify(row));
    router.push(`/appManage/detail?id=${row.connectorId}`);
  };

  const columns: TableColumnList = [
    {
      label: "应用信息",
      prop: "connectorName",
      minWidth: 180,
      cellRenderer: ({ row, $index }) => {
        const colorStyle = ICON_COLORS[$index % ICON_COLORS.length];
        const firstChar = row.connectorName?.[0] || "A";
        return (
          <div
            style={{
              display: "flex",
              alignItems: "center",
              gap: "12px"
            }}
          >
            {row.icon ? (
              <img
                src={getUrl(row.icon)}
                style={{
                  width: "36px",
                  height: "36px",
                  borderRadius: "8px",
                  objectFit: "cover",
                  flexShrink: "0"
                }}
              />
            ) : (
              <div
                style={{
                  width: "36px",
                  height: "36px",
                  borderRadius: "8px",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  fontSize: "14px",
                  fontWeight: "600",
                  flexShrink: "0",
                  background: colorStyle.bg,
                  color: colorStyle.color
                }}
              >
                {firstChar}
              </div>
            )}
            <span style={{ fontSize: "14px" }}>{row.connectorName}</span>
          </div>
        );
      }
    },
    {
      label: "当前版本",
      prop: "version",
      width: 110,
      align: "center"
    },
    {
      label: "应用 ID",
      prop: "connPluginId",
      minWidth: 160
    },
    {
      label: "类型",
      prop: "groupName",
      width: 90,
      align: "center",
      cellRenderer: ({ row }) => (
        <span
          style={{
            color: row.groupName === "主题" ? "#8b5cf6" : "#409eff",
            fontSize: "14px",
            cursor: "pointer"
          }}
        >
          {row.groupName || "插件"}
        </span>
      )
    },
    {
      label: "状态",
      prop: "status",
      width: 110,
      cellRenderer: ({ row }) => {
        const conf = STATUS_MAP[row.staus] ?? STATUS_MAP[0];
        return (
          <div
            style={{
              display: "flex",
              alignItems: "center",
              gap: "6px"
            }}
          >
            <span
              style={{
                width: "8px",
                height: "8px",
                borderRadius: "50%",
                flexShrink: "0",
                background: conf.dotColor,
                display: "inline-block"
              }}
            />
            <span style={{ fontSize: "14px" }}>{conf.label}</span>
          </div>
        );
      }
    },
    {
      label: "更新时间",
      prop: "updateTime",
      minWidth: 170,
      cellRenderer: ({ row }) => (
        <span
          style={{
            fontSize: "13px",
            color: "var(--el-text-color-secondary)"
          }}
        >
          {dayjs(row.updateTime).format("YYYY-MM-DD HH:mm:ss")}
        </span>
      )
    },
    {
      label: "操作",
      width: 80,
      align: "center",
      fixed: "right",
      cellRenderer: ({ row }) => (
        <el-dropdown trigger="click" placement="bottom-end">
          {{
            default: () => (
              <div class="op-btn">
                <iconify-icon-online
                  icon="ri:more-2-fill"
                  style={{
                    fontSize: "18px",
                    color: "var(--el-text-color-secondary)"
                  }}
                />
              </div>
            ),
            dropdown: () => (
              <el-dropdown-menu>
                <el-dropdown-item onClick={() => handleDetail(row)}>
                  详情
                </el-dropdown-item>
              </el-dropdown-menu>
            )
          }}
        </el-dropdown>
      )
    }
  ];

  const getList = () => {
    loading.value = true;
    const params = {
      currentPage: currentPage.value,
      pageSize: pageSize.value,
      connectorName: searchKeyword.value,
      sortType: sortType.value
    };
    getAppList(params)
      .then(res => {
        if (res.success) {
          pagedData.value = res.data.list;
          total.value = res.data.total;
          pagedData.value.forEach(async app => {
            await getServiceNameFn(app);
            await getDockerStatusFn(app);
          });
        }
      })
      .finally(() => {
        loading.value = false;
      });
  };

  const handleSearch = useDebounceFn(() => {
    currentPage.value = 1;
    getList();
  }, 300);

  const toMarket = () => {
    window.open("http://connectorx.livepulse.com.cn/", "_blank");
  };

  const onSizeChange = (size: number) => {
    pageSize.value = size;
    currentPage.value = 1;
    getList();
  };

  const onCurrentChange = (page: number) => {
    currentPage.value = page;
    getList();
  };

  const composeDir = ref("");
  const getComposeDirFn = () => {
    getComposeDir().then(res => {
      if (res.success) {
        composeDir.value = res.data;
      }
    });
  };

  onMounted(() => {
    syncConnector();
    getComposeDirFn();
  });

  const getServiceNameFn = async (app: AppItem) => {
    await getServiceName({ connectorName: app.connectorName }).then(res => {
      if (res.success) {
        app.serviceName = res.data;
      }
    });
  };
  const getDockerStatusFn = async (app: AppItem) => {
    const params = {
      composeDir: composeDir.value,
      projectName: app.connectorName,
      serviceName: app.serviceName
    };
    await getDockerStatus(params).then(res => {
      if (res.success) {
        app.dockerStatus = res.data?.[0]?.status || "";
      }
    });
  };

  const dockerStatus = ref("");
  const statusMap = {
    created: "容器已被创建，但尚未启动",
    restarting: "容器正在重启中",
    running: "容器正在运行",
    removing: "容器正在被删除",
    paused: "容器已被暂停",
    exited: "容器已停止运行",
    dead: "容器死亡"
  };

  const changeStatus = async (app: AppItem) => {
    app.loading = true;
    const params = {
      composeDir: composeDir.value,
      serviceName: app.serviceName
    };
    try {
      switch (app.dockerStatus) {
        case "running":
          await stopDockerCompose(params);
          getList();
          break;
        case "paused":
        case "exited":
          await startDockerCompose(params);
          getList();
          break;

        default:
          break;
      }
    } catch (error) {}
    app.loading = false;
  };

  const handleRemoveApp = (app: AppItem) => {
    ElMessageBox.confirm(
      `确认卸载「${app.connectorName}」？卸载后需重新安装。`,
      "卸载确认",
      {
        confirmButtonText: "确认卸载",
        cancelButtonText: "取消",
        type: "warning"
      }
    )
      .then(() => {
        removeApp(app.localConnectorId).then(res => {
          if (res.success) {
            ElMessage.success("卸载成功");
            getList();
          }
        });
      })
      .catch(() => {
        // 取消卸载
      });
  };

  return {
    loading,
    searchKeyword,
    statusFilter,
    typeFilter,
    sortType,
    statusMap,
    currentPage,
    pageSize,
    total,
    columns,
    pagedData,
    handleSearch,
    handleDetail,
    handleRemoveApp,
    changeStatus,
    toMarket,
    onSizeChange,
    onCurrentChange
  };
}
