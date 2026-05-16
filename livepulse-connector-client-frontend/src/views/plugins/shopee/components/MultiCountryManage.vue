<template>
  <div class="multi-country-manage-page">
    <div class="config-card">
      <div class="page-header">
        <h2 class="config-title">多国家/地区管理</h2>
        <el-button type="primary" @click="handleAddCountry">
          添加国家
        </el-button>
      </div>

      <!-- 国家列表表格 -->
      <el-table :data="countryList" class="country-table" border>
        <el-table-column type="index" label="编号" width="60" align="center" />
        <el-table-column prop="countryName" label="国家/地区" min-width="150" />
        <el-table-column prop="regionCode" label="区域代码" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ row.regionCode }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="currency" label="货币" width="100" align="center">
          <template #default="{ row }">
            <span class="currency-symbol">{{ row.currencySymbol }}</span>
            {{ row.currency }}
          </template>
        </el-table-column>
        <el-table-column prop="shopDomain" label="店铺域名" min-width="200" show-overflow-tooltip />
        <el-table-column prop="partnerId" label="Partner ID" min-width="180" show-overflow-tooltip />
        <el-table-column prop="shopId" label="Shop ID" min-width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="连接状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag
              v-if="row.status === 'connected'"
              type="success"
              size="small"
            >
              已连接
            </el-tag>
            <el-tag
              v-else-if="row.status === 'disconnected'"
              type="danger"
              size="small"
            >
              已断开
            </el-tag>
            <el-tag v-else type="warning" size="small">
              连接中
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastSyncTime" label="最后同步时间" width="180" />
        <el-table-column label="默认" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isDefault" type="primary" size="small">
              默认
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleTestConnect(row)">
              测试连接
            </el-button>
            <el-button type="primary" link @click="handleSync(row)">
              同步数据
            </el-button>
            <el-button
              v-if="!row.isDefault"
              type="primary"
              link
              @click="handleSetDefault(row)"
            >
              设为默认
            </el-button>
            <el-button type="primary" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 统计信息 -->
      <div class="country-summary">
        <div class="summary-item">
          <span class="label">总国家数：</span>
          <span class="value">{{ countryList.length }}</span>
        </div>
        <div class="summary-item">
          <span class="label">已连接：</span>
          <span class="value success">{{ connectedCount }}</span>
        </div>
        <div class="summary-item">
          <span class="label">已断开：</span>
          <span class="value error">{{ disconnectedCount }}</span>
        </div>
        <div class="summary-item">
          <span class="label">默认国家：</span>
          <span class="value primary">{{ defaultCountryName }}</span>
        </div>
      </div>
    </div>

    <!-- 添加/编辑国家对话框 -->
    <el-dialog
      v-model="countryDialogVisible"
      :title="isEdit ? '编辑国家配置' : '添加国家配置'"
      width="700px"
    >
      <el-form :model="countryForm" label-width="140px">
        <el-form-item label="国家/地区" required>
          <el-select
            v-model="countryForm.regionCode"
            placeholder="请选择国家/地区"
            style="width: 100%"
            :disabled="isEdit"
          >
            <el-option label="新加坡" value="SG" />
            <el-option label="马来西亚" value="MY" />
            <el-option label="台湾" value="TW" />
            <el-option label="泰国" value="TH" />
            <el-option label="菲律宾" value="PH" />
            <el-option label="越南" value="VN" />
            <el-option label="印度尼西亚" value="ID" />
            <el-option label="巴西" value="BR" />
          </el-select>
        </el-form-item>
        <el-form-item label="店铺域名" required>
          <el-input
            v-model="countryForm.shopDomain"
            placeholder="seller.shopee.sg"
          >
            <template #prepend>https://</template>
          </el-input>
        </el-form-item>
        <el-form-item label="Partner ID" required>
          <el-input
            v-model="countryForm.partnerId"
            placeholder="请输入Partner ID"
          />
        </el-form-item>
        <el-form-item label="Partner Key" required>
          <el-input
            v-model="countryForm.partnerKey"
            type="password"
            placeholder="请输入Partner Key"
            show-password
          />
        </el-form-item>
        <el-form-item label="Shop ID">
          <el-input
            v-model="countryForm.shopId"
            placeholder="请输入Shop ID（授权后自动获取）"
          />
        </el-form-item>
        <el-form-item label="Access Token">
          <el-input
            v-model="countryForm.accessToken"
            type="password"
            placeholder="授权后自动生成"
            show-password
          />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="countryForm.isDefault" />
          <span class="form-tip">设为默认后，该国家将作为主要运营地区</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="countryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmSave">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 同步对话框 -->
    <el-dialog v-model="syncDialogVisible" title="同步数据" width="500px">
      <el-form :model="syncForm" label-width="120px">
        <el-form-item label="同步类型">
          <el-checkbox-group v-model="syncForm.syncTypes">
            <el-checkbox label="product">商品信息</el-checkbox>
            <el-checkbox label="order">订单信息</el-checkbox>
            <el-checkbox label="customer">客户信息</el-checkbox>
            <el-checkbox label="logistics">物流信息</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="同步方式">
          <el-radio-group v-model="syncForm.syncMode">
            <el-radio label="increment">增量同步</el-radio>
            <el-radio label="full">全量同步</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="syncDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmSync">
          开始同步
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";

