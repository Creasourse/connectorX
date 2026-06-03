<template>
  <div class="data-sync">
    <!-- 标题和操作按钮 -->
    <div class="header">
      <h2 class="title">数据同步管理</h2>
      <el-button type="primary" class="create-task-btn" @click="handleCreateTask">
        <el-icon><Plus /></el-icon>
        创建同步任务
      </el-button>
    </div>

    <!-- 活跃同步任务 -->
    <div class="active-tasks-section">
      <div class="section-header">
        <div class="section-title-wrapper">
          <el-icon class="wave-icon"><TrendCharts /></el-icon>
          <span class="section-title">活跃同步任务</span>
          <span class="task-count">{{ taskCount }}个</span>
        </div>
      </div>

      <!-- 任务列表 -->
      <div class="task-list" v-loading="loading">
        <div
          v-for="task in taskList"
          :key="task.id"
          v-memo="[task.id, task.status, task.syncing]"
          class="task-card"
        >
          <!-- 任务头部 -->
          <div class="task-header">
            <div class="task-info">
              <div class="status-dot" :class="task.status"></div>
              <h3 class="task-name">{{ task.name }}</h3>
            </div>
            <div class="task-actions">
              <el-button
                size="small"
                class="action-btn view-btn"
                @click="handleView(task)"
              >
                查看
              </el-button>
              <el-button size="small" class="action-btn edit-btn" @click="handleEdit(task)">
                编辑
              </el-button>
              <el-button
                size="small"
                class="action-btn pause-btn"
                @click="handleToggleEnabled(task)"
                :loading="task._toggling"
              >
                {{ task.isEnabled === 1 ? "停用" : "启用" }}
              </el-button>
              <el-button
                size="small"
                type="primary"
                class="action-btn sync-btn"
                @click="handleSyncNow(task)"
                :loading="task.syncing"
                :disabled="task.isEnabled === 0"
              >
                立即同步
              </el-button>
            </div>
          </div>

          <!-- 任务详情 -->
          <div class="task-details">
            <div class="detail-item">
              <el-icon class="detail-icon"><Grid /></el-icon>
              <span class="detail-label">表格：</span>
              <span class="detail-value">{{ task.tableName }}</span>
            </div>
            <div class="detail-item">
              <el-icon class="detail-icon">
                <component :is="getSyncDirectionIcon(task.direction)" />
              </el-icon>
              <span class="detail-value">{{ getSyncDirectionText(task.direction) }}</span>
            </div>
          </div>

          <!-- 同步信息 -->
          <div class="sync-info">
            <div class="info-item">
              <span class="info-label">{{
                task.status === "active" ? "最后同步" : "下次同步"
              }}：</span>
              <span class="info-value">{{ task.syncTime }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">已同步：</span>
              <span class="info-value">{{ task.syncedCount }}条</span>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <el-empty
          v-if="!loading && taskCount === 0"
          v-once
          description="暂无同步任务"
          :image-size="120"
        />
      </div>
    </div>

    <!-- 创建同步任务弹窗 -->
    <CreateSyncTaskDialog
      v-model="showCreateDialog"
      :task-id="editingTaskId"
      @save="handleSaveTask"
    />

    <!-- 查看详情弹窗 -->
    <el-dialog
      v-model="showDetailDialog"
      title="同步任务详情"
      width="600px"
    >
      <div v-if="currentTask" class="task-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="任务名称">
            {{ currentTask.name }}
          </el-descriptions-item>
          <el-descriptions-item label="目标表格">
            {{ currentTask.tableName }}
          </el-descriptions-item>
          <el-descriptions-item label="同步方向">
            {{ getSyncDirectionText(currentTask.direction) }}
          </el-descriptions-item>
          <el-descriptions-item label="同步状态">
            <el-tag
              :type="currentTask.isEnabled === 1 ? 'success' : 'info'"
              size="small"
            >
              {{ currentTask.isEnabled === 1 ? '已启用' : '已停用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="最后同步时间">
            {{ currentTask.syncTime }}
          </el-descriptions-item>
          <el-descriptions-item label="已同步记录数">
            {{ currentTask.syncedCount }} 条
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button type="primary" @click="handleEditFromDetail">编辑</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, markRaw, defineAsyncComponent, watch } from "vue";
import type { PropType } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Plus,
  TrendCharts,
  Grid,
  Sort,
  Download,
  ArrowRight
} from "@element-plus/icons-vue";
import {
  getFeishuDataSyncPageList,
  executeFeishuDataSync,
  toggleFeishuDataSyncEnabled,
  deleteFeishuDataSync,
  getFeishuDataSyncDetail
} from "@/api/feishu";
import type { SyncTask } from "../types";

const props = defineProps({
  config: {
    type: Object as PropType<Record<string, any>>,
    default: () => ({})
  }
});

// 懒加载弹窗组件
const CreateSyncTaskDialog = defineAsyncComponent(() => import("./CreateSyncTaskDialog.vue"));

defineOptions({
  name: "DataSync"
});

const loading = ref(false);
const showCreateDialog = ref(false);
const showDetailDialog = ref(false);
const editingTaskId = ref<number | undefined>(undefined);
const currentTask = ref<SyncTask | null>(null);

// 使用 markRaw 优化图标映射（不需要响应式）
const iconMap = markRaw({
  bidirectional: Sort,
  pull: Download,
  push: ArrowRight,
  default: Sort
});

// 使用 computed 缓存同步方向文本映射
const syncDirectionTextMap = markRaw({
  bidirectional: "双向同步",
  pull: "单向拉取",
  push: "单向推送",
  unknown: "未知"
});

// 任务列表数据
const taskList = ref<SyncTask[]>([]);

// 使用 computed 缓存计算结果
const taskCount = computed(() => taskList.value.length);

// 获取同步方向图标（使用映射表优化性能）
const getSyncDirectionIcon = (direction: string) => {
  return iconMap[direction as keyof typeof iconMap] || iconMap.default;
};

// 获取同步方向文本（使用映射表优化性能）
const getSyncDirectionText = (direction: string) => {
  return (
    syncDirectionTextMap[direction as keyof typeof syncDirectionTextMap] ||
    syncDirectionTextMap.unknown
  );
};

// 加载任务列表
const loadTaskList = async () => {
  if (!props.config.feishuAppId) {
    ElMessage.warning("请先配置飞书应用");
    return;
  }

  loading.value = true;
  try {
    const params = {
      pageNum: 1,
      pageSize: 100,
      feishuDataTableId: props.config.feishuDataTableId
    };
    const res = await getFeishuDataSyncPageList(params);
    if (res.success && res.data) {
      taskList.value = (res.data.records || []).map((item: any) => ({
        id: item.id,
        name: item.syncName,
        tableName: item.tableName || "未命名",
        direction: item.syncDirection || "unknown",
        status: item.syncStatus === "success" ? "active" : item.syncStatus === "failed" ? "failed" : "pending",
        syncTime: item.lastSyncTime || "未同步",
        syncedCount: item.syncedCount || 0,
        syncing: false,
        isEnabled: item.isEnabled ?? 1,
        _toggling: false,
        // 保存完整原始数据，用于编辑时回显
        _rawData: item
      }));
    }
  } catch (error) {
    ElMessage.error("加载任务列表失败");
    console.error(error);
  } finally {
    loading.value = false;
  }
};

// 查看任务详情
const handleView = (task: SyncTask) => {
  currentTask.value = task;
  showDetailDialog.value = true;
};

// 创建任务
const handleCreateTask = () => {
  editingTaskId.value = undefined;
  showCreateDialog.value = true;
};

// 编辑任务
const handleEdit = async (task: SyncTask) => {
  if (!task.id) return;

  // 先加载任务详情，确保数据完整
  try {
    const res = await getFeishuDataSyncDetail(task.id);
    if (res.success && res.data) {
      // 设置编辑任务ID
      editingTaskId.value = task.id;
      // 打开编辑对话框
      showCreateDialog.value = true;
    } else {
      ElMessage.error(res.msg || "加载任务详情失败");
    }
  } catch (error) {
    ElMessage.error("加载任务详情失败");
    console.error(error);
  }
};

// 从详情对话框编辑
const handleEditFromDetail = async () => {
  if (!currentTask.value?.id) return;

  showDetailDialog.value = false;

  // 加载完整数据
  try {
    const res = await getFeishuDataSyncDetail(currentTask.value.id);
    if (res.success && res.data) {
      editingTaskId.value = currentTask.value.id;
      showCreateDialog.value = true;
    } else {
      ElMessage.error(res.msg || "加载任务详情失败");
    }
  } catch (error) {
    ElMessage.error("加载任务详情失败");
    console.error(error);
  }
};

// 切换启用/停用状态
const handleToggleEnabled = async (task: SyncTask) => {
  if (!task.id) return;

  try {
    await ElMessageBox.confirm(
      `确定要${task.isEnabled === 1 ? "停用" : "启用"}同步任务"${task.name}"吗？`,
      "确认操作",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }
    );
  } catch {
    return;
  }

  task._toggling = true;
  try {
    const res = await toggleFeishuDataSyncEnabled(task.id);
    if (res.success) {
      task.isEnabled = task.isEnabled === 1 ? 0 : 1;
      ElMessage.success(`任务已${task.isEnabled === 1 ? "启用" : "停用"}`);
    } else {
      ElMessage.error(res.msg || "操作失败");
    }
  } catch (error) {
    ElMessage.error("操作失败");
    console.error(error);
  } finally {
    task._toggling = false;
  }
};

