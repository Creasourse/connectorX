<template>
  <div class="multi-org-card">
    <div class="card-header">
      <h2 class="card-title">多组织管理</h2>
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
        <el-button type="primary" @click="handleAddOrg">
          <el-icon><Plus /></el-icon>
          添加组织
        </el-button>
        <el-button @click="handleBatchSync">
          <el-icon><Refresh /></el-icon>
          批量同步
        </el-button>
      </div>
      <div class="action-bar-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索组织"
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
        <span class="stat-label">组织总数：</span>
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

    <!-- 组织列表表格 -->
    <div class="table-container">
      <el-table
        :data="filteredTableData"
        style="width: 100%"
        row-key="orgId"
        border
      >
        <el-table-column prop="orgName" label="组织名称" min-width="180">
          <template #default="{ row }">
            <div class="org-name-cell">
              <el-icon class="org-icon"><OfficeBuilding /></el-icon>
              <span class="org-name">{{ row.orgName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="instanceUrl" label="实例URL" min-width="250">
          <template #default="{ row }">
            <div class="url-cell">
              <el-link :href="row.instanceUrl" target="_blank" type="primary">
                {{ row.instanceUrl }}
              </el-link>
            </div>
          </template>
        </el-table-column>

        <el-table-column
          prop="apiVersion"
          label="API版本"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <el-tag size="small" type="info">v{{ row.apiVersion }}</el-tag>
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

        <el-table-column label="操作" width="200" align="center" fixed="right">
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
              type="primary"
              size="small"
              @click="handleSync(row)"
            >
              同步
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
        description="暂无组织配置"
        :image-size="100"
      />
    </div>

    <!-- 添加/编辑组织对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑组织' : '添加组织'"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="dialogFormRef"
        :model="dialogFormData"
        :rules="dialogFormRules"
        label-width="140px"
        label-position="left"
      >
        <el-form-item label="组织名称" prop="orgName">
          <el-input
            v-model="dialogFormData.orgName"
            placeholder="请输入组织名称，如：生产环境、测试环境"
          />
        </el-form-item>

        <el-form-item label="实例URL" prop="instanceUrl">
          <el-input
            v-model="dialogFormData.instanceUrl"
            placeholder="请输入Salesforce实例URL，如：https://yourinstance.my.salesforce.com"
          >
            <template #prepend>https://</template>
          </el-input>
        </el-form-item>

        <el-form-item label="API版本" prop="apiVersion">
          <el-select
            v-model="dialogFormData.apiVersion"
            placeholder="选择API版本"
            style="width: 100%"
          >
            <el-option label="v61.0 (Summer '24)" value="61.0" />
            <el-option label="v60.0 (Spring '24)" value="60.0" />
            <el-option label="v59.0 (Winter '24)" value="59.0" />
            <el-option label="v58.0 (Summer '23)" value="58.0" />
            <el-option label="v57.0 (Spring '23)" value="57.0" />
          </el-select>
        </el-form-item>

        <el-divider content-position="left">认证信息</el-divider>

        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="dialogFormData.username"
            placeholder="请输入Salesforce用户名"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="dialogFormData.password"
            type="password"
            placeholder="请输入Salesforce密码 + 安全令牌"
            show-password
          />
        </el-form-item>

        <el-form-item label="OAuth方式">
          <el-switch v-model="dialogFormData.useOAuth" />
          <span class="form-tip">启用后使用OAuth 2.0认证方式</span>
        </el-form-item>

        <template v-if="dialogFormData.useOAuth">
          <el-form-item label="客户端ID" prop="clientId">
            <el-input
              v-model="dialogFormData.clientId"
              placeholder="请输入OAuth客户端ID"
            />
          </el-form-item>

          <el-form-item label="客户端密钥" prop="clientSecret">
            <el-input
              v-model="dialogFormData.clientSecret"
              type="password"
              placeholder="请输入OAuth客户端密钥"
              show-password
            />
          </el-form-item>

          <el-form-item label="回调URL" prop="callbackUrl">
            <el-input
              v-model="dialogFormData.callbackUrl"
              placeholder="请输入OAuth回调URL"
            />
          </el-form-item>
        </template>

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
            placeholder="组织描述信息（可选）"
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
import { ref, reactive, computed, onMounted } from "vue";
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from "element-plus";
import {
  Plus,
  Refresh,
  Search,
  OfficeBuilding
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
    orgId: 1,
    orgName: "生产环境",
    instanceUrl: "https://na1.salesforce.com",
    apiVersion: "61.0",
    connectionStatus: "connected",
    syncStatus: "normal",
    lastSyncTime: "2024-01-15 14:30:25",
    username: "admin@company.com",
    useOAuth: false,
    enableAutoSync: true,
    syncInterval: "30m",
    description: "生产环境主组织"
  },
  {
    orgId: 2,
    orgName: "测试环境",
    instanceUrl: "https://test.salesforce.com",
    apiVersion: "61.0",
    connectionStatus: "connected",
    syncStatus: "normal",
    lastSyncTime: "2024-01-15 14:25:10",
    username: "test@company.com",
    useOAuth: false,
    enableAutoSync: true,
    syncInterval: "1h",
    description: "测试环境组织"
  },
  {
    orgId: 3,
    orgName: "开发环境",
    instanceUrl: "https://dev.salesforce.com",
    apiVersion: "60.0",
    connectionStatus: "disconnected",
    syncStatus: "error",
    lastSyncTime: "2024-01-14 09:15:30",
    username: "dev@company.com",
    useOAuth: true,
    enableAutoSync: false,
    syncInterval: "1h",
    description: "开发环境组织"
  }
]);

