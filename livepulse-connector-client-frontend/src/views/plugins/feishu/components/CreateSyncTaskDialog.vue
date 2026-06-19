<template>
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="70%"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="dialog-content">
      <!-- 1. 基本设置 -->
      <div class="config-section">
        <div class="section-header">
          <span class="section-number">1</span>
          <span class="section-title">基本设置</span>
        </div>

        <div class="form-grid">
          <div class="form-item">
            <label class="form-label">任务名称</label>
            <el-input
              v-model="formData.taskName"
              placeholder="新同步任务"
              class="custom-input"
            />
          </div>

          <div class="form-item" v-if="!props.bitableId">
            <label class="form-label">多维表格</label>
            <el-select
              v-model="formData.bitableId"
              placeholder="请选择多维表格"
              class="custom-select"
              :loading="loadingBitables"
              filterable
              @change="handleBitableChange"
            >
              <el-option
                v-for="bitable in bitableList"
                :key="bitable.value"
                :label="bitable.label"
                :value="bitable.value"
              />
            </el-select>
          </div>

          <div class="form-item">
            <label class="form-label">目标表格</label>
            <el-select
              v-model="formData.targetTable"
              placeholder="请选择目标表格"
              class="custom-select"
              :loading="loadingTables"
              filterable
              :disabled="!currentBitableId"
            >
              <el-option
                v-for="table in tableList"
                :key="table.value"
                :label="table.label"
                :value="table.value"
              />
            </el-select>
          </div>

          <div class="form-item full-width">
            <label class="form-label">同步方向</label>
            <el-radio-group v-model="formData.syncDirection" class="radio-group">
              <el-radio value="pull">仅从飞书拉取</el-radio>
              <el-radio value="push">仅向飞书推送</el-radio>
              <el-radio value="bidirectional">双向同步</el-radio>
            </el-radio-group>
          </div>

          <div class="form-item full-width">
            <el-checkbox v-model="formData.advancedFilter">
              条件同步（高级）
            </el-checkbox>
          </div>
        </div>
      </div>

      <!-- 2. 字段映射引擎 -->
      <div class="config-section">
        <div class="section-header">
          <span class="section-number">2</span>
          <span class="section-title">字段映射引擎</span>
        </div>

        <div class="mapping-options">
          <el-checkbox v-model="formData.smartMapping">
            启用智能字段匹配
          </el-checkbox>
          <el-select
            v-model="formData.mappingRules"
            class="rules-select"
            placeholder="映射规则库"
          >
            <el-option label="使用默认规则集" value="default" />
            <el-option label="自定义规则集" value="custom" />
          </el-select>
          <span class="field-count" v-if="formData.fieldMappings.length > 0">
            已加载 {{ formData.fieldMappings.length }} 个字段
          </span>
        </div>

        <div class="mapping-table">
          <div class="table-header">
            <span class="header-cell">源字段（飞书）</span>
            <span class="header-cell">目标字段（插件）</span>
            <span class="header-cell">转换规则</span>
          </div>
          <div class="table-body" v-loading="loadingFields">
            <div
              v-for="(mapping, index) in formData.fieldMappings"
              :key="index"
              class="table-row"
            >
              <span class="table-cell">{{ mapping.sourceField }}</span>
              <span class="table-cell">{{ mapping.targetField }}</span>
              <span class="table-cell">
                <el-button
                  type="primary"
                  link
                  size="small"
                  class="rule-link"
                  :class="mapping.ruleType"
                >
                  {{ mapping.rule }}
                </el-button>
              </span>
            </div>
            <div v-if="!loadingFields && formData.fieldMappings.length === 0" class="empty-state">
              请先选择目标表格
            </div>
          </div>
        </div>
      </div>

      <!-- 3. 实时更新策略 -->
      <div class="config-section">
        <div class="section-header">
          <div class="green-bar"></div>
          <span class="section-title">3. 实时更新策略</span>
        </div>

        <div class="form-grid">
          <div class="form-item full-width">
            <label class="form-label">更新检测方式</label>
            <el-select
              v-model="formData.updateDetection"
              placeholder="请选择更新检测方式"
              class="custom-select"
            >
              <el-option label="事件订阅 + 轮询互补" value="hybrid" />
              <el-option label="仅事件订阅" value="webhook" />
              <el-option label="仅定时轮询" value="polling" />
            </el-select>

            <div class="detection-options">
              <el-radio value="webhook" disabled>
                事件订阅（实时推送）- 延迟: &lt;1s
              </el-radio>
              <el-radio value="polling" disabled>
                定时轮询（兜底）- 间隔: 5分钟
              </el-radio>
            </div>
          </div>

          <div class="form-item full-width">
            <el-checkbox v-model="formData.incrementalSync">
              增量同步
            </el-checkbox>
            <el-select
              v-model="formData.incrementFields"
              class="inline-multiple-select"
              placeholder="选择增量同步字段"
              size="small"
              multiple
              collapse-tags
              collapse-tags-tooltip
              :disabled="!formData.incrementalSync"
            >
              <el-option
                v-for="field in dateFields"
                :key="field.fieldId"
                :label="field.fieldName"
                :value="field.fieldId"
              />
            </el-select>
            <el-select
              v-model="formData.firstSync"
              class="inline-select"
              placeholder="首次同步"
              size="small"
            >
              <el-option label="最近7天数据" value="7days" />
              <el-option label="最近30天数据" value="30days" />
              <el-option label="全部数据" value="all" />
            </el-select>
          </div>

          <div class="form-item full-width conflict-resolution">
            <label class="form-label warning">
              <el-icon><WarningFilled /></el-icon>
              冲突解决策略
            </label>
            <div class="conflict-selects">
              <el-select
                v-model="formData.recordConflict"
                placeholder="记录冲突"
                class="custom-select small"
              >
                <el-option label="以飞书为准" value="feishu" />
                <el-option label="以插件为准" value="plugin" />
                <el-option label="以最新更新时间为准" value="latest" />
              </el-select>
              <el-select
                v-model="formData.fieldConflict"
                placeholder="字段冲突"
                class="custom-select small"
              >
                <el-option label="合并处理" value="merge" />
                <el-option label="以飞书为准" value="feishu" />
                <el-option label="以插件为准" value="plugin" />
              </el-select>
              <el-select
                v-model="formData.timeWindow"
                placeholder="时间窗口"
                class="custom-select small"
              >
                <el-option label="5分钟内的冲突" value="5min" />
                <el-option label="10分钟内的冲突" value="10min" />
                <el-option label="30分钟内的冲突" value="30min" />
              </el-select>
            </div>
          </div>
        </div>
      </div>

      <!-- 4. 性能优化设置 -->
      <div class="config-section">
        <div class="section-header">
          <div class="purple-bar"></div>
          <span class="section-title">4. 性能优化设置</span>
        </div>

        <div class="performance-settings">
          <div class="setting-item">
            <label class="form-label">批量大小</label>
            <div class="input-with-unit">
              <el-input-number
                v-model="formData.batchSize"
                :min="1"
                :max="1000"
                class="custom-input-number"
              />
              <span class="unit">条/次</span>
            </div>
          </div>
          <div class="setting-item">
            <label class="form-label">批次间隔</label>
            <div class="input-with-unit">
              <el-input-number
                v-model="formData.batchInterval"
                :min="100"
                :max="10000"
                :step="100"
                class="custom-input-number"
              />
              <span class="unit">ms</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button
          v-if="props.taskId"
          :type="formData.isEnabled === 1 ? 'success' : 'warning'"
          @click="handleToggleEnabled"
          :loading="toggling"
        >
          {{ formData.isEnabled === 1 ? '已启用' : '已停用' }}
        </el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">
          {{ props.taskId ? '更新任务' : '创建任务' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { WarningFilled } from "@element-plus/icons-vue";
import {
  getFeishuDataTablePageList,
  getFeishuFieldPageList,
  saveOrUpdateFeishuDataSync,
  getFeishuBitablePageList,
  fetchFieldsFromFeishu,
  getFeishuDataSyncDetail,
  toggleFeishuDataSyncEnabled,
  getFeishuDataTableDetail
} from "@/api/feishu";

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  bitableId: {
    type: Number,
    default: undefined
  },
  taskId: {
    type: Number,
    default: undefined
  }
});

