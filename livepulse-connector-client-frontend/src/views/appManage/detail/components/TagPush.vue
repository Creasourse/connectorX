<template>
  <div class="tag-push-page">
    <div class="config-card">
      <h2 class="config-title">标签推送配置</h2>
      <p>请联系管理员购买专业版</p>

      <!-- 推送策略配置 -->
      <!-- <div class="push-strategy">
        <div class="config-section-title">推送策略</div>
        <el-form :model="strategyForm" label-width="140px" class="strategy-form">
          <el-form-item label="推送方式">
            <el-radio-group v-model="strategyForm.pushMode">
              <el-radio label="manual">手动推送</el-radio>
              <el-radio label="auto">自动推送</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="推送频率" v-if="strategyForm.pushMode === 'auto'">
            <el-select
              v-model="strategyForm.frequency"
              placeholder="请选择推送频率"
              style="width: 200px"
            >
              <el-option label="每小时" value="hourly" />
              <el-option label="每天" value="daily" />
              <el-option label="每周" value="weekly" />
              <el-option label="每月" value="monthly" />
            </el-select>
          </el-form-item>

          <el-form-item label="推送时间" v-if="strategyForm.pushMode === 'auto'">
            <el-time-picker
              v-model="strategyForm.pushTime"
              format="HH:mm"
              value-format="HH:mm"
              placeholder="选择推送时间"
            />
          </el-form-item>
        </el-form>
      </div> -->

      <!-- 标签映射配置 -->
      <!-- <div class="tag-mapping">
        <div class="mapping-header">
          <div class="config-section-title">标签映射配置</div>
          <el-button type="primary" @click="handleAddMapping">
            添加映射
          </el-button>
        </div>

        <el-table :data="mappingList" class="mapping-table" border>
          <el-table-column type="index" label="编号" width="60" align="center" />
          <el-table-column prop="tagName" label="企业微信标签" min-width="150" />
          <el-table-column prop="mappedTag" label="映射标签" min-width="150">
            <template #default="{ row }">
              <el-select
                v-model="row.mappedTag"
                placeholder="选择映射标签"
                filterable
                allow-create
              >
                <el-option
                  v-for="tag in availableTags"
                  :key="tag.id"
                  :label="tag.name"
                  :value="tag.name"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="说明" min-width="200" />
          <el-table-column label="操作" width="100" align="center">
            <template #default="{ row, $index }">
              <el-button type="danger" link @click="handleDeleteMapping($index)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div> -->

      <!-- 推送目标配置 -->
      <!-- <div class="push-target">
        <div class="config-section-title">推送目标配置</div>
        <el-form :model="targetForm" label-width="140px" class="target-form">
          <el-form-item label="推送系统">
            <el-checkbox-group v-model="targetForm.targetSystems">
              <el-checkbox label="cdp">CDP系统</el-checkbox>
              <el-checkbox label="crm">CRM系统</el-checkbox>
              <el-checkbox label="marketing">营销系统</el-checkbox>
            </el-checkbox-group>
          </el-form-item>

          <el-form-item label="推送字段">
            <el-checkbox-group v-model="targetForm.pushFields">
              <el-checkbox label="userid">用户ID</el-checkbox>
              <el-checkbox label="name">姓名</el-checkbox>
              <el-checkbox label="mobile">手机号</el-checkbox>
              <el-checkbox label="department">部门</el-checkbox>
              <el-checkbox label="position">职位</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-form>
      </div> -->

      <!-- 同步规则说明 -->
      <!-- <div class="sync-rules">
        <div class="config-section-title">同步规则说明</div>
        <ul class="rules-list">
          <li>标签推送将企业微信中的标签信息同步到指定的目标系统</li>
          <li>自动推送模式下，系统将按照设定的频率和时间自动执行推送任务</li>
          <li>手动推送模式下，需要用户手动触发推送操作</li>
          <li>标签映射配置用于将企业微信标签映射到目标系统的标签</li>
          <li>推送字段决定了同步到目标系统的数据内容</li>
        </ul>
      </div> -->
    </div>

    <!-- 底部操作按钮 -->
    <!-- <div class="action-buttons">
      <el-button @click="handleTestPush">测试推送</el-button>
      <el-button @click="handleManualPush" :disabled="strategyForm.pushMode === 'auto'">
        立即推送
      </el-button>
      <el-button type="primary" @click="handleSaveConfig">保存配置</el-button>
    </div> -->

    <!-- 添加映射对话框 -->
    <!-- <el-dialog v-model="addDialogVisible" title="添加标签映射" width="600px">
      <el-form :model="addForm" label-width="120px">
        <el-form-item label="企业微信标签">
          <el-select v-model="addForm.wecomTag" placeholder="选择企业微信标签">
            <el-option
              v-for="tag in wecomTags"
              :key="tag.id"
              :label="tag.name"
              :value="tag.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="映射标签">
          <el-select
            v-model="addForm.mappedTag"
            placeholder="选择或输入映射标签"
            filterable
            allow-create
          >
            <el-option
              v-for="tag in availableTags"
              :key="tag.id"
              :label="tag.name"
              :value="tag.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="说明">
          <el-input
            v-model="addForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入映射说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmAdd">确定</el-button>
      </template>
    </el-dialog> -->
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { ElMessage } from "element-plus";

