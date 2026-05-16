<template>
  <div class="webhook-subscription">
    <div class="header">
      <h2 class="title">Webhook 订阅</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加订阅
      </el-button>
    </div>

    <!-- 订阅列表 -->
    <div class="subscription-list">
      <el-empty v-if="subscriptionList.length === 0" description="暂无订阅" />

      <div
        v-for="subscription in subscriptionList"
        :key="subscription.id"
        class="subscription-item"
      >
        <div class="item-header">
          <div class="item-title">
            <el-icon class="icon"><Bell /></el-icon>
            <span>{{ subscription.name }}</span>
          </div>
          <div class="item-status">
            <el-tag :type="subscription.enabled ? 'success' : 'info'" size="small">
              {{ subscription.enabled ? '已启用' : '已禁用' }}
            </el-tag>
          </div>
        </div>

        <div class="item-content">
          <div class="content-row">
            <span class="label">事件类型：</span>
            <span class="value">{{ subscription.eventType }}</span>
          </div>
          <div class="content-row">
            <span class="label">回调地址：</span>
            <span class="value url">{{ subscription.callbackUrl }}</span>
          </div>
        </div>

        <div class="item-actions">
          <el-button
            :type="subscription.enabled ? 'warning' : 'success'"
            size="small"
            @click="handleToggleStatus(subscription)"
          >
            {{ subscription.enabled ? '禁用' : '启用' }}
          </el-button>
          <el-button type="primary" size="small" @click="handleEdit(subscription)">
            编辑
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(subscription)">
            删除
          </el-button>
        </div>
      </div>
    </div>

    <!-- 添加/编辑订阅对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑订阅' : '添加订阅'"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-position="top"
      >
        <el-form-item label="订阅名称" prop="name">
          <el-input
            v-model="formData.name"
            placeholder="请输入订阅名称"
            clearable
          />
        </el-form-item>

        <el-form-item label="事件类型" prop="eventType">
          <el-select
            v-model="formData.eventType"
            placeholder="请选择事件类型"
            style="width: 100%"
          >
            <el-option label="广告数据变更通知" value="ad.data_change" />
            <el-option label="视频发布成功通知" value="video.publish.success" />
            <el-option label="用户互动事件通知" value="user.interaction" />
            <el-option label="评论事件通知" value="comment.event" />
            <el-option label="分享事件通知" value="share.event" />
            <el-option label="点赞事件通知" value="like.event" />
          </el-select>
        </el-form-item>

        <el-form-item label="回调地址" prop="callbackUrl">
          <el-input
            v-model="formData.callbackUrl"
            placeholder="请输入回调地址，如：https://your-domain.com/webhook/tiktok"
            clearable
          />
          <div class="form-tip">
            回调地址必须是公网可访问的 HTTPS 地址
          </div>
        </el-form-item>

        <el-form-item label="启用状态">
          <el-switch v-model="formData.enabled" />
          <span class="switch-label">{{ formData.enabled ? '已启用' : '已禁用' }}</span>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from "element-plus";
import { Plus, Bell } from "@element-plus/icons-vue";

const formRef = ref<FormInstance>();
const dialogVisible = ref(false);
const isEdit = ref(false);

// 订阅列表
const subscriptionList = ref([
  {
    id: '1',
    name: '广告数据变更通知',
    eventType: 'ad.data_change',
    callbackUrl: 'https://your-domain.com/webhook/tiktok/ad',
    enabled: true
  },
  {
    id: '2',
    name: '视频发布成功通知',
    eventType: 'video.publish.success',
    callbackUrl: 'https://your-domain.com/webhook/tiktok/video',
    enabled: true
  },
  {
    id: '3',
    name: '用户互动事件通知',
    eventType: 'user.interaction',
    callbackUrl: 'https://your-domain.com/webhook/tiktok/user',
    enabled: true
  }
]);

// 表单数据
const formData = reactive({
  id: '',
  name: '',
  eventType: '',
  callbackUrl: '',
  enabled: true
});

