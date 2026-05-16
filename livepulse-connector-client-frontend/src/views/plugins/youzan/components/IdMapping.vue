<template>
  <div class="id-mapping-card">
    <div class="card-header">
      <h2 class="card-title">ID映射管理</h2>
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
        <el-button type="primary" @click="handleAddMapping">
          <el-icon><Plus /></el-icon>
          添加映射
        </el-button>
        <el-button @click="handleBatchImport">
          <el-icon><Upload /></el-icon>
          批量导入
        </el-button>
        <el-button @click="handleExport">
          <el-icon><Download /></el-icon>
          导出
        </el-button>
      </div>
      <div class="action-bar-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索映射"
          clearable
          style="width: 240px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 映射说明 -->
    <div class="mapping-info">
      <div class="info-title">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
        ID映射说明
      </div>
      <ul class="info-features">
        <li>ID映射用于将有赞平台的业务对象ID与本地系统ID进行关联</li>
        <li>支持订单、商品、客户等多种业务对象的ID映射</li>
        <li>提供批量导入导出功能，方便迁移和备份</li>
        <li>映射关系建立后，数据同步时会自动使用映射关系</li>
      </ul>
    </div>

    <!-- 映射类型统计 -->
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-label">映射总数：</span>
        <span class="stat-value">{{ tableData.length }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">订单映射：</span>
        <span class="stat-value">{{ typeCount.order }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">商品映射：</span>
        <span class="stat-value">{{ typeCount.item }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">客户映射：</span>
        <span class="stat-value">{{ typeCount.user }}</span>
      </div>
    </div>

    <!-- 映射列表表格 -->
    <div class="table-container">
      <el-table
        :data="filteredTableData"
        style="width: 100%"
        row-key="mappingId"
        border
      >
        <el-table-column
          prop="mappingName"
          label="映射名称"
          min-width="200"
        >
          <template #default="{ row }">
            <div class="mapping-name-cell">
              <el-icon class="mapping-icon" :color="row.iconColor">
                <component :is="row.icon" />
              </el-icon>
              <span class="mapping-name">{{ row.mappingName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="youzanId" label="有赞ID" min-width="180">
          <template #default="{ row }">
            <span class="id-text">{{ row.youzanId }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="systemId" label="系统ID" min-width="180">
          <template #default="{ row }">
            <span class="id-text">{{ row.systemId }}</span>
          </template>
        </el-table-column>

        <el-table-column label="映射类型" width="140" align="center">
          <template #default="{ row }">
            <el-tag
              :type="getTypeTagType(row.mappingType)"
              size="small"
            >
              {{ getTypeText(row.mappingType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="remark"
          label="备注"
          min-width="200"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <span class="remark-text">{{ row.remark || "-" }}</span>
          </template>
        </el-table-column>

        <el-table-column
          prop="createTime"
          label="创建时间"
          width="180"
          align="center"
        >
          <template #default="{ row }">
            <span class="time-text">{{ row.createTime }}</span>
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
        description="暂无ID映射配置"
        :image-size="100"
      />
    </div>

    <!-- 添加/编辑映射对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑ID映射' : '添加ID映射'"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="dialogFormRef"
        :model="dialogFormData"
        :rules="dialogFormRules"
        label-width="120px"
        label-position="left"
      >
        <el-form-item label="映射名称" prop="mappingName">
          <el-input
            v-model="dialogFormData.mappingName"
            placeholder="请输入映射名称"
          />
        </el-form-item>

        <el-form-item label="映射类型" prop="mappingType">
          <el-select
            v-model="dialogFormData.mappingType"
            placeholder="选择映射类型"
            style="width: 100%"
          >
            <el-option label="订单映射" value="order" />
            <el-option label="商品映射" value="item" />
            <el-option label="客户映射" value="user" />
            <el-option label="退款映射" value="refund" />
            <el-option label="优惠券映射" value="coupon" />
          </el-select>
        </el-form-item>

        <el-form-item label="有赞ID" prop="youzanId">
          <el-input
            v-model="dialogFormData.youzanId"
            placeholder="请输入有赞平台的业务对象ID"
          />
          <div class="form-tip">
            从有赞平台获取的业务对象唯一标识
          </div>
        </el-form-item>

        <el-form-item label="系统ID" prop="systemId">
          <el-input
            v-model="dialogFormData.systemId"
            placeholder="请输入本地系统的业务对象ID"
          />
          <div class="form-tip">
            本地系统中对应的业务对象唯一标识
          </div>
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="dialogFormData.remark"
            type="textarea"
            :rows="3"
            placeholder="输入备注信息（可选）"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleDialogSave">确定</el-button>
      </template>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="批量导入ID映射"
      width="600px"
    >
      <div class="import-content">
        <el-alert
          title="导入说明"
          type="info"
          :closable="false"
          style="margin-bottom: 16px"
        >
          <ul style="margin: 8px 0 0 0; padding-left: 20px">
            <li>支持导入Excel文件（.xlsx、.xls）</li>
            <li>文件格式：映射名称、映射类型、有赞ID、系统ID、备注</li>
            <li>单次最多导入1000条映射数据</li>
          </ul>
        </el-alert>

        <el-upload
          ref="uploadRef"
          class="upload-area"
          drag
          action="#"
          :auto-upload="false"
          :on-change="handleFileChange"
          accept=".xlsx,.xls"
        >
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              只支持 .xlsx、.xls 格式的Excel文件
            </div>
          </template>
        </el-upload>

        <div class="template-download">
          <el-link type="primary" @click="handleDownloadTemplate">
            <el-icon><Download /></el-icon>
            下载导入模板
          </el-link>
        </div>
      </div>

      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleImportConfirm">确定导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from "vue";
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadFile } from "element-plus";
import {
  Plus,
  Upload,
  Download,
  Search,
  InfoFilled,
  ShoppingCart,
  Box,
  User,
  UploadFilled
} from "@element-plus/icons-vue";
import dayjs from "dayjs";

const props = defineProps({
  config: {
    type: Object as PropType<unknown>
  }
});

const emit = defineEmits<{
  (e: "update"): void;
}>();

const dialogFormRef = ref<FormInstance>();
const uploadRef = ref();
const searchKeyword = ref("");
const dialogVisible = ref(false);
const importDialogVisible = ref(false);
const isEdit = ref(false);
const uploadedFile = ref<File | null>(null);

// 表格数据
const tableData = ref([
  {
    mappingId: 1,
    mappingName: "测试订单映射",
    youzanId: "E123456789",
    systemId: "ORD-2024-001",
    mappingType: "order",
    icon: ShoppingCart,
    iconColor: "#fa8c16",
    remark: "测试环境的订单映射关系",
    createTime: "2024-01-15 10:30:00"
  },
  {
    mappingId: 2,
    mappingName: "商品SKU映射001",
    youzanId: "ITEM-001-A",
    systemId: "SKU-10001",
    mappingType: "item",
    icon: Box,
    iconColor: "#52c41a",
    remark: "商品SKU编号映射",
    createTime: "2024-01-15 11:20:00"
  },
  {
    mappingId: 3,
    mappingName: "VIP客户张三",
    youzanId: "USER-88888",
    systemId: "CUST-20001",
    mappingType: "user",
    icon: User,
    iconColor: "#1890ff",
    remark: "VIP客户信息映射",
    createTime: "2024-01-15 14:15:00"
  },
  {
    mappingId: 4,
    mappingName: "批量订单映射001",
    youzanId: "E987654321",
    systemId: "ORD-2024-002",
    mappingType: "order",
    icon: ShoppingCart,
    iconColor: "#fa8c16",
    remark: "",
    createTime: "2024-01-15 15:45:00"
  },
  {
    mappingId: 5,
    mappingName: "限时活动商品",
    youzanId: "ITEM-PROMO-01",
    systemId: "SKU-10002",
    mappingType: "item",
    icon: Box,
    iconColor: "#52c41a",
    remark: "限时促销活动商品映射",
    createTime: "2024-01-15 16:30:00"
  }
]);

// 对话框表单数据
const dialogFormData = reactive({
  mappingId: 0,
  mappingName: "",
  mappingType: "",
  youzanId: "",
  systemId: "",
  remark: ""
});

// 表单验证规则
const dialogFormRules: FormRules = {
  mappingName: [
    { required: true, message: "请输入映射名称", trigger: "blur" }
  ],
  mappingType: [
    { required: true, message: "请选择映射类型", trigger: "change" }
  ],
  youzanId: [
    { required: true, message: "请输入有赞ID", trigger: "blur" }
  ],
  systemId: [{ required: true, message: "请输入系统ID", trigger: "blur" }]
};

// 过滤后的表格数据
const filteredTableData = computed(() => {
  if (!searchKeyword.value) {
    return tableData.value;
  }
  return tableData.value.filter(
    item =>
      item.mappingName.includes(searchKeyword.value) ||
      item.youzanId.includes(searchKeyword.value) ||
      item.systemId.includes(searchKeyword.value)
  );
});

// 类型统计
const typeCount = computed(() => {
  return {
    order: tableData.value.filter(item => item.mappingType === "order").length,
    item: tableData.value.filter(item => item.mappingType === "item").length,
    user: tableData.value.filter(item => item.mappingType === "user").length
  };
});

// 获取类型标签类型
const getTypeTagType = (type: string) => {
  const typeMap: Record<string, string> = {
    order: "warning",
    item: "success",
    user: "primary",
    refund: "danger",
    coupon: "info"
  };
  return typeMap[type] || "info";
};

// 获取类型文本
const getTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    order: "订单",
    item: "商品",
    user: "客户",
    refund: "退款",
    coupon: "优惠券"
  };
  return textMap[type] || type;
};

// 获取图标
const getIcon = (type: string) => {
  const iconMap: Record<string, any> = {
    order: ShoppingCart,
    item: Box,
    user: User
  };
  return iconMap[type] || ShoppingCart;
};

// 获取图标颜色
const getIconColor = (type: string) => {
  const colorMap: Record<string, string> = {
    order: "#fa8c16",
    item: "#52c41a",
    user: "#1890ff",
    refund: "#f5222d",
    coupon: "#eb2f96"
  };
  return colorMap[type] || "#666";
};

// 添加映射
const handleAddMapping = () => {
  isEdit.value = false;
  dialogFormData.mappingId = 0;
  dialogFormData.mappingName = "";
  dialogFormData.mappingType = "";
  dialogFormData.youzanId = "";
  dialogFormData.systemId = "";
  dialogFormData.remark = "";
  dialogVisible.value = true;
};

// 批量导入
const handleBatchImport = () => {
  uploadedFile.value = null;
  importDialogVisible.value = true;
};

// 导出
const handleExport = () => {
  ElMessage.success("正在导出ID映射数据...");
};

// 文件变化
const handleFileChange = (file: UploadFile) => {
  if (file.raw) {
    uploadedFile.value = file.raw;
  }
};

// 下载模板
const handleDownloadTemplate = () => {
  ElMessage.success("正在下载导入模板...");
};

// 导入确认
const handleImportConfirm = () => {
  if (!uploadedFile.value) {
    ElMessage.warning("请先选择要导入的文件");
    return;
  }
  ElMessage.success("导入成功");
  importDialogVisible.value = false;
};

// 编辑
const handleEdit = (row: any) => {
  isEdit.value = true;
  dialogFormData.mappingId = row.mappingId;
  dialogFormData.mappingName = row.mappingName;
  dialogFormData.mappingType = row.mappingType;
  dialogFormData.youzanId = row.youzanId;
  dialogFormData.systemId = row.systemId;
  dialogFormData.remark = row.remark;
  dialogVisible.value = true;
};

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    `确定要删除ID映射"${row.mappingName}"吗？`,
    "提示",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    }
  )
    .then(() => {
      const index = tableData.value.findIndex(
        item => item.mappingId === row.mappingId
      );
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

    const icon = getIcon(dialogFormData.mappingType);
    const iconColor = getIconColor(dialogFormData.mappingType);

    if (isEdit.value) {
      // 编辑
      const index = tableData.value.findIndex(
        item => item.mappingId === dialogFormData.mappingId
      );
      if (index !== -1) {
        tableData.value[index] = {
          ...tableData.value[index],
          mappingName: dialogFormData.mappingName,
          youzanId: dialogFormData.youzanId,
          systemId: dialogFormData.systemId,
          remark: dialogFormData.remark
        };
      }
      ElMessage.success("更新成功");
    } else {
      // 新增
      tableData.value.push({
        mappingId: Date.now(),
        mappingName: dialogFormData.mappingName,
        youzanId: dialogFormData.youzanId,
        systemId: dialogFormData.systemId,
        mappingType: dialogFormData.mappingType,
        icon,
        iconColor,
        remark: dialogFormData.remark,
        createTime: dayjs().format("YYYY-MM-DD HH:mm:ss")
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
</script>

<style scoped lang="scss">
.id-mapping-card {
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
    padding: 16px;
    background: linear-gradient(135deg, #fff7e6 0%, #ffe7ba 100%);
    border: 1px solid #ffd591;
    border-radius: 6px;

    .info-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      font-weight: 600;
      color: #fa8c16;
      margin-bottom: 12px;

      .info-icon {
        font-size: 16px;
      }
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

  .stats-bar {
    display: flex;
    gap: 32px;
    padding: 16px;
    background-color: #f6ffed;
    border: 1px solid #b7eb8f;
    border-radius: 4px;
    margin-bottom: 16px;

    .stat-item {
      display: flex;
      align-items: center;
      gap: 8px;

      .stat-label {
        font-size: 14px;
        color: #52c41a;
      }

      .stat-value {
        font-size: 18px;
        font-weight: 600;
        color: #1890ff;
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

    .mapping-name-cell {
      display: flex;
      align-items: center;
      gap: 8px;

      .mapping-icon {
        font-size: 18px;
      }

      .mapping-name {
        font-weight: 500;
        color: #333;
      }
    }

    .id-text {
      font-family: "Courier New", monospace;
      color: #fa8c16;
      font-size: 13px;
    }

    .remark-text {
      font-size: 13px;
      color: #666;
    }

    .time-text {
      font-size: 13px;
      color: #999;
    }
  }

  .form-tip {
    margin-top: 4px;
    font-size: 12px;
    color: #999;
  }

  .import-content {
    .upload-area {
      margin-bottom: 16px;

      :deep(.el-upload-dragger) {
        width: 100%;
      }
    }

    .template-download {
      text-align: center;
    }
  }
}
</style>
