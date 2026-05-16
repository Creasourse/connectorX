<template>
  <div class="data-capture-card">
    <div class="card-header">
      <h2 class="card-title">变更数据捕获（CDC）配置</h2>
      <div class="header-actions">
        <el-button class="action-btn" @click="handleCancel">取消</el-button>
        <el-button type="primary" class="action-btn" @click="handleSave"
          >保存</el-button
        >
      </div>
    </div>

    <!-- CDC 说明 -->
    <div class="cdc-info">
      <div class="info-title">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
        什么是变更数据捕获（CDC）？
      </div>
      <div class="info-content">
        <p>
          CDC（Change Data Capture）是Salesforce提供的一种实时数据变更通知机制。通过订阅对象的CDC事件，您可以实时获取数据的创建、更新、删除等变更操作，无需频繁轮询API。
        </p>
        <ul class="info-features">
          <li>✓ 实时推送数据变更事件，降低API调用次数</li>
          <li>✓ 支持增量的数据同步，提升同步效率</li>
          <li>✓ 减少系统负载，提升整体性能</li>
          <li>✓ 支持标准对象和自定义对象</li>
        </ul>
      </div>
    </div>

    <!-- 对象订阅列表 -->
    <div class="table-container">
      <div class="table-header">
        <h3 class="table-title">订阅对象列表</h3>
        <el-button type="primary" size="small" @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新列表
        </el-button>
      </div>

      <el-table
        ref="tableRef"
        :data="tableData"
        style="width: 100%"
        row-key="objectApiName"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />

        <el-table-column
          prop="objectApiName"
          label="对象名称"
          min-width="180"
        >
          <template #default="{ row }">
            <div class="object-name">
              <el-icon class="object-icon"><Document /></el-icon>
              <span>{{ row.objectApiName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="description" label="描述" min-width="250" />

        <el-table-column label="订阅状态" width="140" align="center">
          <template #default="{ row }">
            <el-tag
              :type="getStatusType(row.subscribeStatus)"
              size="small"
            >
              {{ getStatusText(row.subscribeStatus) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="事件类型" width="180">
          <template #default="{ row }">
            <el-tag
              v-for="event in row.eventTypes"
              :key="event"
              size="small"
              style="margin-right: 4px; margin-bottom: 4px"
            >
              {{ event }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              size="small"
              @click="handleConfig(row)"
            >
              配置
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 配置对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="`配置 ${currentObject?.objectApiName} CDC订阅`"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="dialogFormRef"
        :model="dialogFormData"
        label-width="140px"
        label-position="left"
      >
        <el-form-item label="对象名称">
          <el-input v-model="dialogFormData.objectApiName" disabled />
        </el-form-item>

        <el-form-item label="描述">
          <el-input v-model="dialogFormData.description" disabled />
        </el-form-item>

        <el-form-item label="启用订阅">
          <el-switch v-model="dialogFormData.enableSubscribe" />
        </el-form-item>

        <el-form-item label="订阅事件类型">
          <el-checkbox-group
            v-model="dialogFormData.eventTypes"
            :disabled="!dialogFormData.enableSubscribe"
          >
            <el-checkbox label="create">创建（Create）</el-checkbox>
            <el-checkbox label="update">更新（Update）</el-checkbox>
            <el-checkbox label="delete">删除（Delete）</el-checkbox>
            <el-checkbox label="undelete">恢复（Undelete）</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="字段过滤">
          <el-select
            v-model="dialogFormData.fieldFilter"
            multiple
            placeholder="选择需要监听的字段（留空表示监听所有字段）"
            style="width: 100%"
            :disabled="!dialogFormData.enableSubscribe"
          >
            <el-option
              v-for="field in dialogFormData.availableFields"
              :key="field.name"
              :label="field.label"
              :value="field.name"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="回调URL">
          <el-input
            v-model="dialogFormData.callbackUrl"
            placeholder="接收CDC事件的回调地址"
            :disabled="!dialogFormData.enableSubscribe"
          />
        </el-form-item>

        <el-form-item label="重试策略">
          <el-select
            v-model="dialogFormData.retryStrategy"
            placeholder="选择重试策略"
            style="width: 100%"
            :disabled="!dialogFormData.enableSubscribe"
          >
            <el-option label="不重试" value="none" />
            <el-option label="线性退避" value="linear" />
            <el-option label="指数退避" value="exponential" />
          </el-select>
        </el-form-item>

        <el-form-item
          v-if="dialogFormData.retryStrategy !== 'none'"
          label="最大重试次数"
        >
          <el-input-number
            v-model="dialogFormData.maxRetries"
            :min="1"
            :max="10"
            :disabled="!dialogFormData.enableSubscribe"
          />
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
import { ref, reactive, onMounted } from "vue";
import { ElMessage, type FormInstance } from "element-plus";
import { InfoFilled, Document, Refresh } from "@element-plus/icons-vue";

const props = defineProps({
  config: {
    type: Object as PropType<unknown>
  }
});

const emit = defineEmits<{
  (e: "update"): void;
}>();

const tableRef = ref();
const dialogFormRef = ref<FormInstance>();
const selectedRows = ref<any[]>([]);
const dialogVisible = ref(false);
const currentObject = ref<any>(null);

// 表格数据
const tableData = ref([
  {
    objectApiName: "Account",
    description: "客户账户信息",
    subscribeStatus: "subscribed",
    eventTypes: ["create", "update", "delete"],
    config: {
      enableSubscribe: true,
      eventTypes: ["create", "update", "delete"],
      fieldFilter: [],
      callbackUrl: "",
      retryStrategy: "exponential",
      maxRetries: 3
    }
  },
  {
    objectApiName: "Contact",
    description: "联系人信息",
    subscribeStatus: "subscribed",
    eventTypes: ["create", "update"],
    config: {
      enableSubscribe: true,
      eventTypes: ["create", "update"],
      fieldFilter: [],
      callbackUrl: "",
      retryStrategy: "exponential",
      maxRetries: 3
    }
  },
  {
    objectApiName: "Opportunity",
    description: "销售商机信息",
    subscribeStatus: "unsubscribed",
    eventTypes: [],
    config: {
      enableSubscribe: false,
      eventTypes: [],
      fieldFilter: [],
      callbackUrl: "",
      retryStrategy: "exponential",
      maxRetries: 3
    }
  },
  {
    objectApiName: "Lead",
    description: "潜在客户信息",
    subscribeStatus: "unsubscribed",
    eventTypes: [],
    config: {
      enableSubscribe: false,
      eventTypes: [],
      fieldFilter: [],
      callbackUrl: "",
      retryStrategy: "exponential",
      maxRetries: 3
    }
  },
  {
    objectApiName: "Campaign",
    description: "营销活动信息",
    subscribeStatus: "pending",
    eventTypes: [],
    config: {
      enableSubscribe: false,
      eventTypes: [],
      fieldFilter: [],
      callbackUrl: "",
      retryStrategy: "exponential",
      maxRetries: 3
    }
  },
  {
    objectApiName: "Case",
    description: "客户服务工单",
    subscribeStatus: "unsubscribed",
    eventTypes: [],
    config: {
      enableSubscribe: false,
      eventTypes: [],
      fieldFilter: [],
      callbackUrl: "",
      retryStrategy: "exponential",
      maxRetries: 3
    }
  }
]);

// 对话框表单数据
const dialogFormData = reactive({
  objectApiName: "",
  description: "",
  enableSubscribe: false,
  eventTypes: [],
  fieldFilter: [],
  availableFields: [],
  callbackUrl: "",
  retryStrategy: "exponential",
  maxRetries: 3
});

// 获取状态类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    subscribed: "success",
    unsubscribed: "info",
    pending: "warning",
    failed: "danger"
  };
  return typeMap[status] || "info";
};

// 获取状态文本
const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    subscribed: "已订阅",
    unsubscribed: "未订阅",
    pending: "订阅中",
    failed: "订阅失败"
  };
  return textMap[status] || "未知";
};

// 选择变化
const handleSelectionChange = (selection: any[]) => {
  selectedRows.value = selection;
};

// 刷新列表
const handleRefresh = () => {
  // TODO: 调用刷新接口
  ElMessage.success("列表已刷新");
};

// 配置
const handleConfig = (row: any) => {
  currentObject.value = row;
  dialogFormData.objectApiName = row.objectApiName;
  dialogFormData.description = row.description;
  dialogFormData.enableSubscribe = row.config?.enableSubscribe || false;
  dialogFormData.eventTypes = row.config?.eventTypes || [];
  dialogFormData.fieldFilter = row.config?.fieldFilter || [];
  dialogFormData.callbackUrl = row.config?.callbackUrl || "";
  dialogFormData.retryStrategy = row.config?.retryStrategy || "exponential";
  dialogFormData.maxRetries = row.config?.maxRetries || 3;

  // 根据对象类型设置可用字段
  dialogFormData.availableFields = getObjectFields(row.objectApiName);

  dialogVisible.value = true;
};

// 获取对象字段
const getObjectFields = (apiName: string) => {
  const fieldMap: Record<string, any[]> = {
    Account: [
      { name: "Id", label: "ID" },
      { name: "Name", label: "客户名称" },
      { name: "Industry", label: "行业" },
      { name: "AnnualRevenue", label: "年收入" },
      { name: "BillingStreet", label: "账单街道" },
      { name: "BillingCity", label: "账单城市" },
      { name: "Type", label: "客户类型" }
    ],
    Contact: [
      { name: "Id", label: "ID" },
      { name: "FirstName", label: "名字" },
      { name: "LastName", label: "姓氏" },
      { name: "Email", label: "邮箱" },
      { name: "Phone", label: "电话" },
      { name: "AccountId", label: "账户ID" }
    ],
    Opportunity: [
      { name: "Id", label: "ID" },
      { name: "Name", label: "商机名称" },
      { name: "Amount", label: "金额" },
      { name: "StageName", label: "阶段" },
      { name: "CloseDate", label: "关闭日期" },
      { name: "AccountId", label: "账户ID" }
    ],
    Lead: [
      { name: "Id", label: "ID" },
      { name: "FirstName", label: "名字" },
      { name: "LastName", label: "姓氏" },
      { name: "Company", label: "公司" },
      { name: "Status", label: "状态" },
      { name: "Email", label: "邮箱" }
    ],
    Campaign: [
      { name: "Id", label: "ID" },
      { name: "Name", label: "活动名称" },
      { name: "Type", label: "类型" },
      { name: "Status", label: "状态" },
      { name: "StartDate", label: "开始日期" }
    ],
    Case: [
      { name: "Id", label: "ID" },
      { name: "Subject", label: "主题" },
      { name: "Status", label: "状态" },
      { name: "Priority", label: "优先级" },
      { name: "Origin", label: "来源" }
    ]
  };

  return fieldMap[apiName] || [];
};

// 对话框保存
const handleDialogSave = () => {
  if (!currentObject.value) return;

  // 更新当前对象的配置
  const index = tableData.value.findIndex(
    item => item.objectApiName === currentObject.value.objectApiName
  );
  if (index !== -1) {
    tableData.value[index].config = {
      enableSubscribe: dialogFormData.enableSubscribe,
      eventTypes: dialogFormData.eventTypes,
      fieldFilter: dialogFormData.fieldFilter,
      callbackUrl: dialogFormData.callbackUrl,
      retryStrategy: dialogFormData.retryStrategy,
      maxRetries: dialogFormData.maxRetries
    };

    // 更新订阅状态
    tableData.value[index].subscribeStatus = dialogFormData.enableSubscribe
      ? "subscribed"
      : "unsubscribed";
    tableData.value[index].eventTypes = dialogFormData.eventTypes;
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
  // TODO: 重置数据
  ElMessage.info("已取消修改");
};

// 保存
const handleSave = () => {
  // TODO: 调用保存接口
  ElMessage.success("保存成功");
  emit("update");
};

onMounted(() => {
  // TODO: 从接口获取CDC订阅列表
});
</script>

<style scoped lang="scss">
.data-capture-card {
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

  .cdc-info {
    margin-bottom: 24px;
    padding: 20px;
    background: linear-gradient(135deg, #e6f7ff 0%, #f0f9ff 100%);
    border: 1px solid #91d5ff;
    border-radius: 6px;

    .info-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 15px;
      font-weight: 600;
      color: #1890ff;
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

    .object-name {
      display: flex;
      align-items: center;
      gap: 8px;

      .object-icon {
        font-size: 16px;
        color: #1890ff;
      }

      span {
        font-family: "Courier New", monospace;
        color: #1890ff;
        font-weight: 500;
      }
    }
  }

  :deep(.el-dialog__body) {
    padding: 20px;
  }

  :deep(.el-checkbox-group) {
    display: flex;
    flex-direction: column;
    gap: 8px;

    .el-checkbox {
      margin-right: 0;
    }
  }
}
</style>
