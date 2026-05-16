<template>
  <div class="multi-store-manage-page">
    <div class="config-card">
      <div class="page-header">
        <h2 class="config-title">多店铺管理</h2>
        <el-button type="primary" @click="handleAddStore">
          添加店铺
        </el-button>
      </div>

      <!-- 店铺列表表格 -->
      <el-table :data="storeList" class="store-table" border>
        <el-table-column type="index" label="编号" width="60" align="center" />
        <el-table-column prop="shopName" label="店铺名称" min-width="180" />
        <el-table-column prop="shopDomain" label="店铺域名" min-width="220" show-overflow-tooltip />
        <el-table-column prop="apiKey" label="API密钥" min-width="180" show-overflow-tooltip />
        <el-table-column prop="status" label="连接状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag
              v-if="row.status === 'connected'"
              type="success"
              size="small"
            >
              已连接
            </el-tag>
            <el-tag
              v-else-if="row.status === 'disconnected'"
              type="danger"
              size="small"
            >
              已断开
            </el-tag>
            <el-tag v-else type="warning" size="small">
              连接中
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastSyncTime" label="最后同步时间" width="180" />
        <el-table-column prop="syncStatus" label="同步状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag
              v-if="row.syncStatus === 'success'"
              type="success"
              size="small"
            >
              成功
            </el-tag>
            <el-tag
              v-else-if="row.syncStatus === 'failed'"
              type="danger"
              size="small"
            >
              失败
            </el-tag>
            <el-tag v-else type="info" size="small">
              未同步
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isDefault" label="默认店铺" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isDefault" type="primary" size="small">
              默认
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleTestConnect(row)">
              测试连接
            </el-button>
            <el-button type="primary" link @click="handleSync(row)">
              立即同步
            </el-button>
            <el-button
              v-if="!row.isDefault"
              type="primary"
              link
              @click="handleSetDefault(row)"
            >
              设为默认
            </el-button>
            <el-button type="primary" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 统计信息 -->
      <div class="store-summary">
        <div class="summary-item">
          <span class="label">总店铺数：</span>
          <span class="value">{{ storeList.length }}</span>
        </div>
        <div class="summary-item">
          <span class="label">已连接：</span>
          <span class="value success">{{ connectedCount }}</span>
        </div>
        <div class="summary-item">
          <span class="label">已断开：</span>
          <span class="value error">{{ disconnectedCount }}</span>
        </div>
        <div class="summary-item">
          <span class="label">默认店铺：</span>
          <span class="value primary">{{ defaultStoreName }}</span>
        </div>
      </div>
    </div>

    <!-- 添加/编辑店铺对话框 -->
    <el-dialog
      v-model="storeDialogVisible"
      :title="isEdit ? '编辑店铺' : '添加店铺'"
      width="600px"
    >
      <el-form :model="storeForm" label-width="120px">
        <el-form-item label="店铺名称" required>
          <el-input
            v-model="storeForm.shopName"
            placeholder="请输入店铺名称"
          />
        </el-form-item>
        <el-form-item label="店铺域名" required>
          <el-input
            v-model="storeForm.shopDomain"
            placeholder="example.myshopify.com"
          >
            <template #prepend>https://</template>
          </el-input>
        </el-form-item>
        <el-form-item label="API密钥" required>
          <el-input
            v-model="storeForm.apiKey"
            placeholder="请输入API密钥"
          />
        </el-form-item>
        <el-form-item label="API密码" required>
          <el-input
            v-model="storeForm.apiSecret"
            type="password"
            placeholder="请输入API密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="storeForm.isDefault" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="storeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmSave">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";

// 店铺列表数据
const storeList = ref([
  {
    id: 1,
    shopName: "主店铺",
    shopDomain: "mystore.myshopify.com",
    apiKey: "shpat_xxxxxx1",
    status: "connected",
    lastSyncTime: "2024-01-15 10:30:00",
    syncStatus: "success",
    isDefault: true
  },
  {
    id: 2,
    shopName: "分店A",
    shopDomain: "store-a.myshopify.com",
    apiKey: "shpat_xxxxxx2",
    status: "connected",
    lastSyncTime: "2024-01-15 11:45:00",
    syncStatus: "success",
    isDefault: false
  },
  {
    id: 3,
    shopName: "分店B",
    shopDomain: "store-b.myshopify.com",
    apiKey: "shpat_xxxxxx3",
    status: "disconnected",
    lastSyncTime: "2024-01-14 16:20:00",
    syncStatus: "failed",
    isDefault: false
  }
]);

