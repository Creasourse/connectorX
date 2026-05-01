<script setup lang="ts">
import { ref, reactive, onMounted, computed } from "vue";
import { ElMessageBox } from "element-plus";
import { Search } from "@element-plus/icons-vue";
import { message } from "@/utils/message";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import AddFill from "~icons/ri/add-circle-line";
import AppCard from "./components/AppCard.vue";
import type { ConnectorItem, LabelItem, GroupItem } from "@/api/app";
import { getLabelEnum, getGroupEnum, getConnectorList } from "@/api/app";
import { useUserStoreHook } from "@/store/modules/user";
import { useDebounceFn } from "@vueuse/core";

defineOptions({
  name: "AppList"
});

const svg = `
  <path class="path" d="
    M 30 15
    L 28 17
    M 25.61 25.61
    A 15 15, 0, 0, 1, 15 30
    A 15 15, 0, 1, 1, 27.99 7.5
    L 15 15
  " style="stroke-width: 4px; fill: rgba(0, 0, 0, 0)"/>
`;

const type = computed(() => {
  return useUserStoreHook()?.type;
});
const labelList = ref<LabelItem[]>([]);
const getLabelList = () => {
  getLabelEnum().then(res => {
    if (res.success) {
      labelList.value = res.data || [];
    }
  });
};
const changeLabel = (v?: string | number | undefined) => {
  const obj = labelList.value.find(e => e.connLabelEnumId === v);
  pluginParams.connLabelName = obj?.connLabelName || "";
  handleSearch();
};
const groupList = ref<GroupItem[]>([]);
const getGroupList = () => {
  getGroupEnum().then(res => {
    if (res.success) {
      groupList.value = res.data;
    }
  });
};
const changeGroup = v => {
  pluginParams.groupName = v || "";
  handleSearch();
};
const appList = ref<ConnectorItem[]>([]);
const total = ref(0);
const dataLoading = ref(true);
const pluginParams = reactive({
  pageQueryParam: {
    pageNo: 1,
    pageSize: 12
  },
  connectorName: "",
  connLabelName: "",
  groupName: "",
  version: undefined,
  type: undefined, // 1:免费 2:收费
  staus: 1, // 用户是开发者 状态只能是0和3 当开发者发布状态为3 0:未上架 1:上架 2:下架 3:审核中,待审核 4:历史版本 5:驳回
  sortType: 2 // 排序类型 1:更新时间正序, 2:更新时间倒序, 3:创建时间正序, 4:创建时间正序
});
const description = computed(() =>
  total.value ? `未找到"${pluginParams.connectorName}"相关应用` : "暂无应用"
);
const getAppListFn = () => {
  dataLoading.value = true;
  getConnectorList(pluginParams)
    .then(res => {
      if (res.success) {
        appList.value = res.data.list;
        total.value = res.data.total;
      }
    })
    .finally(() => {
      dataLoading.value = false;
    });
};

const handleSearch = useDebounceFn(getAppListFn, 300);

onMounted(() => {
  getLabelList();
  getGroupList();
  handleSearch();
});

const onPageSizeChange = (size: number) => {
  pluginParams.pageQueryParam.pageSize = size;
  pluginParams.pageQueryParam.pageNo = 1;
};
const onCurrentChange = (current: number) => {
  pluginParams.pageQueryParam.pageNo = current;
};

const handleDeleteItem = (app: ConnectorItem) => {
  ElMessageBox.confirm(
    `确认删除后"${app.connPluginName}"的所有信息将被清空，且无法恢复`,
    "提示",
    { type: "warning" }
  )
    .then(() => {
      appList.value = appList.value.filter(
        item => item.connPluginId !== app.connPluginId
      );
      total.value = appList.value.length;
      message("删除成功", { type: "success" });
    })
    .catch(() => {});
};
</script>

