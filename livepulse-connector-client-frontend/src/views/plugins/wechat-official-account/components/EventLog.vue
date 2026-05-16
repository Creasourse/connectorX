<template>
  <div class="config-card">
    <h2 class="config-title">事件日志</h2>

    <!-- 搜索筛选区域 -->
    <div class="search-area">
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="事件类型">
          <el-select
            v-model="searchForm.eventType"
            placeholder="请选择事件类型"
            clearable
          >
            <el-option label="全部" value="" />
            <el-option label="用户订阅" value="subscribe" />
            <el-option label="用户取消订阅" value="unsubscribe" />
            <el-option label="用户扫码" value="scan" />
            <el-option label="菜单点击" value="click" />
            <el-option label="模板消息发送结果" value="template" />
          </el-select>
        </el-form-item>

        <el-form-item label="处理状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
          >
            <el-option label="全部" value="" />
            <el-option label="成功" value="success" />
            <el-option label="失败" value="failed" />
            <el-option label="处理中" value="processing" />
          </el-select>
        </el-form-item>

        <el-form-item label="时间范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            clearable
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 事件日志表格 -->
    <div class="log-table">
      <el-table :data="logList" style="width: 100%">
        <el-table-column prop="eventId" label="事件ID" width="180" />
        <el-table-column prop="eventType" label="事件类型" width="150" />
        <el-table-column
          prop="eventContent"
          label="事件内容"
          show-overflow-tooltip
        />
        <el-table-column label="处理状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="receiveTime" label="接收时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
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

    <!-- 配置说明 -->
    <div class="permission-info">
      <div class="permission-title">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
        配置说明
      </div>
      <ul class="permission-list">
        <li>事件日志记录所有接收到的事件通知</li>
        <li>支持按事件类型、状态、时间范围筛选查询</li>
        <li>可查看事件详细内容用于问题排查</li>
        <li>日志保留时间为最近30天</li>
      </ul>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" class="save-btn" @click="handleSave"
        >保存</el-button
      >
    </div>
  </div>
</template>

<script setup lang="ts">
import { type PropType, ref, reactive, watch } from "vue";
import { ElMessage } from "element-plus";
import { InfoFilled } from "@element-plus/icons-vue";

interface LogItem {
  id: string;
  eventId: string;
  eventType: string;
  eventContent: string;
  status: 'success' | 'failed' | 'processing';
  receiveTime: string;
}

const props = defineProps({
  config: {
    type: Object as PropType<unknown>
  }
});

const emit = defineEmits<{
  (e: "update"): void;
}>();

// 搜索表单
const searchForm = reactive({
  eventType: '',
  status: '',
  dateRange: null as any
});

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

// 日志列表
const logList = ref<LogItem[]>([
  {
    id: '1',
    eventId: 'EVT20240115001',
    eventType: '用户订阅',
    eventContent: '用户关注公众号，OpenID: oK3_4567890abcdef',
    status: 'success',
    receiveTime: '2024-01-15 14:30:25'
  },
  {
    id: '2',
    eventId: 'EVT20240115002',
    eventType: '模板消息发送结果',
    eventContent: '模板消息发送成功，模板ID: abc123...',
    status: 'success',
    receiveTime: '2024-01-15 15:20:10'
  },
  {
    id: '3',
    eventId: 'EVT20240115003',
    eventType: '用户扫码',
    eventContent: '用户扫描带参数二维码，场景值: 1001',
    status: 'failed',
    receiveTime: '2024-01-15 16:00:00'
  },
  {
    id: '4',
    eventId: 'EVT20240115004',
    eventType: '菜单点击',
    eventContent: '用户点击菜单: 会员中心',
    status: 'success',
    receiveTime: '2024-01-14 09:15:30'
  },
  {
    id: '5',
    eventId: 'EVT20240115005',
    eventType: '用户取消订阅',
    eventContent: '用户取消关注公众号，OpenID: oK3_1234567890abc',
    status: 'success',
    receiveTime: '2024-01-14 10:45:00'
  }
]);

pagination.total = logList.value.length;

// 监听 props.config 变化
watch(
  () => props.config,
  (newVal: any) => {
    if (newVal && newVal.logList) {
      logList.value = newVal.logList;
    }
  },
  { immediate: true, deep: true }
);

// 获取状态类型
const getStatusType = (status: string) => {
  const statusMap: Record<string, any> = {
    success: 'success',
    failed: 'danger',
    processing: 'warning'
  };
  return statusMap[status] || 'info';
};

// 获取状态文本
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    success: '成功',
    failed: '失败',
    processing: '处理中'
  };
  return statusMap[status] || '未知';
};

// 查询
const handleSearch = () => {
  console.log('查询条件:', searchForm);
  // TODO: 调用查询接口
  ElMessage.success('查询成功');
};

// 重置
const handleReset = () => {
  searchForm.eventType = '';
  searchForm.status = '';
  searchForm.dateRange = null;
  pagination.currentPage = 1;
  ElMessage.info('已重置查询条件');
};

// 查看详情
const handleView = (row: LogItem) => {
  console.log('查看详情:', row);
  // TODO: 打开详情弹窗
};

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.pageSize = size;
  // TODO: 重新加载数据
};

// 当前页变化
const handleCurrentChange = (page: number) => {
  pagination.currentPage = page;
  // TODO: 重新加载数据
};

// 保存配置
const handleSave = async () => {
  try {
    const params = {
      searchForm,
      pagination
    };
    // TODO: 调用保存接口
    console.log('保存事件日志配置:', params);
    ElMessage.success('保存成功');
    emit('update');
  } catch (error) {
    ElMessage.error('保存失败，请稍后重试');
  }
};
</script>

<style scoped lang="scss">
.config-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

  .config-title {
    margin: 0 0 24px;
    font-size: 16px;
    font-weight: 600;
    color: #333;
  }

  .search-area {
    margin-bottom: 24px;
    padding: 16px;
    background-color: #f5f5f5;
    border-radius: 4px;

    .search-form {
      display: flex;
      flex-wrap: wrap;
      gap: 16px;

      :deep(.el-form-item) {
        margin-bottom: 0;
      }

      :deep(.el-select),
      :deep(.el-date-picker) {
        width: 200px;
      }
    }
  }

  .log-table {
    margin-bottom: 24px;

    :deep(.el-table) {
      font-size: 14px;

      th.el-table__cell {
        background-color: #fafafa;
        font-weight: 600;
        color: #333;
      }

      td.el-table__cell {
        color: #666;
      }
    }

    .pagination {
      margin-top: 16px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .permission-info {
    margin-top: 24px;
    padding: 16px;
    background-color: #f6ffed;
    border: 1px solid #b7eb8f;
    border-radius: 4px;

    .permission-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      font-weight: 600;
      color: #52c41a;
      margin-bottom: 12px;

      .info-icon {
        font-size: 16px;
      }
    }

    .permission-list {
      margin: 0;
      padding-left: 24px;

      li {
        font-size: 13px;
        color: #52c41a;
        line-height: 1.8;
        margin-bottom: 4px;

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
    margin-top: 32px;

    .save-btn {
      width: 80px;
      background-color: #1890ff;

      &:hover {
        background-color: #40a9ff;
      }
    }
  }
}
</style>
