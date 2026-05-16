<template>
  <div class="message-push-card">
    <div class="card-header">
      <h2 class="card-title">消息推送配置</h2>
      <div class="header-actions">
        <el-button class="action-btn" @click="handleCancel">取消</el-button>
        <el-button type="primary" class="action-btn" @click="handleSave"
          >保存</el-button
        >
      </div>
    </div>

    <!-- 操作区域 -->
    <div class="action-bar">
      <div class="action-bar-left">
        <el-button type="primary" @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新状态
        </el-button>
        <el-button @click="handleTestPush">
          <el-icon>< Promotion /></el-icon>
          测试推送
        </el-button>
      </div>
      <div class="action-bar-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索消息类型"
          clearable
          style="width: 240px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 消息推送说明 -->
    <div class="push-info">
      <div class="info-title">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
        消息推送说明
      </div>
      <div class="info-content">
        <p>
          消息推送功能让您能够实时接收有赞商城的重要业务事件通知，包括订单状态变更、商品信息变更、客户行为等。通过订阅消息，您可以及时响应业务变化，实现自动化处理。
        </p>
        <ul class="info-features">
          <li>支持多种业务消息类型：订单、商品、客户、营销、退款等</li>
          <li>实时推送：事件发生后立即推送到配置的回调地址</li>
          <li>灵活过滤：支持按条件过滤消息，只接收需要的消息</li>
          <li>可靠性保障：支持失败重试和消息确认机制</li>
        </ul>
      </div>
    </div>

    <!-- 消息类型列表 -->
    <div class="table-container">
      <div class="table-header">
        <h3 class="table-title">消息类型订阅列表</h3>
        <div class="subscribe-stats">
          <span class="stat-item">
            已订阅：<strong>{{ subscribedCount }}</strong>
          </span>
          <span class="stat-item">
            总数：<strong>{{ tableData.length }}</strong>
          </span>
        </div>
      </div>

      <el-table
        :data="filteredTableData"
        style="width: 100%"
        row-key="msgId"
        border
      >
        <el-table-column
          prop="msgName"
          label="消息名称"
          min-width="200"
        >
          <template #default="{ row }">
            <div class="msg-name-cell">
              <el-icon class="msg-icon" :color="row.iconColor">
                <component :is="row.icon" />
              </el-icon>
              <span class="msg-name">{{ row.msgName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="msgCode" label="消息代码" width="200">
          <template #default="{ row }">
            <span class="msg-code">{{ row.msgCode }}</span>
          </template>
        </el-table-column>

        <el-table-column
          prop="msgDesc"
          label="消息描述"
          min-width="250"
          show-overflow-tooltip
        />

        <el-table-column label="订阅状态" width="120" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.subscribed"
              active-color="#fa8c16"
              inactive-color="#d9d9d9"
              @change="handleSubscribeChange(row)"
            />
          </template>
        </el-table-column>

        <el-table-column label="推送状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag
              v-if="row.subscribed"
              :type="getPushStatusType(row.pushStatus)"
              size="small"
            >
              {{ getPushStatusText(row.pushStatus) }}
            </el-tag>
            <span v-else class="no-subscribe">未订阅</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              size="small"
              @click="handleConfig(row)"
              :disabled="!row.subscribed"
            >
              配置
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty
        v-if="filteredTableData.length === 0"
        description="暂无消息类型配置"
        :image-size="100"
      />
    </div>

    <!-- 配置对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="`配置 ${currentMsg?.msgName} 消息推送`"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="dialogFormRef"
        :model="dialogFormData"
        label-width="140px"
        label-position="left"
      >
        <el-form-item label="消息名称">
          <el-input v-model="dialogFormData.msgName" disabled />
        </el-form-item>

        <el-form-item label="消息代码">
          <el-input v-model="dialogFormData.msgCode" disabled />
        </el-form-item>

        <el-form-item label="回调URL">
          <el-input
            v-model="dialogFormData.callbackUrl"
            placeholder="接收消息推送的回调地址"
          />
        </el-form-item>

        <el-form-item label="消息格式">
          <el-radio-group v-model="dialogFormData.msgFormat">
            <el-radio label="JSON">JSON格式</el-radio>
            <el-radio label="XML">XML格式</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="签名方式">
          <el-select
            v-model="dialogFormData.signMethod"
            placeholder="选择签名方式"
            style="width: 100%"
          >
            <el-option label="MD5" value="md5" />
            <el-option label="SHA256" value="sha256" />
            <el-option label="HMAC-SHA256" value="hmac-sha256" />
          </el-select>
        </el-form-item>

        <el-divider content-position="left">消息过滤</el-divider>

        <el-form-item label="启用过滤">
          <el-switch v-model="dialogFormData.enableFilter" />
        </el-form-item>

        <el-form-item
          v-if="dialogFormData.enableFilter"
          label="过滤条件"
        >
          <el-input
            v-model="dialogFormData.filterCondition"
            type="textarea"
            :rows="3"
            placeholder="输入过滤条件（JSON格式），如：{&quot;status&quot;: &quot;paid&quot;}"
          />
        </el-form-item>

        <el-form-item
          v-if="dialogFormData.enableFilter"
          label="过滤字段"
        >
          <el-select
            v-model="dialogFormData.filterFields"
            placeholder="选择需要过滤的字段"
            style="width: 100%"
            multiple
          >
            <el-option label="订单状态（status）" value="status" />
            <el-option label="支付状态（pay_status）" value="pay_status" />
            <el-option label="订单金额（payment）" value="payment" />
            <el-option label="客户等级（customer_level）" value="customer_level" />
          </el-select>
        </el-form-item>

        <el-divider content-position="left">重试配置</el-divider>

        <el-form-item label="启用重试">
          <el-switch v-model="dialogFormData.enableRetry" />
        </el-form-item>

        <el-form-item
          v-if="dialogFormData.enableRetry"
          label="最大重试次数"
        >
          <el-input-number
            v-model="dialogFormData.maxRetries"
            :min="1"
            :max="10"
          />
        </el-form-item>

        <el-form-item
          v-if="dialogFormData.enableRetry"
          label="重试间隔"
        >
          <el-select
            v-model="dialogFormData.retryInterval"
            placeholder="选择重试间隔"
            style="width: 100%"
          >
            <el-option label="1分钟" value="1m" />
            <el-option label="5分钟" value="5m" />
            <el-option label="10分钟" value="10m" />
            <el-option label="30分钟" value="30m" />
          </el-select>
        </el-form-item>

        <el-form-item label="失败告警">
          <el-switch v-model="dialogFormData.enableAlert" />
        </el-form-item>

        <el-form-item v-if="dialogFormData.enableAlert" label="告警方式">
          <el-checkbox-group v-model="dialogFormData.alertMethods">
            <el-checkbox label="email">邮件</el-checkbox>
            <el-checkbox label="sms">短信</el-checkbox>
            <el-checkbox label="webhook">Webhook</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleDialogSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from "vue";
import { ElMessage, type FormInstance } from "element-plus";
import {
  Refresh,
  Promotion,
  Search,
  InfoFilled,
  ShoppingCart,
  Box,
  User,
  Ticket,
  Money,
  Bell
} from "@element-plus/icons-vue";

const props = defineProps({
  config: {
    type: Object as PropType<unknown>
  }
});

const emit = defineEmits<{
  (e: "update"): void;
}>();

const dialogFormRef = ref<FormInstance>();
const searchKeyword = ref("");
const dialogVisible = ref(false);
const currentMsg = ref<any>(null);

// 表格数据
const tableData = ref([
  {
    msgId: 1,
    msgName: "订单消息",
    msgCode: "trade_OrderMsg",
    msgDesc: "订单创建、支付、发货、完成等状态变更通知",
    icon: ShoppingCart,
    iconColor: "#fa8c16",
    subscribed: true,
    pushStatus: "normal",
    config: {
      callbackUrl: "",
      msgFormat: "JSON",
      signMethod: "md5",
      enableFilter: false,
      filterCondition: "",
      filterFields: [],
      enableRetry: true,
      maxRetries: 3,
      retryInterval: "5m",
      enableAlert: false,
      alertMethods: []
    }
  },
  {
    msgId: 2,
    msgName: "商品消息",
    msgCode: "item_ItemMsg",
    msgDesc: "商品上架、下架、库存变更、价格调整等通知",
    icon: Box,
    iconColor: "#52c41a",
    subscribed: true,
    pushStatus: "normal",
    config: {
      callbackUrl: "",
      msgFormat: "JSON",
      signMethod: "md5",
      enableFilter: false,
      filterCondition: "",
      filterFields: [],
      enableRetry: true,
      maxRetries: 3,
      retryInterval: "5m",
      enableAlert: false,
      alertMethods: []
    }
  },
  {
    msgId: 3,
    msgName: "客户消息",
    msgCode: "user_UserMsg",
    msgDesc: "新客户注册、信息变更、等级变更等通知",
    icon: User,
    iconColor: "#1890ff",
    subscribed: true,
    pushStatus: "normal",
    config: {
      callbackUrl: "",
      msgFormat: "JSON",
      signMethod: "md5",
      enableFilter: false,
      filterCondition: "",
      filterFields: [],
      enableRetry: true,
      maxRetries: 3,
      retryInterval: "5m",
      enableAlert: false,
      alertMethods: []
    }
  },
  {
    msgId: 4,
    msgName: "营销消息",
    msgCode: "ump_PromotionMsg",
    msgDesc: "优惠券发放、活动开始、促销变更等通知",
    icon: Ticket,
    iconColor: "#eb2f96",
    subscribed: false,
    pushStatus: "pending",
    config: {
      callbackUrl: "",
      msgFormat: "JSON",
      signMethod: "md5",
      enableFilter: false,
      filterCondition: "",
      filterFields: [],
      enableRetry: true,
      maxRetries: 3,
      retryInterval: "5m",
      enableAlert: false,
      alertMethods: []
    }
  },
  {
    msgId: 5,
    msgName: "退款消息",
    msgCode: "refund_RefundMsg",
    msgDesc: "退款申请、退款成功、退款失败等通知",
    icon: Money,
    iconColor: "#f5222d",
    subscribed: true,
    pushStatus: "normal",
    config: {
      callbackUrl: "",
      msgFormat: "JSON",
      signMethod: "md5",
      enableFilter: false,
      filterCondition: "",
      filterFields: [],
      enableRetry: true,
      maxRetries: 3,
      retryInterval: "5m",
      enableAlert: false,
      alertMethods: []
    }
  },
  {
    msgId: 6,
    msgName: "系统消息",
    msgCode: "system_SystemMsg",
    msgDesc: "系统公告、维护通知、重要提醒等",
    icon: Bell,
    iconColor: "#722ed1",
    subscribed: false,
    pushStatus: "pending",
    config: {
      callbackUrl: "",
      msgFormat: "JSON",
      signMethod: "md5",
      enableFilter: false,
      filterCondition: "",
      filterFields: [],
      enableRetry: true,
      maxRetries: 3,
      retryInterval: "5m",
      enableAlert: false,
      alertMethods: []
    }
  }
]);

// 对话框表单数据
const dialogFormData = reactive({
  msgId: 0,
  msgName: "",
  msgCode: "",
  callbackUrl: "",
  msgFormat: "JSON",
  signMethod: "md5",
  enableFilter: false,
  filterCondition: "",
  filterFields: [],
  enableRetry: false,
  maxRetries: 3,
  retryInterval: "5m",
  enableAlert: false,
  alertMethods: []
});

// 过滤后的表格数据
const filteredTableData = computed(() => {
  if (!searchKeyword.value) {
    return tableData.value;
  }
  return tableData.value.filter(
    item =>
      item.msgName.includes(searchKeyword.value) ||
      item.msgCode.includes(searchKeyword.value) ||
      item.msgDesc.includes(searchKeyword.value)
  );
});

// 已订阅数量
const subscribedCount = computed(() => {
  return tableData.value.filter(item => item.subscribed).length;
});

// 获取推送状态类型
const getPushStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    normal: "success",
    error: "danger",
    paused: "warning",
    pending: "info"
  };
  return typeMap[status] || "info";
};

