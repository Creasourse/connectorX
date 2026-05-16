<template>
  <div class="interface-sync-page">
    <div class="config-card">
      <h2 class="config-title">接口同步配置</h2>

      <!-- 定时同步规则 -->
      <!-- <div class="timing-rule">
        <span class="rule-label">定时同步规则：</span>
        <el-input-number
          v-model="syncDays"
          :min="1"
          :max="30"
          class="day-input"
        />
        <span class="rule-text">天</span>
        <el-time-picker
          v-model="syncTime"
          format="HH:mm"
          value-format="HH:mm"
          placeholder="选择时间"
          class="time-picker"
        />
        <el-button type="primary" class="save-rule-btn" @click="handleSaveRule">
          保存
        </el-button>
      </div> -->

      <!-- 接口列表表格 -->
      <el-table
        v-loading="loading"
        :data="interfaceList"
        class="interface-table"
        border
      >
        <el-table-column type="index" label="编号" width="60" align="center" />
        <el-table-column prop="tableName" label="表名称" min-width="150" />
        <el-table-column prop="tableAlias" label="表中文名" min-width="180" />
        <el-table-column prop="comment" label="表说明" min-width="200" />
        <el-table-column label="详情" width="100" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewTable(row)">
              查看表
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 底部分页 -->
      <div class="table-footer">
        <span class="total-text">共 {{ total }} 项数</span>
        <el-pagination
          v-model:current-page="form.currentPage"
          v-model:page-size="form.pageSize"
          :page-sizes="[10, 20, 50]"
          layout="sizes, prev, pager, next"
          :total="total"
          :pager-count="5"
          background
          @size-change="onSizeChange"
          @current-change="onCurrentChange"
        />
      </div>
    </div>

    <!-- 同步策略说明 -->
    <div class="sync-strategy">
      <div class="strategy-title">同步策略</div>
      <ul class="strategy-list">
        <li v-for="item in syncStrategies" :key="item.id">
          <el-link type="primary" :underline="false">{{ item.name }}</el-link>
          ：{{ item.description }}
        </li>
      </ul>
    </div>

    <!-- 底部操作按钮 -->
    <!-- <div class="action-buttons">
      <el-button class="sync-now-btn" @click="handleSyncNow">
        立即同步
      </el-button>
      <el-button
        type="primary"
        class="save-config-btn"
        @click="handleSaveConfig"
      >
        保存配置
      </el-button>
    </div> -->

    <!-- 查看表字段弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="`数据表字段 - ${currentTable?.tableName || ''}`"
      width="800px"
      class="table-dialog"
    >
      <el-table :data="currentFields" border max-height="400">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column
          prop="tableColumnName"
          label="英文名"
          min-width="180"
        />
        <el-table-column
          prop="tableColumnAlias"
          label="中文名"
          min-width="150"
        />
        <el-table-column prop="dataTypeName" label="数据类型" min-width="150" />
        <el-table-column prop="isPk" label="主键" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isPk === 1" type="success" size="small"
              >是</el-tag
            >
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, type PropType, watch } from "vue";
import {
  saveOrUpdateMetaTable,
  getMetaTableList,
  getColumnList
} from "@/api/wechat";
import { ElMessage } from "element-plus";

const props = defineProps({
  config: {
    type: Object as PropType<unknown>
  }
});

// 定时同步规则
const syncDays = ref(1);
const syncTime = ref("03:00");

const loading = ref(false);
const form = reactive({
  currentPage: 1,
  pageSize: 10,
  tableName: "",
  tableAlias: "",
  sortType: 1
});
const total = ref(0);

// 接口列表数据
const interfaceList = ref([]);

// 获取表数据
const getTableList = () => {
  loading.value = true;
  getMetaTableList(form)
    .then(res => {
      if (res.success) {
        interfaceList.value = res.data.list;
        total.value = res.data.total;
      }
    })
    .finally(() => {
      loading.value = false;
    });
};

const onSizeChange = (size: number) => {
  form.pageSize = size;
  form.currentPage = 1;
  getTableList();
};

const onCurrentChange = (page: number) => {
  form.currentPage = page;
  getTableList();
};

