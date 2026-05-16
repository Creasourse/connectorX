<template>
  <div class="event-callback-page">
    <div class="config-card">
      <h2 class="config-title">事件回调记录</h2>

      <!-- 筛选条件 -->
      <div class="filter-section">
        <el-form :model="form" inline class="filter-form">
          <el-form-item label="事件类型">
            <el-select
              v-model="form.syncType"
              placeholder="请选择事件类型"
              clearable
              style="width: 180px"
            >
              <el-option label="全部" value="" />
              <el-option
                v-for="(v, k) in syncTypeMap"
                :key="k"
                :label="v"
                :value="k"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="回调状态">
            <el-select
              v-model="form.syncStatus"
              placeholder="请选择状态"
              clearable
              style="width: 140px"
            >
              <el-option label="全部" value="" />
              <el-option label="成功" value="success" />
              <el-option label="失败" value="failed" />
              <el-option label="处理中" value="processing" />
            </el-select>
          </el-form-item>

          <el-form-item label="时间范围">
            <el-date-picker
              v-model="form.dateRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DD HH:mm"
              style="width: 360px"
              @change="changeTime"
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 回调记录表格 -->
      <el-table
        v-loading="loading"
        :data="callbackList"
        class="callback-table"
        border
      >
        <el-table-column type="index" label="编号" width="60" align="center" />
        <el-table-column prop="syncType" label="同步类型" min-width="120" />
        <el-table-column
          prop="startTime"
          label="开始时间"
          min-width="180"
          show-overflow-tooltip
        />
        <el-table-column
          prop="endTime"
          label="结束时间"
          min-width="180"
          show-overflow-tooltip
        />
        <el-table-column prop="totalCount" label="总记录数" min-width="120" />
        <el-table-column prop="successCount" label="成功数量" min-width="120" />
        <el-table-column prop="failCount" label="失败数量" min-width="120" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag
              v-if="row.syncStatus === 'success'"
              type="success"
              size="small"
            >
              成功
            </el-tag>
            <el-tag
              v-else-if="row.syncStatus === 'failed'"
              type="danger"
              size="small"
            >
              失败
            </el-tag>
            <el-tag v-else type="warning" size="small"> 处理中 </el-tag>
          </template>
        </el-table-column>
        <!-- <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewDetail(row)">
              查看详情
            </el-button>
            <el-button
              v-if="row.syncStatus === 'failed'"
              type="warning"
              link
              @click="handleRetry(row)"
            >
              重试
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column> -->
      </el-table>

      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="form.currentPage"
          v-model:page-size="form.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="回调详情"
      width="800px"
      class="detail-dialog"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="事件类型">
          {{ currentDetail.syncType }}
        </el-descriptions-item>
        <el-descriptions-item label="事件名称">
          {{ currentDetail.eventName }}
        </el-descriptions-item>
        <el-descriptions-item label="回调地址">
          {{ currentDetail.callbackUrl }}
        </el-descriptions-item>
        <el-descriptions-item label="回调时间">
          {{ currentDetail.callbackTime }}
        </el-descriptions-item>
        <el-descriptions-item label="重试次数">
          {{ currentDetail.retryCount }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag
            v-if="currentDetail.syncStatus === 'success'"
            type="success"
            size="small"
          >
            成功
          </el-tag>
          <el-tag
            v-else-if="currentDetail.syncStatus === 'failed'"
            type="danger"
            size="small"
          >
            失败
          </el-tag>
          <el-tag v-else type="warning" size="small"> 处理中 </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="请求内容">
          <pre class="json-content">{{
            formatJson(currentDetail.requestContent)
          }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="响应内容">
          <pre class="json-content">{{
            formatJson(currentDetail.responseContent)
          }}</pre>
        </el-descriptions-item>
        <el-descriptions-item
          v-if="currentDetail.errorMessage"
          label="错误信息"
        >
          <span class="error-message">{{ currentDetail.errorMessage }}</span>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button type="primary" @click="detailDialogVisible = false"
          >关闭</el-button
        >
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from "vue";
import { getEventLogList } from "@/api/wechat";
import { ElMessage, ElMessageBox } from "element-plus";

const props = defineProps({
  config: {
    type: Object
  }
});
const wecomCorpId = computed(() => props.config?.wecomCorpId || "");
const syncTypeMap = {
  DEPARTMENT: "部门",
  EMPLOYEE: "员工",
  EXTERNAL_CONTACT: "外部联系人",
  CUSTOMER_GROUP: "客户群",
  CORP_TAG: "企业标签"
};
const syncStatusMap = {
  SUCCESS: "成功",
  FAILED: "失败",
  PARTIAL: "部分成功"
};
const loading = ref(false);
const form = reactive({
  wecomCorpId: null,
  currentPage: 1,
  pageSize: 10,
  syncType: "", // DEPARTMENT-部门，EMPLOYEE-员工，EXTERNAL_CONTACT-外部联系人，CUSTOMER_GROUP-客户群，CORP_TAG-企业标签
  syncStatus: "", // SUCCESS-成功，FAILED-失败，PARTIAL-部分成功
  startTime: "",
  endTime: "",
  sortType: 1,
  dateRange: []
});
const total = ref(0);

// 回调记录列表
const callbackList = ref<any[]>([]);
const getList = () => {
  loading.value = true;
  getEventLogList({ ...form, wecomCorpId: wecomCorpId.value })
    .then(res => {
      if (res.success) {
        callbackList.value = res.data.list;
        total.value = res.data.total;
      }
    })
    .finally(() => {
      loading.value = false;
    });
};

// 监听 props.config 变化
watch(
  () => props.config,
  (newVal: any) => {
    const wecomCorpId = newVal?.wecomCorpId;
    if (newVal && wecomCorpId) {
      getList();
    }
  },
  { immediate: true, deep: true }
);

// 详情对话框
const detailDialogVisible = ref(false);
const currentDetail = ref<any>({});

// 改变日期
const changeTime = (val: any) => {
  if (val.length > 1 && val[0] && val[1]) {
    form.startTime = val[0];
    form.endTime = val[1];
  } else {
    form.startTime = "";
    form.endTime = "";
  }
  handleSearch();
};

// 查询
const handleSearch = () => {
  form.currentPage = 1;
  getList();
};

// 重置
const handleReset = () => {
  form.syncType = "";
  form.syncStatus = "";
  form.startTime = "";
  form.endTime = "";
  form.dateRange = [];
  form.currentPage = 1;
  ElMessage.info("已重置筛选条件");
  handleSearch();
};

// 查看详情
const handleViewDetail = (row: any) => {
  currentDetail.value = row;
  detailDialogVisible.value = true;
};

// 重试
const handleRetry = (row: any) => {
  ElMessageBox.confirm(`确定要重试该回调记录吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    // TODO: 调用重试接口
    ElMessage.success("重试任务已启动");
  });
};

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除该回调记录吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    // delete
    ElMessage.success("删除成功");
  });
};

// 分页大小变化
const handleSizeChange = (size: number) => {
  form.pageSize = size;
  form.currentPage = 1;
  getList();
};

// 当前页变化
const handleCurrentChange = (page: number) => {
  form.currentPage = page;
  getList();
};

// 格式化JSON
const formatJson = (jsonStr: string) => {
  if (!jsonStr) return "{}";
  try {
    const obj = JSON.parse(jsonStr);
    return JSON.stringify(obj, null, 2);
  } catch {
    return jsonStr;
  }
};
</script>

<style scoped lang="scss">
.event-callback-page {
  .config-card {
    background-color: #fff;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

    .config-title {
      margin: 0 0 24px;
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }

    .filter-section {
      margin-bottom: 24px;
      padding: 16px;
      background-color: #f5f5f5;
      border-radius: 4px;

      .filter-form {
        :deep(.el-form-item__label) {
          font-size: 14px;
          color: #333;
          font-weight: 500;
        }

        :deep(.el-input__wrapper),
        :deep(.el-select .el-input__wrapper) {
          background-color: #fff;
          box-shadow: none;
          border: 1px solid #dcdfe6;

          &.is-focus {
            border-color: #1890ff;
          }
        }
      }
    }

    .callback-table {
      width: 100%;
      margin-bottom: 16px;

      :deep(.el-table__header) {
        th {
          background-color: #fafafa;
          color: #333;
          font-weight: 600;
        }
      }

      :deep(.el-table__body) {
        td {
          color: #666;
        }
      }
    }

    .pagination-section {
      display: flex;
      justify-content: flex-end;
      padding-top: 16px;
      border-top: 1px solid #e8e8e8;
    }
  }

  .detail-dialog {
    :deep(.el-dialog__header) {
      padding: 16px 24px;
      border-bottom: 1px solid #e8e8e8;

      .el-dialog__title {
        font-size: 16px;
        font-weight: 600;
        color: #333;
      }
    }

    :deep(.el-dialog__body) {
      padding: 24px;
    }

    :deep(.el-descriptions__label) {
      width: 120px;
      font-weight: 600;
      background-color: #fafafa !important;
    }

    :deep(.el-descriptions__content) {
      background-color: #fff !important;
    }

    .json-content {
      margin: 0;
      padding: 12px;
      background-color: #f5f5f5;
      border-radius: 4px;
      font-size: 12px;
      color: #333;
      white-space: pre-wrap;
      word-break: break-all;
      max-height: 300px;
      overflow-y: auto;
    }

    .error-message {
      color: #f56c6c;
    }
  }
}
</style>
