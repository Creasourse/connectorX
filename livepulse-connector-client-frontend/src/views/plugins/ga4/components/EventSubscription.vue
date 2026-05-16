<template>
  <div class="event-subscription-page">
    <div class="config-card">
      <div class="page-header">
        <h2 class="config-title">事件订阅配置</h2>
        <el-button type="primary" @click="handleAddSubscription">
          添加订阅
        </el-button>
      </div>

      <!-- 订阅列表表格 -->
      <el-table :data="subscriptionList" class="subscription-table" border>
        <el-table-column type="index" label="编号" width="60" align="center" />
        <el-table-column prop="subscriptionName" label="订阅名称" min-width="180" />
        <el-table-column prop="eventType" label="事件类型" width="140">
          <template #default="{ row }">
            <el-tag :type="getEventTypeTag(row.eventType)" size="small">
              {{ getEventTypeName(row.eventType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="eventName" label="事件名称/筛选条件" min-width="220">
          <template #default="{ row }">
            <div v-if="row.eventType === 'custom'" class="event-name-cell">
              <el-tag
                v-for="(event, index) in row.eventNames"
                :key="index"
                size="small"
                style="margin-right: 4px; margin-bottom: 4px"
              >
                {{ event }}
              </el-tag>
            </div>
            <span v-else>{{ row.filterCondition || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="callbackUrl" label="回调地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.enabled"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="lastTriggerTime" label="最后触发时间" width="180" />
        <el-table-column prop="triggerCount" label="触发次数" width="100" align="right">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.triggerCount }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleTest(row)">
              测试
            </el-button>
            <el-button type="primary" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 统计信息 -->
      <div class="subscription-summary">
        <div class="summary-item">
          <span class="label">总订阅数：</span>
          <span class="value">{{ subscriptionList.length }}</span>
        </div>
        <div class="summary-item">
          <span class="label">已启用：</span>
          <span class="value success">{{ enabledCount }}</span>
        </div>
        <div class="summary-item">
          <span class="label">今日触发：</span>
          <span class="value primary">{{ todayTriggerCount }}</span>
        </div>
      </div>
    </div>

    <!-- 配置说明 -->
    <div class="config-instruction">
      <div class="instruction-title">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
        事件订阅说明
      </div>
      <div class="instruction-content">
        <h4>支持的订阅类型：</h4>
        <ul>
          <li><strong>实时事件订阅：</strong>监听GA4实时事件，支持自定义事件筛选</li>
          <li><strong>转换事件订阅：</strong>订阅关键转化事件，如购买、注册等</li>
          <li><strong>异常事件订阅：</strong>订阅异常或错误事件，及时发现问题</li>
          <li><strong>定期报告订阅：</strong>按周期生成数据报告并推送到回调地址</li>
        </ul>

        <h4>回调格式：</h4>
        <ul>
          <li>请求方式：POST (application/json)</li>
          <li>数据格式：包含事件名称、事件参数、用户属性、时间戳等</li>
          <li>重试机制：失败自动重试3次，间隔1分钟、5分钟、10分钟</li>
        </ul>

        <h4>最佳实践：</h4>
        <ul>
          <li>回调地址必须公网可访问，建议使用HTTPS</li>
          <li>处理逻辑要快速响应，避免超时（建议5秒内返回）</li>
          <li>对回调数据进行签名验证，确保数据来源可靠</li>
          <li>合理设置筛选条件，避免无效回调</li>
        </ul>
      </div>
    </div>

    <!-- 添加/编辑订阅对话框 -->
    <el-dialog
      v-model="subscriptionDialogVisible"
      :title="isEdit ? '编辑事件订阅' : '添加事件订阅'"
      width="700px"
    >
      <el-form :model="subscriptionForm" label-width="140px">
        <el-form-item label="订阅名称" required>
          <el-input
            v-model="subscriptionForm.subscriptionName"
            placeholder="请输入订阅名称，便于识别"
          />
        </el-form-item>

        <el-form-item label="事件类型" required>
          <el-select
            v-model="subscriptionForm.eventType"
            placeholder="请选择事件类型"
            style="width: 100%"
            @change="handleEventTypeChange"
          >
            <el-option label="实时事件" value="realtime" />
            <el-option label="转化事件" value="conversion" />
            <el-option label="异常事件" value="exception" />
            <el-option label="定期报告" value="report" />
          </el-select>
        </el-form-item>

        <!-- 实时事件：选择具体事件 -->
        <template v-if="subscriptionForm.eventType === 'realtime'">
          <el-form-item label="事件名称" required>
            <el-select
              v-model="subscriptionForm.eventNames"
              multiple
              filterable
              allow-create
              placeholder="请选择或输入事件名称"
              style="width: 100%"
            >
              <el-option label="page_view" value="page_view" />
              <el-option label="session_start" value="session_start" />
              <el-option label="user_engagement" value="user_engagement" />
              <el-option label="first_visit" value="first_visit" />
              <el-option label="scroll" value="scroll" />
              <el-option label="click" value="click" />
              <el-option label="file_download" value="file_download" />
              <el-option label="video_start" value="video_start" />
            </el-select>
            <div class="form-tip">
              支持选择预定义事件或输入自定义事件名称
            </div>
          </el-form-item>
        </template>

        <!-- 转化事件：选择转化目标 -->
        <template v-if="subscriptionForm.eventType === 'conversion'">
          <el-form-item label="转化事件" required>
            <el-select
              v-model="subscriptionForm.eventNames"
              multiple
              placeholder="请选择转化事件"
              style="width: 100%"
            >
              <el-option label="purchase" value="purchase" />
              <el-option label="add_to_cart" value="add_to_cart" />
              <el-option label="begin_checkout" value="begin_checkout" />
              <el-option label="sign_up" value="sign_up" />
              <el-option label="generate_lead" value="generate_lead" />
            </el-select>
          </el-form-item>
        </template>

        <!-- 异常事件：异常类型 -->
        <template v-if="subscriptionForm.eventType === 'exception'">
          <el-form-item label="异常类型" required>
            <el-checkbox-group v-model="subscriptionForm.exceptionTypes">
              <el-checkbox label="error">页面错误</el-checkbox>
              <el-checkbox label="crash">应用崩溃</el-checkbox>
              <el-checkbox label="timeout">超时异常</el-checkbox>
              <el-checkbox label="api_error">API错误</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </template>

        <!-- 定期报告：报告配置 -->
        <template v-if="subscriptionForm.eventType === 'report'">
          <el-form-item label="报告类型" required>
            <el-select
              v-model="subscriptionForm.reportType"
              placeholder="请选择报告类型"
              style="width: 100%"
            >
              <el-option label="每日报告" value="daily" />
              <el-option label="每周报告" value="weekly" />
              <el-option label="每月报告" value="monthly" />
            </el-select>
          </el-form-item>

          <el-form-item label="报告时间" required>
            <el-time-picker
              v-model="subscriptionForm.reportTime"
              format="HH:mm"
              value-format="HH:mm"
              placeholder="选择报告生成时间"
            />
          </el-form-item>
        </template>

        <!-- 通用筛选条件 -->
        <el-form-item label="筛选条件">
          <el-input
            v-model="subscriptionForm.filterCondition"
            type="textarea"
            :rows="3"
            placeholder="输入筛选条件，JSON格式&#10;示例：{&quot;country&quot;: &quot;CN&quot;, &quot;device&quot;: &quot;mobile&quot;}"
          />
          <div class="form-tip">
            可选：根据事件参数、用户属性等设置筛选条件
          </div>
        </el-form-item>

        <el-form-item label="回调地址" required>
          <el-input
            v-model="subscriptionForm.callbackUrl"
            placeholder="https://your-domain.com/ga4-webhook"
          />
          <div class="form-tip">
            必须是公网可访问的HTTPS地址
          </div>
        </el-form-item>

        <el-form-item label="请求头配置">
          <el-input
            v-model="subscriptionForm.headers"
            type="textarea"
            :rows="3"
            placeholder='JSON格式，示例：{&quot;Authorization&quot;: &quot;Bearer xxx&quot;}'"
          />
          <div class="form-tip">
            可选：添加自定义请求头，如认证信息
          </div>
        </el-form-item>

        <el-form-item label="订阅状态">
          <el-switch
            v-model="subscriptionForm.enabled"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>

        <el-form-item label="重试次数">
          <el-input-number
            v-model="subscriptionForm.retryCount"
            :min="0"
            :max="10"
          />
          <span class="form-tip">次</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="subscriptionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmSave">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 测试订阅对话框 -->
    <el-dialog
      v-model="testDialogVisible"
      title="测试事件订阅"
      width="600px"
    >
      <div class="test-content">
        <div class="test-info">
          <el-icon class="test-icon"><InfoFilled /></el-icon>
          <span>将发送测试事件到回调地址</span>
        </div>

        <el-form label-width="120px">
          <el-form-item label="回调地址">
            <el-input :value="testSubscription?.callbackUrl" readonly />
          </el-form-item>

          <el-form-item label="测试事件">
            <el-select v-model="testEventType" style="width: 100%">
              <el-option label="page_view" value="page_view" />
              <el-option label="purchase" value="purchase" />
              <el-option label="exception" value="exception" />
            </el-select>
          </el-form-item>

          <el-form-item label="测试数据">
            <el-input
              type="textarea"
              :rows="6"
              :model-value="testEventData"
              readonly
              class="test-data-textarea"
            />
          </el-form-item>
        </el-form>

        <div class="test-result" v-if="testResult">
          <div class="result-title">测试结果：</div>
          <el-alert
            :type="testResult.success ? 'success' : 'error'"
            :title="testResult.success ? '发送成功' : '发送失败'"
            :description="testResult.message"
            show-icon
            :closable="false"
          />
        </div>
      </div>
      <template #footer>
        <el-button @click="testDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleRunTest" :loading="testing">
          发送测试
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { InfoFilled } from "@element-plus/icons-vue";

// 订阅列表
const subscriptionList = ref([
  {
    id: 1,
    subscriptionName: "实时用户行为监听",
    eventType: "realtime",
    eventNames: ["page_view", "user_engagement", "session_start"],
    filterCondition: '{"device": "mobile"}',
    callbackUrl: "https://api.example.com/ga4/webhook/realtime",
    enabled: true,
    lastTriggerTime: "2024-01-15 14:30:25",
    triggerCount: 15234
  },
  {
    id: 2,
    subscriptionName: "购买转化通知",
    eventType: "conversion",
    eventNames: ["purchase", "add_to_cart"],
    filterCondition: '{"country": "CN"}',
    callbackUrl: "https://api.example.com/ga4/webhook/conversion",
    enabled: true,
    lastTriggerTime: "2024-01-15 14:28:15",
    triggerCount: 856
  },
  {
    id: 3,
    subscriptionName: "异常监控告警",
    eventType: "exception",
    exceptionTypes: ["error", "crash"],
    filterCondition: "",
    callbackUrl: "https://api.example.com/ga4/webhook/exception",
    enabled: true,
    lastTriggerTime: "2024-01-15 12:15:00",
    triggerCount: 23
  },
  {
    id: 4,
    subscriptionName: "每日数据报告",
    eventType: "report",
    reportType: "daily",
    reportTime: "09:00",
    filterCondition: "",
    callbackUrl: "https://api.example.com/ga4/webhook/report",
    enabled: false,
    lastTriggerTime: "2024-01-14 09:00:00",
    triggerCount: 45
  }
]);

// 已启用数量
const enabledCount = computed(() => {
  return subscriptionList.value.filter(item => item.enabled).length;
});

// 今日触发次数
const todayTriggerCount = computed(() => {
  return 12567;
});

// 订阅对话框
const subscriptionDialogVisible = ref(false);
const isEdit = ref(false);
const subscriptionForm = ref({
  id: null,
  subscriptionName: "",
  eventType: "realtime",
  eventNames: [],
  exceptionTypes: [],
  reportType: "daily",
  reportTime: "09:00",
  filterCondition: "",
  callbackUrl: "",
  headers: "",
  enabled: true,
  retryCount: 3
});

// 测试对话框
const testDialogVisible = ref(false);
const testSubscription = ref<any>(null);
const testEventType = ref("page_view");
const testResult = ref<any>(null);
const testing = ref(false);

// 测试数据示例
const testEventData = computed(() => {
  const examples: Record<string, string> = {
    page_view: JSON.stringify({
      event_name: "page_view",
      event_timestamp: "1705305625000000",
      user_pseudo_id: "abc123xyz",
      event_params: [
        { key: "page_location", value: { string_value: "https://example.com/page" } },
        { key: "page_title", value: { string_value: "示例页面" } }
      ],
      user_properties: [
        { key: "country", value: { string_value: "CN" } },
        { key: "device_category", value: { string_value: "mobile" } }
      ]
    }, null, 2),
    purchase: JSON.stringify({
      event_name: "purchase",
      event_timestamp: "1705305625000000",
      user_pseudo_id: "abc123xyz",
      event_params: [
        { key: "transaction_id", value: { string_value: "ORDER_12345" } },
        { key: "currency", value: { string_value: "CNY" } },
        { key: "value", value: { double_value: 299.00 } },
        { key: "items", value: { array_value: { values: [{ string_value: "product_123" }] } } }
      ]
    }, null, 2),
    exception: JSON.stringify({
      event_name: "exception",
      event_timestamp: "1705305625000000",
      user_pseudo_id: "abc123xyz",
      event_params: [
        { key: "error_type", value: { string_value: "javascript_error" } },
        { key: "error_message", value: { string_value: "Uncaught TypeError" } },
        { key: "error_url", value: { string_value: "https://example.com/app.js" } }
      ]
    }, null, 2)
  };
  return examples[testEventType.value] || examples.page_view;
});

// 获取事件类型标签
const getEventTypeTag = (type: string) => {
  const tagMap: Record<string, string> = {
    realtime: "primary",
    conversion: "success",
    exception: "danger",
    report: "warning"
  };
  return tagMap[type] || "";
};

// 获取事件类型名称
const getEventTypeName = (type: string) => {
  const nameMap: Record<string, string> = {
    realtime: "实时事件",
    conversion: "转化事件",
    exception: "异常事件",
    report: "定期报告"
  };
  return nameMap[type] || type;
};

// 事件类型变化
const handleEventTypeChange = () => {
  subscriptionForm.value.eventNames = [];
  subscriptionForm.value.exceptionTypes = [];
};

// 添加订阅
const handleAddSubscription = () => {
  isEdit.value = false;
  subscriptionForm.value = {
    id: null,
    subscriptionName: "",
    eventType: "realtime",
    eventNames: [],
    exceptionTypes: [],
    reportType: "daily",
    reportTime: "09:00",
    filterCondition: "",
    callbackUrl: "",
    headers: "",
    enabled: true,
    retryCount: 3
  };
  subscriptionDialogVisible.value = true;
};

// 编辑订阅
const handleEdit = (row: any) => {
  isEdit.value = true;
  subscriptionForm.value = { ...row };
  subscriptionDialogVisible.value = true;
};

// 确认保存
const handleConfirmSave = () => {
  if (!subscriptionForm.value.subscriptionName || !subscriptionForm.value.callbackUrl) {
    ElMessage.warning("请完整填写必填信息");
    return;
  }

  if (subscriptionForm.value.eventType === "realtime" && subscriptionForm.value.eventNames.length === 0) {
    ElMessage.warning("请选择至少一个事件名称");
    return;
  }

  if (subscriptionForm.value.eventType === "conversion" && subscriptionForm.value.eventNames.length === 0) {
    ElMessage.warning("请选择至少一个转化事件");
    return;
  }

  if (subscriptionForm.value.eventType === "exception" && subscriptionForm.value.exceptionTypes.length === 0) {
    ElMessage.warning("请选择至少一个异常类型");
    return;
  }

  if (isEdit.value) {
    const index = subscriptionList.value.findIndex(s => s.id === subscriptionForm.value.id);
    if (index > -1) {
      subscriptionList.value[index] = { ...subscriptionForm.value };
    }
    ElMessage.success("事件订阅已更新");
  } else {
    subscriptionList.value.push({
      ...subscriptionForm.value,
      id: Date.now(),
      lastTriggerTime: "-",
      triggerCount: 0
    });
    ElMessage.success("事件订阅添加成功");
  }

  subscriptionDialogVisible.value = false;
};

// 状态变化
const handleStatusChange = (row: any) => {
  const status = row.enabled ? "已启用" : "已禁用";
  ElMessage.success(`${row.subscriptionName} ${status}`);
};

// 测试订阅
const handleTest = (row: any) => {
  testSubscription.value = row;
  testEventType.value = row.eventType === "realtime" ? "page_view" :
                       row.eventType === "conversion" ? "purchase" :
                       row.eventType === "exception" ? "exception" : "page_view";
  testResult.value = null;
  testDialogVisible.value = true;
};

// 运行测试
const handleRunTest = () => {
  testing.value = true;

  // TODO: 调用测试接口
  setTimeout(() => {
    const success = Math.random() > 0.3; // 模拟70%成功率
    testResult.value = {
      success,
      message: success
        ? "测试事件已成功发送到回调地址"
        : "发送失败：连接超时，请检查回调地址是否可访问"
    };
    testing.value = false;
  }, 1500);
};

// 删除订阅
const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    `确定要删除事件订阅"${row.subscriptionName}"吗？删除后将无法继续接收相关事件通知。`,
    "警告",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    }
  ).then(() => {
    const index = subscriptionList.value.findIndex(s => s.id === row.id);
    if (index > -1) {
      subscriptionList.value.splice(index, 1);
    }
    ElMessage.success("事件订阅删除成功");
  });
};
</script>

<style scoped lang="scss">
.event-subscription-page {
  .config-card {
    background-color: #fff;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
    margin-bottom: 16px;

    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;

      .config-title {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: #333;
      }
    }

    .subscription-table {
      width: 100%;
      margin-bottom: 24px;

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

      .event-name-cell {
        display: flex;
        flex-wrap: wrap;
        gap: 4px;
      }
    }

    .subscription-summary {
      display: flex;
      gap: 32px;
      padding: 16px;
      background-color: #f0f9ff;
      border-radius: 4px;

      .summary-item {
        display: flex;
        align-items: center;
        gap: 8px;

        .label {
          font-size: 14px;
          color: #666;
        }

        .value {
          font-size: 16px;
          font-weight: 600;
          color: #333;

          &.success {
            color: #52c41a;
          }

          &.primary {
            color: #4285f4;
          }
        }
      }
    }
  }

  .config-instruction {
    padding: 20px;
    background-color: #fef7e0;
    border: 1px solid #fbbc04;
    border-radius: 6px;

    .instruction-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      font-weight: 600;
      color: #f9ab00;
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

        strong {
          color: #4285f4;
        }
      }

      ul {
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

  .form-tip {
    margin-left: 12px;
    font-size: 12px;
    color: #999;
  }

  .test-content {
    .test-info {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px;
      margin-bottom: 20px;
      background-color: #e8f0fe;
      border-radius: 4px;
      color: #4285f4;
      font-size: 14px;

      .test-icon {
        font-size: 18px;
      }
    }

    .test-data-textarea {
      :deep(.el-textarea__inner) {
        font-family: 'Courier New', monospace;
        font-size: 12px;
        line-height: 1.5;
      }
    }

    .test-result {
      margin-top: 20px;

      .result-title {
        font-size: 14px;
        font-weight: 600;
        color: #333;
        margin-bottom: 8px;
      }
    }
  }
}
</style>