// 统计数据
const connectedCount = computed(() => {
  return storeList.value.filter(store => store.status === "connected").length;
});

const disconnectedCount = computed(() => {
  return storeList.value.filter(store => store.status === "disconnected").length;
});

const defaultStoreName = computed(() => {
  const defaultStore = storeList.value.find(store => store.isDefault);
  return defaultStore ? defaultStore.shopName : "未设置";
});

// 店铺对话框
const storeDialogVisible = ref(false);
const isEdit = ref(false);
const storeForm = ref({
  id: null,
  shopName: "",
  shopDomain: "",
  apiKey: "",
  apiSecret: "",
  isDefault: false
});

// 添加店铺
const handleAddStore = () => {
  isEdit.value = false;
  storeForm.value = {
    id: null,
    shopName: "",
    shopDomain: "",
    apiKey: "",
    apiSecret: "",
    isDefault: false
  };
  storeDialogVisible.value = true;
};

// 编辑店铺
const handleEdit = (row: any) => {
  isEdit.value = true;
  storeForm.value = {
    id: row.id,
    shopName: row.shopName,
    shopDomain: row.shopDomain,
    apiKey: row.apiKey,
    apiSecret: "",
    isDefault: row.isDefault
  };
  storeDialogVisible.value = true;
};

// 确认保存
const handleConfirmSave = () => {
  if (!storeForm.value.shopName || !storeForm.value.shopDomain) {
    ElMessage.warning("请完整填写店铺信息");
    return;
  }

  if (isEdit.value) {
    // 编辑模式
    const index = storeList.value.findIndex(s => s.id === storeForm.value.id);
    if (index > -1) {
      storeList.value[index] = {
        ...storeList.value[index],
        shopName: storeForm.value.shopName,
        shopDomain: storeForm.value.shopDomain,
        apiKey: storeForm.value.apiKey,
        isDefault: storeForm.value.isDefault
      };
    }
    ElMessage.success("店铺信息已更新");
  } else {
    // 添加模式
    storeList.value.push({
      id: Date.now(),
      shopName: storeForm.value.shopName,
      shopDomain: storeForm.value.shopDomain,
      apiKey: storeForm.value.apiKey,
      status: "pending",
      lastSyncTime: "-",
      syncStatus: "pending",
      isDefault: storeForm.value.isDefault
    });
    ElMessage.success("店铺添加成功");
  }

  storeDialogVisible.value = false;
};

// 测试连接
const handleTestConnect = (row: any) => {
  ElMessage.info(`正在测试 ${row.shopName} 的连接...`);
  // TODO: 调用测试连接接口
  setTimeout(() => {
    row.status = "connected";
    ElMessage.success(`${row.shopName} 连接成功`);
  }, 1000);
};

// 立即同步
const handleSync = (row: any) => {
  ElMessageBox.confirm(`确定要同步 ${row.shopName} 的数据吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    // TODO: 调用同步接口
    ElMessage.success(`${row.shopName} 同步任务已启动`);
  });
};

// 设为默认
const handleSetDefault = (row: any) => {
  storeList.value.forEach(store => {
    store.isDefault = store.id === row.id;
  });
  ElMessage.success(`已将 ${row.shopName} 设为默认店铺`);
};

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    `确定要删除店铺"${row.shopName}"吗？删除后无法恢复。`,
    "警告",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    }
  ).then(() => {
    const index = storeList.value.findIndex(store => store.id === row.id);
    if (index > -1) {
      storeList.value.splice(index, 1);
    }
    ElMessage.success("店铺删除成功");
  });
};
</script>

<style scoped lang="scss">
.multi-store-manage-page {
  .config-card {
    background-color: #fff;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;

      .config-title {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: #333;
      }
    }

    .store-table {
      width: 100%;
      margin-bottom: 24px;

      :deep(.el-table__header) {
        th {
          background-color: #fafafa;
          color: #333;
          font-weight: 600;
        }
      }

      :deep(.el-table__body) {
        td {
          color: #666;
        }
      }
    }

    .store-summary {
      display: flex;
      gap: 32px;
      padding: 16px;
      background-color: #f5f5f5;
      border-radius: 4px;

      .summary-item {
        display: flex;
        align-items: center;
        gap: 8px;

        .label {
          font-size: 14px;
          color: #666;
        }

        .value {
          font-size: 16px;
          font-weight: 600;
          color: #333;

          &.success {
            color: #52c41a;
          }

          &.error {
            color: #f5222d;
          }

          &.primary {
            color: #1890ff;
          }
        }
      }
    }
  }
}
</style>
