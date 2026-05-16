<template>
  <div class="config-card">
    <h2 class="config-title">多账户管理</h2>

    <!-- 账户列表表格 -->
    <div class="account-table">
      <div class="table-header">
        <h3 class="table-title">账户列表</h3>
        <el-button type="primary" @click="handleAdd">
          <el-icon class="btn-icon"><Plus /></el-icon>
          添加账户
        </el-button>
      </div>
      <el-table :data="accountList" style="width: 100%">
        <el-table-column prop="accountName" label="账户名称" width="250" />
        <el-table-column prop="accountId" label="账户ID" width="200" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 'enabled' ? 'success' : 'info'">
              {{ row.status === "enabled" ? "启用" : "停用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 配置说明 -->
    <div class="permission-info">
      <div class="permission-title">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
        配置说明
      </div>
      <ul class="permission-list">
        <li>支持添加多个Facebook广告账户进行统一管理</li>
        <li>可对账户进行编辑、启用、停用和删除操作</li>
        <li>停用的账户将不会同步数据和使用API</li>
        <li>建议定期检查账户状态确保数据正常同步</li>
      </ul>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" class="save-btn" @click="handleSave"
        >保存</el-button
      >
    </div>
  </div>
</template>

<script setup lang="ts">
import { type PropType, ref, reactive, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { InfoFilled, Plus } from "@element-plus/icons-vue";

interface AccountItem {
  id: string;
  accountName: string;
  accountId: string;
  status: "enabled" | "disabled";
}

const props = defineProps({
  config: {
    type: Object as PropType<unknown>
  }
});

const emit = defineEmits<{
  (e: "update"): void;
}>();

// 账户列表
const accountList = ref<AccountItem[]>([
  {
    id: "1",
    accountName: "Facebook广告账户1",
    accountId: "2345678901",
    status: "enabled"
  },
  {
    id: "2",
    accountName: "Facebook广告账户2",
    accountId: "2345678902",
    status: "enabled"
  },
  {
    id: "3",
    accountName: "Facebook广告账户3",
    accountId: "2345678903",
    status: "disabled"
  },
  {
    id: "4",
    accountName: "Facebook广告账户4",
    accountId: "2345678904",
    status: "enabled"
  },
  {
    id: "5",
    accountName: "Facebook广告账户5",
    accountId: "2345678905",
    status: "enabled"
  }
]);

// 监听 props.config 变化
watch(
  () => props.config,
  (newVal: any) => {
    if (newVal && newVal.accountList) {
      accountList.value = newVal.accountList;
    }
  },
  { immediate: true, deep: true }
);

// 添加账户
const handleAdd = () => {
  console.log("添加账户");
  // TODO: 打开添加账户弹窗
};

// 编辑账户
const handleEdit = (row: AccountItem) => {
  console.log("编辑账户:", row);
  // TODO: 打开编辑账户弹窗
};

// 删除账户
const handleDelete = (row: AccountItem) => {
  ElMessageBox.confirm(
    `确认删除账户「${row.accountName}」？删除后无法恢复。`,
    "删除确认",
    {
      confirmButtonText: "确认删除",
      cancelButtonText: "取消",
      type: "warning"
    }
  )
    .then(() => {
      const index = accountList.value.findIndex(item => item.id === row.id);
      if (index > -1) {
        accountList.value.splice(index, 1);
        ElMessage.success("删除成功");
      }
    })
    .catch(() => {
      // 取消删除
    });
};

// 保存配置
const handleSave = async () => {
  try {
    const params = {
      accountList: accountList.value
    };
    // TODO: 调用保存接口
    console.log("保存多账户管理配置:", params);
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

  .account-table {
    margin-bottom: 24px;

    .table-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .table-title {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: #333;
      }

      .el-button {
        display: flex;
        align-items: center;
        gap: 6px;

        .btn-icon {
          font-size: 14px;
        }
      }
    }

    :deep(.el-table) {
      font-size: 14px;

      th.el-table__cell {
        background-color: #fafafa;
        font-weight: 600;
        color: #333;
      }

      td.el-table__cell {
        color: #666;
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
