<template>
  <div class="api-sync-page">
    <div class="config-card">
      <h2 class="config-title">GA4 数据同步配置</h2>

      <!-- 同步策略配置 -->
      <div class="sync-strategy">
        <div class="section-title">
          <el-icon><Setting /></el-icon>
          同步策略配置
        </div>
        <el-form :model="strategyForm" label-width="140px" class="strategy-form">
          <el-form-item label="同步方式">
            <el-radio-group v-model="strategyForm.syncMode">
              <el-radio label="manual">手动同步</el-radio>
              <el-radio label="auto">自动同步</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="同步频率" v-if="strategyForm.syncMode === 'auto'">
            <el-select
              v-model="strategyForm.frequency"
              placeholder="请选择同步频率"
              style="width: 200px"
            >
              <el-option label="每小时" value="hourly" />
              <el-option label="每6小时" value="6hours" />
              <el-option label="每12小时" value="12hours" />
              <el-option label="每天" value="daily" />
              <el-option label="每周" value="weekly" />
            </el-select>
            <div class="form-tip">系统将按照设定的频率自动拉取GA4数据</div>
          </el-form-item>

          <el-form-item label="同步时间" v-if="strategyForm.syncMode === 'auto' && strategyForm.frequency !== 'hourly'">
            <el-time-picker
              v-model="strategyForm.syncTime"
              format="HH:mm"
              value-format="HH:mm"
              placeholder="选择同步时间"
            />
            <div class="form-tip">建议选择业务低峰期进行同步</div>
          </el-form-item>

          <el-form-item label="数据日期范围">
            <el-date-picker
              v-model="strategyForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              style="width: 300px"
            />
            <div class="form-tip">GA4数据最长保留14个月</div>
          </el-form-item>
        </el-form>
      </div>

      <!-- API报告列表 -->
      <div class="api-reports">
        <div class="report-header">
          <div class="section-title">
            <el-icon><Document /></el-icon>
            GA4 报告配置列表
          </div>
          <el-button type="primary" @click="handleAddReport">
            <el-icon><Plus /></el-icon>
            添加报告
          </el-button>
        </div>

        <el-table :data="reportList" class="report-table" border>
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="reportName" label="报告名称" min-width="180">
            <template #default="{ row }">
              <div class="report-name-cell">
                <el-icon class="report-icon"><DataAnalysis /></el-icon>
                <span>{{ row.reportName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="metrics" label="指标" min-width="220">
            <template #default="{ row }">
              <div class="tags-container">
                <el-tag
                  v-for="(metric, index) in row.metrics"
                  :key="index"
                  size="small"
                  type="primary"
                  style="margin-right: 4px; margin-bottom: 4px"
                >
                  {{ metric }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="dimensions" label="维度" min-width="200">
            <template #default="{ row }">
              <div class="tags-container">
                <el-tag
                  v-for="(dimension, index) in row.dimensions"
                  :key="index"
                  size="small"
                  type="success"
                  style="margin-right: 4px; margin-bottom: 4px"
                >
                  {{ dimension }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="lastSyncTime" label="最后同步时间" width="180" />
          <el-table-column label="同步状态" width="100" align="center">
            <template #default="{ row }">
              <el-switch
                v-model="row.enabled"
                active-text="启用"
                inactive-text="禁用"
                @change="handleStatusChange(row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220" align="center" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleSync(row)">
                <el-icon><Refresh /></el-icon>
                立即同步
              </el-button>
              <el-button type="primary" link @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="danger" link @click="handleDelete(row)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 统计信息 -->
        <div class="report-summary">
          <el-row :gutter="16">
            <el-col :span="8">
              <div class="summary-item">
                <el-icon class="summary-icon" color="#4285f4"><Grid /></el-icon>
                <div class="summary-content">
                  <div class="summary-value">{{ reportList.length }}</div>
                  <div class="summary-label">已配置报告</div>
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="summary-item">
                <el-icon class="summary-icon" color="#34a853"><CircleCheck /></el-icon>
                <div class="summary-content">
                  <div class="summary-value">{{ enabledCount }}</div>
                  <div class="summary-label">已启用同步</div>
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="summary-item">
                <el-icon class="summary-icon" color="#fbbc04"><Clock /></el-icon>
                <div class="summary-content">
                  <div class="summary-value">{{ todaySyncCount }}</div>
                  <div class="summary-label">今日同步次数</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- API使用说明 -->
      <div class="api-usage-info">
        <div class="info-title">
          <el-icon><InfoFilled /></el-icon>
          API 使用说明
        </div>
        <div class="info-content">
          <h4>GA4 Reporting API v2 支持</h4>
          <ul>
            <li><strong>常用指标：</strong>sessions（会话数）、activeUsers（活跃用户）、eventCount（事件数）、totalRevenue（总营收）、conversionRate（转化率）等</li>
            <li><strong>常用维度：</strong>date（日期）、country（国家）、deviceCategory（设备）、sessionSource（流量来源）、eventName（事件名称）等</li>
            <li><strong>数据类型：</strong>支持整数、浮点数、百分比、货币等多种数据类型</li>
          </ul>

          <h4>API 配额限制</h4>
          <ul>
            <li>每个项目每分钟最多 <strong>10次</strong> 请求</li>
            <li>每个项目每天最多 <strong>10万次</strong> 请求</li>
            <li>建议合理安排同步频率，避免超出配额限制</li>
          </ul>

          <h4>最佳实践</h4>
          <ul>
            <li>✅ 使用具体的时间范围而非"allTime"以提高查询性能</li>
            <li>✅ 每次请求不超过10个维度和10个指标</li>
            <li>✅ 合理使用排序和限制参数控制返回数据量</li>
            <li>✅ 在业务低峰期执行自动同步任务</li>
          </ul>
        </div>
      </div>
    </div>

    <!-- 底部操作按钮 -->
    <div class="action-buttons">
      <el-button @click="handleViewLog">
        <el-icon><List /></el-icon>
        查看日志
      </el-button>
      <el-button type="primary" @click="handleSaveConfig">
        <el-icon><Check /></el-icon>
        保存配置
      </el-button>
    </div>

    <!-- 添加/编辑报告对话框 -->
    <el-dialog
      v-model="addDialogVisible"
      :title="isEdit ? '编辑GA4报告' : '添加GA4报告'"
      width="700px"
    >
      <el-form :model="addForm" label-width="120px">
        <el-form-item label="报告名称" required>
          <el-input
            v-model="addForm.reportName"
            placeholder="请输入报告名称，如：用户访问分析"
          />
        </el-form-item>

        <el-form-item label="指标" required>
          <el-select
            v-model="addForm.metrics"
            multiple
            placeholder="请选择指标"
            style="width: 100%"
          >
            <el-option label="会话数 (sessions)" value="sessions" />
            <el-option label="活跃用户 (activeUsers)" value="activeUsers" />
            <el-option label="事件数 (eventCount)" value="eventCount" />
            <el-option label="总营收 (totalRevenue)" value="totalRevenue" />
            <el-option label="转化率 (conversionRate)" value="conversionRate" />
            <el-option label="页面浏览 (screenPageViews)" value="screenPageViews" />
            <el-option label="参与时长 (userEngagementDuration)" value="userEngagementDuration" />
            <el-option label="新用户 (newUsers)" value="newUsers" />
            <el-option label="总用户 (totalUsers)" value="totalUsers" />
          </el-select>
          <div class="form-tip">至少选择一个指标</div>
        </el-form-item>

        <el-form-item label="维度">
          <el-select
            v-model="addForm.dimensions"
            multiple
            placeholder="请选择维度（可选）"
            style="width: 100%"
          >
            <el-option label="日期 (date)" value="date" />
            <el-option label="国家 (country)" value="country" />
            <el-option label="设备类别 (deviceCategory)" value="deviceCategory" />
            <el-option label="浏览器 (browser)" value="browser" />
            <el-option label="流量来源 (sessionSource)" value="sessionSource" />
            <el-option label="事件名称 (eventName)" value="eventName" />
            <el-option label="页面路径 (pagePath)" value="pagePath" />
            <el-option label="城市 (city)" value="city" />
          </el-select>
          <div class="form-tip">维度可选，用于数据分组</div>
        </el-form-item>

        <el-form-item label="排序">
          <el-select
            v-model="addForm.orderBy"
            placeholder="请选择排序字段"
            style="width: 100%"
          >
            <el-option label="日期（降序）" value="date desc" />
            <el-option label="日期（升序）" value="date asc" />
            <el-option label="会话数（降序）" value="sessions desc" />
            <el-option label="活跃用户（降序）" value="activeUsers desc" />
            <el-option label="事件数（降序）" value="eventCount desc" />
            <el-option label="总营收（降序）" value="totalRevenue desc" />
          </el-select>
        </el-form-item>

        <el-form-item label="数据行数限制">
          <el-input-number
            v-model="addForm.limit"
            :min="1"
            :max="100000"
            :step="1000"
          />
          <span class="form-tip">行</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmAdd">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Setting,
  Document,
  DataAnalysis,
  Plus,
  Refresh,
  Edit,
  Delete,
  Grid,
  CircleCheck,
  Clock,
  InfoFilled,
  List,
  Check
} from "@element-plus/icons-vue";

// 同步策略表单
const strategyForm = ref({
  syncMode: "manual",
  frequency: "daily",
  syncTime: "",
  dateRange: []
});

// 报告列表
const reportList = ref([
  {
    id: 1,
    reportName: "用户访问分析",
    metrics: ["sessions", "activeUsers", "eventCount"],
    dimensions: ["date", "country", "deviceCategory"],
    orderBy: "date desc",
    limit: 10000,
    lastSyncTime: "2024-01-15 10:30:00",
    enabled: true
  },
  {
    id: 2,
    reportName: "电商转化分析",
    metrics: ["totalRevenue", "conversionRate", "eventCount"],
    dimensions: ["date", "sessionSource", "eventName"],
    orderBy: "date desc",
    limit: 10000,
    lastSyncTime: "2024-01-15 11:00:00",
    enabled: true
  },
  {
    id: 3,
    reportName: "页面路径分析",
    metrics: ["screenPageViews", "userEngagementDuration"],
    dimensions: ["pagePath", "date"],
    orderBy: "screenPageViews desc",
    limit: 5000,
    lastSyncTime: "2024-01-15 09:00:00",
    enabled: false
  },
  {
    id: 4,
    reportName: "流量来源分析",
    metrics: ["sessions", "activeUsers", "totalRevenue"],
    dimensions: ["sessionSource", "sessionMedium"],
    orderBy: "sessions desc",
    limit: 100,
    lastSyncTime: "2024-01-14 18:00:00",
    enabled: true
  }
]);

// 已启用数量
const enabledCount = computed(() => {
  return reportList.value.filter(item => item.enabled).length;
});

// 今日同步次数
const todaySyncCount = computed(() => {
  return 28;
});

// 添加报告对话框
const addDialogVisible = ref(false);
const isEdit = ref(false);
const addForm = ref({
  reportName: "",
  metrics: [],
  dimensions: [],
  orderBy: "",
  limit: 10000
});

// 添加报告
const handleAddReport = () => {
  isEdit.value = false;
  addForm.value = {
    reportName: "",
    metrics: [],
    dimensions: [],
    orderBy: "",
    limit: 10000
  };
  addDialogVisible.value = true;
};

// 编辑报告
const handleEdit = (row: any) => {
  isEdit.value = true;
  addForm.value = { ...row };
  addDialogVisible.value = true;
};

// 确认添加
const handleConfirmAdd = () => {
  if (!addForm.value.reportName || addForm.value.metrics.length === 0) {
    ElMessage.warning("请填写报告名称并选择至少一个指标");
    return;
  }

  if (isEdit.value) {
    const index = reportList.value.findIndex(item => item.id === addForm.value.id);
    if (index > -1) {
      reportList.value[index] = {
        ...addForm.value,
        lastSyncTime: reportList.value[index].lastSyncTime
      };
    }
    ElMessage.success("报告配置已更新");
  } else {
    reportList.value.push({
      ...addForm.value,
      id: Date.now(),
      lastSyncTime: "-",
      enabled: true
    });
    ElMessage.success("GA4报告添加成功");
  }

  addDialogVisible.value = false;
};

// 状态变化
const handleStatusChange = (row: any) => {
  const status = row.enabled ? "已启用" : "已禁用";
  ElMessage.success(`${row.reportName} ${status}`);
};

// 立即同步
const handleSync = (row: any) => {
  ElMessageBox.confirm(`确定要同步"${row.reportName}"数据吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    // TODO: 调用同步接口
    ElMessage.success(`${row.reportName} 同步任务已启动`);
  });
};

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除"${row.reportName}"吗？删除后无法恢复。`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    const index = reportList.value.findIndex(item => item.id === row.id);
    if (index > -1) {
      reportList.value.splice(index, 1);
    }
    ElMessage.success("删除成功");
  });
};

// 查看日志
const handleViewLog = () => {
  // TODO: 跳转到同步日志页面或打开日志对话框
  ElMessage.info("跳转到同步日志页面");
};

// 保存配置
const handleSaveConfig = () => {
  // TODO: 调用保存配置接口
  ElMessage.success("GA4数据同步配置已保存");
};
</script>

<style scoped lang="scss">
.api-sync-page {
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

    .section-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      font-weight: 600;
      color: #333;
      margin-bottom: 16px;
      padding-bottom: 8px;
      border-bottom: 2px solid #fbbc04;
    }

    .sync-strategy {
      margin-bottom: 32px;

      .strategy-form {
        :deep(.el-form-item__label) {
          font-size: 14px;
          color: #333;
          font-weight: 500;
        }

        :deep(.el-radio__label) {
          font-size: 14px;
          color: #666;
        }

        .form-tip {
          margin-left: 12px;
          font-size: 12px;
          color: #999;
          line-height: 1.5;
        }
      }
    }

    .api-reports {
      margin-bottom: 32px;

      .report-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        .section-title {
          margin-bottom: 0;
        }
      }

      .report-table {
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

        .report-name-cell {
          display: flex;
          align-items: center;
          gap: 6px;

          .report-icon {
            font-size: 16px;
            color: #4285f4;
          }
        }

        .tags-container {
          display: flex;
          flex-wrap: wrap;
          gap: 4px;
        }
      }

      .report-summary {
        padding: 16px;
        background-color: #f8f9fa;
        border-radius: 6px;

        .summary-item {
          display: flex;
          align-items: center;
          gap: 12px;
          padding: 12px;
          background-color: #fff;
          border-radius: 6px;
          border: 1px solid #e0e0e0;

          .summary-icon {
            font-size: 28px;
          }

          .summary-content {
            .summary-value {
              font-size: 20px;
              font-weight: 600;
              color: #333;
              line-height: 1;
            }

            .summary-label {
              font-size: 12px;
              color: #666;
              margin-top: 4px;
            }
          }
        }
      }
    }

    .api-usage-info {
      padding: 20px;
      background-color: #fef7e0;
      border: 1px solid #fbbc04;
      border-radius: 6px;

      .info-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 14px;
        font-weight: 600;
        color: #f9ab00;
        margin-bottom: 16px;
      }

      .info-content {
        h4 {
          font-size: 13px;
          font-weight: 600;
          color: #333;
          margin: 16px 0 8px;

          &:first-child {
            margin-top: 0;
          }
        }

        ul {
          margin: 8px 0;
          padding-left: 24px;

          li {
            font-size: 13px;
            color: #666;
            line-height: 1.8;
            margin-bottom: 4px;

            &:last-child {
              margin-bottom: 0;
            }

            strong {
              color: #4285f4;
            }
          }
        }
      }
    }
  }

  .action-buttons {
    display: flex;
    justify-content: flex-end;
    gap: 12px;

    button {
      min-width: 100px;
    }
  }

  .form-tip {
    margin-left: 12px;
    font-size: 12px;
    color: #999;
  }
}
</style>
