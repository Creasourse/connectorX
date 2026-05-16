<template>
  <div class="performance-optimize">
    <h2 class="title">性能优化</h2>

    <!-- 性能指标 -->
    <div class="performance-metrics">
      <div class="metric-card">
        <div class="metric-icon response">
          <el-icon><Timer /></el-icon>
        </div>
        <div class="metric-content">
          <div class="metric-value">{{ responseTime }}ms</div>
          <div class="metric-label">接口响应时间</div>
        </div>
        <div class="metric-trend success">
          <el-icon><TrendCharts /></el-icon>
          <span>-12%</span>
        </div>
      </div>

      <div class="metric-card">
        <div class="metric-icon sync">
          <el-icon><Refresh /></el-icon>
        </div>
        <div class="metric-content">
          <div class="metric-value">{{ syncSpeed }}条/分钟</div>
          <div class="metric-label">数据同步速度</div>
        </div>
        <div class="metric-trend success">
          <el-icon><TrendCharts /></el-icon>
          <span>+8%</span>
        </div>
      </div>

      <div class="metric-card">
        <div class="metric-icon cache">
          <el-icon><Coin /></el-icon>
        </div>
        <div class="metric-content">
          <div class="metric-value">{{ cacheRate }}%</div>
          <div class="metric-label">缓存命中率</div>
        </div>
        <div class="metric-trend success">
          <el-icon><TrendCharts /></el-icon>
          <span>+3.2%</span>
        </div>
      </div>
    </div>

    <!-- 优化设置 -->
    <div class="optimize-settings">
      <h3 class="section-title">优化设置</h3>

      <el-form label-position="left" label-width="120px">
        <el-form-item label="缓存优化">
          <el-switch
            v-model="optimizeConfig.cacheEnabled"
            active-text="启用"
            inactive-text="禁用"
          />
          <div class="form-tip">启用缓存可以减少接口调用，提升响应速度</div>
        </el-form-item>

        <el-form-item label="同步间隔">
          <el-select v-model="optimizeConfig.syncInterval" style="width: 200px">
            <el-option label="实时同步" value="realtime" />
            <el-option label="每5分钟" value="5" />
            <el-option label="每15分钟" value="15" />
            <el-option label="每30分钟" value="30" />
            <el-option label="每小时" value="60" />
          </el-select>
          <div class="form-tip">数据同步的时间间隔，根据业务需求调整</div>
        </el-form-item>

        <el-form-item label="并发请求数">
          <el-slider
            v-model="optimizeConfig.concurrentRequests"
            :min="1"
            :max="10"
            :step="1"
            show-stops
            style="width: 300px"
          />
          <div class="form-tip">同时处理的请求数量，建议根据服务器性能调整</div>
        </el-form-item>

        <el-form-item label="批量大小">
          <el-select v-model="optimizeConfig.batchSize" style="width: 200px">
            <el-option label="50条/批次" :value="50" />
            <el-option label="100条/批次" :value="100" />
            <el-option label="200条/批次" :value="200" />
            <el-option label="500条/批次" :value="500" />
          </el-select>
          <div class="form-tip">批量处理数据时的批次大小</div>
        </el-form-item>

        <el-form-item label="超时时间">
          <el-input-number
            v-model="optimizeConfig.timeout"
            :min="5"
            :max="120"
            :step="5"
            style="width: 200px"
          />
          <span style="margin-left: 12px; color: #666">秒</span>
          <div class="form-tip">接口请求的超时时间</div>
        </el-form-item>

        <el-form-item label="重试次数">
          <el-input-number
            v-model="optimizeConfig.retryCount"
            :min="0"
            :max="5"
            :step="1"
            style="width: 200px"
          />
          <div class="form-tip">请求失败时的重试次数</div>
        </el-form-item>
      </el-form>

      <div class="action-buttons">
        <el-button @click="handleReset">恢复默认</el-button>
        <el-button type="primary" @click="handleSave">保存设置</el-button>
      </div>
    </div>

    <!-- 性能日志 -->
    <div class="performance-logs">
      <div class="logs-header">
        <h3 class="section-title">性能日志</h3>
        <el-button text type="primary" @click="handleClearLogs">
          <el-icon><Delete /></el-icon>
          清空日志
        </el-button>
      </div>

      <el-table :data="performanceLogs" style="width: 100%" max-height="400">
        <el-table-column prop="timestamp" label="时间" width="180">
          <template #default="{ row }">
            <span class="timestamp">{{ row.timestamp }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="operation" label="操作类型" width="150">
          <template #default="{ row }">
            <el-tag :type="getOperationType(row.operation)" size="small">
              {{ row.operation }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="details" label="详细信息" min-width="200" />

        <el-table-column prop="duration" label="耗时" width="120">
          <template #default="{ row }">
            <span class="duration">{{ row.duration }}ms</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '成功' ? 'success' : 'danger'" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Timer,
  Refresh,
  Coin,
  TrendCharts,
  Delete
} from "@element-plus/icons-vue";

// 性能指标
const responseTime = ref(125);
const syncSpeed = ref(1234);
const cacheRate = ref(95.8);

// 优化配置
const optimizeConfig = ref({
  cacheEnabled: true,
  syncInterval: '30',
  concurrentRequests: 5,
  batchSize: 100,
  timeout: 30,
  retryCount: 3
});

// 性能日志
const performanceLogs = ref([
  {
    id: '1',
    timestamp: '2024-01-20 15:30:25',
    operation: '订单同步',
    details: '同步了 150 条订单数据',
    duration: 1250,
    status: '成功'
  },
  {
    id: '2',
    timestamp: '2024-01-20 15:25:18',
    operation: '商品更新',
    details: '更新了 25 个商品信息',
    duration: 856,
    status: '成功'
  },
  {
    id: '3',
    timestamp: '2024-01-20 15:20:10',
    operation: '库存查询',
    details: '查询了 500 个SKU库存',
    duration: 2340,
    status: '成功'
  },
  {
    id: '4',
    timestamp: '2024-01-20 15:15:05',
    operation: '数据缓存',
    details: '缓存了商品分类数据',
    duration: 125,
    status: '成功'
  },
  {
    id: '5',
    timestamp: '2024-01-20 15:10:02',
    operation: '接口调用',
    details: '获取店铺信息失败',
    duration: 5000,
    status: '失败'
  }
]);

// 获取操作类型颜色
const getOperationType = (operation: string) => {
  const typeMap: Record<string, string> = {
    '订单同步': 'primary',
    '商品更新': 'success',
    '库存查询': 'warning',
    '数据缓存': 'info',
    '接口调用': 'default'
  };
  return typeMap[operation] || 'default';
};

// 恢复默认设置
const handleReset = () => {
  ElMessageBox.confirm('确定要恢复默认设置吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    optimizeConfig.value = {
      cacheEnabled: true,
      syncInterval: '30',
      concurrentRequests: 5,
      batchSize: 100,
      timeout: 30,
      retryCount: 3
    };
    ElMessage.success('已恢复默认设置');
  });
};

