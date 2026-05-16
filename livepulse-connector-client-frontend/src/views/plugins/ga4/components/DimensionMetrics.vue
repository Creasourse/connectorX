<template>
  <div class="dimension-metrics-page">
    <div class="config-card">
      <h2 class="config-title">GA4 维度与指标指南</h2>

      <!-- 搜索和筛选 -->
      <div class="search-section">
        <el-row :gutter="16">
          <el-col :span="10">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索维度或指标名称、API名称"
              clearable
              @input="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-col>
          <el-col :span="8">
            <el-select
              v-model="selectedCategory"
              placeholder="选择分类"
              clearable
              @change="handleCategoryChange"
            >
              <el-option label="全部" value="" />
              <el-option label="用户维度" value="user" />
              <el-option label="会话维度" value="session" />
              <el-option label="事件维度" value="event" />
              <el-option label="电商维度" value="ecommerce" />
              <el-option label="广告维度" value="ad" />
              <el-option label="流量来源维度" value="traffic" />
              <el-option label="技术维度" value="tech" />
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-select
              v-model="selectedType"
              placeholder="类型"
              clearable
              @change="handleTypeChange"
            >
              <el-option label="全部" value="" />
              <el-option label="维度" value="dimension" />
              <el-option label="指标" value="metric" />
            </el-select>
          </el-col>
        </el-row>
      </div>

      <!-- 统计信息 -->
      <div class="stats-section">
        <div class="stat-item">
          <el-icon class="stat-icon" color="#4285f4"><Grid /></el-icon>
          <div class="stat-content">
            <div class="stat-value">{{ totalDimensions }}</div>
            <div class="stat-label">维度总数</div>
          </div>
        </div>
        <div class="stat-item">
          <el-icon class="stat-icon" color="#34a853"><DataLine /></el-icon>
          <div class="stat-content">
            <div class="stat-value">{{ totalMetrics }}</div>
            <div class="stat-label">指标总数</div>
          </div>
        </div>
        <div class="stat-item">
          <el-icon class="stat-icon" color="#fbbc04"><Document /></el-icon>
          <div class="stat-content">
            <div class="stat-value">{{ filteredCount }}</div>
            <div class="stat-label">筛选结果</div>
          </div>
        </div>
      </div>

      <!-- Tab 切换 -->
      <el-tabs v-model="activeTab" class="content-tabs">
        <el-tab-pane label="维度" name="dimension">
          <DimensionTable
            :data="filteredDimensions"
            :loading="loading"
          />
        </el-tab-pane>
        <el-tab-pane label="指标" name="metric">
          <MetricTable
            :data="filteredMetrics"
            :loading="loading"
          />
        </el-tab-pane>
        <el-tab-pane label="使用示例" name="example">
          <ExampleContent />
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 快速参考 -->
    <div class="quick-reference-card">
      <div class="reference-title">
        <el-icon><QuestionFilled /></el-icon>
        快速参考
      </div>
      <div class="reference-content">
        <el-row :gutter="16">
          <el-col :span="8">
            <div class="ref-item">
              <div class="ref-icon">📱</div>
              <div class="ref-text">
                <strong>设备类别</strong>
                <code>deviceCategory</code>
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="ref-item">
              <div class="ref-icon">🌍</div>
              <div class="ref-text">
                <strong>国家/地区</strong>
                <code>country</code>
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="ref-item">
              <div class="ref-icon">🔗</div>
              <div class="ref-text">
                <strong>页面路径</strong>
                <code>pagePath</code>
              </div>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="16" style="margin-top: 16px">
          <el-col :span="8">
            <div class="ref-item">
              <div class="ref-icon">👥</div>
              <div class="ref-text">
                <strong>活跃用户</strong>
                <code>activeUsers</code>
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="ref-item">
              <div class="ref-icon">💰</div>
              <div class="ref-text">
                <strong>总营收</strong>
                <code>totalRevenue</code>
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="ref-item">
              <div class="ref-icon">🎯</div>
              <div class="ref-text">
                <strong>转化率</strong>
                <code>conversionRate</code>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- API 使用说明 -->
    <div class="api-usage-card">
      <div class="usage-title">
        <el-icon><DocumentCopy /></el-icon>
        API 使用说明
      </div>
      <div class="usage-content">
        <h4>请求格式：</h4>
        <pre><code>POST https://analyticsdata.googleapis.com/v1beta/properties/GA4_PROPERTY_ID:runReport
{
  "dimensions": [
    {"name": "date"},
    {"name": "country"}
  ],
  "metrics": [
    {"name": "activeUsers"},
    {"name": "sessions"}
  ],
  "dateRanges": [{
    "startDate": "30daysAgo",
    "endDate": "yesterday"
  }]
}</code></pre>

        <h4>常见错误：</h4>
        <ul>
          <li><strong>不兼容的组合：</strong>某些维度和指标不能一起使用，如 <code>session</code> 指标与 <code>eventName</code> 维度</li>
          <li><strong>配额限制：</strong>每个项目每分钟最多10次请求，每天10万次请求</li>
          <li><strong>数据可用性：</strong>GA4数据最长保留14个月，超过范围的数据无法查询</li>
        </ul>

        <h4>最佳实践：</h4>
        <ul>
          <li>使用具体的时间范围而不是"allTime"以提高性能</li>
          <li>每次请求不超过10个维度和10个指标</li>
          <li>使用 <code>offset</code> 和 <code>limit</code> 参数分页获取大量数据</li>
          <li>合理使用 <code>orderBys</code> 参数进行排序</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { Search, Grid, DataLine, Document, QuestionFilled, DocumentCopy } from "@element-plus/icons-vue";
