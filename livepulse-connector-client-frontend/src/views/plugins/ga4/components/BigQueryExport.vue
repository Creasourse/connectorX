<template>
  <div class="bigquery-export-page">
    <div class="config-card">
      <h2 class="config-title">BigQuery 导出配置</h2>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-position="top"
        class="config-form"
      >
        <!-- BigQuery 项目配置 -->
        <div class="form-section">
          <div class="section-title">BigQuery 项目配置</div>

          <el-form-item label="项目ID" prop="projectId">
            <el-input
              v-model="formData.projectId"
              placeholder="请输入Google Cloud项目ID"
              clearable
            >
              <template #prepend>
                <el-icon><Platform /></el-icon>
              </template>
            </el-input>
            <div class="form-tip">
              在Google Cloud Console首页可以找到项目ID
            </div>
          </el-form-item>

          <el-form-item label="数据集ID" prop="datasetId">
            <el-input
              v-model="formData.datasetId"
              placeholder="请输入BigQuery数据集ID"
              clearable
            >
              <template #prepend>
                <el-icon><Files /></el-icon>
              </template>
            </el-input>
            <div class="form-tip">
              GA4数据将导出到该数据集，数据集会自动创建
            </div>
          </el-form-item>

          <el-form-item label="数据集位置" prop="location">
            <el-select
              v-model="formData.location"
              placeholder="请选择数据集位置"
              style="width: 100%"
            >
              <el-option label="美国 (US)" value="US" />
              <el-option label="欧盟 (EU)" value="EU" />
              <el-option label="亚太地区 (asia-northeast1)" value="asia-northeast1" />
              <el-option label="澳大利亚 (australia-southeast1)" value="australia-southeast1" />
            </el-select>
            <div class="form-tip">
              选择与GA4属性相同的数据集位置以优化性能
            </div>
          </el-form-item>
        </div>

        <!-- 导出配置 -->
        <div class="form-section">
          <div class="section-title">导出配置</div>

          <el-form-item label="导出频率">
            <el-radio-group v-model="formData.exportFrequency">
              <el-radio label="daily">每日</el-radio>
              <el-radio label="streaming">流式（实时）</el-radio>
            </el-radio-group>
            <div class="form-tip">
              <strong>每日导出：</strong>每天一次，通常在凌晨导出前一天的数据<br>
              <strong>流式导出：</strong>实时推送事件数据到BigQuery，延迟更小
            </div>
          </el-form-item>

          <el-form-item label="包含广告数据">
            <el-switch v-model="formData.includeAds" />
            <div class="form-tip">
              是否将Google Ads数据导出到BigQuery
            </div>
          </el-form-item>

          <el-form-item label="数据保留策略" prop="retentionPeriod">
            <el-select
              v-model="formData.retentionPeriod"
              placeholder="请选择数据保留时间"
              style="width: 100%"
            >
              <el-option label="保留30天" value="30" />
              <el-option label="保留60天" value="60" />
              <el-option label="保留90天" value="90" />
              <el-option label="保留180天" value="180" />
              <el-option label="保留365天" value="365" />
              <el-option label="永久保留" value="infinity" />
            </el-select>
            <div class="form-tip">
              超过保留期的数据表将被自动删除
            </div>
          </el-form-item>

          <el-form-item label="表名模板">
            <el-input
              v-model="formData.tableTemplate"
              placeholder="events_{yyyyMMdd}"
            >
              <template #prepend>events_</template>
            </el-input>
            <div class="form-tip">
              支持{yyyyMMdd}、{yyyyMM}等日期占位符
            </div>
          </el-form-item>
        </div>

        <!-- 数据筛选 -->
        <div class="form-section">
          <div class="section-title">数据筛选（可选）</div>

          <el-form-item label="包含事件">
            <el-select
              v-model="formData.includedEvents"
              multiple
              filterable
              allow-create
              placeholder="请输入事件名称，如：purchase、page_view"
              style="width: 100%"
            >
              <el-option label="page_view" value="page_view" />
              <el-option label="purchase" value="purchase" />
              <el-option label="session_start" value="session_start" />
              <el-option label="first_visit" value="first_visit" />
              <el-option label="user_engagement" value="user_engagement" />
            </el-select>
            <div class="form-tip">
              留空表示导出所有事件
            </div>
          </el-form-item>

          <el-form-item label="排除参数">
            <el-input
              v-model="formData.excludedParams"
              type="textarea"
              :rows="3"
              placeholder="每行一个参数名，如：&#10;password&#10;token&#10;secret_key"
            />
            <div class="form-tip">
              从事件参数中排除敏感或不需要的参数
            </div>
          </el-form-item>
        </div>

        <!-- Schema配置 -->
        <div class="form-section">
          <div class="section-title">Schema 配置</div>

          <el-form-item label="使用BigQuery Storage API">
            <el-switch v-model="formData.useStorageApi" />
            <div class="form-tip">
              启用后可提高大数据量查询性能
            </div>
          </el-form-item>

          <el-form-item label="自动更新Schema">
            <el-switch v-model="formData.autoUpdateSchema" />
            <div class="form-tip">
              自动添加新的事件和参数到表结构中
            </div>
          </el-form-item>

          <el-form-item label="同步Schema">
            <el-switch v-model="formData.syncSchema" />
            <div class="form-tip">
              确保导出数据的Schema与GA4中定义的保持一致
            </div>
          </el-form-item>
        </div>
      </el-form>

      <!-- 配置预览 -->
      <div class="config-preview">
        <div class="preview-title">
          <el-icon><View /></el-icon>
          配置预览
        </div>
        <div class="preview-content">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="项目ID">
              {{ formData.projectId || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="数据集ID">
              {{ formData.datasetId || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="数据集位置">
              {{ getLocationName(formData.location) }}
            </el-descriptions-item>
            <el-descriptions-item label="导出频率">
              {{ formData.exportFrequency === 'daily' ? '每日' : '流式（实时）' }}
            </el-descriptions-item>
            <el-descriptions-item label="包含广告数据">
              <el-tag :type="formData.includeAds ? 'success' : 'info'" size="small">
                {{ formData.includeAds ? '是' : '否' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="数据保留">
              {{ getRetentionName(formData.retentionPeriod) }}
            </el-descriptions-item>
            <el-descriptions-item label="表名模板" :span="2">
              <code>events_{{ formData.tableTemplate || '{yyyyMMdd}' }}</code>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>

      <!-- 配置说明 -->
      <div class="config-instruction">
        <div class="instruction-title">
          <el-icon class="info-icon"><InfoFilled /></el-icon>
          配置说明
        </div>
        <div class="instruction-content">
          <h4>前提条件：</h4>
          <ul>
            <li>需要Google Cloud项目并启用BigQuery API</li>
            <li>需要BigQuery管理员权限或项目编辑者权限</li>
            <li>GA4属性需要配置BigQuery链接（在GA4管理员 > 产品关联 > BigQuery链接中设置）</li>
          </ul>

          <h4>数据导出说明：</h4>
          <ul>
            <li>每日导出通常在凌晨完成，数据按天分表</li>
            <li>流式导出实时推送，数据按时间分区</li>
            <li>导出的表包含：事件信息、用户属性、事件参数、物品信息等</li>
            <li>数据格式符合GA4 BigQuery Export Schema</li>
          </ul>

          <h4>费用估算：</h4>
          <ul>
            <li>BigQuery存储费用：约$0.02/GB/月</li>
            <li>查询费用：按扫描数据量计费，建议使用时间分区减少查询范围</li>
            <li>流式导出可能产生额外的写入费用</li>
          </ul>

          <h4>最佳实践：</h4>
          <ul>
            <li>定期检查数据导出是否正常</li>
            <li>使用数据保留策略控制存储成本</li>
            <li>合理使用分区表提高查询效率</li>
            <li>敏感数据使用排除参数过滤</li>
          </ul>
        </div>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button class="test-btn" @click="handleTest">
        测试连接
      </el-button>
      <el-button class="preview-btn" @click="handlePreviewData">
        预览数据
      </el-button>
      <el-button type="primary" class="save-btn" @click="handleSave">
        保存配置
      </el-button>
    </div>

    <!-- 数据预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="BigQuery 数据预览"
      width="80%"
      :close-on-click-modal="false"
    >
      <div class="preview-dialog-content">
        <div class="preview-query">
          <div class="query-title">示例查询：</div>
          <el-input
            type="textarea"
            :rows="4"
            readonly
            :model-value="sampleQuery"
            class="query-textarea"
          />
        </div>

        <div class="preview-result">
          <div class="result-title">预览结果（最近7天事件统计）：</div>
          <el-table :data="previewData" border max-height="400">
            <el-table-column prop="eventDate" label="日期" width="120" />
            <el-table-column prop="eventName" label="事件名称" min-width="150" />
            <el-table-column prop="eventCount" label="事件数" width="120" align="right">
              <template #default="{ row }">
                <el-tag type="primary">{{ row.eventCount.toLocaleString() }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="userCount" label="用户数" width="120" align="right" />
          </el-table>
        </div>
      </div>
      <template #footer>
        <el-button @click="previewDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleRunQuery">
          在BigQuery中运行
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { type PropType, ref, reactive, watch, computed } from "vue";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import { InfoFilled, Platform, Files, View } from "@element-plus/icons-vue";

const formRef = ref<FormInstance>();

const props = defineProps({
  config: {
    type: Object as PropType<unknown>
  }
});

const emit = defineEmits<{
  (e: "update"): void;
}>();

// 表单数据
const formData = reactive({
  projectId: "",
  datasetId: "analytics_",
  location: "US",
  exportFrequency: "daily",
  includeAds: false,
  retentionPeriod: "60",
  tableTemplate: "{yyyyMMdd}",
  includedEvents: [],
  excludedParams: "",
  useStorageApi: true,
  autoUpdateSchema: true,
  syncSchema: true
});

// 预览对话框
const previewDialogVisible = ref(false);

// 示例查询
const sampleQuery = computed(() => {
  if (!formData.projectId || !formData.datasetId) {
    return "-- 请先配置项目ID和数据集ID --";
  }
  return `SELECT
  event_date,
  event_name,
  COUNT(*) as event_count,
  COUNT(DISTINCT user_pseudo_id) as user_count
FROM \`${formData.projectId}.${formData.datasetId}.events_*\`
WHERE _TABLE_SUFFIX BETWEEN FORMAT_DATE('%Y%m%d', DATE_SUB(CURRENT_DATE(), INTERVAL 7 DAY))
  AND FORMAT_DATE('%Y%m%d', CURRENT_DATE())
GROUP BY 1, 2
ORDER BY 1 DESC, 3 DESC
LIMIT 1000`;
});

// 预览数据
const previewData = ref([
  {
    eventDate: "2024-01-15",
    eventName: "page_view",
    eventCount: 15234,
    userCount: 3245
  },
  {
    eventDate: "2024-01-15",
    eventName: "session_start",
    eventCount: 3245,
    userCount: 3245
  },
  {
    eventDate: "2024-01-15",
    eventName: "user_engagement",
    eventCount: 28567,
    userCount: 2987
  },
  {
    eventDate: "2024-01-14",
    eventName: "page_view",
    eventCount: 14567,
    userCount: 3156
  },
  {
    eventDate: "2024-01-14",
    eventName: "purchase",
    eventCount: 234,
    userCount: 189
  }
]);

// 监听 props.config 变化
watch(
  () => props.config,
  (newVal: any) => {
    if (newVal) {
      Object.assign(formData, newVal);
    }
  },
  { immediate: true, deep: true }
);

// 表单验证规则
const formRules: FormRules = {
  projectId: [
    { required: true, message: "请输入项目ID", trigger: "blur" },
    {
      pattern: /^[a-z0-9\-]+$/,
      message: "项目ID只能包含小写字母、数字和连字符",
      trigger: "blur"
    }
  ],
  datasetId: [
    { required: true, message: "请输入数据集ID", trigger: "blur" },
    {
      pattern: /^[a-zA-Z_][a-zA-Z0-9_]*$/,
      message: "数据集ID只能包含字母、数字和下划线，且必须以字母或下划线开头",
      trigger: "blur"
    }
  ],
  location: [
    { required: true, message: "请选择数据集位置", trigger: "change" }
  ],
  retentionPeriod: [
    { required: true, message: "请选择数据保留策略", trigger: "change" }
  ]
};

// 获取位置名称
const getLocationName = (location: string) => {
  const locationMap: Record<string, string> = {
    US: "美国 (US)",
    EU: "欧盟 (EU)",
    "asia-northeast1": "亚太地区",
    "australia-southeast1": "澳大利亚"
  };
  return locationMap[location] || "-";
};

// 获取保留期名称
const getRetentionName = (period: string) => {
  if (period === "infinity") return "永久保留";
  return `保留${period}天`;
};

// 测试连接
const handleTest = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    // TODO: 调用测试BigQuery连接接口
    ElMessage.success("BigQuery连接测试成功");
  } catch (error) {
    ElMessage.error("请先完善必填项");
  }
};

// 预览数据
const handlePreviewData = async () => {
  if (!formData.projectId || !formData.datasetId) {
    ElMessage.warning("请先配置项目ID和数据集ID");
    return;
  }

  try {
    await formRef.value?.validate();
    // TODO: 调用预览数据接口
    previewDialogVisible.value = true;
  } catch (error) {
    ElMessage.error("请先完善必填项");
  }
};

// 运行查询
const handleRunQuery = () => {
  // TODO: 跳转到BigQuery控制台或执行查询
  window.open(`https://console.cloud.google.com/bigquery?project=${formData.projectId}`, "_blank");
  ElMessage.info("正在跳转到BigQuery控制台");
};

// 保存配置
const handleSave = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    // TODO: 调用保存接口
    ElMessage.success("BigQuery导出配置已保存");
    emit("update");
  } catch (error) {
    ElMessage.error("请先完善必填项");
  }
};
</script>

<style scoped lang="scss">
.bigquery-export-page {
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

    .config-form {
      max-width: 700px;

      .form-section {
        margin-bottom: 32px;
        padding: 20px;
        background-color: #f8f9fa;
        border-radius: 6px;

        .section-title {
          font-size: 14px;
          font-weight: 600;
          color: #4285f4;
          margin-bottom: 20px;
          padding-bottom: 8px;
          border-bottom: 2px solid #4285f4;
        }
      }

      :deep(.el-form-item__label) {
        font-size: 14px;
        color: #333;
        font-weight: 500;
      }

      :deep(.el-input__wrapper) {
        background-color: #f5f5f5;
        border: none;
        box-shadow: none;
        padding: 8px 12px;

        &.is-focus {
          background-color: #e8f0fe;
        }
      }

      :deep(.el-input__inner) {
        color: #333;
      }

      :deep(.el-select .el-input__wrapper) {
        background-color: #f5f5f5;
      }

      :deep(.el-textarea__inner) {
        background-color: #f5f5f5;
        border: none;
        box-shadow: none;

        &:focus {
          background-color: #e8f0fe;
        }
      }

      :deep(.el-radio-group) {
        display: flex;
        gap: 24px;
      }

      .form-tip {
        margin-top: 4px;
        font-size: 12px;
        color: #999;
        line-height: 1.6;

        strong {
          color: #666;
        }
      }
    }

    .config-preview {
      margin-bottom: 24px;
      padding: 20px;
      background-color: #e8f0fe;
      border: 1px solid #4285f4;
      border-radius: 6px;

      .preview-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 14px;
        font-weight: 600;
        color: #4285f4;
        margin-bottom: 16px;
      }

      .preview-content {
        code {
          padding: 2px 6px;
          background-color: rgba(0, 0, 0, 0.05);
          border-radius: 3px;
          font-size: 13px;
          color: #d93025;
          font-family: 'Courier New', monospace;
        }
      }
    }

    .config-instruction {
      padding: 20px;
      background-color: #fef7e0;
      border: 1px solid #fbbc04;
      border-radius: 6px;

      .instruction-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 14px;
        font-weight: 600;
        color: #f9ab00;
        margin-bottom: 16px;

        .info-icon {
          font-size: 16px;
        }
      }

      .instruction-content {
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
          margin: 0;
          padding-left: 24px;

          li {
            font-size: 13px;
            color: #666;
            line-height: 1.8;
            margin-bottom: 4px;

            &:last-child {
              margin-bottom: 0;
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

    .test-btn {
      border: 1px solid #4285f4;
      color: #4285f4;
      background-color: #fff;

      &:hover {
        background-color: #e8f0fe;
      }
    }

    .preview-btn {
      border: 1px solid #34a853;
      color: #34a853;
      background-color: #fff;

      &:hover {
        background-color: #e6f4ea;
      }
    }

    .save-btn {
      background-color: #4285f4;

      &:hover {
        background-color: #3367d6;
      }
    }
  }

  .preview-dialog-content {
    .preview-query {
      margin-bottom: 24px;

      .query-title {
        font-size: 14px;
        font-weight: 600;
        color: #333;
        margin-bottom: 8px;
      }

      .query-textarea {
        :deep(.el-textarea__inner) {
          font-family: 'Courier New', monospace;
          font-size: 13px;
          line-height: 1.6;
        }
      }
    }

    .preview-result {
      .result-title {
        font-size: 14px;
        font-weight: 600;
        color: #333;
        margin-bottom: 12px;
      }
    }
  }
}
</style>
