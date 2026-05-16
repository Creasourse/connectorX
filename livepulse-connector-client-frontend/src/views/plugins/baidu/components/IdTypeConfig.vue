<template>
  <div class="id-type-config">
    <!-- ID类型配置 -->
    <div class="config-section">
      <div class="section-header">
        <h3 class="section-title">ID类型配置</h3>
        <span class="section-subtitle">支持的ID类型及加密方式配置</span>
      </div>

      <div class="id-type-list">
        <div v-for="idType in idTypes" :key="idType.key" class="id-type-item">
          <div class="id-type-left">
            <el-switch
              v-model="idType.enabled"
              active-color="#1890ff"
              inactive-color="#d9d9d9"
            />
            <div class="id-type-info">
              <div class="id-type-label">
                <span class="label-text">{{ idType.label }}</span>
                <el-tag v-if="idType.recommended" type="primary" size="small"
                  >推荐</el-tag
                >
              </div>
              <div class="id-type-desc">加密方式: {{ idType.encryption }}</div>
            </div>
          </div>
          <div class="id-type-right">
            <span class="usage-rate">{{ idType.usageRate }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据预处理 -->
    <div class="config-section">
      <h3 class="section-title">数据预处理</h3>

      <div class="preprocessing-group">
        <div class="group-title">手机号格式化</div>
        <div class="checkbox-list">
          <el-checkbox v-model="preprocessing.removeNonDigits"
            >去除非数字字符</el-checkbox
          >
          <el-checkbox v-model="preprocessing.uniformFormat"
            >统一为11位格式</el-checkbox
          >
          <el-checkbox v-model="preprocessing.removeCountryCode"
            >去除国际区号</el-checkbox
          >
        </div>
      </div>

      <div class="preprocessing-group">
        <div class="group-title">设备ID格式化</div>
        <div class="checkbox-list">
          <el-checkbox v-model="preprocessing.imeiUppercase"
            >IMEI转大写</el-checkbox
          >
          <el-checkbox v-model="preprocessing.idfaRemoveHyphen"
            >IDFA去除横线</el-checkbox
          >
        </div>
      </div>
    </div>

    <!-- 文件格式要求 -->
    <div class="config-section">
      <h3 class="section-title">文件格式要求</h3>

      <div class="format-grid">
        <div class="format-item">
          <div class="format-label">文件类型</div>
          <el-input v-model="fileFormat.fileType" placeholder="TXT, CSV" />
        </div>
        <div class="format-item">
          <div class="format-label">编码格式</div>
          <el-input v-model="fileFormat.encoding" placeholder="UTF-8" />
        </div>
        <div class="format-item">
          <div class="format-label">最大文件大小</div>
          <el-input v-model="fileFormat.maxSize" placeholder="60 MB" />
        </div>
        <div class="format-item">
          <div class="format-label">内容格式</div>
          <el-input
            v-model="fileFormat.contentFormat"
            placeholder="每行一个加密后的ID"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { type PropType, ref, reactive, watch } from "vue";
import { ElMessage } from "element-plus";

const props = defineProps({
  config: {
    type: Object as PropType<unknown>
  }
});

const emit = defineEmits<{
  (e: "update"): void;
}>();

interface IdType {
  key: string;
  label: string;
  encryption: string;
  usageRate: string;
  recommended: boolean;
  enabled: boolean;
}

const idTypes = ref<IdType[]>([
  {
    key: "mobile",
    label: "手机号",
    encryption: "MD5",
    usageRate: "70%",
    recommended: true,
    enabled: true
  },
  {
    key: "imei",
    label: "IMEI (Android设备)",
    encryption: "MD5",
    usageRate: "20%",
    recommended: false,
    enabled: true
  },
  {
    key: "idfa",
    label: "IDFA (iOS设备)",
    encryption: "MD5",
    usageRate: "8%",
    recommended: false,
    enabled: true
  },
  {
    key: "oaid",
    label: "OAID (Android 10+)",
    encryption: "MD5",
    usageRate: "2%",
    recommended: false,
    enabled: false
  }
]);

const preprocessing = reactive({
  removeNonDigits: true,
  uniformFormat: true,
  removeCountryCode: true,
  imeiUppercase: true,
  idfaRemoveHyphen: true
});

const fileFormat = reactive({
  fileType: "TXT, CSV",
  encoding: "UTF-8",
  maxSize: "60 MB",
  contentFormat: "每行一个加密后的ID"
});

watch(
  () => props.config,
  (newVal: any) => {
    if (newVal?.idTypes) {
      idTypes.value = newVal.idTypes;
    }
    if (newVal?.preprocessing) {
      Object.assign(preprocessing, newVal.preprocessing);
    }
    if (newVal?.fileFormat) {
      Object.assign(fileFormat, newVal.fileFormat);
    }
  },
  { immediate: true, deep: true }
);
</script>

<style scoped lang="scss">
.id-type-config {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.config-section {
  margin-bottom: 32px;

  &:last-child {
    margin-bottom: 0;
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  margin: 0 0 20px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.section-subtitle {
  font-size: 14px;
  color: #999;
}

.id-type-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.id-type-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background-color: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  transition: all 0.3s;

  &:hover {
    border-color: #1890ff;
    box-shadow: 0 2px 8px rgba(24, 144, 255, 0.1);
  }
}

.id-type-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.id-type-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.id-type-label {
  display: flex;
  align-items: center;
  gap: 8px;

  .label-text {
    font-size: 14px;
    font-weight: 500;
    color: #333;
  }
}

.id-type-desc {
  font-size: 12px;
  color: #999;
}

.id-type-right {
  display: flex;
  align-items: center;
}

.usage-rate {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.preprocessing-group {
  margin-bottom: 24px;

  &:last-child {
    margin-bottom: 0;
  }
}

.group-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 12px;
}

.checkbox-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-left: 16px;

  :deep(.el-checkbox) {
    font-size: 14px;
    color: #333;
  }
}

.format-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.format-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.format-label {
  font-size: 14px;
  color: #666;
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

:deep(.el-switch.is-checked .el-switch__core) {
  background-color: #1890ff;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #1890ff;
  border-color: #1890ff;
}
</style>
