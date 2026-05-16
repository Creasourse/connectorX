<template>
  <div class="object-sync-card">
    <div class="card-header">
      <h2 class="card-title">对象同步配置</h2>
      <div class="header-actions">
        <el-button class="action-btn" @click="handleCancel">取消</el-button>
        <el-button type="primary" class="action-btn" @click="handleSave"
          >保存</el-button
        >
      </div>
    </div>

    <div class="table-container">
      <el-table
        ref="tableRef"
        :data="tableData"
        style="width: 100%"
        row-key="apiName"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />

        <el-table-column prop="apiName" label="API名称" min-width="180">
          <template #default="{ row }">
            <span class="api-name">{{ row.apiName }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="displayName" label="显示名称" min-width="150" />

        <el-table-column
          prop="description"
          label="描述"
          min-width="200"
          show-overflow-tooltip
        />

        <el-table-column label="同步状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag
              :type="row.syncStatus === 'synced' ? 'success' : 'info'"
              size="small"
            >
              {{ row.syncStatus === "synced" ? "已同步" : "未同步" }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="100" align="center" fixed="right">
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
      :title="`配置 ${currentObject?.displayName}`"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="dialogFormRef"
        :model="dialogFormData"
        label-width="120px"
        label-position="left"
      >
        <el-form-item label="API名称">
          <el-input v-model="dialogFormData.apiName" disabled />
        </el-form-item>

        <el-form-item label="显示名称">
          <el-input v-model="dialogFormData.displayName" disabled />
        </el-form-item>

        <el-form-item label="同步频率">
          <el-select
            v-model="dialogFormData.syncFrequency"
            placeholder="请选择同步频率"
            style="width: 100%"
          >
            <el-option label="实时" value="realtime" />
            <el-option label="每小时" value="hourly" />
            <el-option label="每天" value="daily" />
            <el-option label="每周" value="weekly" />
          </el-select>
        </el-form-item>

        <el-form-item label="字段映射">
          <el-checkbox-group v-model="dialogFormData.selectedFields">
            <el-checkbox
              v-for="field in dialogFormData.availableFields"
              :key="field.name"
              :label="field.name"
            >
              {{ field.label }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="启用数据过滤">
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
            placeholder="请输入SOQL查询条件，如：CreatedDate > LAST_N_DAYS:30"
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
    apiName: "Account",
    displayName: "客户",
    description: "代表公司或个人客户信息",
    syncStatus: "synced",
    config: {
      syncFrequency: "daily",
      selectedFields: ["Name", "Industry", "AnnualRevenue"],
      enableFilter: false,
      filterCondition: ""
    }
  },
  {
    apiName: "Contact",
    displayName: "联系人",
    description: "代表与客户关联的个人联系人",
    syncStatus: "synced",
    config: {
      syncFrequency: "daily",
      selectedFields: ["FirstName", "LastName", "Email", "Phone"],
      enableFilter: false,
      filterCondition: ""
    }
  },
  {
    apiName: "Opportunity",
    displayName: "商机",
    description: "代表销售机会和潜在交易",
    syncStatus: "synced",
    config: {
      syncFrequency: "daily",
      selectedFields: ["Name", "Amount", "StageName", "CloseDate"],
      enableFilter: false,
      filterCondition: ""
    }
  },
  {
    apiName: "Lead",
    displayName: "潜在客户",
    description: "代表尚未资格的销售线索",
    syncStatus: "unsynced",
    config: {
      syncFrequency: "daily",
      selectedFields: ["FirstName", "LastName", "Company", "Status"],
      enableFilter: false,
      filterCondition: ""
    }
  },
  {
    apiName: "Campaign",
    displayName: "营销活动",
    description: "代表市场营销活动",
    syncStatus: "unsynced",
    config: {
      syncFrequency: "weekly",
      selectedFields: ["Name", "Type", "Status", "StartDate"],
      enableFilter: false,
      filterCondition: ""
    }
  },
  {
    apiName: "Case",
    displayName: "工单",
    description: "代表客户支持案例",
    syncStatus: "unsynced",
    config: {
      syncFrequency: "daily",
      selectedFields: ["Subject", "Status", "Priority", "Origin"],
      enableFilter: false,
      filterCondition: ""
    }
  }
]);