<template>
  <div class="flex max-md:flex-col">
    <aside class="w-50 md:pr-5 max-md:w-full">
      <ul>
        <li class="border-0 border-b border-solid pb-2.5 mb-2.5">
          <div>
            <h2 class="text-base">类型</h2>
            <div>
              <el-radio-group
                v-model="pluginParams.groupName"
                @change="changeGroup"
              >
                <el-radio class="md:w-full" value="">全部</el-radio>
                <el-radio
                  v-for="item in groupList"
                  :key="item.groupId"
                  class="md:w-full"
                  :value="item.groupName"
                  >{{ item.groupName }}</el-radio
                >
              </el-radio-group>
            </div>
          </div>
        </li>
        <li class="border-0 border-b border-solid pb-2.5 mb-2.5">
          <div class="">
            <h2 class="text-base">价格</h2>
            <div>
              <el-radio-group
                v-model="pluginParams.type"
                fill="#6c6cff"
                @change="handleSearch"
              >
                <el-radio class="md:w-full" :value="null">全部</el-radio>
                <el-radio class="md:w-full" :value="1">免费</el-radio>
                <el-radio class="md:w-full" :value="2">收费</el-radio>
              </el-radio-group>
            </div>
          </div>
        </li>
        <li class="max-md:mb-4">
          <div class="">
            <h2 class="text-base">标签</h2>
            <div class="">
              <span
                class="inline-flex cursor-pointer select-none items-center gap-x-1.5 rounded-md px-2 py-1 text-xs font-medium ring-1 ring-inset"
                :class="
                  !pluginParams.connLabelName
                    ? 'bg-blue-600 text-white ring-blue-600'
                    : 'bg-gray-50 text-gray-600 ring-gray-500/10'
                "
                @click="changeLabel()"
                >全部</span
              >
              <span
                v-for="(item, i) in labelList"
                :key="i"
                class="inline-flex cursor-pointer select-none items-center gap-x-1.5 rounded-md px-2 py-1 text-xs font-medium ring-1 ring-inset"
                :class="
                  item.connLabelName === pluginParams.connLabelName
                    ? 'bg-blue-600 text-white ring-blue-600'
                    : 'bg-gray-50 text-gray-600 ring-gray-500/10'
                "
                @click="changeLabel(item.connLabelEnumId)"
                >{{ item.connLabelName }}</span
              >
            </div>
          </div>
        </li>
      </ul>
    </aside>
    <div class="flex-1 overflow-hidden">
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <el-input
          v-model="pluginParams.connectorName"
          placeholder="搜索应用名称"
          class="search-input"
          clearable
          @input="handleSearch"
        />

        <el-select
          v-model="pluginParams.sortType"
          placeholder="排序"
          class="sort-select"
          clearable
          @change="handleSearch"
        >
          <el-option label="按更新时间升序" :value="1" />
          <el-option label="按更新时间倒序" :value="2" />
          <el-option label="按创建时间升序" :value="3" />
          <el-option label="按创建时间倒序" :value="4" />
        </el-select>
      </div>

      <!-- 卡片网格 -->
      <div
        v-loading="dataLoading"
        :element-loading-svg="svg"
        element-loading-svg-view-box="-10, -10, 50, 50"
        class="card-grid grid-cols-3"
      >
        <el-empty v-show="appList.length === 0" :description="description" />
        <template v-if="total > 0">
          <AppCard v-for="(app, index) in appList" :key="index" :app="app" />
        </template>
      </div>
      <el-pagination
        v-model:currentPage="pluginParams.pageQueryParam.pageNo"
        class="float-right mt-20"
        :page-size="pluginParams.pageQueryParam.pageSize"
        :total="total"
        :page-sizes="[12, 24, 36]"
        :background="true"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="onPageSizeChange"
        @current-change="onCurrentChange"
      />
    </div>
  </div>
</template>

<style lang="scss" scoped>
/* ---- 筛选栏 ---- */
.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: var(--el-bg-color);
  border-radius: 10px;
  margin-bottom: 20px;
  border: 1px solid var(--el-border-color-lighter);

  .search-input {
    flex: 1;

    :deep(.el-input__wrapper) {
      border-radius: 6px;
      box-shadow: none;
      border: 1px solid var(--el-border-color);
      background: var(--el-fill-color-blank);
    }
  }

  .filter-select {
    width: 140px;

    :deep(.el-select__wrapper) {
      border-radius: 6px;
      box-shadow: none;
      border: 1px solid var(--el-border-color);
    }
  }

  .sort-select {
    width: 180px;

    :deep(.el-select__wrapper) {
      border-radius: 6px;
      box-shadow: none;
      border: 1px solid var(--el-border-color);
    }
  }
}
/* ---- 卡片网格 ---- */
.card-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  min-height: 200px;

  @media (max-width: 1400px) {
    grid-template-columns: repeat(3, 1fr);
  }

  @media (max-width: 960px) {
    grid-template-columns: repeat(2, 1fr);
  }
}

.grid-empty {
  grid-column: 1 / -1;
}
</style>
