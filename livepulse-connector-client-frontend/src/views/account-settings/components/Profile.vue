<script setup lang="ts">
import { message } from "@/utils/message";
import { onMounted, reactive, ref } from "vue";
import { type UserInfo, getMine, updateMine, uploadAvatar } from "@/api/user";
import type { FormInstance, FormRules } from "element-plus";
import ReCropperPreview from "@/components/ReCropperPreview";
import {
  storageLocal,
  createFormData,
  deviceDetection
} from "@pureadmin/utils";
import { getUrl } from "@/utils/format";
import { userKey } from "@/utils/auth";
import { useUserStoreHook } from "@/store/modules/user";
import { useNav } from "@/layout/hooks/useNav";
import dayjs from "dayjs";

const { firstchar } = useNav();

defineOptions({
  name: "Profile"
});

const imgSrc = ref("");
const imgName = ref("");
const cropperBlob = ref();
const cropRef = ref();
const uploadRef = ref();
const isShowCropper = ref(false);
const isEditing = ref(false);
const userInfoFormRef = ref<FormInstance>();
const userInfo = ref(storageLocal().getItem(userKey));

const userInfos = reactive<UserInfo>({
  avatar: "",
  userId: null,
  nickName: "",
  userName: "",
  email: "",
  phone: "",
  description: "",
  roles: [],
  createdAt: ""
});

const getUserInfo = () => {
  Object.assign(userInfos, storageLocal().getItem(userKey));
  getMine().then(res => {
    if (res.success) {
      const { userId, avatar, nickName, userName, phone, email, type } =
        res.data;
      const newAvatar = avatar.replace("/data/connectorX/uploadPath/", "");
      Object.assign(userInfos, {
        avatar,
        nickName,
        userId,
        userName,
        phone,
        email,
        type
      });
    }
  });
};

onMounted(async () => {
  getUserInfo();
});

// 编辑时的临时副本
const editForm = reactive<Partial<UserInfo>>({
  userId: null,
  userName: "",
  nickName: "",
  type: null,
  email: "",
  phone: ""
});

const rules = reactive<FormRules>({
  nickName: [{ required: true, message: "显示名称必填", trigger: "blur" }],
  email: [
    {
      type: "email",
      message: "请输入正确的邮箱地址",
      trigger: ["blur", "change"]
    }
  ]
});

// 头像点击触发文件选择
const avatarInputRef = ref<HTMLInputElement>();
const onAvatarClick = () => {
  avatarInputRef.value?.click();
};

const onAvatarFileChange = (e: Event) => {
  const file = (e.target as HTMLInputElement).files?.[0];
  if (!file) return;
  const reader = new FileReader();
  reader.onload = ev => {
    imgName.value = file.name;
    imgSrc.value = ev.target.result as string;
    isShowCropper.value = true;
  };
  reader.readAsDataURL(file);
  // 重置 input 以允许重复选同一文件
  (e.target as HTMLInputElement).value = "";
};

const handleCropperClose = () => {
  isShowCropper.value = false;
};

const onCropper = ({ blob, info }) => {
  cropperBlob.value = blob;
};

const handleSubmitImage = async () => {
  try {
    const formData = createFormData({
      files: new File([cropperBlob.value], imgName.value)
    });
    const res = await uploadAvatar(formData);
    if (res.success && res.data) {
      message("更新头像成功", { type: "success" });
      handleCropperClose();
      getUserInfo();
    } else {
      message(res.msg || "更新头像失败", { type: "error" });
    }
  } catch (error) {
    message(`提交异常 ${error}`, { type: "error" });
  }
};

// 进入编辑模式
const enterEdit = () => {
  editForm.userId = userInfos.userId;
  editForm.userName = userInfos.userName;
  editForm.nickName = userInfos.nickName;
  editForm.type = userInfos.type;
  editForm.email = userInfos.email;
  editForm.phone = userInfos.phone;
  isEditing.value = true;
};

// 取消编辑
const cancelEdit = () => {
  isEditing.value = false;
};

// 保存编辑
const onSubmit = async (formEl: FormInstance) => {
  if (!formEl) return;
  await formEl.validate(async valid => {
    if (!valid) return;
    try {
      const res = await updateMine({ ...editForm });
      if (res.success && res.data) {
        Object.assign(userInfos, editForm);
        isEditing.value = false;
        message("更新信息成功", { type: "success" });
      } else {
        message(res.msg || "更新失败", { type: "error" });
      }
    } catch (error) {
      message(`提交异常 ${error}`, { type: "error" });
    }
  });
};

const roleColorMap: Record<string, string> = {
  管理员: "#07C160",
  开发者: "#1677ff",
  普通用户: "#8c8c8c"
};

const getRoleLabel = (type: number) => {
  if (type === 3) return "管理员";
  if (type === 2) return "开发者";
  return "普通用户";
};
</script>

