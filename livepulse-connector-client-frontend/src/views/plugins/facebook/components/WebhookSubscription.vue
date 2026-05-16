<template>
  <div class="config-card">
    <h2 class="config-title">webhook订阅</h2>

    <!-- Webhook配置表格 -->
    <div class="webhook-table">
      <el-table :data="webhookList" style="width: 100%">
        <el-table-column prop="eventName" label="事件名称" width="200" />
        <el-table-column prop="description" label="说明" />
        <el-table-column label="订阅状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.subscribed"
              :active-value="true"
              :inactive-value="false"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 配置说明 -->
    <div class="permission-info">
      <div class="permission-title">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
        配置说明
      </div>
      <ul class="permission-list">
        <li>开启订阅后，系统将实时接收对应的Webhook通知</li>
        <li>评论事件会在用户评论帖子时触发</li>
        <li>点赞和分享事件用于追踪用户互动行为</li>
        <li>Mentions事件用于监控品牌提及和@通知</li>
        <li>消息事件用于接收用户发送的私信</li>
      </ul>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" class="save-btn" @click="handleSave"
        >保存</el-button
      >
    </div>
  </div>
</template>

<script setup lang="ts">
import { type PropType, ref, reactive, watch } from "vue";
import { ElMessage } from "element-plus";
import { InfoFilled } from "@element-plus/icons-vue";

interface WebhookItem {
  id: string;
  eventName: string;
  description: string;
  subscribed: boolean;
}

const props = defineProps({
  config: {
    type: Object as PropType<unknown>
  }
});

const emit = defineEmits<{
  (e: "update"): void;
}>();

// Webhook列表
const webhookList = ref<WebhookItem[]>([
  {
    id: "comments",
    eventName: "评论",
    description: "用户评论帖子时的通知事件",
    subscribed: true
  },
  {
    id: "likes",
    eventName: "点赞",
    description: "用户点赞帖子时的通知事件",
    subscribed: true
  },
  {
    id: "shares",
    eventName: "分享",
    description: "用户分享帖子时的通知事件",
    subscribed: false
  },
  {
    id: "mentions",
    eventName: "Mentions",
    description: "品牌被提及或收到@通知时的消息事件",
    subscribed: false
  },
  {
    id: "messages",
    eventName: "消息",
    description: "用户发送私信时的消息事件",
    subscribed: true
  }
]);

// 监听 props.config 变化
watch(
  () => props.config,
  (newVal: any) => {
    if (newVal && newVal.webhookList) {
      webhookList.value = newVal.webhookList;
    }
  },
  { immediate: true, deep: true }
);

// 状态变更处理
const handleStatusChange = (row: WebhookItem) => {
  console.log(`Webhook ${row.eventName} 订阅状态: ${row.subscribed}`);
};

// 保存配置
const handleSave = async () => {
  try {
    const subscribedWebhooks = webhookList.value
      .filter(item => item.subscribed)
      .map(item => item.id);

    const params = {
      webhookList: webhookList.value,
      subscribedWebhooks
    };
    // TODO: 调用保存接口
    console.log("保存Webhook订阅配置:", params);
    ElMessage.success("保存成功");
    emit("update");
  } catch (error) {
    ElMessage.error("保存失败，请稍后重试");
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

  .webhook-table {
    margin-bottom: 24px;

    :deep(.el-table) {
      font-size: 14px;

      th.el-table__cell {
        background-color: #fafafa;
        font-weight: 600;
        color: #333;
      }

      td.el-table__cell {
        color: #666;
      }
    }

    :deep(.el-switch) {
      &.is-checked {
        .el-switch__core {
          background-color: #52c41a;
          border-color: #52c41a;
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
