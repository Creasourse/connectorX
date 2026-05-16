<template>
  <div class="config-card">
    <h2 class="config-title">插件基础配置</h2>

    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-position="top"
      class="config-form"
    >
      <el-form-item label="企业名称" prop="companyName">
        <el-input
          v-model="formData.companyName"
          placeholder="请填写企业名称"
          clearable
        />
      </el-form-item>

      <el-form-item label="企业ID (CorpID)" prop="corpId">
        <el-input
          v-model="formData.corpId"
          placeholder="请在企业微信后台「我的企业」中获取"
          clearable
        />
      </el-form-item>

      <el-form-item label="企业Secret (corpSecret)" prop="corpSecret">
        <el-input
          v-model="formData.corpSecret"
          type="password"
          placeholder="请在企业微信后台「应用管理」中获取"
          clearable
          show-password
        />
      </el-form-item>

      <el-form-item label="应用AgentId" prop="agentId">
        <el-input
          v-model="formData.agentId"
          placeholder="请在企业微信后台创建应用后获取"
          clearable
        />
      </el-form-item>
    </el-form>

    <!-- 权限配置说明 -->
    <div class="permission-info">
      <div class="permission-title">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
        权限配置说明
      </div>
      <ul class="permission-list">
        <li>企业微信后台需开启「API可读」权限</li>
        <li>需激活「客户联系」功能并配置相关权限</li>
        <li>需创建通讯录同步应用并获取AgentId</li>
        <li>Secret信息请妥善保管，泄露可能导致数据安全问题</li>
      </ul>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button class="test-btn" @click="handleTest">测试</el-button>
      <el-button type="primary" class="save-btn" @click="handleSave"
        >保存</el-button
      >
    </div>
  </div>
</template>

<script setup lang="ts">
import { type PropType, ref, reactive, watch } from "vue";
import { saveOrUpdateCorp } from "@/api/wechat";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import { InfoFilled } from "@element-plus/icons-vue";

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
  wecomCorpId: "",
  corpId: "",
  corpSecret: "",
  companyName: "",
  agentId: ""
});

// 监听 props.config 变化
watch(
  () => props.config,
  (newVal: any) => {
    if (newVal) {
      // 逐个赋值以触发响应式更新
      formData.wecomCorpId = newVal.wecomCorpId || "";
      formData.corpId = newVal.corpId || "";
      formData.corpSecret = newVal.corpSecret || "";
      formData.companyName = newVal.companyName || "";
      formData.agentId = newVal.agentId || "";
    }
  },
  { immediate: true, deep: true }
);

// 表单验证规则
const formRules: FormRules = {
  corpId: [{ required: true, message: "请输入企业ID", trigger: "blur" }],
  corpSecret: [
    { required: true, message: "请输入企业Secret", trigger: "blur" }
  ],
  companyName: [{ required: true, message: "请输入企业名称", trigger: "blur" }],
  agentId: [{ required: true, message: "请输入应用AgentId", trigger: "blur" }]
};

// 测试配置
const handleTest = async () => {
  if (!formRef.value) return;

  // try {
  //   await formRef.value.validate();
  //   // TODO: 调用测试接口
  //   ElMessage.success("配置测试成功");
  // } catch (error) {
  //   ElMessage.error("请先完善必填项");
  // }
};

// 保存配置
const handleSave = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    const { corpId, corpSecret, companyName, agentId } = formData;
    const params = {
      corpId,
      corpSecret,
      companyName,
      logoUrl: "",
      agents: [
        {
          wecomCorpAgentId: agentId,
          wecomeCorpId: null,
          agentSecret: ""
        }
      ]
    };
    saveOrUpdateCorp(params).then(res => {
      if (res.success) {
        ElMessage.success("保存成功");
        emit("update");
      } else {
        ElMessage.error(res.errorMsg);
      }
    });
    // TODO: 调用保存接口
  } catch (error) {
    ElMessage.error("请先完善必填项");
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

    .test-btn {
      width: 80px;
      border: 1px solid #1890ff;
      color: #1890ff;
      background-color: #fff;

      &:hover {
        background-color: #e6f7ff;
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