// 获取推送状态文本
const getPushStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    normal: "推送正常",
    error: "推送异常",
    paused: "已暂停",
    pending: "待推送"
  };
  return textMap[status] || "未知";
};

// 刷新状态
const handleRefresh = () => {
  ElMessage.success("状态已刷新");
};

// 测试推送
const handleTestPush = () => {
  ElMessage.success("测试推送已发送，请检查回调地址");
};

// 订阅状态变化
const handleSubscribeChange = (row: any) => {
  if (row.subscribed) {
    row.pushStatus = "normal";
    ElMessage.success(`已订阅"${row.msgName}"`);
  } else {
    row.pushStatus = "pending";
    ElMessage.info(`已取消订阅"${row.msgName}"`);
  }
};

// 配置
const handleConfig = (row: any) => {
  currentMsg.value = row;
  dialogFormData.msgId = row.msgId;
  dialogFormData.msgName = row.msgName;
  dialogFormData.msgCode = row.msgCode;
  dialogFormData.callbackUrl = row.config?.callbackUrl || "";
  dialogFormData.msgFormat = row.config?.msgFormat || "JSON";
  dialogFormData.signMethod = row.config?.signMethod || "md5";
  dialogFormData.enableFilter = row.config?.enableFilter || false;
  dialogFormData.filterCondition = row.config?.filterCondition || "";
  dialogFormData.filterFields = row.config?.filterFields || [];
  dialogFormData.enableRetry = row.config?.enableRetry || false;
  dialogFormData.maxRetries = row.config?.maxRetries || 3;
  dialogFormData.retryInterval = row.config?.retryInterval || "5m";
  dialogFormData.enableAlert = row.config?.enableAlert || false;
  dialogFormData.alertMethods = row.config?.alertMethods || [];
  dialogVisible.value = true;
};

