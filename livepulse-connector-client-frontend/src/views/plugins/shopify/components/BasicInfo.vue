<template>
  <div class="config-card">
    <h2 class="config-title">Shopify插件基础配置</h2>

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
          placeholder="请输入Shopify店铺名称"
          clearable
        />
      </el-form-item>

      <el-form-item label="Shop域名" prop="shopDomain">
        <el-input
          v-model="formData.shopDomain"
          placeholder="请输入Shopify店铺域名，如：example.myshopify.com"
          clearable
        >
          <template #prepend>https://</template>
        </el-input>
        <div class="form-tip">
          在Shopify后台设置 > 域名中获取您的店铺域名
        </div>
      </el-form-item>

      <el-form-item label="API密钥" prop="apiKey">
        <el-input
          v-model="formData.apiKey"
          placeholder="请在Shopify后台「应用管理」中获取API密钥"
          clearable
        />
      </el-form-item>

      <el-form-item label="API密钥密码" prop="apiSecretKey">
        <el-input
          v-model="formData.apiSecretKey"
          type="password"
          placeholder="请在Shopify后台「应用管理」中获取API密钥密码"
          clearable
          show-password
        />
      </el-form-item>

      <el-form-item label="API版本" prop="apiVersion">
        <el-select
          v-model="formData.apiVersion"
          placeholder="请选择API版本"
          style="width: 100%"
        >
          <el-option label="2024-01" value="2024-01" />
          <el-option label="2023-10" value="2023-10" />
          <el-option label="2023-07" value="2023-07" />
          <el-option label="2023-04" value="2023-04" />
          <el-option label="2023-01" value="2023-01" />
        </el-select>
        <div class="form-tip">
          建议使用最新的稳定版本API
        </div>
      </el-form-item>

      <el-form-item label="Webhook URL（可选）">
        <el-input
          v-model="formData.webhookUrl"
          placeholder="用于接收Shopify事件通知的回调地址"
          clearable
        />
        <div class="form-tip">
          配置后可接收订单、产品等变更事件通知
        </div>
      </el-form-item>
    </el-form>

    <!-- 权限配置说明 -->
    <div class="permission-info">
      <div class="permission-title">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
        API权限配置说明
      </div>
      <ul class="permission-list">
        <li>需要在Shopify后台创建自定义应用并获取API凭据</li>
        <li>应用需要配置相应的访问权限：读取产品、订单、客户等信息</li>
        <li>确保API密钥和密钥密码妥善保管，避免泄露</li>
        <li>Webhook URL必须是公网可访问的HTTPS地址</li>
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
  shopifyCorpId: "",
  shopName: "",
  shopDomain: "",
  apiKey: "",
  apiSecretKey: "",
  apiVersion: "2024-01",
  webhookUrl: ""
});

// 监听 props.config 变化
watch(
  () => props.config,
  (newVal: any) => {
    if (newVal) {
      formData.shopifyCorpId = newVal.shopifyCorpId || "";
      formData.shopName = newVal.shopName || "";
      formData.shopDomain = newVal.shopDomain || "";
      formData.apiKey = newVal.apiKey || "";
      formData.apiSecretKey = newVal.apiSecretKey || "";
      formData.apiVersion = newVal.apiVersion || "2024-01";
      formData.webhookUrl = newVal.webhookUrl || "";
    }
  },
  { immediate: true, deep: true }
);

// 表单验证规则
const formRules: FormRules = {
  shopName: [{ required: true, message: "请输入店铺名称", trigger: "blur" }],
  shopDomain: [
    { required: true, message: "请输入Shop域名", trigger: "blur" },
    {
      pattern: /^[a-zA-Z0-9][a-zA-Z0-9\-]*\.myshopify\.com$/,
      message: "请输入正确的Shopify域名格式",
      trigger: "blur"
    }
  ],
  apiKey: [{ required: true, message: "请输入API密钥", trigger: "blur" }],
  apiSecretKey: [
    { required: true, message: "请输入API密钥密码", trigger: "blur" }
  ],
  apiVersion: [
    { required: true, message: "请选择API版本", trigger: "change" }
  ]
};

// 测试连接
const handleTest = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    // TODO: 调用测试连接接口
    ElMessage.success("Shopify连接测试成功");
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