// 表单验证规则
const formRules: FormRules = {
  name: [{ required: true, message: '请输入订阅名称', trigger: 'blur' }],
  eventType: [{ required: true, message: '请选择事件类型', trigger: 'change' }],
  callbackUrl: [
    { required: true, message: '请输入回调地址', trigger: 'blur' },
    {
      pattern: /^https?:\/\/.+/,
      message: '请输入有效的URL地址',
      trigger: 'blur'
    }
  ]
};

// 添加订阅
const handleAdd = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

// 编辑订阅
const handleEdit = (subscription: any) => {
  isEdit.value = true;
  formData.id = subscription.id;
  formData.name = subscription.name;
  formData.eventType = subscription.eventType;
  formData.callbackUrl = subscription.callbackUrl;
  formData.enabled = subscription.enabled;
  dialogVisible.value = true;
};

// 删除订阅
const handleDelete = (subscription: any) => {
  ElMessageBox.confirm(
    `确定要删除订阅"${subscription.name}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const index = subscriptionList.value.findIndex(item => item.id === subscription.id);
    if (index > -1) {
      subscriptionList.value.splice(index, 1);
      ElMessage.success('删除成功');
    }
  }).catch(() => {
    // 用户取消删除
  });
};

// 切换状态
const handleToggleStatus = (subscription: any) => {
  subscription.enabled = !subscription.enabled;
  ElMessage.success(subscription.enabled ? '已启用' : '已禁用');
};

// 保存
const handleSave = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();

    if (isEdit.value) {
      // 编辑
      const index = subscriptionList.value.findIndex(item => item.id === formData.id);
      if (index > -1) {
        subscriptionList.value[index] = {
          id: formData.id,
          name: formData.name,
          eventType: formData.eventType,
          callbackUrl: formData.callbackUrl,
          enabled: formData.enabled
        };
      }
      ElMessage.success('更新成功');
    } else {
      // 新增
      subscriptionList.value.push({
        id: Date.now().toString(),
        name: formData.name,
        eventType: formData.eventType,
        callbackUrl: formData.callbackUrl,
        enabled: formData.enabled
      });
      ElMessage.success('添加成功');
    }

    dialogVisible.value = false;
    resetForm();
  } catch (error) {
    ElMessage.error('请先完善必填项');
  }
};

// 对话框关闭
const handleDialogClose = () => {
  resetForm();
};

// 重置表单
const resetForm = () => {
  formData.id = '';
  formData.name = '';
  formData.eventType = '';
  formData.callbackUrl = '';
  formData.enabled = true;
  formRef.value?.resetFields();
};
</script>

<style scoped lang="scss">
.webhook-subscription {
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

  .subscription-list {
    display: flex;
    flex-direction: column;
    gap: 16px;

    .subscription-item {
      border: 1px solid #e8e8e8;
      border-radius: 8px;
      padding: 16px;
      background-color: #fafafa;
      transition: all 0.3s;

      &:hover {
        border-color: #1890ff;
        background-color: #f0f5ff;
      }

      .item-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        .item-title {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 14px;
          font-weight: 600;
          color: #333;

          .icon {
            font-size: 16px;
            color: #1890ff;
          }
        }

        .item-status {
          display: flex;
          align-items: center;
          gap: 8px;
        }
      }

      .item-content {
        margin-bottom: 12px;

        .content-row {
          display: flex;
          align-items: center;
          margin-bottom: 8px;
          font-size: 13px;

          &:last-child {
            margin-bottom: 0;
          }

          .label {
            color: #666;
            min-width: 80px;
          }

          .value {
            color: #333;

            &.url {
              color: #1890ff;
              word-break: break-all;
            }
          }
        }
      }

      .item-actions {
        display: flex;
        gap: 8px;
        justify-content: flex-end;
      }
    }
  }

  .form-tip {
    margin-top: 4px;
    font-size: 12px;
    color: #999;
    line-height: 1.5;
  }

  .switch-label {
    margin-left: 8px;
    font-size: 14px;
    color: #666;
  }
}
</style>
