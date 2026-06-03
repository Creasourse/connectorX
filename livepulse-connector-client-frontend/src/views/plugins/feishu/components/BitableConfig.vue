<template>
  <div class="config-card">
    <h2 class="config-title">多维表格配置</h2>

    <div class="table-toolbar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加多维表格
      </el-button>
      <el-button @click="getConfig">
        <el-icon><Refresh /></el-icon>
        刷新列表
      </el-button>
    </div>

    <!-- 表格列表 -->
    <el-table
      v-loading="loading"
      :data="tableList"
      border
      stripe
      style="width: 100%"
    >
      <el-table-column prop="bitableName" label="多维表格名称" width="200" />
      <el-table-column prop="tableName" label="数据表名称" width="200" />
      <el-table-column prop="syncMode" label="同步模式" width="120">
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
      <el-table-column prop="isEnabled" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.isEnabled ? 'success' : 'info'">
            {{ row.isEnabled ? "启用" : "禁用" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="lastSyncTime" label="最后同步时间" width="180">
        <template #default="{ row }">
          {{ formatTime(row.lastSyncTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button type="success" link @click="handleSync(row)">
            同步
          </el-button>
          <el-button type="danger" link @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑多维表格' : '添加多维表格'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-position="top"
      >
        <el-form-item label="多维表格ID" prop="bitableId">
          <el-input
            v-model="formData.bitableId"
            placeholder="请输入多维表格ID"
            clearable
          />
        </el-form-item>

        <el-form-item label="多维表格名称" prop="bitableName">
          <el-input
            v-model="formData.bitableName"
            placeholder="请输入多维表格名称"
            clearable
          />
        </el-form-item>

        <el-form-item label="数据表ID" prop="tableId">
          <el-input
            v-model="formData.tableId"
            placeholder="请输入数据表ID"
            clearable
          />
        </el-form-item>

        <el-form-item label="数据表名称" prop="tableName">
          <el-input
            v-model="formData.tableName"
            placeholder="请输入数据表名称"
            clearable
          />
        </el-form-item>

        <el-form-item label="数据表URL" prop="tableUrl">
          <el-input
            v-model="formData.tableUrl"
            placeholder="请输入数据表URL"
            clearable
          />
        </el-form-item>

        <el-form-item label="同步模式" prop="syncMode">
          <el-select
            v-model="formData.syncMode"
            placeholder="请选择同步模式"
            style="width: 100%"
          >
            <el-option label="Webhook实时推送" value="webhook" />
            <el-option label="轮询同步" value="polling" />
            <el-option label="混合模式" value="hybrid" />
          </el-select>
        </el-form-item>

        <el-form-item label="是否启用">
          <el-switch
            v-model="formData.isEnabled"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from "vue";
import type { PropType } from "vue";
import {
  getFeishuBitablePageList,
  saveOrUpdateFeishuBitable,
  deleteFeishuBitable,
  syncFeishuBitableData
} from "@/api/feishu";
import {
  ElMessage,
  ElMessageBox,
  type FormInstance,
  type FormRules
} from "element-plus";
import { Plus, Refresh } from "@element-plus/icons-vue";

const formRef = ref<FormInstance>();
const loading = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);

const props = defineProps({
  config: {
    type: Object as PropType<unknown>
  }
});

const emit = defineEmits<{
  (e: "update"): void;
}>();

// 表格数据
const tableList = ref<any[]>([]);
const currentRow = ref<any>(null);

// 表单数据
const formData = reactive({
  id: "",
  bitableId: "",
  bitableName: "",
  tableId: "",
  tableName: "",
  tableUrl: "",
  syncMode: "hybrid",
  isEnabled: 1
});

// 表单验证规则
const formRules: FormRules = {
  bitableId: [{ required: true, message: "请输入多维表格ID", trigger: "blur" }],
  bitableName: [
    { required: true, message: "请输入多维表格名称", trigger: "blur" }
  ],
  tableId: [{ required: true, message: "请输入数据表ID", trigger: "blur" }],
  tableName: [{ required: true, message: "请输入数据表名称", trigger: "blur" }],
  syncMode: [{ required: true, message: "请选择同步模式", trigger: "change" }]
};

// 获取配置列表
const getConfig = async () => {
  if (!props.config?.feishuAppId) {
    ElMessage.warning("请先配置飞书应用基础信息");
    return;
  }

  loading.value = true;
  try {
    const params = {
      feishuAppId: props.config.feishuAppId,
      pageNum: 1,
      pageSize: 100
    };
    const res = await getFeishuBitablePageList(params);
    if (res.success) {
      tableList.value = res.data?.records || [];
    }
  } catch (error) {
    ElMessage.error("获取多维表格配置失败");
  } finally {
    loading.value = false;
  }
};

// 添加
const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, {
    id: "",
    bitableId: "",
    bitableName: "",
    tableId: "",
    tableName: "",
    tableUrl: "",
    syncMode: "hybrid",
    isEnabled: 1
  });
  dialogVisible.value = true;
};

// 编辑
const handleEdit = (row: any) => {
  isEdit.value = true;
  currentRow.value = row;
  Object.assign(formData, row);
  dialogVisible.value = true;
};

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm("确定要删除该多维表格配置吗？", "提示", {
      type: "warning"
    });

    const res = await deleteFeishuBitable(row.id);
    if (res.success) {
      ElMessage.success("删除成功");
      getConfig();
      emit("update");
    } else {
      ElMessage.error(res.message || "删除失败");
    }
  } catch (error) {
    // 用户取消删除
  }
};

