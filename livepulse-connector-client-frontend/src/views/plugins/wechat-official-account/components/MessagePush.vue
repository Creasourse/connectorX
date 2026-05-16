<template>
  <div class="config-card">
    <h2 class="config-title">消息推送</h2>

    <!-- 消息推送表格 -->
    <div class="message-table">
      <el-table :data="messageList" style="width: 100%">
        <el-table-column prop="templateName" label="模板名称" width="200" />
        <el-table-column prop="messageType" label="消息类型" width="150" />
        <el-table-column label="发送状态" width="120">
          <template #default="{ row }">
            <el-tag
              :type="getStatusType(row.sendStatus)"
            >
              {{ getStatusText(row.sendStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="pushTime" label="推送时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">
              查看
            </el-button>
            <el-button link type="primary" @click="handleResend(row)">
              重新推送
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 新增按钮 -->
      <div class="add-actions">
        <el-button type="primary" @click="handleAdd">
          <el-icon class="btn-icon"><Plus /></el-icon>
          新增推送
        </el-button>
      </div>
    </div>

    <!-- 配置说明 -->
    <div class="permission-info">
      <div class="permission-title">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
        配置说明
      </div>
      <ul class="permission-list">
        <li>消息推送支持模板消息和文本消息两种类型</li>
        <li>可查看推送记录并支持重新推送失败消息</li>
        <li>模板消息需在微信公众平台先配置模板</li>
        <li>推送时间支持即时推送和定时推送</li>
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
import { InfoFilled, Plus } from "@element-plus/icons-vue";

interface MessageItem {
  id: string;
  templateName: string;
  messageType: 'template' | 'text';
  sendStatus: 'success' | 'failed' | 'pending';
  pushTime: string;
}

const props = defineProps({
  config: {
    type: Object as PropType<unknown>
  }
});

const emit = defineEmits<{
  (e: "update"): void;
}>();

// 消息列表
const messageList = ref<MessageItem[]>([
  {
    id: '1',
    templateName: '订单通知模板',
    messageType: 'template',
    sendStatus: 'success',
    pushTime: '2024-01-15 14:30:25'
  },
  {
    id: '2',
    templateName: '活动提醒模板',
    messageType: 'template',
    sendStatus: 'failed',
    pushTime: '2024-01-15 15:20:10'
  },
  {
    id: '3',
    templateName: '会员升级通知',
    messageType: 'text',
    sendStatus: 'pending',
    pushTime: '2024-01-15 16:00:00'
  },
  {
    id: '4',
    templateName: '生日祝福模板',
    messageType: 'template',
    sendStatus: 'success',
    pushTime: '2024-01-14 09:15:30'
  },
  {
    id: '5',
    templateName: '优惠券到账提醒',
    messageType: 'template',
    sendStatus: 'success',
    pushTime: '2024-01-14 10:45:00'
  }
]);

// 监听 props.config 变化
watch(
  () => props.config,
  (newVal: any) => {
    if (newVal && newVal.messageList) {
      messageList.value = newVal.messageList;
    }
  },
  { immediate: true, deep: true }
);

// 获取状态类型
const getStatusType = (status: string) => {
  const statusMap: Record<string, any> = {
    success: 'success',
    failed: 'danger',
    pending: 'warning'
  };
  return statusMap[status] || 'info';
};

// 获取状态文本
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    success: '成功',
    failed: '失败',
    pending: '待发送'
  };
  return statusMap[status] || '未知';
};

// 查看消息
const handleView = (row: MessageItem) => {
  console.log('查看消息:', row);
  // TODO: 打开详情弹窗
};

// 重新推送
const handleResend = async (row: MessageItem) => {
  try {
    // TODO: 调用重新推送接口
    ElMessage.success(`消息「${row.templateName}」已重新加入推送队列`);
  } catch (error) {
    ElMessage.error('重新推送失败，请稍后重试');
  }
};

// 新增推送
const handleAdd = () => {
  console.log('新增推送');
  // TODO: 打开新增弹窗
};

// 保存配置
const handleSave = async () => {
  try {
    const params = {
      messageList: messageList.value
    };
    // TODO: 调用保存接口
    console.log('保存消息推送配置:', params);
    ElMessage.success('保存成功');
    emit('update');
  } catch (error) {
    ElMessage.error('保存失败，请稍后重试');
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

  .message-table {
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

    .add-actions {
      margin-top: 16px;
      display: flex;
      justify-content: flex-end;

      .el-button {
        display: flex;
        align-items: center;
        gap: 6px;

        .btn-icon {
          font-size: 14px;
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
