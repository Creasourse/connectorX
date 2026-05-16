<template>
  <div class="product-manage">
    <h2 class="title">商品管理</h2>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon total">
          <el-icon><Box /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ totalProducts }}</div>
          <div class="stat-label">总商品数</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon selling">
          <el-icon><ShoppingCart /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ sellingProducts }}</div>
          <div class="stat-label">在售商品</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon offline">
          <el-icon><Remove /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ offlineProducts }}</div>
          <div class="stat-label">已下架</div>
        </div>
      </div>
    </div>

    <!-- 搜索和操作栏 -->
    <div class="action-bar">
      <div class="search-area">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索商品名称或ID"
          clearable
          style="width: 300px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>

        <el-select
          v-model="selectedCategory"
          placeholder="商品分类"
          clearable
          style="width: 150px; margin-left: 12px"
        >
          <el-option label="全部分类" value="" />
          <el-option label="服装" value="clothing" />
          <el-option label="电子产品" value="electronics" />
          <el-option label="家居用品" value="home" />
          <el-option label="美妆护肤" value="beauty" />
        </el-select>

        <el-select
          v-model="selectedStatus"
          placeholder="商品状态"
          clearable
          style="width: 120px; margin-left: 12px"
        >
          <el-option label="全部状态" value="" />
          <el-option label="在售" value="selling" />
          <el-option label="已下架" value="offline" />
          <el-option label="库存不足" value="low_stock" />
        </el-select>
      </div>

      <div class="action-buttons">
        <el-button type="primary" @click="handleAddProduct">
          <el-icon><Plus /></el-icon>
          添加商品
        </el-button>
        <el-button @click="handleBatchSync">
          <el-icon><Refresh /></el-icon>
          批量同步
        </el-button>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="product-list">
      <el-table :data="filteredProducts" style="width: 100%">
        <el-table-column label="商品信息" min-width="300">
          <template #default="{ row }">
            <div class="product-info">
              <el-image
                :src="row.image"
                fit="cover"
                class="product-image"
              />
              <div class="product-details">
                <div class="product-name">{{ row.name }}</div>
                <div class="product-id">ID: {{ row.id }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="price" label="价格" min-width="120">
          <template #default="{ row }">
            <span class="price">¥{{ row.price }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="stock" label="库存" min-width="100">
          <template #default="{ row }">
            <span :class="['stock', getStockClass(row.stock)]">
              {{ row.stock }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="sales" label="销量" min-width="100">
          <template #default="{ row }">
            <span class="sales">{{ row.sales }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button
              :type="row.status === 'selling' ? 'warning' : 'success'"
              size="small"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 'selling' ? '下架' : '上架' }}
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="totalCount"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Box,
  ShoppingCart,
  Remove,
  Search,
  Plus,
  Refresh
} from "@element-plus/icons-vue";

// 统计数据
const totalProducts = ref(1234);
const sellingProducts = ref(890);
const offlineProducts = ref(344);

// 搜索和筛选
const searchKeyword = ref("");
const selectedCategory = ref("");
const selectedStatus = ref("");

// 分页
const currentPage = ref(1);
const pageSize = ref(10);
const totalCount = ref(50);

// 商品列表
const productList = ref([
  {
    id: "TKS20240120001",
    name: "时尚休闲运动鞋男款夏季透气跑步鞋",
    price: "299.00",
    stock: 150,
    status: "selling",
    sales: 1234,
    image: "https://via.placeholder.com/80x80"
  },
  {
    id: "TKS20240120002",
    name: "无线蓝牙耳机降噪运动耳机",
    price: "199.00",
    stock: 8,
    status: "selling",
    sales: 856,
    image: "https://via.placeholder.com/80x80"
  },
  {
    id: "TKS20240120003",
    name: "简约百搭帆布包女包大容量购物袋",
    price: "89.00",
    stock: 0,
    status: "offline",
    sales: 432,
    image: "https://via.placeholder.com/80x80"
  },
  {
    id: "TKS20240120004",
    name: "纯棉短袖T恤男士夏季圆领上衣",
    price: "79.00",
    stock: 200,
    status: "selling",
    sales: 2341,
    image: "https://via.placeholder.com/80x80"
  },
  {
    id: "TKS20240120005",
    name: "智能手表运动手表心率监测防水",
    price: "599.00",
    stock: 45,
    status: "selling",
    sales: 567,
    image: "https://via.placeholder.com/80x80"
  },
  {
    id: "TKS20240120006",
    name: "复古眼镜框防蓝光眼镜平光镜",
    price: "129.00",
    stock: 12,
    status: "low_stock",
    sales: 234,
    image: "https://via.placeholder.com/80x80"
  },
  {
    id: "TKS20240120007",
    name: "户外运动水壶大容量便携水杯",
    price: "59.00",
    stock: 88,
    status: "selling",
    sales: 1567,
    image: "https://via.placeholder.com/80x80"
  },
  {
    id: "TKS20240120008",
    name: "简约双肩包女包百搭背包",
    price: "159.00",
    stock: 0,
    status: "offline",
    sales: 345,
    image: "https://via.placeholder.com/80x80"
  },
  {
    id: "TKS20240120009",
    name: "韩版女装连衣裙夏季修身显瘦裙子",
    price: "189.00",
    stock: 67,
    status: "selling",
    sales: 1890,
    image: "https://via.placeholder.com/80x80"
  },
  {
    id: "TKS20240120010",
    name: "男士皮带真皮自动扣腰带",
    price: "99.00",
    stock: 23,
    status: "selling",
    sales: 678,
    image: "https://via.placeholder.com/80x80"
  }
]);

// 过滤后的商品列表
const filteredProducts = computed(() => {
  let products = productList.value;

  if (searchKeyword.value) {
    products = products.filter(
      (product) =>
        product.name.includes(searchKeyword.value) ||
        product.id.includes(searchKeyword.value)
    );
  }

  if (selectedStatus.value) {
    products = products.filter((product) => product.status === selectedStatus.value);
  }

  return products;
});

// 获取库存样式类
const getStockClass = (stock: number) => {
  if (stock === 0) return "out-of-stock";
  if (stock < 10) return "low-stock";
  return "normal";
};

// 获取状态类型
const getStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    selling: "success",
    offline: "info",
    low_stock: "warning"
  };
  return statusMap[status] || "info";
};

