<template>
  <div class="message-push-page">
    <div class="config-card">
      <h2 class="config-title">消息推送配置</h2>

      <!-- 消息推送策略 -->
      <div class="push-strategy">
        <div class="config-section-title">推送策略</div>
        <el-form :model="strategyForm" label-width="140px" class="strategy-form">
          <el-form-item label="推送方式">
            <el-radio-group v-model="strategyForm.pushMode">
              <el-radio label="realtime">实时推送</el-radio>
              <el-radio label="batch">批量推送</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="推送频率" v-if="strategyForm.pushMode === 'batch'">
            <el-select
              v-model="strategyForm.frequency"
              placeholder="请选择推送频率"
              style="width: 200px"
            >
              <el-option label="每5分钟" value="5min" />
              <el-option label="每15分钟" value="15min" />
              <el-option label="每30分钟" value="30min" />
              <el-option label="每小时" value="1hour" />
            </el-select>
          </el-form-item>

          <el-form-item label="失败重试">
            <el-switch v-model="strategyForm.retryEnabled" />
            <span class="form-tip">开启后，推送失败将自动重试</span>
          </el-form-item>

          <el-form-item label="重试次数" v-if="strategyForm.retryEnabled">
            <el-input-number
              v-model="strategyForm.retryCount"
              :min="1"
              :max="5"
              style="width: 120px"
            />
            <span class="form-tip">次</span>
          </el-form-item>
        </el-form>
      </div>

      <!-- 消息类型配置 -->
      <div class="message-type">
        <div class="config-section-title">消息类型配置</div>
        <el-table
          :data="messageTypes"
          class="message-type-table"
          border
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column type="index" label="编号" width="60" align="center" />
          <el-table-column prop="typeName" label="消息类型" min-width="150" />
          <el-table-column prop="eventKey" label="事件Key" min-width="180" />
          <el-table-column prop="description" label="说明" min-width="250" />
          <el-table-column label="启用状态" width="100" align="center">
            <template #default="{ row }">
              <el-switch
                v-model="row.enabled"
                @change="handleStatusChange(row)"
              />
            </template>
          </el-table-column>
        </el-table>

        <div class="message-summary">
          <span class="summary-text">
            已选择 <strong>{{ selectedMessages.length }}</strong> 种消息类型，
            已启用 <strong>{{ enabledCount }}</strong> 种
          </span>
        </div>
      </div>

      <!-- 推送目标配置 -->
      <div class="push-target">
        <div class="config-section-title">推送目标配置</div>
        <el-form :model="targetForm" label-width="140px" class="target-form">
          <el-form-item label="推送URL">
            <el-input
              v-model="targetForm.pushUrl"
              placeholder="请输入接收消息的回调地址"
              clearable
            />
          </el-form-item>

          <el-form-item label="认证方式">
            <el-select
              v-model="targetForm.authType"
              placeholder="请选择认证方式"
              style="width: 200px"
            >
              <el-option label="无需认证" value="none" />
              <el-option label="Token认证" value="token" />
              <el-option label="签名认证" value="signature" />
            </el-select>
          </el-form-item>

          <el-form-item label="Token" v-if="targetForm.authType === 'token'">
            <el-input
              v-model="targetForm.token"
              placeholder="请输入Token"
              clearable
              show-password
            />
          </el-form-item>

          <el-form-item label="签名密钥" v-if="targetForm.authType === 'signature'">
            <el-input
              v-model="targetForm.secret"
              placeholder="请输入签名密钥"
              clearable
              show-password
            />
          </el-form-item>

          <el-form-item label="超时时间">
            <el-input-number
              v-model="targetForm.timeout"
              :min="1"
              :max="60"
              style="width: 120px"
            />
            <span class="form-tip">秒</span>
          </el-form-item>
        </el-form>
      </div>

      <!-- 消息格式配置 -->
      <div class="message-format">
        <div class="config-section-title">消息格式配置</div>
        <el-form :model="formatForm" label-width="140px" class="format-form">
          <el-form-item label="消息格式">
            <el-radio-group v-model="formatForm.messageFormat">
              <el-radio label="json">JSON格式</el-radio>
              <el-radio label="xml">XML格式</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="数据加密">
            <el-switch v-model="formatForm.encryptionEnabled" />
            <span class="form-tip">启用后将对消息内容进行加密</span>
          </el-form-item>

          <el-form-item
            label="加密方式"
            v-if="formatForm.encryptionEnabled"
          >
            <el-select
              v-model="formatForm.encryptionMethod"
              placeholder="请选择加密方式"
              style="width: 200px"
            >
              <el-option label="AES-256-CBC" value="aes-256-cbc" />
              <el-option label="RSA" value="rsa" />
            </el-select>
          </el-form-item>

          <el-form-item label="字符编码">
            <el-select
              v-model="formatForm.charset"
              placeholder="请选择字符编码"
              style="width: 200px"
            >
              <el-option label="UTF-8" value="utf-8" />
              <el-option label="GBK" value="gbk" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 底部操作按钮 -->
    <div class="action-buttons">
      <el-button @click="handleTestPush">测试推送</el-button>
      <el-button @click="handleViewLog">推送日志</el-button>
      <el-button type="primary" @click="handleSaveConfig">保存配置</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { ElMessage } from "element-plus";