const emit = defineEmits<{
  (e: "update:modelValue", value: boolean): void;
  (e: "save", data: any): void;
}>();

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit("update:modelValue", value)
});

const dialogTitle = computed(() =>
  props.taskId ? "编辑同步任务" : "同步任务配置"
);

const saving = ref(false);
const toggling = ref(false);
const loadingTables = ref(false);
const loadingFields = ref(false);
const loadingBitables = ref(false);

// 当前使用的 bitableId (可能是传入的，也可能是用户选择的)
const currentBitableId = ref<number | undefined>(props.bitableId);

// 表单数据
const formData = reactive({
  taskName: "新同步任务",
  bitableId: undefined as number | undefined,
  targetTable: "",
  syncDirection: "bidirectional",
  advancedFilter: false,
  smartMapping: true,
  mappingRules: "default",
  updateDetection: "hybrid",
  incrementalSync: true,
  incrementalField: "updatedTime",
  incrementFields: [] as string[],
  firstSync: "7days",
  recordConflict: "feishu",
  fieldConflict: "merge",
  timeWindow: "5min",
  batchSize: 100,
  batchInterval: 1000,
  isEnabled: 1 as number,
  fieldMappings: [] as Array<{
    sourceField: string;
    targetField: string;
    rule: string;
    ruleType: string;
  }>
});

