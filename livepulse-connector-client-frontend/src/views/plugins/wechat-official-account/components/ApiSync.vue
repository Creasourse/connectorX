<template>
  <div class="config-card">
    <h2 class="config-title">API同步</h2>

    <el-form
      ref="formRef"
      :model="formData"
      label-position="top"
      class="config-form"
    >
      <!-- 同步对象 -->
      <el-form-item label="同步对象">
        <div class="sync-objects">
          <el-checkbox-group v-model="formData.syncObjects">
            <el-checkbox label="userInfo">用户信息（OpenID）</el-checkbox>
            <el-checkbox label="userTags">用户标签</el-checkbox>
            <el-checkbox label="material">素材管理（图文消息、图片、语音）</el-checkbox>
            <el-checkbox label="templateMessage">模板消息</el-checkbox>
            <el-checkbox label="autoReply">自动回复规则</el-checkbox>
            <el-checkbox label="menuConfig">菜单配置</el-checkbox>
          </el-checkbox-group>
        </div>
      </el-form-item>

      <!-- 同步频率 -->
      <el-form-item label="同步频率">
        <el-radio-group v-model="formData.syncFrequency">
          <el-radio label="manual">手动触发</el-radio>
          <el-radio label="realtime">实时同步（推荐）</el-radio>
          <el-radio label="hourly">每小时</el-radio>
          <el-radio label="daily">每天凌晨2点</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>

    <!-- 配置说明 -->
    <div class="permission-info">
      <div class="permission-title">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
        配置说明
      </div>
      <ul class="permission-list">
        <li>实时同步会在数据变更时自动同步至本系统</li>
        <li>手动触发需要点击下方按钮立即执行同步</li>
        <li>素材管理包含图文消息、图片、语音等多种类型</li>
        <li>同步频率可根据业务需求灵活调整</li>
      </ul>
    </div>

    <!-- 手动触发按钮 -->
    <div class="action-buttons">
      <el-button class="sync-btn" @click="handleSync">
        <el-icon class="btn-icon"><Refresh /></el-icon>
        立即同步
      </el-button>
      <el-button type="primary" class="save-btn" @click="handleSave"
        >保存</el-button
      >
    </div>
  </div>
</template>

<script setup lang="ts">
import { type PropType, ref, reactive, watch } from "vue";
import { ElMessage, type FormInstance } from "element-plus";
import { InfoFilled, Refresh } from "@element-plus/icons-vue";

const formRef = ref<FormInstance>();

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
  syncObjects: [] as string[],
  syncFrequency: "realtime"
});

// 监听 props.config 变化
watch(
  () => props.config,
  (newVal: any) => {
    if (newVal) {
      formData.syncObjects = newVal.syncObjects || [];
      formData.syncFrequency = newVal.syncFrequency || "realtime";
    }
  },
  { immediate: true, deep: true }
);

// 立即同步
const handleSync = async () => {
  if (formData.syncObjects.length === 0) {
    ElMessage.warning("请先选择同步对象");
    return;
  }

  try {
    // TODO: 调用同步接口
    ElMessage.success("同步任务已启动，请稍后查看同步结果");
  } catch (error) {
    ElMessage.error("同步失败，请稍后重试");
  }
};

// 保存配置
const handleSave = async () => {
  if (!formRef.value) return;

  try {
    if (formData.syncObjects.length === 0) {
      ElMessage.warning("请至少选择一个同步对象");
      return;
    }

    const { syncObjects, syncFrequency } = formData;
    const params = {
      syncObjects,
      syncFrequency
    };
    // TODO: 调用保存接口
    console.log("保存API同步配置:", params);
    ElMessage.success("保存成功");
    emit("update");
  } catch (error) {
    ElMessage.error("保存失败，请稍后重试");
  }
};
</script>

<style scoped lang="scss">
.config-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

  .config-title {
    margin: 0 0 24px;
    font-size: 16px;
    font-weight: 600;
    color: #333;
  }

  .config-form {
    max-width: 600px;

    :deep(.el-form-item__label) {
      font-size: 14px;
      color: #333;
      font-weight: 500;
    }

    .sync-objects {
      width: 100%;

      :deep(.el-checkbox-group) {
        display: flex;
        flex-direction: column;
        gap: 12px;
      }

      :deep(.el-checkbox) {
        margin-right: 0;
        font-size: 14px;
        color: #333;

        .el-checkbox__label {
          color: #333;
        }
      }
    }

    :deep(.el-radio-group) {
      display: flex;
      flex-direction: column;
      gap: 12px;
    }

    :deep(.el-radio) {
      margin-right: 0;
      font-size: 14px;
      color: #333;

      .el-radio__label {
        color: #333;
      }
    }
  }

  .permission-info {
    margin-top: 24px;
    padding: 16px;
    background-color: #f6ffed;
    border: 1px solid #b7eb8f;
    border-radius: 4px;

    .permission-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      font-weight: 600;
      color: #52c41a;
      margin-bottom: 12px;

      .info-icon {
        font-size: 16px;
      }
    }

    .permission-list {
      margin: 0;
      padding-left: 24px;

      li {
        font-size: 13px;
        color: #52c41a;
        line-height: 1.8;
        margin-bottom: 4px;

        &:last-child {
          margin-bottom: 0;
        }
      }
    }
  }

  .action-buttons {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 32px;

    .sync-btn {
      display: flex;
      align-items: center;
      gap: 6px;
      width: 100px;
      border: 1px solid #1890ff;
      color: #1890ff;
      background-color: #fff;

      &:hover {
        background-color: #e6f7ff;
      }

      .btn-icon {
        font-size: 14px;
      }
    }

    .save-btn {
      width: 80px;
      background-color: #1890ff;

      &:hover {
        background-color: #40a9ff;
      }
    }
  }
}
</style>
