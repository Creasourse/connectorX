<script setup lang="ts">
import { computed, PropType } from "vue";
import { useRouter } from "vue-router";
import dayjs from "dayjs";
import relativeTime from "dayjs/plugin/relativeTime";
import "dayjs/locale/zh-cn";
import { type AppItem } from "@/api/app";
import { getUrl } from "@/utils/format";
import { message } from "@/utils/message";

dayjs.extend(relativeTime);

defineOptions({ name: "AppCard" });

const props = defineProps({
  app: { type: Object as PropType<AppItem>, required: true }
});

const emit = defineEmits(["delete-item"]);
const router = useRouter();

const handleClickDetail = () => {
  router.push(`/apps/${props.app.connPluginId}`);
};

const isTheme = computed(() => props.app.groupName === "主题");

const updateTimeText = computed(() => {
  if (!props.app.updateTime) return "";
  return dayjs(props.app.updateTime).locale("zh-cn").fromNow() + "更新";
});

const tags = computed(() => {
  if (!props.app.connLabelName) return [];
  return props.app.connLabelName.split(",").filter(Boolean).slice(0, 1);
});

const installFn = (app: AppItem) => {
  console.log(app);
};
</script>

<template>
  <div class="app-card" @click="handleClickDetail">
    <!-- 顶部：图标 + 类型徽章 + 更新时间 -->
    <div class="card-header">
      <div class="card-icon">
        <img v-if="app.icon" :src="getUrl(app.icon)" class="icon-img" />
        <span v-else class="icon-fallback">{{
          app.connPluginName?.[0] || "A"
        }}</span>
      </div>
      <div class="card-header-right">
        <span
          class="type-badge"
          :class="isTheme ? 'type-badge--theme' : 'type-badge--plugin'"
        >
          {{ app.groupName || "插件" }}
        </span>
        <span class="update-time">{{ updateTimeText }}</span>
      </div>
    </div>

    <!-- 应用名称 + 版本号 -->
    <div class="card-name-row">
      <span class="card-name">{{ app.connPluginName }}</span>
      <span v-if="app.version" class="card-version">{{ app.version }}</span>
    </div>

    <!-- 描述 -->
    <p class="card-desc">{{ app.sketchOut }}</p>

    <!-- 标签 -->
    <div class="card-labels">
      <template v-if="tags.length">
        <span class="label-key">标签：</span>
        <el-tag v-for="tag in tags" :key="tag" size="small" class="label-tag">
          {{ tag }}
        </el-tag>
      </template>
      <span v-else class="label-placeholder" />
    </div>

    <!-- 开发者 -->
    <div class="card-author">
      <div class="author-avatar">
        <img v-if="app.avatar" :src="getUrl(app.avatar)" class="avatar-img" />
        <span v-else class="avatar-fallback">
          {{ app.developUserName?.[0] || "U" }}
        </span>
      </div>
      <span class="author-name flex-1">{{ app.developUserName }}</span>
      <el-button type="primary" @click.stop="installFn(app)">安装</el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.app-card {
  background: #fff;
  border: 1px solid #e8eaef;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: box-shadow 0.2s ease;
  display: flex;
  flex-direction: column;
  min-width: 0;

  &:hover {
    box-shadow: 0 4px 16px 0 rgb(0 0 0 / 8%);
  }
}

/* ---- 顶部区域 ---- */
.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 16px;
}

.card-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
  background: #f0f4ff;
  display: flex;
  align-items: center;
  justify-content: center;

  .icon-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .icon-fallback {
    font-size: 22px;
    font-weight: 700;
    color: #4080ff;
  }
}

.card-header-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.type-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  line-height: 20px;

  &--plugin {
    background: #eff3ff;
    color: #4080ff;
  }

  &--theme {
    background: #fff4e6;
    color: #f97316;
  }
}

.update-time {
  font-size: 12px;
  color: #adb5bd;
  white-space: nowrap;
}

/* ---- 名称行 ---- */
.card-name-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 8px;
}

.card-name {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1.4;
}

.card-version {
  font-size: 13px;
  color: #adb5bd;
  flex-shrink: 0;
}

/* ---- 描述 ---- */
.card-desc {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  overflow: hidden;
  margin: 0 0 12px;
  flex: 1;
  min-height: 62px;
}

/* ---- 标签 ---- */
.card-labels {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 14px;
  min-height: 22px;
}

.label-key {
  font-size: 12px;
  color: #999;
}

.label-tag {
  background: #f0f2f5;
  border-color: #f0f2f5;
  color: #666;
  border-radius: 4px;
  padding: 0 6px;
}

.label-placeholder {
  display: block;
  height: 22px;
}

/* ---- 开发者 ---- */
.card-author {
  display: flex;
  align-items: center;
  gap: 8px;
  border-top: 1px solid #f0f2f5;
  padding-top: 12px;
  margin-top: auto;
}

.author-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  background: #e0ebff;
  display: flex;
  align-items: center;
  justify-content: center;

  .avatar-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .avatar-fallback {
    font-size: 12px;
    font-weight: 600;
    color: #3b82f6;
  }
}

.author-name {
  font-size: 13px;
  color: #555;
}
</style>