// 推送策略表单
const strategyForm = ref({
  pushMode: "realtime",
  frequency: "5min",
  retryEnabled: true,
  retryCount: 3
});

// 消息类型列表
const messageTypes = ref([
  {
    id: 1,
    typeName: "文本消息",
    eventKey: "text_message",
    description: "接收并推送文本类型消息",
    enabled: true
  },
  {
    id: 2,
    typeName: "图片消息",
    eventKey: "image_message",
    description: "接收并推送图片类型消息",
    enabled: true
  },
  {
    id: 3,
    typeName: "语音消息",
    eventKey: "voice_message",
    description: "接收并推送语音类型消息",
    enabled: false
  },
  {
    id: 4,
    typeName: "视频消息",
    eventKey: "video_message",
    description: "接收并推送视频类型消息",
    enabled: false
  },
  {
    id: 5,
    typeName: "文件消息",
    eventKey: "file_message",
    description: "接收并推送文件类型消息",
    enabled: true
  },
  {
    id: 6,
    typeName: "链接消息",
    eventKey: "link_message",
    description: "接收并推送链接类型消息",
    enabled: true
  },
  {
    id: 7,
    typeName: "事件消息",
    eventKey: "event_message",
    description: "接收并推送事件类型消息",
    enabled: true
  },
  {
    id: 8,
    typeName: "位置消息",
    eventKey: "location_message",
    description: "接收并推送位置类型消息",
    enabled: false
  }
]);

// 已选择的消息类型
const selectedMessages = ref<any[]>([]);

// 已启用数量
const enabledCount = computed(() => {
  return messageTypes.value.filter((item) => item.enabled).length;
});

// 推送目标配置
const targetForm = ref({
  pushUrl: "",
  authType: "none",
  token: "",
  secret: "",
  timeout: 30
});

// 消息格式配置
const formatForm = ref({
  messageFormat: "json",
  encryptionEnabled: false,
  encryptionMethod: "aes-256-cbc",
  charset: "utf-8"
});

// 选择变化
const handleSelectionChange = (selection: any[]) => {
  selectedMessages.value = selection;
};

// 状态变化
const handleStatusChange = (row: any) => {
  const status = row.enabled ? "已启用" : "已禁用";
  ElMessage.success(`${row.typeName} ${status}`);
};

// 测试推送
const handleTestPush = () => {
  if (!targetForm.value.pushUrl) {
    ElMessage.warning("请先配置推送URL");
    return;
  }
  // TODO: 调用测试推送接口
  ElMessage.success("测试推送已发送");
};

// 推送日志
const handleViewLog = () => {
  // TODO: 跳转到推送日志页面
  ElMessage.info("跳转到推送日志页面");
};

// 保存配置
const handleSaveConfig = () => {
  if (!targetForm.value.pushUrl) {
    ElMessage.warning("请先配置推送URL");
    return;
  }

  // TODO: 调用保存配置接口
  ElMessage.success("消息推送配置已保存");
};
</script>

<style scoped lang="scss">
.message-push-page {
  .config-card {
    background-color: #fff;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
    margin-bottom: 16px;

    .config-title {
      margin: 0 0 24px;
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }

    .config-section-title {
      font-size: 14px;
      font-weight: 600;
      color: #333;
      margin-bottom: 16px;
      padding-left: 8px;
      border-left: 3px solid #1890ff;
    }

    .push-strategy,
    .message-type,
    .push-target,
    .message-format {
      margin-bottom: 32px;

      &:last-child {
        margin-bottom: 0;
      }
    }

    .strategy-form,
    .target-form,
    .format-form {
      max-width: 600px;

      :deep(.el-form-item__label) {
        font-size: 14px;
        color: #333;
        font-weight: 500;
      }

      :deep(.el-input__wrapper),
      :deep(.el-select .el-input__wrapper) {
        background-color: #f5f5f5;
        border: none;
        box-shadow: none;
        padding: 8px 12px;

        &.is-focus {
          background-color: #e6f7ff;
        }
      }

      :deep(.el-radio__label),
      :deep(.el-checkbox__label) {
        font-size: 14px;
        color: #666;
      }

      .form-tip {
        margin-left: 12px;
        font-size: 13px;
        color: #999;
      }
    }

    .message-type-table {
      width: 100%;
      margin-bottom: 16px;

      :deep(.el-table__header) {
        th {
          background-color: #fafafa;
          color: #333;
          font-weight: 600;
        }
      }

      :deep(.el-table__body) {
        td {
          color: #666;
        }
      }
    }

    .message-summary {
      text-align: right;
      padding: 12px;
      background-color: #f5f5f5;
      border-radius: 4px;

      .summary-text {
        font-size: 13px;
        color: #666;

        strong {
          color: #1890ff;
          font-weight: 600;
        }
      }
    }
  }

  .action-buttons {
    display: flex;
    justify-content: flex-end;
    gap: 12px;

    button {
      min-width: 100px;
    }
  }
}
</style>