// 多维表格列表
const bitableList = ref<Array<{ label: string; value: number }>>([]);

// 表格列表
const tableList = ref<Array<{ label: string; value: number; tableId: string }>>([]);

// 字段列表
const fieldList = ref<Array<{
  id: string;
  fieldId: string;
  fieldName: string;
  fieldType: number;
  uiType?: string;
  isHidden?: number;
  property?: any;
  isRequired?: boolean;
  isUnique?: boolean;
  description?: string;
}>>([]);

// 当前选中表格的飞书 table_id
const currentFeishuTableId = ref<string>("");

// 日期类型字段列表（用于增量同步）
const dateFields = computed(() => {
  // 飞书字段类型：5-日期时间, 15-日期, 以及其他可能的日期类型
  // 也包括 createdTime, modifiedTime 等系统字段
  return fieldList.value.filter(field => {
    const fieldType = field.fieldType;
    // 日期时间类型：5 (dateTime), 15 (date)
    // 系统字段：createdTime, modifiedTime
    return fieldType === 5 || fieldType === 15 ||
           field.fieldName === '创建时间' ||
           field.fieldName === '修改时间' ||
           field.fieldName === '最后更新时间' ||
           field.uiType === 'dateTime' ||
           field.uiType === 'date';
  });
});

// 加载多维表格列表
const loadBitableList = async () => {
  loadingBitables.value = true;
  try {
    const res = await getFeishuBitablePageList({
      pageNum: 1,
      pageSize: 100,
      isEnabled: 1
    });

    if (res.data?.records) {
      bitableList.value = res.data.records.map((b: any) => ({
        label: `${b.bitableName || "未命名"} (${b.appToken})`,
        value: b.id
      }));
    }
  } catch (error) {
    console.error("加载多维表格列表失败:", error);
    ElMessage.error("加载多维表格列表失败");
  } finally {
    loadingBitables.value = false;
  }
};

// 加载表格列表
const loadTableList = async (bitableId: number) => {
  if (!bitableId) return;

  loadingTables.value = true;
  try {
    const res = await getFeishuDataTablePageList({
      pageNum: 1,
      pageSize: 100,
      feishuBitableId: bitableId,
      isEnabled: 1
    });

    if (res.data?.records) {
      tableList.value = res.data.records.map((t: any) => ({
        label: `${t.tableName} (${t.tableId})`,
        value: t.id,
        tableId: t.tableId
      }));
    }
  } catch (error) {
    console.error("加载表格列表失败:", error);
    ElMessage.error("加载表格列表失败");
  } finally {
    loadingTables.value = false;
  }
};

