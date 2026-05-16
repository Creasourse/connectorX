<template>
  <div class="api-sync-page">
    <div class="config-card">
      <h2 class="config-title">API同步配置</h2>

      <!-- 同步策略配置 -->
      <div class="sync-strategy">
        <div class="config-section-title">同步策略</div>
        <el-form :model="strategyForm" label-width="140px" class="strategy-form">
          <el-form-item label="同步方式">
            <el-radio-group v-model="strategyForm.syncMode">
              <el-radio label="manual">手动同步</el-radio>
              <el-radio label="auto">自动同步</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="同步频率" v-if="strategyForm.syncMode === 'auto'">
            <el-select
              v-model="strategyForm.frequency"
              placeholder="请选择同步频率"
              style="width: 200px"
            >
              <el-option label="每30分钟" value="30min" />
              <el-option label="每小时" value="hourly" />
              <el-option label="每2小时" value="2hours" />
              <el-option label="每6小时" value="6hours" />
              <el-option label="每天" value="daily" />
            </el-select>
          </el-form-item>

          <el-form-item label="同步时间" v-if="strategyForm.syncMode === 'auto'">
            <el-time-picker
              v-model="strategyForm.syncTime"
              format="HH:mm"
              value-format="HH:mm"
              placeholder="选择同步时间"
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- API资源列表 -->
      <div class="api-resources">
        <div class="resource-header">
          <div class="config-section-title">API资源列表</div>
          <el-button type="primary" @click="handleAddResource">
            添加资源
          </el-button>
        </div>

        <el-table :data="resourceList" class="resource-table" border>
          <el-table-column type="index" label="编号" width="60" align="center" />
          <el-table-column prop="resourceName" label="资源名称" min-width="150" />
          <el-table-column prop="apiEndpoint" label="API端点" min-width="220" />
          <el-table-column prop="syncFields" label="同步字段" min-width="200">
            <template #default="{ row }">
              <el-tag
                v-for="(field, index) in row.syncFields"
                :key="index"
                size="small"
                style="margin-right: 4px; margin-bottom: 4px"
              >
                {{ field }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="lastSyncTime" label="最后同步时间" width="180" />
          <el-table-column label="同步状态" width="100" align="center">
            <template #default="{ row }">
              <el-switch
                v-model="row.enabled"
                @change="handleStatusChange(row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" align="center">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleSync(row)">
                立即同步
              </el-button>
              <el-button type="danger" link @click="handleDelete(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 统计信息 -->
        <div class="resource-summary">
          <span class="summary-text">
            共配置 <strong>{{ resourceList.length }}</strong> 个API资源，
            已启用 <strong>{{ enabledCount }}</strong> 个
          </span>
        </div>
      </div>

      <!-- 同步日志说明 -->
      <div class="sync-log-info">
        <div class="config-section-title">同步日志说明</div>
        <ul class="log-list">
          <li>产品同步：将Shopee的商品信息同步到系统，包括标题、价格、库存、图片等</li>
          <li>订单同步：同步订单数据，包括订单状态、商品信息、买家信息、物流信息等</li>
          <li>店铺同步：获取店铺基本信息、设置、物流渠道等配置数据</li>
          <li>自动同步模式下，系统将按照设定的频率和时间自动执行同步任务</li>
          <li>注意Shopee API调用频率限制，避免频繁调用导致限流</li>
        </ul>
      </div>
    </div>

    <!-- 底部操作按钮 -->
    <div class="action-buttons">
      <el-button @click="handleViewLog">查看日志</el-button>
      <el-button type="primary" @click="handleSaveConfig">保存配置</el-button>
    </div>

    <!-- 添加资源对话框 -->
    <el-dialog v-model="addDialogVisible" title="添加API资源" width="600px">
      <el-form :model="addForm" label-width="120px">
        <el-form-item label="资源名称">
          <el-select v-model="addForm.resourceName" placeholder="请选择资源类型">
            <el-option label="商品/产品" value="Product" />
            <el-option label="订单" value="Order" />
            <el-option label="店铺" value="Shop" />
            <el-option label="物流" value="Logistics" />
            <el-option label="退货" value="Return" />
            <el-option label="促销" value="Promotion" />
          </el-select>
        </el-form-item>
        <el-form-item label="API端点">
          <el-input
            v-model="addForm.apiEndpoint"
            placeholder="/api/v2/product/get_item_list"
          />
        </el-form-item>
        <el-form-item label="同步字段">
          <el-checkbox-group v-model="addForm.syncFields">
            <el-checkbox label="item_id">商品ID</el-checkbox>
            <el-checkbox label="title">标题</el-checkbox>
            <el-checkbox label="price">价格</el-checkbox>
            <el-checkbox label="stock">库存</el-checkbox>
            <el-checkbox label="status">状态</el-checkbox>
            <el-checkbox label="create_time">创建时间</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmAdd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";

// 同步策略表单
const strategyForm = ref({
  syncMode: "manual",
  frequency: "hourly",
  syncTime: ""
});

// API资源列表
const resourceList = ref([
  {
    id: 1,
    resourceName: "Product",
    apiEndpoint: "/api/v2/product/get_item_list",
    syncFields: ["item_id", "title", "price", "stock", "status"],
    lastSyncTime: "2024-01-15 10:30:00",
    enabled: true
  },
  {
    id: 2,
    resourceName: "Order",
    apiEndpoint: "/api/v2/order/get_order_list",
    syncFields: ["order_sn", "status", "total_amount", "create_time"],
    lastSyncTime: "2024-01-15 11:00:00",
    enabled: true
  },
  {
    id: 3,
    resourceName: "Shop",
    apiEndpoint: "/api/v2/shop/get_shop_info",
    syncFields: ["shop_id", "shop_name", "region", "status"],
    lastSyncTime: "2024-01-15 09:00:00",
    enabled: false
  }
]);

// 已启用数量
const enabledCount = computed(() => {
  return resourceList.value.filter(item => item.enabled).length;
});

// 添加资源对话框
const addDialogVisible = ref(false);
const addForm = ref({
  resourceName: "",
  apiEndpoint: "",
  syncFields: []
});

// 添加资源
const handleAddResource = () => {
  addForm.value = {
    resourceName: "",
    apiEndpoint: "",
    syncFields: []
  };
  addDialogVisible.value = true;
};

// 确认添加
const handleConfirmAdd = () => {
  if (!addForm.value.resourceName || !addForm.value.apiEndpoint) {
    ElMessage.warning("请完整填写资源信息");
    return;
  }

  const endpointMap: any = {
    Product: "/api/v2/product/get_item_list",
    Order: "/api/v2/order/get_order_list",
    Shop: "/api/v2/shop/get_shop_info",
    Logistics: "/api/v2/logistics/get_channel_list",
    Return: "/api/v2/return/get_return_list",
    Promotion: "/api/v2/promotion/get_promotion_info"
  };

  resourceList.value.push({
    id: Date.now(),
    resourceName: addForm.value.resourceName,
    apiEndpoint: addForm.value.apiEndpoint || endpointMap[addForm.value.resourceName],
    syncFields: addForm.value.syncFields,
    lastSyncTime: "-",
    enabled: true
  });

  addDialogVisible.value = false;
  ElMessage.success("API资源添加成功");
};

// 状态变化
const handleStatusChange = (row: any) => {
  const status = row.enabled ? "已启用" : "已禁用";
  ElMessage.success(`${row.resourceName} ${status}`);
};

// 立即同步
const handleSync = (row: any) => {
  ElMessageBox.confirm(`确定要同步${row.resourceName}数据吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    // TODO: 调用同步接口
    ElMessage.success(`${row.resourceName}同步任务已启动`);
  });
};

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除${row.resourceName}吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    const index = resourceList.value.findIndex(item => item.id === row.id);
    if (index > -1) {
      resourceList.value.splice(index, 1);
    }
    ElMessage.success("删除成功");
  });
};

// 查看日志
const handleViewLog = () => {
  // TODO: 跳转到同步日志页面或打开日志对话框
  ElMessage.info("跳转到同步日志页面");
};

// 保存配置
const handleSaveConfig = () => {
  // TODO: 调用保存配置接口
  ElMessage.success("API同步配置已保存");
};
</script>

<style scoped lang="scss">
.api-sync-page {
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
      border-left: 3px solid #ee4d2d;
    }

    .sync-strategy {
      margin-bottom: 32px;

      .strategy-form {
        :deep(.el-form-item__label) {
          font-size: 14px;
          color: #333;
          font-weight: 500;
        }

        :deep(.el-radio__label) {
          font-size: 14px;
          color: #666;
        }
      }
    }

    .api-resources {
      margin-bottom: 32px;

      .resource-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        .config-section-title {
          margin-bottom: 0;
        }
      }

      .resource-table {
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

      .resource-summary {
        text-align: right;
        padding: 12px;
        background-color: #f5f5f5;
        border-radius: 4px;

        .summary-text {
          font-size: 13px;
          color: #666;

          strong {
            color: #ee4d2d;
            font-weight: 600;
          }
        }
      }
    }

    .sync-log-info {
      padding: 16px;
      background-color: #fff7e6;
      border: 1px solid #ffd591;
      border-radius: 4px;

      .config-section-title {
        border-left-color: #fa8c16;
        margin-bottom: 12px;
      }

      .log-list {
        margin: 0;
        padding-left: 24px;

        li {
          font-size: 13px;
          color: #666;
          line-height: 1.8;
          margin-bottom: 4px;

          &:last-child {
            margin-bottom: 0;
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
</style>
