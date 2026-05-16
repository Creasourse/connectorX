<template>
  <div class="config-card">
    <h2 class="config-title">有赞商城基础配置</h2>

    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-position="top"
      class="config-form"
    >
      <el-form-item label="店铺名称" prop="shopName">
        <el-input
          v-model="formData.shopName"
          placeholder="请输入有赞店铺名称"
          clearable
        />
      </el-form-item>

      <el-form-item label="店铺ID" prop="shopId">
        <el-input
          v-model="formData.shopId"
          placeholder="请输入有赞店铺ID"
          clearable
        />
        <div class="form-tip">
          在有赞商家后台 > 设置 > 店铺信息中获取店铺ID
        </div>
      </el-form-item>

      <el-form-item label="Client ID" prop="clientId">
        <el-input
          v-model="formData.clientId"
          placeholder="请输入有赞开放平台应用的Client ID"
          clearable
        />
        <div class="form-tip">
          在有赞开放平台（open.youzan.com）创建应用后获取
        </div>
      </el-form-item>

      <el-form-item label="Client Secret" prop="clientSecret">
        <el-input
          v-model="formData.clientSecret"
          type="password"
          placeholder="请输入有赞开放平台应用的Client Secret"
          clearable
          show-password
        />
        <div class="form-tip">
          在有赞开放平台应用详情中获取，请妥善保管
        </div>
      </el-form-item>

      <el-form-item label="API版本" prop="apiVersion">
        <el-select
          v-model="formData.apiVersion"
          placeholder="请选择API版本"
          style="width: 100%"
        >
          <el-option label="1.0.0 (最新版本)" value="1.0.0" />
          <el-option label="0.5.0" value="0.5.0" />
          <el-option label="0.4.0" value="0.4.0" />
        </el-select>
        <div class="form-tip">
          建议使用最新的稳定版本API
        </div>
      </el-form-item>

      <el-form-item label="回调URL" prop="callbackUrl">
        <el-input
          v-model="formData.callbackUrl"
          placeholder="用于接收有赞消息推送的回调地址"
          clearable
        />
        <div class="form-tip">
          配置后可接收订单、商品等变更事件通知，必须是公网可访问的HTTPS地址
        </div>
      </el-form-item>

      <!-- 功能开关 -->
      <div class="switch-config">
        <div class="switch-item">
          <span class="switch-label">启用订单同步</span>
          <el-switch
            v-model="formData.enableOrderSync"
            active-color="#1890ff"
            inactive-color="#d9d9d9"
          />
        </div>

        <div class="switch-item">
          <span class="switch-label">启用商品同步</span>
          <el-switch
            v-model="formData.enableProductSync"
            active-color="#1890ff"
            inactive-color="#d9d9d9"
          />
        </div>

        <div class="switch-item">
          <span class="switch-label">启用客户同步</span>
          <el-switch
            v-model="formData.enableCustomerSync"
            active-color="#1890ff"
            inactive-color="#d9d9d9"
          />
        </div>

        <div class="switch-item">
          <span class="switch-label">启用消息推送</span>
          <el-switch
            v-model="formData.enableWebhook"
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
        有赞开放平台接入说明
      </div>
      <ul class="permission-list">
        <li>需要在有赞开放平台（open.youzan.com）创建应用并获取授权</li>
        <li>应用需要配置相应的权限：读取订单、商品、客户等信息</li>
        <li>确保Client ID和Client Secret妥善保管，避免泄露</li>
        <li>回调URL必须是公网可访问的HTTPS地址，用于接收消息推送</li>
        <li>首次使用需要进行店铺授权，获取访问令牌（Access Token）</li>
      </ul>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button class="auth-btn" @click="handleAuth">
        <el-icon><Link /></el-icon>
        店铺授权
      </el-button>
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
import { InfoFilled, Link } from "@element-plus/icons-vue";

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
  shopName: "",
  shopId: "",
  clientId: "",
  clientSecret: "",
  apiVersion: "1.0.0",
  callbackUrl: "",
  enableOrderSync: true,
  enableProductSync: true,
  enableCustomerSync: false,
  enableWebhook: true
});

// 监听 props.config 变化
watch(
  () => props.config,
  (newVal: any) => {
    if (newVal) {
      formData.shopName = newVal.shopName || "";
      formData.shopId = newVal.shopId || "";
      formData.clientId = newVal.clientId || "";
      formData.clientSecret = newVal.clientSecret || "";
      formData.apiVersion = newVal.apiVersion || "1.0.0";
      formData.callbackUrl = newVal.callbackUrl || "";
      formData.enableOrderSync = newVal.enableOrderSync ?? true;
      formData.enableProductSync = newVal.enableProductSync ?? true;
      formData.enableCustomerSync = newVal.enableCustomerSync ?? false;
      formData.enableWebhook = newVal.enableWebhook ?? true;
    }
  },
  { immediate: true, deep: true }
);

// 表单验证规则
const formRules: FormRules = {
  shopName: [{ required: true, message: "请输入店铺名称", trigger: "blur" }],
  shopId: [
    { required: true, message: "请输入店铺ID", trigger: "blur" }
  ],
  clientId: [
    { required: true, message: "请输入Client ID", trigger: "blur" }
  ],
  clientSecret: [
    { required: true, message: "请输入Client Secret", trigger: "blur" }
  ],
  apiVersion: [
    { required: true, message: "请选择API版本", trigger: "change" }
  ]
};

// 店铺授权
const handleAuth = () => {
  // TODO: 跳转到有赞授权页面
  ElMessage.success("正在跳转到有赞授权页面...");
};

// 测试连接
const handleTest = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    // TODO: 调用测试连接接口
    ElMessage.success("有赞商城连接测试成功");
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
    }

    :deep(.el-input__inner) {
      color: #333;
    }

    :deep(.el-select .el-input__wrapper) {
      background-color: #f5f5f5;
    }

    .form-tip {
      margin-top: 4px;
      font-size: 12px;
      color: #999;
      line-height: 1.5;
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
    background-color: #fff7e6;
    border: 1px solid #ffd591;
    border-radius: 4px;

    .permission-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      font-weight: 600;
      color: #fa8c16;
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
        color: #fa8c16;
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

    .auth-btn {
      width: 120px;
      border: 1px solid #fa8c16;
      color: #fa8c16;
      background-color: #fff;

      &:hover {
        background-color: #fff7e6;
      }
    }

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