// 加载字段列表
const loadFieldList = async () => {
  if (!formData.targetTable) return;

  // 获取选中表格的 tableId
  const selectedTable = tableList.value.find(t => t.value === formData.targetTable);
  if (!selectedTable) {
    ElMessage.error("未找到选中的表格信息");
    return;
  }

  loadingFields.value = true;
  try {
    // 调用飞书实时接口获取字段列表
    const res = await fetchFieldsFromFeishu(selectedTable.tableId);

    console.log("飞书字段接口返回:", res);

    if (res.data) {
      fieldList.value = res.data.map((f: any) => ({
        fieldId: f.field_id,
        id: f.field_id,
        fieldName: f.field_name,
        fieldType: f.type,
        uiType: f.ui_type,
        isHidden: f.is_hidden ? 1 : 0,
        property: f.property,
        isRequired: f.required || false,
        isUnique: f.unique || false,
        description: f.description
      }));

      console.log("解析后的字段列表:", fieldList.value);

      // 更新字段映射
      updateFieldMappings();

      ElMessage.success(`成功加载 ${fieldList.value.length} 个字段`);
    }
  } catch (error) {
    console.error("加载字段列表失败:", error);
    ElMessage.error("加载字段列表失败");
  } finally {
    loadingFields.value = false;
  }
};

// 更新字段映射
const updateFieldMappings = () => {
  console.log("开始更新字段映射，字段列表:", fieldList.value);

  formData.fieldMappings = fieldList.value.map(field => {
    let rule = "文本直传";
    let ruleType = "text";

    // 飞书字段类型映射：type 是数字类型
    // 1: text, 2: number, 3: singleSelect, 4: multiSelect,
    // 5: dateTime, 11: phone, 15: url, 17: user, 18: group,
    // 19: attachment, 20: member, 21: dept, 23: lookup
    const fieldType = field.fieldType;

    if (fieldType === 11) {
      // 手机号
      rule = "手机号格式化";
      ruleType = "link";
    } else if (fieldType === 17 || fieldType === 20) {
      // 人员/成员
      rule = "人员→用户ID";
      ruleType = "purple";
    } else if (fieldType === 18 || fieldType === 21) {
      // 群组/部门
      rule = "群组→ID";
      ruleType = "purple";
    } else if (fieldType === 2) {
      // 数字
      rule = "数值转换";
      ruleType = "purple";
    } else if (fieldType === 5) {
      // 日期时间
      rule = "日期格式化";
      ruleType = "purple";
    } else if (fieldType === 15) {
      // URL
      rule = "URL链接";
      ruleType = "link";
    } else if (fieldType === 3 || fieldType === 4) {
      // 单选/多选
      rule = "选项映射";
      ruleType = "purple";
    } else if (fieldType === 23) {
      // 查找引用
      rule = "引用查询";
      ruleType = "purple";
    } else {
      // 文本和其他类型
      rule = "文本直传";
      ruleType = "text";
    }

    return {
      sourceField: field.fieldName,
      targetField: field.fieldName.toLowerCase().replace(/\s+/g, "_"),
      rule,
      ruleType
    };
  });

  console.log("字段映射更新完成:", formData.fieldMappings);
};

// 处理多维表格变化
const handleBitableChange = (bitableId: number) => {
  currentBitableId.value = bitableId;
  formData.targetTable = "";
  formData.fieldMappings = [];
  loadTableList(bitableId);
};

// 监听目标表格变化
watch(() => formData.targetTable, () => {
  if (formData.targetTable) {
    loadFieldList();
  }
});

// 监听弹窗打开
watch(dialogVisible, async (isOpen) => {
  if (isOpen) {
    if (props.taskId) {
      // 编辑模式：加载任务详情
      await loadTaskDetail(props.taskId);
    } else {
      // 新增模式：重置表单
      resetForm();
      if (props.bitableId) {
        currentBitableId.value = props.bitableId;
        loadTableList(props.bitableId);
      } else {
        currentBitableId.value = undefined;
        loadBitableList();
      }
    }
  }
});

