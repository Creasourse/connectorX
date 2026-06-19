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

          <!-- 同步统计信息 -->
          <div class="sync-stats">
            <div class="stat-group">
              <div class="stat-item cumulative">
                <div class="stat-label">累计同步</div>
                <div class="stat-value">{{ task.syncedCount }}条</div>
              </div>
              <div class="stat-item current" v-if="task.currentSyncData && task.currentSyncData.recordCount > 0">
                <div class="stat-label">本次同步</div>
                <div class="stat-value">
                  <span :class="{
                    'success': task.currentSyncData.failedCount === 0,
                    'partial': task.currentSyncData.failedCount > 0 && task.currentSyncData.successCount > 0,
                    'failed': task.currentSyncData.successCount === 0
                  }">
                    {{ task.currentSyncData.successCount }}/{{ task.currentSyncData.recordCount }}条
                  </span>
                </div>
                <div class="stat-duration">{{ task.currentSyncData.duration }}秒</div>
              </div>
              <div class="stat-item time">
                <div class="stat-label">{{ task.status === "active" ? "最后同步" : "同步状态" }}</div>
                <div class="stat-value">{{ formatSyncTime(task.syncTime) }}</div>
              </div>
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
        <el-descriptions :column="2" border>
          <el-descriptions-item label="任务名称" :span="2">
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
            {{ formatSyncTime(currentTask.syncTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="累计同步记录数">
            <div class="detail-stat-item">
              <el-tag type="info" size="small">{{ currentTask.syncedCount }} 条</el-tag>
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="本次同步记录数" v-if="currentTask.currentSyncData && currentTask.currentSyncData.recordCount > 0" :span="2">
            <div class="sync-result-detail-expanded">
              <div class="result-main">
                <el-tag
                  :type="currentTask.currentSyncData.failedCount === 0 ? 'success' :
                        currentTask.currentSyncData.successCount > 0 ? 'warning' : 'danger'"
                  size="default"
                >
                  {{ currentTask.currentSyncData.successCount }}/{{ currentTask.currentSyncData.recordCount }} 条
                </el-tag>
                <div class="result-details">
                  <span class="detail-item">
                    <i class="el-icon-success">✓</i>
                    成功: {{ currentTask.currentSyncData.successCount }}
                  </span>
                  <span class="detail-item" v-if="currentTask.currentSyncData.failedCount > 0">
                    <i class="el-icon-error">✗</i>
                    失败: {{ currentTask.currentSyncData.failedCount }}
                  </span>
                  <span class="detail-item">
                    <i class="el-icon-time">⏱</i>
                    耗时: {{ currentTask.currentSyncData.duration }}秒
                  </span>
                </div>
              </div>
            </div>
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
  getFeishuDataSyncDetail,
  getFeishuSyncLogPageList
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

// 格式化同步时间
const formatSyncTime = (timeStr: string) => {
  if (!timeStr || timeStr === "未同步") return "未同步";

  try {
    const date = new Date(timeStr);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return "刚刚";
    if (diffMins < 60) return `${diffMins}分钟前`;
    if (diffHours < 24) return `${diffHours}小时前`;
    if (diffDays < 7) return `${diffDays}天前`;

    // 超过7天显示具体日期
    return `${date.getMonth() + 1}/${date.getDate()} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
  } catch (error) {
    return timeStr;
  }
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
      const tasks = (res.data.records || []).map((item: any) => ({
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

      // 为每个任务获取最新的同步记录（本次同步数据）
      for (const task of tasks) {
        try {
          const logParams = {
            pageNum: 1,
            pageSize: 1,
            feishuDataSyncId: task.id
          };
          const logRes = await getFeishuSyncLogPageList(logParams);
          if (logRes.success && logRes.data && logRes.data.records && logRes.data.records.length > 0) {
            const latestLog = logRes.data.records[0];
            task.currentSyncData = {
              recordCount: latestLog.recordCount || 0,
              successCount: latestLog.successCount || 0,
              failedCount: latestLog.failedCount || 0,
              duration: latestLog.duration || 0,
              syncTime: latestLog.startTime || task.syncTime,
              syncStatus: latestLog.syncStatus
            };
          } else {
            task.currentSyncData = {
              recordCount: 0,
              successCount: 0,
              failedCount: 0,
              duration: 0,
              syncTime: task.syncTime,
              syncStatus: task.status
            };
          }
        } catch (error) {
          console.warn(`获取任务${task.id}的最新同步记录失败:`, error);
          task.currentSyncData = {
            recordCount: 0,
            successCount: 0,
            failedCount: 0,
            duration: 0,
            syncTime: task.syncTime,
            syncStatus: task.status
          };
        }
      }

      taskList.value = tasks;
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
  let syncMessage: any = null; // 用于存储消息实例

  try {
    // 显示开始同步提示
    syncMessage = ElMessage({
      message: '正在启动数据同步，请稍候...',
      type: 'info',
      duration: 0, // 不自动关闭
      showClose: false
    });

    const res = await executeFeishuDataSync(task.id);

    // 关闭开始同步提示
    if (syncMessage) {
      syncMessage.close();
    }

    if (res.success && res.data) {
      const result = res.data as any;
      const totalRecords = result.totalRecords || 0;
      const successRecords = result.successRecords || 0;
      const duration = result.duration || 0;
      const status = result.status;

      if (status === 'success') {
        ElMessage.success({
          message: `同步成功！共同步 ${successRecords}/${totalRecords} 条记录，耗时 ${duration}秒`,
          duration: 5000,
          showClose: true
        });
      } else if (status === 'partial_success') {
        ElMessage.warning({
          message: `同步部分完成：${successRecords}/${totalRecords} 条成功，耗时 ${duration}秒`,
          duration: 5000,
          showClose: true
        });
      } else {
        ElMessage.error({
          message: `同步失败：${successRecords}/${totalRecords} 条成功，耗时 ${duration}秒`,
          duration: 5000,
          showClose: true
        });
      }

      // 立即更新任务的本次同步数据（无需等待列表刷新）
      task.currentSyncData = {
        recordCount: totalRecords,
        successCount: successRecords,
        failedCount: (totalRecords - successRecords) || 0,
        duration: duration,
        syncTime: new Date().toISOString(),
        syncStatus: status
      };

      // 注意：累计数据由后端根据增量/全量同步逻辑更新
      // 这里仅更新本次同步数据，准确的累计数据会在loadTaskList()刷新后从后端获取
      if (status === 'success') {
        console.log('同步成功，累计数据已在后端正确更新，即将刷新获取最新数据');
      }

      // 异步刷新任务列表以获取最新状态（包括后端更新的累计数据）
      setTimeout(() => {
        loadTaskList();
      }, 1000);
    } else {
      ElMessage.error({
        message: res.msg || "同步触发失败",
        duration: 5000,
        showClose: true
      });
    }
  } catch (error: any) {
    // 关闭开始同步提示
    if (syncMessage) {
      syncMessage.close();
    }

    // 处理不同类型的错误
    let errorMessage = "同步触发失败";

    if (error.code === 'ECONNABORTED' || error.message?.includes('timeout')) {
      errorMessage = "同步超时，但后台任务可能仍在执行。请稍后刷新查看同步状态。";
    } else if (error.response?.status === 401) {
      errorMessage = "登录已过期，请重新登录";
    } else if (error.response?.status === 500) {
      errorMessage = "服务器内部错误，请稍后重试";
    } else if (error.message) {
      errorMessage = `同步失败：${error.message}`;
    }

    ElMessage.error({
      message: errorMessage,
      duration: 8000,
      showClose: true
    });

    console.error('同步错误详情:', error);

    // 即使出错也尝试刷新列表，可能后端已经完成了部分同步
    setTimeout(() => {
      loadTaskList();
    }, 2000);
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

        .sync-stats {
          margin-top: 16px;
          padding-top: 16px;
          border-top: 1px solid #f3f4f6;

          .stat-group {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 16px;

            .stat-item {
              display: flex;
              flex-direction: column;
              align-items: center;
              justify-content: center;
              padding: 12px;
              background: #f9fafb;
              border-radius: 8px;
              border: 1px solid #e5e7eb;
              transition: all 0.2s;

              &:hover {
                border-color: #d1d5db;
                background: #f3f4f6;
              }

              &.cumulative {
                border-left: 3px solid #6b7280;
              }

              &.current {
                border-left: 3px solid #6c5ce7;

                .stat-value {
                  .success {
                    color: #10b981;
                    font-weight: 600;
                  }

                  .partial {
                    color: #f59e0b;
                    font-weight: 600;
                  }

                  .failed {
                    color: #ef4444;
                    font-weight: 600;
                  }
                }
              }

              &.time {
                border-left: 3px solid #9ca3af;
              }

              .stat-label {
                font-size: 11px;
                color: #6b7280;
                font-weight: 500;
                margin-bottom: 4px;
                text-transform: uppercase;
                letter-spacing: 0.5px;
              }

              .stat-value {
                font-size: 16px;
                font-weight: 600;
                color: #374151;
              }

              .stat-duration {
                font-size: 11px;
                color: #9ca3af;
                margin-top: 2px;
              }
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

  .detail-stat-item {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .sync-result-detail-expanded {
    .result-main {
      display: flex;
      flex-direction: column;
      gap: 8px;

      .result-details {
        display: flex;
        gap: 16px;
        flex-wrap: wrap;

        .detail-item {
          display: flex;
          align-items: center;
          gap: 4px;
          font-size: 13px;
          color: #6b7280;

          i {
            font-style: normal;
            font-size: 14px;
          }

          &.success-item {
            color: #10b981;
          }

          &.error-item {
            color: #ef4444;
          }
        }
      }
    }
  }
}
</style>