<template>
  <div class="profile-page">
    <!-- 顶部头像卡片 -->
    <div class="avatar-card">
      <div class="avatar-wrapper" title="点击更换头像" @click="onAvatarClick">
        <el-avatar
          v-if="userInfos.avatar"
          :size="64"
          :src="getUrl(userInfos.avatar)"
        />
        <span
          v-else
          class="size-16 text-xl/15.5 text-center rounded-full border border-black-20 select-none"
        >
          {{ firstchar }}
        </span>
        <div class="avatar-overlay">
          <IconifyIconOnline style="font-size: 18px" icon="ep:camera" />
        </div>
      </div>
      <div class="ml-4">
        <div class="text-xl font-semibold">
          {{ userInfos.nickName || userInfos.userName }}
        </div>
        <div class="text-sm text-(--el-text-color-secondary) mt-0.5">
          @{{ userInfos.userName }}
        </div>
      </div>
      <!-- 隐藏的 file input -->
      <input
        ref="avatarInputRef"
        type="file"
        accept="image/*"
        class="hidden"
        @change="onAvatarFileChange"
      />
    </div>

    <!-- 详情卡片 -->
    <div class="detail-card">
      <!-- Tab 标头 + 编辑按钮 -->
      <div class="detail-header">
        <div class="tab-label">
          <span>详情</span>
          <div class="tab-underline" />
        </div>
        <template v-if="!isEditing">
          <el-button plain @click="enterEdit">编辑</el-button>
        </template>
        <template v-else>
          <div class="flex gap-2">
            <el-button @click="cancelEdit">取消</el-button>
            <el-button type="primary" @click="onSubmit(userInfoFormRef)"
              >保存</el-button
            >
          </div>
        </template>
      </div>

      <el-divider class="my-0!" />

      <!-- 字段列表 -->
      <el-form
        v-if="isEditing"
        ref="userInfoFormRef"
        :model="editForm"
        :rules="rules"
        label-position="left"
        label-width="120px"
        class="edit-form"
      >
        <el-form-item label="显示名称" prop="nickName">
          <el-input v-model="editForm.nickName" placeholder="请输入显示名称" />
        </el-form-item>
        <el-form-item label="电子邮箱" prop="email">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <!-- <el-form-item label="描述">
          <el-input
            v-model="editForm.description"
            type="textarea"
            placeholder="请输入描述"
            :autosize="{ minRows: 3, maxRows: 6 }"
            maxlength="200"
            show-word-limit
          />
        </el-form-item> -->
      </el-form>

      <template v-else>
        <div class="info-row">
          <span class="info-label">显示名称</span>
          <span class="info-value">{{ userInfos.nickName || "—" }}</span>
        </div>
        <el-divider class="my-0!" />
        <div class="info-row">
          <span class="info-label">用户名</span>
          <span class="info-value">{{ userInfos.userName || "—" }}</span>
        </div>
        <el-divider class="my-0!" />
        <div class="info-row">
          <span class="info-label">电子邮箱</span>
          <span
            class="info-value"
            :class="{ 'text-(--el-color-primary)': userInfos.email }"
            >{{ userInfos.email || "—" }}</span
          >
        </div>
        <el-divider class="my-0!" />
        <div class="info-row">
          <span class="info-label">手机号</span>
          <span class="info-value">{{ userInfos.phone || "—" }}</span>
        </div>
        <el-divider class="my-0!" />
        <div class="info-row">
          <span class="info-label">角色</span>
          <span class="info-value">
            <template v-if="userInfos.type">
              <el-tag
                class="mr-2"
                :color="roleColorMap[userInfos.type] + '22'"
                round
              >
                {{ getRoleLabel(userInfos.type) }}
              </el-tag>
            </template>
            <span v-else>—</span>
          </span>
        </div>
        <!-- <el-divider class="my-0!" />
        <div class="info-row">
          <span class="info-label">描述</span>
          <span class="info-value flex-1">
            {{ userInfos.description || "无" }}
          </span>
        </div> -->
        <el-divider class="my-0!" />
        <div class="info-row">
          <span class="info-label">注册时间</span>
          <span class="info-value">{{ userInfos.createdAt || "—" }}</span>
        </div>
      </template>
    </div>

    <!-- 裁剪弹窗 -->
    <el-dialog
      v-model="isShowCropper"
      width="40%"
      title="编辑头像"
      destroy-on-close
      :closeOnClickModal="false"
      :before-close="handleCropperClose"
      :fullscreen="deviceDetection()"
    >
      <ReCropperPreview ref="cropRef" :imgSrc="imgSrc" @cropper="onCropper" />
      <template #footer>
        <el-button bg text @click="handleCropperClose">取消</el-button>
        <el-button bg text type="primary" @click="handleSubmitImage"
          >确定</el-button
        >
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.profile-page {
  padding: 24px;
  max-width: 1100px;
}

.avatar-card {
  display: flex;
  align-items: center;
  padding: 20px 24px;
  background: var(--el-bg-color);
  border-radius: 8px;
  margin-bottom: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover .avatar-overlay {
    opacity: 1;
  }
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.75);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
  border-radius: 50%;
}

.detail-card {
  background: var(--el-bg-color);
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.detail-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 56px;
}

.tab-label {
  position: relative;
  font-size: 14px;
  font-weight: 600;
  color: var(--el-color-primary);
  padding-bottom: 4px;
}

.tab-underline {
  position: absolute;
  bottom: -16px;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--el-color-primary);
  border-radius: 1px;
}

.info-row {
  display: flex;
  align-items: center;
  padding: 20px 24px;
  min-height: 64px;
}

.info-label {
  width: 120px;
  flex-shrink: 0;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.info-value {
  font-size: 14px;
  color: var(--el-text-color-primary);
}

.edit-form {
  padding: 20px 24px;
}
</style>
