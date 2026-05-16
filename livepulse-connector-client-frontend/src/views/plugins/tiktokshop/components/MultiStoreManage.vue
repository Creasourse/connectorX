<template>
  <div class="multi-store-manage">
    <div class="header">
      <h2 class="title">多店铺管理</h2>
      <el-button type="primary" @click="handleAddStore">
        <el-icon><Plus /></el-icon>
        添加店铺
      </el-button>
    </div>

    <!-- 店铺列表 -->
    <div class="store-list">
      <el-empty v-if="storeList.length === 0" description="暂无店铺" />

      <el-table :data="storeList" style="width: 100%">
        <el-table-column prop="name" label="店铺名称" min-width="200">
          <template #default="{ row }">
            <div class="store-name">
              <el-icon class="icon"><Shop /></el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="storeId" label="店铺ID" min-width="150">
          <template #default="{ row }">
            <span class="store-id">{{ row.storeId }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="country" label="国家/地区" min-width="120">
          <template #default="{ row }">
            <div class="country-info">
              <span class="flag">{{ row.flag }}</span>
              <span>{{ row.country }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '已连接' ? 'success' : 'info'" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="添加时间" min-width="180">
          <template #default="{ row }">
            <span class="create-time">{{ row.createdAt }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleViewDetail(row)">
              查看详情
            </el-button>
            <el-button type="primary" size="small" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" link @click="handleDisconnect(row)">
              断开
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 添加/编辑店铺对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑店铺' : '添加店铺'"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-position="top"
      >
        <el-form-item label="店铺名称" prop="name">
          <el-input
            v-model="formData.name"
            placeholder="请输入店铺名称，便于识别"
            clearable
          />
        </el-form-item>

        <el-form-item label="国家/地区" prop="country">
          <el-select
            v-model="formData.country"
            placeholder="请选择店铺所在国家/地区"
            style="width: 100%"
            @change="handleCountryChange"
          >
            <el-option label="美国" value="美国" />
            <el-option label="英国" value="英国" />
            <el-option label="加拿大" value="加拿大" />
            <el-option label="澳大利亚" value="澳大利亚" />
            <el-option label="新加坡" value="新加坡" />
            <el-option label="马来西亚" value="马来西亚" />
            <el-option label="泰国" value="泰国" />
            <el-option label="菲律宾" value="菲律宾" />
            <el-option label="越南" value="越南" />
            <el-option label="印度尼西亚" value="印度尼西亚" />
          </el-select>
        </el-form-item>

        <el-form-item label="授权方式" prop="authMethod">
          <el-radio-group v-model="formData.authMethod">
            <el-radio label="oauth">OAuth 授权</el-radio>
            <el-radio label="token">Access Token</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- OAuth 授权 -->
        <template v-if="formData.authMethod === 'oauth'">
          <el-form-item label="授权说明">
            <div class="auth-info">
              <p>点击下方按钮跳转到 TikTok Shop 授权页面，完成店铺授权。</p>
              <p class="tip">请确保您已完成基础信息中的 App Key 和 App Secret 配置。</p>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              :loading="authLoading"
              @click="handleOAuth"
            >
              <el-icon><Link /></el-icon>
              跳转授权
            </el-button>
          </el-form-item>
        </template>

        <!-- Access Token -->
        <template v-if="formData.authMethod === 'token'">
          <el-form-item label="店铺ID" prop="storeId">
            <el-input
              v-model="formData.storeId"
              placeholder="请输入店铺ID"
              clearable
            />
          </el-form-item>

          <el-form-item label="Access Token" prop="accessToken">
            <el-input
              v-model="formData.accessToken"
              type="textarea"
              :rows="4"
              placeholder="请输入Access Token"
            />
          </el-form-item>
        </template>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button
          v-if="formData.authMethod === 'token'"
          type="primary"
          @click="handleSave"
        >
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from "element-plus";
import { Plus, Shop, Link } from "@element-plus/icons-vue";

const formRef = ref<FormInstance>();
const dialogVisible = ref(false);
const isEdit = ref(false);
const authLoading = ref(false);

// 国家/地区对应表情
const countryFlags: Record<string, string> = {
  '美国': '🇺🇸',
  '英国': '🇬🇧',
  '加拿大': '🇨🇦',
  '澳大利亚': '🇦🇺',
  '新加坡': '🇸🇬',
  '马来西亚': '🇲🇾',
  '泰国': '🇹🇭',
  '菲律宾': '🇵🇭',
  '越南': '🇻🇳',
  '印度尼西亚': '🇮🇩'
};

// 店铺列表
const storeList = ref([
  {
    id: '1',
    name: 'TikTok Shop US店铺',
    storeId: 'TKSUS001',
    country: '美国',
    flag: '🇺🇸',
    status: '已连接',
    createdAt: '2024-01-15 10:30:00'
  },
  {
    id: '2',
    name: 'TikTok Shop UK店铺',
    storeId: 'TKSUK001',
    country: '英国',
    flag: '🇬🇧',
    status: '已连接',
    createdAt: '2024-01-16 14:20:00'
  },
  {
    id: '3',
    name: 'TikTok Shop东南亚店铺',
    storeId: 'TKSSG001',
    country: '新加坡',
    flag: '🇸🇬',
    status: '已连接',
    createdAt: '2024-01-17 09:15:00'
  }
]);

// 表单数据
const formData = reactive({
  name: '',
  country: '',
  storeId: '',
  authMethod: 'oauth',
  accessToken: ''
});

// 表单验证规则
const formRules: FormRules = {
  name: [{ required: true, message: '请输入店铺名称', trigger: 'blur' }],
  country: [{ required: true, message: '请选择国家/地区', trigger: 'change' }],
  storeId: [
    {
      required: true,
      message: '请输入店铺ID',
      trigger: 'blur',
      validator: (rule, value, callback) => {
        if (formData.authMethod === 'token' && !value) {
          callback(new Error('请输入店铺ID'));
        } else {
          callback();
        }
      }
    }
  ],
  accessToken: [
    {
      required: true,
      message: '请输入Access Token',
      trigger: 'blur',
      validator: (rule, value, callback) => {
        if (formData.authMethod === 'token' && !value) {
          callback(new Error('请输入Access Token'));
        } else {
          callback();
        }
      }
    }
  ]
};

// 添加店铺
const handleAddStore = () => {
  resetForm();
  isEdit.value = false;
  dialogVisible.value = true;
};

// 编辑店铺
const handleEdit = (store: any) => {
  isEdit.value = true;
  formData.name = store.name;
  formData.country = store.country;
  formData.storeId = store.storeId;
  dialogVisible.value = true;
};

// 查看详情
const handleViewDetail = (store: any) => {
  ElMessage.info(`查看店铺详情：${store.name}`);
};

// 断开店铺
const handleDisconnect = (store: any) => {
  ElMessageBox.confirm(
    `确定要断开店铺"${store.name}"的连接吗？断开后将无法同步该店铺的数据。`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const index = storeList.value.findIndex(item => item.id === store.id);
    if (index > -1) {
      storeList.value.splice(index, 1);
      ElMessage.success('已断开连接');
    }
  }).catch(() => {
    // 用户取消
  });
};

// OAuth 授权
const handleOAuth = () => {
  if (!formData.name || !formData.country) {
    ElMessage.warning('请先输入店铺名称并选择国家/地区');
    return;
  }

  authLoading.value = true;

  // TODO: 构造 OAuth 授权 URL
  const regionCode = getRegionCode(formData.country);
  const authUrl = `https://partner.tiktokshop.com/openapi/v1/auth.html?region=${regionCode}&redirect_uri=${encodeURIComponent(window.location.origin + '/tiktokshop/store/callback')}&response_type=code&state=${Date.now()}`;

  setTimeout(() => {
    authLoading.value = false;
    ElMessage.info('正在跳转到 TikTok Shop 授权页面...');
    window.open(authUrl, '_blank');
  }, 1000);
};

// 保存店铺
const handleSave = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();

    // 模拟添加店铺
    const newStore = {
      id: Date.now().toString(),
      name: formData.name,
      storeId: formData.storeId,
      country: formData.country,
      flag: countryFlags[formData.country] || '🏳️',
      status: '已连接',
      createdAt: new Date().toLocaleString('zh-CN', { hour12: false })
    };

    storeList.value.push(newStore);
    ElMessage.success('店铺添加成功');
    dialogVisible.value = false;
    resetForm();
  } catch (error) {
    ElMessage.error('请先完善必填项');
  }
};

