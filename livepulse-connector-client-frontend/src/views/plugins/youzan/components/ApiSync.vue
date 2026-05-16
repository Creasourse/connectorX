<template>
  <div class="api-sync-card">
    <div class="card-header">
      <h2 class="card-title">接口同步配置</h2>
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
        <el-button type="primary" @click="handleBatchSync">
          <el-icon><Refresh /></el-icon>
          批量同步
        </el-button>
        <el-button @click="handleRefresh">
          <el-icon><RefreshRight /></el-icon>
          刷新状态
        </el-button>
      </div>
      <div class="action-bar-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索接口"
          clearable
          style="width: 240px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 同步说明 -->
    <div class="sync-info">
      <div class="info-title">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
        接口同步说明
      </div>
      <ul class="info-features">
        <li>支持有赞开放平台的所有业务接口数据同步</li>
        <li>可配置不同的同步频率，支持实时、定时同步</li>
        <li>提供同步状态监控和日志记录功能</li>
        <li>支持批量同步和单接口立即同步</li>
      </ul>
    </div>

    <!-- 接口列表表格 -->
    <div class="table-container">
      <el-table
        :data="filteredTableData"
        style="width: 100%"
        row-key="apiId"
        border
      >
        <el-table-column
          prop="apiName"
          label="接口名称"
          min-width="200"
        >
          <template #default="{ row }">
            <div class="api-name-cell">
              <el-icon class="api-icon"><Connection /></el-icon>
              <span class="api-name">{{ row.apiName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
          prop="apiDesc"
          label="接口说明"
          min-width="250"
          show-overflow-tooltip
        />

        <el-table-column label="同步频率" width="140" align="center">
          <template #default="{ row }">
            <el-tag
              :type="getFrequencyTagType(row.syncFrequency)"
              size="small"
            >
              {{ getFrequencyText(row.syncFrequency) }}
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

        <el-table-column label="同步状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag
              :type="getStatusType(row.syncStatus)"
              size="small"
            >
              {{ getStatusText(row.syncStatus) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              size="small"
              @click="handleConfig(row)"
            >
              配置
            </el-button>
            <el-button
              link
              type="success"
              size="small"
              @click="handleSyncNow(row)"
            >
              立即同步
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty
        v-if="filteredTableData.length === 0"
        description="暂无接口配置"
        :image-size="100"
      />
    </div>

    <!-- 配置对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="`配置 ${currentApi?.apiName}`"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="dialogFormRef"
        :model="dialogFormData"
        label-width="140px"
        label-position="left"
      >
        <el-form-item label="接口名称">
          <el-input v-model="dialogFormData.apiName" disabled />
        </el-form-item>

        <el-form-item label="接口说明">
          <el-input v-model="dialogFormData.apiDesc" disabled />
        </el-form-item>

        <el-form-item label="启用同步">
          <el-switch v-model="dialogFormData.enableSync" />
        </el-form-item>

        <el-form-item label="同步频率">
          <el-select
            v-model="dialogFormData.syncFrequency"
            placeholder="选择同步频率"
            style="width: 100%"
            :disabled="!dialogFormData.enableSync"
          >
            <el-option label="实时同步" value="realtime" />
            <el-option label="每5分钟" value="5m" />
            <el-option label="每15分钟" value="15m" />
            <el-option label="每30分钟" value="30m" />
            <el-option label="每小时" value="1h" />
            <el-option label="每天" value="1d" />
            <el-option label="手动同步" value="manual" />
          </el-select>
        </el-form-item>

        <el-form-item label="同步数据量">
          <el-select
            v-model="dialogFormData.pageSize"
            placeholder="选择每次同步的数据量"
            style="width: 100%"
            :disabled="!dialogFormData.enableSync"
          >
            <el-option label="每页20条" :value="20" />
            <el-option label="每页50条" :value="50" />
            <el-option label="每页100条" :value="100" />
            <el-option label="每页200条" :value="200" />
          </el-select>
        </el-form-item>

        <el-form-item label="启用增量同步">
          <el-switch v-model="dialogFormData.enableIncremental" />
          <span class="form-tip">只同步新增或变更的数据</span>
        </el-form-item>

        <el-form-item
          v-if="dialogFormData.enableIncremental"
          label="增量字段"
        >
          <el-select
            v-model="dialogFormData.incrementalField"
            placeholder="选择用于增量同步的字段"
            style="width: 100%"
            multiple
          >
            <el-option label="更新时间（update_time）" value="update_time" />
            <el-option label="创建时间（created_time）" value="created_time" />
            <el-option label="订单号（tid）" value="tid" />
            <el-option label="商品编号（num_iid）" value="num_iid" />
          </el-select>
        </el-form-item>

        <el-form-item label="数据过滤">
          <el-input
            v-model="dialogFormData.filterCondition"
            type="textarea"
            :rows="3"
            placeholder="输入过滤条件（JSON格式），留空表示同步所有数据"
            :disabled="!dialogFormData.enableSync"
          />
        </el-form-item>

        <el-form-item label="失败重试">
          <el-switch v-model="dialogFormData.enableRetry" />
        </el-form-item>

        <el-form-item
          v-if="dialogFormData.enableRetry"
          label="最大重试次数"
        >
          <el-input-number
            v-model="dialogFormData.maxRetries"
            :min="1"
            :max="10"
          />
        </el-form-item>

        <el-form-item label="错误通知">
          <el-switch v-model="dialogFormData.enableErrorNotify" />
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
import { ElMessage, type FormInstance } from "element-plus";
import {
  Refresh,
  RefreshRight,
  Search,
  InfoFilled,
  Connection
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
const currentApi = ref<any>(null);

// 表格数据
const tableData = ref([
  {
    apiId: 1,
    apiName: "youzan.trade.get",
    apiDesc: "获取订单列表",
    syncFrequency: "30m",
    lastSyncTime: "2024-01-15 14:30:25",
    syncStatus: "normal",
    config: {
      enableSync: true,
      syncFrequency: "30m",
      pageSize: 100,
      enableIncremental: true,
      incrementalField: ["update_time"],
      filterCondition: "",
      enableRetry: true,
      maxRetries: 3,
      enableErrorNotify: true
    }
  },
  {
    apiId: 2,
    apiName: "youzan.item.get",
    apiDesc: "获取商品列表",
    syncFrequency: "1h",
    lastSyncTime: "2024-01-15 14:00:10",
    syncStatus: "normal",
    config: {
      enableSync: true,
      syncFrequency: "1h",
      pageSize: 100,
      enableIncremental: true,
      incrementalField: ["update_time"],
      filterCondition: "",
      enableRetry: true,
      maxRetries: 3,
      enableErrorNotify: true
    }
  },
  {
    apiId: 3,
    apiName: "youzan.user.get",
    apiDesc: "获取客户信息",
    syncFrequency: "1h",
    lastSyncTime: "2024-01-15 13:45:30",
    syncStatus: "normal",
    config: {
      enableSync: true,
      syncFrequency: "1h",
      pageSize: 50,
      enableIncremental: true,
      incrementalField: ["update_time"],
      filterCondition: "",
      enableRetry: true,
      maxRetries: 3,
      enableErrorNotify: true
    }
  },
  {
    apiId: 4,
    apiName: "youzan.trade.bill.get",
    apiDesc: "获取交易数据",
    syncFrequency: "1d",
    lastSyncTime: "2024-01-15 00:00:00",
    syncStatus: "normal",
    config: {
      enableSync: true,
      syncFrequency: "1d",
      pageSize: 200,
      enableIncremental: true,
      incrementalField: ["created_time"],
      filterCondition: "",
      enableRetry: true,
      maxRetries: 3,
      enableErrorNotify: true
    }
  },
  {
    apiId: 5,
    apiName: "youzan.refund.get",
    apiDesc: "获取退款信息",
    syncFrequency: "1h",
    lastSyncTime: "2024-01-15 14:15:20",
    syncStatus: "normal",
    config: {
      enableSync: true,
      syncFrequency: "1h",
      pageSize: 100,
      enableIncremental: true,
      incrementalField: ["created_time"],
      filterCondition: "",
      enableRetry: true,
      maxRetries: 3,
      enableErrorNotify: true
    }
  },
  {
    apiId: 6,
    apiName: "youzan.ump.coupon.get",
    apiDesc: "获取优惠券信息",
    syncFrequency: "manual",
    lastSyncTime: "",
    syncStatus: "pending",
    config: {
      enableSync: false,
      syncFrequency: "manual",
      pageSize: 100,
      enableIncremental: false,
      incrementalField: [],
      filterCondition: "",
      enableRetry: false,
      maxRetries: 3,
      enableErrorNotify: false
    }
  }
]);

// 对话框表单数据
const dialogFormData = reactive({
  apiId: 0,
  apiName: "",
  apiDesc: "",
  enableSync: false,
  syncFrequency: "30m",
  pageSize: 100,
  enableIncremental: false,
  incrementalField: [],
  filterCondition: "",
  enableRetry: false,
  maxRetries: 3,
  enableErrorNotify: false
});

// 过滤后的表格数据
const filteredTableData = computed(() => {
  if (!searchKeyword.value) {
    return tableData.value;
  }
  return tableData.value.filter(
    item =>
      item.apiName.includes(searchKeyword.value) ||
      item.apiDesc.includes(searchKeyword.value)
  );
});

// 获取频率标签类型
const getFrequencyTagType = (frequency: string) => {
  const typeMap: Record<string, string> = {
    realtime: "danger",
    "5m": "warning",
    "15m": "warning",
    "30m": "success",
    "1h": "success",
    "1d": "info",
    manual: ""
  };
  return typeMap[frequency] || "";
};

// 获取频率文本
const getFrequencyText = (frequency: string) => {
  const textMap: Record<string, string> = {
    realtime: "实时",
    "5m": "每5分钟",
    "15m": "每15分钟",
    "30m": "每30分钟",
    "1h": "每小时",
    "1d": "每天",
    manual: "手动"
  };
  return textMap[frequency] || frequency;
};

// 获取状态类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    normal: "success",
    syncing: "warning",
    error: "danger",
    pending: "info"
  };
  return typeMap[status] || "info";
};

// 获取状态文本
const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    normal: "正常",
    syncing: "同步中",
    error: "异常",
    pending: "待同步"
  };
  return textMap[status] || "未知";
};

// 批量同步
const handleBatchSync = () => {
  ElMessage.info("批量同步功能开发中...");
};

// 刷新状态
const handleRefresh = () => {
  ElMessage.success("状态已刷新");
};

// 配置
const handleConfig = (row: any) => {
  currentApi.value = row;
  dialogFormData.apiId = row.apiId;
  dialogFormData.apiName = row.apiName;
  dialogFormData.apiDesc = row.apiDesc;
  dialogFormData.enableSync = row.config?.enableSync || false;
  dialogFormData.syncFrequency = row.config?.syncFrequency || "30m";
  dialogFormData.pageSize = row.config?.pageSize || 100;
  dialogFormData.enableIncremental = row.config?.enableIncremental || false;
  dialogFormData.incrementalField = row.config?.incrementalField || [];
  dialogFormData.filterCondition = row.config?.filterCondition || "";
  dialogFormData.enableRetry = row.config?.enableRetry || false;
  dialogFormData.maxRetries = row.config?.maxRetries || 3;
  dialogFormData.enableErrorNotify = row.config?.enableErrorNotify || false;
  dialogVisible.value = true;
};

// 立即同步
const handleSyncNow = (row: any) => {
  row.syncStatus = "syncing";
  ElMessage.info(`正在同步"${row.apiName}"...`);

  // 模拟同步完成
  setTimeout(() => {
    row.syncStatus = "normal";
    row.lastSyncTime = dayjs().format("YYYY-MM-DD HH:mm:ss");
    ElMessage.success(`"${row.apiName}"同步成功`);
  }, 2000);
};

// 对话框保存
const handleDialogSave = () => {
  if (!currentApi.value) return;

  // 更新当前接口的配置
  const index = tableData.value.findIndex(
    item => item.apiId === dialogFormData.apiId
  );
  if (index !== -1) {
    tableData.value[index].config = {
      enableSync: dialogFormData.enableSync,
      syncFrequency: dialogFormData.syncFrequency,
      pageSize: dialogFormData.pageSize,
      enableIncremental: dialogFormData.enableIncremental,
      incrementalField: dialogFormData.incrementalField,
      filterCondition: dialogFormData.filterCondition,
      enableRetry: dialogFormData.enableRetry,
      maxRetries: dialogFormData.maxRetries,
      enableErrorNotify: dialogFormData.enableErrorNotify
    };

    // 更新同步频率
    tableData.value[index].syncFrequency = dialogFormData.syncFrequency;
    tableData.value[index].syncStatus = dialogFormData.enableSync
      ? "normal"
      : "pending";
  }

  ElMessage.success("配置保存成功");
  dialogVisible.value = false;
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
.api-sync-card {
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

  .sync-info {
    margin-bottom: 16px;
    padding: 16px;
    background: linear-gradient(135deg, #fff7e6 0%, #ffe7ba 100%);
    border: 1px solid #ffd591;
    border-radius: 6px;

    .info-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      font-weight: 600;
      color: #fa8c16;
      margin-bottom: 12px;

      .info-icon {
        font-size: 16px;
      }
    }

    .info-features {
      margin: 0;
      padding-left: 20px;

      li {
        font-size: 13px;
        color: #fa8c16;
        line-height: 1.8;
        margin-bottom: 4px;

        &:last-child {
          margin-bottom: 0;
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

    .api-name-cell {
      display: flex;
      align-items: center;
      gap: 8px;

      .api-icon {
        font-size: 16px;
        color: #fa8c16;
      }

      .api-name {
        font-family: "Courier New", monospace;
        color: #333;
        font-weight: 500;
      }
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
