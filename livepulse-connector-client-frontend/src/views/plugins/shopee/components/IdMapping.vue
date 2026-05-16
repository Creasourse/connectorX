<template>
  <div class="id-mapping-page">
    <div class="config-card">
      <div class="page-header">
        <h2 class="config-title">ID映射管理</h2>
        <div class="header-actions">
          <el-button @click="handleBatchImport">
            批量导入
          </el-button>
          <el-button type="primary" @click="handleAddMapping">
            添加映射
          </el-button>
        </div>
      </div>

      <!-- 筛选条件 -->
      <div class="filter-section">
        <el-form :model="filterForm" inline class="filter-form">
          <el-form-item label="映射类型">
            <el-select
              v-model="filterForm.mappingType"
              placeholder="请选择映射类型"
              clearable
              style="width: 160px"
            >
              <el-option label="全部" value="" />
              <el-option label="商品ID" value="product" />
              <el-option label="订单ID" value="order" />
              <el-option label="客户ID" value="customer" />
              <el-option label="店铺ID" value="shop" />
            </el-select>
          </el-form-item>

          <el-form-item label="系统ID">
            <el-input
              v-model="filterForm.systemId"
              placeholder="请输入系统ID"
              clearable
              style="width: 180px"
            />
          </el-form-item>

          <el-form-item label="Shopee ID">
            <el-input
              v-model="filterForm.shopeeId"
              placeholder="请输入Shopee ID"
              clearable
              style="width: 180px"
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 映射列表表格 -->
      <el-table :data="mappingList" class="mapping-table" border>
        <el-table-column type="index" label="编号" width="60" align="center" />
        <el-table-column prop="mappingType" label="映射类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTagColor(row.mappingType)" size="small">
              {{ getTypeName(row.mappingType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="systemId" label="系统ID" min-width="150" show-overflow-tooltip />
        <el-table-column prop="systemName" label="系统名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="shopeeId" label="Shopee ID" min-width="150" show-overflow-tooltip />
        <el-table-column prop="shopeeName" label="Shopee名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="同步状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag
              v-if="row.syncStatus === 'synced'"
              type="success"
              size="small"
            >
              已同步
            </el-tag>
            <el-tag
              v-else-if="row.syncStatus === 'pending'"
              type="warning"
              size="small"
            >
              待同步
            </el-tag>
            <el-tag v-else type="danger" size="small">
              同步失败
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleSync(row)">
              同步
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
      <div class="mapping-summary">
        <div class="summary-item">
          <span class="label">总映射数：</span>
          <span class="value">{{ totalMappings }}</span>
        </div>
        <div class="summary-item">
          <span class="label">商品映射：</span>
          <span class="value product">{{ productCount }}</span>
        </div>
        <div class="summary-item">
          <span class="label">订单映射：</span>
          <span class="value order">{{ orderCount }}</span>
        </div>
        <div class="summary-item">
          <span class="label">客户映射：</span>
          <span class="value customer">{{ customerCount }}</span>
        </div>
        <div class="summary-item">
          <span class="label">已同步：</span>
          <span class="value success">{{ syncedCount }}</span>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 添加/编辑映射对话框 -->
    <el-dialog
      v-model="mappingDialogVisible"
      :title="isEdit ? '编辑映射' : '添加映射'"
      width="600px"
    >
      <el-form :model="mappingForm" label-width="120px">
        <el-form-item label="映射类型" required>
          <el-select
            v-model="mappingForm.mappingType"
            placeholder="请选择映射类型"
            style="width: 100%"
          >
            <el-option label="商品ID" value="product" />
            <el-option label="订单ID" value="order" />
            <el-option label="客户ID" value="customer" />
            <el-option label="店铺ID" value="shop" />
          </el-select>
        </el-form-item>
        <el-form-item label="系统ID" required>
          <el-input
            v-model="mappingForm.systemId"
            placeholder="请输入系统内部ID"
          />
        </el-form-item>
        <el-form-item label="系统名称">
          <el-input
            v-model="mappingForm.systemName"
            placeholder="请输入系统名称（可选）"
          />
        </el-form-item>
        <el-form-item label="Shopee ID" required>
          <el-input
            v-model="mappingForm.shopeeId"
            placeholder="请输入Shopee平台的ID"
          />
        </el-form-item>
        <el-form-item label="Shopee名称">
          <el-input
            v-model="mappingForm.shopeeName"
            placeholder="请输入Shopee名称（可选）"
          />
        </el-form-item>
        <el-form-item label="自动同步">
          <el-switch v-model="mappingForm.autoSync" />
          <span class="form-tip">开启后，当数据更新时自动同步到Shopee</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="mappingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmSave">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog v-model="importDialogVisible" title="批量导入映射" width="600px">
      <el-form :model="importForm" label-width="120px">
        <el-form-item label="映射类型" required>
          <el-select
            v-model="importForm.mappingType"
            placeholder="请选择映射类型"
            style="width: 100%"
          >
            <el-option label="商品ID" value="product" />
            <el-option label="订单ID" value="order" />
            <el-option label="客户ID" value="customer" />
            <el-option label="店铺ID" value="shop" />
          </el-select>
        </el-form-item>
        <el-form-item label="导入文件" required>
          <el-upload
            class="upload-demo"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            accept=".xlsx,.xls,.csv"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              拖拽文件到此处或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持.xlsx、.xls、.csv格式，文件大小不超过10MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="文件模板">
          <el-link type="primary" @click="handleDownloadTemplate">
            下载导入模板
          </el-link>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmImport">
          开始导入
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { UploadFilled } from "@element-plus/icons-vue";

// 筛选表单
const filterForm = ref({
  mappingType: "",
  systemId: "",
  shopeeId: ""
});

// 映射列表数据
const mappingList = ref([
  {
    id: 1,
    mappingType: "product",
    systemId: "SYS-PROD-001",
    systemName: "夏季T恤",
    shopeeId: "SP-ITEM-12345",
    shopeeName: "Summer T-Shirt",
    createTime: "2024-01-10 09:30:00",
    updateTime: "2024-01-15 14:20:00",
    syncStatus: "synced"
  },
  {
    id: 2,
    mappingType: "product",
    systemId: "SYS-PROD-002",
    systemName: "运动鞋",
    shopeeId: "SP-ITEM-67890",
    shopeeName: "Sports Shoes",
    createTime: "2024-01-11 10:15:00",
    updateTime: "2024-01-15 14:20:00",
    syncStatus: "synced"
  },
  {
    id: 3,
    mappingType: "order",
    systemId: "SYS-ORD-1001",
    systemName: "订单-1001",
    shopeeId: "SP-ORD-98765",
    shopeeName: "Order #98765",
    createTime: "2024-01-12 11:00:00",
    updateTime: "2024-01-12 11:00:00",
    syncStatus: "pending"
  },
  {
    id: 4,
    mappingType: "customer",
    systemId: "SYS-CUST-501",
    systemName: "张三",
    shopeeId: "SP-CUST-11111",
    shopeeName: "Zhang San",
    createTime: "2024-01-13 13:45:00",
    updateTime: "2024-01-13 13:45:00",
    syncStatus: "synced"
  }
]);

// 分页配置
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 4
});

// 统计数据
const totalMappings = computed(() => mappingList.value.length);
const productCount = computed(() => mappingList.value.filter(m => m.mappingType === "product").length);
const orderCount = computed(() => mappingList.value.filter(m => m.mappingType === "order").length);
const customerCount = computed(() => mappingList.value.filter(m => m.mappingType === "customer").length);
const syncedCount = computed(() => mappingList.value.filter(m => m.syncStatus === "synced").length);

// 映射对话框
const mappingDialogVisible = ref(false);
const isEdit = ref(false);
const mappingForm = ref({
  id: null,
  mappingType: "",
  systemId: "",
  systemName: "",
  shopeeId: "",
  shopeeName: "",
  autoSync: false
});

// 导入对话框
const importDialogVisible = ref(false);
const importForm = ref({
  mappingType: "",
  file: null
});

// 获取类型名称
const getTypeName = (type: string) => {
  const typeMap: any = {
    product: "商品ID",
    order: "订单ID",
    customer: "客户ID",
    shop: "店铺ID"
  };
  return typeMap[type] || type;
};

// 获取类型标签颜色
const getTypeTagColor = (type: string) => {
  const colorMap: any = {
    product: "success",
    order: "primary",
    customer: "warning",
    shop: "info"
  };
  return colorMap[type] || "";
};

// 查询
const handleSearch = () => {
  // TODO: 调用查询接口
  ElMessage.success("查询成功");
};

// 重置
const handleReset = () => {
  filterForm.value = {
    mappingType: "",
    systemId: "",
    shopeeId: ""
  };
  ElMessage.info("已重置筛选条件");
};

// 添加映射
const handleAddMapping = () => {
  isEdit.value = false;
  mappingForm.value = {
    id: null,
    mappingType: "",
    systemId: "",
    systemName: "",
    shopeeId: "",
    shopeeName: "",
    autoSync: false
  };
  mappingDialogVisible.value = true;
};

// 编辑映射
const handleEdit = (row: any) => {
  isEdit.value = true;
  mappingForm.value = {
    id: row.id,
    mappingType: row.mappingType,
    systemId: row.systemId,
    systemName: row.systemName,
    shopeeId: row.shopeeId,
    shopeeName: row.shopeeName,
    autoSync: row.syncStatus === "synced"
  };
  mappingDialogVisible.value = true;
};

// 确认保存
const handleConfirmSave = () => {
  if (!mappingForm.value.mappingType || !mappingForm.value.systemId || !mappingForm.value.shopeeId) {
    ElMessage.warning("请完整填写必填信息");
    return;
  }

  if (isEdit.value) {
    // 编辑模式
    const index = mappingList.value.findIndex(m => m.id === mappingForm.value.id);
    if (index > -1) {
      mappingList.value[index] = {
        ...mappingList.value[index],
        mappingType: mappingForm.value.mappingType,
        systemId: mappingForm.value.systemId,
        systemName: mappingForm.value.systemName,
        shopeeId: mappingForm.value.shopeeId,
        shopeeName: mappingForm.value.shopeeName,
        updateTime: new Date().toLocaleString("zh-CN", { hour12: false }).replace(/\//g, "-")
      };
    }
    ElMessage.success("映射更新成功");
  } else {
    // 添加模式
    const now = new Date().toLocaleString("zh-CN", { hour12: false }).replace(/\//g, "-");
    mappingList.value.push({
      id: Date.now(),
      mappingType: mappingForm.value.mappingType,
      systemId: mappingForm.value.systemId,
      systemName: mappingForm.value.systemName,
      shopeeId: mappingForm.value.shopeeId,
      shopeeName: mappingForm.value.shopeeName,
      createTime: now,
      updateTime: now,
      syncStatus: "pending"
    });
    ElMessage.success("映射添加成功");
  }

  mappingDialogVisible.value = false;
};

// 同步
const handleSync = (row: any) => {
  ElMessageBox.confirm(`确定要同步该映射数据到Shopee吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    // TODO: 调用同步接口
    row.syncStatus = "synced";
    ElMessage.success("同步成功");
  });
};

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除该映射吗？删除后无法恢复。`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    const index = mappingList.value.findIndex(m => m.id === row.id);
    if (index > -1) {
      mappingList.value.splice(index, 1);
    }
    ElMessage.success("删除成功");
  });
};

// 批量导入
const handleBatchImport = () => {
  importForm.value = {
    mappingType: "",
    file: null
  };
  importDialogVisible.value = true;
};

// 文件变化
const handleFileChange = (file: any) => {
  importForm.value.file = file.raw;
};

// 下载模板
const handleDownloadTemplate = () => {
  // TODO: 生成并下载Excel模板
  ElMessage.info("正在下载导入模板...");
};

// 确认导入
const handleConfirmImport = () => {
  if (!importForm.value.mappingType) {
    ElMessage.warning("请选择映射类型");
    return;
  }
  if (!importForm.value.file) {
    ElMessage.warning("请上传导入文件");
    return;
  }

  // TODO: 调用导入接口
  ElMessage.success("导入成功");
  importDialogVisible.value = false;
};

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.value.pageSize = size;
  // TODO: 重新加载数据
};

// 当前页变化
const handleCurrentChange = (page: number) => {
  pagination.value.currentPage = page;
  // TODO: 重新加载数据
};
</script>

<style scoped lang="scss">
.id-mapping-page {
  .config-card {
    background-color: #fff;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

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

      .header-actions {
        display: flex;
        gap: 12px;
      }
    }

    .filter-section {
      margin-bottom: 24px;
      padding: 16px;
      background-color: #f5f5f5;
      border-radius: 4px;

      .filter-form {
        :deep(.el-form-item__label) {
          font-size: 14px;
          color: #333;
          font-weight: 500;
        }

        :deep(.el-input__wrapper),
        :deep(.el-select .el-input__wrapper) {
          background-color: #fff;
          box-shadow: none;
          border: 1px solid #dcdfe6;

          &.is-focus {
            border-color: #ee4d2d;
          }
        }
      }
    }

    .mapping-table {
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

    .mapping-summary {
      display: flex;
      gap: 24px;
      margin-bottom: 16px;
      padding: 16px;
      background-color: #fff7e6;
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

          &.product {
            color: #52c41a;
          }

          &.order {
            color: #1890ff;
          }

          &.customer {
            color: #faad14;
          }

          &.success {
            color: #ee4d2d;
          }
        }
      }
    }

    .pagination-section {
      display: flex;
      justify-content: flex-end;
      padding-top: 16px;
      border-top: 1px solid #e8e8e8;
    }
  }

  .upload-demo {
    width: 100%;
  }

  .form-tip {
    margin-left: 12px;
    font-size: 12px;
    color: #999;
  }
}
</style>
