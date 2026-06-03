<template>
  <div class="sync-monitor">
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon success">
          <el-icon><SuccessFilled /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">同步成功</div>
          <div class="stat-value">{{ stats.successCount }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon failed">
          <el-icon><CircleCloseFilled /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">同步失败</div>
          <div class="stat-value">{{ stats.failedCount }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon processing">
          <el-icon><Loading /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">同步中</div>
          <div class="stat-value">{{ stats.processingCount }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon total">
          <el-icon><DataLine /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">总计</div>
          <div class="stat-value">{{ stats.totalCount }}</div>
        </div>
      </div>
    </div>

    <!-- 查询表单 -->
    <div class="filter-form">
      <el-form :model="queryForm" inline>
        <el-form-item label="多维表格">
          <el-select
            v-model="queryForm.bitableId"
            placeholder="请选择多维表格"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="item in bitableList"
              :key="item.id"
              :label="item.bitableName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="同步状态">
          <el-select
            v-model="queryForm.syncStatus"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="成功" value="success" />
            <el-option label="失败" value="failed" />
            <el-option label="同步中" value="processing" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="queryForm.dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 350px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><RefreshLeft /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 同步日志列表 -->
    <el-table :data="logList" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="bitableName" label="多维表格" width="180" />
      <el-table-column prop="syncMode" label="同步模式" width="100">
        <template #default="{ row }">
          <el-tag :type="getSyncModeType(row.syncMode)">
            {{ getSyncModeLabel(row.syncMode) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="syncStatus" label="同步状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getSyncStatusType(row.syncStatus)">
            {{ getSyncStatusLabel(row.syncStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="recordCount" label="记录数" width="100" align="right" />
      <el-table-column prop="successCount" label="成功数" width="100" align="right" />
      <el-table-column prop="failedCount" label="失败数" width="100" align="right" />
      <el-table-column prop="startTime" label="开始时间" width="180">
        <template #default="{ row }">
          {{ formatTime(row.startTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="endTime" label="结束时间" width="180">
        <template #default="{ row }">
          {{ formatTime(row.endTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="duration" label="耗时（秒）" width="120" align="right" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleViewDetail(row)"> 详情 </el-button>
          <el-button
            v-if="row.syncStatus === 'failed'"
            type="warning"
            link
            @click="handleRetry(row)"
            :loading="row.retrying"
          >
            重试
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="table-footer">
      <span class="total-text">共 {{ total }} 条记录</span>
      <el-pagination
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="sizes, prev, pager, next"
        :total="total"
        background
        @size-change="getLogList"
        @current-change="getLogList"
      />
    </div>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="同步详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="多维表格">
          {{ currentLog?.bitableName }}
        </el-descriptions-item>
        <el-descriptions-item label="同步模式">
          {{ getSyncModeLabel(currentLogSyncMode) }}
        </el-descriptions-item>
        <el-descriptions-item label="同步状态">
          <el-tag :type="getSyncStatusType(currentLogSyncStatus)">
            {{ getSyncStatusLabel(currentLogSyncStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="记录数">
          {{ currentLog?.recordCount }}
        </el-descriptions-item>
        <el-descriptions-item label="成功数">
          {{ currentLog?.successCount }}
        </el-descriptions-item>
        <el-descriptions-item label="失败数">
          {{ currentLog?.failedCount }}
        </el-descriptions-item>
        <el-descriptions-item label="开始时间" :span="2">
          {{ formatTime(currentLogStartTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="结束时间" :span="2">
          {{ formatTime(currentLogEndTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="耗时（秒）" :span="2">
          {{ currentLog?.duration }}
        </el-descriptions-item>
        <el-descriptions-item label="错误消息" :span="2" v-if="currentLog?.errorMessage">
          <div class="error-message">{{ currentLog.errorMessage }}</div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, markRaw, watch, computed } from "vue";
import type { PropType } from "vue";
import { ElMessage } from "element-plus";
import {
  SuccessFilled,
  CircleCloseFilled,
  Loading,
  DataLine,
  Search,
  RefreshLeft
} from "@element-plus/icons-vue";
import {
  getFeishuBitableListByAppId,
  getFeishuSyncLogPageList,
  getFeishuSyncLogStatistics,
  executeFeishuDataSync
} from "@/api/feishu";
import type { SyncLog, SyncStats, LogQueryParams, Bitable } from "../types";

const props = defineProps({
  config: {
    type: Object as PropType<Record<string, any>>,
    default: () => ({})
  }
});

const loading = ref(false);
const detailVisible = ref(false);
const currentLog = ref<SyncLog | null>(null);

// 统计数据
const stats = reactive<SyncStats>({
  successCount: 0,
  failedCount: 0,
  processingCount: 0,
  totalCount: 0
});

// 多维表格列表
const bitableList = ref<Bitable[]>([]);

// 查询表单
const queryForm = reactive<LogQueryParams>({
  bitableId: undefined,
  syncStatus: undefined,
  dateRange: undefined as any,
  pageNum: 1,
  pageSize: 20
});

// 日志列表
const logList = ref<SyncLog[]>([]);
const total = ref(0);

// 当前日志的计算属性（用于详情对话框）
const currentLogSyncMode = computed(() => currentLog.value?.syncMode || "");
const currentLogSyncStatus = computed(() => currentLog.value?.syncStatus || "");
const currentLogStartTime = computed(() => currentLog.value?.startTime || "");
const currentLogEndTime = computed(() => currentLog.value?.endTime || "");

// 使用 markRaw 优化映射表（不需要响应式）
const syncModeLabelMap = markRaw({
  webhook: "Webhook",
  polling: "轮询",
  hybrid: "混合"
});

const syncModeTypeMap = markRaw({
  webhook: "success",
  polling: "warning",
  hybrid: "primary"
});

const syncStatusLabelMap = markRaw({
  success: "成功",
  failed: "失败",
  processing: "同步中"
});

const syncStatusTypeMap = markRaw({
  success: "success",
  failed: "danger",
  processing: "warning"
});

// 加载多维表格列表
const loadBitableList = async () => {
  if (!props.config.feishuAppId) return;

  try {
    const res = await getFeishuBitableListByAppId(props.config.feishuAppId);
    if (res.success && res.data) {
      bitableList.value = res.data;
    }
  } catch (error) {
    console.error("加载多维表格列表失败:", error);
  }
};

// 获取统计数据
const loadStats = async () => {
  if (!props.config.feishuAppId) return;

  try {
    const params: any = {};
    if (queryForm.dateRange && queryForm.dateRange.length === 2) {
      params.startTime = queryForm.dateRange[0];
      params.endTime = queryForm.dateRange[1];
    }

    const res = await getFeishuSyncLogStatistics(params);
    if (res.success && res.data) {
      Object.assign(stats, res.data);
    }
  } catch (error) {
    console.error("获取统计数据失败:", error);
  }
};

// 获取日志列表
const getLogList = async () => {
  if (!props.config.feishuAppId) return;

  loading.value = true;
  try {
    const params: any = {
      pageNum: queryForm.pageNum,
      pageSize: queryForm.pageSize
    };

    // 添加查询条件
    if (queryForm.bitableId) {
      // 如果选择了多维表格，需要先查询该多维表格下的所有同步任务ID
      // 这里暂时简化处理，直接传0表示不筛选
    }
    if (queryForm.syncStatus) {
      params.syncStatus = queryForm.syncStatus;
    }
    if (queryForm.dateRange && queryForm.dateRange.length === 2) {
      params.startTime = queryForm.dateRange[0];
      params.endTime = queryForm.dateRange[1];
    }

    const res = await getFeishuSyncLogPageList(params);
    if (res.success && res.data) {
      logList.value = (res.data.records || []).map((item: any) => ({
        id: item.id,
        feishuDataSyncId: item.feishuDataSyncId,
        syncName: item.syncName,
        bitableName: item.bitableName || "未命名",
        tableName: item.tableName || "-",
        syncType: item.syncType || "manual",
        syncDirection: item.syncDirection || "unknown",
        syncStatus: item.syncStatus,
        startTime: item.startTime,
        endTime: item.endTime,
        recordCount: item.recordCount || 0,
        successCount: item.successCount || 0,
        failedCount: item.failedCount || 0,
        duration: item.duration || 0,
        errorMessage: item.errorMessage
      }));
      total.value = res.data.total || 0;
    }
  } catch (error) {
    ElMessage.error("获取日志列表失败");
    console.error(error);
  } finally {
    loading.value = false;
  }
};

// 查询
const handleQuery = () => {
  queryForm.pageNum = 1;
  getLogList();
};

// 重置
const handleReset = () => {
  Object.assign(queryForm, {
    bitableId: undefined,
    syncStatus: undefined,
    dateRange: undefined as any,
    pageNum: 1,
    pageSize: 20
  });
  getLogList();
  loadStats();
};

// 查看详情
const handleViewDetail = async (row: SyncLog) => {
  currentLog.value = row;
  detailVisible.value = true;
};

// 重试
const handleRetry = async (row: SyncLog) => {
  try {
    (row as any).retrying = true;

    if (!row.feishuDataSyncId) {
      ElMessage.error("同步任务ID不存在");
      return;
    }

    await executeFeishuDataSync(row.feishuDataSyncId);
    ElMessage.success("重试已触发");

    // 刷新列表和统计
    await getLogList();
    await loadStats();
  } catch (error) {
    ElMessage.error("重试失败");
    console.error(error);
  } finally {
    (row as any).retrying = false;
  }
};

// 工具方法（使用映射表优化性能）
const getSyncModeLabel = (mode: string) => {
  return syncModeLabelMap[mode as keyof typeof syncModeLabelMap] || mode;
};

const getSyncModeType = (mode: string) => {
  return syncModeTypeMap[mode as keyof typeof syncModeTypeMap] || "";
};

const getSyncStatusLabel = (status: string) => {
  return syncStatusLabelMap[status as keyof typeof syncStatusLabelMap] || status;
};

const getSyncStatusType = (status: string) => {
  return syncStatusTypeMap[status as keyof typeof syncStatusTypeMap] || "";
};

const formatTime = (time: string) => {
  if (!time) return "-";
  return new Date(time).toLocaleString("zh-CN");
};

// 定时刷新统计数据
let refreshTimer: NodeJS.Timeout | null = null;

const startRefresh = () => {
  // 每 30 秒刷新一次统计
  refreshTimer = setInterval(() => {
    loadStats();
  }, 30000);
};

const stopRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer);
    refreshTimer = null;
  }
};

// 监听配置变化
watch(
  () => props.config.feishuAppId,
  newId => {
    if (newId) {
      loadBitableList();
      getLogList();
      loadStats();
    }
  },
  { immediate: true }
);

// 初始化
onMounted(async () => {
  await loadStats();
  await getLogList();
  startRefresh();
});

// 组件卸载时停止定时器
onUnmounted(() => {
  stopRefresh();
});
</script>

<style lang="scss" scoped>
.sync-monitor {
  .stats-cards {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    margin-bottom: 24px;

    .stat-card {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 20px;
      background: #fff;
      border-radius: 8px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
      border: 1px solid #e5e7eb;
      transition: all 0.3s;

      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }

      .stat-icon {
        width: 48px;
        height: 48px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 8px;
        font-size: 24px;

        &.success {
          background: #f0fdf4;
          color: #00b894;
        }

        &.failed {
          background: #fef2f2;
          color: #ef4444;
        }

        &.processing {
          background: #fffbeb;
          color: #f59e0b;
        }

        &.total {
          background: #eff6ff;
          color: #6c5ce7;
        }
      }

      .stat-content {
        flex: 1;

        .stat-label {
          font-size: 14px;
          color: #6b7280;
          margin-bottom: 8px;
        }

        .stat-value {
          font-size: 24px;
          font-weight: 600;
          color: #2d3748;
        }
      }
    }
  }

  .filter-form {
    margin-bottom: 16px;
    padding: 16px;
    background: #fff;
    border-radius: 8px;
    border: 1px solid #e5e7eb;
  }

  .error-message {
    color: #ef4444;
    word-break: break-all;
    white-space: pre-wrap;
  }

  :deep(.el-table) {
    .el-button {
      padding: 4px 8px;
    }
  }

  .table-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 16px;

    .total-text {
      font-size: 13px;
      color: #6b7280;
    }
  }
}
</style>
