<template>
  <div class="config-card">
    <h2 class="config-title">Google Analytics 4 连接器配置</h2>

    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-position="top"
      class="config-form"
    >
      <el-form-item label="账号名称" prop="accountName">
        <el-input
          v-model="formData.accountName"
          placeholder="请输入账号名称，便于识别"
          clearable
        />
      </el-form-item>

      <el-form-item label="属性" prop="propertyId">
        <el-select
          v-model="formData.propertyId"
          placeholder="请选择GA4属性"
          style="width: 100%"
          @change="handlePropertyChange"
        >
          <el-option
            v-for="property in propertyList"
            :key="property.id"
            :label="property.name"
            :value="property.id"
          />
        </el-select>
        <div class="form-tip">
          授权后自动获取GA4账号下的属性列表
        </div>
      </el-form-item>

      <el-form-item label="数据流" prop="dataStreamId">
        <el-select
          v-model="formData.dataStreamId"
          placeholder="请选择数据流"
          style="width: 100%"
          :disabled="!formData.propertyId"
        >
          <el-option
            v-for="stream in dataStreamList"
            :key="stream.id"
            :label="stream.name"
            :value="stream.id"
          />
        </el-select>
        <div class="form-tip">
          选择属性后自动获取该属性下的数据流
        </div>
      </el-form-item>

      <el-form-item label="凭证类型" prop="credentialType">
        <el-radio-group v-model="formData.credentialType" @change="handleCredentialTypeChange">
          <el-radio label="oauth">OAuth 2.0</el-radio>
          <el-radio label="service">服务账号</el-radio>
        </el-radio-group>
        <div class="form-tip">
          OAuth 2.0 适用于用户授权场景，服务账号适用于服务器端调用
        </div>
      </el-form-item>

      <!-- OAuth 2.0 配置 -->
      <template v-if="formData.credentialType === 'oauth'">
        <el-form-item label="客户端 ID" prop="oauthClientId">
          <el-input
            v-model="formData.oauthClientId"
            placeholder="请输入OAuth 2.0客户端ID"
            clearable
          />
          <div class="form-tip">
            在Google Cloud Console中创建OAuth 2.0凭据
          </div>
        </el-form-item>

        <el-form-item label="客户端密钥" prop="oauthClientSecret">
          <el-input
            v-model="formData.oauthClientSecret"
            type="password"
            placeholder="请输入OAuth 2.0客户端密钥"
            show-password
          />
        </el-form-item>

        <el-form-item label="授权状态">
          <div class="auth-status">
            <el-tag v-if="isAuthorized" type="success" size="small">
              已授权
            </el-tag>
            <el-tag v-else type="info" size="small">
              未授权
            </el-tag>
            <el-button
              v-if="!isAuthorized"
              type="primary"
              link
              @click="handleAuthorize"
            >
              点击授权
            </el-button>
            <el-button
              v-else
              type="danger"
              link
              @click="handleRevoke"
            >
              取消授权
            </el-button>
          </div>
        </el-form-item>
      </template>

      <!-- 服务账号配置 -->
      <template v-if="formData.credentialType === 'service'">
        <el-form-item label="JSON密钥文件" prop="serviceAccountKey">
          <el-upload
            ref="uploadRef"
            class="key-file-uploader"
            :action="uploadUrl"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleFileChange"
            accept=".json"
            drag
          >
            <div v-if="!fileName" class="upload-content">
              <el-icon class="upload-icon"><UploadFilled /></el-icon>
              <div class="upload-text">
                点击或拖拽JSON密钥文件到此区域上传
              </div>
              <div class="upload-tip">
                仅支持 .json 格式，文件大小不超过10KB
              </div>
            </div>
            <div v-else class="file-info">
              <el-icon class="file-icon"><Document /></el-icon>
              <span class="file-name">{{ fileName }}</span>
              <el-icon class="remove-icon" @click.stop="handleRemoveFile">
                <CircleClose />
              </el-icon>
            </div>
          </el-upload>
          <div class="form-tip">
            在Google Cloud Console中创建服务账号并下载JSON密钥文件
          </div>
        </el-form-item>
      </template>
    </el-form>

    <!-- 配置说明 -->
    <div class="config-instruction">
      <div class="instruction-title">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
        配置说明
      </div>
      <div class="instruction-content">
        <h4>OAuth 2.0 凭证配置步骤：</h4>
        <ol>
          <li>登录 Google Cloud Console (console.cloud.google.com)</li>
          <li>创建新项目或选择现有项目</li>
          <li>启用 Google Analytics API</li>
          <li>在"API和服务" > "凭据"中创建OAuth 2.0客户端ID</li>
          <li>配置已授权的重定向 URI</li>
          <li>点击"点击授权"按钮完成授权流程</li>
        </ol>

        <h4>服务账号配置步骤：</h4>
        <ol>
          <li>在 Google Cloud Console 中创建服务账号</li>
          <li>为服务账号授予"查看者"权限</li>
          <li>下载 JSON 密钥文件</li>
          <li>在上方上传区域上传密钥文件</li>
        </ol>

        <h4>权限要求：</h4>
        <ul>
          <li>需要 Google Analytics 查看权限</li>
          <li>支持读取报告数据、用户数据等</li>
          <li>不支持写入或修改配置</li>
        </ul>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button class="test-btn" @click="handleTest">测试连接</el-button>
      <el-button type="primary" class="save-btn" @click="handleSave">保存</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { type PropType, ref, reactive, watch } from "vue";