// 国家列表数据
const countryList = ref([
  {
    id: 1,
    countryName: "新加坡",
    regionCode: "SG",
    currency: "SGD",
    currencySymbol: "S$",
    shopDomain: "seller.shopee.sg",
    partnerId: "shp_sg_xxxxx1",
    shopId: "123456789",
    status: "connected",
    lastSyncTime: "2024-01-15 10:30:00",
    isDefault: true
  },
  {
    id: 2,
    countryName: "马来西亚",
    regionCode: "MY",
    currency: "MYR",
    currencySymbol: "RM",
    shopDomain: "seller.shopee.com.my",
    partnerId: "shp_my_xxxxx2",
    shopId: "234567890",
    status: "connected",
    lastSyncTime: "2024-01-15 11:45:00",
    isDefault: false
  },
  {
    id: 3,
    countryName: "台湾",
    regionCode: "TW",
    currency: "TWD",
    currencySymbol: "NT$",
    shopDomain: "seller.shopee.tw",
    partnerId: "shp_tw_xxxxx3",
    shopId: "345678901",
    status: "disconnected",
    lastSyncTime: "2024-01-14 16:20:00",
    isDefault: false
  },
  {
    id: 4,
    countryName: "泰国",
    regionCode: "TH",
    currency: "THB",
    currencySymbol: "฿",
    shopDomain: "seller.shopee.co.th",
    partnerId: "shp_th_xxxxx4",
    shopId: "456789012",
    status: "connected",
    lastSyncTime: "2024-01-15 09:15:00",
    isDefault: false
  },
  {
    id: 5,
    countryName: "菲律宾",
    regionCode: "PH",
    currency: "PHP",
    currencySymbol: "₱",
    shopDomain: "seller.shopee.com.ph",
    partnerId: "shp_ph_xxxxx5",
    shopId: "567890123",
    status: "pending",
    lastSyncTime: "-",
    isDefault: false
  },
  {
    id: 6,
    countryName: "越南",
    regionCode: "VN",
    currency: "VND",
    currencySymbol: "₫",
    shopDomain: "seller.shopee.vn",
    partnerId: "shp_vn_xxxxx6",
    shopId: "678901234",
    status: "connected",
    lastSyncTime: "2024-01-15 12:00:00",
    isDefault: false
  }
]);

// 统计数据
const connectedCount = computed(() => {
  return countryList.value.filter(country => country.status === "connected").length;
});

const disconnectedCount = computed(() => {
  return countryList.value.filter(country => country.status === "disconnected").length;
});

const defaultCountryName = computed(() => {
  const defaultCountry = countryList.value.find(country => country.isDefault);
  return defaultCountry ? defaultCountry.countryName : "未设置";
});

// 国家对话框
const countryDialogVisible = ref(false);
const isEdit = ref(false);
const countryForm = ref({
  id: null,
  regionCode: "",
  shopDomain: "",
  partnerId: "",
  partnerKey: "",
  shopId: "",
  accessToken: "",
  isDefault: false
});

// 同步对话框
const syncDialogVisible = ref(false);
const syncForm = ref({
  countryId: null,
  syncTypes: ["product", "order"],
  syncMode: "increment"
});

// 添加国家
const handleAddCountry = () => {
  isEdit.value = false;
  countryForm.value = {
    id: null,
    regionCode: "",
    shopDomain: "",
    partnerId: "",
    partnerKey: "",
    shopId: "",
    accessToken: "",
    isDefault: false
  };
  countryDialogVisible.value = true;
};

// 编辑国家
const handleEdit = (row: any) => {
  isEdit.value = true;
  countryForm.value = {
    id: row.id,
    regionCode: row.regionCode,
    shopDomain: row.shopDomain.replace("https://", ""),
    partnerId: row.partnerId,
    partnerKey: "",
    shopId: row.shopId,
    accessToken: "",
    isDefault: row.isDefault
  };
  countryDialogVisible.value = true;
};