import DimensionTable from "./DimensionTable.vue";
import MetricTable from "./MetricTable.vue";
import ExampleContent from "./ExampleContent.vue";

// 搜索关键词
const searchKeyword = ref("");
const selectedCategory = ref("");
const selectedType = ref("");
const activeTab = ref("dimension");
const loading = ref(false);

// 维度数据
const allDimensions = ref([
  // 用户维度
  { apiName: "country", uiName: "国家/地区", category: "user", description: "用户所在国家/地区", example: "China, United States" },
  { apiName: "city", uiName: "城市", category: "user", description: "用户所在城市", example: "Beijing, New York" },
  { apiName: "language", uiName: "语言", category: "user", description: "用户浏览器语言", example: "zh-cn, en-us" },
  { apiName: "userGender", uiName: "用户性别", category: "user", description: "用户性别（需启用）", example: "male, female" },
  { apiName: "userAgeBracket", uiName: "用户年龄段", category: "user", description: "用户年龄范围（需启用）", example: "18-24, 25-34" },

  // 会话维度
  { apiName: "sessionSource", uiName: "会话来源", category: "session", description: "会话流量来源", example: "google, facebook" },
  { apiName: "sessionMedium", uiName: "会话媒介", category: "session", description: "会话流量媒介", example: "organic, cpc, referral" },
  { apiName: "sessionCampaign", uiName: "会话营销活动", category: "session", description: "会话关联的营销活动", example: "spring_sale" },
  { apiName: "sessionDefaultChannelGroup", uiName: "默认渠道组", category: "session", description: "默认渠道分组", example: "Organic Search, Paid Social" },

  // 事件维度
  { apiName: "eventName", uiName: "事件名称", category: "event", description: "GA4事件的名称", example: "page_view, purchase" },
  { apiName: "date", uiName: "日期", category: "event", description: "事件的日期（YYYYMMDD格式）", example: "20240115" },
  { apiName: "year", uiName: "年份", category: "event", description: "事件的年份", example: "2024" },
  { apiName: "month", uiName: "月份", category: "event", description: "事件的月份（1-12）", example: "1" },
  { apiName: "week", uiName: "周", category: "event", description: "事件的周数（1-54）", example: "3" },
  { apiName: "hour", uiName: "小时", category: "event", description: "事件发生的小时（0-23）", example: "14" },
  { apiName: "pagePath", uiName: "页面路径", category: "event", description: "页面URL路径", example: "/home, /products/123" },
  { apiName: "pageTitle", uiName: "页面标题", category: "event", description: "页面标题", example: "首页, 产品详情" },
  { apiName: "pageReferrer", uiName: "页面引用来源", category: "event", description: "引导用户到当前页面的来源", example: "https://google.com" },

  // 电商维度
  { apiName: "itemName", uiName: "商品名称", category: "ecommerce", description: "电商商品的名称", example: "iPhone 15 Pro" },
  { apiName: "itemCategory", uiName: "商品类别", category: "ecommerce", description: "电商商品的类别", example: "Electronics, Phones" },
  { apiName: "itemId", uiName: "商品ID", category: "ecommerce", description: "电商商品的唯一标识", example: "SKU_12345" },
  { apiName: "transactionId", uiName: "交易ID", category: "ecommerce", description: "购买交易的唯一标识", example: "T_20240115_001" },

  // 广告维度
  { apiName: "campaignName", uiName: "营销活动名称", category: "ad", description: "Google Ads营销活动名称", example: "Spring Sale 2024" },
  { apiName: "adGroupName", uiName: "广告组名称", category: "ad", description: "Google Ads广告组名称", example: "Display Ads Group 1" },

  // 流量来源维度
  { apiName: "source", uiName: "来源", category: "traffic", description: "流量来源", example: "google, bing, direct" },
  { apiName: "medium", uiName: "媒介", category: "traffic", description: "流量媒介", example: "organic, cpc, referral, none" },
  { apiName: "sourceMedium", uiName: "来源/媒介", category: "traffic", description: "来源和媒介的组合", example: "google / organic, facebook / cpc" },

  // 技术维度
  { apiName: "deviceCategory", uiName: "设备类别", category: "tech", description: "用户设备类型", example: "mobile, desktop, tablet" },
  { apiName: "browser", uiName: "浏览器", category: "tech", description: "用户浏览器", example: "Chrome, Safari, Firefox" },
  { apiName: "operatingSystem", uiName: "操作系统", category: "tech", description: "用户操作系统", example: "iOS, Android, Windows" },
  { apiName: "platform", uiName: "平台", category: "tech", description: "应用平台", example: "iOS, Android, Web" }
]);

