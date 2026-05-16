<template>
  <div class="order-sync">
    <h2 class="title">订单同步</h2>

    <!-- 同步设置 -->
    <div class="sync-config">
      <div class="config-section">
        <h3 class="section-title">同步设置</h3>

        <el-form label-position="left" label-width="100px">
          <el-form-item label="同步方式">
            <el-radio-group v-model="syncMode">
              <el-radio label="manual">手动同步</el-radio>
              <el-radio label="auto">自动同步</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item v-if="syncMode === 'auto'" label="同步间隔">
            <el-select v-model="syncInterval" style="width: 200px">
              <el-option label="每5分钟" value="5" />
              <el-option label="每15分钟" value="15" />
              <el-option label="每30分钟" value="30" />
              <el-option label="每小时" value="60" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>

      <!-- 同步状态 -->
      <div class="config-section">
        <h3 class="section-title">同步状态</h3>

        <div class="status-grid">
          <div class="status-item">
            <span class="label">上次同步时间：</span>
            <span class="value">{{ lastSyncTime }}</span>
          </div>
          <div class="status-item">
            <span class="label">订单总数：</span>
            <span class="value number">{{ totalOrders }}</span>
          </div>
          <div class="status-item">
            <span class="label">待处理订单：</span>
            <span class="value number pending">{{ pendingOrders }}</span>
          </div>
        </div>

        <div class="sync-actions">
          <el-button type="primary" :loading="syncing" @click="handleSyncNow">
            <el-icon><Refresh /></el-icon>
            立即同步
          </el-button>
        </div>
      </div>
    </div>

    <!-- 同步记录 -->
    <div class="sync-records">
      <div class="records-header">
        <h3 class="section-title">同步记录</h3>
        <el-button text type="primary" @click="handleExport">
          <el-icon><Download /></el-icon>
          导出记录
        </el-button>
      </div>

      <el-table :data="orderRecords" style="width: 100%">
        <el-table-column prop="orderId" label="订单编号" min-width="180">
          <template #default="{ row }">
            <span class="order-id">{{ row.orderId }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="buyerName" label="买家姓名" min-width="120" />

        <el-table-column prop="orderAmount" label="订单金额" min-width="120">
          <template #default="{ row }">
            <span class="amount">¥{{ row.orderAmount }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="orderStatus" label="订单状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getOrderStatusType(row.orderStatus)" size="small">
              {{ row.orderStatus }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="syncStatus" label="同步状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getSyncStatusType(row.syncStatus)" size="small">
              {{ row.syncStatus }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="syncTime" label="同步时间" min-width="180">
          <template #default="{ row }">
            <span class="sync-time">{{ row.syncTime }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleViewDetail(row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { ElMessage } from "element-plus";
import { Refresh, Download } from "@element-plus/icons-vue";

// 同步方式
const syncMode = ref('manual');
const syncInterval = ref('30');

// 同步状态
const lastSyncTime = ref('2024-01-20 15:30:00');
const totalOrders = ref(1234);
const pendingOrders = ref(56);
const syncing = ref(false);

// 订单同步记录
const orderRecords = ref([
  {
    id: '1',
    orderId: 'TKS202401200001',
    buyerName: '张三',
    orderAmount: '299.00',
    orderStatus: '待发货',
    syncStatus: '已同步',
    syncTime: '2024-01-20 15:30:00'
  },
  {
    id: '2',
    orderId: 'TKS202401200002',
    buyerName: '李四',
    orderAmount: '158.00',
    orderStatus: '已发货',
    syncStatus: '已同步',
    syncTime: '2024-01-20 15:30:00'
  },
  {
    id: '3',
    orderId: 'TKS202401200003',
    buyerName: '王五',
    orderAmount: '599.00',
    orderStatus: '待支付',
    syncStatus: '同步中',
    syncTime: '2024-01-20 15:30:00'
  },
  {
    id: '4',
    orderId: 'TKS202401200004',
    buyerName: '赵六',
    orderAmount: '88.00',
    orderStatus: '已完成',
    syncStatus: '已同步',
    syncTime: '2024-01-20 15:25:00'
  },
  {
    id: '5',
    orderId: 'TKS202401200005',
    buyerName: '孙七',
    orderAmount: '358.00',
    orderStatus: '已取消',
    syncStatus: '同步失败',
    syncTime: '2024-01-20 15:20:00'
  }
]);

// 获取订单状态类型
const getOrderStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    '待支付': 'warning',
    '待发货': 'primary',
    '已发货': 'success',
    '已完成': 'info',
    '已取消': 'danger'
  };
  return statusMap[status] || 'info';
};

// 获取同步状态类型
const getSyncStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    '已同步': 'success',
    '同步中': 'warning',
    '同步失败': 'danger',
    '待同步': 'info'
  };
  return statusMap[status] || 'info';
};

// 立即同步
const handleSyncNow = () => {
  syncing.value = true;

  // TODO: 调用同步接口
  setTimeout(() => {
    syncing.value = false;
    lastSyncTime.value = new Date().toLocaleString('zh-CN', { hour12: false });
    totalOrders.value += Math.floor(Math.random() * 10);
    pendingOrders.value = Math.floor(Math.random() * 100);
    ElMessage.success('订单同步成功');
  }, 2000);
};

// 导出记录
const handleExport = () => {
  // TODO: 调用导出接口
  ElMessage.info('正在导出同步记录...');
};

// 查看详情
const handleViewDetail = (row: any) => {
  // TODO: 打开订单详情对话框
  ElMessage.info(`查看订单详情：${row.orderId}`);
};
</script>

<style scoped lang="scss">
.order-sync {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

  .title {
    margin: 0 0 24px;
    font-size: 16px;
    font-weight: 600;
    color: #333;
  }

  .sync-config {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24px;
    margin-bottom: 32px;

    .config-section {
      border: 1px solid #e8e8e8;
      border-radius: 8px;
      padding: 20px;

      .section-title {
        margin: 0 0 16px;
        font-size: 14px;
        font-weight: 600;
        color: #333;
      }

      .status-grid {
        display: grid;
        grid-template-columns: 1fr;
        gap: 12px;
        margin-bottom: 16px;

        .status-item {
          display: flex;
          align-items: center;
          font-size: 13px;

          .label {
            color: #666;
            min-width: 100px;
          }

          .value {
            color: #333;
            font-weight: 500;

            &.number {
              color: #1890ff;
              font-weight: 600;
            }

            &.pending {
              color: #faad14;
            }
          }
        }
      }

      .sync-actions {
        display: flex;
        gap: 12px;
      }
    }
  }

  .sync-records {
    .records-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .section-title {
        margin: 0;
        font-size: 14px;
        font-weight: 600;
        color: #333;
      }
    }

    :deep(.el-table) {
      border: 1px solid #e8e8e8;
      border-radius: 8px;

      .el-table__header {
        th {
          background-color: #fafafa;
          font-weight: 600;
          color: #333;
        }
      }

      .el-table__body {
        tr {
          &:hover {
            background-color: #f0f5ff;
          }
        }
      }
    }

    .order-id {
      font-family: 'Courier New', monospace;
      color: #666;
      font-size: 13px;
    }

    .amount {
      color: #f5222d;
      font-weight: 600;
    }

    .sync-time {
      color: #999;
      font-size: 13px;
    }
  }
}
</style>
