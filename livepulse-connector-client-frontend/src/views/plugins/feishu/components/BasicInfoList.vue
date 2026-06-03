<template>
  <div class="basic-info-list-container">
    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">飞书应用列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增应用
          </el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <div class="search-form">
        <el-form :model="searchForm" :inline="true">
          <el-form-item label="应用名称">
            <el-input
              v-model="searchForm.appName"
              placeholder="请输入应用名称"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="App ID">
            <el-input
              v-model="searchForm.appId"
              placeholder="请输入App ID"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="企业名称">
            <el-input
              v-model="searchForm.companyName"
              placeholder="请输入企业名称"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select
              v-model="searchForm.isEnabled"
              placeholder="请选择状态"
              clearable
              style="width: 120px"
            >
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        border
        style="width: 100%"
      >
        <el-table-column prop="appName" label="应用名称" min-width="150" />
        <el-table-column prop="appId" label="App ID" min-width="200" />
        <el-table-column prop="appSecret" label="App Secret" min-width="200">
          <template #default="{ row }">
            <el-text type="info">{{ row.appSecret }}</el-text>
          </template>
        </el-table-column>
        <el-table-column prop="companyName" label="企业名称" min-width="150" />
        <el-table-column prop="appToken" label="App Token" min-width="180">
          <template #default="{ row }">
            <span v-if="row.appToken">{{ row.appToken }}</span>
            <el-text v-else type="info">未配置</el-text>
          </template>
        </el-table-column>
        <el-table-column prop="isEnabled" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isEnabled === 1 ? 'success' : 'info'">
              {{ row.isEnabled === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            <span v-if="row.createTime">{{ formatTime(row.createTime) }}</span>
            <el-text v-else type="info">-</el-text>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              @click="handleView(row)"
            >
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button
              type="primary"
              size="small"
              link
              @click="handleEdit(row)"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              type="primary"
              size="small"
              link
              @click="handleToggleEnabled(row)"
            >
              <el-icon><Switch /></el-icon>
              {{ row.isEnabled === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-popconfirm
              title="确定删除该应用吗？"
              confirm-button-text="确定"
              cancel-button-text="取消"
              @confirm="handleDelete(row)"
            >
              <template #reference>
                <el-button type="danger" size="small" link :loading="row._deleting">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="应用详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="应用名称">
          {{ currentRow?.appName }}
        </el-descriptions-item>
        <el-descriptions-item label="App ID">
          {{ currentRow?.appId }}
        </el-descriptions-item>
        <el-descriptions-item label="App Secret">
          {{ currentRow?.appSecret }}
        </el-descriptions-item>
        <el-descriptions-item label="企业名称">
          {{ currentRow?.companyName }}
        </el-descriptions-item>
        <el-descriptions-item label="App Token">
          {{ currentRow?.appToken || '未配置' }}
        </el-descriptions-item>
        <el-descriptions-item label="应用描述">
          {{ currentRow?.description || '无' }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentRow?.isEnabled === 1 ? 'success' : 'info'">
            {{ currentRow?.isEnabled === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ currentRow?.createTime ? formatTime(currentRow.createTime) : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间">
          {{ currentRow?.updateTime ? formatTime(currentRow.updateTime) : '-' }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="editVisible"
      :title="isEdit ? '编辑应用' : '新增应用'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-form-item label="应用名称" prop="appName">
          <el-input
            v-model="formData.appName"
            placeholder="请输入应用名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="App ID" prop="appId">
          <el-input
            v-model="formData.appId"
            placeholder="请输入App ID"
            clearable
          />
        </el-form-item>
        <el-form-item label="App Secret" prop="appSecret">
          <el-input
            v-model="formData.appSecret"
            type="password"
            placeholder="请输入App Secret"
            clearable
            show-password
          />
        </el-form-item>
        <el-form-item label="企业名称" prop="companyName">
          <el-input
            v-model="formData.companyName"
            placeholder="请输入企业名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="App Token" prop="appToken">
          <el-input
            v-model="formData.appToken"
            placeholder="请输入App Token（可选）"
            clearable
          />
        </el-form-item>
        <el-form-item label="应用描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入应用描述"
          />
        </el-form-item>
        <el-form-item label="是否启用" prop="isEnabled">
          <el-switch
            v-model="formData.isEnabled"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import {
  getFeishuAppPageList,
  getFeishuAppDetail,
  saveOrUpdateFeishuApp,
  deleteFeishuApp,
  toggleFeishuAppEnabled
} from "@/api/feishu";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import {
  Plus,
  Search,
  Refresh,
  View,
  Edit,
  Delete,
  Switch
} from "@element-plus/icons-vue";

defineOptions({
  name: "BasicInfoList"
});

const loading = ref(false);
const saving = ref(false);
const tableData = ref<any[]>([]);
const currentRow = ref<any>(null);

// 搜索表单
const searchForm = reactive({
  appName: "",
  appId: "",
  companyName: "",
  isEnabled: undefined as number | undefined
});

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
});

// 详情对话框
const detailVisible = ref(false);

// 编辑对话框
const editVisible = ref(false);
const isEdit = ref(false);
const formRef = ref<FormInstance>();

// 表单数据
const formData = reactive({
  id: undefined as number | undefined,
  appName: "",
  appId: "",
  appSecret: "",
  companyName: "",
  appToken: "",
  description: "",
  isEnabled: 1
});

// 表单验证规则
const formRules: FormRules = {
  appName: [{ required: true, message: "请输入应用名称", trigger: "blur" }],
  appId: [{ required: true, message: "请输入App ID", trigger: "blur" }],
  appSecret: [{ required: true, message: "请输入App Secret", trigger: "blur" }],
  companyName: [{ required: true, message: "请输入企业名称", trigger: "blur" }]
};

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return "";
  return new Date(time).toLocaleString("zh-CN");
};

// 加载数据
const loadData = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    };
    const res = await getFeishuAppPageList(params);
    if (res.success) {
      tableData.value = res.data?.records || [];
      pagination.total = res.data?.total || 0;
    } else {
      ElMessage.error(res.msg || "加载数据失败");
    }
  } catch (error) {
    ElMessage.error("加载数据失败");
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1;
  loadData();
};

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    appName: "",
    appId: "",
    companyName: "",
    isEnabled: undefined
  });
  pagination.pageNum = 1;
  loadData();
};

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.pageSize = size;
  loadData();
};

