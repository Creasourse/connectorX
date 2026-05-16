<template>
  <div class="performance-monitor">
    <!-- 时间范围选择器 -->
    <div class="time-filter">
      <span class="filter-label">时间范围:</span>
      <el-select v-model="timeRange" placeholder="选择时间范围" @change="handleTimeRangeChange">
        <el-option label="最近1小时" value="1h" />
        <el-option label="最近24小时" value="24h" />
        <el-option label="最近7天" value="7d" />
        <el-option label="最近30天" value="30d" />
      </el-select>
    </div>

    <!-- 性能指标卡片 -->
    <div class="metrics-grid">
      <div
        v-for="metric in metrics"
        :key="metric.key"
        class="metric-card"
      >
        <div class="metric-header">
          <div class="metric-icon">
            <component :is="metric.icon" />
          </div>
          <div class="metric-title">{{ metric.title }}</div>
        </div>

        <div class="metric-value">{{ metric.value }}</div>

        <div class="metric-trend" :class="metric.trendClass">
          <el-icon>
            <component :is="metric.trendIcon" />
          </el-icon>
          <span>{{ metric.change }}</span>
        </div>
      </div>
    </div>

    <!-- 详细统计表格 -->
    <div class="stats-table">
      <h4 class="table-title">详细统计</h4>

      <el-table :data="statsData" style="width: 100%" border>
        <el-table-column prop="apiName" label="API名称" width="200" />
        <el-table-column prop="callCount" label="调用次数" width="120" align="right" />
        <el-table-column prop="successCount" label="成功次数" width="120" align="right" />
        <el-table-column prop="failCount" label="失败次数" width="120" align="right" />
        <el-table-column prop="successRate" label="成功率" width="100" align="right">
          <template #default="{ row }">
            <span :class="getSuccessRateClass(row.successRate)">
              {{ row.successRate }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="avgResponseTime" label="平均响应时间" width="120" align="right" />
        <el-table-column prop="maxResponseTime" label="最大响应时间" width="120" align="right" />
        <el-table-column prop="lastCallTime" label="最后调用时间" width="180" />
      </el-table>
    </div>

    <!-- 系统状态 -->
    <div class="system-status">
      <h4 class="status-title">系统状态</h4>

      <div class="status-grid">
        <div class="status-item">
          <div class="status-label">连接状态</div>
          <div class="status-value">
            <el-tag :type="connectionStatus === 'connected' ? 'success' : 'danger'" size="small">
              {{ connectionStatus === 'connected' ? '已连接' : '未连接' }}
            </el-tag>
          </div>
        </div>

        <div class="status-item">
          <div class="status-label">API版本</div>
          <div class="status-value">v2.0</div>
        </div>

        <div class="status-item">
          <div class="status-label">配额使用</div>
          <div class="status-value">
            <el-progress
              :percentage="quotaUsage"
              :color="getQuotaColor(quotaUsage)"
              :stroke-width="8"
            />
          </div>
        </div>

        <div class="status-item">
          <div class="status-label">最后同步时间</div>
          <div class="status-value">{{ lastSyncTime }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue";
import {
  TrendCharts,
  SuccessFilled,
  Timer,
  WarningFilled,
  ArrowUp,
  ArrowDown,
  Minus
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";

const timeRange = ref("7d");
const connectionStatus = ref<"connected" | "disconnected">("connected");
const quotaUsage = ref(68);
const lastSyncTime = ref(new Date().toLocaleString("zh-CN", { hour12: false }));

interface Metric {
  key: string;
  title: string;
  value: string;
  change: string;
  trendIcon: any;
  trendClass: string;
  icon: any;
}

const metrics = ref<Metric[]>([
  {
    key: "totalCalls",
    title: "总调用次数",
    value: "28,456",
    change: "+12.5%",
    trendIcon: ArrowUp,
    trendClass: "trend-up",
    icon: TrendCharts
  },
  {
    key: "successRate",
    title: "成功率",
    value: "98.6%",
    change: "+2.1%",
    trendIcon: ArrowUp,
    trendClass: "trend-up",
    icon: SuccessFilled
  },
  {
    key: "avgResponseTime",
    title: "平均响应时间",
    value: "125ms",
    change: "-8.3%",
    trendIcon: ArrowDown,
    trendClass: "trend-up",
    icon: Timer
  },
  {
    key: "errorRate",
    title: "错误率",
    value: "1.4%",
    change: "-0.5%",
    trendIcon: ArrowDown,
    trendClass: "trend-up",
    icon: WarningFilled
  }
]);

interface StatsData {
  apiName: string;
  callCount: number;
  successCount: number;
  failCount: number;
  successRate: string;
  avgResponseTime: string;
  maxResponseTime: string;
  lastCallTime: string;
}

const statsData = ref<StatsData[]>([
  {
    apiName: "人群包上传",
    callCount: 12580,
    successCount: 12345,
    failCount: 235,
    successRate: "98.1%",
    avgResponseTime: "135ms",
    maxResponseTime: "520ms",
    lastCallTime: "2026-05-09 18:25:30"
  },
  {
    apiName: "人群包查询",
    callCount: 9842,
    successCount: 9756,
    failCount: 86,
    successRate: "99.1%",
    avgResponseTime: "85ms",
    maxResponseTime: "320ms",
    lastCallTime: "2026-05-09 18:20:15"
  },
  {
    apiName: "ID映射",
    callCount: 6034,
    successCount: 5987,
    failCount: 47,
    successRate: "99.2%",
    avgResponseTime: "95ms",
    maxResponseTime: "280ms",
    lastCallTime: "2026-05-09 18:15:40"
  }
]);

const handleTimeRangeChange = (value: string) => {
  ElMessage.info(`切换时间范围: ${value}`);
  // TODO: 根据时间范围重新加载数据
};

const getSuccessRateClass = (rate: string) => {
  const numRate = parseFloat(rate);
  if (numRate >= 99) return "success-rate-excellent";
  if (numRate >= 95) return "success-rate-good";
  return "success-rate-normal";
};

const getQuotaColor = (percentage: number) => {
  if (percentage >= 90) return "#f56c6c";
  if (percentage >= 70) return "#e6a23c";
  return "#67c23a";
};
</script>

<style scoped lang="scss">
.performance-monitor {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.time-filter {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.filter-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

:deep(.el-select) {
  width: 160px;
}

:deep(.el-select__wrapper) {
  background-color: #f5f5f5;
  border: none;
  box-shadow: none;
  padding: 8px 12px;

  &.is-focus {
    background-color: #e6f7ff;
  }
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 32px;
}

.metric-card {
  padding: 20px;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-radius: 8px;
  transition: all 0.3s;

  &:hover {
    box-shadow: 0 4px 12px rgba(24, 144, 255, 0.2);
    transform: translateY(-2px);
  }
}

.metric-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.metric-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(24, 144, 255, 0.1);
  border-radius: 6px;
  color: #1890ff;
  font-size: 18px;
}

.metric-title {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.metric-value {
  font-size: 28px;
  font-weight: 600;
  color: #1890ff;
  margin-bottom: 8px;
}

.metric-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 500;

  &.trend-up {
    color: #52c41a;
  }

  &.trend-down {
    color: #f5222d;
  }

  &.trend-stable {
    color: #faad14;
  }
}

.stats-table,
.system-status {
  margin-top: 32px;
}

.table-title,
.status-title {
  margin: 0 0 16px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

:deep(.el-table) {
  font-size: 14px;

  th {
    background-color: #fafafa;
    color: #666;
    font-weight: 500;
  }

  td {
    color: #333;
  }
}

.success-rate-excellent {
  color: #52c41a;
  font-weight: 500;
}

.success-rate-good {
  color: #1890ff;
  font-weight: 500;
}

.success-rate-normal {
  color: #faad14;
  font-weight: 500;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.status-item {
  padding: 16px;
  background-color: #fafafa;
  border-radius: 8px;
}

.status-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.status-value {
  font-size: 14px;
  color: #333;
  font-weight: 500;

  :deep(.el-progress) {
    margin-top: 4px;
  }

  :deep(.el-progress__text) {
    font-size: 12px !important;
  }
}

@media (max-width: 1200px) {
  .metrics-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .status-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