// 获取状态文本
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    selling: "在售",
    offline: "已下架",
    low_stock: "库存不足"
  };
  return statusMap[status] || "未知";
};

// 添加商品
const handleAddProduct = () => {
  ElMessage.info("打开添加商品对话框");
};

// 编辑商品
const handleEdit = (row: any) => {
  ElMessage.info(`编辑商品：${row.name}`);
};

// 切换状态
const handleToggleStatus = (row: any) => {
  const action = row.status === "selling" ? "下架" : "上架";
  ElMessageBox.confirm(`确定要${action}商品"${row.name}"吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    row.status = row.status === "selling" ? "offline" : "selling";
    ElMessage.success(`${action}成功`);
  });
};

// 删除商品
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除商品"${row.name}"吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    const index = productList.value.findIndex((item) => item.id === row.id);
    if (index > -1) {
      productList.value.splice(index, 1);
      ElMessage.success("删除成功");
    }
  });
};

// 批量同步
const handleBatchSync = () => {
  ElMessage.info("正在批量同步商品信息...");
};

// 分页变化
const handleSizeChange = (size: number) => {
  pageSize.value = size;
};

const handleCurrentChange = (page: number) => {
  currentPage.value = page;
};
</script>

<style scoped lang="scss">
.product-manage {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

  .title {
    margin: 0 0 24px;
    font-size: 16px;
    font-weight: 600;
    color: #333;
  }

  .stats-cards {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;
    margin-bottom: 24px;

    .stat-card {
      display: flex;
      align-items: center;
      padding: 20px;
      background-color: #fafafa;
      border: 1px solid #e8e8e8;
      border-radius: 8px;

      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;
        font-size: 24px;

        &.total {
          background-color: #e6f7ff;
          color: #1890ff;
        }

        &.selling {
          background-color: #f6ffed;
          color: #52c41a;
        }

        &.offline {
          background-color: #fff7e6;
          color: #faad14;
        }
      }

      .stat-content {
        flex: 1;

        .stat-value {
          font-size: 24px;
          font-weight: 600;
          color: #333;
          line-height: 1.2;
        }

        .stat-label {
          font-size: 14px;
          color: #666;
          margin-top: 4px;
        }
      }
    }
  }

  .action-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .search-area {
      display: flex;
      align-items: center;
    }

    .action-buttons {
      display: flex;
      gap: 12px;
    }
  }

  .product-list {
    :deep(.el-table) {
      border: 1px solid #e8e8e8;
      border-radius: 8px;

      .el-table__header {
        th {
          background-color: #fafafa;
          font-weight: 600;
          color: #333;
        }
      }

      .el-table__body {
        tr {
          &:hover {
            background-color: #f0f5ff;
          }
        }
      }
    }

    .product-info {
      display: flex;
      align-items: center;
      gap: 12px;

      .product-image {
        width: 60px;
        height: 60px;
        border-radius: 4px;
        object-fit: cover;
      }

      .product-details {
        flex: 1;

        .product-name {
          font-size: 14px;
          color: #333;
          font-weight: 500;
          margin-bottom: 4px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          max-width: 200px;
        }

        .product-id {
          font-size: 12px;
          color: #999;
          font-family: "Courier New", monospace;
        }
      }
    }

    .price {
      color: #f5222d;
      font-weight: 600;
      font-size: 14px;
    }

    .stock {
      font-weight: 500;

      &.normal {
        color: #52c41a;
      }

      &.low-stock {
        color: #faad14;
      }

      &.out-of-stock {
        color: #f5222d;
      }
    }

    .sales {
      color: #666;
      font-size: 14px;
    }
  }

  .pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>