import { ElMessage, type FormInstance, type FormRules, type UploadFile } from "element-plus";
import { InfoFilled, UploadFilled, Document, CircleClose } from "@element-plus/icons-vue";

const formRef = ref<FormInstance>();
const uploadRef = ref();
const uploadUrl = ref("/api/upload/ga4-key");

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
  ga4CorpId: "",
  accountName: "",
  propertyId: "",
  dataStreamId: "",
  credentialType: "oauth",
  oauthClientId: "",
  oauthClientSecret: "",
  serviceAccountKey: "",
  refreshToken: ""
});

// 授权状态
const isAuthorized = ref(false);

// 文件名
const fileName = ref("");

// 属性列表
const propertyList = ref([
  { id: "properties/123456789", name: "示例网站 - 生产环境" },
  { id: "properties/987654321", name: "示例网站 - 测试环境" }
]);

// 数据流列表
const dataStreamList = ref([
  { id: "streams/111111", name: "Web 数据流 1" },
  { id: "streams/222222", name: "Web 数据流 2" }
]);

// 监听 props.config 变化
watch(
  () => props.config,
  (newVal: any) => {
    if (newVal) {
      formData.ga4CorpId = newVal.ga4CorpId || "";
      formData.accountName = newVal.accountName || "";
      formData.propertyId = newVal.propertyId || "";
      formData.dataStreamId = newVal.dataStreamId || "";
      formData.credentialType = newVal.credentialType || "oauth";
      formData.oauthClientId = newVal.oauthClientId || "";
      formData.oauthClientSecret = newVal.oauthClientSecret || "";
      formData.serviceAccountKey = newVal.serviceAccountKey || "";
      formData.refreshToken = newVal.refreshToken || "";
      isAuthorized.value = !!newVal.refreshToken;
      if (newVal.serviceAccountKey) {
        fileName.value = "service-account-key.json";
      }
    }
  },
  { immediate: true, deep: true }
);

// 表单验证规则
const formRules: FormRules = {
  accountName: [{ required: true, message: "请输入账号名称", trigger: "blur" }],
  propertyId: [{ required: true, message: "请选择属性", trigger: "change" }],
  dataStreamId: [{ required: true, message: "请选择数据流", trigger: "change" }],
  credentialType: [{ required: true, message: "请选择凭证类型", trigger: "change" }],
  oauthClientId: [
    {
      required: true,
      message: "请输入客户端ID",
      trigger: "blur",
      validator: (rule, value, callback) => {
        if (formData.credentialType === "oauth" && !value) {
          callback(new Error("请输入客户端ID"));
        } else {
          callback();
        }
      }
    }
  ],
  oauthClientSecret: [
    {
      required: true,
      message: "请输入客户端密钥",
      trigger: "blur",
      validator: (rule, value, callback) => {
        if (formData.credentialType === "oauth" && !value) {
          callback(new Error("请输入客户端密钥"));
        } else {
          callback();
        }
      }
    }
  ],
  serviceAccountKey: [
    {
      required: true,
      message: "请上传服务账号密钥文件",
      trigger: "change",
      validator: (rule, value, callback) => {
        if (formData.credentialType === "service" && !value) {
          callback(new Error("请上传服务账号密钥文件"));
        } else {
          callback();
        }
      }
    }
  ]
};

