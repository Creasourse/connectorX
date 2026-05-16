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
import IdTypeConfig from "./components/IdTypeConfig.vue";
import AudiencePackageManage from "./components/AudiencePackageManage.vue";
import PerformanceMonitor from "./components/PerformanceMonitor.vue";

const router = useRouter();

const formData = reactive({
  connectorName: "",
  connectorId: "",
  account: "",
  appId: "",
  appKey: "",
  secretKey: "",
  description: "",
  idTypes: [],
  preprocessing: {},
  fileFormat: {}
});

const getConfig = () => {
  console.log("获取百度营销配置");
};

onMounted(() => {
  getConfig();
});

const tabs = [
  { key: "basic", label: "基础信息", component: markRaw(BasicInfo) },
  { key: "idType", label: "ID类型配置", component: markRaw(IdTypeConfig) },
  { key: "audience", label: "人群包管理", component: markRaw(AudiencePackageManage) },
  { key: "performance", label: "性能监控", component: markRaw(PerformanceMonitor) }
];

const activeTab = ref("basic");

const currentComponent = computed(() => {
  const currentTab = tabs.find(tab => tab.key === activeTab.value);
  return currentTab?.component || markRaw(BasicInfo);
});

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
