<template>
  <div class="bitable-manager-container">
    <el-card class="manager-card">
      <!-- 头部操作区 -->
      <div class="header-actions">
        <div class="header-left">
          <el-input
            v-model="searchForm.bitableName"
            placeholder="搜索多维表格名称"
            clearable
            style="width: 240px"
            @clear="loadBitableList"
            @keyup.enter="loadBitableList"
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
            @change="loadBitableList"
          >
            <el-option label="全部" value="" />
            <el-option label="待同步" value="pending" />
            <el-option label="同步中" value="processing" />
            <el-option label="同步成功" value="success" />
            <el-option label="同步失败" value="failed" />
          </el-select>
          <el-select
            v-model="searchForm.isEnabled"
            placeholder="状态"
            clearable
            style="width: 120px"
            @change="loadBitableList"
          >
            <el-option label="全部" value="" />
            <el-option label="启用" :value="true" />
            <el-option label="禁用" :value="false" />
          </el-select>
        </div>
        <div class="header-right">
          <el-button :icon="Refresh" @click="loadBitableList">刷新</el-button>
          <el-button type="primary" :icon="Plus" @click="showAddDialog">
            添加多维表格
          </el-button>
        </div>
      </div>

      <!-- 表格列表 -->
      <el-table
        :data="tableData"
        v-loading="loading"
        stripe
        style="width: 100%; margin-top: 16px"
      >
        <el-table-column prop="bitableName" label="表格名称" min-width="180" />
        <el-table-column prop="appToken" label="App Token" min-width="200">
          <template #default="{ row }">
            <el-text type="info" size="small">{{ row.appToken }}</el-text>
          </template>
        </el-table-column>
        <el-table-column prop="syncMode" label="同步模式" width="100">
          <template #default="{ row }">
            <el-tag size="small" v-if="row.syncMode === 'manual'">手动同步</el-tag>
            <el-tag size="small" type="success" v-else-if="row.syncMode === 'auto'">
              自动同步
            </el-tag>
            <el-tag size="small" type="warning" v-else-if="row.syncMode === 'realtime'">
              实时同步
            </el-tag>
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
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-tooltip
              content="同步所有数据表及其字段数、记录数（数据表较多时可能需要较长时间）"
              placement="top"
            >
              <el-button
                type="primary"
                size="small"
                link
                :icon="Refresh"
                :loading="row._syncing"
                @click="handleSync(row)"
              >
                同步表结构
              </el-button>
            </el-tooltip>
            <el-button
              type="success"
              size="small"
              link
              @click="handleViewTables(row)"
            >
              数据表
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
        @size-change="loadBitableList"
        @current-change="loadBitableList"
      />
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-form-item label="App Token" prop="appToken">
          <el-input
            v-model="formData.appToken"
            placeholder="请输入多维表格的 App Token (如 bascnxxxxx)"
            clearable
          />
          <div class="form-tip">
            从多维表格URL中获取，格式如：https://xxx.feishu.cn/base/bascnxxxxxx
          </div>
        </el-form-item>
        <el-form-item label="表格名称" prop="bitableName">
          <el-input
            v-model="formData.bitableName"
            placeholder="请输入表格名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="表格URL" prop="bitableUrl">
          <el-input
            v-model="formData.bitableUrl"
            placeholder="请输入表格URL（可选）"
            clearable
          />
        </el-form-item>
        <el-form-item label="同步模式" prop="syncMode">
          <el-select v-model="formData.syncMode" style="width: 100%">
            <el-option label="手动同步" value="manual" />
            <el-option label="自动同步" value="auto" />
            <el-option label="实时同步" value="realtime" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否启用">
          <el-switch
            v-model="formData.isEnabled"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">
          保存
        </el-button>
      </template>
    </el-dialog>

    <!-- 数据表列表对话框 -->
    <el-dialog
      v-model="tablesDialogVisible"
      :title="`${currentBitable?.bitableName} - 数据表列表`"
      width="900px"
    >
      <el-table
        :data="dataTableList"
        v-loading="loadingTables"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="tableName" label="数据表名称" min-width="180" />
        <el-table-column prop="tableId" label="Table ID" min-width="200">
          <template #default="{ row }">
            <el-text type="info" size="small">{{ row.tableId }}</el-text>
          </template>
        </el-table-column>
        <el-table-column prop="recordCount" label="记录数" width="100" />
        <el-table-column prop="fieldCount" label="字段数" width="100" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              :loading="row._syncing"
              @click="handleSyncRecords(row)"
            >
              同步表信息
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from "vue";
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from "element-plus";
import { Search, Refresh, Plus, Delete } from "@element-plus/icons-vue";
import {
  getFeishuBitablePageList,
  saveOrUpdateFeishuBitable,
  deleteFeishuBitable,
  syncFeishuBitableData,
  toggleFeishuBitableEnabled,
  getFeishuDataTablePageList,
  syncFeishuDataTableInfo
} from "@/api/feishu";
import type { BitableConfig } from "../types";
import { SYNC_STATUS_MAP } from "../types";

defineOptions({
  name: "BitableManager"
});

const props = defineProps({
  config: {
    type: Object as PropType<Record<string, any>>,
    default: () => ({})
  }
});

// 状态
const loading = ref(false);
const saving = ref(false);
const dialogVisible = ref(false);
const tablesDialogVisible = ref(false);
const loadingTables = ref(false);
const dialogTitle = computed(() => (formData.id ? "编辑多维表格" : "添加多维表格"));

// 表格数据
const tableData = ref<(BitableConfig & { _syncing?: boolean; _toggling?: boolean })[]>([]);

// 数据表列表
const dataTableList = ref<any[]>([]);
const currentBitable = ref<BitableConfig | null>(null);

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
});

