<template>
  <div class="config-card">
    <h2 class="config-title">主页数据</h2>

    <!-- 数据统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-label">粉丝总数</div>
        <div class="stat-value">12,345</div>
        <div class="stat-trend up">
          <el-icon><TrendCharts /></el-icon>
          <span>+12.5%</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-label">帖子总数</div>
        <div class="stat-value">234</div>
        <div class="stat-trend up">
          <el-icon><TrendCharts /></el-icon>
          <span>+8.3%</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-label">本周互动量</div>
        <div class="stat-value">5,678</div>
        <div class="stat-trend down">
          <el-icon><TrendCharts /></el-icon>
          <span>-3.2%</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-label">本周曝光量</div>
        <div class="stat-value">34,567</div>
        <div class="stat-trend up">
          <el-icon><TrendCharts /></el-icon>
          <span>+15.8%</span>
        </div>
      </div>
    </div>

    <!-- 帖子列表表格 -->
    <div class="posts-table">
      <div class="table-header">
        <h3 class="table-title">帖子列表</h3>
        <el-button type="primary" @click="handleSync">
          <el-icon class="btn-icon"><Refresh /></el-icon>
          同步数据
        </el-button>
      </div>
      <el-table :data="postsList" style="width: 100%">
        <el-table-column
          prop="content"
          label="帖子内容"
          show-overflow-tooltip
        />
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column prop="engagement" label="互动量" width="120" />
        <el-table-column prop="impressions" label="曝光量" width="120" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">
              查看详情
            </el-button>
            <el-button link type="primary" @click="handleResync(row)">
              重新同步
            </el-button>
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
        <li>主页数据包含粉丝数、帖子数、互动量等关键指标</li>
        <li>支持手动同步和自动定期同步Facebook主页数据</li>
        <li>点击同步数据可获取最新的主页信息和帖子列表</li>
        <li>数据更新频率可根据业务需求进行调整</li>
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
import { InfoFilled, Refresh, TrendCharts } from "@element-plus/icons-vue";

interface PostItem {
  id: string;
  content: string;
  publishTime: string;
  engagement: number;
  impressions: number;
}

const props = defineProps({
  config: {
    type: Object as PropType<unknown>
  }
});

const emit = defineEmits<{
  (e: "update"): void;
}>();

// 帖子列表
const postsList = ref<PostItem[]>([
  {
    id: "1",
    content: "新品发布通知：我们的最新产品现已上市，欢迎了解更多详情！",
    publishTime: "2024-01-15 14:30:25",
    engagement: 1234,
    impressions: 8567
  },
  {
    id: "2",
    content: "感谢大家的支持！我们的粉丝数已突破10000人！",
    publishTime: "2024-01-14 10:15:30",
    engagement: 3456,
    impressions: 15234
  },
  {
    id: "3",
    content: "本周优惠活动：全场8折，仅限3天！",
    publishTime: "2024-01-13 16:45:00",
    engagement: 2345,
    impressions: 12345
  },
  {
    id: "4",
    content: "分享我们的品牌故事，了解更多关于我们的信息...",
    publishTime: "2024-01-12 09:20:15",
    engagement: 987,
    impressions: 6789
  },
  {
    id: "5",
    content: "用户反馈征集：告诉我们您的想法和建议！",
    publishTime: "2024-01-11 15:00:00",
    engagement: 1567,
    impressions: 9876
  }
]);

// 监听 props.config 变化
watch(
  () => props.config,
  (newVal: any) => {
    if (newVal && newVal.postsList) {
      postsList.value = newVal.postsList;
    }
  },
  { immediate: true, deep: true }
);

// 同步数据
const handleSync = async () => {
  try {
    // TODO: 调用同步接口
    ElMessage.success("数据同步任务已启动，请稍后查看同步结果");
  } catch (error) {
    ElMessage.error("同步失败，请稍后重试");
  }
};

// 查看详情
const handleView = (row: PostItem) => {
  console.log("查看详情:", row);
  // TODO: 打开详情弹窗
};

// 重新同步
const handleResync = async (row: PostItem) => {
  try {
    // TODO: 调用重新同步接口
    ElMessage.success(`帖子「${row.content.substring(0, 20)}...」已重新同步`);
  } catch (error) {
    ElMessage.error("重新同步失败，请稍后重试");
  }
};

// 保存配置
const handleSave = async () => {
  try {
    const params = {
      postsList: postsList.value
    };
    // TODO: 调用保存接口
    console.log("保存主页数据配置:", params);
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

  .stats-cards {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    margin-bottom: 24px;

    .stat-card {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 8px;
      padding: 20px;
      color: #fff;

      .stat-label {
        font-size: 14px;
        opacity: 0.9;
        margin-bottom: 12px;
      }

      .stat-value {
        font-size: 28px;
        font-weight: 700;
        margin-bottom: 8px;
      }

      .stat-trend {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;

        &.up {
          color: #52c41a;
        }

        &.down {
          color: #ff4d4f;
        }
      }
    }
  }

  .posts-table {
    margin-bottom: 24px;

    .table-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .table-title {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: #333;
      }

      .el-button {
        display: flex;
        align-items: center;
        gap: 6px;

        .btn-icon {
          font-size: 14px;
        }
      }
    }

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
