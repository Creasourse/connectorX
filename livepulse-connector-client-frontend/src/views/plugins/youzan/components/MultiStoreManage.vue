<template>
  <div class="multi-store-card">
    <div class="card-header">
      <h2 class="card-title">多店铺管理</h2>
      <div class="header-actions">
        <el-button class="action-btn" @click="handleCancel">取消</el-button>
        <el-button type="primary" class="action-btn" @click="handleSave"
          >保存</el-button
        >
      </div>
    </div>

    <!-- 操作区域 -->
    <div class="action-bar">
      <div class="action-bar-left">
        <el-button type="primary" @click="handleAddStore">
          <el-icon><Plus /></el-icon>
          添加店铺
        </el-button>
        <el-button @click="handleBatchSync">
          <el-icon><Refresh /></el-icon>
          批量同步
        </el-button>
      </div>
      <div class="action-bar-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索店铺"
          clearable
          style="width: 240px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 统计信息 -->
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-label">店铺总数：</span>
        <span class="stat-value">{{ tableData.length }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">已连接：</span>
        <span class="stat-value success">{{ connectedCount }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">同步正常：</span>
        <span class="stat-value success">{{ normalSyncCount }}</span>
      </div>
    </div>

    <!-- 店铺列表表格 -->
    <div class="table-container">
      <el-table
        :data="filteredTableData"
        style="width: 100%"
        row-key="storeId"
        border
      >
        <el-table-column prop="shopName" label="店铺名称" min-width="200">
          <template #default="{ row }">
            <div class="shop-name-cell">
              <el-icon class="shop-icon" :color="row.iconColor">
                <Shop />
              </el-icon>
              <div class="shop-info">
                <span class="shop-name">{{ row.shopName }}</span>
                <span class="shop-type-tag">{{ getShopTypeText(row.shopType) }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="shopId" label="店铺ID" width="180">
          <template #default="{ row }">
            <span class="shop-id">{{ row.shopId }}</span>
          </template>
        </el-table-column>

        <el-table-column
          prop="appId"
          label="应用ID"
          min-width="200"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <span class="app-id">{{ row.appId }}</span>
          </template>
        </el-table-column>

        <el-table-column label="连接状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag
              :type="row.connectionStatus === 'connected' ? 'success' : 'danger'"
              size="small"
            >
              {{ row.connectionStatus === "connected" ? "已连接" : "未连接" }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="同步状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag
              :type="getSyncStatusType(row.syncStatus)"
              size="small"
            >
              {{ getSyncStatusText(row.syncStatus) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="lastSyncTime"
          label="最后同步时间"
          width="180"
          align="center"
        >
          <template #default="{ row }">
            <span class="sync-time">{{ row.lastSyncTime || "-" }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              size="small"
              @click="handleTestConnection(row)"
            >
              测试连接
            </el-button>
            <el-button
              link
              type="success"
              size="small"
              @click="handleSync(row)"
            >
              同步
            </el-button>
            <el-button
              link
              type="primary"
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              link
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty
        v-if="filteredTableData.length === 0"
        description="暂无店铺配置"
        :image-size="100"
      />
    </div>

    <!-- 添加/编辑店铺对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑店铺' : '添加店铺'"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="dialogFormRef"
        :model="dialogFormData"
        :rules="dialogFormRules"
        label-width="140px"
        label-position="left"
      >
        <el-form-item label="店铺名称" prop="shopName">
          <el-input
            v-model="dialogFormData.shopName"
            placeholder="请输入店铺名称，如：主店铺、测试店铺"
          />
        </el-form-item>

        <el-form-item label="店铺类型" prop="shopType">
          <el-select
            v-model="dialogFormData.shopType"
            placeholder="选择店铺类型"
            style="width: 100%"
          >
            <el-option label="有赞微商城" value="weimall" />
            <el-option label="有赞零售" value="retail" />
            <el-option label="有赞连锁" value="chain" />
            <el-option label="有赞外贸" value="trade" />
          </el-select>
        </el-form-item>

        <el-form-item label="店铺ID" prop="shopId">
          <el-input
            v-model="dialogFormData.shopId"
            placeholder="请输入有赞店铺ID"
          />
          <div class="form-tip">
            在有赞商家后台的店铺设置中可以找到店铺ID
          </div>
        </el-form-item>

        <el-divider content-position="left">认证配置</el-divider>

        <el-form-item label="应用ID" prop="appId">
          <el-input
            v-model="dialogFormData.appId"
            placeholder="请输入有赞开放平台应用ID"
          />
          <div class="form-tip">
            在有赞开放平台创建应用后获取
          </div>
        </el-form-item>

        <el-form-item label="应用密钥" prop="appSecret">
          <el-input
            v-model="dialogFormData.appSecret"
            type="password"
            placeholder="请输入有赞开放平台应用密钥"
            show-password
          />
          <div class="form-tip">
            在应用详情中获取，请妥善保管
          </div>
        </el-form-item>

        <el-form-item label="授权方式">
          <el-radio-group v-model="dialogFormData.authType">
            <el-radio label="token">令牌授权</el-radio>
            <el-radio label="oauth">OAuth授权</el-radio>
          </el-radio-group>
        </el-form-item>

        <template v-if="dialogFormData.authType === 'token'">
          <el-form-item label="访问令牌" prop="accessToken">
            <el-input
              v-model="dialogFormData.accessToken"
              type="password"
              placeholder="请输入访问令牌（Access Token）"
              show-password
            />
          </el-form-item>
        </template>

        <template v-if="dialogFormData.authType === 'oauth'">
          <el-form-item label="回调地址" prop="callbackUrl">
            <el-input
              v-model="dialogFormData.callbackUrl"
              placeholder="请输入OAuth回调地址"
            />
          </el-form-item>
          <el-form-item label="授权码">
            <el-input
              v-model="dialogFormData.authCode"
              placeholder="请输入授权码"
            />
          </el-form-item>
        </template>

        <el-divider content-position="left">同步配置</el-divider>

        <el-form-item label="启用自动同步">
          <el-switch v-model="dialogFormData.enableAutoSync" />
        </el-form-item>

        <el-form-item
          v-if="dialogFormData.enableAutoSync"
          label="同步间隔"
        >
          <el-select
            v-model="dialogFormData.syncInterval"
            placeholder="选择同步间隔"
            style="width: 100%"
          >
            <el-option label="每5分钟" value="5m" />
            <el-option label="每15分钟" value="15m" />
            <el-option label="每30分钟" value="30m" />
            <el-option label="每小时" value="1h" />
            <el-option label="每天" value="1d" />
          </el-select>
        </el-form-item>

        <el-form-item label="描述">
          <el-input
            v-model="dialogFormData.description"
            type="textarea"
            :rows="3"
            placeholder="店铺描述信息（可选）"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleDialogSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from "vue";
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from "element-plus";
import {
  Plus,
  Refresh,
  Search,
  Shop
} from "@element-plus/icons-vue";
import dayjs from "dayjs";

const props = defineProps({
  config: {
    type: Object as PropType<unknown>
  }
});

const emit = defineEmits<{
  (e: "update"): void;
}>();

const dialogFormRef = ref<FormInstance>();
const searchKeyword = ref("");
const dialogVisible = ref(false);
const isEdit = ref(false);

// 表格数据
const tableData = ref([
  {
    storeId: 1,
    shopName: "主店铺",
    shopId: "shop_abc123",
    shopType: "weimall",
    appId: "app_001",
    appSecret: "",
    authType: "token",
    accessToken: "",
    connectionStatus: "connected",
    syncStatus: "normal",
    lastSyncTime: "2024-01-15 14:30:25",
    enableAutoSync: true,
    syncInterval: "30m",
    iconColor: "#fa8c16",
    description: "主运营店铺"
  },
  {
    storeId: 2,
    shopName: "零售门店A",
    shopId: "shop_def456",
    shopType: "retail",
    appId: "app_002",
    appSecret: "",
    authType: "token",
    accessToken: "",
    connectionStatus: "connected",
    syncStatus: "normal",
    lastSyncTime: "2024-01-15 14:25:10",
    enableAutoSync: true,
    syncInterval: "1h",
    iconColor: "#52c41a",
    description: "线下零售门店"
  },
  {
    storeId: 3,
    shopName: "测试店铺",
    shopId: "shop_test789",
    shopType: "weimall",
    appId: "app_003",
    appSecret: "",
    authType: "oauth",
    accessToken: "",
    connectionStatus: "disconnected",
    syncStatus: "error",
    lastSyncTime: "2024-01-14 09:15:30",
    enableAutoSync: false,
    syncInterval: "1h",
    iconColor: "#1890ff",
    description: "测试环境店铺"
  }
]);

// 对话框表单数据
const dialogFormData = reactive({
  storeId: 0,
  shopName: "",
  shopType: "",
  shopId: "",
  appId: "",
  appSecret: "",
  authType: "token",
  accessToken: "",
  callbackUrl: "",
  authCode: "",
  enableAutoSync: false,
  syncInterval: "30m",
  description: ""
});

// 表单验证规则
const dialogFormRules: FormRules = {
  shopName: [{ required: true, message: "请输入店铺名称", trigger: "blur" }],
  shopType: [
    { required: true, message: "请选择店铺类型", trigger: "change" }
  ],
  shopId: [{ required: true, message: "请输入店铺ID", trigger: "blur" }],
  appId: [{ required: true, message: "请输入应用ID", trigger: "blur" }],
  appSecret: [
    { required: true, message: "请输入应用密钥", trigger: "blur" }
  ]
};

// 过滤后的表格数据
const filteredTableData = computed(() => {
  if (!searchKeyword.value) {
    return tableData.value;
  }
  return tableData.value.filter(
    item =>
      item.shopName.includes(searchKeyword.value) ||
      item.shopId.includes(searchKeyword.value)
  );
});

// 统计数据
const connectedCount = computed(() => {
  return tableData.value.filter(
    item => item.connectionStatus === "connected"
  ).length;
});

const normalSyncCount = computed(() => {
  return tableData.value.filter(item => item.syncStatus === "normal").length;
});

// 获取店铺类型文本
const getShopTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    weimall: "微商城",
    retail: "零售",
    chain: "连锁",
    trade: "外贸"
  };
  return textMap[type] || type;
};

// 获取同步状态类型
const getSyncStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    normal: "success",
    syncing: "warning",
    error: "danger",
    pending: "info"
  };
  return typeMap[status] || "info";
};

// 获取同步状态文本
const getSyncStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    normal: "正常",
    syncing: "同步中",
    error: "异常",
    pending: "等待中"
  };
  return textMap[status] || "未知";
};

// 获取图标颜色
const getIconColor = (type: string) => {
  const colorMap: Record<string, string> = {
    weimall: "#fa8c16",
    retail: "#52c41a",
    chain: "#1890ff",
    trade: "#eb2f96"
  };
  return colorMap[type] || "#666";
};

// 添加店铺
const handleAddStore = () => {
  isEdit.value = false;
  dialogFormData.storeId = 0;
  dialogFormData.shopName = "";
  dialogFormData.shopType = "";
  dialogFormData.shopId = "";
  dialogFormData.appId = "";
  dialogFormData.appSecret = "";
  dialogFormData.authType = "token";
  dialogFormData.accessToken = "";
  dialogFormData.callbackUrl = "";
  dialogFormData.authCode = "";
  dialogFormData.enableAutoSync = false;
  dialogFormData.syncInterval = "30m";
  dialogFormData.description = "";
  dialogVisible.value = true;
};

// 批量同步
const handleBatchSync = () => {
  ElMessage.info("批量同步功能开发中...");
};

// 测试连接
const handleTestConnection = (row: any) => {
  ElMessage.success(`店铺"${row.shopName}"连接测试成功`);
};

// 同步
const handleSync = (row: any) => {
  row.syncStatus = "syncing";
  ElMessage.info(`正在同步店铺"${row.shopName}"...`);

  // 模拟同步完成
  setTimeout(() => {
    row.syncStatus = "normal";
    row.lastSyncTime = dayjs().format("YYYY-MM-DD HH:mm:ss");
    ElMessage.success(`店铺"${row.shopName}"同步成功`);
  }, 2000);
};

// 编辑
const handleEdit = (row: any) => {
  isEdit.value = true;
  dialogFormData.storeId = row.storeId;
  dialogFormData.shopName = row.shopName;
  dialogFormData.shopType = row.shopType;
  dialogFormData.shopId = row.shopId;
  dialogFormData.appId = row.appId;
  dialogFormData.appSecret = row.appSecret;
  dialogFormData.authType = row.authType;
  dialogFormData.accessToken = row.accessToken;
  dialogFormData.enableAutoSync = row.enableAutoSync;
  dialogFormData.syncInterval = row.syncInterval;
  dialogFormData.description = row.description;
  dialogVisible.value = true;
};

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    `确定要删除店铺"${row.shopName}"吗？删除后将停止该店铺的数据同步。`,
    "提示",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    }
  )
    .then(() => {
      const index = tableData.value.findIndex(
        item => item.storeId === row.storeId
      );
      if (index !== -1) {
        tableData.value.splice(index, 1);
      }
      ElMessage.success("删除成功");
    })
    .catch(() => {});
};