// 加载任务详情
const loadTaskDetail = async (taskId: number) => {
  try {
    const res = await getFeishuDataSyncDetail(taskId);
    if (res.success && res.data) {
      const task = res.data;

      // 基本信息
      formData.taskName = task.syncName || "新同步任务";
      formData.targetTable = task.feishuDataTableId || "";
      formData.isEnabled = task.isEnabled ?? 1;

      // 同步配置
      formData.syncDirection = task.syncDirection || "bidirectional";
      formData.syncMode = task.syncMode || "incremental";

      // 解析同步模式
      if (task.syncMode === "incremental") {
        formData.incrementalSync = true;
      } else if (task.syncMode === "full") {
        formData.incrementalSync = false;
      }

      // 冲突解决策略（从后端字段映射或使用默认值）
      formData.recordConflict = task.recordConflict || "feishu";
      formData.fieldConflict = task.fieldConflict || "merge";
      formData.timeWindow = task.timeWindow || "5min";

      // 更新检测方式
      formData.updateDetection = task.updateDetection || "hybrid";

      // 增量同步字段
      formData.incrementalField = task.incrementalField || "updatedTime";
      formData.incrementFields = task.incrementFields || [];
      formData.firstSync = task.firstSync || "7days";

      // 如果有增量字段列表，确保加载字段信息后再设置
      if (task.incrementFields && task.incrementFields.length > 0) {
        formData.incrementFields = task.incrementFields; // 直接使用字段ID列表
      }

      // 性能设置
      formData.batchSize = task.batchSize || 100;
      formData.batchInterval = task.batchInterval || 1000;

      // 高级选项
      formData.advancedFilter = task.advancedFilter || false;
      formData.smartMapping = task.smartMapping !== undefined ? task.smartMapping : true;
      formData.mappingRules = task.mappingRules || "default";

      // 根据数据表ID获取所属的多维表格ID和表格信息
      if (task.feishuDataTableId) {
        // 查询数据表详情以获取 feishuBitableId 和 tableId
        const tableDetailRes = await getFeishuDataTableDetail(task.feishuDataTableId);

        if (tableDetailRes.success && tableDetailRes.data) {
          const targetTable = tableDetailRes.data;
          const feishuBitableId = targetTable.feishuBitableId;
          const feishuTableId = targetTable.tableId;

          currentBitableId.value = feishuBitableId;
          formData.bitableId = feishuBitableId;

          // 加载多维表格列表
          await loadBitableList();

          // 加载该多维表格下的所有表格
          await loadTableList(feishuBitableId);

          // 加载字段映射
          if (feishuTableId) {
            await loadFieldListForTable(feishuTableId);
          }
        }
      }

      console.log("任务详情加载完成，表单数据:", formData);
    }
  } catch (error) {
    console.error("加载任务详情失败:", error);
    ElMessage.error("加载任务详情失败");
  }
};

// 为指定表格加载字段列表（用于编辑模式）
const loadFieldListForTable = async (tableId: string) => {
  loadingFields.value = true;
  try {
    // 调用飞书实时接口获取字段列表
    const res = await fetchFieldsFromFeishu(tableId);

    if (res.data) {
      fieldList.value = res.data.map((f: any) => ({
        fieldId: f.field_id,
        id: f.field_id,
        fieldName: f.field_name,
        fieldType: f.type,
        uiType: f.ui_type,
        isHidden: f.is_hidden ? 1 : 0,
        property: f.property,
        isRequired: f.required || false,
        isUnique: f.unique || false,
        description: f.description
      }));

      // 更新字段映射
      updateFieldMappings();

      console.log("编辑模式 - 字段列表加载成功:", fieldList.value.length);
    }
  } catch (error) {
    console.error("编辑模式 - 加载字段列表失败:", error);
    // 编辑模式下字段加载失败不阻塞流程
  } finally {
    loadingFields.value = false;
  }
};

// 重置表单
const resetForm = () => {
  formData.taskName = "新同步任务";
  formData.bitableId = undefined;
  formData.targetTable = "";
  formData.isEnabled = 1;
  formData.fieldMappings = [];
};

onMounted(() => {
  if (props.bitableId) {
    currentBitableId.value = props.bitableId;
    loadTableList(props.bitableId);
  } else {
    loadBitableList();
  }
});

// 关闭弹窗
const handleClose = () => {
  dialogVisible.value = false;
};

