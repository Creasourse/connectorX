<template>
  <div class="config-card">
    <h2 class="config-title">标签同步</h2>

    <!-- 标签同步表格 -->
    <div class="tag-table">
      <el-table :data="tagList" style="width: 100%">
        <el-table-column prop="tagName" label="标签名称" width="200" />
        <el-table-column prop="userCount" label="用户数量" width="150" />
        <el-table-column label="同步状态" width="150">
          <template #default="{ row }">
            <el-tag :type="row.syncStatus === 'synced' ? 'success' : 'info'">
              {{ row.syncStatus === 'synced' ? '已同步' : '未同步' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleSyncTag(row)"
            >
              同步
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 批量操作 -->
      <div class="batch-actions">
        <el-button type="primary" @click="handleSyncAll">
          <el-icon class="btn-icon"><Refresh /></el-icon>
          立即同步全部标签
        </el-button>
      </div>
    </div>

    <!-- 配置说明 -->
    <div class="permission-info">
      <div class="permission-title">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
        配置说明
      </div>
      <ul class="permission-list">
        <li>标签同步可将微信公众号标签同步至本系统</li>
        <li>支持单个标签同步和批量同步操作</li>
        <li>同步后的标签可在用户管理中使用</li>
        <li>建议定期同步以保持标签数据最新</li>
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
import { InfoFilled, Refresh } from "@element-plus/icons-vue";

interface TagItem {
  id: string;
  tagName: string;
  userCount: number;
  syncStatus: 'synced' | 'unsynced';
}

const props = defineProps({
  config: {
    type: Object as PropType<unknown>
  }
});

const emit = defineEmits<{
  (e: "update"): void;
}>();

// 标签列表
const tagList = ref<TagItem[]>([
  {
    id: '1',
    tagName: 'VIP客户',
    userCount: 1523,
    syncStatus: 'synced'
  },
  {
    id: '2',
    tagName: '潜在客户',
    userCount: 3456,
    syncStatus: 'synced'
  },
  {
    id: '3',
    tagName: '活跃用户',
    userCount: 8934,
    syncStatus: 'unsynced'
  },
  {
    id: '4',
    tagName: '新用户',
    userCount: 5678,
    syncStatus: 'unsynced'
  },
  {
    id: '5',
    tagName: '沉睡用户',
    userCount: 2345,
    syncStatus: 'unsynced'
  }
]);

// 监听 props.config 变化
watch(
  () => props.config,
  (newVal: any) => {
    if (newVal && newVal.tagList) {
      tagList.value = newVal.tagList;
    }
  },
  { immediate: true, deep: true }
);

// 同步单个标签
const handleSyncTag = async (row: TagItem) => {
  try {
    // TODO: 调用同步接口
    ElMessage.success(`标签「${row.tagName}」同步成功`);
    row.syncStatus = 'synced';
  } catch (error) {
    ElMessage.error(`标签「${row.tagName}」同步失败，请稍后重试`);
  }
};

// 同步全部标签
const handleSyncAll = async () => {
  try {
    // TODO: 调用批量同步接口
    ElMessage.success('全部标签同步任务已启动，请稍后查看同步结果');
    // 模拟全部同步成功
    tagList.value.forEach(tag => {
      tag.syncStatus = 'synced';
    });
  } catch (error) {
    ElMessage.error('批量同步失败，请稍后重试');
  }
};

// 保存配置
const handleSave = async () => {
  try {
    const params = {
      tagList: tagList.value
    };
    // TODO: 调用保存接口
    console.log('保存标签同步配置:', params);
    ElMessage.success('保存成功');
    emit('update');
  } catch (error) {
    ElMessage.error('保存失败，请稍后重试');
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

  .tag-table {
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

    .batch-actions {
      margin-top: 16px;
      display: flex;
      justify-content: flex-end;

      .el-button {
        display: flex;
        align-items: center;
        gap: 6px;

        .btn-icon {
          font-size: 14px;
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