// 对话框保存
const handleDialogSave = async () => {
  if (!dialogFormRef.value) return;

  try {
    await dialogFormRef.value.validate();

    const iconColor = getIconColor(dialogFormData.shopType);

    if (isEdit.value) {
      // 编辑
      const index = tableData.value.findIndex(
        item => item.storeId === dialogFormData.storeId
      );
      if (index !== -1) {
        tableData.value[index] = {
          ...tableData.value[index],
          shopName: dialogFormData.shopName,
          shopType: dialogFormData.shopType,
          shopId: dialogFormData.shopId,
          appId: dialogFormData.appId,
          appSecret: dialogFormData.appSecret,
          authType: dialogFormData.authType,
          accessToken: dialogFormData.accessToken,
          enableAutoSync: dialogFormData.enableAutoSync,
          syncInterval: dialogFormData.syncInterval,
          description: dialogFormData.description
        };
      }
      ElMessage.success("更新成功");
    } else {
      // 新增
      tableData.value.push({
        storeId: Date.now(),
        shopName: dialogFormData.shopName,
        shopType: dialogFormData.shopType,
        shopId: dialogFormData.shopId,
        appId: dialogFormData.appId,
        appSecret: dialogFormData.appSecret,
        authType: dialogFormData.authType,
        accessToken: dialogFormData.accessToken,
        connectionStatus: "disconnected",
        syncStatus: "pending",
        lastSyncTime: "",
        enableAutoSync: dialogFormData.enableAutoSync,
        syncInterval: dialogFormData.syncInterval,
        iconColor,
        description: dialogFormData.description
      });
      ElMessage.success("添加成功");
    }

    dialogVisible.value = false;
  } catch (error) {
    ElMessage.error("请完善必填项");
  }
};

