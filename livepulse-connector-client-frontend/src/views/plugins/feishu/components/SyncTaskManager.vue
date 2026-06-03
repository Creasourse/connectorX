<template>
  <div class="sync-task-manager">
    <!-- 头部操作区 -->
    <div class="header-actions">
      <div class="header-left">
        <el-input
          v-model="searchForm.syncName"
          placeholder="搜索任务名称"
          clearable
          style="width: 240px"
          @clear="loadTaskList"
          @keyup.enter="loadTaskList"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-model="searchForm.syncStatus"
          placeholder="同步状态"
          clearable
          style="width: 140px"
          @change="loadTaskList"
        >
          <el-option label="全部" value="" />
          <el-option label="待同步" value="pending" />
          <el-option label="同步中" value="running" />
          <el-option label="同步成功" value="success" />
          <el-option label="同步失败" value="failed" />
        </el-select>
        <el-select
          v-model="searchForm.isEnabled"
          placeholder="状态"
          clearable
          style="width: 120px"
          @change="loadTaskList"
        >
          <el-option label="全部" value="" />
          <el-option label="启用" :value="true" />
          <el-option label="禁用" :value="false" />
        </el-select>
      </div>
      <div class="header-right">
        <el-button :icon="Refresh" @click="loadTaskList">刷新</el-button>
        <el-button type="primary" :icon="Plus" @click="handleAdd">
          添加同步任务
        </el-button>
      </div>
    </div>

    <!-- 任务列表 -->
    <el-table
      :data="taskList"
      v-loading="loading"
      stripe
      style="width: 100%; margin-top: 16px"
    >
      <el-table-column prop="syncName" label="任务名称" min-width="180" />
      <el-table-column prop="tableName" label="目标数据表" min-width="200">
        <template #default="{ row }">
          <el-text type="info" size="small">{{ row.tableName || '-' }}</el-text>
        </template>
      </el-table-column>
      <el-table-column prop="syncDirection" label="同步方向" width="120">
        <template #default="{ row }">
          <el-tag size="small" v-if="row.syncDirection === 'pull'">拉取</el-tag>
          <el-tag size="small" type="success" v-else-if="row.syncDirection === 'push'">
            推送
          </el-tag>
          <el-tag size="small" type="warning" v-else-if="row.syncDirection === 'bidirectional'">
            双向
          </el-tag>
          <el-text v-else type="info" size="small">-</el-text>
        </template>
      </el-table-column>
      <el-table-column prop="syncMode" label="同步模式" width="100">
        <template #default="{ row }">
          <el-tag size="small" type="info" v-if="row.syncMode === 'full'">全量</el-tag>
          <el-tag size="small" type="success" v-else-if="row.syncMode === 'incremental'">
            增量
          </el-tag>
          <el-text v-else type="info" size="small">-</el-text>
        </template>
      </el-table-column>
      <el-table-column prop="syncStatus" label="同步状态" width="100">
        <template #default="{ row }">
          <el-tag
            size="small"
            :type="getStatusType(row.syncStatus)"
            v-if="row.syncStatus"
          >
            {{ SYNC_STATUS_MAP[row.syncStatus] || row.syncStatus }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="lastSyncTime" label="最后同步时间" width="160">
        <template #default="{ row }">
          <span v-if="row.lastSyncTime">{{ formatTime(row.lastSyncTime) }}</span>
          <el-text v-else type="info">未同步</el-text>
        </template>
      </el-table-column>
      <el-table-column prop="isEnabled" label="状态" width="80">
        <template #default="{ row }">
          <el-switch
            v-model="row.isEnabled"
            :active-value="1"
            :inactive-value="0"
            @change="handleToggleEnabled(row)"
            :loading="row._toggling"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button
            type="primary"
            size="small"
            link
            :icon="Refresh"
            :loading="row._syncing"
            @click="handleSync(row)"
            :disabled="row.isEnabled === 0"
          >
            立即同步
          </el-button>
          <el-button type="primary" size="small" link @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button
            type="danger"
            size="small"
            link
            :icon="Delete"
            @click="handleDelete(row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="pagination.pageNum"
      v-model:page-size="pagination.pageSize"
      :page-sizes="[10, 20, 50, 100]"
      :total="pagination.total"
      layout="total, sizes, prev, pager, next, jumper"
      style="margin-top: 16px; justify-content: flex-end"
      @size-change="loadTaskList"
      @current-change="loadTaskList"
    />

    <!-- 添加/编辑对话框 -->
    <CreateSyncTaskDialog
      v-model="dialogVisible"
      :task-id="editingTaskId"
      @save="handleTaskSaved"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, Refresh, Plus, Delete } from "@element-plus/icons-vue";