// 保存设置
const handleSave = () => {
  // TODO: 调用保存接口
  ElMessage.success('设置保存成功');
};

// 清空日志
const handleClearLogs = () => {
  ElMessageBox.confirm('确定要清空所有性能日志吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    performanceLogs.value = [];
    ElMessage.success('日志已清空');
  });
};
</script>

<style scoped lang="scss">
.performance-optimize {
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

  .performance-metrics {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;
    margin-bottom: 32px;

    .metric-card {
      display: flex;
      align-items: center;
      padding: 20px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 8px;
      color: #fff;

      &:nth-child(2) {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      }

      &:nth-child(3) {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      }

      .metric-icon {
        width: 48px;
        height: 48px;
        border-radius: 8px;
        background-color: rgba(255, 255, 255, 0.2);
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;
        font-size: 24px;
      }

      .metric-content {
        flex: 1;

        .metric-value {
          font-size: 24px;
          font-weight: 600;
          line-height: 1.2;
          margin-bottom: 4px;
        }

        .metric-label {
          font-size: 13px;
          opacity: 0.9;
        }
      }

      .metric-trend {
        display: flex;
        align-items: center;
        gap: 4px;
        padding: 4px 8px;
        background-color: rgba(255, 255, 255, 0.2);
        border-radius: 4px;
        font-size: 12px;
        font-weight: 600;

        &.success {
          background-color: rgba(82, 196, 26, 0.3);
        }
      }
    }
  }

  .optimize-settings {
    border: 1px solid #e8e8e8;
    border-radius: 8px;
    padding: 24px;
    margin-bottom: 32px;

    .section-title {
      margin: 0 0 20px;
      font-size: 14px;
      font-weight: 600;
      color: #333;
    }

    .form-tip {
      margin-top: 4px;
      font-size: 12px;
      color: #999;
      line-height: 1.5;
    }

    .action-buttons {
      display: flex;
      justify-content: flex-end;
      gap: 12px;
      margin-top: 24px;
      padding-top: 24px;
      border-top: 1px solid #e8e8e8;
    }
  }

  .performance-logs {
    .logs-header {
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

    .timestamp {
      color: #999;
      font-size: 13px;
      font-family: 'Courier New', monospace;
    }

    .duration {
      color: #666;
      font-weight: 500;
    }
  }
}
</style>
