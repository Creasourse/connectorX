<template>
  <div class="dimension-table-container">
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
      <el-table-column prop="description" label="说明" min-width="200" show-overflow-tooltip />
      <el-table-column prop="example" label="示例值" width="180">
        <template #default="{ row }">
          <span class="example-value">{{ row.example }}</span>
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
    ad: "info",
    traffic: "",
    tech: ""
  };
  return tagMap[category] || "";
};

const getCategoryName = (category: string) => {
  const nameMap: Record<string, string> = {
    user: "用户",
    session: "会话",
    event: "事件",
    ecommerce: "电商",
    ad: "广告",
    traffic: "流量",
    tech: "技术"
  };
  return nameMap[category] || category;
};

const handleCopy = (row: any) => {
  navigator.clipboard.writeText(row.apiName).then(() => {
    ElMessage.success(`已复制：${row.apiName}`);
  });
};
</script>

<style scoped lang="scss">
.dimension-table-container {
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
    font-style: italic;
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
