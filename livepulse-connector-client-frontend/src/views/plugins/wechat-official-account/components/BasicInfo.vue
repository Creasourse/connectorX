<template>
  <div class="config-card">
    <h2 class="config-title">插件基础配置</h2>

    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-position="top"
      class="config-form"
    >
      <el-form-item label="连接器名称">
        <el-input
          :value="wechatConnectorName"
          disabled
          placeholder="微信服务号连接器"
        />
      </el-form-item>

      <el-form-item label="连接器名称" prop="displayName">
        <el-input
          v-model="formData.displayName"
          placeholder="请填写连接器名称"
          clearable
        />
      </el-form-item>

      <el-form-item label="AppID" prop="appId">
        <el-input
          v-model="formData.appId"
          placeholder="请输入AppID"
          clearable
        />
      </el-form-item>

      <el-form-item label="AppSecret" prop="appSecret">
        <el-input
          v-model="formData.appSecret"
          type="password"
          placeholder="请输入AppSecret"
          clearable
          show-password
        />
      </el-form-item>

      <el-form-item label="Token" prop="token">
        <el-input
          v-model="formData.token"
          placeholder="请输入Token"
          clearable
        />
      </el-form-item>

      <el-form-item label="EncodingAESKey" prop="encodingAESKey">
        <el-input
          v-model="formData.encodingAESKey"
          placeholder="请输入EncodingAESKey"
          clearable
        />
      </el-form-item>

      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="formData.remark"
          type="textarea"
          :rows="4"
          placeholder="请输入备注信息"
          clearable
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
        <li>AppID 和 AppSecret 请在微信公众平台后台获取</li>
        <li>Token 和 EncodingAESKey 用于消息加密，可选填</li>
        <li>请妥善保管 AppSecret 信息，泄露可能导致数据安全问题</li>
        <li>配置完成后，请点击「测试」验证连接是否正常</li>
      </ul>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button class="test-btn" @click="handleTest">测试</el-button>
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

// 连接器名称（固定）
const wechatConnectorName = "微信服务号连接器";

// 表单数据
const formData = reactive({
  connectorName: "微信服务号连接器",
  displayName: "",
  appId: "",
  appSecret: "",
  token: "",
  encodingAESKey: "",
  remark: ""
});

// 监听 props.config 变化
watch(
  () => props.config,
  (newVal: any) => {
    if (newVal) {
      formData.connectorName = newVal.connectorName || "微信服务号连接器";
      formData.displayName = newVal.displayName || "";
      formData.appId = newVal.appId || "";
      formData.appSecret = newVal.appSecret || "";
      formData.token = newVal.token || "";
      formData.encodingAESKey = newVal.encodingAESKey || "";
      formData.remark = newVal.remark || "";
    }
  },
  { immediate: true, deep: true }
);

// 表单验证规则
const formRules: FormRules = {
  displayName: [
    { required: true, message: "请填写连接器名称", trigger: "blur" }
  ],
  appId: [{ required: true, message: "请输入AppID", trigger: "blur" }],
  appSecret: [{ required: true, message: "请输入AppSecret", trigger: "blur" }]
};

// 测试配置
const handleTest = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    // TODO: 调用测试接口
    ElMessage.success("配置测试成功");
  } catch (error) {
    ElMessage.error("请先完善必填项");
  }
};

// 保存配置
const handleSave = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    const { displayName, appId, appSecret, token, encodingAESKey, remark } =
      formData;
    const params = {
      connectorName: "微信服务号连接器",
      displayName,
      appId,
      appSecret,
      token,
      encodingAESKey,
      remark
    };
    // TODO: 调用保存接口
    console.log("保存配置:", params);
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
      width: 80px;
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
