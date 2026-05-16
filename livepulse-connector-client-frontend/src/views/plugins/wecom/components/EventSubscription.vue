<template>
  <div class="event-subscription-page">
    <div class="config-card">
      <h2 class="config-title">事件订阅配置</h2>

      <!-- 回调配置 -->
      <!-- <div class="callback-config">
        <div class="config-section-title">回调URL配置</div>
        <el-form
          :model="callbackForm"
          label-width="140px"
          class="callback-form"
        >
          <el-form-item label="回调URL">
            <el-input
              v-model="callbackForm.url"
              placeholder="请输入接收事件的回调地址"
              clearable
            />
          </el-form-item>
          <el-form-item label="Token">
            <el-input
              v-model="callbackForm.token"
              placeholder="请输入Token用于验证请求来源"
              clearable
            />
          </el-form-item>
          <el-form-item label="EncodingAESKey">
            <el-input
              v-model="callbackForm.encodingAESKey"
              placeholder="请输入EncodingAESKey用于消息加密"
              clearable
            />
          </el-form-item>
        </el-form>
      </div> -->

      <!-- 事件订阅列表 -->
      <div class="event-list">
        <div class="config-section-title">订阅事件列表</div>
        <el-table
          v-loading="loading"
          :data="eventList"
          class="event-table"
          border
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column
            type="index"
            label="编号"
            width="60"
            align="center"
          />
          <el-table-column prop="eventName" label="事件名称" min-width="180" />
          <el-table-column prop="eventCode" label="事件编码" min-width="150" />
          <el-table-column
            prop="eventDescription"
            label="事件说明"
            min-width="250"
          />
          <el-table-column label="订阅状态" width="100" align="center">
            <template #default="{ row }">
              <el-switch
                :model-value="row.enabled === 1"
                @change="handleSubscribeChange(row)"
              />
            </template>
          </el-table-column>
        </el-table>

        <!-- 底部分页 -->
        <div class="table-footer">
          <span class="total-text">共 {{ total }} 项数</span>
          <el-pagination
            v-model:current-page="form.currentPage"
            v-model:page-size="form.pageSize"
            :page-sizes="[10, 20, 50]"
            layout="sizes, prev, pager, next"
            :total="total"
            :pager-count="5"
            background
            @size-change="onSizeChange"
            @current-change="onCurrentChange"
          />
        </div>

        <!-- 统计信息 -->
        <div class="event-summary">
          <span class="summary-text">
            已选择 <strong>{{ selectedEvents.length }}</strong> 个事件， 已订阅
            <strong>{{ subscribedCount }}</strong> 个事件
          </span>
        </div>
      </div>
    </div>

    <!-- 底部操作按钮 -->
    <!-- <div class="action-buttons">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSave">保存配置</el-button>
    </div> -->
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, watch } from "vue";
import { saveOrUpdateConfig, getCallbackPageList } from "@/api/wechat";
import { ElMessage } from "element-plus";

const props = defineProps({
  config: {
    type: Object
  }
});
const wecomCorpId = computed(() => props.config?.wecomCorpId || "");

// 回调配置表单
const callbackForm = ref({
  url: "",
  token: "",
  encodingAESKey: ""
});

const loading = ref(false);
const form = reactive({
  currentPage: 1,
  pageSize: 10,
  tableName: "",
  tableAlias: "",
  sortType: 1
});
const total = ref(0);

// 事件列表数据
const eventList = ref<any[]>([]);
const getList = () => {
  loading.value = true;
  getCallbackPageList({ ...form, wecomCorpId: wecomCorpId.value })
    .then(res => {
      if (res.success) {
        eventList.value = res.data.list;
        total.value = res.data.total;
      }
    })
    .finally(() => {
      loading.value = false;
    });
};
const onSizeChange = (size: number) => {
  form.pageSize = size;
  form.currentPage = 1;
  getList();
};

const onCurrentChange = (page: number) => {
  form.currentPage = page;
  getList();
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

// 已选择的事件
const selectedEvents = ref<any[]>([]);

// 已订阅数量
const subscribedCount = computed(() => {
  return eventList.value.filter(item => item.enabled === 1).length;
});

// 选择变化
const handleSelectionChange = (selection: any[]) => {
  selectedEvents.value = selection;
};

// 订阅状态变化
const handleSubscribeChange = (row: any) => {
  const enabled = row.enabled === 1 ? 0 : 1;
  const params = Object.assign({}, row, { enabled });
  saveOrUpdateConfig(params).then(res => {
    if (res.success) {
      const status = row.enabled === 0 ? "已开启" : "已关闭";
      ElMessage.success(`${row.eventName} ${status}`);
      getList();
    }
  });
};

// 取消
const handleCancel = () => {
  // TODO: 重置表单或返回上一页
  ElMessage.info("已取消修改");
};

// 保存配置
const handleSave = () => {
  if (!callbackForm.value.url) {
    ElMessage.warning("请先配置回调URL");
    return;
  }

  // TODO: 调用保存接口
  ElMessage.success("事件订阅配置已保存");
};
</script>

<style scoped lang="scss">
.event-subscription-page {
  .config-card {
    background-color: #fff;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
    margin-bottom: 16px;

    .config-title {
      margin: 0 0 24px;
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }

    .config-section-title {
      font-size: 14px;
      font-weight: 600;
      color: #333;
      margin-bottom: 16px;
      padding-left: 8px;
      border-left: 3px solid #1890ff;
    }

    .callback-config {
      margin-bottom: 32px;

      .callback-form {
        max-width: 600px;

        :deep(.el-form-item__label) {
          font-size: 14px;
          color: #333;
          font-weight: 500;
        }

        :deep(.el-input__wrapper) {
          background-color: #f5f5f5;
          border: none;
          box-shadow: none;
          padding: 8px 12px;

          &.is-focus {
            background-color: #e6f7ff;
          }
        }

        :deep(.el-input__inner) {
          color: #333;
        }
      }
    }

    .event-list {
      .event-table {
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

      .event-summary {
        text-align: right;
        padding: 12px;
        background-color: #f5f5f5;
        border-radius: 4px;

        .summary-text {
          font-size: 13px;
          color: #666;

          strong {
            color: #1890ff;
            font-weight: 600;
          }
        }
      }
    }
  }

  .action-buttons {
    display: flex;
    justify-content: flex-end;
    gap: 12px;

    button {
      min-width: 100px;
    }
  }
}
.table-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 20px 0;

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
