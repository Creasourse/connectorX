<script setup lang="ts">
import { ref, onMounted, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useMetadata } from "./metadata";

defineOptions({ name: "MetadataManagement" });

const {
  loading,
  tableName,
  dataTypeFilter,
  currentPage,
  pageSize,
  total,
  tableData,
  columns,
  handleSearch,
  handleReset,
  handleAdd,
  handleViewDetail,
  handleEdit,
  handleDelete,
  onSizeChange,
  onCurrentChange,
  // 弹窗相关
  dialogVisible,
  dialogTitle,
  isEdit,
  formRef,
  formData,
  tableTypeOptions,
  formRules,
  handleDialogClose,
  handleSubmit
} = useMetadata();

const multipleSelection = ref<any[]>([]);

const handleSelectionChange = (val: any[]) => {
  multipleSelection.value = val;
};

onMounted(() => {
  handleSearch();
});
</script>

<template>
  <div class="metadata-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">元数据管理</h2>
        <p class="page-subtitle">
          管理系统的元数据配置，包括数据类型、标识和描述信息。
        </p>
      </div>
      <div class="header-btns">
        <!-- <el-button>删除</el-button> -->
        <el-button type="primary" @click="handleAdd">创建</el-button>
      </div>
    </div>

    <!-- 搜索表单 -->
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-select
        v-model="dataTypeFilter"
        placeholder="全部类型"
        clearable
        class="filter-select"
        @change="handleSearch"
      >
        <el-option label="主表" value="主表" />
        <el-option label="维度表" value="维度表" />
        <el-option label="属性表" value="属性表" />
        <el-option label="映射表" value="映射表" />
      </el-select>
      <el-input
        v-model="tableName"
        placeholder="请搜索元数据表名"
        clearable
        class="search-input"
        @input="handleSearch"
      />
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <pure-table
        row-key="tableId"
        style="width: 100%"
        :loading="loading"
        :data="tableData"
        :columns="columns"
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
        @selection-change="handleSelectionChange"
      >
        <template #tableName="{ row }">
          <el-button type="primary" link @click="handleViewDetail(row)">
            {{ row.tableName }}
          </el-button>
        </template>
        <template #operation="{ row }">
          <el-button type="primary" link size="small" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button type="danger" link size="small" @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </pure-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="onSizeChange"
          @current-change="onCurrentChange"
        />
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        label-position="right"
      >
        <el-form-item label="表英文名称" prop="tableName">
          <el-input
            v-model="formData.tableName"
            placeholder="请输入表英文名称"
            :disabled="isEdit"
            clearable
          />
        </el-form-item>

        <el-form-item label="表中文名称" prop="tableAlias">
          <el-input
            v-model="formData.tableAlias"
            placeholder="请输入表中文名称"
            :disabled="isEdit"
            clearable
          />
        </el-form-item>

        <el-form-item label="表类型" prop="type">
          <el-select
            v-model="formData.type"
            placeholder="请选择表类型"
            style="width: 100%"
            clearable
          >
            <el-option
              v-for="item in tableTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="描述" prop="comment">
          <el-input
            v-model="formData.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入描述"
            clearable
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.metadata-management {
  min-height: 100%;
}

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 24px;

  .header-left {
    display: flex;
    flex-direction: column;
    gap: 4px;

    .page-title {
      margin: 0;
      font-size: 22px;
      font-weight: 600;
      color: var(--el-text-color-primary);
      line-height: 1.3;
    }

    .page-subtitle {
      margin: 0;
      font-size: 13px;
      color: var(--el-text-color-secondary);
    }
  }
  .header-btns {
    display: flex;
  }
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: var(--el-bg-color);
  border-radius: 10px;
  margin-bottom: 16px;
  border: 1px solid var(--el-border-color-lighter);

  .search-input {
    flex: 1;

    :deep(.el-input__wrapper) {
      border-radius: 6px;
      box-shadow: none;
      border: 1px solid var(--el-border-color);
      background: var(--el-fill-color-blank);
    }
  }

  .filter-select,
  .sort-select {
    width: 140px;

    :deep(.el-select__wrapper) {
      border-radius: 6px;
      box-shadow: none;
      border: 1px solid var(--el-border-color);
    }
  }
  .sort-select {
    width: 200px;
  }

  .sort-btn {
    border-radius: 6px;
    border: 1px solid var(--el-border-color);
    background: var(--el-fill-color-blank);
    color: var(--el-text-color-regular);
    white-space: nowrap;
  }
}

.table-container {
  background: var(--el-bg-color);
  border-radius: 8px;
  padding: 20px;
  border: 1px solid var(--el-border-color-lighter);

  .pagination-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
}
</style>