// 对话框保存
const handleDialogSave = () => {
  if (!currentMsg.value) return;

  // 更新当前消息的配置
  const index = tableData.value.findIndex(
    item => item.msgId === dialogFormData.msgId
  );
  if (index !== -1) {
    tableData.value[index].config = {
      callbackUrl: dialogFormData.callbackUrl,
      msgFormat: dialogFormData.msgFormat,
      signMethod: dialogFormData.signMethod,
      enableFilter: dialogFormData.enableFilter,
      filterCondition: dialogFormData.filterCondition,
      filterFields: dialogFormData.filterFields,
      enableRetry: dialogFormData.enableRetry,
      maxRetries: dialogFormData.maxRetries,
      retryInterval: dialogFormData.retryInterval,
      enableAlert: dialogFormData.enableAlert,
      alertMethods: dialogFormData.alertMethods
    };
  }

  ElMessage.success("配置保存成功");
  dialogVisible.value = false;
};

// 对话框关闭
const handleDialogClose = () => {
  dialogFormRef.value?.resetFields();
};

// 取消
const handleCancel = () => {
  ElMessage.info("已取消修改");
};

// 保存
const handleSave = () => {
  // TODO: 调用保存接口
  ElMessage.success("保存成功");
  emit("update");
};
</script>

<style scoped lang="scss">
.message-push-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .card-title {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }

    .header-actions {
      display: flex;
      gap: 12px;

      .action-btn {
        width: 80px;
      }
    }
  }

  .action-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .action-bar-left {
      display: flex;
      gap: 12px;
    }
  }

  .push-info {
    margin-bottom: 16px;
    padding: 20px;
    background: linear-gradient(135deg, #fff7e6 0%, #ffe7ba 100%);
    border: 1px solid #ffd591;
    border-radius: 6px;

    .info-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 15px;
      font-weight: 600;
      color: #fa8c16;
      margin-bottom: 12px;

      .info-icon {
        font-size: 18px;
      }
    }

    .info-content {
      p {
        margin: 0 0 12px;
        font-size: 14px;
        color: #333;
        line-height: 1.6;
      }

      .info-features {
        margin: 0;
        padding-left: 20px;

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
  }

  .table-container {
    .table-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .table-title {
        margin: 0;
        font-size: 15px;
        font-weight: 600;
        color: #333;
      }

      .subscribe-stats {
        display: flex;
        gap: 24px;

        .stat-item {
          font-size: 14px;
          color: #666;

          strong {
            color: #fa8c16;
            font-size: 16px;
          }
        }
      }
    }

    :deep(.el-table) {
      font-size: 14px;

      .el-table__header {
        th {
          background-color: #fafafa;
          color: #666;
          font-weight: 500;
        }
      }

      .el-table__body {
        td {
          color: #333;
        }
      }
    }

    .msg-name-cell {
      display: flex;
      align-items: center;
      gap: 8px;

      .msg-icon {
        font-size: 18px;
      }

      .msg-name {
        font-weight: 500;
        color: #333;
      }
    }

    .msg-code {
      font-family: "Courier New", monospace;
      color: #fa8c16;
      font-size: 13px;
    }

    .no-subscribe {
      color: #999;
      font-size: 13px;
    }
  }

  :deep(.el-dialog__body) {
    padding: 20px;
  }

  :deep(.el-checkbox-group) {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;

    .el-checkbox {
      margin-right: 0;
    }
  }
}
</style>
