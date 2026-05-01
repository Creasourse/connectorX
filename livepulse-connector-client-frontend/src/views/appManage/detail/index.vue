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
            v-if="pluginDetail.icon"
            :src="getUrl(pluginDetail.icon)"
            alt="插件图标"
          />
          <span v-else class="icon-fallback">{{
            pluginDetail.connectorName?.[0] || "A"
          }}</span>
        </div>
        <div class="plugin-info">
          <h1 class="plugin-title">{{ pluginDetail.connectorName }}</h1>
          <span class="plugin-version">{{ pluginDetail.version }}</span>
        </div>
      </div>
    </div>

    <!-- Tab 导航 -->
    <div class="tab-navigation">
      <div
        v-for="tab in tabs"
        :key="tab.key"
        class="tab-item"
        :class="{ active: activeTab === tab.key }"
        @click="handleTabChange(tab.key)"
      >
        {{ tab.label }}
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content-area">
      <component
        :is="currentComponent"
        :config="formData"
        @update="getConfig"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, markRaw } from "vue";
import { useRouter } from "vue-router";
import { ArrowLeft } from "@element-plus/icons-vue";
import { getUrl } from "@/utils/format";
import { getWecomCorp } from "@/api/wechat";
import BasicInfo from "./components/BasicInfo.vue";
import InterfaceSync from "./components/InterfaceSync.vue";
import EventSubscription from "./components/EventSubscription.vue";
import TagPush from "./components/TagPush.vue";
import EventCallback from "./components/EventCallback.vue";
import InterfaceCallback from "./components/InterfaceCallback.vue";

const router = useRouter();

const pluginDetail = JSON.parse(sessionStorage.getItem("plugin-detail"));

// 获取配置
const formData = reactive({
  wecomCorpId: "",
  corpId: "",
  corpSecret: "",
  companyName: "",
  agentId: ""
});
const getConfig = () => {
  const params = {
    currentPage: 1,
    pageSize: 10,
    companyName: "",
    corpId: "",
    sortType: 1
  };
  getWecomCorp(params).then(res => {
    if (res.success) {
      const config = res.data.list?.[0] || null;
      if (!config) return;
      const { wecomeCorpId, corpId, corpSecret, companyName, agents } = config;
      const agentId = agents?.[0]?.wecomCorpAgentId || "";
      Object.assign(formData, {
        wecomCorpId: wecomeCorpId,
        corpId,
        corpSecret,
        companyName,
        agentId
      });
    }
  });
};
onMounted(() => {
  getConfig();
});

// Tab 配置
const tabs = [
  { key: "basic", label: "基础信息", component: markRaw(BasicInfo) },
  { key: "sync", label: "接口同步", component: markRaw(InterfaceSync) },
  { key: "event", label: "事件订阅", component: markRaw(EventSubscription) },
  { key: "tag", label: "标签推送", component: markRaw(TagPush) },
  {
    key: "eventCallback",
    label: "事件回调记录",
    component: markRaw(EventCallback)
  },
  {
    key: "interfaceCallback",
    label: "接口回调记录",
    component: markRaw(InterfaceCallback)
  }
];

const activeTab = ref("basic");

// 当前显示的组件
const currentComponent = computed(() => {
  const currentTab = tabs.find(tab => tab.key === activeTab.value);
  return currentTab?.component || markRaw(BasicInfo);
});

// 处理返回
const handleBack = () => {
  router.back();
};

// 切换 Tab
const handleTabChange = (key: string) => {
  activeTab.value = key;
};
</script>

<style scoped lang="scss">
.plugin-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
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

.tab-navigation {
  display: flex;
  background-color: #fff;
  padding: 0 24px;
  border-bottom: 1px solid #e8e8e8;

  .tab-item {
    padding: 16px 20px;
    font-size: 14px;
    color: #666;
    cursor: pointer;
    position: relative;
    transition: all 0.3s;

    &:hover {
      color: #1890ff;
    }

    &.active {
      color: #1890ff;
      font-weight: 500;

      &::after {
        content: "";
        position: absolute;
        bottom: -1px;
        left: 0;
        right: 0;
        height: 2px;
        background-color: #1890ff;
      }
    }
  }
}

.content-area {
  padding: 24px;
}
</style>
