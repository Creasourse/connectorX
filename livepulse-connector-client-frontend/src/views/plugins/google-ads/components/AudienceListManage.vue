<template>
  <div class="audience-list-manage">
    <!-- 页面头部 -->
    <div class="page-header">
      <h3 class="page-title">受众列表管理</h3>
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon>
        创建受众列表
      </el-button>
    </div>

    <!-- 受众列表 -->
    <div class="audience-list">
      <div
        v-for="item in audienceList"
        :key="item.id"
        class="audience-item"
      >
        <div class="item-content">
          <div class="item-main">
            <div class="item-name">{{ item.name }}</div>
            <el-tag v-if="item.status === 'active'" type="success" size="small">活跃</el-tag>
            <el-tag v-else type="info" size="small">未激活</el-tag>
          </div>

          <div class="item-details">
            <div class="detail-item">
              <span class="detail-label">受众ID:</span>
              <span class="detail-value">{{ item.audienceId }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">规模:</span>
              <span class="detail-value">{{ formatNumber(item.scale) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">匹配率:</span>
              <span class="detail-value">{{ item.matchRate }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">更新时间:</span>
              <span class="detail-value">{{ item.updateTime }}</span>
            </div>
          </div>
        </div>

        <div class="item-actions">
          <el-tooltip content="上传" placement="top">
            <el-button
              type="primary"
              :icon="Upload"
              circle
              size="small"
              @click="handleUpload(item)"
            />
          </el-tooltip>
          <el-tooltip content="刷新" placement="top">
            <el-button
              type="primary"
              :icon="Refresh"
              circle
              size="small"
              @click="handleRefresh(item)"
            />
          </el-tooltip>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="audienceList.length === 0" description="暂无受众列表数据" />
    </div>

    <!-- 创建受众列表对话框 -->
    <el-dialog
      v-model="createDialogVisible"
      title="创建受众列表"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createFormRules"
        label-position="top"
      >
        <el-form-item label="受众列表名称" prop="name">
          <el-input
            v-model="createForm.name"
            placeholder="请输入受众列表名称"
            clearable
          />
        </el-form-item>

        <el-form-item label="受众列表描述" prop="description">
          <el-input
            v-model="createForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入受众列表描述"
          />
        </el-form-item>

        <el-form-item label="应用类型" prop="applicationType">
          <el-select v-model="createForm.applicationType" placeholder="选择应用类型">
            <el-option label="客户匹配" value="customer_match" />
            <el-option label="相似受众" value="similar_audience" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmCreate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue";
import { Plus, Upload, Refresh } from "@element-plus/icons-vue";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";

interface AudienceItem {
  id: string;
  name: string;
  audienceId: string;
  scale: number;
  matchRate: string;
  updateTime: string;
  status: "active" | "inactive";
}

const audienceList = ref<AudienceItem[]>([
  {
    id: "1",
    name: "高价值客户-购买3次以上",
    audienceId: "ga_aud_1234567890",
    scale: 156890,
    matchRate: "82.3%",
    updateTime: "2026-05-08 16:45:30",
    status: "active"
  },
  {
    id: "2",
    name: "30天内活跃用户",
    audienceId: "ga_aud_1234567891",
    scale: 124560,
    matchRate: "78.6%",
    updateTime: "2026-05-07 14:20:15",
    status: "active"
  },
  {
    id: "3",
    name: "购物车放弃者",
    audienceId: "ga_aud_1234567892",
    scale: 89345,
    matchRate: "71.2%",
    updateTime: "2026-05-06 12:10:45",
    status: "active"
  }
]);

const createDialogVisible = ref(false);
const createFormRef = ref<FormInstance>();

const createForm = reactive({
  name: "",
  description: "",
  applicationType: "customer_match"
});

const createFormRules: FormRules = {
  name: [{ required: true, message: "请输入受众列表名称", trigger: "blur" }],
  description: [{ required: true, message: "请输入受众列表描述", trigger: "blur" }],
  applicationType: [{ required: true, message: "请选择应用类型", trigger: "change" }]
};

const formatNumber = (num: number) => {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

const handleCreate = () => {
  createDialogVisible.value = true;
};

const handleConfirmCreate = async () => {
  if (!createFormRef.value) return;

  try {
    await createFormRef.value.validate();

    const newAudience: AudienceItem = {
      id: Date.now().toString(),
      name: createForm.name,
      audienceId: `ga_aud_${Date.now()}`,
      scale: 0,
      matchRate: "0%",
      updateTime: new Date().toLocaleString("zh-CN", { hour12: false }),
      status: "inactive"
    };

    audienceList.value.unshift(newAudience);
    createDialogVisible.value = false;
    createFormRef.value.resetFields();
    ElMessage.success("创建成功");
  } catch (error) {
    ElMessage.error("请完善必填项");
  }
};

const handleUpload = (item: AudienceItem) => {
  ElMessage.info(`上传受众列表: ${item.name}`);
};

const handleRefresh = (item: AudienceItem) => {
  ElMessage.info(`刷新受众列表: ${item.name}`);
};
</script>

<style scoped lang="scss">
.audience-list-manage {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.audience-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.audience-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background-color: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  transition: all 0.3s;

  &:hover {
    border-color: #1890ff;
    box-shadow: 0 2px 8px rgba(24, 144, 255, 0.1);
  }
}

.item-content {
  flex: 1;
}

.item-main {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.item-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.item-details {
  display: flex;
  gap: 24px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.detail-label {
  font-size: 12px;
  color: #999;
}

.detail-value {
  font-size: 12px;
  color: #666;
}

.item-actions {
  display: flex;
  gap: 8px;

  .el-button {
    width: 32px;
    height: 32px;
  }
}

:deep(.el-button--primary) {
  background-color: #1890ff;
  border-color: #1890ff;

  &:hover {
    background-color: #40a9ff;
    border-color: #40a9ff;
  }
}

:deep(.el-dialog__body) {
  padding-top: 20px;
}

:deep(.el-form-item__label) {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

:deep(.el-select) {
  width: 100%;
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

:deep(.el-textarea__inner) {
  background-color: #f5f5f5;
  border: none;
  box-shadow: none;
  padding: 8px 12px;

  &:focus {
    background-color: #e6f7ff;
  }
}
</style>
