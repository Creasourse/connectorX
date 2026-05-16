<template>
  <div>
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
import BasicInfo from "./components/BasicInfo.vue";
import OrderSync from "./components/OrderSync.vue";
import ProductManage from "./components/ProductManage.vue";
import UserIdentity from "./components/UserIdentity.vue";
import MultiStoreManage from "./components/MultiStoreManage.vue";
import PerformanceOptimize from "./components/PerformanceOptimize.vue";

const router = useRouter();

// 获取配置
const formData = reactive({
  connectorName: "",
  connectorId: "",
  appKey: "",
  appSecret: "",
  description: "",
  syncMode: "manual",
  syncInterval: "30",
  products: [],
  authCode: "",
  accessToken: "",
  refreshToken: "",
  stores: [],
  performance: {}
});

const getConfig = () => {
  // TODO: 从接口获取TikTok Shop配置
  console.log("获取TikTok Shop配置");
};

onMounted(() => {
  getConfig();
});

// Tab 配置
const tabs = [
  { key: "basic", label: "基础信息", component: markRaw(BasicInfo) },
  { key: "order", label: "订单同步", component: markRaw(OrderSync) },
  { key: "product", label: "商品管理", component: markRaw(ProductManage) },
  { key: "identity", label: "用户身份", component: markRaw(UserIdentity) },
  { key: "store", label: "多店铺管理", component: markRaw(MultiStoreManage) },
  { key: "performance", label: "性能优化", component: markRaw(PerformanceOptimize) }
];

const activeTab = ref("basic");

// 当前显示的组件
const currentComponent = computed(() => {
  const currentTab = tabs.find(tab => tab.key === activeTab.value);
  return currentTab?.component || markRaw(BasicInfo);
});

// 切换 Tab
const handleTabChange = (key: string) => {
  activeTab.value = key;
};
</script>

<style scoped lang="scss">
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