// 同步
const handleSync = async (row: any) => {
  loading.value = true;
  try {
    const res = await syncFeishuBitableData(row.id);
    if (res.success) {
      ElMessage.success("同步成功");
      getConfig();
      emit("update");
    } else {
      ElMessage.error(res.message || "同步失败");
    }
  } catch (error) {
    ElMessage.error("同步失败");
  } finally {
    loading.value = false;
  }
};

// 确认
const handleConfirm = async () => {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  try {
    const params = {
      id: isEdit.value ? formData.id : undefined,
      feishuAppId: props.config?.feishuAppId,
      ...formData
    };
    const res = await saveOrUpdateFeishuBitable(params);
    if (res.success) {
      ElMessage.success(isEdit.value ? "更新成功" : "添加成功");
      dialogVisible.value = false;
      getConfig();
      emit("update");
    } else {
      ElMessage.error(res.message || "保存失败");
    }
  } catch (error) {
    ElMessage.error("保存失败");
  }
};

// 工具方法
const getSyncModeLabel = (mode: string) => {
  const map: Record<string, string> = {
    webhook: "Webhook",
    polling: "轮询",
    hybrid: "混合"
  };
  return map[mode] || mode;
};

const getSyncModeType = (mode: string) => {
  const map: Record<string, string> = {
    webhook: "success",
    polling: "warning",
    hybrid: "primary"
  };
  return map[mode] || "";
};

const getSyncStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    success: "成功",
    failed: "失败",
    pending: "待同步",
    processing: "同步中"
  };
  return map[status] || status;
};

const getSyncStatusType = (status: string) => {
  const map: Record<string, string> = {
    success: "success",
    failed: "danger",
    pending: "info",
    processing: "warning"
  };
  return map[status] || "";
};

const formatTime = (time: string) => {
  if (!time) return "-";
  return new Date(time).toLocaleString("zh-CN");
};

// 监听配置变化
watch(
  () => props.config,
  newConfig => {
    if (newConfig?.feishuAppId) {
      getConfig();
    }
  },
  { deep: true, immediate: true }
);
</script>

<style lang="scss" scoped>
.config-card {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  .config-title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 24px 0;
    padding-bottom: 16px;
    border-bottom: 1px solid #e5e7eb;
  }

  .table-toolbar {
    margin-bottom: 16px;
    display: flex;
    gap: 12px;

    .el-icon {
      margin-right: 4px;
    }
  }

  :deep(.el-table) {
    .el-button {
      padding: 4px 8px;
    }
  }
}
</style>
