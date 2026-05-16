<template>
  <div class="plugin-detail-page">
    <!-- 头部信息 -->
    <div class="page-header">
      <div class="header-left">
        <el-icon class="back-icon" @click="handleBack">
          <ArrowLeft />
        </el-icon>
        <div class="plugin-icon">
          <img
            v-if="pluginDetail?.icon"
            :src="getUrl(pluginDetail.icon)"
            alt="插件图标"
          />
          <span v-else class="icon-fallback">{{
            pluginDetail?.connectorName?.[0] || "A"
          }}</span>
        </div>
        <div class="plugin-info">
          <h1 class="plugin-title">
            {{ pluginDetail?.connectorName || "插件详情" }}
          </h1>
          <span class="plugin-version">{{ pluginDetail?.version || "" }}</span>
        </div>
      </div>
    </div>

    <!-- 组件内容 -->
    <component :is="plugin?.component" v-if="plugin" />
    <div v-else class="loading-placeholder">
      <el-empty description="插件组件加载中..." />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ArrowLeft } from "@element-plus/icons-vue";
import { getUrl } from "@/utils/format";

import { pluginConfig } from "@/views/plugins";

const router = useRouter();
const route = useRoute();

// 安全地获取 pluginDetail
const pluginDetail = computed(() => {
  try {
    const detail = sessionStorage.getItem("plugin-detail");
    return detail ? JSON.parse(detail) : null;
  } catch (error) {
    console.error("解析 plugin-detail 失败:", error);
    return null;
  }
});

// 查找匹配的插件配置
const plugin = computed(() => {
  if (!pluginDetail.value) {
    console.warn("pluginDetail 为空");
    return null;
  }

  // 尝试多种匹配方式
  const connectorName = pluginDetail.value.connectorName || "";
  // console.log("查找插件配置:", connectorName);

  // 1. 精确匹配 title
  let obj = pluginConfig.find(e => e.title === connectorName);

  // 2. 如果没找到，尝试匹配 name
  if (!obj) {
    obj = pluginConfig.find(e => e.name === connectorName);
  }

  // 3. 如果没找到，尝试匹配 aliases
  if (!obj) {
    obj = pluginConfig.find(e =>
      e.aliases?.some(
        alias => connectorName.includes(alias) || alias.includes(connectorName)
      )
    );
  }

  // 4. 如果没找到，尝试模糊匹配 title
  if (!obj) {
    obj = pluginConfig.find(
      e => connectorName.includes(e.title) || e.title.includes(connectorName)
    );
  }

  // 5. 如果还是没找到，使用第一个插件作为默认值
  if (!obj && pluginConfig.length > 0) {
    console.warn("未找到匹配的插件配置，使用默认插件:", pluginConfig[0]);
    obj = pluginConfig[0];
  }

  return obj || null;
});

// 处理返回
const handleBack = () => {
  router.back();
};

// 组件挂载时检查
onMounted(() => {
  // console.log("当前路由参数:", route.query);
  // console.log("pluginDetail:", pluginDetail.value);
  // console.log("pluginConfig:", pluginConfig);
  // console.log("匹配到的插件:", plugin.value);
});
</script>

<style scoped lang="scss">
.plugin-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;

  .loading-placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 400px;
    background-color: #fff;
    margin: 24px;
    border-radius: 8px;
  }
}

.page-header {
  background-color: #fff;
  padding: 20px 24px;
  border-bottom: 1px solid #e8e8e8;

  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;

    .back-icon {
      font-size: 20px;
      color: #666;
      cursor: pointer;
      transition: color 0.3s;

      &:hover {
        color: #1890ff;
      }
    }

    .plugin-icon {
      width: 48px;
      height: 48px;
      border-radius: 8px;
      overflow: hidden;
      background-color: #52c41a;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 20px;
      font-weight: bold;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .plugin-info {
      display: flex;
      align-items: baseline;
      gap: 12px;

      .plugin-title {
        margin: 0;
        font-size: 18px;
        font-weight: 600;
        color: #333;
      }

      .plugin-version {
        font-size: 14px;
        color: #999;
      }
    }
  }
}
</style>