// 保存配置
const handleSave = async () => {
  if (!formData.taskName) {
    ElMessage.warning("请输入任务名称");
    return;
  }
  if (!formData.targetTable) {
    ElMessage.warning("请选择目标表格");
    return;
  }

  saving.value = true;
  try {
    const selectedTable = tableList.value.find(t => t.value === formData.targetTable);

    // 构建保存参数（包含同步任务和字段信息）
    const saveParams: any = {
      id: props.taskId,
      feishuDataTableId: formData.targetTable,
      syncName: formData.taskName,
      syncType: "full",
      syncDirection: formData.syncDirection,
      syncMode: formData.incrementalSync ? "incremental" : "full",
      cronExpression: "",
      isEnabled: formData.isEnabled,

      // 同步策略配置
      updateDetection: formData.updateDetection,
      incrementalSync: formData.incrementalSync,
      incrementalField: formData.incrementalField,
      incrementFields: formData.incrementFields.length > 0 ? formData.incrementFields.map(fieldId => {
        const field = fieldList.value.find(f => f.fieldId === fieldId);
        return {
          increField: fieldId,
          increFieldName: field ? field.fieldName : fieldId
        };
      }) : [],
      firstSync: formData.firstSync,

      // 冲突解决策略
      recordConflict: formData.recordConflict,
      fieldConflict: formData.fieldConflict,
      timeWindow: formData.timeWindow,

      // 性能配置
      batchSize: formData.batchSize,
      batchInterval: formData.batchInterval,

      // 高级选项
      advancedFilter: formData.advancedFilter,
      smartMapping: formData.smartMapping,
      mappingRules: formData.mappingRules,

      // 字段信息（同时保存到 feishu_field 表）
      fields: fieldList.value.map((field, index) => ({
        fieldId: field.fieldId,
        fieldName: field.fieldName,
        fieldType: String(field.fieldType),
        uiType: field.uiType,
        isHidden: field.isHidden ?? 0,
        property: field.property ? JSON.stringify(field.property) : undefined,
        isRequired: field.isRequired ? 1 : 0,
        isUnique: field.isUnique ? 1 : 0,
        description: field.description,
        sortOrder: index
      }))
    };

    console.log("保存参数（包含字段信息）:", saveParams);

    // 一次性保存：同步任务 + 字段信息（在同一个事务中）
    await saveOrUpdateFeishuDataSync(saveParams);

    emit("save", {
      id: props.taskId,
      name: formData.taskName,
      tableName: selectedTable?.label || "",
      direction: formData.syncDirection,
      syncTime: "即将开始"
    });

    dialogVisible.value = false;
    ElMessage.success(props.taskId ? "同步任务更新成功" : "同步任务创建成功");
  } catch (error) {
    console.error("保存失败:", error);
    ElMessage.error("保存失败");
  } finally {
    saving.value = false;
  }
};

// 切换启用状态
const handleToggleEnabled = async () => {
  if (!props.taskId) return;

  toggling.value = true;
  try {
    const res = await toggleFeishuDataSyncEnabled(props.taskId);
    if (res.success) {
      formData.isEnabled = formData.isEnabled === 1 ? 0 : 1;
      ElMessage.success(formData.isEnabled === 1 ? "任务已启用" : "任务已停用");
    } else {
      ElMessage.error(res.msg || "操作失败");
    }
  } catch (error) {
    console.error("切换启用状态失败:", error);
    ElMessage.error("操作失败");
  } finally {
    toggling.value = false;
  }
};
</script>

<style lang="scss" scoped>
.dialog-content {
  max-height: 60vh;
  overflow-y: auto;
  padding: 0 8px;
}

