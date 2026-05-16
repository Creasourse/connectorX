<template>
  <div class="multi-account-manage">
    <div class="header">
      <h2 class="title">多账户管理</h2>
      <el-button type="primary" @click="handleAddAccount">
        <el-icon><Plus /></el-icon>
        添加账户
      </el-button>
    </div>

    <!-- 账户列表 -->
    <div class="account-list">
      <el-empty v-if="accountList.length === 0" description="暂无账户" />

      <el-table :data="accountList" style="width: 100%">
        <el-table-column prop="name" label="账户名称" min-width="200">
          <template #default="{ row }">
            <div class="account-name">
              <el-icon class="icon"><User /></el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="accountId" label="账户ID" min-width="180">
          <template #default="{ row }">
            <span class="account-id">{{ row.accountId }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="type" label="账户类型" min-width="120">
          <template #default="{ row }">
            <el-tag :type="row.type === '广告' ? 'danger' : 'primary'" size="small">
              {{ row.type }}
            </el-tag>
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

        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              type="danger"
              size="small"
              link
              @click="handleDisconnect(row)"
            >
              断开
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 添加账户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="添加TikTok账户"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-position="top"
      >
        <el-form-item label="账户名称" prop="name">
          <el-input
            v-model="formData.name"
            placeholder="请输入账户名称，便于识别"
            clearable
          />
        </el-form-item>

        <el-form-item label="账户类型" prop="type">
          <el-radio-group v-model="formData.type">
            <el-radio label="广告">广告账户</el-radio>
            <el-radio label="内容">内容账户</el-radio>
          </el-radio-group>
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
              <p>点击下方按钮跳转到 TikTok 授权页面，完成账户授权。</p>
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
          <el-form-item label="Access Token" prop="accessToken">
            <el-input
              v-model="formData.accessToken"
              type="textarea"
              :rows="4"
              placeholder="请输入Access Token"
            />
          </el-form-item>

          <el-form-item label="账户ID（可选）">
            <el-input
              v-model="formData.accountId"
              placeholder="如果已知账户ID，请输入"
              clearable
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
import { Plus, User, Link } from "@element-plus/icons-vue";

const formRef = ref<FormInstance>();
const dialogVisible = ref(false);
const authLoading = ref(false);

// 账户列表
const accountList = ref([
  {
    id: '1',
    name: 'TikTok广告账户1',
    accountId: '7258492012345678',
    type: '广告',
    status: '已连接',
    createdAt: '2024-01-15 10:30:00'
  },
  {
    id: '2',
    name: 'TikTok广告账户2',
    accountId: '7258492012345679',
    type: '广告',
    status: '已连接',
    createdAt: '2024-01-16 14:20:00'
  },
  {
    id: '3',
    name: 'TikTok内容账户1',
    accountId: 'content_user_123456789',
    type: '内容',
    status: '已连接',
    createdAt: '2024-01-17 09:15:00'
  }
]);

// 表单数据
const formData = reactive({
  name: '',
  type: '广告',
  authMethod: 'oauth',
  accessToken: '',
  accountId: ''
});

// 表单验证规则
const formRules: FormRules = {
  name: [{ required: true, message: '请输入账户名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择账户类型', trigger: 'change' }],
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

// 添加账户
const handleAddAccount = () => {
  resetForm();
  dialogVisible.value = true;
};

// OAuth 授权
const handleOAuth = () => {
  if (!formData.name) {
    ElMessage.warning('请先输入账户名称');
    return;
  }

  authLoading.value = true;

  // TODO: 构造 OAuth 授权 URL
  // 根据账户类型选择不同的授权 scope
  const scope = formData.type === '广告'
    ? 'advertiser.info,campaign.management'
    : 'user.info.basic,video.list';

  const authUrl = `https://ads.tiktok.com/marketing_api/auth?app_id=${formData.type === '广告' ? 'marketing' : 'content'}&redirect_uri=${encodeURIComponent(window.location.origin + '/tiktok/account/callback')}&response_type=code&scope=${scope}`;

  setTimeout(() => {
    authLoading.value = false;
    ElMessage.info('正在跳转到 TikTok 授权页面...');
    window.open(authUrl, '_blank');
  }, 1000);
};

// 保存账户
const handleSave = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();

    // 模拟添加账户
    const newAccount = {
      id: Date.now().toString(),
      name: formData.name,
      accountId: formData.accountId || `auto_${Date.now()}`,
      type: formData.type,
      status: '已连接',
      createdAt: new Date().toLocaleString('zh-CN', { hour12: false })
    };

    accountList.value.push(newAccount);
    ElMessage.success('账户添加成功');
    dialogVisible.value = false;
    resetForm();
  } catch (error) {
    ElMessage.error('请先完善必填项');
  }
};

// 断开账户
const handleDisconnect = (account: any) => {
  ElMessageBox.confirm(
    `确定要断开账户"${account.name}"的连接吗？断开后将无法同步该账户的数据。`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const index = accountList.value.findIndex(item => item.id === account.id);
    if (index > -1) {
      accountList.value.splice(index, 1);
      ElMessage.success('已断开连接');
    }
  }).catch(() => {
    // 用户取消
  });
};

// 对话框关闭
const handleDialogClose = () => {
  resetForm();
};

// 重置表单
const resetForm = () => {
  formData.name = '';
  formData.type = '广告';
  formData.authMethod = 'oauth';
  formData.accessToken = '';
  formData.accountId = '';
  formRef.value?.resetFields();
};
</script>

<style scoped lang="scss">
.multi-account-manage {
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

  .account-list {
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

    .account-name {
      display: flex;
      align-items: center;
      gap: 8px;

      .icon {
        font-size: 16px;
        color: #1890ff;
      }
    }

    .account-id {
      font-family: 'Courier New', monospace;
      color: #666;
      font-size: 13px;
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