// 指标数据
const allMetrics = ref([
  // 用户指标
  { apiName: "activeUsers", uiName: "活跃用户", category: "user", description: "去重后的活跃用户数", type: "integer", example: "12500" },
  { apiName: "newUsers", uiName: "新用户", category: "user", description: "首次访问的用户数", type: "integer", example: "3200" },
  { apiName: "totalUsers", uiName: "总用户", category: "user", description: "选定时间范围内的去重用户总数", type: "integer", example: "45000" },
  { apiName: "userRetentionRate", uiName: "用户留存率", category: "user", description: "用户回访率", type: "percent", example: "45.2%" },

  // 会话指标
  { apiName: "sessions", uiName: "会话数", category: "session", description: "总会话数量", type: "integer", example: "25000" },
  { apiName: "sessionsPerUser", uiName: "每用户会话数", category: "session", description: "平均每个用户的会话数", type: "float", example: "1.8" },
  { apiName: "engagementRate", uiName: "参与度", category: "session", description: "参与会话的百分比", type: "percent", example: "68.5%" },

  // 事件指标
  { apiName: "eventCount", uiName: "事件数", category: "event", description: "总事件触发次数", type: "integer", example: "150000" },
  { apiName: "eventCountPerSession", uiName: "每会话事件数", category: "event", description: "平均每次会话的事件数", type: "float", example: "6.0" },

  // 页面浏览指标
  { apiName: "screenPageViews", uiName: "页面浏览量", category: "event", description: "APP屏幕或网页页面浏览次数", type: "integer", example: "85000" },
  { apiName: "screenPageViewsPerSession", uiName: "每会话页面浏览", category: "event", description: "平均每次会话的页面浏览数", type: "float", example: "3.4" },

  // 电商指标
  { apiName: "totalRevenue", uiName: "总营收", category: "ecommerce", description: "总购买收入（不含税费、运费）", type: "currency", example: "¥125,600" },
  { apiName: "itemRevenue", uiName: "商品营收", category: "ecommerce", description: "商品销售总收入", type: "currency", example: "¥98,500" },
  { apiName: "purchaseRevenue", uiName: "购买营收", category: "ecommerce", description: "购买事件产生的收入", type: "currency", example: "¥115,000" },
  { apiName: "purchaseToViewRate", uiName: "购买浏览比", category: "ecommerce", description: "购买次数与浏览次数的比率", type: "percent", example: "3.2%" },
  { apiName: "itemsViewed", uiName: "商品查看次数", category: "ecommerce", description: "商品详情页查看事件数", type: "integer", example: "15000" },
  { apiName: "itemsPurchased", uiName: "商品购买数量", category: "ecommerce", description: "购买的商品总数量", type: "integer", example: "850" },

  // 转化指标
  { apiName: "conversions", uiName: "转化次数", category: "ecommerce", description: "转化事件总次数", example: "1250", type: "integer" },
  { apiName: "conversionRate", uiName: "转化率", category: "ecommerce", description: "转化次数与总会话数的比率", example: "5.0%", type: "percent" },

  // 广告指标
  { apiName: "adUnitExposure", uiName: "广告单元曝光", category: "ad", description: "广告单元曝光次数", example: "50000", type: "integer" },
  { apiName: "adUnitImpressions", uiName: "广告单元展示", category: "ad", description: "广告单元展示次数", example: "45000", type: "integer" },

  // 用户参与指标
  { apiName: "userEngagementDuration", uiName: "用户参与时长", category: "session", description: "用户总参与时长（秒）", example: "1250000", type: "integer" },
  { apiName: "averageSessionDuration", uiName: "平均会话时长", category: "session", description: "平均每次会话的持续时间（秒）", example: "185", type: "float" },

  // ROI指标
  { apiName: "ROAS", uiName: "广告支出回报率", category: "ad", description: "广告支出回报率", example: "350%", type: "percent" },
  { apiName: "returnOnAdSpend", uiName: "广告投资回报", category: "ad", description: "广告投资回报", example: "3.5", type: "float" }
]);

