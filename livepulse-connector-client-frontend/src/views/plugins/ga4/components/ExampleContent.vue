<template>
  <div class="example-content-container">
    <!-- 示例场景 -->
    <div class="example-section">
      <h3 class="section-title">常见使用场景示例</h3>

      <el-collapse v-model="activeNames" accordion>
        <!-- 场景1：用户访问分析 -->
        <el-collapse-item title="场景1：按国家统计活跃用户" name="1">
          <div class="code-block">
            <div class="code-header">
              <span>请求示例</span>
              <el-button type="primary" link @click="copyCode('countryUsers')">
                <el-icon><DocumentCopy /></el-icon>
                复制
              </el-button>
            </div>
            <pre><code id="countryUsers">{
  "dimensions": [{"name": "country"}],
  "metrics": [{"name": "activeUsers"}],
  "dateRanges": [{
    "startDate": "30daysAgo",
    "endDate": "yesterday"
  }]
}</code></pre>
          </div>
          <div class="result-preview">
            <strong>返回结果示例：</strong>
            <el-table :data="countryUsersResult" border size="small" style="margin-top: 8px">
              <el-table-column prop="country" label="国家" width="120" />
              <el-table-column prop="activeUsers" label="活跃用户" width="120" align="right" />
            </el-table>
          </div>
        </el-collapse-item>

        <!-- 场景2：电商转化分析 -->
        <el-collapse-item title="场景2：电商转化漏斗分析" name="2">
          <div class="code-block">
            <div class="code-header">
              <span>请求示例</span>
              <el-button type="primary" link @click="copyCode('ecommerceFunnel')">
                <el-icon><DocumentCopy /></el-icon>
                复制
              </el-button>
            </div>
            <pre><code id="ecommerceFunnel">{
  "dimensions": [{"name": "date"}],
  "metrics": [
    {"name": "screenPageViews"},
    {"name": "itemsViewed"},
    {"name": "addToCarts"},
    {"name": "purchaseToViewRate"}
  ],
  "dateRanges": [{
    "startDate": "7daysAgo",
    "endDate": "yesterday"
  }],
  "orderBys": [
    {"dimension": {"dimensionName": "date"}, "desc": true}
  ]
}</code></pre>
          </div>
        </el-collapse-item>

        <!-- 场景3：流量来源分析 -->
        <el-collapse-item title="场景3：流量来源渠道分析" name="3">
          <div class="code-block">
            <div class="code-header">
              <span>请求示例</span>
              <el-button type="primary" link @click="copyCode('trafficSource')">
                <el-icon><DocumentCopy /></el-icon>
                复制
              </el-button>
            </div>
            <pre><code id="trafficSource">{
  "dimensions": [
    {"name": "sessionSource"},
    {"name": "sessionMedium"}
  ],
  "metrics": [
    {"name": "sessions"},
    {"name": "conversions"},
    {"name": "totalRevenue"}
  ],
  "dateRanges": [{
    "startDate": "30daysAgo",
    "endDate": "yesterday"
  }],
  "orderBys": [
    {"metric": {"metricName": "totalRevenue"}, "desc": true}
  ],
  "limit": 20
}</code></pre>
          </div>
        </el-collapse-item>

        <!-- 场景4：设备分析 -->
        <el-collapse-item title="场景4：设备类型用户行为分析" name="4">
          <div class="code-block">
            <div class="code-header">
              <span>请求示例</span>
              <el-button type="primary" link @click="copyCode('deviceAnalysis')">
                <el-icon><DocumentCopy /></el-icon>
                复制
              </el-button>
            </div>
            <pre><code id="deviceAnalysis">{
  "dimensions": [{"name": "deviceCategory"}],
  "metrics": [
    {"name": "activeUsers"},
    {"name": "sessions"},
    {"name": "userEngagementDuration"},
    {"name": "screenPageViewsPerSession"}
  ],
  "dateRanges": [{
    "startDate": "7daysAgo",
    "endDate": "yesterday"
  }]
}</code></pre>
          </div>
        </el-collapse-item>

        <!-- 场景5：事件路径分析 -->
        <el-collapse-item title="场景5：用户事件路径分析" name="5">
          <div class="code-block">
            <div class="code-header">
              <span>请求示例</span>
              <el-button type="primary" link @click="copyCode('eventPath')">
                <el-icon><DocumentCopy /></el-icon>
                复制
              </el-button>
            </div>
            <pre><code id="eventPath">{
  "dimensions": [
    {"name": "eventName"},
    {"name": "pagePath"}
  ],
  "metrics": [
    {"name": "eventCount"},
    {"name": "totalUsers"}
  ],
  "dimensionFilter": {
    "filter": {
      "fieldName": "eventName",
      "inListFilter": {
        "values": [
          "page_view",
          "add_to_cart",
          "begin_checkout",
          "purchase"
        ]
      }
    }
  },
  "dateRanges": [{
    "startDate": "30daysAgo",
    "endDate": "yesterday"
  }]
}</code></pre>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>

    <!-- 最佳实践 -->
    <div class="best-practices">
      <h3 class="section-title">查询最佳实践</h3>
      <el-alert type="success" :closable="false">
        <template #title>
          <strong>性能优化建议</strong>
        </template>
        <ul class="practice-list">
          <li>✅ 使用具体的时间范围而非"allTime"，可显著提升查询性能</li>
          <li>✅ 限制返回的维度和指标数量，建议每次不超过10个</li>
          <li>✅ 使用 <code>limit</code> 参数控制返回行数，避免大数据量</li>
          <li>✅ 合理使用 <code>dimensionFilter</code> 过滤不需要的数据</li>
          <li>✅ 对结果进行排序时，优先对指标排序而非维度</li>
        </ul>
      </el-alert>

      <el-alert type="warning" :closable="false" style="margin-top: 16px">
        <template #title>
          <strong>常见错误避免</strong>
        </template>
        <ul class="practice-list">
          <li>⚠️ 避免使用不兼容的维度-指标组合（如 <code>sessions</code> 与 <code>eventName</code>）</li>
          <li>⚠️ 注意API配额限制，避免频繁调用导致超出限制</li>
          <li>⚠️ 数据可用性最长14个月，超出范围将返回空结果</li>
          <li>⚠️ 某些维度需要在GA4界面中启用（如用户年龄、性别）</li>
        </ul>
      </el-alert>
    </div>

    <!-- API参考链接 -->
    <div class="api-reference">
      <h3 class="section-title">相关资源</h3>
      <div class="reference-links">
        <el-link href="https://developers.google.com/analytics/devguides/reporting/data/v1/api-schema" target="_blank" type="primary">
          <el-icon><Link /></el-icon>
          GA4 Data API Schema 文档
        </el-link>
        <el-link href="https://developers.google.com/analytics/devguides/reporting/data/v1/rest/v1beta/properties/runReport" target="_blank" type="primary">
          <el-icon><Link /></el-icon>
          runReport 方法参考
        </el-link>
        <el-link href="https://ga-dev-tools.google/ga4/query-explorer/" target="_blank" type="primary">
          <el-icon><Link /></el-icon>
          GA4 Query Explorer（查询测试工具）
        </el-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { ElMessage } from "element-plus";
