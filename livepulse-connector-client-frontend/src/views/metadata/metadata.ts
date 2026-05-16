import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getMetadataList,
  deleteMetadata,
  saveOrUpdateMetadata
} from "@/api/metadata";
import dayjs from "dayjs";

export interface MetadataItem {
  tableId?: number;
  tableName: string;
  tableAlias: string;
  type: string;
  comment: string;
  source: number;
  createTime: string;
  updateTime?: string;
}

export interface MetadataForm {
  tableId?: number;
  tableName: string;
  tableAlias: string;
  type: string;
  comment: string;
}

export function useMetadata() {
  const router = useRouter();
  const loading = ref(false);
  const tableName = ref("");
  const dataTypeFilter = ref("");
  const currentPage = ref(1);
  const pageSize = ref(20);
  const total = ref(0);
  const tableData = ref<MetadataItem[]>([]);

  // 弹窗相关
  const dialogVisible = ref(false);
  const dialogTitle = ref("新增元数据表");
  const isEdit = ref(false);
  const formRef = ref();
  const formData = reactive<MetadataForm>({
    tableName: "",
    tableAlias: "",
    type: "",
    comment: ""
  });

  const tableTypeOptions = [
    { label: "主表", value: "主表" },
    { label: "维度表", value: "维度表" },
    { label: "属性表", value: "属性表" },
    { label: "映射表", value: "映射表" }
  ];

  const formRules = {
    tableName: [
      { required: true, message: "请输入表英文名称", trigger: "blur" },
      {
        pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/,
        message: "英文名称只能包含字母、数字和下划线，且必须以字母开头",
        trigger: "blur"
      }
    ],
    tableAlias: [
      { required: true, message: "请输入表中文名称", trigger: "blur" }
    ],
    type: [{ required: true, message: "请选择表类型", trigger: "change" }],
    comment: [{ required: true, message: "请输入描述", trigger: "blur" }]
  };

  const columns: TableColumnList = [
    {
      label: "序号",
      type: "index",
      width: 80,
      align: "center"
    },
    {
      label: "表英文名称",
      prop: "tableName",
      minWidth: 150,
      slot: "tableName"
    },
    {
      label: "表中文名称",
      prop: "tableAlias",
      minWidth: 150
    },
    // { label: "表类型", prop: "type", width: 120 },
    // { label: "来源", prop: "source", minWidth: 200 },
    { label: "描述", prop: "comment", minWidth: 200 },
    {
      label: "最后同步",
      prop: "updateTime",
      minWidth: 300,
      cellRenderer: ({ row }) => {
        return dayjs(row.updateTime).format("YYYY-MM-DD HH:mm:ss");
      }
    },
    {
      label: "操作",
      width: 140,
      align: "center",
      fixed: "right",
      slot: "operation"
    }
  ];

  const getList = async () => {
    loading.value = true;
    try {
      const params = {
        currentPage: currentPage.value,
        pageSize: pageSize.value,
        tableName: tableName.value,
        tableType: dataTypeFilter.value,
        sortType: 1
      };

      const res: any = await getMetadataList(params);
      if (res.success) {
        tableData.value = res.data.list || [];
        total.value = res.data.total || 0;
      } else {
        ElMessage.error(res.msg || "获取数据失败");
      }
    } catch (error) {
      console.error("获取元数据列表失败:", error);
      ElMessage.error("获取数据失败");
    } finally {
      loading.value = false;
    }
  };

  const handleSearch = () => {
    currentPage.value = 1;
    getList();
  };

  const handleReset = () => {
    tableName.value = "";
    dataTypeFilter.value = "";
    currentPage.value = 1;
    getList();
  };

  const handleAdd = () => {
    isEdit.value = false;
    dialogTitle.value = "新增元数据表";
    resetForm();
    dialogVisible.value = true;
  };

  const handleViewDetail = (row: MetadataItem) => {
    router.push({
      name: "MetadataDetail",
      query: {
        tableId: row.tableId,
        tableName: row.tableName
      }
    });
  };

  const handleEdit = (row: MetadataItem) => {
    isEdit.value = true;
    dialogTitle.value = "编辑元数据表";
    Object.assign(formData, {
      tableId: row.tableId,
      tableName: row.tableName,
      tableAlias: row.tableAlias,
      type: row.type,
      comment: row.comment
    });
    dialogVisible.value = true;
  };

  const resetForm = () => {
    Object.assign(formData, {
      tableId: undefined,
      tableName: "",
      tableAlias: "",
      type: "",
      comment: ""
    });
    formRef.value?.clearValidate();
  };

  const handleDialogClose = () => {
    dialogVisible.value = false;
    resetForm();
  };

  const handleSubmit = async () => {
    if (!formRef.value) return;

    await formRef.value.validate(async (valid: boolean) => {
      if (valid) {
        try {
          const params = {
            ...formData
          };
          const res: any = await saveOrUpdateMetadata(params);
          if (res.success) {
            ElMessage.success(isEdit.value ? "编辑成功" : "新增成功");
            dialogVisible.value = false;
            resetForm();
            getList();
          } else {
            ElMessage.error(res.msg || "操作失败");
          }
        } catch (error) {
          console.error("保存元数据失败:", error);
          ElMessage.error("操作失败");
        }
      }
    });
  };

  const handleDelete = (row: MetadataItem) => {
    ElMessageBox.confirm(
      `确定要删除元数据"${row.tableAlias}"吗？删除后不可恢复。`,
      "提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }
    )
      .then(async () => {
        try {
          const res: any = await deleteMetadata(row.tableId);
          if (res.success) {
            ElMessage.success("删除成功");
            getList();
          } else {
            ElMessage.error(res.msg || "删除失败");
          }
        } catch (error) {
          console.error("删除元数据失败:", error);
          ElMessage.error("删除失败");
        }
      })
      .catch(() => {
        ElMessage.info("已取消删除");
      });
  };

  const onSizeChange = (size: number) => {
    pageSize.value = size;
    currentPage.value = 1;
    getList();
  };

  const onCurrentChange = (page: number) => {
    currentPage.value = page;
    getList();
  };

  return {
    loading,
    tableName,
    dataTypeFilter,
    currentPage,
    pageSize,
    total,
    tableData,
    columns,
    handleSearch,
    handleReset,
    handleAdd,
    handleViewDetail,
    handleEdit,
    handleDelete,
    onSizeChange,
    onCurrentChange,
    // 弹窗相关
    dialogVisible,
    dialogTitle,
    isEdit,
    formRef,
    formData,
    tableTypeOptions,
    formRules,
    handleDialogClose,
    handleSubmit
  };
}
