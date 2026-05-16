<script setup lang="ts">
import { ref, onMounted, computed, reactive } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { ArrowLeft } from "@element-plus/icons-vue";
import {
  getMetadataDetail,
  getMetadataColumns,
  deleteMetaTableColumn,
  saveOrUpdateMetaTableColumn,
  getMetaTableColumn
} from "@/api/metadata";
import dayjs from "dayjs";

defineOptions({ name: "MetadataDetail" });

const route = useRoute();
const router = useRouter();

const tableId = computed(() => Number(route.query?.tableId || undefined));

const loading = ref(false);
const tableDetail = ref<any>(null);
const columnList = ref<any[]>([]);
const activeTab = ref("structure");

// 字段弹窗相关
const columnDialogVisible = ref(false);
const columnDialogTitle = ref("新增字段");
const isColumnEdit = ref(false);
const columnFormRef = ref();
const columnFormData = reactive({
  tableColumnId: null as number | null,
  tableId: tableId.value,
  tableColumnName: "",
  tableColumnAlias: "",
  dataType: "",
  dataTypeName: "",
  shortDataType: "",
  isPk: 0,
  isNullable: 1,
  defaultValue: "",
  sourceField: "",
  comment: ""
});

const dataTypeOptions = [
  { label: "STRING", value: 1, shortDatatype: 1 },
  { label: "INT", value: 2, shortDatatype: 2 },
  { label: "BIGINT", value: 3, shortDatatype: 2 },
  { label: "DECIMAL", value: 4, shortDatatype: 3 },
  { label: "DATE", value: 5, shortDatatype: 4 },
  { label: "DATETIME", value: 6, shortDatatype: 5 },
  { label: "TIMESTAMP", value: 7, shortDatatype: 5 },
  { label: "MULTI_VALUE", value: 8, shortDatatype: 6 }
  // { label: "BOOLEAN", value: 9, shortDatatype: 7 },
  // { label: "JSON", value: 10, shortDatatype: 8 }
];

const columnFormRules = {
  tableColumnName: [
    { required: true, message: "请输入字段名", trigger: "blur" },
    {
      pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/,
      message: "字段名只能包含字母、数字和下划线，且必须以字母开头",
      trigger: "blur"
    }
  ],
  tableColumnAlias: [
    { required: true, message: "请输入中文名", trigger: "blur" }
  ],
  dataType: [{ required: true, message: "请选择数据类型", trigger: "change" }]
};

const statisticsCards = computed(() => {
  if (!tableDetail.value) return [];
  return [
    {
      title: "数据量",
      value: tableDetail.value.dataCount || "0",
      unit: "条记录",
      icon: "ri-file-list-3-line",
      color: "#409EFF"
    },
    {
      title: "存储大小",
      value: tableDetail.value.storageSize || "0",
      unit: "占用空间",
      icon: "ri-database-2-line",
      color: "#67C23A"
    },
    {
      title: "最后同步",
      value: tableDetail.value.updateTime
        ? dayjs(tableDetail.value.updateTime).format("YYYY-MM-DD HH:mm:ss")
        : "-",
      unit: "上次更新时间",
      icon: "ri-refresh-line",
      color: "#E6A23C"
    },
    {
      title: "更新频率",
      value: tableDetail.value.syncFrequency || "-",
      unit: "自动同步",
      icon: "ri-time-line",
      color: "#909399"
    }
  ];
});

const columnColumns = [
  { label: "序号", type: "index", width: 80, align: "center" },
  { label: "字段名", prop: "tableColumnName", minWidth: 150 },
  { label: "中文名", prop: "tableColumnAlias", minWidth: 120 },
  { label: "数据类型", prop: "dataTypeName", minWidth: 120 },
  {
    label: "主键",
    prop: "isPk",
    width: 80,
    align: "center",
    cellRenderer: ({ row }) => {
      return row.isPk === 1 ? "PK" : "-";
    }
  },
  {
    label: "可空",
    prop: "isNullable",
    width: 80,
    align: "center",
    cellRenderer: ({ row }) => {
      return row.isNullable === 1 ? "是" : "否";
    }
  },
  { label: "默认值", prop: "defaultValue", minWidth: 100 },
  { label: "来源字段", prop: "sourceField", minWidth: 120 },
  { label: "描述", prop: "comment", minWidth: 200 },
  {
    label: "操作",
    width: 140,
    align: "center",
    fixed: "right",
    slot: "operation"
  }
];

