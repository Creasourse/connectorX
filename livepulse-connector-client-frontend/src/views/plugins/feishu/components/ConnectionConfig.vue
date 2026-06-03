<template>
  <div class="connection-config-container">
    <el-card class="config-card">
      <div class="card-header">
        <h3 class="card-title">连接配置</h3>
        <p class="card-desc">配置数据连接参数和重试策略</p>
      </div>

      <div class="config-content">
        <!-- 最大连接数 -->
        <div class="config-section">
          <div class="section-label">
            <div class="label-left">
              <div class="green-bar"></div>
              <span class="label-text">最大连接数</span>
            </div>
            <el-input-number
              v-model="configData.maxConnections"
              :min="1"
              :max="50"
              controls-position="right"
            />
          </div>
          <el-slider
            v-model="configData.maxConnections"
            :min="1"
            :max="50"
            :marks="connectionMarks"
            :step="1"
            style="margin-top: 20px"
          />
          <p class="section-desc">
            当前设置: {{ configData.maxConnections }} 个并发连接
          </p>
        </div>

        <!-- 重试策略 -->
        <div class="config-section">
          <div class="section-label">
            <div class="label-left">
              <div class="green-bar"></div>
              <span class="label-text">重试策略</span>
            </div>
            <el-select v-model="configData.retryStrategy" style="width: 160px">
              <el-option
                v-for="item in RETRY_STRATEGY_OPTIONS"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </div>
          <p class="section-desc">
            {{
              configData.retryStrategy === "exponential"
                ? "指数退避: 每次重试间隔呈指数增长 (1s, 2s, 4s, 8s...)"
                : configData.retryStrategy === "fixed"
                  ? "固定间隔: 每次重试间隔固定为 2 秒"
                  : "线性递增: 每次重试间隔线性增长 (1s, 2s, 3s, 4s...)"
            }}
          </p>
        </div>

        <!-- 最大重试次数 -->
        <div class="config-section">
          <div class="section-label">
            <div class="label-left">
              <div class="green-bar"></div>
              <span class="label-text">最大重试次数</span>
            </div>
            <el-input-number
              v-model="configData.maxRetries"
              :min="0"
              :max="10"
              controls-position="right"
            />
          </div>
          <el-slider
            v-model="configData.maxRetries"
            :min="0"
            :max="10"
            :marks="{ 0: '0', 3: '3', 5: '5', 10: '10' }"
            style="margin-top: 20px"
          />
          <p class="section-desc">
            连接失败后的最大重试次数，当前设置为 {{ configData.maxRetries }} 次
          </p>
        </div>

        <!-- 数据过滤 -->
        <div class="config-section">
          <div class="section-label">
            <div class="label-left">
              <div class="green-bar"></div>
              <span class="label-text">数据过滤</span>
            </div>
          </div>
          <el-input
            v-model="configData.dataFilter"
            type="textarea"
            :rows="3"
            placeholder="输入数据过滤条件，例如: status='active' AND created_time > '2024-01-01'"
          />
          <p class="section-desc">支持SQL风格的过滤表达式</p>
        </div>

        <!-- 排除字段 -->
        <div class="config-section">
          <div class="section-label">
            <div class="label-left">
              <div class="green-bar"></div>
              <span class="label-text">排除字段</span>
            </div>
          </div>
          <div class="excluded-fields">
            <el-tag
              v-for="(field, index) in configData.excludedFields"
              :key="index"
              closable
              @close="removeField(index)"
              class="field-tag"
            >
              {{ field }}
            </el-tag>
            <el-input
              v-if="inputVisible"
              ref="inputRef"
              v-model="inputValue"
              size="small"
              style="width: 100px"
              @blur="handleInputConfirm"
              @keyup.enter="handleInputConfirm"
            />
            <el-button
              v-else
              size="small"
              @click="showInput"
              :icon="Plus"
              class="add-field-btn"
            >
              添加字段
            </el-button>
          </div>
          <p class="section-desc">这些字段将不会同步到目标系统</p>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button class="cancel-btn" @click="handleCancel">取消</el-button>
          <el-button class="test-btn" :icon="Connection" :loading="testing" @click="handleTest">
            测试连接
          </el-button>
          <el-button type="primary" class="save-btn" :loading="saving" @click="handleSave">
            保存配置
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, nextTick } from "vue";
import type { PropType } from "vue";
import { ElMessage } from "element-plus";
import { Plus, Connection } from "@element-plus/icons-vue";
import { RETRY_STRATEGY_OPTIONS } from "../types";
import type { ConnectionConfig } from "../types";