// 凭证类型切换
const handleCredentialTypeChange = () => {
  if (formData.credentialType === "oauth") {
    fileName.value = "";
    formData.serviceAccountKey = "";
  } else {
    formData.oauthClientId = "";
    formData.oauthClientSecret = "";
  }
};

// 属性切换
const handlePropertyChange = () => {
  formData.dataStreamId = "";
  // TODO: 根据属性ID重新获取数据流列表
};

// 文件选择
const handleFileChange = (file: UploadFile) => {
  if (!file.raw) return;

  // 验证文件类型
  if (!file.name.endsWith(".json")) {
    ElMessage.error("仅支持上传 .json 格式的文件");
    return;
  }

  // 验证文件大小（10KB）
  if (file.size > 10 * 1024) {
    ElMessage.error("文件大小不能超过10KB");
    return;
  }

  fileName.value = file.name;

  // 读取文件内容
  const reader = new FileReader();
  reader.onload = (e) => {
    try {
      const content = e.target?.result as string;
      JSON.parse(content); // 验证JSON格式
      formData.serviceAccountKey = content;
      ElMessage.success("密钥文件上传成功");
    } catch (error) {
      ElMessage.error("JSON格式不正确");
      fileName.value = "";
    }
  };
  reader.readAsText(file.raw);
};

// 删除文件
const handleRemoveFile = () => {
  fileName.value = "";
  formData.serviceAccountKey = "";
  if (uploadRef.value) {
    uploadRef.value.clearFiles();
  }
};

// OAuth授权
const handleAuthorize = () => {
  if (!formData.oauthClientId || !formData.oauthClientSecret) {
    ElMessage.warning("请先完成OAuth 2.0凭据配置");
    return;
  }

  // TODO: 调用OAuth授权接口
  ElMessage.info("正在跳转到Google授权页面...");
};

// 取消授权
const handleRevoke = () => {
  // TODO: 调用取消授权接口
  isAuthorized.value = false;
  formData.refreshToken = "";
  ElMessage.success("已取消授权");
};

// 测试连接
const handleTest = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    // TODO: 调用测试连接接口
    ElMessage.success("GA4连接测试成功");
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

    :deep(.el-radio-group) {
      display: flex;
      gap: 24px;
    }

    .form-tip {
      margin-top: 4px;
      font-size: 12px;
      color: #999;
      line-height: 1.5;
    }

    .auth-status {
      display: flex;
      align-items: center;
      gap: 12px;
    }
  }

  .key-file-uploader {
    width: 100%;

    :deep(.el-upload) {
      width: 100%;
    }

    :deep(.el-upload-dragger) {
      width: 100%;
      height: 160px;
      padding: 20px;
      background-color: #f5f5f5;
      border: 1px dashed #d9d9d9;
      border-radius: 4px;

      &:hover {
        border-color: #1890ff;
        background-color: #e6f7ff;
      }
    }

    .upload-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 100%;

      .upload-icon {
        font-size: 48px;
        color: #1890ff;
        margin-bottom: 12px;
      }

      .upload-text {
        font-size: 14px;
        color: #333;
        margin-bottom: 8px;
      }

      .upload-tip {
        font-size: 12px;
        color: #999;
      }
    }

    .file-info {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 8px 12px;
      background-color: #e6f7ff;
      border: 1px solid #1890ff;
      border-radius: 4px;

      .file-icon {
        font-size: 20px;
        color: #1890ff;
      }

      .file-name {
        flex: 1;
        font-size: 14px;
        color: #333;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .remove-icon {
        font-size: 18px;
        color: #999;
        cursor: pointer;

        &:hover {
          color: #f5222d;
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
