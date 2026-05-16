<template>
  <div class="config-card">
    <h2 class="config-title">Shopee插件基础配置</h2>

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
          placeholder="请输入Shopee店铺名称"
          clearable
        />
      </el-form-item>

      <el-form-item label="站点/区域" prop="region">
        <el-select
          v-model="formData.region"
          placeholder="请选择Shopee站点"
          style="width: 100%"
        >
          <el-option label="新加坡" value="SG" />
          <el-option label="马来西亚" value="MY" />
          <el-option label="台湾" value="TW" />
          <el-option label="泰国" value="TH" />
          <el-option label="菲律宾" value="PH" />
          <el-option label="越南" value="VN" />
          <el-option label="印度尼西亚" value="ID" />
          <el-option label="巴西" value="BR" />
        </el-select>
        <div class="form-tip">
          选择店铺所在的国家或地区，不同站点API地址不同
        </div>
      </el-form-item>

      <el-form-item label="Partner ID" prop="partnerId">
        <el-input
          v-model="formData.partnerId"
          placeholder="请在Shopee开放平台获取Partner ID"
          clearable
        />
      </el-form-item>

      <el-form-item label="Partner Key" prop="partnerKey">
        <el-input
          v-model="formData.partnerKey"
          type="password"
          placeholder="请在Shopee开放平台获取Partner Key"
          clearable
          show-password
        />
      </el-form-item>

      <el-form-item label="Shop ID" prop="shopId">
        <el-input
          v-model="formData.shopId"
          placeholder="请输入店铺Shop ID"
          clearable
        />
        <div class="form-tip">
          完成授权后会自动获取Shop ID，也可手动输入
        </div>
      </el-form-item>

      <el-form-item label="Access Token（可选）">
        <el-input
          v-model="formData.accessToken"
          type="password"
          placeholder="授权后自动生成的访问令牌"
          clearable
          show-password
        />
        <div class="form-tip">
          通过OAuth授权流程获取，有效期30天，过期需重新授权
        </div>
      </el-form-item>

      <el-form-item label="回调URL（可选）">
        <el-input
          v-model="formData.redirectUrl"
          placeholder="用于接收Shopee授权回调的地址"
          clearable
        />
        <div class="form-tip">
          必须是在Shopee开放平台配置过的白名单域名
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
        <li>需要在Shopee开放平台（open.shopee.com）创建应用并获取Partner ID和Key</li>
        <li>需要配置应用访问权限：读取订单、产品、物流、店铺等信息</li>
        <li>首次使用需要通过OAuth授权流程获取Shop ID和Access Token</li>
        <li>Access Token有效期为30天，过期后需要重新授权</li>
        <li>建议使用官方沙箱环境进行测试，避免影响正式店铺数据</li>
      </ul>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button class="auth-btn" @click="handleAuth">
        获取授权
      </el-button>
      <el-button class="test-btn" @click="handleTest">
        测试连接
      </el-button>
      <el-button type="primary" class="save-btn" @click="handleSave">
        保存
      </el-button>
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
  shopeeCorpId: "",
  shopName: "",
  region: "",
  partnerId: "",
  partnerKey: "",
  shopId: "",
  accessToken: "",
  redirectUrl: ""
});

// 监听 props.config 变化
watch(
  () => props.config,
  (newVal: any) => {
    if (newVal) {
      formData.shopeeCorpId = newVal.shopeeCorpId || "";
      formData.shopName = newVal.shopName || "";
      formData.region = newVal.region || "";
      formData.partnerId = newVal.partnerId || "";
      formData.partnerKey = newVal.partnerKey || "";
      formData.shopId = newVal.shopId || "";
      formData.accessToken = newVal.accessToken || "";
      formData.redirectUrl = newVal.redirectUrl || "";
    }
  },
  { immediate: true, deep: true }
);

// 表单验证规则
const formRules: FormRules = {
  shopName: [{ required: true, message: "请输入店铺名称", trigger: "blur" }],
  region: [{ required: true, message: "请选择站点/区域", trigger: "change" }],
  partnerId: [{ required: true, message: "请输入Partner ID", trigger: "blur" }],
  partnerKey: [
    { required: true, message: "请输入Partner Key", trigger: "blur" }
  ],
  shopId: [{ required: true, message: "请输入Shop ID", trigger: "blur" }]
};

// 获取授权
const handleAuth = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate(["partnerId", "partnerKey", "redirectUrl"]);
    // TODO: 调用授权接口，跳转到Shopee授权页面
    ElMessage.success("正在跳转到Shopee授权页面...");
  } catch (error) {
    ElMessage.error("请先完善Partner ID、Partner Key和回调URL");
  }
};

// 测试连接
const handleTest = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    // TODO: 调用测试连接接口
    ElMessage.success("Shopee连接测试成功");
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

    .auth-btn {
      width: 100px;
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
