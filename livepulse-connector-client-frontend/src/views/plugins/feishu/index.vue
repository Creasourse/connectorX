<template>
  <div class="feishu-plugin-container">
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
        v-bind="activeTab !== 'basic' ? { config: formData } : {}"
        @update="handleConfigUpdate"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, defineAsyncComponent } from "vue";
import { getFeishuAppPageList } from "@/api/feishu";

// 使用 defineAsyncComponent 实现组件懒加载
const BasicInfoList = defineAsyncComponent(() =>
  import("./components/BasicInfoList.vue")
);

const BasicInfo = defineAsyncComponent(() =>
  import("./components/BasicInfo.vue")
);

const BitableManager = defineAsyncComponent(() =>
  import("./components/BitableManager.vue")
);

const ConnectionConfig = defineAsyncComponent(() =>
  import("./components/ConnectionConfig.vue")
);

const DataSync = defineAsyncComponent(() =>
  import("./components/DataSync.vue")
);

const SyncMonitor = defineAsyncComponent(() =>
  import("./components/SyncMonitor.vue")
);

defineOptions({
  name: "FeishuPlugin"
});

// 获取配置
const formData = reactive({
  feishuAppId: null as number | null,
  appName: "",
  appId: "",
  appSecret: "",
  companyName: "",
  appToken: "",
  description: "",
  isEnabled: 1,
  // 连接配置数据
  connectionConfig: {
    maxConnections: 10,
    retryStrategy: "exponential" as const,
    maxRetries: 3,
    dataFilter: "",
    dataRange: "all" as const,
    excludedFields: ["创建人ID", "最后修改人"]
  }
});

const loadConfig = async () => {
  try {
    const params = {
      pageNum: 1,
      pageSize: 10,
      isEnabled: 1
    };
    const res = await getFeishuAppPageList(params);
    if (res.success && res.data?.records?.length > 0) {
      const config = res.data.records[0];
      Object.assign(formData, {
        feishuAppId: config.id || null,
        appName: config.appName || "",
        appId: config.appId || "",
        appSecret: config.appSecret || "",
        companyName: config.companyName || "",
        appToken: config.appToken || "",
        description: config.description || "",
        isEnabled: config.isEnabled ?? 1
      });
    }
  } catch (err) {
    console.error("获取飞书配置失败:", err);
  }
};

const handleConfigUpdate = () => {
  loadConfig();
};

onMounted(() => {
  loadConfig();
});

// Tab 配置
const tabs = [
  { key: "basic", label: "基础信息" },
  { key: "bitable", label: "多维表格" },
  { key: "connection", label: "连接配置" },
  { key: "dataSync", label: "数据同步" },
  { key: "monitor", label: "同步监控" }
];

const activeTab = ref("basic");

// 当前显示的组件
const currentComponent = computed(() => {
  const componentMap = {
    basic: BasicInfoList,
    bitable: BitableManager,
    connection: ConnectionConfig,
    dataSync: DataSync,
    monitor: SyncMonitor
  };
  return componentMap[activeTab.value as keyof typeof componentMap] || BasicInfoList;
});

// 切换 Tab
const handleTabChange = (key: string) => {
  activeTab.value = key;
};
</script>

<style lang="scss" scoped>
.feishu-plugin-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.tab-navigation {
  display: flex;
  gap: 24px;
  padding: 0 24px;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
  margin-bottom: 0;

  .tab-item {
    position: relative;
    padding: 16px 4px;
    font-size: 14px;
    color: #6b7280;
    cursor: pointer;
    transition: all 0.3s;
    user-select: none;
    font-weight: 500;

    &:hover {
      color: #6c5ce7;
    }

    &.active {
      color: #6c5ce7;
      font-weight: 600;

      &::after {
        content: "";
        position: absolute;
        bottom: -1px;
        left: 0;
        right: 0;
        height: 2px;
        background: #6c5ce7;
        border-radius: 2px 2px 0 0;
      }
    }
  }
}

.content-area {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}
</style>
