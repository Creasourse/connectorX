<template>
  <div class="config-card">
    <h2 class="config-title">Google Ads 连接器</h2>

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

      <el-form-item label="Developer Token" prop="developerToken">
        <el-input
          v-model="formData.developerToken"
          placeholder="请输入Developer Token"
          clearable
        />
      </el-form-item>

      <el-form-item label="Client ID" prop="clientId">
        <el-input
          v-model="formData.clientId"
          placeholder="请输入Client ID"
          clearable
        />
      </el-form-item>

      <el-form-item label="Client Secret" prop="clientSecret">
        <el-input
          v-model="formData.clientSecret"
          type="password"
          placeholder="请输入Client Secret"
          show-password
        />
      </el-form-item>

      <el-form-item label="Refresh Token" prop="refreshToken">
        <el-input
          v-model="formData.refreshToken"
          type="textarea"
          :rows="3"
          placeholder="请输入Refresh Token"
          show-password
        />
      </el-form-item>

      <el-form-item label="Customer ID" prop="customerId">
        <el-input
          v-model="formData.customerId"
          placeholder="请输入Customer ID (格式: 123-456-7890)"
          clearable
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

      <el-form-item>
        <el-button type="primary" @click="handleAuthorize">OAuth授权</el-button>
        <el-button @click="handleTestConnection">测试连接</el-button>
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

const formData = reactive({
  connectorName: "",
  connectorId: "",
  developerToken: "",
  clientId: "",
  clientSecret: "",
  refreshToken: "",
  customerId: "",
  description: ""
});

watch(
  () => props.config,
  (newVal: any) => {
    if (newVal) {
      formData.connectorName = newVal.connectorName || "";
      formData.connectorId = newVal.connectorId || generateConnectorId();
      formData.developerToken = newVal.developerToken || "";
      formData.clientId = newVal.clientId || "";
      formData.clientSecret = newVal.clientSecret || "";
      formData.refreshToken = newVal.refreshToken || "";
      formData.customerId = newVal.customerId || "";
      formData.description = newVal.description || "";
    } else {
      formData.connectorId = generateConnectorId();
    }
  },
  { immediate: true, deep: true }
);

const generateConnectorId = () => {
  return "googleads_" + Date.now() + "_" + Math.random().toString(36).substr(2, 9);
};

const formRules: FormRules = {
  connectorName: [{ required: true, message: "请输入连接器名称", trigger: "blur" }],
  developerToken: [{ required: true, message: "请输入Developer Token", trigger: "blur" }],
  clientId: [{ required: true, message: "请输入Client ID", trigger: "blur" }],
  clientSecret: [{ required: true, message: "请输入Client Secret", trigger: "blur" }],
  refreshToken: [{ required: true, message: "请输入Refresh Token", trigger: "blur" }],
  customerId: [
    { required: true, message: "请输入Customer ID", trigger: "blur" },
    { pattern: /^\d{3}-\d{3}-\d{4}$/, message: "Customer ID格式为: 123-456-7890", trigger: "blur" }
  ]
};

const handleAuthorize = () => {
  ElMessage.info("OAuth授权功能待实现");
};

const handleTestConnection = () => {
  ElMessage.info("测试连接功能待实现");
};

const handleCancel = () => {
  formRef.value?.resetFields();
  ElMessage.info("已取消");
};

const handleSave = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
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