// 对话框表单数据
const dialogFormData = reactive({
  orgId: 0,
  orgName: "",
  instanceUrl: "",
  apiVersion: "61.0",
  username: "",
  password: "",
  useOAuth: false,
  clientId: "",
  clientSecret: "",
  callbackUrl: "",
  enableAutoSync: false,
  syncInterval: "30m",
  description: ""
});

// 表单验证规则
const dialogFormRules: FormRules = {
  orgName: [{ required: true, message: "请输入组织名称", trigger: "blur" }],
  instanceUrl: [
    { required: true, message: "请输入实例URL", trigger: "blur" },
    {
      pattern: /^[a-zA-Z0-9][a-zA-Z0-9\-]*\.salesforce\.com$/,
      message: "请输入正确的Salesforce实例URL",
      trigger: "blur"
    }
  ],
  apiVersion: [
    { required: true, message: "请选择API版本", trigger: "change" }
  ],
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
  clientId: [
    {
      required: true,
      message: "请输入客户端ID",
      trigger: "blur"
    }
  ],
  clientSecret: [
    {
      required: true,
      message: "请输入客户端密钥",
      trigger: "blur"
    }
  ]
};

// 过滤后的表格数据
const filteredTableData = computed(() => {
  if (!searchKeyword.value) {
    return tableData.value;
  }
  return tableData.value.filter(
    item =>
      item.orgName.includes(searchKeyword.value) ||
      item.instanceUrl.includes(searchKeyword.value)
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

// 添加组织
const handleAddOrg = () => {
  isEdit.value = false;
  dialogFormData.orgId = 0;
  dialogFormData.orgName = "";
  dialogFormData.instanceUrl = "";
  dialogFormData.apiVersion = "61.0";
  dialogFormData.username = "";
  dialogFormData.password = "";
  dialogFormData.useOAuth = false;
  dialogFormData.clientId = "";
  dialogFormData.clientSecret = "";
  dialogFormData.callbackUrl = "";
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
  ElMessage.success(`组织"${row.orgName}"连接测试成功`);
};

// 同步
const handleSync = (row: any) => {
  row.syncStatus = "syncing";
  ElMessage.info(`正在同步组织"${row.orgName}"...`);

  // 模拟同步完成
  setTimeout(() => {
    row.syncStatus = "normal";
    row.lastSyncTime = dayjs().format("YYYY-MM-DD HH:mm:ss");
    ElMessage.success(`组织"${row.orgName}"同步成功`);
  }, 2000);
};

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    `确定要删除组织"${row.orgName}"吗？删除后将停止该组织的数据同步。`,
    "提示",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    }
  )
    .then(() => {
      const index = tableData.value.findIndex(
        item => item.orgId === row.orgId
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

    if (isEdit.value) {
      // 编辑
      const index = tableData.value.findIndex(
        item => item.orgId === dialogFormData.orgId
      );
      if (index !== -1) {
        tableData.value[index] = {
          ...tableData.value[index],
          orgName: dialogFormData.orgName,
          instanceUrl: dialogFormData.instanceUrl,
          apiVersion: dialogFormData.apiVersion,
          username: dialogFormData.username,
          useOAuth: dialogFormData.useOAuth,
          enableAutoSync: dialogFormData.enableAutoSync,
          syncInterval: dialogFormData.syncInterval,
          description: dialogFormData.description
        };
      }
      ElMessage.success("更新成功");
    } else {
      // 新增
      tableData.value.push({
        orgId: Date.now(),
        orgName: dialogFormData.orgName,
        instanceUrl: dialogFormData.instanceUrl,
        apiVersion: dialogFormData.apiVersion,
        connectionStatus: "disconnected",
        syncStatus: "pending",
        lastSyncTime: "",
        username: dialogFormData.username,
        useOAuth: dialogFormData.useOAuth,
        enableAutoSync: dialogFormData.enableAutoSync,
        syncInterval: dialogFormData.syncInterval,
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

onMounted(() => {
  // TODO: 从接口获取组织列表
});
</script>

<style scoped lang="scss">
.multi-org-card {
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

    .org-name-cell {
      display: flex;
      align-items: center;
      gap: 8px;

      .org-icon {
        font-size: 18px;
        color: #1890ff;
      }

      .org-name {
        font-weight: 500;
        color: #333;
      }
    }

    .url-cell {
      font-size: 13px;
    }

    .sync-time {
      font-size: 13px;
      color: #666;
    }
  }

  .form-tip {
    margin-left: 12px;
    font-size: 13px;
    color: #999;
  }
}
</style>