// 当前页改变
const handleCurrentChange = (page: number) => {
  pagination.pageNum = page;
  loadData();
};

// 新增
const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, {
    id: undefined,
    appName: "",
    appId: "",
    appSecret: "",
    companyName: "",
    appToken: "",
    description: "",
    isEnabled: 1
  });
  editVisible.value = true;
};

// 查看
const handleView = async (row: any) => {
  try {
    const res = await getFeishuAppDetail(row.id);
    if (res.success) {
      currentRow.value = res.data;
      detailVisible.value = true;
    } else {
      ElMessage.error(res.msg || "获取详情失败");
    }
  } catch (error) {
    ElMessage.error("获取详情失败");
  }
};

// 编辑
const handleEdit = async (row: any) => {
  try {
    const res = await getFeishuAppDetail(row.id);
    if (res.success) {
      isEdit.value = true;
      Object.assign(formData, {
        id: res.data.id,
        appName: res.data.appName || "",
        appId: res.data.appId || "",
        appSecret: res.data.appSecret || "",
        companyName: res.data.companyName || "",
        appToken: res.data.appToken || "",
        description: res.data.description || "",
        isEnabled: res.data.isEnabled ?? 1
      });
      editVisible.value = true;
    } else {
      ElMessage.error(res.msg || "获取详情失败");
    }
  } catch (error) {
    ElMessage.error("获取详情失败");
  }
};

// 保存
const handleSave = async () => {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  saving.value = true;
  try {
    const params = {
      id: formData.id,
      appName: formData.appName,
      appId: formData.appId,
      appSecret: formData.appSecret,
      companyName: formData.companyName,
      appToken: formData.appToken,
      description: formData.description,
      isEnabled: formData.isEnabled
    };
    const res = await saveOrUpdateFeishuApp(params);
    if (res.success) {
      ElMessage.success(isEdit.value ? "更新成功" : "创建成功");
      editVisible.value = false;
      loadData();
    } else {
      ElMessage.error(res.msg || "保存失败");
    }
  } catch (error) {
    ElMessage.error("保存失败");
  } finally {
    saving.value = false;
  }
};

// 切换启用状态
const handleToggleEnabled = async (row: any) => {
  try {
    const res = await toggleFeishuAppEnabled(row.id);
    if (res.success) {
      ElMessage.success("状态更新成功");
      loadData();
    } else {
      ElMessage.error(res.msg || "状态更新失败");
    }
  } catch (error) {
    ElMessage.error("状态更新失败");
  }
};

// 删除
const handleDelete = async (row: any) => {
  try {
    const res = await deleteFeishuApp(row.id);
    if (res.success) {
      ElMessage.success("删除成功");
      if (tableData.value.length === 1 && pagination.pageNum > 1) {
        pagination.pageNum--;
      }
      loadData();
    } else {
      ElMessage.error(res.msg || "删除失败");
    }
  } catch (error) {
    ElMessage.error("删除失败");
  }
};

onMounted(() => {
  loadData();
});
</script>

<style lang="scss" scoped>
.basic-info-list-container {
  width: 100%;
}

.list-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  :deep(.el-card__header) {
    padding: 16px 20px;
    border-bottom: 1px solid #e5e7eb;
  }

  :deep(.el-card__body) {
    padding: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }

  .search-form {
    margin-bottom: 20px;
    padding: 16px;
    background: #f5f7fa;
    border-radius: 4px;
  }

  .pagination-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
}
</style>
