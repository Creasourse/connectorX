<template>
  <div class="basic-info-container">
    <el-card class="config-card">
      <h2 class="config-title">飞书应用基础配置</h2>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-position="top"
        class="config-form"
      >
        <el-form-item label="应用名称" prop="appName">
          <el-input
            v-model="formData.appName"
            placeholder="请输入飞书应用名称"
            clearable
          />
        </el-form-item>

        <el-form-item label="App ID" prop="appId">
          <el-input
            v-model="formData.appId"
            placeholder="请在飞书开放平台获取应用ID"
            clearable
          />
        </el-form-item>

        <el-form-item label="App Secret" prop="appSecret">
          <el-input
            v-model="formData.appSecret"
            type="password"
            placeholder="请在飞书开放平台获取应用密钥"
            clearable
            show-password
          />
        </el-form-item>

        <el-form-item label="企业名称" prop="companyName">
          <el-input
            v-model="formData.companyName"
            placeholder="请输入企业名称"
            clearable
          />
        </el-form-item>

        <el-form-item label="多维表格 App Token" prop="appToken">
          <el-input
            v-model="formData.appToken"
            placeholder="请输入多维表格唯一标识（可选）"
            clearable
          />
        </el-form-item>

        <el-form-item label="应用描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入应用描述信息"
            clearable
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

      <!-- 配置说明 -->
      <div class="permission-info">
        <div class="permission-title">
          <el-icon class="info-icon"><InfoFilled /></el-icon>
          配置说明
        </div>
        <ul class="permission-list">
          <li>访问飞书开放平台创建企业自建应用</li>
          <li>获取 App ID 和 App Secret</li>
          <li>申请多维表格权限：bitable:app、bitable:app:readonly</li>
          <li>配置事件订阅以接收实时数据变更通知</li>
          <li>建议生产环境配置加密密钥和验证令牌</li>
        </ul>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button class="test-btn" @click="handleTest" :loading="testing">
          <el-icon><Connection /></el-icon>
          测试连接
        </el-button>
        <el-button type="primary" class="save-btn" @click="handleSave" :loading="saving">
          <el-icon><Select /></el-icon>
          保存配置
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from "vue";
import type { PropType } from "vue";
import { saveOrUpdateFeishuApp, testFeishuAppConnection } from "@/api/feishu";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import { InfoFilled, Connection, Select } from "@element-plus/icons-vue";
import type { FeishuAppForm } from "../types";

const emit = defineEmits<{
  (e: "update"): void;
}>();

const props = defineProps({
  config: {
    type: Object as PropType<Record<string, any>>,
    default: () => ({})
  }
});

const formRef = ref<FormInstance>();
const testing = ref(false);
const saving = ref(false);

// 表单数据
const formData = reactive<FeishuAppForm>({
  id: undefined,
  appName: "",
  appId: "",
  appSecret: "",
  companyName: "",
  appToken: "",
  description: "",
  isEnabled: 1
});

// 表单验证规则
const formRules: FormRules = {
  appName: [{ required: true, message: "请输入应用名称", trigger: "blur" }],
  appId: [{ required: true, message: "请输入 App ID", trigger: "blur" }],
  appSecret: [{ required: true, message: "请输入 App Secret", trigger: "blur" }],
  companyName: [{ required: true, message: "请输入企业名称", trigger: "blur" }]
};

// 监听 props.config 变化
watch(
  () => props.config,
  newConfig => {
    if (newConfig) {
      Object.assign(formData, {
        id: newConfig.feishuAppId,
        appName: newConfig.appName || "",
        appId: newConfig.appId || "",
        appSecret: newConfig.appSecret || "",
        companyName: newConfig.companyName || "",
        appToken: newConfig.appToken || "",
        description: newConfig.description || "",
        isEnabled: newConfig.isEnabled ?? 1
      });
    }
  },
  { deep: true, immediate: true }
);

// 测试连接
const handleTest = async () => {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  testing.value = true;
  try {
    const res = await testFeishuAppConnection(formData.appId, formData.appSecret);
    if (res.success) {
      ElMessage.success("连接测试成功");
    } else {
      ElMessage.error(res.msg || "连接测试失败");
    }
  } catch (error) {
    ElMessage.error("连接测试失败");
  } finally {
    testing.value = false;
  }
};

// 保存配置
const handleSave = async () => {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  saving.value = true;
  try {
    const params = {
      id: formData.id,
      appName: formData.appName,
      appId: formData.appId,
      appSecret: formData.appSecret,
      companyName: formData.companyName,
      appToken: formData.appToken,
      description: formData.description,
      isEnabled: formData.isEnabled
    };
    const res = await saveOrUpdateFeishuApp(params);
    if (res.success) {
      ElMessage.success("保存成功");
      emit("update");
    } else {
      ElMessage.error(res.msg || "保存失败");
    }
  } catch (error) {
    ElMessage.error("保存失败");
  } finally {
    saving.value = false;
  }
};
</script>

<style lang="scss" scoped>
.basic-info-container {
  width: 100%;
}

.config-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  :deep(.el-card__body) {
    padding: 24px;
  }

  .config-title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 24px 0;
    padding-bottom: 16px;
    border-bottom: 1px solid #e5e7eb;
  }

  .config-form {
    max-width: 600px;

    :deep(.el-form-item__label) {
      font-weight: 500;
      color: #606266;
    }
  }

  .permission-info {
    margin-top: 24px;
    padding: 16px;
    background: #f4f4f5;
    border-radius: 4px;
    border-left: 3px solid #409eff;

    .permission-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 12px;

      .info-icon {
        color: #409eff;
        font-size: 16px;
      }
    }

    .permission-list {
      margin: 0;
      padding-left: 24px;

      li {
        font-size: 13px;
        color: #606266;
        line-height: 1.8;
        position: relative;

        &::before {
          content: "•";
          position: absolute;
          left: -12px;
          color: #909399;
        }
      }
    }
  }

  .action-buttons {
    display: flex;
    gap: 12px;
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid #e5e7eb;

    .el-button {
      min-width: 100px;

      .el-icon {
        margin-right: 4px;
      }
    }
  }
}
</style>
