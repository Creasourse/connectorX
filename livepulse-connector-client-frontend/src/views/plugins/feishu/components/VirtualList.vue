<template>
  <div
    ref="containerRef"
    class="virtual-list"
    :style="{ height: `${containerHeight}px` }"
    @scroll="handleScroll"
  >
    <!-- 滚动占位符（撑开容器高度） -->
    <div class="virtual-list-phantom" :style="{ height: `${totalHeight}px` }"></div>

    <!-- 可视区域内容 -->
    <div
      class="virtual-list-content"
      :style="{ transform: `translate3d(0, ${offset}px, 0)` }"
    >
      <div
        v-for="item in visibleData"
        :key="item[keyProp]"
        class="virtual-list-item"
        :style="{ height: `${estimatedItemHeight}px` }"
        :data-index="item.__index"
      >
        <slot :item="item" :index="item.__index"></slot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" generic="T">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';

interface Props<T> {
  // 数据列表
  data: T[];
  // 预估项高度（px）
  estimatedItemHeight?: number;
  // 容器高度（px）
  containerHeight?: number;
  // 缓冲区大小（可视区域外额外渲染的项目数）
  bufferSize?: number;
  // 唯一键名
  keyProp?: string;
}

const props = withDefaults(defineProps<Props<T>>(), {
  estimatedItemHeight: 50,
  containerHeight: 400,
  bufferSize: 5,
  keyProp: 'id'
});

const emit = defineEmits<{
  // 滚动到底部事件
  scrollBottom: [];
}>();

// 容器引用
const containerRef = ref<HTMLElement>();

// 滚动偏移量
const offset = ref(0);

// 可视区域起始索引
const startIndex = ref(0);

// 可视区域结束索引
const endIndex = ref(0);

// 实际高度映射
const heightMap = ref<Map<number, number>>(new Map());

// 计算总高度
const totalHeight = computed(() => {
  return props.data.length * props.estimatedItemHeight;
});

// 计算可视区域数据
const visibleData = computed(() => {
  const start = Math.max(0, startIndex.value - props.bufferSize);
  const end = Math.min(props.data.length, endIndex.value + props.bufferSize);

  return props.data.slice(start, end).map((item, index) => ({
    ...item,
    __index: start + index
  }));
});

// 获取实际项高度
const getItemHeight = (index: number): number => {
  return heightMap.value.get(index) || props.estimatedItemHeight;
};

// 更新实际高度
const updateItemHeight = (index: number, height: number) => {
  heightMap.value.set(index, height);
};

// 计算可视区域的索引范围
const updateVisibleRange = () => {
  if (!containerRef.value) return;

  const scrollTop = containerRef.value.scrollTop;
  const containerHeight = props.containerHeight;

  // 计算起始索引（使用二分查找优化）
  let start = 0;
  let end = props.data.length - 1;
  let accumulatedHeight = 0;

  while (start < end) {
    const mid = Math.floor((start + end) / 2);
    const midHeight = getItemHeight(mid);

    if (accumulatedHeight + midHeight < scrollTop) {
      accumulatedHeight += midHeight;
      start = mid + 1;
    } else {
      end = mid;
    }
  }

  startIndex.value = Math.max(0, start - props.bufferSize);

  // 计算结束索引
  let visibleEnd = start;
  let visibleHeight = accumulatedHeight;

  while (visibleEnd < props.data.length && visibleHeight < scrollTop + containerHeight + props.bufferSize * props.estimatedItemHeight) {
    visibleHeight += getItemHeight(visibleEnd);
    visibleEnd++;
  }

  endIndex.value = visibleEnd;

  // 更新偏移量
  let offsetHeight = 0;
  for (let i = 0; i < startIndex.value; i++) {
    offsetHeight += getItemHeight(i);
  }

  offset.value = offsetHeight;

  // 检测是否滚动到底部
  const isNearBottom = scrollTop + containerHeight >= totalHeight.value - 50;
  if (isNearBottom) {
    emit('scrollBottom');
  }
};

// 节流滚动处理
let scrollTimer: NodeJS.Timeout | null = null;
const handleScroll = () => {
  if (scrollTimer) {
    return;
  }

  scrollTimer = setTimeout(() => {
    updateVisibleRange();
    scrollTimer = null;
  }, 16); // 约 60fps
};

// 滚动到指定索引
const scrollToIndex = (index: number) => {
  if (!containerRef.value || index < 0 || index >= props.data.length) {
    return;
  }

  let scrollTop = 0;
  for (let i = 0; i < index; i++) {
    scrollTop += getItemHeight(i);
  }

  containerRef.value.scrollTop = scrollTop;
};

// 滚动到顶部
const scrollToTop = () => {
  if (containerRef.value) {
    containerRef.value.scrollTop = 0;
  }
};

// 滚动到底部
const scrollToBottom = () => {
  if (containerRef.value) {
    containerRef.value.scrollTop = totalHeight.value;
  }
};

// 刷新列表（重新计算）
const refresh = () => {
  heightMap.value.clear();
  updateVisibleRange();
};

// 监听数据变化
watch(
  () => props.data.length,
  () => {
    refresh();
  }
);

// 组件挂载后初始化
onMounted(() => {
  updateVisibleRange();

  // 使用 ResizeObserver 监听容器大小变化
  if (typeof ResizeObserver !== 'undefined' && containerRef.value) {
    const resizeObserver = new ResizeObserver(() => {
      updateVisibleRange();
    });

    resizeObserver.observe(containerRef.value);

    onUnmounted(() => {
      resizeObserver.disconnect();
    });
  }
});

// 暴露方法供父组件调用
defineExpose({
  scrollToIndex,
  scrollToTop,
  scrollToBottom,
  refresh,
  updateItemHeight
});
</script>

<style lang="scss" scoped>
.virtual-list {
  position: relative;
  overflow-y: auto;
  overflow-x: hidden;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb {
    background: #888;
    border-radius: 3px;

    &:hover {
      background: #555;
    }
  }

  .virtual-list-phantom {
    position: absolute;
    left: 0;
    right: 0;
    top: 0;
    z-index: -1;
  }

  .virtual-list-content {
    position: absolute;
    left: 0;
    right: 0;
    top: 0;
    will-change: transform;

    .virtual-list-item {
      box-sizing: border-box;
      overflow: hidden;
    }
  }
}
</style>
