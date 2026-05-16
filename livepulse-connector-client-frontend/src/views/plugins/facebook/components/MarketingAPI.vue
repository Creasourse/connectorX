<template>
  <div class="config-card">
    <h2 class="config-title">营销API</h2>

    <!-- API配置表格 -->
    <div class="api-table">
      <el-table :data="apiList" style="width: 100%">
        <el-table-column prop="apiName" label="API名称" width="200" />
        <el-table-column prop="description" label="API说明" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.enabled"
              :active-value="true"
              :inactive-value="false"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 配置说明 -->
    <div class="permission-info">
      <div class="permission-title">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
        配置说明
      </div>
      <ul class="permission-list">
        <li>开启API后，系统将调用对应的Facebook Marketing API</li>
        <li>广告账户管理用于获取和管理广告账户信息</li>
        <li>广告系列管理用于创建和管理广告系列</li>
        <li>广告效果分析用于获取广告投放数据和分析报告</li>
        <li>请确保Access Token具备调用相应API的权限</li>
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

interface ApiItem {
  id: string;
  apiName: string;
  description: string;
  enabled: boolean;
}

const props = defineProps({
  config: {
    type: Object as PropType<unknown>
  }
});

const emit = defineEmits<{
  (e: "update"): void;
}>();

// API列表
const apiList = ref<ApiItem[]>([
  {
    id: "adAccount",
    apiName: "广告账户管理",
    description: "获取和管理广告账户信息，包括账户状态、预算等",
    enabled: true
  },
  {
    id: "adCampaign",
    apiName: "广告系列管理",
    description: "创建和管理广告系列，设置广告目标和预算",
    enabled: true
  },
  {
    id: "adSet",
    apiName: "广告组管理",
    description: "管理广告组，设置投放时间、受众、出价等",
    enabled: false
  },
  {
    id: "adCreative",
    apiName: "广告创意管理",
    description: "管理广告创意素材，包括图片、视频、文案等",
    enabled: false
  },
  {
    id: "adInsights",
    apiName: "广告效果分析",
    description: "获取广告投放数据，包括曝光、点击、转化等指标",
    enabled: true
  },
  {
    id: "audienceAnalysis",
    apiName: "受众分析",
    description: "分析受众画像，获取受众人口统计数据和兴趣偏好",
    enabled: false
  }
]);

// 监听 props.config 变化
watch(
  () => props.config,
  (newVal: any) => {
    if (newVal && newVal.apiList) {
      apiList.value = newVal.apiList;
    }
  },
  { immediate: true, deep: true }
);

// 状态变更处理
const handleStatusChange = (row: ApiItem) => {
  console.log(`API ${row.apiName} 状态: ${row.enabled ? "开启" : "关闭"}`);
};

// 保存配置
const handleSave = async () => {
  try {
    const enabledApis = apiList.value
      .filter(item => item.enabled)
      .map(item => item.id);

    const params = {
      apiList: apiList.value,
      enabledApis
    };
    // TODO: 调用保存接口
    console.log("保存营销API配置:", params);
    ElMessage.success("保存成功");
    emit("update");
  } catch (error) {
    ElMessage.error("保存失败，请稍后重试");
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

  .api-table {
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

    :deep(.el-switch) {
      &.is-checked {
        .el-switch__core {
          background-color: #52c41a;
          border-color: #52c41a;
        }
      }
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