// 推送策略表单
const strategyForm = ref({
  pushMode: "manual",
  frequency: "daily",
  pushTime: ""
});

// 标签映射列表
const mappingList = ref([
  {
    id: 1,
    tagName: "重要客户",
    mappedTag: "",
    description: "标记重要客户标签"
  },
  {
    id: 2,
    tagName: "潜在客户",
    mappedTag: "",
    description: "标记潜在客户标签"
  },
  {
    id: 3,
    tagName: "已成交",
    mappedTag: "",
    description: "标记已成交客户标签"
  }
]);

// 可用的映射标签
const availableTags = ref([
  { id: 1, name: "VIP客户" },
  { id: 2, name: "普通客户" },
  { id: 3, name: "新客户" },
  { id: 4, name: "活跃客户" },
  { id: 5, name: "沉睡客户" }
]);

// 企业微信标签列表
const wecomTags = ref([
  { id: 1, name: "重要客户" },
  { id: 2, name: "潜在客户" },
  { id: 3, name: "已成交" },
  { id: 4, name: "待跟进" },
  { id: 5, name: "已流失" },
  { id: 6, name: "高意向" }
]);

// 推送目标配置
const targetForm = ref({
  targetSystems: [],
  pushFields: []
});

// 添加映射对话框
const addDialogVisible = ref(false);
const addForm = ref({
  wecomTag: "",
  mappedTag: "",
  description: ""
});

// 添加映射
const handleAddMapping = () => {
  addForm.value = {
    wecomTag: "",
    mappedTag: "",
    description: ""
  };
  addDialogVisible.value = true;
};

// 确认添加
const handleConfirmAdd = () => {
  if (!addForm.value.wecomTag || !addForm.value.mappedTag) {
    ElMessage.warning("请完整填写映射信息");
    return;
  }

  mappingList.value.push({
    id: Date.now(),
    tagName: addForm.value.wecomTag,
    mappedTag: addForm.value.mappedTag,
    description: addForm.value.description
  });

  addDialogVisible.value = false;
  ElMessage.success("标签映射添加成功");
};

// 删除映射
const handleDeleteMapping = (index: number) => {
  mappingList.value.splice(index, 1);
  ElMessage.success("标签映射删除成功");
};

// 测试推送
const handleTestPush = () => {
  // TODO: 调用测试推送接口
  ElMessage.success("测试推送已发送");
};

// 立即推送
const handleManualPush = () => {
  // TODO: 调用手动推送接口
  ElMessage.success("推送任务已启动");
};

// 保存配置
const handleSaveConfig = () => {
  // TODO: 调用保存配置接口
  ElMessage.success("标签推送配置已保存");
};
</script>

<style scoped lang="scss">
.tag-push-page {
  .config-card {
    background-color: #fff;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
    margin-bottom: 16px;

    .config-title {
      margin: 0 0 24px;
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }

    .config-section-title {
      font-size: 14px;
      font-weight: 600;
      color: #333;
      margin-bottom: 16px;
      padding-left: 8px;
      border-left: 3px solid #1890ff;
    }

    .push-strategy {
      margin-bottom: 32px;

      .strategy-form {
        :deep(.el-form-item__label) {
          font-size: 14px;
          color: #333;
          font-weight: 500;
        }

        :deep(.el-radio__label),
        :deep(.el-checkbox__label) {
          font-size: 14px;
          color: #666;
        }
      }
    }

    .tag-mapping {
      margin-bottom: 32px;

      .mapping-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        .config-section-title {
          margin-bottom: 0;
        }
      }

      .mapping-table {
        width: 100%;

        :deep(.el-table__header) {
          th {
            background-color: #fafafa;
            color: #333;
            font-weight: 600;
          }
        }

        :deep(.el-table__body) {
          td {
            color: #666;
          }
        }

        :deep(.el-select) {
          width: 100%;
        }
      }
    }

    .push-target {
      margin-bottom: 32px;

      .target-form {
        :deep(.el-form-item__label) {
          font-size: 14px;
          color: #333;
          font-weight: 500;
        }

        :deep(.el-checkbox__label) {
          font-size: 14px;
          color: #666;
        }

        :deep(.el-checkbox-group) {
          display: flex;
          flex-wrap: wrap;
          gap: 16px;
        }
      }
    }

    .sync-rules {
      padding: 16px;
      background-color: #f6ffed;
      border: 1px solid #b7eb8f;
      border-radius: 4px;

      .config-section-title {
        border-left-color: #52c41a;
        margin-bottom: 12px;
      }

      .rules-list {
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
  }

  .action-buttons {
    display: flex;
    justify-content: flex-end;
    gap: 12px;

    button {
      min-width: 100px;
    }
  }
}
</style>