const getDetail = async () => {
  const tableId = route.query.tableId as string;
  if (!tableId) {
    ElMessage.error("缺少表ID参数");
    return;
  }

  loading.value = true;
  try {
    const res: any = await getMetadataDetail(Number(tableId));
    if (res.success) {
      tableDetail.value = res.data;
    } else {
      ElMessage.error(res.msg || "获取详情失败");
    }
  } catch (error) {
    console.error("获取元数据详情失败:", error);
    ElMessage.error("获取详情失败");
  } finally {
    loading.value = false;
  }
};

const getColumns = async () => {
  const tableId = route.query.tableId as string;
  if (!tableId) return;

  try {
    const res: any = await getMetadataColumns(Number(tableId));
    if (res.success) {
      columnList.value = res.data || [];
    }
  } catch (error) {
    console.error("获取字段列表失败:", error);
  }
};

const handleBack = () => {
  router.push({ name: "MetadataList" });
};

const handleExportDDL = () => {
  ElMessage.info("导出DDL功能待实现");
};

const resetColumnForm = () => {
  Object.assign(columnFormData, {
    tableColumnId: null,
    tableId: tableId.value,
    tableColumnName: "",
    tableColumnAlias: "",
    dataType: "",
    dataTypeName: "",
    shortDataType: "",
    isPk: 0,
    isNullable: 1,
    defaultValue: "",
    sourceField: "",
    comment: ""
  });
  columnFormRef.value?.clearValidate();
};

const changeDataType = (v: any) => {
  const obj = dataTypeOptions.find(e => e.value === v);
  if (obj) {
    const { label, value, shortDatatype } = obj;
    Object.assign(columnFormData, {
      dataType: value,
      dataTypeName: label,
      shortDataType: shortDatatype
    });
  } else {
    Object.assign(columnFormData, {
      dataType: "",
      dataTypeName: "",
      shortDataType: ""
    });
  }
};

const handleAddColumn = () => {
  isColumnEdit.value = false;
  columnDialogTitle.value = "新增字段";
  columnFormData.tableId = Number(route.query.tableId);
  resetColumnForm();
  columnDialogVisible.value = true;
};

const handleEditColumn = async (row: any) => {
  isColumnEdit.value = true;
  columnDialogTitle.value = "编辑字段";
  try {
    const res: any = await getMetaTableColumn(row.tableColumnId);
    if (res.success) {
      Object.assign(columnFormData, res.data);
      columnDialogVisible.value = true;
    } else {
      ElMessage.error(res.msg || "获取字段详情失败");
    }
  } catch (error) {
    console.error("获取字段详情失败:", error);
    ElMessage.error("获取字段详情失败");
  }
};

const handleColumnDialogClose = () => {
  columnDialogVisible.value = false;
  resetColumnForm();
};

const handleSubmitColumn = async () => {
  if (!columnFormRef.value) return;

  await columnFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        const params = {
          ...columnFormData
        };
        const res: any = await saveOrUpdateMetaTableColumn(params);
        if (res.success) {
          ElMessage.success(isColumnEdit.value ? "编辑成功" : "新增成功");
          columnDialogVisible.value = false;
          resetColumnForm();
          getColumns();
        } else {
          ElMessage.error(res.msg || "操作失败");
        }
      } catch (error) {
        console.error("保存字段失败:", error);
        ElMessage.error("操作失败");
      }
    }
  });
};

const handleDeleteColumn = (row: any) => {
  ElMessageBox.confirm(
    `确定要删除字段"${row.tableColumnAlias}"吗？删除后不可恢复。`,
    "提示",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    }
  )
    .then(async () => {
      try {
        const res: any = await deleteMetaTableColumn(row.tableColumnId);
        if (res.success) {
          ElMessage.success("删除成功");
          getColumns();
        } else {
          ElMessage.error(res.msg || "删除失败");
        }
      } catch (error) {
        console.error("删除字段失败:", error);
        ElMessage.error("删除失败");
      }
    })
    .catch(() => {
      ElMessage.info("已取消删除");
    });
};