import {
  getFeishuDataSyncPageList,
  deleteFeishuDataSync,
  executeFeishuDataSync,
  toggleFeishuDataSyncEnabled
} from "@/api/feishu";
import CreateSyncTaskDialog from "./CreateSyncTaskDialog.vue";

defineOptions({
  name: "SyncTaskManager"
});

const SYNC_STATUS_MAP: Record<string, string> = {
  pending: "待同步",
  running: "同步中",
  success: "成功",
  failed: "失败",
  disabled: "已禁用"
};

// 状态
const loading = ref(false);
const dialogVisible = ref(false);
const editingTaskId = ref<number | undefined>(undefined);

// 任务列表
const taskList = ref<any[]>([]);

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
});

// 搜索表单
const searchForm = reactive({
  syncName: "",
  syncStatus: "",
  isEnabled: ""
});

// 获取状态标签类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, any> = {
    pending: "info",
    running: "primary",
    success: "success",
    failed: "danger",
    disabled: "info"
  };
  return typeMap[status] || "";
};

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return "";
  return new Date(time).toLocaleString("zh-CN");
};

// 加载任务列表
const loadTaskList = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      syncName: searchForm.syncName || undefined,
      syncStatus: searchForm.syncStatus || undefined,
      isEnabled:
        searchForm.isEnabled !== "" ? (searchForm.isEnabled as unknown as number) : undefined
    };
    const res = await getFeishuDataSyncPageList(params);
    if (res.success && res.data) {
      taskList.value = (res.data.records || []).map((t: any) => ({
        ...t,
        _syncing: false,
        _toggling: false
      }));
      pagination.total = res.data.total || 0;
    } else {
      ElMessage.error(res.msg || "加载数据失败");
    }
  } catch (error) {
    ElMessage.error("加载数据失败");
    console.error(error);
  } finally {
    loading.value = false;
  }
};

// 添加任务
const handleAdd = () => {
  editingTaskId.value = undefined;
  dialogVisible.value = true;
};

// 编辑任务
const handleEdit = (row: any) => {
  editingTaskId.value = row.id;
  dialogVisible.value = true;
};

// 任务保存成功回调
const handleTaskSaved = () => {
  loadTaskList();
};

// 同步
const handleSync = async (row: any) => {
  if (!row.id) return;

  row._syncing = true;
  try {
    const res = await executeFeishuDataSync(row.id);
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
      loadTaskList();
    } else {
      ElMessage.error(res.msg || "同步失败");
    }
  } catch (error) {
    ElMessage.error("同步失败");
    console.error(error);
  } finally {
    row._syncing = false;
  }
};

// 切换启用状态
const handleToggleEnabled = async (row: any) => {
  if (!row.id) return;

  row._toggling = true;
  try {
    const res = await toggleFeishuDataSyncEnabled(row.id);
    if (res.success) {
      ElMessage.success("操作成功");
    } else {
      row.isEnabled = !row.isEnabled;
      ElMessage.error(res.msg || "操作失败");
    }
  } catch (error) {
    row.isEnabled = !row.isEnabled;
    ElMessage.error("操作失败");
    console.error(error);
  } finally {
    row._toggling = false;
  }
};

// 删除
const handleDelete = async (row: any) => {
  if (!row.id) return;

  try {
    await ElMessageBox.confirm(`确定要删除同步任务"${row.syncName}"吗？`, "提示", {
      type: "warning"
    });
    const res = await deleteFeishuDataSync(row.id);
    if (res.success) {
      ElMessage.success("删除成功");
      loadTaskList();
    } else {
      ElMessage.error(res.msg || "删除失败");
    }
  } catch (error: any) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

onMounted(() => {
  loadTaskList();
});
</script>

<style lang="scss" scoped>
.sync-task-manager {
  width: 100%;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .header-left {
    display: flex;
    gap: 12px;
  }

  .header-right {
    display: flex;
    gap: 12px;
  }
}
</style>