// 立即同步
const handleSyncNow = async (task: SyncTask) => {
  if (!task.id) return;

  task.syncing = true;
  try {
    const res = await executeFeishuDataSync(task.id);
    if (res.success && res.data) {
      const result = res.data as any;
      const totalRecords = result.totalRecords || 0;
      const successRecords = result.successRecords || 0;
      const duration = result.duration || 0;

      if (result.status === 'success') {
        ElMessage.success(`同步成功！共同步 ${successRecords}/${totalRecords} 条记录，耗时 ${duration}秒`);
      } else {
        ElMessage.warning(`同步完成，但有部分失败：${successRecords}/${totalRecords} 条成功`);
      }

      // 刷新任务列表
      await loadTaskList();
    } else {
      ElMessage.error(res.msg || "同步触发失败");
    }
  } catch {
    ElMessage.error("同步触发失败");
  } finally {
    task.syncing = false;
  }
};

// 保存任务成功回调
const handleSaveTask = () => {
  // 刷新任务列表
  loadTaskList();
  // 重置编辑状态
  editingTaskId.value = undefined;
};

// 监听对话框关闭，重置编辑状态
watch(showCreateDialog, (isOpen) => {
  if (!isOpen) {
    editingTaskId.value = undefined;
  }
});

// 监听配置变化
watch(
  () => props.config.feishuAppId,
  newId => {
    if (newId) {
      loadTaskList();
    }
  },
  { immediate: true }
);