onMounted(() => {
  getDetail();
  getColumns();
});
</script>

<template>
  <div v-loading="loading" class="metadata-detail">
    <!-- 面包屑导航 -->
    <div class="breadcrumb-nav">
      <el-icon class="back-icon" @click="handleBack">
        <ArrowLeft />
      </el-icon>
      <span class="breadcrumb-item" @click="handleBack">元数据管理</span>
      <span class="breadcrumb-separator">/</span>
      <span class="breadcrumb-current">{{
        tableDetail?.tableAlias || "详情"
      }}</span>
    </div>

    <!-- 表基本信息区 -->
    <div class="table-info-section">
      <div class="info-left">
        <h2 class="table-title">{{ tableDetail?.tableAlias }}</h2>
        <p class="table-subtitle">表名：{{ tableDetail?.tableName }}</p>
      </div>
      <div class="info-right">
        <el-button @click="handleExportDDL">导出DDL</el-button>
        <el-button type="primary" @click="handleAddColumn">添加字段</el-button>
      </div>
    </div>

    <!-- 数据统计卡片区 -->
    <div class="statistics-cards">
      <div v-for="card in statisticsCards" :key="card.title" class="stat-card">
        <!-- <div class="stat-icon" :style="{ backgroundColor: card.color + '20' }">
          <el-icon :style="{ color: card.color }">
            <component :is="card.icon" />
          </el-icon>
        </div> -->
        <div class="stat-content">
          <div class="stat-label">{{ card.title }}</div>
          <div class="stat-value">{{ card.value }}</div>
          <div class="stat-unit">{{ card.unit }}</div>
        </div>
      </div>
    </div>

    <!-- 标签页导航 -->
    <div class="tabs-section">
      <el-tabs v-model="activeTab" class="detail-tabs">
        <el-tab-pane label="表结构" name="structure">
          <!-- 字段列表表格 -->
          <div class="table-wrapper">
            <pure-table
              row-key="tableColumnId"
              style="width: 100%"
              :data="columnList"
              :columns="columnColumns"
              :header-cell-style="{
                color: 'var(--el-text-color-secondary)',
                fontWeight: '400',
                fontSize: '13px',
                borderBottom: '1px solid var(--el-border-color-lighter)'
              }"
              :row-style="{ height: '64px' }"
              :cell-style="{
                borderBottom: '1px solid var(--el-border-color-lighter)'
              }"
              table-layout="auto"
            >
              <template #operation="{ row }">
                <el-button
                  type="primary"
                  link
                  size="small"
                  @click="handleEditColumn(row)"
                >
                  编辑
                </el-button>
                <el-button
                  type="danger"
                  link
                  size="small"
                  @click="handleDeleteColumn(row)"
                >
                  删除
                </el-button>
              </template>
            </pure-table>
            <div class="table-footer">共 {{ columnList.length }} 个字段</div>
          </div>
        </el-tab-pane>

        <!-- <el-tab-pane label="索引" name="index">
          <div class="empty-state">
            <el-empty description="索引功能待实现" />
          </div>
        </el-tab-pane>

        <el-tab-pane label="关联关系" name="relation">
          <div class="empty-state">
            <el-empty description="关联关系功能待实现" />
          </div>
        </el-tab-pane> -->

        <el-tab-pane label="数据预览" name="preview">
          <div class="empty-state">
            <el-empty description="数据预览功能待实现" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 字段管理弹窗 -->
    <el-dialog
      v-model="columnDialogVisible"
      :title="columnDialogTitle"
      width="600px"
      :close-on-click-modal="false"
      @close="handleColumnDialogClose"
    >
      <el-form
        ref="columnFormRef"
        :model="columnFormData"
        :rules="columnFormRules"
        label-width="120px"
        label-position="right"
      >
        <el-form-item label="字段名" prop="tableColumnName">
          <el-input
            v-model="columnFormData.tableColumnName"
            placeholder="请输入字段名（英文）"
            :disabled="isColumnEdit"
            clearable
          />
        </el-form-item>

        <el-form-item label="中文名" prop="tableColumnAlias">
          <el-input
            v-model="columnFormData.tableColumnAlias"
            placeholder="请输入字段中文名"
            clearable
          />
        </el-form-item>

        <el-form-item label="数据类型" prop="dataType">
          <el-select
            v-model="columnFormData.dataType"
            placeholder="请选择数据类型"
            style="width: 100%"
            clearable
            @change="changeDataType"
          >
            <el-option
              v-for="item in dataTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="是否主键">
          <el-switch
            v-model="columnFormData.isPk"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>

        <el-form-item label="是否可空">
          <el-switch
            v-model="columnFormData.isNullable"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>

        <el-form-item label="默认值">
          <el-input
            v-model="columnFormData.defaultValue"
            placeholder="请输入默认值"
            clearable
          />
        </el-form-item>

        <el-form-item label="来源字段">
          <el-input
            v-model="columnFormData.sourceField"
            placeholder="请输入来源字段"
            clearable
          />
        </el-form-item>

        <el-form-item label="描述">
          <el-input
            v-model="columnFormData.comment"
            type="textarea"
            :rows="3"
            placeholder="请输入字段描述"
            clearable
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleColumnDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSubmitColumn">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.metadata-detail {
  min-height: 100%;
  padding: 20px;
}