.config-section {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #e5e7eb;

  &:last-of-type {
    border-bottom: none;
    margin-bottom: 0;
  }

  .section-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;
    position: relative;

    .green-bar,
    .purple-bar {
      position: absolute;
      left: -16px;
      width: 4px;
      height: 24px;
      border-radius: 2px;
    }

    .green-bar {
      background: #00b894;
    }

    .purple-bar {
      background: #6c5ce7;
    }

    .section-number {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 28px;
      height: 28px;
      background: #6c5ce7;
      color: #fff;
      border-radius: 50%;
      font-size: 14px;
      font-weight: 600;
    }

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: #2d3748;
    }
  }

  .form-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;

    .form-item {
      &.full-width {
        grid-column: 1 / -1;
      }

      .form-label {
        display: block;
        font-size: 14px;
        font-weight: 500;
        color: #374151;
        margin-bottom: 8px;

        &.warning {
          display: flex;
          align-items: center;
          gap: 6px;
          color: #f59e0b;

          .el-icon {
            font-size: 16px;
          }
        }
      }

      :deep(.custom-input),
      :deep(.custom-select) {
        width: 100%;

        .el-input__wrapper {
          background: #f8f9fa;
          border: 1px solid #e9ecef;
          box-shadow: none;
          border-radius: 6px;

          &:hover {
            border-color: #6c5ce7;
          }
        }
      }

      .radio-group {
        display: flex;
        flex-direction: column;
        gap: 12px;

        :deep(.el-radio) {
          margin-right: 0;
        }
      }

      .detection-options {
        display: flex;
        flex-direction: column;
        gap: 8px;
        margin-top: 8px;

        :deep(.el-radio) {
          margin-right: 0;
          color: #6b7280;
          font-size: 13px;
        }
      }

      .inline-select {
        margin-left: 12px;
        width: 180px;
      }

      .inline-multiple-select {
        margin-left: 12px;
        width: 300px;
      }
    }
  }

  .mapping-options {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 16px;

    .rules-select {
      width: 200px;

      :deep(.el-input__wrapper) {
        background: #f8f9fa;
        border: 1px solid #e9ecef;
        box-shadow: none;
      }
    }

    .field-count {
      margin-left: auto;
      font-size: 13px;
      color: #00b894;
      font-weight: 500;
    }
  }

  .mapping-table {
    border: 1px solid #e5e7eb;
    border-radius: 6px;
    overflow: hidden;

    .table-header {
      display: grid;
      grid-template-columns: 1fr 1fr 1fr;
      background: #f8f9fa;
      border-bottom: 1px solid #e5e7eb;

      .header-cell {
        padding: 12px 16px;
        font-size: 13px;
        font-weight: 600;
        color: #374151;
        text-align: center;

        &:not(:last-child) {
          border-right: 1px solid #e5e7eb;
        }
      }
    }

    .table-body {
      .empty-state {
        padding: 32px 16px;
        text-align: center;
        color: #9ca3af;
        font-size: 13px;
      }

      .table-row {
        display: grid;
        grid-template-columns: 1fr 1fr 1fr;
        border-bottom: 1px solid #f3f4f6;

        &:last-child {
          border-bottom: none;
        }

        .table-cell {
          padding: 12px 16px;
          font-size: 13px;
          color: #6b7280;
          text-align: center;

          &:not(:last-child) {
            border-right: 1px solid #f3f4f6;
          }

          .rule-link {
            font-size: 12px;
            font-weight: 500;

            &.link {
              color: #3b82f6;
            }

            &.purple {
              color: #6c5ce7;
            }
          }
        }
      }
    }
  }

  .conflict-resolution {
    background: #fffbeb;
    padding: 16px;
    border-radius: 6px;
    border: 1px solid #fbbf24;

    .conflict-selects {
      display: flex;
      gap: 12px;
      margin-top: 12px;

      .custom-select {
        flex: 1;

        &.small {
          :deep(.el-input__wrapper) {
            background: #fff;
            border: 1px solid #e9ecef;
          }
        }
      }
    }
  }

  .performance-settings {
    display: flex;
    gap: 32px;

    .setting-item {
      flex: 1;

      .form-label {
        display: block;
        font-size: 14px;
        font-weight: 500;
        color: #374151;
        margin-bottom: 8px;
      }

      .input-with-unit {
        display: flex;
        align-items: center;
        gap: 8px;

        :deep(.custom-input-number) {
          flex: 1;

          .el-input__wrapper {
            background: #f8f9fa;
            border: 1px solid #e9ecef;
            box-shadow: none;
            border-radius: 6px;

            &:hover {
              border-color: #6c5ce7;
            }
          }
        }

        .unit {
          font-size: 13px;
          color: #6b7280;
          font-weight: 500;
        }
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;

  .el-button {
    padding: 10px 24px;
    border-radius: 6px;
    font-weight: 500;

    &.el-button--primary {
      background: #6c5ce7;
      border-color: #6c5ce7;

      &:hover {
        background: #5a4bd1;
      }
    }
  }
}

:deep(.el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;

  .el-dialog__title {
    font-size: 18px;
    font-weight: 600;
    color: #2d3748;
  }
}

:deep(.el-dialog__body) {
  padding: 24px;
}

:deep(.el-dialog__footer) {
  padding: 16px 24px;
}
</style>