const props = defineProps({
  config: {
    type: Object as PropType<ConnectionConfig>,
    required: true
  }
});

const emit = defineEmits<{
  (e: "update", config: ConnectionConfig): void;
}>();

const testing = ref(false);
const saving = ref(false);

const configData = reactive<ConnectionConfig>({
  maxConnections: 10,
  retryStrategy: "exponential",
  maxRetries: 3,
  dataFilter: "",
  dataRange: "all",
  excludedFields: ["创建人ID", "最后修改人"]
});

// 连接数标记
const connectionMarks = {
  1: "1",
  10: "10",
  25: "25",
  50: "50"
};

// 排除字段输入
const inputVisible = ref(false);
const inputValue = ref("");
const inputRef = ref();

// 监听props变化
watch(
  () => props.config,
  newConfig => {
    if (newConfig) {
      Object.assign(configData, newConfig);
    }
  },
  { deep: true, immediate: true }
);

// 显示输入框
const showInput = () => {
  inputVisible.value = true;
  nextTick(() => {
    inputRef.value?.focus();
  });
};

// 确认添加字段
const handleInputConfirm = () => {
  const value = inputValue.value.trim();
  if (value) {
    if (!configData.excludedFields.includes(value)) {
      configData.excludedFields.push(value);
    } else {
      ElMessage.warning("字段已存在");
    }
  }
  inputVisible.value = false;
  inputValue.value = "";
};

// 移除字段
const removeField = (index: number) => {
  configData.excludedFields.splice(index, 1);
};

// 取消
const handleCancel = () => {
  Object.assign(configData, props.config);
};

// 测试连接
const handleTest = async () => {
  testing.value = true;
  try {
    // TODO: 调用测试连接接口
    await new Promise(resolve => setTimeout(resolve, 1000));
    ElMessage.success("连接测试成功");
  } catch (error) {
    ElMessage.error("连接测试失败");
  } finally {
    testing.value = false;
  }
};

// 保存配置
const handleSave = async () => {
  saving.value = true;
  try {
    // TODO: 调用保存接口
    await new Promise(resolve => setTimeout(resolve, 500));
    ElMessage.success("保存成功");
    emit("update", { ...configData });
  } catch (error) {
    ElMessage.error("保存失败");
  } finally {
    saving.value = false;
  }
};
</script>

<style lang="scss" scoped>
.connection-config-container {
  width: 100%;
}

.config-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  :deep(.el-card__body) {
    padding: 24px;
  }
}

.card-header {
  margin-bottom: 32px;

  .card-title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 8px 0;
  }

  .card-desc {
    font-size: 14px;
    color: #909399;
    margin: 0;
  }
}

.config-content {
  max-width: 700px;
}

.config-section {
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;

  &:last-of-type {
    border-bottom: none;
  }

  .section-label {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .label-left {
      display: flex;
      align-items: center;
      gap: 12px;

      .green-bar {
        width: 3px;
        height: 18px;
        background: #00b894;
        border-radius: 2px;
      }

      .label-text {
        font-size: 15px;
        font-weight: 500;
        color: #303133;
      }
    }
  }

  .section-desc {
    font-size: 13px;
    color: #909399;
    margin: 12px 0 0 0;
    line-height: 1.6;
  }
}

.excluded-fields {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;

  .field-tag {
    background: #f0f2f5;
    border-color: #d9d9d9;
  }

  .add-field-btn {
    border-style: dashed;
  }
}

.action-buttons {
  display: flex;
  gap: 12px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;

  .el-button {
    min-width: 100px;

    .el-icon {
      margin-right: 4px;
    }
  }
}
</style>
