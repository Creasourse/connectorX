<script setup lang="ts">
import { ref, reactive, computed, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import type { FormInstance, FormRules } from "element-plus";
import { Picture as PictureIcon } from "@element-plus/icons-vue";
import { useUserStoreHook } from "@/store/modules/user";
import type { LabelItem, GroupItem } from "@/api/app";
import { getAppDetail, getLabelEnum, getGroupEnum } from "@/api/app";
import { cloneDeep } from "@pureadmin/utils";
import MarkdownEditor from "@/components/MarkdownEdit.vue";
import { formatNow, getUrl } from "@/utils/format";
// import { VueCropper } from "cropper-next-vue";
// import "cropper-next-vue/style.css";

defineOptions({ name: "AppManagePublish" });

const userStore = useUserStoreHook();
const userAvatar = computed(() => userStore.avatar || "");

const router = useRouter();
const route = useRoute();
const id = computed(() => Number(route.query.id));
const isEdit = computed(() => route.query?.type === "edit");
const isNewVersion = computed(() => route.query?.type === "version");

const cropperRef = ref<any>();
const formRef = ref<FormInstance>();
const oldForm = reactive<any>({});
const form = reactive({
  connPluginId: id.value || undefined,
  connPluginName: "",
  groupId: undefined,
  groupName: "",
  version: "",
  developUserId: "",
  developUserName: "",
  avatar: userAvatar.value,
  type: 1, // 1:免费 2:收费
  price: 0,
  staus: 0, // 0:未上架 1:上架 2:下架 3:审核中,待审核 4:历史版本 5:驳回
  icon: "",
  remark: "",
  sketchOut: "",
  titile: "",
  connLabelName: "",
  category: "",
  cdpVersion: "",
  tags: [],
  locationUrl: ""
});
const rules = reactive({
  titile: [
    { required: true, message: "请填写应用名称", trigger: "blur" },
    { min: 2, max: 20, message: "长度在2~20字符", trigger: "blur" }
  ],
  groupName: [
    {
      required: true,
      message: "请选择分类",
      trigger: "change"
    }
  ],
  connLabelName: [
    {
      required: true,
      message: "请增加应用标签",
      trigger: "change"
    }
  ],
  version: [
    {
      required: true,
      message: "请填写版本号",
      trigger: "change"
    }
  ],
  remark: [
    {
      required: true,
      message: "请填写更新日志",
      trigger: "change"
    }
  ],
  sketchOut: [
    {
      required: true,
      message: "请填写应用描述",
      trigger: "blur"
    }
  ],
  locationUrl: [
    {
      required: true,
      message: "请填写资源链接",
      trigger: "change"
    }
  ]
});

const labelList = ref<LabelItem[]>([]);
const getLabelList = () => {
  getLabelEnum().then(res => {
    if (res.success) {
      labelList.value = res.data || [];
    }
  });
};
const changeLabel = (v?: any[]) => {
  form.connLabelName = v.length > 0 ? v.join(",") : "";
};
const groupList = ref<GroupItem[]>([]);
const getGroupList = () => {
  getGroupEnum().then(res => {
    if (res.success) {
      groupList.value = res.data;
    }
  });
};
const changeGroup = v => {
  const obj = groupList.value.find(e => e.groupId === v);
  form.groupName = obj?.groupName || "";
};
const getAppDetailFn = (id: number) => {
  getAppDetail(id).then(res => {
    if (res.success) {
      const { connPluginName, connLabelName, icon } = res.data;
      Object.assign(form, res.data, {
        titile: connPluginName,
        tags: connLabelName?.split(",") || []
      });
      Object.assign(oldForm, cloneDeep(form));
      iconPreview.value = getUrl(icon);
    }
  });
};
onMounted(() => {
  getLabelList();
  getGroupList();
  if (id.value) {
    getAppDetailFn(id.value);
  }
});

const loading = ref(false);
const iconPreview = ref("");

const handleUpload = url => {
  iconPreview.value = url;
};

// 实时预览
const realTime = (data: any) => {
  iconPreview.value = data;
};

const handleCancel = () => {
  router.push("/appManage/list");
};
</script>

<template>
  <div class="publish-page">
    <div class="publish-card">
      <el-form
        ref="formRef"
        style="max-width: 600px"
        :model="form"
        :rules="rules"
        label-position="top"
      >
        <!-- 基本信息 -->
        <section class="form-section">
          <h3 class="section-title">
            <span class="title-bar" />
            基本信息
          </h3>

          <!-- 应用图标 -->
          <div class="field-group">
            <label class="field-label">应用图标</label>
            <!-- <vue-cropper
              ref="cropperRef"
              :img="iconPreview"
              :crop-layout="{ width: 200, height: 200 }"
              @img-upload="handleUpload"
            /> -->
            <el-upload
              class="icon-uploader"
              :show-file-list="false"
              accept="image/*"
              :auto-upload="false"
              @change="handleIconChange"
            >
              <div class="icon-placeholder">
                <img
                  v-if="iconPreview"
                  :src="iconPreview"
                  class="icon-preview"
                />
                <el-icon v-else class="icon-empty"><PictureIcon /></el-icon>
              </div>
            </el-upload>
            <span class="field-hint">上传200*200图标</span>
          </div>

          <!-- 应用名称 + 应用分类 -->
          <div class="field-row">
            <el-form-item class="field-col" label="应用名称" prop="titile">
              <el-input
                v-model="form.titile"
                placeholder="请使用简洁明了的中文名称"
                :disabled="isEdit || isNewVersion"
              />
            </el-form-item>
            <el-form-item class="field-col" label="应用分类" prop="groupName">
              <el-select
                v-model="form.groupId"
                placeholder="选择类型"
                class="field-input"
                filterable
                @change="changeGroup"
              >
                <el-option
                  v-for="item in groupList"
                  :key="item.groupId"
                  :value="item.groupId"
                  :label="item.groupName"
                />
              </el-select>
            </el-form-item>
          </div>
          <div class="field-row">
            <el-form-item class="field-col" label="应用描述" prop="sketchOut">
              <el-input
                v-model="form.sketchOut"
                placeholder="请使用简洁明了的介绍应用"
              />
            </el-form-item>
          </div>
        </section>

        <el-divider />

        <!-- 版本管理 -->
        <section class="form-section">
          <h3 class="section-title">
            <span class="title-bar" />
            版本管理
          </h3>

          <div class="field-row">
            <el-form-item
              class="field-col"
              label="发布应用的版本号"
              prop="version"
            >
              <el-input
                v-model="form.version"
                placeholder="1.0.0"
                :disabled="isEdit"
              />
            </el-form-item>
          </div>

          <el-form-item
            class="field-group mt20"
            label="发布版本信息 (更新日志)"
            prop="remark"
          >
            <MarkdownEditor v-model="form.remark" />
          </el-form-item>

          <el-form-item
            class="field-group mt20"
            label="应用标签"
            prop="connLabelName"
          >
            <el-select
              v-model="form.tags"
              placeholder="选择标签"
              class="field-input"
              multiple
              filterable
              allow-create
              default-first-option
              @change="changeLabel"
            >
              <el-option
                v-for="item in labelList"
                :key="item.connLabelEnumId"
                :value="item.connLabelName"
                >{{ item.connLabelName }}</el-option
              >
            </el-select>
          </el-form-item>
        </section>

        <el-divider />

        <!-- 资源链接 -->
        <section class="form-section">
          <h3 class="section-title">
            <span class="title-bar" />
            资源链接
          </h3>

          <el-form-item
            class="field-group"
            label="上传应用到 Git 地址"
            prop="locationUrl"
          >
            <el-input
              v-model="form.locationUrl"
              placeholder="Git用户名/项目名，例如：creasourse/connectx"
              class="field-input"
            >
              <template #prepend>
                <svg
                  class="git-icon"
                  viewBox="0 0 16 16"
                  width="16"
                  height="16"
                  fill="currentColor"
                >
                  <path
                    d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.013 8.013 0 0016 8c0-4.42-3.58-8-8-8z"
                  />
                </svg>
                <span class="ml-2">https://github.com/</span>
              </template>
            </el-input>
          </el-form-item>
        </section>
      </el-form>
    </div>

    <!-- 底部操作栏 -->
    <div class="footer-bar">
      <el-button class="btn-cancel" @click="handleCancel">取消</el-button>
      <el-button
        type="primary"
        plain
        class="btn-save"
        :loading="loading"
        @click="handleSave(formRef)"
      >
        保存
      </el-button>
      <el-button
        type="primary"
        class="btn-publish"
        :loading="loading"
        @click="handlePublish(formRef)"
      >
        <svg
          class="publish-icon"
          viewBox="0 0 24 24"
          width="14"
          height="14"
          fill="currentColor"
        >
          <path d="M8 5v14l11-7z" />
        </svg>
        发布
      </el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
:deep(.el-form-item--label-top .el-form-item__label) {
  margin-bottom: 0;
}

.publish-page {
  display: flex;
  flex-direction: column;
  min-height: 100%;
  background: var(--el-bg-color-page);
  padding: 24px 28px 100px;

  @media (max-width: 640px) {
    padding: 12px 12px 100px;
  }
}

.publish-card {
  background: var(--el-bg-color);
  border-radius: 10px;
  border: 1px solid var(--el-border-color-lighter);
  padding: 32px 40px;
  max-width: 920px;
  margin: 0 auto;
  width: 100%;

  @media (max-width: 640px) {
    padding: 20px 16px;
    border-radius: 8px;

    :deep(.el-form) {
      max-width: 100% !important;
    }
  }
}

/* ---- 分区标题 ---- */
.form-section {
  margin-bottom: 4px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 17px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0 0 24px;

  .title-bar {
    display: inline-block;
    width: 4px;
    height: 18px;
    background: #409eff;
    border-radius: 2px;
    flex-shrink: 0;
  }
}

/* ---- 字段布局 ---- */
.field-group {
  display: flex;
  flex-direction: column;
}

.field-row {
  display: flex;
  gap: 32px;
  margin-bottom: 10px;

  .field-col {
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  @media (max-width: 640px) {
    flex-direction: column;
    gap: 0;
  }
}

.mt-20 {
  margin-top: 20px;
}

.field-label {
  font-size: 13px;
  color: var(--el-text-color-primary);
  font-weight: 400;
}

.field-input {
  width: 100%;
}

.field-hint {
  font-size: 12px;
  color: var(--el-text-color-placeholder);
  line-height: 1.4;

  &--link {
    color: #409eff;
    cursor: pointer;
  }
}

.input-suffix-text {
  font-size: 12px;
  color: var(--el-text-color-placeholder);
}

/* ---- 图标上传 ---- */
.icon-uploader {
  :deep(.el-upload) {
    cursor: pointer;
  }
}

.icon-placeholder {
  width: 72px;
  height: 72px;
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--el-fill-color-lighter);
  overflow: hidden;

  .icon-empty {
    font-size: 28px;
    color: var(--el-text-color-placeholder);
  }

  .icon-preview {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

/* ---- 标签 ---- */
.tags-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.app-tag {
  background: #eff6ff;
  border-color: #bfdbfe;
  color: #3b82f6;
  border-radius: 4px;
  margin: 0 3px 3px 0;

  :deep(.el-tag__close) {
    color: #3b82f6;

    &:hover {
      background: #bfdbfe;
    }
  }
}

.tag-input {
  width: 90px;
}

.add-tag-btn {
  padding: 0 10px;
  height: 24px;
  border-radius: 4px;
  border: 1px dashed var(--el-border-color);
  color: var(--el-text-color-secondary);
  background: transparent;
  font-size: 16px;
  line-height: 1;
}

/* ---- Git 图标 ---- */
.git-icon {
  color: #333;
}

/* ---- 分隔线 ---- */
:deep(.el-divider) {
  margin: 28px 0;
}

/* ---- 底部操作栏 ---- */
.footer-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 16px 0;
  background: var(--el-bg-color);
  border-top: 1px solid var(--el-border-color-lighter);
  z-index: 100;

  .btn-cancel {
    width: 100px;
    border-radius: 6px;
  }

  .btn-save {
    width: 100px;
    border-radius: 6px;
  }

  .btn-publish {
    width: 100px;
    border-radius: 6px;
    display: flex;
    align-items: center;
    gap: 6px;

    .publish-icon {
      flex-shrink: 0;
    }
  }

  @media (max-width: 480px) {
    padding: 12px 16px;
    justify-content: stretch;

    .btn-cancel,
    .btn-save,
    .btn-publish {
      flex: 1;
      width: auto;
    }
  }
}
</style>
