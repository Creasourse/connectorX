<template>
  <div class="config-card">
    <h2 class="config-title">同步配置</h2>

    <div class="config-section">
      <div class="section-title">
        <el-icon><Timer /></el-icon>
        同步策略配置
      </div>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-position="top"
        class="config-form"
      >
        <el-form-item label="轮询间隔（分钟）" prop="pollingInterval">
          <el-input-number
            v-model="formData.pollingInterval"
            :min="1"
            :max="1440"
            :step="1"
            placeholder="请输入轮询间隔"
            style="width: 200px"
          />
          <span class="form-tip">设置轮询同步的时间间隔，最小1分钟，最大1440分钟（24小时）</span>
        </el-form-item>

        <el-form-item label="批量处理大小" prop="batchSize">
          <el-input-number
            v-model="formData.batchSize"
            :min="1"
            :max="1000"
            :step="10"
            placeholder="请输入批量大小"
            style="width: 200px"
          />
          <span class="form-tip">每次批量处理的记录数量，建议100-500</span>
        </el-form-item>

        <el-form-item label="同步超时时间（秒）" prop="syncTimeout">
          <el-input-number
            v-model="formData.syncTimeout"
            :min="30"
            :max="3600"
            :step="30"
            placeholder="请输入超时时间"
            style="width: 200px"
          />
          <span class="form-tip">单个同步任务的超时时间，超过此时间将标记为失败</span>
        </el-form-item>

        <el-form-item label="最大重试次数" prop="maxRetryTimes">
          <el-input-number
            v-model="formData.maxRetryTimes"
            :min="0"
            :max="10"
            :step="1"
            placeholder="请输入最大重试次数"
            style="width: 200px"
          />
          <span class="form-tip">同步失败后的最大重试次数，0表示不重试</span>
        </el-form-item>

        <el-form-item label="重试间隔（秒）" prop="retryInterval">
          <el-input-number
            v-model="formData.retryInterval"
            :min="10"
            :max="300"
            :step="10"
            placeholder="请输入重试间隔"
            style="width: 200px"
          />
          <span class="form-tip">重试之间的等待时间</span>
        </el-form-item>

        <el-form-item label="数据保留天数" prop="dataRetentionDays">
          <el-input-number
            v-model="formData.dataRetentionDays"
            :min="1"
            :max="365"
            :step="1"
            placeholder="请输入保留天数"
            style="width: 200px"
          />
          <span class="form-tip">同步日志的保留天数，超过此天数的日志将被清理</span>
        </el-form-item>

        <el-form-item label="是否启用自动清理">
          <el-switch
            v-model="formData.enableAutoCleanup"
            active-text="启用"
            inactive-text="禁用"
          />
          <span class="form-tip">启用后将自动清理过期的同步日志</span>
        </el-form-item>

        <el-form-item label="是否启用增量同步">
          <el-switch
            v-model="formData.enableIncrementalSync"
            active-text="启用"
            inactive-text="禁用"
          />
          <span class="form-tip">仅同步发生变化的数据，提高同步效率</span>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button @click="handleReset">重置</el-button>
      <el-button type="primary" @click="handleSave" :loading="saving">
        <el-icon><Select /></el-icon>
        保存配置
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from "vue";
import type { PropType } from "vue";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import { Timer, Select } from "@element-plus/icons-vue";

const formRef = ref<FormInstance>();
const saving = ref(false);

const props = defineProps({
  config: {
    type: Object as PropType<unknown>
  }
});

const emit = defineEmits<{
  (e: "update"): void;
}>();

// 表单数据
const formData = reactive({
  pollingInterval: 30,
  batchSize: 100,
  syncTimeout: 300,
  maxRetryTimes: 3,
  retryInterval: 60,
  dataRetentionDays: 30,
  enableAutoCleanup: true,
  enableIncrementalSync: true
});

// 表单验证规则
const formRules: FormRules = {
  pollingInterval: [
    { required: true, message: "请输入轮询间隔", trigger: "blur" }
  ],
  batchSize: [
    { required: true, message: "请输入批量大小", trigger: "blur" }
  ],
  syncTimeout: [
    { required: true, message: "请输入超时时间", trigger: "blur" }
  ],
  maxRetryTimes: [
    { required: true, message: "请输入最大重试次数", trigger: "blur" }
  ],
  retryInterval: [
    { required: true, message: "请输入重试间隔", trigger: "blur" }
  ],
  dataRetentionDays: [
    { required: true, message: "请输入数据保留天数", trigger: "blur" }
  ]
};

// 监听 props.config 变化
watch(
  () => props.config,
  (newConfig) => {
    if (newConfig) {
      // 从配置中加载同步策略数据
      Object.assign(formData, {
        pollingInterval: newConfig.pollingInterval || 30,
        batchSize: newConfig.batchSize || 100,
        syncTimeout: newConfig.syncTimeout || 300,
        maxRetryTimes: newConfig.maxRetryTimes || 3,
        retryInterval: newConfig.retryInterval || 60,
        dataRetentionDays: newConfig.dataRetentionDays || 30,
        enableAutoCleanup: newConfig.enableAutoCleanup !== false,
        enableIncrementalSync: newConfig.enableIncrementalSync !== false
      });
    }
  },
  { deep: true, immediate: true }
);

// 重置
const handleReset = () => {
  Object.assign(formData, {
    pollingInterval: 30,
    batchSize: 100,
    syncTimeout: 300,
    maxRetryTimes: 3,
    retryInterval: 60,
    dataRetentionDays: 30,
    enableAutoCleanup: true,
    enableIncrementalSync: true
  });
  ElMessage.info("已重置为默认配置");
};

// 保存配置
const handleSave = async () => {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  saving.value = true;
  try {
    // TODO: 调用保存同步配置接口
    await new Promise(resolve => setTimeout(resolve, 1000));
    ElMessage.success("保存成功");
    emit("update");
  } catch (error) {
    ElMessage.error("保存失败");
  } finally {
    saving.value = false;
  }
};
</script>

<style lang="scss" scoped>
.config-card {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  .config-title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 24px 0;
    padding-bottom: 16px;
    border-bottom: 1px solid #e5e7eb;
  }

  .config-section {
    margin-bottom: 32px;

    .section-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 20px;
      padding-bottom: 12px;
      border-bottom: 1px solid #e5e7eb;

      .el-icon {
        color: #409eff;
        font-size: 18px;
      }
    }

    .config-form {
      max-width: 700px;

      :deep(.el-form-item__label) {
        font-weight: 500;
        color: #606266;
      }

      .form-tip {
        margin-left: 12px;
        font-size: 12px;
        color: #909399;
      }
    }
  }

  .action-buttons {
    display: flex;
    gap: 12px;
    padding-top: 24px;
    border-top: 1px solid #e5e7eb;

    .el-button {
      min-width: 100px;

      .el-icon {
        margin-right: 4px;
      }
    }
  }
}
</style>