// 对话框关闭
const handleDialogClose = () => {
  dialogFormRef.value?.resetFields();
};

// 取消
const handleCancel = () => {
  ElMessage.info("已取消修改");
};

// 保存
const handleSave = () => {
  // TODO: 调用保存接口
  ElMessage.success("保存成功");
  emit("update");
};
</script>

<style scoped lang="scss">
.multi-store-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .card-title {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }

    .header-actions {
      display: flex;
      gap: 12px;

      .action-btn {
        width: 80px;
      }
    }
  }

  .action-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .action-bar-left {
      display: flex;
      gap: 12px;
    }
  }

  .stats-bar {
    display: flex;
    gap: 32px;
    padding: 16px;
    background-color: #f6ffed;
    border: 1px solid #b7eb8f;
    border-radius: 4px;
    margin-bottom: 16px;

    .stat-item {
      display: flex;
      align-items: center;
      gap: 8px;

      .stat-label {
        font-size: 14px;
        color: #52c41a;
      }

      .stat-value {
        font-size: 18px;
        font-weight: 600;
        color: #52c41a;

        &.success {
          color: #1890ff;
        }
      }
    }
  }

  .table-container {
    :deep(.el-table) {
      font-size: 14px;

      .el-table__header {
        th {
          background-color: #fafafa;
          color: #666;
          font-weight: 500;
        }
      }

      .el-table__body {
        td {
          color: #333;
        }
      }
    }

    .shop-name-cell {
      display: flex;
      align-items: center;
      gap: 12px;

      .shop-icon {
        font-size: 20px;
      }

      .shop-info {
        display: flex;
        flex-direction: column;
        gap: 4px;

        .shop-name {
          font-weight: 500;
          color: #333;
        }

        .shop-type-tag {
          font-size: 12px;
          color: #999;
        }
      }
    }

    .shop-id,
    .app-id {
      font-family: "Courier New", monospace;
      color: #fa8c16;
      font-size: 13px;
    }

    .sync-time {
      font-size: 13px;
      color: #666;
    }
  }

  .form-tip {
    margin-top: 4px;
    font-size: 12px;
    color: #999;
  }
}
</style>
