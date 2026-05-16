<template>
  <div class="id-type-config">
    <!-- 客户匹配配置 -->
    <div class="config-section">
      <div class="section-header">
        <h3 class="section-title">客户匹配</h3>
        <span class="section-subtitle">支持的用户标识符类型及加密配置</span>
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
            <span class="match-rate">{{ idType.matchRate }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据预处理 -->
    <div class="config-section">
      <h3 class="section-title">数据预处理</h3>

      <div class="preprocessing-group">
        <div class="group-title">邮箱格式化</div>
        <div class="checkbox-list">
          <el-checkbox v-model="preprocessing.emailToLower">转小写</el-checkbox>
          <el-checkbox v-model="preprocessing.emailRemoveSpaces"
            >去除空格</el-checkbox
          >
          <el-checkbox v-model="preprocessing.emailRemoveSpecial"
            >去除特殊字符</el-checkbox
          >
        </div>
      </div>

      <div class="preprocessing-group">
        <div class="group-title">手机号格式化</div>
        <div class="checkbox-list">
          <el-checkbox v-model="preprocessing.phoneRemoveNonDigits"
            >去除非数字字符</el-checkbox
          >
          <el-checkbox v-model="preprocessing.phoneAddCountryCode"
            >添加国家代码</el-checkbox
          >
          <el-checkbox v-model="preprocessing.phoneStandardize"
            >标准化格式</el-checkbox
          >
        </div>
      </div>

      <div class="preprocessing-group">
        <div class="group-title">地址格式化</div>
        <div class="checkbox-list">
          <el-checkbox v-model="preprocessing.addressStandardize"
            >标准化地址</el-checkbox
          >
          <el-checkbox v-model="preprocessing.addressRemoveApartment"
            >去除公寓号</el-checkbox
          >
        </div>
      </div>
    </div>

    <!-- Google Ads客户匹配要求 -->
    <div class="info-box">
      <h4 class="info-title">Google Ads 客户匹配要求</h4>
      <ul class="info-list">
        <li>所有数据必须使用SHA-256加密</li>
        <li>邮箱和手机号的匹配率最高（推荐使用）</li>
        <li>受众列表最少需要1000个用户才能用于广告投放</li>
        <li>受众保留期最长540天</li>
        <li>需要账户符合Google Ads政策要求</li>
      </ul>
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
  matchRate: string;
  recommended: boolean;
  enabled: boolean;
}

const idTypes = ref<IdType[]>([
  {
    key: "email",
    label: "电子邮件",
    encryption: "SHA-256",
    matchRate: "75-85%",
    recommended: true,
    enabled: true
  },
  {
    key: "phone",
    label: "手机号",
    encryption: "SHA-256",
    matchRate: "65-75%",
    recommended: true,
    enabled: true
  },
  {
    key: "address",
    label: "邮寄地址",
    encryption: "SHA-256",
    matchRate: "50-60%",
    recommended: false,
    enabled: false
  },
  {
    key: "mobileId",
    label: "移动设备ID (IDFA/AAID)",
    encryption: "SHA-256",
    matchRate: "70-80%",
    recommended: false,
    enabled: false
  }
]);

const preprocessing = reactive({
  emailToLower: true,
  emailRemoveSpaces: true,
  emailRemoveSpecial: true,
  phoneRemoveNonDigits: true,
  phoneAddCountryCode: true,
  phoneStandardize: true,
  addressStandardize: true,
  addressRemoveApartment: false
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

.match-rate {
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

.info-box {
  margin-top: 32px;
  padding: 20px;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-radius: 8px;
  border-left: 4px solid #1890ff;
}

.info-title {
  margin: 0 0 16px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.info-list {
  margin: 0;
  padding-left: 20px;
  list-style-type: disc;

  li {
    font-size: 14px;
    color: #666;
    line-height: 1.8;
    margin-bottom: 8px;

    &:last-child {
      margin-bottom: 0;
    }
  }
}

:deep(.el-switch.is-checked .el-switch__core) {
  background-color: #1890ff;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #1890ff;
  border-color: #1890ff;
}
</style>
