<template>
  <div class="sync-log-list-virtual">
    <!-- 使用虚拟滚动优化长列表渲染 -->
    <VirtualList
      ref="virtualListRef"
      :data="logList"
      :container-height="containerHeight"
      :estimated-item-height="80"
      :buffer-size="5"
      key-prop="id"
      @scroll-bottom="handleScrollBottom"
    >
      <template #default="{ item, index }">
        <div class="log-row" :class="`log-row-${item.syncStatus}`">
          <div class="log-cell">{{ item.bitableName }}</div>
          <div class="log-cell">
            <el-tag :type="getSyncModeType(item.syncMode)" size="small">
              {{ getSyncModeLabel(item.syncMode) }}
            </el-tag>
          </div>
          <div class="log-cell">
            <el-tag :type="getSyncStatusType(item.syncStatus)" size="small">
              {{ getSyncStatusLabel(item.syncStatus) }}
            </el-tag>
          </div>
          <div class="log-cell text-right">{{ item.recordCount }}</div>
          <div class="log-cell text-right">{{ item.successCount }}</div>
          <div class="log-cell text-right">{{ item.failedCount }}</div>
          <div class="log-cell">{{ formatTime(item.startTime) }}</div>
          <div class="log-cell">{{ formatTime(item.endTime) }}</div>
          <div class="log-cell text-right">{{ item.duration }}s</div>
          <div class="log-cell actions">
            <el-button
              type="primary"
              link
              size="small"
              @click="handleViewDetail(item)"
            >
              详情
            </el-button>
            <el-button
              v-if="item.syncStatus === 'failed'"
              type="warning"
              link
              size="small"
              @click="handleRetry(item)"
              :loading="item.retrying"
            >
              重试
            </el-button>
          </div>
        </div>
      </template>
    </VirtualList>

    <!-- 加载更多提示 -->
    <div v-if="hasMore" class="load-more" @click="loadMore">
      <el-button :loading="loading" text>加载更多...</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import VirtualList from './VirtualList.vue';
import type { SyncLog } from '../types';
import { markRaw } from 'vue';

interface Props {
  logList: SyncLog[];
  loading?: boolean;
  hasMore?: boolean;
  containerHeight?: number;
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  hasMore: false,
  containerHeight: 600
});

const emit = defineEmits<{
  viewDetail: [log: SyncLog];
  retry: [log: SyncLog];
  loadMore: [];
}>();

const virtualListRef = ref();

// 使用 markRaw 优化映射表
const syncModeLabelMap = markRaw({
  webhook: 'Webhook',
  polling: '轮询',
  hybrid: '混合'
});

const syncModeTypeMap = markRaw({
  webhook: 'success',
  polling: 'warning',
  hybrid: 'primary'
});

const syncStatusLabelMap = markRaw({
  success: '成功',
  failed: '失败',
  processing: '同步中'
});

const syncStatusTypeMap = markRaw({
  success: 'success',
  failed: 'danger',
  processing: 'warning'
});

const getSyncModeLabel = (mode: string) => {
  return syncModeLabelMap[mode as keyof typeof syncModeLabelMap] || mode;
};

const getSyncModeType = (mode: string) => {
  return syncModeTypeMap[mode as keyof typeof syncModeTypeMap] || '';
};

const getSyncStatusLabel = (status: string) => {
  return syncStatusLabelMap[status as keyof typeof syncStatusLabelMap] || status;
};

const getSyncStatusType = (status: string) => {
  return syncStatusTypeMap[status as keyof typeof syncStatusTypeMap] || '';
};

const formatTime = (time: string) => {
  if (!time) return '-';
  return new Date(time).toLocaleString('zh-CN');
};

const handleViewDetail = (log: SyncLog) => {
  emit('viewDetail', log);
};

const handleRetry = (log: SyncLog) => {
  emit('retry', log);
};

const handleScrollBottom = () => {
  if (props.hasMore && !props.loading) {
    emit('loadMore');
  }
};

const loadMore = () => {
  if (props.hasMore && !props.loading) {
    emit('loadMore');
  }
};

// 暴露方法
defineExpose({
  scrollToTop: () => virtualListRef.value?.scrollToTop(),
  scrollToBottom: () => virtualListRef.value?.scrollToBottom(),
  refresh: () => virtualListRef.value?.refresh()
});
</script>

<style lang="scss" scoped>
.sync-log-list-virtual {
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  overflow: hidden;

  .log-row {
    display: grid;
    grid-template-columns: 180px 100px 100px 100px 100px 100px 180px 180px 120px 150px;
    gap: 8px;
    padding: 12px 16px;
    border-bottom: 1px solid #f3f4f6;
    align-items: center;
    font-size: 13px;
    transition: background 0.2s;

    &:hover {
      background: #f9fafb;
    }

    &.log-row-success {
      border-left: 3px solid #00b894;
    }

    &.log-row-failed {
      border-left: 3px solid #ef4444;
    }

    &.log-row-processing {
      border-left: 3px solid #f59e0b;
    }

    .log-cell {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;

      &.text-right {
        text-align: right;
      }

      &.actions {
        display: flex;
        gap: 8px;
        justify-content: flex-start;
      }
    }
  }

  .load-more {
    padding: 12px;
    text-align: center;
    border-top: 1px solid #e5e7eb;
    cursor: pointer;
    transition: background 0.2s;

    &:hover {
      background: #f9fafb;
    }
  }
}
</style>