// 对话框表单数据
const dialogFormData = reactive({
  apiName: "",
  displayName: "",
  syncFrequency: "daily",
  selectedFields: [],
  availableFields: [],
  enableFilter: false,
  filterCondition: ""
});

// 选择变化
const handleSelectionChange = (selection: any[]) => {
  selectedRows.value = selection;
};

// 配置
const handleConfig = (row: any) => {
  currentObject.value = row;
  dialogFormData.apiName = row.apiName;
  dialogFormData.displayName = row.displayName;
  dialogFormData.syncFrequency = row.config?.syncFrequency || "daily";
  dialogFormData.selectedFields = row.config?.selectedFields || [];
  dialogFormData.enableFilter = row.config?.enableFilter || false;
  dialogFormData.filterCondition = row.config?.filterCondition || "";

  // 根据对象类型设置可用字段
  dialogFormData.availableFields = getObjectFields(row.apiName);

  dialogVisible.value = true;
};

// 获取对象字段
const getObjectFields = (apiName: string) => {
  const fieldMap: Record<string, any[]> = {
    Account: [
      { name: "Name", label: "客户名称" },
      { name: "Industry", label: "行业" },
      { name: "AnnualRevenue", label: "年收入" },
      { name: "BillingStreet", label: "账单街道" },
      { name: "BillingCity", label: "账单城市" },
      { name: "BillingState", label: "账单省/州" },
      { name: "BillingCountry", label: "账单国家" }
    ],
    Contact: [
      { name: "FirstName", label: "名字" },
      { name: "LastName", label: "姓氏" },
      { name: "Email", label: "邮箱" },
      { name: "Phone", label: "电话" },
      { name: "MobilePhone", label: "手机" },
      { name: "Title", label: "职位" }
    ],
    Opportunity: [
      { name: "Name", label: "商机名称" },
      { name: "Amount", label: "金额" },
      { name: "StageName", label: "阶段" },
      { name: "CloseDate", label: "关闭日期" },
      { name: "Probability", label: "概率" },
      { name: "Type", label: "类型" }
    ],
    Lead: [
      { name: "FirstName", label: "名字" },
      { name: "LastName", label: "姓氏" },
      { name: "Company", label: "公司" },
      { name: "Status", label: "状态" },
      { name: "Email", label: "邮箱" },
      { name: "Phone", label: "电话" }
    ],
    Campaign: [
      { name: "Name", label: "活动名称" },
      { name: "Type", label: "类型" },
      { name: "Status", label: "状态" },
      { name: "StartDate", label: "开始日期" },
      { name: "EndDate", label: "结束日期" },
      { name: "BudgetedCost", label: "预算成本" }
    ],
    Case: [
      { name: "Subject", label: "主题" },
      { name: "Status", label: "状态" },
      { name: "Priority", label: "优先级" },
      { name: "Origin", label: "来源" },
      { name: "Description", label: "描述" },
      { name: "ClosedDate", label: "关闭日期" }
    ]
  };

  return fieldMap[apiName] || [];
};

// 对话框保存
const handleDialogSave = () => {
  if (!currentObject.value) return;

  // 更新当前对象的配置
  const index = tableData.value.findIndex(
    item => item.apiName === currentObject.value.apiName
  );
  if (index !== -1) {
    tableData.value[index].config = {
      syncFrequency: dialogFormData.syncFrequency,
      selectedFields: dialogFormData.selectedFields,
      enableFilter: dialogFormData.enableFilter,
      filterCondition: dialogFormData.filterCondition
    };
    tableData.value[index].syncStatus = "synced";
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
  // TODO: 从接口获取对象列表
});
</script>

<style scoped lang="scss">
.object-sync-card {
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

  .table-container {
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

    .api-name {
      font-family: "Courier New", monospace;
      color: #1890ff;
      font-weight: 500;
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