// 国家/地区变化
const handleCountryChange = () => {
  // 可以在这里根据国家/地区做一些处理
};

// 获取地区代码
const getRegionCode = (country: string) => {
  const regionMap: Record<string, string> = {
    '美国': 'US',
    '英国': 'GB',
    '加拿大': 'CA',
    '澳大利亚': 'AU',
    '新加坡': 'SG',
    '马来西亚': 'MY',
    '泰国': 'TH',
    '菲律宾': 'PH',
    '越南': 'VN',
    '印度尼西亚': 'ID'
  };
  return regionMap[country] || 'US';
};

// 对话框关闭
const handleDialogClose = () => {
  resetForm();
};

// 重置表单
const resetForm = () => {
  formData.name = '';
  formData.country = '';
  formData.storeId = '';
  formData.authMethod = 'oauth';
  formData.accessToken = '';
  formRef.value?.resetFields();
};
</script>

<style scoped lang="scss">
.multi-store-manage {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .title {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }
  }

  .store-list {
    :deep(.el-table) {
      border: 1px solid #e8e8e8;
      border-radius: 8px;

      .el-table__header {
        th {
          background-color: #fafafa;
          font-weight: 600;
          color: #333;
        }
      }

      .el-table__body {
        tr {
          &:hover {
            background-color: #f0f5ff;
          }
        }
      }
    }

    .store-name {
      display: flex;
      align-items: center;
      gap: 8px;

      .icon {
        font-size: 16px;
        color: #1890ff;
      }
    }

    .store-id {
      font-family: 'Courier New', monospace;
      color: #666;
      font-size: 13px;
    }

    .country-info {
      display: flex;
      align-items: center;
      gap: 8px;

      .flag {
        font-size: 20px;
      }
    }

    .create-time {
      color: #999;
      font-size: 13px;
    }
  }

  .auth-info {
    padding: 12px;
    background-color: #f0f5ff;
    border: 1px solid #adc6ff;
    border-radius: 4px;
    font-size: 13px;
    color: #666;
    line-height: 1.8;

    p {
      margin: 0;

      &.tip {
        margin-top: 8px;
        color: #1890ff;
      }
    }
  }
}
</style>