// 监听 props.config 变化
watch(
  () => props.config,
  (newVal: any) => {
    const wecomCorpId = newVal?.wecomCorpId;
    if (newVal && wecomCorpId) {
      getTableList();
    }
  },
  { immediate: true, deep: true }
);

// 同步策略说明
const syncStrategies = [
  {
    id: 1,
    name: "客户信息接口",
    description: "获取企业成员的客户列表及详细信息"
  },
  {
    id: 2,
    name: "组织架构接口",
    description: "获取企业组织架构信息"
  },
  {
    id: 3,
    name: "成员信息接口",
    description: "获取企业成员详细信息"
  },
  {
    id: 4,
    name: "外部联系人接口",
    description: "获取外部联系人详细信息"
  }
];

// 弹窗相关
const dialogVisible = ref(false);
const currentTable = ref<any>(null);
const currentFields = ref<any[]>([]);

// 保存定时规则
const handleSaveRule = () => {
  // TODO: 调用保存定时规则接口
  ElMessage.success("定时同步规则已保存");
};

// 查看表字段
const handleViewTable = (row: any) => {
  currentTable.value = row;
  dialogVisible.value = true;
  // 获取表字段数据
  getColumnList(row.tableId).then(res => {
    if (res.success) {
      currentFields.value = res.data || [];
    }
  });
};

// 立即同步
const handleSyncNow = () => {
  // TODO: 调用立即同步接口
  ElMessage.success("同步任务已启动");
};

// 保存配置
const handleSaveConfig = () => {
  // TODO: 调用保存配置接口
  ElMessage.success("配置已保存");
};
</script>

<style scoped lang="scss">
.interface-sync-page {
  .config-card {
    background-color: #fff;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
    margin-bottom: 16px;

    .config-title {
      margin: 0 0 24px;
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }

    .timing-rule {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 24px;
      padding: 16px;
      background-color: #f5f5f5;
      border-radius: 4px;

      .rule-label {
        font-size: 14px;
        color: #333;
        font-weight: 500;
      }

      .rule-text {
        font-size: 14px;
        color: #666;
      }

      .day-input {
        width: 120px;

        :deep(.el-input__wrapper) {
          background-color: #fff;
        }
      }

      .time-picker {
        width: 160px;

        :deep(.el-input__wrapper) {
          background-color: #fff;
        }
      }

      .save-rule-btn {
        width: 80px;
        background-color: #1890ff;

        &:hover {
          background-color: #40a9ff;
        }
      }
    }

    .interface-table {
      width: 100%;

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
  }

  .sync-strategy {
    background-color: #e6f7ff;
    border: 1px solid #91d5ff;
    border-radius: 8px;
    padding: 16px 24px;
    margin-bottom: 16px;

    .strategy-title {
      font-size: 14px;
      font-weight: 600;
      color: #333;
      margin-bottom: 12px;
    }

    .strategy-list {
      margin: 0;
      padding-left: 24px;

      li {
        font-size: 13px;
        color: #666;
        line-height: 1.8;
        margin-bottom: 8px;

        &:last-child {
          margin-bottom: 0;
        }
      }
    }
  }

  .action-buttons {
    display: flex;
    justify-content: flex-end;
    gap: 12px;

    .sync-now-btn {
      width: 100px;
      border: 1px solid #d9d9d9;
      color: #333;
      background-color: #fff;

      &:hover {
        border-color: #1890ff;
        color: #1890ff;
      }
    }

    .save-config-btn {
      width: 100px;
      background-color: #1d39c4;

      &:hover {
        background-color: #2b4ac8;
      }
    }
  }
}

.table-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 20px;

  .total-text {
    font-size: 13px;
    color: var(--el-text-color-secondary);
  }

  :deep(.el-pagination) {
    .el-pagination__sizes {
      margin-right: 8px;
    }

    .btn-prev,
    .btn-next,
    .el-pager li {
      border-radius: 6px;
    }
  }
}

.table-dialog {
  :deep(.el-dialog__header) {
    padding: 16px 24px;
    border-bottom: 1px solid #e8e8e8;

    .el-dialog__title {
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }
  }

  :deep(.el-dialog__body) {
    padding: 24px;
  }

  :deep(.el-dialog__footer) {
    padding: 16px 24px;
    border-top: 1px solid #e8e8e8;
  }
}
</style>