// 统计数据
const totalDimensions = computed(() => allDimensions.value.length);
const totalMetrics = computed(() => allMetrics.value.length);
const filteredCount = computed(() => {
  return filteredDimensions.value.length + filteredMetrics.value.length;
});

// 筛选后的维度
const filteredDimensions = computed(() => {
  let result = allDimensions.value;

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(item =>
      item.apiName.toLowerCase().includes(keyword) ||
      item.uiName.toLowerCase().includes(keyword) ||
      item.description.toLowerCase().includes(keyword)
    );
  }

  if (selectedCategory.value) {
    result = result.filter(item => item.category === selectedCategory.value);
  }

  return result;
});

// 筛选后的指标
const filteredMetrics = computed(() => {
  let result = allMetrics.value;

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(item =>
      item.apiName.toLowerCase().includes(keyword) ||
      item.uiName.toLowerCase().includes(keyword) ||
      item.description.toLowerCase().includes(keyword)
    );
  }

  if (selectedCategory.value) {
    result = result.filter(item => item.category === selectedCategory.value);
  }

  return result;
});

// 搜索处理
const handleSearch = () => {
  // 触发筛选
};

// 分类变化
const handleCategoryChange = () => {
  // 触发筛选
};

// 类型变化
const handleTypeChange = () => {
  if (selectedType.value === "dimension") {
    activeTab.value = "dimension";
  } else if (selectedType.value === "metric") {
    activeTab.value = "metric";
  }
};

onMounted(() => {
  // 初始化数据
});
</script>

<style scoped lang="scss">
.dimension-metrics-page {
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

    .search-section {
      margin-bottom: 24px;
    }

    .stats-section {
      display: flex;
      gap: 24px;
      margin-bottom: 24px;
      padding: 20px;
      background-color: #f8f9fa;
      border-radius: 8px;

      .stat-item {
        display: flex;
        align-items: center;
        gap: 16px;
        flex: 1;

        .stat-icon {
          font-size: 32px;
        }

        .stat-content {
          .stat-value {
            font-size: 24px;
            font-weight: 600;
            color: #333;
            line-height: 1;
          }

          .stat-label {
            font-size: 13px;
            color: #666;
            margin-top: 4px;
          }
        }
      }
    }

    .content-tabs {
      :deep(.el-tabs__header) {
        margin-bottom: 20px;
      }

      :deep(.el-tabs__nav-wrap::after) {
        display: none;
      }

      :deep(.el-tabs__item) {
        font-size: 14px;
        color: #666;

        &:hover {
          color: #fbbc04;
        }

        &.is-active {
          color: #fbbc04;
          font-weight: 500;
        }
      }

      :deep(.el-tabs__active-bar) {
        background-color: #fbbc04;
      }
    }
  }

  .quick-reference-card {
    background-color: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
    margin-bottom: 16px;

    .reference-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      font-weight: 600;
      color: #333;
      margin-bottom: 16px;
    }

    .reference-content {
      .ref-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 12px;
        background-color: #f8f9fa;
        border-radius: 6px;
        margin-bottom: 8px;

        .ref-icon {
          font-size: 24px;
          width: 40px;
          height: 40px;
          display: flex;
          align-items: center;
          justify-content: center;
          background-color: #fff;
          border-radius: 8px;
        }

        .ref-text {
          flex: 1;

          strong {
            display: block;
            font-size: 14px;
            color: #333;
            margin-bottom: 4px;
          }

          code {
            display: inline-block;
            padding: 2px 8px;
            background-color: #fbbc04;
            color: #fff;
            border-radius: 3px;
            font-size: 12px;
            font-family: 'Courier New', monospace;
          }
        }
      }
    }
  }

  .api-usage-card {
    background-color: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

    .usage-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      font-weight: 600;
      color: #333;
      margin-bottom: 16px;
    }

    .usage-content {
      h4 {
        font-size: 13px;
        font-weight: 600;
        color: #333;
        margin: 16px 0 8px;

        &:first-child {
          margin-top: 0;
        }
      }

      pre {
        background-color: #f5f5f5;
        border: 1px solid #e0e0e0;
        border-radius: 4px;
        padding: 16px;
        overflow-x: auto;
        margin: 8px 0;

        code {
          font-family: 'Courier New', monospace;
          font-size: 12px;
          line-height: 1.6;
          color: #333;
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

          code {
            padding: 1px 4px;
            background-color: #f5f5f5;
            border: 1px solid #e0e0e0;
            border-radius: 3px;
            font-size: 12px;
            font-family: 'Courier New', monospace;
            color: #d93025;
          }

          strong {
            color: #4285f4;
          }
        }
      }
    }
  }
}
</style>