onMounted(() => {
  loadTaskList();
});
</script>

<style lang="scss" scoped>
.data-sync {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .title {
      font-size: 20px;
      font-weight: 600;
      color: #2d3748;
      margin: 0;
    }

    .create-task-btn {
      background: #6c5ce7;
      border: none;
      color: #fff;
      padding: 10px 20px;
      border-radius: 6px;
      font-weight: 500;
      display: flex;
      align-items: center;
      gap: 6px;
      transition: all 0.3s;

      &:hover {
        background: #5a4bd1;
      }

      .el-icon {
        font-size: 16px;
      }
    }
  }

  .active-tasks-section {
    .section-header {
      margin-bottom: 20px;

      .section-title-wrapper {
        display: flex;
        align-items: center;
        gap: 8px;

        .wave-icon {
          font-size: 20px;
          color: #00b894;
        }

        .section-title {
          font-size: 16px;
          font-weight: 600;
          color: #2d3748;
        }

        .task-count {
          font-size: 14px;
          color: #00b894;
          font-weight: 500;
        }
      }
    }

    .task-list {
      display: flex;
      flex-direction: column;
      gap: 16px;

      .task-card {
        background: #fff;
        border-radius: 8px;
        padding: 20px;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
        border: 1px solid #e5e7eb;
        transition: all 0.3s;

        &:hover {
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        .task-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 16px;

          .task-info {
            display: flex;
            align-items: center;
            gap: 10px;

            .status-dot {
              width: 8px;
              height: 8px;
              border-radius: 50%;
              flex-shrink: 0;

              &.active {
                background: #00b894;
                box-shadow: 0 0 0 2px rgba(0, 184, 148, 0.2);
              }

              &.pending {
                background: #3b82f6;
                box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
              }

              &.paused {
                background: #9ca3af;
              }
            }

            .task-name {
              font-size: 16px;
              font-weight: 600;
              color: #2d3748;
              margin: 0;
            }
          }

          .task-actions {
            display: flex;
            gap: 8px;

            .action-btn {
              border-radius: 6px;
              font-weight: 500;
              padding: 8px 16px;

              &.view-btn {
                background: #fff;
                border: 1px solid #e5e7eb;
                color: #3b82f6;

                &:hover {
                  background: #eff6ff;
                  border-color: #3b82f6;
                }
              }

              &.edit-btn {
                background: #fff;
                border: 1px solid #e5e7eb;
                color: #6b7280;

                &:hover {
                  background: #f9fafb;
                  border-color: #d1d5db;
                  color: #374151;
                }
              }

              &.pause-btn {
                background: #fff;
                border: 1px solid #e5e7eb;
                color: #f59e0b;

                &:hover {
                  background: #fffbeb;
                  border-color: #fbbf24;
                }
              }

              &.sync-btn {
                background: #6c5ce7;
                border: none;
                color: #fff;

                &:hover {
                  background: #5a4bd1;
                }

                &:disabled {
                  background: #9ca3af;
                  cursor: not-allowed;
                }
              }
            }
          }
        }

        .task-details {
          display: flex;
          gap: 24px;
          margin-bottom: 12px;
          padding-bottom: 12px;
          border-bottom: 1px solid #f3f4f6;

          .detail-item {
            display: flex;
            align-items: center;
            gap: 6px;

            .detail-icon {
              font-size: 14px;
              color: #9ca3af;
            }

            .detail-label {
              font-size: 13px;
              color: #6b7280;
            }

            .detail-value {
              font-size: 13px;
              color: #374151;
              font-weight: 500;
            }
          }
        }

        .sync-info {
          display: flex;
          gap: 24px;

          .info-item {
            .info-label {
              font-size: 12px;
              color: #9ca3af;
            }

            .info-value {
              font-size: 13px;
              color: #6b7280;
              font-weight: 500;
            }
          }
        }
      }
    }
  }
}

.task-detail {
  :deep(.el-descriptions__label) {
    font-weight: 500;
  }
}
</style>