// 确认保存
const handleConfirmSave = () => {
  if (!countryForm.value.regionCode || !countryForm.value.shopDomain || !countryForm.value.partnerId) {
    ElMessage.warning("请完整填写必填信息");
    return;
  }

  const countryInfo = getCountryInfo(countryForm.value.regionCode);

  if (isEdit.value) {
    // 编辑模式
    const index = countryList.value.findIndex(c => c.id === countryForm.value.id);
    if (index > -1) {
      countryList.value[index] = {
        ...countryList.value[index],
        regionCode: countryForm.value.regionCode,
        countryName: countryInfo.countryName,
        currency: countryInfo.currency,
        currencySymbol: countryInfo.currencySymbol,
        shopDomain: `https://${countryForm.value.shopDomain}`,
        partnerId: countryForm.value.partnerId,
        shopId: countryForm.value.shopId,
        isDefault: countryForm.value.isDefault
      };
    }
    ElMessage.success("国家配置已更新");
  } else {
    // 添加模式
    if (countryForm.value.isDefault) {
      // 如果设为默认，取消其他默认
      countryList.value.forEach(c => c.isDefault = false);
    }
    countryList.value.push({
      id: Date.now(),
      countryName: countryInfo.countryName,
      regionCode: countryForm.value.regionCode,
      currency: countryInfo.currency,
      currencySymbol: countryInfo.currencySymbol,
      shopDomain: `https://${countryForm.value.shopDomain}`,
      partnerId: countryForm.value.partnerId,
      shopId: countryForm.value.shopId,
      status: "pending",
      lastSyncTime: "-",
      isDefault: countryForm.value.isDefault
    });
    ElMessage.success("国家添加成功");
  }

  countryDialogVisible.value = false;
};

// 测试连接
const handleTestConnect = (row: any) => {
  ElMessage.info(`正在测试 ${row.countryName} 的连接...`);
  setTimeout(() => {
    row.status = "connected";
    ElMessage.success(`${row.countryName} 连接成功`);
  }, 1000);
};

// 同步数据
const handleSync = (row: any) => {
  syncForm.value = {
    countryId: row.id,
    syncTypes: ["product", "order"],
    syncMode: "increment"
  };
  syncDialogVisible.value = true;
};

// 确认同步
const handleConfirmSync = () => {
  if (syncForm.value.syncTypes.length === 0) {
    ElMessage.warning("请选择至少一种同步类型");
    return;
  }

  syncDialogVisible.value = false;
  // TODO: 调用同步接口
  ElMessage.success("同步任务已启动");
};

// 设为默认
const handleSetDefault = (row: any) => {
  countryList.value.forEach(country => {
    country.isDefault = country.id === row.id;
  });
  ElMessage.success(`已将 ${row.countryName} 设为默认国家`);
};

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    `确定要删除"${row.countryName}"的配置吗？删除后无法恢复。`,
    "警告",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    }
  ).then(() => {
    const index = countryList.value.findIndex(country => country.id === row.id);
    if (index > -1) {
      countryList.value.splice(index, 1);
    }
    ElMessage.success("国家配置删除成功");
  });
};

// 获取国家信息
const getCountryInfo = (regionCode: string) => {
  const infoMap: any = {
    SG: { countryName: "新加坡", currency: "SGD", currencySymbol: "S$" },
    MY: { countryName: "马来西亚", currency: "MYR", currencySymbol: "RM" },
    TW: { countryName: "台湾", currency: "TWD", currencySymbol: "NT$" },
    TH: { countryName: "泰国", currency: "THB", currencySymbol: "฿" },
    PH: { countryName: "菲律宾", currency: "PHP", currencySymbol: "₱" },
    VN: { countryName: "越南", currency: "VND", currencySymbol: "₫" },
    ID: { countryName: "印度尼西亚", currency: "IDR", currencySymbol: "Rp" },
    BR: { countryName: "巴西", currency: "BRL", currencySymbol: "R$" }
  };
  return infoMap[regionCode] || {};
};
</script>

<style scoped lang="scss">
.multi-country-manage-page {
  .config-card {
    background-color: #fff;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;

      .config-title {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: #333;
      }
    }

    .country-table {
      width: 100%;
      margin-bottom: 24px;

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

      .currency-symbol {
        font-weight: 600;
        margin-right: 4px;
      }
    }

    .country-summary {
      display: flex;
      gap: 32px;
      padding: 16px;
      background-color: #fff7e6;
      border-radius: 4px;

      .summary-item {
        display: flex;
        align-items: center;
        gap: 8px;

        .label {
          font-size: 14px;
          color: #666;
        }

        .value {
          font-size: 16px;
          font-weight: 600;
          color: #333;

          &.success {
            color: #52c41a;
          }

          &.error {
            color: #f5222d;
          }

          &.primary {
            color: #ee4d2d;
          }
        }
      }
    }
  }

  .form-tip {
    margin-left: 12px;
    font-size: 12px;
    color: #999;
  }
}
</style>