// 搜索表单
const searchForm = reactive({
  bitableName: "",
  syncStatus: "",
  isEnabled: ""
});

// 表单
const formRef = ref<FormInstance>();
const formData = reactive<Partial<BitableConfig>>({
  appToken: "",
  bitableName: "",
  bitableUrl: "",
  revision: undefined,
  syncMode: "manual",
  isEnabled: 1
});

// 表单验证规则
const formRules: FormRules = {
  appToken: [{ required: true, message: "请输入 App Token", trigger: "blur" }],
  bitableName: [{ required: true, message: "请输入表格名称", trigger: "blur" }]
};

// 获取状态标签类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, any> = {
    pending: "info",
    processing: "primary",
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

// 加载数据
const loadBitableList = async () => {
  if (!props.config.feishuAppId) {
    ElMessage.warning("请先配置飞书应用");
    return;
  }

  loading.value = true;
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      feishuAppId: props.config.feishuAppId,
      bitableName: searchForm.bitableName || undefined,
      syncStatus: searchForm.syncStatus || undefined,
      isEnabled:
        searchForm.isEnabled !== "" ? (searchForm.isEnabled as unknown as boolean) : undefined
    };
    const res = await getFeishuBitablePageList(params);
    if (res.success && res.data) {
      tableData.value = res.data.records || [];
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

// 显示添加对话框
const showAddDialog = () => {
  formData.id = undefined;
  formData.appToken = "";
  formData.bitableName = "";
  formData.bitableUrl = "";
  formData.revision = undefined;
  formData.syncMode = "manual";
  formData.isEnabled = 1;
  dialogVisible.value = true;
};

// 编辑
const handleEdit = (row: BitableConfig) => {
  Object.assign(formData, row);
  dialogVisible.value = true;
};

// 保存
const handleSave = async () => {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  saving.value = true;
  try {
    const params = {
      ...formData,
      feishuAppId: props.config.feishuAppId
    };
    const res = await saveOrUpdateFeishuBitable(params);
    if (res.success) {
      ElMessage.success("保存成功");
      dialogVisible.value = false;
      loadBitableList();
    } else {
      ElMessage.error(res.msg || "保存失败");
    }
  } catch (error) {
    ElMessage.error("保存失败");
  } finally {
    saving.value = false;
  }
};

// 同步
const handleSync = async (row: BitableConfig & { _syncing?: boolean }) => {
  if (!row.id) return;

  row._syncing = true;
  try {
    const res = await syncFeishuBitableData(row.id);
    if (res.success) {
      ElMessage.success("同步成功");
      loadBitableList();
    } else {
      ElMessage.error(res.msg || "同步失败");
    }
  } catch (error: any) {
    // 判断是否为超时错误
    if (error.code === "ECONNABORTED" || error.message?.includes("timeout")) {
      ElMessage.warning("同步任务已提交，请稍后查看结果（数据表较多时可能需要较长时间）");
    } else {
      ElMessage.error(error.msg || error.message || "同步失败");
    }
  } finally {
    row._syncing = false;
  }
};

// 查看数据表
const handleViewTables = async (row: BitableConfig) => {
  if (!row.id) return;

  currentBitable.value = row;
  tablesDialogVisible.value = true;
  await loadDataTableList(row.id);
};

// 加载数据表列表
const loadDataTableList = async (bitableId: number) => {
  loadingTables.value = true;
  try {
    const res = await getFeishuDataTablePageList({
      pageNum: 1,
      pageSize: 100,
      feishuBitableId: bitableId,
      isEnabled: 1
    });
    if (res.success && res.data) {
      dataTableList.value = (res.data.records || []).map((t: any) => ({
        ...t,
        _syncing: false
      }));
    } else {
      ElMessage.error(res.msg || "加载数据表列表失败");
    }
  } catch (error) {
    ElMessage.error("加载数据表列表失败");
    console.error(error);
  } finally {
    loadingTables.value = false;
  }
};

// 同步表信息
const handleSyncRecords = async (row: any) => {
  if (!row.id) return;

  row._syncing = true;
  try {
    const res = await syncFeishuDataTableInfo(row.id);
    if (res.success && res.data) {
      const result = res.data as any;
      const fieldCount = result.fieldCount || 0;
      const recordCount = result.recordCount || 0;

      // 更新列表数据
      await loadDataTableList(currentBitable.value?.id);

      ElMessage.success(`同步成功！字段数：${fieldCount}，记录数：${recordCount}`);
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
const handleToggleEnabled = async (row: BitableConfig & { _toggling?: boolean }) => {
  if (!row.id) return;

  row._toggling = true;
  try {
    const res = await toggleFeishuBitableEnabled(row.id);
    if (res.success) {
      ElMessage.success("操作成功");
    } else {
      row.isEnabled = !row.isEnabled;
      ElMessage.error(res.msg || "操作失败");
    }
  } catch (error) {
    row.isEnabled = !row.isEnabled;
    ElMessage.error("操作失败");
  } finally {
    row._toggling = false;
  }
};

// 删除
const handleDelete = async (row: BitableConfig) => {
  if (!row.id) return;

  try {
    await ElMessageBox.confirm(`确定要删除多维表格"${row.bitableName}"吗？`, "提示", {
      type: "warning"
    });
    const res = await deleteFeishuBitable(row.id);
    if (res.success) {
      ElMessage.success("删除成功");
      loadBitableList();
    } else {
      ElMessage.error(res.msg || "删除失败");
    }
  } catch (error: any) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields();
};

onMounted(() => {
  loadBitableList();
});
</script>

<style lang="scss" scoped>
.bitable-manager-container {
  width: 100%;
}

.manager-card {
  :deep(.el-card__body) {
    padding: 20px;
  }
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

.form-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
  margin-top: 4px;
}
</style>
