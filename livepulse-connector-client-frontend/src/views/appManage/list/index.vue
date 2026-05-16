<script setup lang="ts">
import { onMounted } from "vue";
import { useColumns } from "./columns";
import { getUrl } from "@/utils/format";

import ArrowRightSLine from "~icons/ri/arrow-right-s-line";

defineOptions({ name: "AppManageList" });

const {
  loading,
  searchKeyword,
  statusFilter,
  typeFilter,
  sortType,
  currentPage,
  pageSize,
  total,
  pagedData,
  columns,
  handleSearch,
  handleDetail,
  handleRemoveApp,
  changeStatus,
  toMarket,
  onSizeChange,
  onCurrentChange
} = useColumns();

onMounted(() => {
  handleSearch();
});
</script>

<template>
  <div class="app-manage-list">
    <!-- 页头 -->
    <div class="list-header">
      <div class="header-left">
        <h2 class="page-title">已安装连接器</h2>
        <p class="page-subtitle">
          管理、配置及新增用于同步外部系统的连接器插件。
        </p>
      </div>
      <el-button type="primary" class="publish-btn" @click="toMarket">
        + 从市场添加
      </el-button>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索应用名称或 ID..."
        class="search-input"
        clearable
        @input="handleSearch"
      />

      <!-- <el-select
        v-model="statusFilter"
        placeholder="状态：全部"
        class="filter-select"
        clearable
        @change="handleSearch"
      >
        <el-option label="状态：全部" value="" />
        <el-option label="已上架" :value="1" />
        <el-option label="未上架" :value="0" />
        <el-option label="审核中" :value="3" />
        <el-option label="驳回" :value="5" />
        <el-option label="已下架" :value="2" />
      </el-select> -->

      <!-- <el-select
        v-model="typeFilter"
        placeholder="类型：全部"
        class="filter-select"
        clearable
        @change="handleSearch"
      >
        <el-option label="类型：全部" value="" />
        <el-option label="插件" value="插件" />
        <el-option label="主题" value="主题" />
      </el-select> -->
      <el-select
        v-model="sortType"
        placeholder="排序"
        class="sort-select"
        clearable
        @change="handleSearch"
      >
        <el-option label="按更新时间升序" :value="1" />
        <el-option label="按更新时间倒序" :value="2" />
        <el-option label="按创建时间升序" :value="3" />
        <el-option label="按创建时间倒序" :value="4" />
        <!-- <el-option :value="2">按更新时间倒序</el-option>
        <el-option :value="3">按创建时间升序</el-option>
        <el-option :value="4">按创建时间倒序</el-option> -->
      </el-select>
    </div>

    <!-- 表格 -->
    <div class="table-wrapper mb-5">
      <div
        v-for="(app, index) in pagedData"
        :key="index"
        class="flex items-center gap-4 rounded-lg border bg-white p-4 transition-shadow hover:shadow-md"
      >
        <div
          class="flex-c size-12 rounded-lg bg-linear-to-br from-purple-50 to-blue-50 text-2xl"
        >
          <img v-if="app.icon" :src="getUrl(app.icon)" class="icon-img" />
          <span v-else class="icon-fallback">{{
            app.connectorName?.[0] || "A"
          }}</span>
        </div>
        <div class="flex-1">
          <div class="flex items-center gap-2">
            <h3 class="font-medium">{{ app.connectorName }}</h3>
            <span class="text-xs text-gray-500">{{ app.version }}</span>
            <span class="rounded bg-red-100 px-2 py-0.5 text-xs text-red-600"
              >推荐</span
            >
          </div>
          <p class="mt-1 text-sm text-gray-600 break-all line-clamp-3">
            {{ app.sketchOut || app.remark }}
          </p>
        </div>
        <span class="text-sm text-gray-600">{{ app.groupName || "插件" }}</span>
        <el-switch
          :model-value="app.dockerStatus === 'running'"
          :loading="app.loading"
          :disabled="app.loading"
          inline-prompt
          active-text="已启用"
          inactive-text="已停止"
          @change="changeStatus(app)"
        />
        <el-button link type="danger" @click="handleRemoveApp(app)"
          >卸载</el-button
        >
        <span
          class="text-gray-400 transition-colors hover:text-gray-600 cursor-pointer"
          @click="handleDetail(app)"
        >
          <IconifyIconOffline :icon="ArrowRightSLine" />
        </span>
      </div>
    </div>

    <!-- 底部分页 -->
    <div class="table-footer">
      <span class="total-text">共 {{ total }} 项数</span>
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        layout="sizes, prev, pager, next"
        :total="total"
        :pager-count="5"
        background
        @size-change="onSizeChange"
        @current-change="onCurrentChange"
      />
    </div>
  </div>
</template>

<style lang="scss" scoped>
.app-manage-list {
  min-height: 100%;
}

.list-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 24px;

  .header-left {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .page-title {
    margin: 0;
    font-size: 22px;
    font-weight: 600;
    color: var(--el-text-color-primary);
    line-height: 1.3;
  }

  .page-subtitle {
    margin: 0;
    font-size: 13px;
    color: var(--el-text-color-secondary);
  }

  .publish-btn {
    padding: 10px 20px;
    font-size: 14px;
    border-radius: 8px;
    height: 40px;
  }
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: var(--el-bg-color);
  border-radius: 10px;
  margin-bottom: 16px;
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

  .filter-select,
  .sort-select {
    width: 140px;

    :deep(.el-select__wrapper) {
      border-radius: 6px;
      box-shadow: none;
      border: 1px solid var(--el-border-color);
    }
  }
  .sort-select {
    width: 200px;
  }

  .sort-btn {
    border-radius: 6px;
    border: 1px solid var(--el-border-color);
    background: var(--el-fill-color-blank);
    color: var(--el-text-color-regular);
    white-space: nowrap;
  }
}

.table-wrapper {
  display: flex;
  flex-direction: column;
  gap: 10px;
  :deep(.op-btn) {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 28px;
    height: 28px;
    border-radius: 6px;
    cursor: pointer;
    transition: background 0.2s;

    &:hover {
      background: var(--el-fill-color);
    }
  }
}

.table-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-top: 1px solid var(--el-border-color-lighter);
  margin-top: 20px;

  .total-text {
    font-size: 13px;
    color: var(--el-text-color-secondary);
  }

  :deep(.el-pagination) {
    .el-pagination__sizes {
      margin-right: 8px;
    }

    .btn-prev,
    .btn-next,
    .el-pager li {
      border-radius: 6px;
    }
  }
}
</style>
