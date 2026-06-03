<template>
  <div class="config-card">
    <h2 class="config-title">字段映射配置</h2>

    <div class="table-selector">
      <el-select
        v-model="selectedBitableId"
        placeholder="请选择多维表格"
        style="width: 300px"
        @change="handleBitableChange"
      >
        <el-option
          v-for="item in bitableList"
          :key="item.id"
          :label="item.bitableName"
          :value="item.id"
        />
      </el-select>
      <el-button type="primary" @click="handleAddMapping" :disabled="!selectedBitableId">
        <el-icon><Plus /></el-icon>
        添加映射
      </el-button>
    </div>

    <!-- 映射列表 -->
    <el-table
      :data="mappingList"
      v-loading="loading"
      border
      stripe
      style="width: 100%; margin-top: 16px"
    >
      <el-table-column prop="feishuFieldName" label="飞书字段名称" width="180" />
      <el-table-column prop="feishuFieldId" label="飞书字段ID" width="180" />
      <el-table-column prop="targetFieldName" label="目标字段名称" width="180" />
      <el-table-column prop="targetFieldType" label="目标字段类型" width="140">
        <template #default="{ row }">
          <el-tag>{{ getFieldTypeLabel(row.targetFieldType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="defaultValue" label="默认值" width="150" />
      <el-table-column prop="isRequired" label="是否必填" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isRequired ? 'danger' : 'info'">
            {{ row.isRequired ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isEnabled" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.isEnabled ? 'success' : 'info'">
            {{ row.isEnabled ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">
            编辑
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
      :title="isEdit ? '编辑字段映射' : '添加字段映射'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-position="top"
      >
        <el-form-item label="飞书字段名称" prop="feishuFieldName">
          <el-input
            v-model="formData.feishuFieldName"
            placeholder="请输入飞书字段名称"
            clearable
          />
        </el-form-item>

        <el-form-item label="飞书字段ID" prop="feishuFieldId">
          <el-input
            v-model="formData.feishuFieldId"
            placeholder="请输入飞书字段ID"
            clearable
          />
        </el-form-item>

        <el-form-item label="目标字段名称" prop="targetFieldName">
          <el-input
            v-model="formData.targetFieldName"
            placeholder="请输入目标字段名称"
            clearable
          />
        </el-form-item>

        <el-form-item label="目标字段类型" prop="targetFieldType">
          <el-select
            v-model="formData.targetFieldType"
            placeholder="请选择目标字段类型"
            style="width: 100%"
          >
            <el-option label="文本" value="text" />
            <el-option label="数字" value="number" />
            <el-option label="日期" value="date" />
            <el-option label="布尔" value="boolean" />
            <el-option label="JSON" value="json" />
          </el-select>
        </el-form-item>

        <el-form-item label="默认值" prop="defaultValue">
          <el-input
            v-model="formData.defaultValue"
            placeholder="请输入默认值"
            clearable
          />
        </el-form-item>

        <el-form-item label="是否必填">
          <el-switch
            v-model="formData.isRequired"
            active-text="必填"
            inactive-text="非必填"
          />
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
import { ref, reactive, watch } from "vue";
import type { PropType } from "vue";
import {
  getFeishuFieldMappingList,
  saveOrUpdateFeishuFieldMapping,
  deleteFeishuFieldMapping,
  getFeishuBitablePageList
} from "@/api/feishu";
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from "element-plus";
import { Plus } from "@element-plus/icons-vue";

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

// 多维表格列表
const bitableList = ref<any[]>([]);
const selectedBitableId = ref<string | number>("");

// 字段映射列表
const mappingList = ref<any[]>([]);
const currentRow = ref<any>(null);

// 表单数据
const formData = reactive({
  id: "",
  feishuFieldName: "",
  feishuFieldId: "",
  targetFieldName: "",
  targetFieldType: "text",
  defaultValue: "",
  isRequired: false,
  isEnabled: 1
});

// 表单验证规则
const formRules: FormRules = {
  feishuFieldName: [
    { required: true, message: "请输入飞书字段名称", trigger: "blur" }
  ],
  feishuFieldId: [
    { required: true, message: "请输入飞书字段ID", trigger: "blur" }
  ],
  targetFieldName: [
    { required: true, message: "请输入目标字段名称", trigger: "blur" }
  ],
  targetFieldType: [
    { required: true, message: "请选择目标字段类型", trigger: "change" }
  ]
};

// 获取多维表格列表
const getBitableList = async () => {
  if (!props.config?.feishuAppId) {
    return;
  }

  try {
    const params = {
      feishuAppId: props.config.feishuAppId,
      pageNum: 1,
      pageSize: 100
    };
    const res = await getFeishuBitablePageList(params);
    if (res.success) {
      bitableList.value = res.data?.records || [];
      if (bitableList.value.length > 0 && !selectedBitableId.value) {
        selectedBitableId.value = bitableList.value[0].id;
        getMappingList();
      }
    }
  } catch (error) {
    console.error("获取多维表格列表失败:", error);
  }
};

// 获取字段映射列表
const getMappingList = async () => {
  if (!selectedBitableId.value) {
    ElMessage.warning("请先选择多维表格");
    return;
  }

  loading.value = true;
  try {
    const res = await getFeishuFieldMappingList(selectedBitableId.value);
    if (res.success) {
      mappingList.value = res.data || [];
    }
  } catch (error) {
    ElMessage.error("获取字段映射列表失败");
  } finally {
    loading.value = false;
  }
};

// 切换多维表格
const handleBitableChange = () => {
  getMappingList();
};

// 添加映射
const handleAddMapping = () => {
  isEdit.value = false;
  Object.assign(formData, {
    id: "",
    feishuFieldName: "",
    feishuFieldId: "",
    targetFieldName: "",
    targetFieldType: "text",
    defaultValue: "",
    isRequired: false,
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
    await ElMessageBox.confirm("确定要删除该字段映射吗？", "提示", {
      type: "warning"
    });

    const res = await deleteFeishuFieldMapping(row.id);
    if (res.success) {
      ElMessage.success("删除成功");
      getMappingList();
      emit("update");
    } else {
      ElMessage.error(res.message || "删除失败");
    }
  } catch (error) {
    // 用户取消删除
  }
};

// 确认
const handleConfirm = async () => {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  try {
    const params = {
      id: isEdit.value ? formData.id : undefined,
      feishuBitableId: selectedBitableId.value,
      ...formData
    };
    const res = await saveOrUpdateFeishuFieldMapping(params);
    if (res.success) {
      ElMessage.success(isEdit.value ? "更新成功" : "添加成功");
      dialogVisible.value = false;
      getMappingList();
      emit("update");
    } else {
      ElMessage.error(res.message || "保存失败");
    }
  } catch (error) {
    ElMessage.error("保存失败");
  }
};

// 工具方法
const getFieldTypeLabel = (type: string) => {
  const map: Record<string, string> = {
    text: "文本",
    number: "数字",
    date: "日期",
    boolean: "布尔",
    json: "JSON"
  };
  return map[type] || type;
};

// 监听配置变化
watch(
  () => props.config,
  (newConfig) => {
    if (newConfig?.feishuAppId) {
      getBitableList();
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

  .table-selector {
    display: flex;
    gap: 12px;
    align-items: center;

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
