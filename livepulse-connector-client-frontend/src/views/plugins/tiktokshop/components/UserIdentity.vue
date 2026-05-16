<template>
  <div class="user-identity">
    <h2 class="title">用户身份</h2>

    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-position="top"
      class="identity-form"
    >
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

      <el-form-item label="授权码" prop="authCode">
        <el-input
          v-model="formData.authCode"
          placeholder="请输入授权码或点击右侧按钮获取"
          clearable
        >
          <template #append>
            <el-button @click="handleGetAuthCode">获取</el-button>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item label="Access Token" prop="accessToken">
        <el-input
          v-model="formData.accessToken"
          placeholder="授权成功后自动获取"
          disabled
        />
      </el-form-item>

      <el-form-item label="Refresh Token" prop="refreshToken">
        <el-input
          v-model="formData.refreshToken"
          placeholder="授权成功后自动获取"
          disabled
        />
      </el-form-item>
    </el-form>

    <!-- 配置说明 -->
    <div class="config-instruction">
      <div class="instruction-title">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
        配置说明
      </div>
      <div class="instruction-content">
        <h4>OAuth 2.0 授权配置步骤：</h4>
        <ol>
          <li>登录 TikTok Shop 开放者平台 (partner.tiktokshop.com)</li>
          <li>创建应用并获取 App Key 和 App Secret</li>
          <li>配置授权回调地址（Redirect URI）</li>
          <li>在上方输入框中填写 App Key 和 App Secret</li>
          <li>点击"获取"按钮跳转到 TikTok Shop 授权页面</li>
          <li>完成授权后，系统将自动获取 Access Token 和 Refresh Token</li>
        </ol>

        <h4>权限范围：</h4>
        <ul>
          <li>支持读取商品信息、库存管理</li>
          <li>支持订单读取和订单状态更新</li>
          <li>支持物流信息查询和物流单号上传</li>
          <li>支持店铺基本信息和店铺数据统计</li>
        </ul>

        <h4>注意事项：</h4>
        <ul>
          <li>Access Token 有效期为 24 小时，过期后需要使用 Refresh Token 刷新</li>
          <li>Refresh Token 长期有效，除非用户取消授权</li>
          <li>请妥善保管 App Secret 和 Refresh Token，避免泄露</li>
          <li>建议定期更换 App Secret 以确保账户安全</li>
        </ul>
      </div>
    </div>

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
  appKey: "",
  appSecret: "",
  authCode: "",
  accessToken: "",
  refreshToken: ""
});

// 监听 props.config 变化
watch(
  () => props.config,
  (newVal: any) => {
    if (newVal) {
      formData.appKey = newVal.appKey || "";
      formData.appSecret = newVal.appSecret || "";
      formData.authCode = newVal.authCode || "";
      formData.accessToken = newVal.accessToken || "";
      formData.refreshToken = newVal.refreshToken || "";
    }
  },
  { immediate: true, deep: true }
);

// 表单验证规则
const formRules: FormRules = {
  appKey: [{ required: true, message: "请输入App Key", trigger: "blur" }],
  appSecret: [{ required: true, message: "请输入App Secret", trigger: "blur" }],
  authCode: [{ required: true, message: "请输入授权码", trigger: "blur" }]
};

// 获取授权码
const handleGetAuthCode = () => {
  if (!formData.appKey || !formData.appSecret) {
    ElMessage.warning("请先输入App Key和App Secret");
    return;
  }

  // TODO: 跳转到 TikTok Shop OAuth 授权页面
  // 需要构造授权 URL，包含 app_key, redirect_uri, response_type=code, state 等参数
  const authUrl = `https://partner.tiktokshop.com/openapi/v1/auth.html?app_key=${formData.appKey}&redirect_uri=${encodeURIComponent(window.location.origin + '/tiktokshop/callback')}&response_type=code&state=${Date.now()}`;

  ElMessage.info("正在跳转到 TikTok Shop 授权页面...");
  window.open(authUrl, '_blank');
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
.user-identity {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

  .title {
    margin: 0 0 24px;
    font-size: 16px;
    font-weight: 600;
    color: #333;
  }

  .identity-form {
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

    :deep(.el-input-group__append) {
      background-color: #fff;
      border: none;
      box-shadow: none;
      padding: 0;

      .el-button {
        border: none;
        background-color: #1890ff;
        color: #fff;
        border-radius: 0 4px 4px 0;

        &:hover {
          background-color: #40a9ff;
        }
      }
    }
  }

  .config-instruction {
    margin-top: 24px;
    padding: 20px;
    background-color: #f0f5ff;
    border: 1px solid #adc6ff;
    border-radius: 4px;

    .instruction-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      font-weight: 600;
      color: #1890ff;
      margin-bottom: 16px;

      .info-icon {
        font-size: 16px;
      }
    }

    .instruction-content {
      h4 {
        font-size: 13px;
        font-weight: 600;
        color: #333;
        margin: 16px 0 8px;

        &:first-child {
          margin-top: 0;
        }
      }

      ol, ul {
        margin: 0;
        padding-left: 24px;

        li {
          font-size: 13px;
          color: #666;
          line-height: 1.8;
          margin-bottom: 4px;

          &:last-child {
            margin-bottom: 0;
          }
        }
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
