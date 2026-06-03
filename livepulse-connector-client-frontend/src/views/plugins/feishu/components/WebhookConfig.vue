<template>
  <div class="config-card">
    <h2 class="config-title">Webhook配置</h2>

    <div class="config-section">
      <div class="section-title">
        <el-icon><Bell /></el-icon>
        回调地址配置
      </div>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-position="top"
        class="config-form"
      >
        <el-form-item label="回调URL" prop="callbackUrl">
          <el-input
            v-model="formData.callbackUrl"
            placeholder="请输入接收飞书事件的回调地址"
            clearable
          />
          <span class="form-tip">飞书会将事件推送到此地址，必须使用HTTPS协议</span>
        </el-form-item>

        <el-form-item label="验证Token" prop="verifyToken">
          <el-input
            v-model="formData.verifyToken"
            type="password"
            placeholder="请输入用于验证请求来源的Token"
            clearable
            show-password
          />
          <span class="form-tip">用于验证飞书推送请求的真实性，建议使用随机字符串</span>
        </el-form-item>

        <el-form-item label="加密密钥（Encrypt Key）" prop="encryptKey">
          <el-input
            v-model="formData.encryptKey"
            type="password"
            placeholder="请输入消息加密密钥"
            clearable
            show-password
          />
          <span class="form-tip">用于加密推送的消息内容，长度为43位的随机字符串</span>
        </el-form-item>

        <el-form-item label="是否启用加密">
          <el-switch
            v-model="formData.enableEncryption"
            active-text="启用"
            inactive-text="禁用"
          />
          <span class="form-tip">启用后将对推送的消息进行加密传输</span>
        </el-form-item>
      </el-form>
    </div>

    <div class="config-section">
      <div class="section-title">
        <el-icon><Document /></el-icon>
        订阅事件配置
      </div>

      <div class="event-config">
        <div class="event-group">
          <div class="group-title">多维表格事件</div>
          <el-checkbox-group v-model="selectedEvents">
            <el-checkbox label="bitable_record_add">记录新增</el-checkbox>
            <el-checkbox label="bitable_record_update">记录更新</el-checkbox>
            <el-checkbox label="bitable_record_delete">记录删除</el-checkbox>
          </el-checkbox-group>
        </div>

        <div class="event-group">
          <div class="group-title">字段变更事件</div>
          <el-checkbox-group v-model="selectedEvents">
            <el-checkbox label="bitable_field_add">字段新增</el-checkbox>
            <el-checkbox label="bitable_field_update">字段更新</el-checkbox>
            <el-checkbox label="bitable_field_delete">字段删除</el-checkbox>
          </el-checkbox-group>
        </div>

        <div class="event-group">
          <div class="group-title">表格变更事件</div>
          <el-checkbox-group v-model="selectedEvents">
            <el-checkbox label="bitable_table_add">表格新增</el-checkbox>
            <el-checkbox label="bitable_table_update">表格更新</el-checkbox>
            <el-checkbox label="bitable_table_delete">表格删除</el-checkbox>
          </el-checkbox-group>
        </div>
      </div>
    </div>

    <!-- 配置说明 -->
    <div class="permission-info">
      <div class="permission-title">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
        配置说明
      </div>
      <ul class="permission-list">
        <li>在飞书开放平台配置事件订阅，填入回调URL和验证Token</li>
        <li>回调URL必须能被公网访问，建议使用域名并配置SSL证书</li>
        <li>验证Token和加密密钥需在飞书开放平台和本系统中保持一致</li>
        <li>启用加密后，系统将使用AES算法对推送消息进行加解密</li>
        <li>订阅事件后，飞书会在事件发生时实时推送数据到回调URL</li>
      </ul>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button @click="handleTest" :loading="testing">
        <el-icon><Connection /></el-icon>
        测试连接
      </el-button>
      <el-button type="primary" @click="handleSave" :loading="saving">
        <el-icon><Select /></el-icon>
        保存配置
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from "vue";
import type { PropType } from "vue";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import { Bell, Document, InfoFilled, Connection, Select } from "@element-plus/icons-vue";

const formRef = ref<FormInstance>();
const testing = ref(false);
const saving = ref(false);

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
  callbackUrl: "",
  verifyToken: "",
  encryptKey: "",
  enableEncryption: true
});

// 选中的事件
const selectedEvents = ref<string[]>([]);

// 表单验证规则
const formRules: FormRules = {
  callbackUrl: [
    { required: true, message: "请输入回调URL", trigger: "blur" },
    { type: "url", message: "请输入有效的URL地址", trigger: "blur" }
  ],
  verifyToken: [
    { required: true, message: "请输入验证Token", trigger: "blur" },
    { min: 6, message: "Token长度不能少于6位", trigger: "blur" }
  ],
  encryptKey: [
    { required: true, message: "请输入加密密钥", trigger: "blur" },
    { min: 43, max: 43, message: "加密密钥必须为43位", trigger: "blur" }
  ]
};

// 监听 props.config 变化
watch(
  () => props.config,
  (newConfig) => {
    if (newConfig) {
      // 从配置中加载Webhook数据
      Object.assign(formData, {
        callbackUrl: newConfig.callbackUrl || "",
        verifyToken: newConfig.verifyToken || "",
        encryptKey: newConfig.encryptKey || "",
        enableEncryption: newConfig.enableEncryption !== false
      });
      selectedEvents.value = newConfig.subscribedEvents || [];
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
    // TODO: 调用测试连接接口
    await new Promise(resolve => setTimeout(resolve, 1000));
    ElMessage.success("连接测试成功");
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

  if (selectedEvents.value.length === 0) {
    ElMessage.warning("请至少选择一个订阅事件");
    return;
  }

  saving.value = true;
  try {
    const params = {
      feishuAppId: props.config?.feishuAppId,
      ...formData,
      subscribedEvents: selectedEvents.value
    };
    // TODO: 调用保存Webhook配置接口
    await new Promise(resolve => setTimeout(resolve, 1000));
    ElMessage.success("保存成功");
    emit("update");
  } catch (error) {
    ElMessage.error("保存失败");
  } finally {
    saving.value = false;
  }
};
</script>

<style lang="scss" scoped>
.config-card {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  .config-title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 24px 0;
    padding-bottom: 16px;
    border-bottom: 1px solid #e5e7eb;
  }

  .config-section {
    margin-bottom: 32px;

    .section-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 20px;
      padding-bottom: 12px;
      border-bottom: 1px solid #e5e7eb;

      .el-icon {
        color: #409eff;
        font-size: 18px;
      }
    }

    .config-form {
      max-width: 700px;

      :deep(.el-form-item__label) {
        font-weight: 500;
        color: #606266;
      }

      .form-tip {
        margin-left: 12px;
        font-size: 12px;
        color: #909399;
      }
    }

    .event-config {
      .event-group {
        margin-bottom: 24px;

        .group-title {
          font-size: 14px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 12px;
        }

        :deep(.el-checkbox-group) {
          display: flex;
          flex-direction: column;
          gap: 12px;
          padding-left: 16px;

          .el-checkbox {
            margin: 0;
          }
        }
      }
    }
  }

  .permission-info {
    margin-bottom: 32px;
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
