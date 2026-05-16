<template>
  <div class="metric-table-container">
    <el-table
      :data="data"
      border
      stripe
      :loading="loading"
      style="width: 100%"
      :default-sort="{ prop: 'apiName', order: 'ascending' }"
    >
      <el-table-column prop="apiName" label="API名称" min-width="180" sortable>
        <template #default="{ row }">
          <code class="api-code">{{ row.apiName }}</code>
        </template>
      </el-table-column>
      <el-table-column prop="uiName" label="显示名称" min-width="150" />
      <el-table-column prop="category" label="分类" width="120" align="center">
        <template #default="{ row }">
          <el-tag :type="getCategoryTag(row.category)" size="small">
            {{ getCategoryName(row.category) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="type" label="数据类型" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="getTypeTag(row.type)" size="small">
            {{ getTypeName(row.type) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="说明" min-width="200" show-overflow-tooltip />
      <el-table-column prop="example" label="示例值" width="150">
        <template #default="{ row }">
          <span class="example-value" :class="`type-${row.type}`">{{ row.example }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleCopy(row)">
            复制
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty
      v-if="!loading && data.length === 0"
      description="暂无数据"
      :image-size="120"
    />
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from "element-plus";

defineProps<{
  data: any[];
  loading: boolean;
}>();

const getCategoryTag = (category: string) => {
  const tagMap: Record<string, string> = {
    user: "success",
    session: "primary",
    event: "warning",
    ecommerce: "danger",
    ad: "info"
  };
  return tagMap[category] || "";
};

const getCategoryName = (category: string) => {
  const nameMap: Record<string, string> = {
    user: "用户",
    session: "会话",
    event: "事件",
    ecommerce: "电商",
    ad: "广告"
  };
  return nameMap[category] || category;
};

const getTypeTag = (type: string) => {
  const tagMap: Record<string, string> = {
    integer: "",
    float: "success",
    percent: "warning",
    currency: "danger"
  };
  return tagMap[type] || "";
};

const getTypeName = (type: string) => {
  const nameMap: Record<string, string> = {
    integer: "整数",
    float: "浮点数",
    percent: "百分比",
    currency: "货币"
  };
  return nameMap[type] || type;
};

const handleCopy = (row: any) => {
  navigator.clipboard.writeText(row.apiName).then(() => {
    ElMessage.success(`已复制：${row.apiName}`);
  });
};
</script>

<style scoped lang="scss">
.metric-table-container {
  .api-code {
    padding: 2px 8px;
    background-color: #f5f5f5;
    border: 1px solid #e0e0e0;
    border-radius: 3px;
    font-size: 13px;
    font-family: 'Courier New', monospace;
    color: #d93025;
  }

  .example-value {
    font-size: 13px;
    color: #666;
    font-family: 'Courier New', monospace;

    &.type-percent {
      color: #f59e0b;
      font-weight: 500;
    }

    &.type-currency {
      color: #10b981;
      font-weight: 500;
    }

    &.type-integer,
    &.type-float {
      color: #3b82f6;
    }
  }

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
</style>
