<template>
  <div class="config-card">
    <h2 class="config-title">基础信息</h2>

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

      <el-form-item label="连接器类型" prop="connectorType">
        <el-input
          v-model="formData.connectorType"
          disabled
          placeholder="Salesforce"
        />
      </el-form-item>

      <el-form-item label="描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="4"
          placeholder="请输入描述信息"
          clearable
        />
      </el-form-item>

      <!-- 开关配置 -->
      <div class="switch-config">
        <div class="switch-item">
          <span class="switch-label">启用API同步</span>
          <el-switch
            v-model="formData.enableApiSync"
            active-color="#1890ff"
            inactive-color="#d9d9d9"
          />
        </div>

        <div class="switch-item">
          <span class="switch-label">启用Webhook订阅</span>
          <el-switch
            v-model="formData.enableWebhook"
            active-color="#1890ff"
            inactive-color="#d9d9d9"
          />
        </div>

        <div class="switch-item">
          <span class="switch-label">启用自动重连</span>
          <el-switch
            v-model="formData.enableAutoReconnect"
            active-color="#1890ff"
            inactive-color="#d9d9d9"
          />
        </div>
      </div>
    </el-form>

    <!-- 配置说明 -->
    <div class="permission-info">
      <div class="permission-title">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
        Salesforce 连接器配置说明
      </div>
      <ul class="permission-list">
        <li>连接器名称将用于标识此连接器，建议使用有意义的名称</li>
        <li>连接器类型为固定值，不可修改</li>
        <li>描述信息可以帮助您更好地理解连接器的用途</li>
        <li>启用相应功能开关后，连接器将支持对应的数据同步和事件订阅功能</li>
      </ul>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button class="test-btn" @click="handleTest">测试连接</el-button>
      <el-button type="primary" class="save-btn" @click="handleSave"
        >保存</el-button
      >
    </div>
  </div>
</template>

<script setup lang="ts">
import { type PropType, ref, reactive, watch } from "vue";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import { InfoFilled } from "@element-plus/icons-vue";

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
  connectorName: "Salesforce系统对接",
  connectorType: "Salesforce",
  description: "",
  enableApiSync: false,
  enableWebhook: false,
  enableAutoReconnect: true
});

// 监听 props.config 变化
watch(
  () => props.config,
  (newVal: any) => {
    if (newVal) {
      formData.connectorName = newVal.connectorName || "Salesforce系统对接";
      formData.connectorType = newVal.connectorType || "Salesforce";
      formData.description = newVal.description || "";
      formData.enableApiSync = newVal.enableApiSync ?? false;
      formData.enableWebhook = newVal.enableWebhook ?? false;
      formData.enableAutoReconnect = newVal.enableAutoReconnect ?? true;
    }
  },
  { immediate: true, deep: true }
);

// 表单验证规则
const formRules: FormRules = {
  connectorName: [
    { required: true, message: "请输入连接器名称", trigger: "blur" }
  ]
};

// 测试连接
const handleTest = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    // TODO: 调用测试连接接口
    ElMessage.success("Salesforce连接测试成功");
  } catch (error) {
    ElMessage.error("请先完善必填项");
  }
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

    .switch-config {
      margin-top: 24px;

      .switch-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 16px;
        background-color: #fafafa;
        border-radius: 4px;
        margin-bottom: 12px;

        &:last-child {
          margin-bottom: 0;
        }

        .switch-label {
          font-size: 14px;
          color: #333;
          font-weight: 500;
        }
      }
    }
  }

  .permission-info {
    margin-top: 24px;
    padding: 16px;
    background-color: #f6ffed;
    border: 1px solid #b7eb8f;
    border-radius: 4px;

    .permission-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      font-weight: 600;
      color: #52c41a;
      margin-bottom: 12px;

      .info-icon {
        font-size: 16px;
      }
    }

    .permission-list {
      margin: 0;
      padding-left: 24px;

      li {
        font-size: 13px;
        color: #52c41a;
        line-height: 1.8;
        margin-bottom: 4px;

        &:last-child {
          margin-bottom: 0;
        }
      }
    }
  }

  .action-buttons {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 32px;

    .test-btn {
      width: 100px;
      border: 1px solid #1890ff;
      color: #1890ff;
      background-color: #fff;

      &:hover {
        background-color: #e6f7ff;
      }
    }

    .save-btn {
      width: 80px;
      background-color: #1890ff;

      &:hover {
        background-color: #40a9ff;
      }
    }
  }
}
</style>
