<template>
  <div class="field-mapping-card">
    <div class="card-header">
      <h2 class="card-title">字段映射配置</h2>
      <div class="header-actions">
        <el-button class="action-btn" @click="handleCancel">取消</el-button>
        <el-button type="primary" class="action-btn" @click="handleSave"
          >保存</el-button
        >
      </div>
    </div>

    <!-- 操作按钮区域 -->
    <div class="action-bar">
      <div class="action-bar-left">
        <el-button type="primary" @click="handleAddMapping">
          <el-icon><Plus /></el-icon>
          添加映射
        </el-button>
        <el-button @click="handleBatchImport">
          <el-icon><Upload /></el-icon>
          批量导入
        </el-button>
      </div>
      <div class="action-bar-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索字段映射"
          clearable
          style="width: 240px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 映射规则说明 -->
    <div class="mapping-info">
      <div class="info-item">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
        <span>字段映射用于将Salesforce对象字段映射到目标系统字段，支持数据转换和格式化</span>
      </div>
    </div>

    <!-- 字段映射表格 -->
    <div class="table-container">
      <el-table
        :data="filteredTableData"
        style="width: 100%"
        row-key="id"
        border
      >
        <el-table-column
          prop="sourceObject"
          label="源对象"
          width="150"
          align="center"
        >
          <template #default="{ row }">
            <el-tag size="small" type="primary">{{ row.sourceObject }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="sourceField" label="源字段" width="180">
          <template #default="{ row }">
            <div class="field-cell">
              <span class="field-name">{{ row.sourceField }}</span>
              <span class="field-type">{{ row.sourceType }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="映射关系" width="60" align="center">
          <template #default>
            <el-icon class="mapping-icon"><Right /></el-icon>
          </template>
        </el-table-column>

        <el-table-column
          prop="targetObject"
          label="目标对象"
          width="150"
          align="center"
        >
          <template #default="{ row }">
            <el-tag size="small" type="success">{{ row.targetObject }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="targetField" label="目标字段" width="180">
          <template #default="{ row }">
            <div class="field-cell">
              <span class="field-name">{{ row.targetField }}</span>
              <span class="field-type">{{ row.targetType }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="transformRule" label="转换规则" min-width="200">
          <template #default="{ row }">
            <el-tag
              v-if="row.transformRule"
              size="small"
              :type="getRuleTagType(row.transformRule)"
            >
              {{ getRuleText(row.transformRule) }}
            </el-tag>
            <span v-else class="no-rule">无</span>
          </template>
        </el-table-column>

        <el-table-column
          prop="defaultRule"
          label="默认值"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <span v-if="row.defaultValue" class="default-value">{{
              row.defaultValue
            }}</span>
            <span v-else class="no-rule">-</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              link
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty
        v-if="filteredTableData.length === 0"
        description="暂无字段映射配置"
        :image-size="100"
      />
    </div>

    <!-- 添加/编辑映射对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑字段映射' : '添加字段映射'"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="dialogFormRef"
        :model="dialogFormData"
        :rules="dialogFormRules"
        label-width="140px"
        label-position="left"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="源对象" prop="sourceObject">
              <el-select
                v-model="dialogFormData.sourceObject"
                placeholder="选择源对象"
                style="width: 100%"
                @change="handleSourceObjectChange"
              >
                <el-option
                  v-for="obj in objectList"
                  :key="obj.value"
                  :label="obj.label"
                  :value="obj.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="源字段" prop="sourceField">
              <el-select
                v-model="dialogFormData.sourceField"
                placeholder="选择源字段"
                style="width: 100%"
                @change="handleSourceFieldChange"
              >
                <el-option
                  v-for="field in sourceFields"
                  :key="field.name"
                  :label="`${field.name} (${field.type})`"
                  :value="field.name"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="目标对象" prop="targetObject">
              <el-select
                v-model="dialogFormData.targetObject"
                placeholder="选择目标对象"
                style="width: 100%"
                @change="handleTargetObjectChange"
              >
                <el-option
                  v-for="obj in targetObjectList"
                  :key="obj.value"
                  :label="obj.label"
                  :value="obj.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目标字段" prop="targetField">
              <el-select
                v-model="dialogFormData.targetField"
                placeholder="选择目标字段"
                style="width: 100%"
              >
                <el-option
                  v-for="field in targetFields"
                  :key="field.name"
                  :label="`${field.name} (${field.type})`"
                  :value="field.name"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="转换规则">
          <el-select
            v-model="dialogFormData.transformRule"
            placeholder="选择转换规则（可选）"
            style="width: 100%"
            clearable
          >
            <el-option label="直接映射" value="direct" />
            <el-option label="日期格式转换" value="date_format" />
            <el-option label="大小写转换" value="case_convert" />
            <el-option label="字符串拼接" value="string_concat" />
            <el-option label="数值计算" value="number_calc" />
            <el-option label="查找替换" value="find_replace" />
            <el-option label="自定义脚本" value="custom_script" />
          </el-select>
        </el-form-item>

        <el-form-item
          v-if="dialogFormData.transformRule === 'date_format'"
          label="日期格式"
        >
          <el-input
            v-model="dialogFormData.dateFormat"
            placeholder="如：YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>

        <el-form-item
          v-if="dialogFormData.transformRule === 'case_convert'"
          label="转换类型"
        >
          <el-radio-group v-model="dialogFormData.caseConvertType">
            <el-radio label="upper">转大写</el-radio>
            <el-radio label="lower">转小写</el-radio>
            <el-radio label="title">首字母大写</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item
          v-if="dialogFormData.transformRule === 'string_concat'"
          label="拼接前缀"
        >
          <el-input
            v-model="dialogFormData.concatPrefix"
            placeholder="输入拼接前缀"
          />
        </el-form-item>

        <el-form-item label="默认值">
          <el-input
            v-model="dialogFormData.defaultValue"
            placeholder="当源字段为空时使用的默认值"
          />
        </el-form-item>

        <el-form-item label="必填字段">
          <el-switch v-model="dialogFormData.required" />
        </el-form-item>

        <el-form-item label="描述">
          <el-input
            v-model="dialogFormData.description"
            type="textarea"
            :rows="2"
            placeholder="映射规则描述"
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
import { ref, reactive, computed, onMounted } from "vue";
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from "element-plus";
import {
  Plus,
  Upload,
  Search,
  Right,
  InfoFilled
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
const isEdit = ref(false);

// 表格数据
const tableData = ref([
  {
    id: 1,
    sourceObject: "Account",
    sourceField: "Name",
    sourceType: "string(255)",
    targetObject: "Customer",
    targetField: "customer_name",
    targetType: "varchar(255)",
    transformRule: "direct",
    defaultValue: "",
    required: true,
    description: "客户名称直接映射"
  },
  {
    id: 2,
    sourceObject: "Account",
    sourceField: "Industry",
    sourceType: "picklist",
    targetObject: "Customer",
    targetField: "industry_type",
    targetType: "varchar(100)",
    transformRule: "case_convert",
    defaultValue: "Unknown",
    required: false,
    description: "行业字段转大写"
  },
  {
    id: 3,
    sourceObject: "Contact",
    sourceField: "Email",
    sourceType: "email",
    targetObject: "User",
    targetField: "email_address",
    targetType: "varchar(255)",
    transformRule: "direct",
    defaultValue: "",
    required: true,
    description: "邮箱直接映射"
  },
  {
    id: 4,
    sourceObject: "Contact",
    sourceField: "Birthdate",
    sourceType: "date",
    targetObject: "User",
    targetField: "birthday",
    targetType: "date",
    transformRule: "date_format",
    defaultValue: "",
    required: false,
    description: "生日日期格式转换"
  },
  {
    id: 5,
    sourceObject: "Opportunity",
    sourceField: "Amount",
    sourceType: "currency",
    targetObject: "Order",
    targetField: "total_amount",
    targetType: "decimal(18,2)",
    transformRule: "number_calc",
    defaultValue: "0",
    required: true,
    description: "金额数值计算"
  }
]);

// 对象列表
const objectList = [
  { label: "客户 (Account)", value: "Account" },
  { label: "联系人 (Contact)", value: "Contact" },
  { label: "商机 (Opportunity)", value: "Opportunity" },
  { label: "潜在客户 (Lead)", value: "Lead" }
];

const targetObjectList = [
  { label: "客户 (Customer)", value: "Customer" },
  { label: "用户 (User)", value: "User" },
  { label: "订单 (Order)", value: "Order" },
  { label: "产品 (Product)", value: "Product" }
];

// 对象字段映射
const objectFields: Record<string, any[]> = {
  Account: [
    { name: "Id", type: "string(18)" },
    { name: "Name", type: "string(255)" },
    { name: "Industry", type: "picklist" },
    { name: "AnnualRevenue", type: "currency" },
    { name: "Type", type: "picklist" }
  ],
  Contact: [
    { name: "Id", type: "string(18)" },
    { name: "FirstName", type: "string(40)" },
    { name: "LastName", type: "string(80)" },
    { name: "Email", type: "email" },
    { name: "Phone", type: "phone" },
    { name: "Birthdate", type: "date" }
  ],
  Opportunity: [
    { name: "Id", type: "string(18)" },
    { name: "Name", type: "string(255)" },
    { name: "Amount", type: "currency" },
    { name: "StageName", type: "picklist" },
    { name: "CloseDate", type: "date" }
  ],
  Lead: [
    { name: "Id", type: "string(18)" },
    { name: "FirstName", type: "string(40)" },
    { name: "LastName", type: "string(80)" },
    { name: "Company", type: "string(255)" },
    { name: "Status", type: "picklist" }
  ],
  Customer: [
    { name: "id", type: "bigint" },
    { name: "customer_name", type: "varchar(255)" },
    { name: "industry_type", type: "varchar(100)" }
  ],
  User: [
    { name: "id", type: "bigint" },
    { name: "email_address", type: "varchar(255)" },
    { name: "birthday", type: "date" }
  ],
  Order: [
    { name: "id", type: "bigint" },
    { name: "total_amount", type: "decimal(18,2)" },
    { name: "order_date", type: "date" }
  ],
  Product: [
    { name: "id", type: "bigint" },
    { name: "product_name", type: "varchar(255)" },
    { name: "price", type: "decimal(18,2)" }
  ]
};

// 当前可用的源字段和目标字段
const sourceFields = ref<any[]>([]);
const targetFields = ref<any[]>([]);

// 对话框表单数据
const dialogFormData = reactive({
  id: 0,
  sourceObject: "",
  sourceField: "",
  targetObject: "",
  targetField: "",
  transformRule: "",
  dateFormat: "",
  caseConvertType: "upper",
  concatPrefix: "",
  defaultValue: "",
  required: false,
  description: ""
});

// 表单验证规则
const dialogFormRules: FormRules = {
  sourceObject: [
    { required: true, message: "请选择源对象", trigger: "change" }
  ],
  sourceField: [
    { required: true, message: "请选择源字段", trigger: "change" }
  ],
  targetObject: [
    { required: true, message: "请选择目标对象", trigger: "change" }
  ],
  targetField: [
    { required: true, message: "请选择目标字段", trigger: "change" }
  ]
};

// 过滤后的表格数据
const filteredTableData = computed(() => {
  if (!searchKeyword.value) {
    return tableData.value;
  }
  return tableData.value.filter(
    item =>
      item.sourceObject.includes(searchKeyword.value) ||
      item.sourceField.includes(searchKeyword.value) ||
      item.targetObject.includes(searchKeyword.value) ||
      item.targetField.includes(searchKeyword.value)
  );
});

// 获取规则标签类型
const getRuleTagType = (rule: string) => {
  const typeMap: Record<string, string> = {
    direct: "",
    date_format: "warning",
    case_convert: "success",
    string_concat: "info",
    number_calc: "danger",
    find_replace: "warning",
    custom_script: "primary"
  };
  return typeMap[rule] || "";
};

// 获取规则文本
const getRuleText = (rule: string) => {
  const textMap: Record<string, string> = {
    direct: "直接映射",
    date_format: "日期格式转换",
    case_convert: "大小写转换",
    string_concat: "字符串拼接",
    number_calc: "数值计算",
    find_replace: "查找替换",
    custom_script: "自定义脚本"
  };
  return textMap[rule] || rule;
};

// 源对象变化
const handleSourceObjectChange = (value: string) => {
  sourceFields.value = objectFields[value] || [];
  dialogFormData.sourceField = "";
};

// 源字段变化
const handleSourceFieldChange = (value: string) => {
  const field = sourceFields.value.find(f => f.name === value);
  if (field) {
    // 自动推荐目标字段类型
  }
};

// 目标对象变化
const handleTargetObjectChange = (value: string) => {
  targetFields.value = objectFields[value] || [];
  dialogFormData.targetField = "";
};

// 添加映射
const handleAddMapping = () => {
  isEdit.value = false;
  dialogFormData.id = 0;
  dialogFormData.sourceObject = "";
  dialogFormData.sourceField = "";
  dialogFormData.targetObject = "";
  dialogFormData.targetField = "";
  dialogFormData.transformRule = "";
  dialogFormData.dateFormat = "";
  dialogFormData.caseConvertType = "upper";
  dialogFormData.concatPrefix = "";
  dialogFormData.defaultValue = "";
  dialogFormData.required = false;
  dialogFormData.description = "";
  sourceFields.value = [];
  targetFields.value = [];
  dialogVisible.value = true;
};

// 批量导入
const handleBatchImport = () => {
  ElMessage.info("批量导入功能开发中...");
};

// 编辑
const handleEdit = (row: any) => {
  isEdit.value = true;
  dialogFormData.id = row.id;
  dialogFormData.sourceObject = row.sourceObject;
  dialogFormData.sourceField = row.sourceField;
  dialogFormData.targetObject = row.targetObject;
  dialogFormData.targetField = row.targetField;
  dialogFormData.transformRule = row.transformRule;
  dialogFormData.defaultValue = row.defaultValue;
  dialogFormData.required = row.required;
  dialogFormData.description = row.description;

  // 加载字段列表
  sourceFields.value = objectFields[row.sourceObject] || [];
  targetFields.value = objectFields[row.targetObject] || [];

  dialogVisible.value = true;
};

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm("确定要删除这条字段映射吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  })
    .then(() => {
      const index = tableData.value.findIndex(item => item.id === row.id);
      if (index !== -1) {
        tableData.value.splice(index, 1);
      }
      ElMessage.success("删除成功");
    })
    .catch(() => {});
};

// 对话框保存
const handleDialogSave = async () => {
  if (!dialogFormRef.value) return;

  try {
    await dialogFormRef.value.validate();

    if (isEdit.value) {
      // 编辑
      const index = tableData.value.findIndex(
        item => item.id === dialogFormData.id
      );
      if (index !== -1) {
        const sourceField = sourceFields.value.find(
          f => f.name === dialogFormData.sourceField
        );
        const targetField = targetFields.value.find(
          f => f.name === dialogFormData.targetField
        );

        tableData.value[index] = {
          ...tableData.value[index],
          sourceObject: dialogFormData.sourceObject,
          sourceField: dialogFormData.sourceField,
          sourceType: sourceField?.type || "",
          targetObject: dialogFormData.targetObject,
          targetField: dialogFormData.targetField,
          targetType: targetField?.type || "",
          transformRule: dialogFormData.transformRule,
          defaultValue: dialogFormData.defaultValue,
          required: dialogFormData.required,
          description: dialogFormData.description
        };
      }
      ElMessage.success("更新成功");
    } else {
      // 新增
      const sourceField = sourceFields.value.find(
        f => f.name === dialogFormData.sourceField
      );
      const targetField = targetFields.value.find(
        f => f.name === dialogFormData.targetField
      );

      tableData.value.push({
        id: Date.now(),
        sourceObject: dialogFormData.sourceObject,
        sourceField: dialogFormData.sourceField,
        sourceType: sourceField?.type || "",
        targetObject: dialogFormData.targetObject,
        targetField: dialogFormData.targetField,
        targetType: targetField?.type || "",
        transformRule: dialogFormData.transformRule,
        defaultValue: dialogFormData.defaultValue,
        required: dialogFormData.required,
        description: dialogFormData.description
      });
      ElMessage.success("添加成功");
    }

    dialogVisible.value = false;
  } catch (error) {
    ElMessage.error("请完善必填项");
  }
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

onMounted(() => {
  // TODO: 从接口获取字段映射列表
});
</script>

<style scoped lang="scss">
.field-mapping-card {
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

  .mapping-info {
    margin-bottom: 16px;
    padding: 12px 16px;
    background-color: #f6ffed;
    border: 1px solid #b7eb8f;
    border-radius: 4px;

    .info-item {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 13px;
      color: #52c41a;

      .info-icon {
        font-size: 16px;
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

    .field-cell {
      display: flex;
      flex-direction: column;
      gap: 4px;

      .field-name {
        font-weight: 500;
        color: #333;
      }

      .field-type {
        font-size: 12px;
        color: #999;
        font-family: "Courier New", monospace;
      }
    }

    .mapping-icon {
      font-size: 18px;
      color: #1890ff;
    }

    .no-rule {
      color: #999;
      font-size: 13px;
    }

    .default-value {
      color: #333;
      font-size: 13px;
    }
  }
}
</style>
