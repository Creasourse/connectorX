<template>
  <div class="webhook-subscription-page">
    <div class="config-card">
      <h2 class="config-title">Webhook订阅配置</h2>

      <!-- Webhook URL配置 -->
      <div class="webhook-url-config">
        <div class="config-section-title">接收URL配置</div>
        <el-form :model="urlForm" label-width="140px" class="url-form">
          <el-form-item label="Webhook URL">
            <el-input
              v-model="urlForm.webhookUrl"
              placeholder="请输入接收Webhook事件的完整URL地址"
              clearable
            />
          </el-form-item>
          <el-form-item label="验证密钥">
            <el-input
              v-model="urlForm.webhookSecret"
              type="password"
              placeholder="用于验证Webhook请求的密钥"
              clearable
              show-password
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- 事件订阅列表 -->
      <div class="event-list">
        <div class="config-section-title">订阅事件列表</div>
        <el-table
          :data="eventList"
          class="event-table"
          border
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column type="index" label="编号" width="60" align="center" />
          <el-table-column prop="eventName" label="事件名称" min-width="180" />
          <el-table-column prop="eventCode" label="事件代码" min-width="200" />
          <el-table-column prop="description" label="事件说明" min-width="260" />
          <el-table-column label="订阅状态" width="100" align="center">
            <template #default="{ row }">
              <el-switch
                v-model="row.subscribed"
                @change="handleSubscribeChange(row)"
              />
            </template>
          </el-table-column>
        </el-table>

        <!-- 统计信息 -->
        <div class="event-summary">
          <span class="summary-text">
            已选择 <strong>{{ selectedEvents.length }}</strong> 个事件，
            已订阅 <strong>{{ subscribedCount }}</strong> 个事件
          </span>
        </div>
      </div>

      <!-- Webhook说明 -->
      <div class="webhook-info">
        <div class="config-section-title">Webhook说明</div>
        <ul class="info-list">
          <li>Shopee Webhook允许在特定事件发生时接收HTTP POST通知</li>
          <li>支持订单状态变更、商品信息更新、店铺信息变更等多种事件</li>
          <li>需要在Shopee开放平台配置Webhook URL并授权访问权限</li>
          <li>Webhook URL必须是公网可访问的HTTPS地址，并支持POST请求</li>
          <li>收到Webhook请求后需要返回HTTP 200状态码确认接收</li>
        </ul>
      </div>
    </div>

    <!-- 底部操作按钮 -->
    <div class="action-buttons">
      <el-button @click="handleTestWebhook">测试Webhook</el-button>
      <el-button type="primary" @click="handleSaveConfig">保存配置</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { ElMessage } from "element-plus";

// Webhook URL配置表单
const urlForm = ref({
  webhookUrl: "",
  webhookSecret: ""
});

// 事件列表数据
const eventList = ref([
  {
    id: 1,
    eventName: "订单创建",
    eventCode: "order.create",
    description: "当新订单创建时触发",
    subscribed: true
  },
  {
    id: 2,
    eventName: "订单状态变更",
    eventCode: "order.status.update",
    description: "当订单状态发生变化时触发",
    subscribed: true
  },
  {
    id: 3,
    eventName: "订单取消",
    eventCode: "order.cancel",
    description: "当订单被取消时触发",
    subscribed: false
  },
  {
    id: 4,
    eventName: "商品创建",
    eventCode: "item.create",
    description: "当新商品创建时触发",
    subscribed: true
  },
  {
    id: 5,
    eventName: "商品信息更新",
    eventCode: "item.update",
    description: "当商品信息更新时触发",
    subscribed: true
  },
  {
    id: 6,
    eventName: "商品库存更新",
    eventCode: "item.stock.update",
    description: "当商品库存变化时触发",
    subscribed: true
  },
  {
    id: 7,
    eventName: "店铺信息更新",
    eventCode: "shop.info.update",
    description: "当店铺基本信息更新时触发",
    subscribed: false
  },
  {
    id: 8,
    eventName: "物流状态更新",
    eventCode: "logistics.status.update",
    description: "当物流状态变化时触发",
    subscribed: false
  },
  {
    id: 9,
    eventName: "退款申请",
    eventCode: "refund.request",
    description: "当买家申请退款时触发",
    subscribed: false
  },
  {
    id: 10,
    eventName: "退货创建",
    eventCode: "return.create",
    description: "当创建退货请求时触发",
    subscribed: false
  },
  {
    id: 11,
    eventName: "促销活动更新",
    eventCode: "promotion.update",
    description: "当促销活动信息更新时触发",
    subscribed: false
  },
  {
    id: 12,
    eventName: "优惠券使用",
    eventCode: "voucher.use",
    description: "当优惠券被使用时触发",
    subscribed: false
  }
]);

// 已选择的事件
const selectedEvents = ref<any[]>([]);

// 已订阅数量
const subscribedCount = computed(() => {
  return eventList.value.filter(item => item.subscribed).length;
});

// 选择变化
const handleSelectionChange = (selection: any[]) => {
  selectedEvents.value = selection;
};

// 订阅状态变化
const handleSubscribeChange = (row: any) => {
  const status = row.subscribed ? "已开启" : "已关闭";
  ElMessage.success(`${row.eventName} ${status}`);
};

// 测试Webhook
const handleTestWebhook = () => {
  if (!urlForm.value.webhookUrl) {
    ElMessage.warning("请先配置Webhook URL");
    return;
  }
  // TODO: 调用测试Webhook接口
  ElMessage.success("测试Webhook已发送");
};

// 保存配置
const handleSaveConfig = () => {
  if (!urlForm.value.webhookUrl) {
    ElMessage.warning("请先配置Webhook URL");
    return;
  }

  // TODO: 调用保存配置接口
  ElMessage.success("Webhook订阅配置已保存");
};
</script>

<style scoped lang="scss">
.webhook-subscription-page {
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
      border-left: 3px solid #ee4d2d;
    }

    .webhook-url-config {
      margin-bottom: 32px;

      .url-form {
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
      }
    }

    .event-list {
      margin-bottom: 32px;

      .event-table {
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

      .event-summary {
        text-align: right;
        padding: 12px;
        background-color: #f5f5f5;
        border-radius: 4px;

        .summary-text {
          font-size: 13px;
          color: #666;

          strong {
            color: #ee4d2d;
            font-weight: 600;
          }
        }
      }
    }

    .webhook-info {
      padding: 16px;
      background-color: #fff7e6;
      border: 1px solid #ffd591;
      border-radius: 4px;

      .config-section-title {
        border-left-color: #fa8c16;
        margin-bottom: 12px;
      }

      .info-list {
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

    button {
      min-width: 120px;
    }
  }
}
</style>
