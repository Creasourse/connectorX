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
      <h4 class="table-title">API调用统计</h4>

      <el-table :data="statsData" style="width: 100%" border>
        <el-table-column prop="apiName" label="API名称" width="220" />
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
        <el-table-column prop="avgResponseTime" label="平均响应时间" width="140" align="right" />
        <el-table-column prop="maxResponseTime" label="最大响应时间" width="140" align="right" />
        <el-table-column prop="lastCallTime" label="最后调用时间" width="180" />
      </el-table>
    </div>

    <!-- 系统状态 -->
    <div class="system-status">
      <h4 class="status-title">API配额状态</h4>

      <div class="status-grid">
        <div class="status-item">
          <div class="status-label">API状态</div>
          <div class="status-value">
            <el-tag :type="connectionStatus === 'connected' ? 'success' : 'danger'" size="small">
              {{ connectionStatus === 'connected' ? '正常' : '异常' }}
            </el-tag>
          </div>
        </div>

        <div class="status-item">
          <div class="status-label">API版本</div>
          <div class="status-value">v15.0</div>
        </div>

        <div class="status-item">
          <div class="status-label">今日配额使用</div>
          <div class="status-value">
            <el-progress
              :percentage="quotaUsage"
              :color="getQuotaColor(quotaUsage)"
              :stroke-width="8"
            />
          </div>
        </div>

        <div class="status-item">
          <div class="status-label">开发者Token状态</div>
          <div class="status-value">
            <el-tag type="success" size="small">已激活</el-tag>
          </div>
        </div>

        <div class="status-item">
          <div class="status-label">最后同步时间</div>
          <div class="status-value">{{ lastSyncTime }}</div>
        </div>

        <div class="status-item">
          <div class="status-label">客户数量</div>
          <div class="status-value">15</div>
        </div>
      </div>
    </div>

    <!-- Google Ads特定指标 -->
    <div class="ads-metrics">
      <h4 class="ads-title">广告效果指标</h4>

      <div class="ads-grid">
        <div class="ads-item">
          <div class="ads-label">活跃受众列表</div>
          <div class="ads-value">23</div>
        </div>

        <div class="ads-item">
          <div class="ads-label">总用户数</div>
          <div class="ads-value">1,245,890</div>
        </div>

        <div class="ads-item">
          <div class="ads-label">平均匹配率</div>
          <div class="ads-value">76.8%</div>
        </div>

        <div class="ads-item">
          <div class="ads-label">待上传列表</div>
          <div class="ads-value">5</div>
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
  ArrowDown
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";

const timeRange = ref("7d");
const connectionStatus = ref<"connected" | "disconnected">("connected");
const quotaUsage = ref(45);
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
    title: "总API调用",
    value: "152,346",
    change: "+18.2%",
    trendIcon: ArrowUp,
    trendClass: "trend-up",
    icon: TrendCharts
  },
  {
    key: "successRate",
    title: "成功率",
    value: "99.2%",
    change: "+1.5%",
    trendIcon: ArrowUp,
    trendClass: "trend-up",
    icon: SuccessFilled
  },
  {
    key: "avgResponseTime",
    title: "平均响应时间",
    value: "185ms",
    change: "-12.3%",
    trendIcon: ArrowDown,
    trendClass: "trend-up",
    icon: Timer
  },
  {
    key: "errorRate",
    title: "错误率",
    value: "0.8%",
    change: "-0.3%",
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
    apiName: "Customer Match Upload",
    callCount: 45620,
    successCount: 45123,
    failCount: 497,
    successRate: "98.9%",
    avgResponseTime: "220ms",
    maxResponseTime: "1850ms",
    lastCallTime: "2026-05-09 18:35:20"
  },
  {
    apiName: "Audience List Query",
    callCount: 52340,
    successCount: 52012,
    failCount: 328,
    successRate: "99.4%",
    avgResponseTime: "145ms",
    maxResponseTime: "680ms",
    lastCallTime: "2026-05-09 18:30:15"
  },
  {
    apiName: "Campaign Data Sync",
    callCount: 31280,
    successCount: 31156,
    failCount: 124,
    successRate: "99.6%",
    avgResponseTime: "165ms",
    maxResponseTime: "890ms",
    lastCallTime: "2026-05-09 18:25:40"
  },
  {
    apiName: "Conversion Tracking",
    callCount: 23106,
    successCount: 22987,
    failCount: 119,
    successRate: "99.5%",
    avgResponseTime: "195ms",
    maxResponseTime: "1050ms",
    lastCallTime: "2026-05-09 18:20:30"
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
.system-status,
.ads-metrics {
  margin-top: 32px;
}

.table-title,
.status-title,
.ads-title {
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
  grid-template-columns: repeat(3, 1fr);
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

.ads-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.ads-item {
  padding: 16px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border-radius: 8px;
  text-align: center;
}

.ads-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
}

.ads-value {
  font-size: 20px;
  font-weight: 600;
  color: #1890ff;
}

@media (max-width: 1200px) {
  .metrics-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .status-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .ads-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
