<template>
  <div class="config-card">
    <h2 class="config-title">TikTok 连接器</h2>

    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-position="top"
      class="config-form"
    >
      <el-form-item label="连接器名称" prop="connectorName">
        <el-input
          v-model="formData.connectorName"
          placeholder="请输入连接器名称"
          clearable
        />
      </el-form-item>

      <el-form-item label="连接器ID" prop="connectorId">
        <el-input
          v-model="formData.connectorId"
          placeholder="系统自动生成"
          disabled
        />
      </el-form-item>

      <el-form-item label="App Key" prop="appKey">
        <el-input
          v-model="formData.appKey"
          placeholder="请输入App Key"
          clearable
        />
      </el-form-item>

      <el-form-item label="App Secret" prop="appSecret">
        <el-input
          v-model="formData.appSecret"
          type="password"
          placeholder="请输入App Secret"
          show-password
        />
      </el-form-item>

      <el-form-item label="描述">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="4"
          placeholder="请输入描述信息"
        />
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSave">保存</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { type PropType, ref, reactive, watch } from "vue";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";

const formRef = ref<FormInstance>();

const props = defineProps({
  config: {
    type: Object as PropType<unknown>
  }
});

const emit = defineEmits<{
  (e: "update"): void;
}>();

// 表单数据
const formData = reactive({
  connectorName: "",
  connectorId: "",
  appKey: "",
  appSecret: "",
  description: ""
});

// 监听 props.config 变化
watch(
  () => props.config,
  (newVal: any) => {
    if (newVal) {
      formData.connectorName = newVal.connectorName || "";
      formData.connectorId = newVal.connectorId || generateConnectorId();
      formData.appKey = newVal.appKey || "";
      formData.appSecret = newVal.appSecret || "";
      formData.description = newVal.description || "";
    } else {
      formData.connectorId = generateConnectorId();
    }
  },
  { immediate: true, deep: true }
);

// 生成连接器ID
const generateConnectorId = () => {
  return "tiktok_" + Date.now() + "_" + Math.random().toString(36).substr(2, 9);
};

// 表单验证规则
const formRules: FormRules = {
  connectorName: [{ required: true, message: "请输入连接器名称", trigger: "blur" }],
  appKey: [{ required: true, message: "请输入App Key", trigger: "blur" }],
  appSecret: [{ required: true, message: "请输入App Secret", trigger: "blur" }]
};

// 取消
const handleCancel = () => {
  formRef.value?.resetFields();
  ElMessage.info("已取消");
};

// 保存配置
const handleSave = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    // TODO: 调用保存接口
    ElMessage.success("保存成功");
    emit("update");
  } catch (error) {
    ElMessage.error("请先完善必填项");
  }
};
</script>

<style scoped lang="scss">
.config-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

  .config-title {
    margin: 0 0 24px;
    font-size: 16px;
    font-weight: 600;
    color: #333;
  }

  .config-form {
    max-width: 600px;

    :deep(.el-form-item__label) {
      font-size: 14px;
      color: #333;
      font-weight: 500;
    }

    :deep(.el-input__wrapper) {
      background-color: #f5f5f5;
      border: none;
      box-shadow: none;
      padding: 8px 12px;

      &.is-focus {
        background-color: #e6f7ff;
      }

      &.is-disabled {
        background-color: #f5f5f5;
        opacity: 0.6;
      }
    }

    :deep(.el-input__inner) {
      color: #333;
    }

    :deep(.el-textarea__inner) {
      background-color: #f5f5f5;
      border: none;
      box-shadow: none;
      padding: 8px 12px;

      &:focus {
        background-color: #e6f7ff;
      }
    }
  }

  .action-buttons {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 32px;

    .el-button {
      min-width: 80px;
    }
  }
}
</style>