import { DocumentCopy, Link } from "@element-plus/icons-vue";

const activeNames = ref(["1"]);

const countryUsersResult = ref([
  { country: "China", activeUsers: "15,234" },
  { country: "United States", activeUsers: "8,567" },
  { country: "Japan", activeUsers: "5,123" }
]);

const copyCode = (id: string) => {
  const codeElement = document.getElementById(id);
  if (codeElement) {
    navigator.clipboard.writeText(codeElement.textContent || "").then(() => {
      ElMessage.success("代码已复制到剪贴板");
    });
  }
};
</script>

<style scoped lang="scss">
.example-content-container {
  .section-title {
    font-size: 15px;
    font-weight: 600;
    color: #333;
    margin-bottom: 16px;
  }

  .example-section {
    margin-bottom: 24px;

    .code-block {
      margin-bottom: 16px;

      .code-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 8px 12px;
        background-color: #f5f5f5;
        border: 1px solid #e0e0e0;
        border-bottom: none;
        border-radius: 4px 4px 0 0;
        font-size: 13px;
        font-weight: 500;
        color: #333;
      }

      pre {
        margin: 0;
        border-radius: 0 0 4px 4px;

        code {
          font-family: 'Courier New', monospace;
          font-size: 12px;
          line-height: 1.6;
        }
      }
    }

    .result-preview {
      padding: 12px;
      background-color: #f8f9fa;
      border-radius: 4px;
      font-size: 13px;

      strong {
        display: block;
        margin-bottom: 8px;
        color: #333;
      }
    }

    :deep(.el-collapse-item__header) {
      font-size: 14px;
      font-weight: 500;
    }

    :deep(.el-collapse-item__content) {
      padding-bottom: 16px;
    }
  }

  .best-practices {
    margin-bottom: 24px;

    .practice-list {
      margin: 8px 0 0;
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
          padding: 1px 6px;
          background-color: rgba(0, 0, 0, 0.05);
          border-radius: 3px;
          font-family: 'Courier New', monospace;
          font-size: 12px;
          color: #d93025;
        }
      }
    }
  }

  .api-reference {
    .reference-links {
      display: flex;
      flex-direction: column;
      gap: 12px;

      .el-link {
        font-size: 14px;
        align-items: center;

        .el-icon {
          margin-right: 4px;
        }
      }
    }
  }
}
</style>
