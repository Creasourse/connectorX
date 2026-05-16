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
import ObjectSync from "./components/ObjectSync.vue";
import DataCapture from "./components/DataCapture.vue";
import FieldMapping from "./components/FieldMapping.vue";
import MultiOrgManage from "./components/MultiOrgManage.vue";

const router = useRouter();

// 获取配置
const formData = reactive({
  connectorName: "Salesforce系统对接",
  connectorType: "Salesforce",
  description: ""
});

const getConfig = () => {
  // TODO: 从接口获取Salesforce配置
  console.log("获取Salesforce配置");
};

onMounted(() => {
  getConfig();
});

// Tab 配置
const tabs = [
  { key: "basic", label: "基础信息", component: markRaw(BasicInfo) },
  { key: "object", label: "对象同步", component: markRaw(ObjectSync) },
  { key: "cdc", label: "变更数据捕获", component: markRaw(DataCapture) },
  { key: "mapping", label: "字段映射", component: markRaw(FieldMapping) },
  { key: "org", label: "多组织管理", component: markRaw(MultiOrgManage) }
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