.breadcrumb-nav {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  font-size: 14px;

  .back-icon {
    font-size: 18px;
    margin-right: 8px;
    cursor: pointer;
    color: var(--el-text-color-regular);
    transition: color 0.3s;

    &:hover {
      color: var(--el-color-primary);
    }
  }

  .breadcrumb-item {
    cursor: pointer;
    color: var(--el-text-color-regular);
    transition: color 0.3s;

    &:hover {
      color: var(--el-color-primary);
    }
  }

  .breadcrumb-separator {
    margin: 0 8px;
    color: var(--el-text-color-secondary);
  }

  .breadcrumb-current {
    color: var(--el-text-color-primary);
    font-weight: 500;
  }
}

.table-info-section {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--el-border-color-lighter);

  .info-left {
    display: flex;
    flex-direction: column;
    gap: 8px;

    .table-title {
      margin: 0;
      font-size: 24px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }

    .table-subtitle {
      margin: 0;
      font-size: 14px;
      color: var(--el-text-color-secondary);
    }
  }

  .info-right {
    display: flex;
    gap: 12px;
  }
}

.statistics-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;

  .stat-card {
    display: flex;
    gap: 16px;
    padding: 20px;
    background: var(--el-bg-color);
    border-radius: 8px;
    border: 1px solid var(--el-border-color-lighter);
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      transform: translateY(-2px);
    }

    .stat-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 56px;
      height: 56px;
      border-radius: 12px;

      .el-icon {
        font-size: 28px;
      }
    }

    .stat-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 4px;

      .stat-value {
        font-size: 20px;
        font-weight: 600;
        color: var(--el-text-color-primary);
      }

      .stat-label {
        font-size: 13px;
        color: var(--el-text-color-secondary);
      }

      .stat-unit {
        font-size: 12px;
        color: var(--el-text-color-placeholder);
      }
    }
  }
}

.tabs-section {
  background: var(--el-bg-color);
  border-radius: 8px;
  border: 1px solid var(--el-border-color-lighter);
  overflow: hidden;

  :deep(.el-tabs__header) {
    margin: 0;
    padding: 0 20px;
    background: var(--el-fill-color-light);
    border-bottom: 1px solid var(--el-border-color-lighter);
  }

  :deep(.el-tabs__nav-wrap::after) {
    display: none;
  }

  :deep(.el-tabs__content) {
    padding: 20px;
  }

  .table-wrapper {
    .table-footer {
      margin-top: 16px;
      padding: 12px 0;
      font-size: 13px;
      color: var(--el-text-color-secondary);
      text-align: right;
    }
  }

  .empty-state {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 400px;
  }
}
</style>
