<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import { getAppDetail, getConnectorVersionList } from "@/api/app";
import type { AppItem, VersionItem } from "@/api/app";
import { formatDate, fromNow } from "@/utils/format";
import MarkdownEditor from "@/components/MarkdownEdit.vue";
import { getUrl } from "@/utils/format";

defineOptions({ name: "AppDetail" });

const route = useRoute();

const app = ref<AppItem | null>(null);
const activeTab = ref("intro");
const versionList = ref<VersionItem[]>([]);
const loading = ref(false);

const appId = computed(() => {
  const id = route.params.id;
  return id ? Number(id) : null;
});
const gitUrl = computed(() => {
  const { locationUrl } = app.value;
  const git = "https://github.com/";
  return locationUrl ? `${git}${locationUrl}` : "-";
});
const getGitUrl = v => {
  const git = "https://github.com/";
  return v ? `${git}${v}` : "-";
};

onMounted(async () => {
  if (appId.value) {
    loading.value = true;
    await getAppDetail(appId.value)
      .then(res => {
        if (res.success && res.data) {
          app.value = res.data;
        }
      })
      .finally(() => {
        loading.value = false;
      });
    const params = {
      pageQueryParam: {
        pageNo: 1,
        pageSize: 100
      },
      // connectorId: app.value.connPluginId,
      connectorName: app.value.connPluginName,
      sortType: 2
    };
    getConnectorVersionList(params).then(res => {
      if (res.success && res.data) {
        versionList.value = res.data.list || [];
      }
    });
  } else {
    try {
      const data = JSON.parse(
        sessionStorage.getItem("session_app_detail") || "null"
      );
      app.value = data;
    } catch {}
  }
});

const updateTimeText = computed(() => {
  if (!app.value?.updateTime) return "";
  return formatDate(app.value.updateTime, "YYYY年MM月DD日");
});

const tags = computed(() => {
  if (!app.value?.connLabelName) return [];
  return app.value.connLabelName.split(",").filter(Boolean);
});

const isTheme = computed(() => app.value?.groupName === "主题");

const downloadApp = () => {};

// Parse remark into sections: { heading, items[] }[]
function parseRemark(remark: string) {
  if (!remark) return [];
  const lines = remark.split(/\r?\n/);
  const sections: { heading: string; items: string[] }[] = [];
  let current: { heading: string; items: string[] } | null = null;
  for (const line of lines) {
    const trimmed = line.trim();
    if (!trimmed) continue;
    if (!trimmed.startsWith("-") && !trimmed.startsWith("•")) {
      if (current) sections.push(current);
      current = { heading: trimmed, items: [] };
    } else {
      if (!current) current = { heading: "", items: [] };
      current.items.push(trimmed.replace(/^[-•]\s*/, ""));
    }
  }
  if (current) sections.push(current);
  return sections;
}
</script>

<template>
  <div v-loading="loading" class="detail-page">
    <el-empty v-if="!loading && !app" description="应用不存在" />

    <template v-else-if="app">
      <!-- ===== Hero 区域 ===== -->
      <div class="hero">
        <div class="hero-left">
          <!-- 图标 -->
          <div class="hero-icon">
            <img
              v-if="app.icon"
              :src="getUrl(app.icon)"
              class="hero-icon__img"
            />
            <span v-else class="hero-icon__fallback">
              {{ app.connPluginName?.[0] || "A" }}
            </span>
          </div>

          <!-- 名称 + 描述 + 统计 -->
          <div class="hero-info">
            <div class="hero-name-row">
              <h1 class="hero-name">{{ app.connPluginName }}</h1>
              <span class="official-badge">官方</span>
            </div>
            <p class="hero-desc">{{ app.sketchOut }}</p>
            <!-- <div class="hero-stats">
              <span class="stat-item">
                <svg
                  viewBox="0 0 16 16"
                  width="14"
                  height="14"
                  fill="currentColor"
                >
                  <path d="M8 12L1 5h14L8 12z" />
                </svg>
                下载量 <strong>—</strong>
              </span>
              <span class="stat-divider">|</span>
              <span class="stat-item">
                <svg
                  viewBox="0 0 16 16"
                  width="14"
                  height="14"
                  fill="currentColor"
                >
                  <path
                    d="M14 1H2a1 1 0 00-1 1v8a1 1 0 001 1h2v3l4-3h6a1 1 0 001-1V2a1 1 0 00-1-1z"
                  />
                </svg>
                反馈 <strong>—</strong>
              </span>
              <span class="stat-divider">|</span>
              <span class="stat-item">
                <svg viewBox="0 0 16 16" width="14" height="14" fill="#f59e0b">
                  <path
                    d="M8 1l2.163 4.279L15 6.118l-3.5 3.312.826 4.57L8 11.779l-4.326 2.221.826-4.57L1 6.118l4.837-.839z"
                  />
                </svg>
                评分 <strong>—</strong>
              </span>
            </div> -->
          </div>
        </div>

        <!-- 右侧操作 -->
        <div class="hero-actions">
          <a class="download-btn" :href="gitUrl" target="_blank">
            <svg
              viewBox="0 0 24 24"
              width="16"
              height="16"
              fill="currentColor"
              style="margin-right: 6px"
            >
              <path d="M12 16l-5-5h3V4h4v7h3l-5 5zm-7 4h14v-2H5v2z" />
            </svg>
            下载应用
          </a>
          <p v-if="updateTimeText" class="last-update">
            最后更新于 {{ updateTimeText }}
          </p>
        </div>
      </div>

      <!-- ===== 主体：左侧边栏 + 右侧主内容 ===== -->
      <div class="main-layout">
        <!-- 左侧边栏 -->
        <aside class="sidebar">
          <h3 class="sidebar-title">应用信息</h3>

          <div class="sidebar-section">
            <div class="sidebar-label">发布者</div>
            <div class="publisher">
              <div class="publisher-avatar">
                <img
                  v-if="app.avatar"
                  :src="getUrl(app.avatar)"
                  class="publisher-avatar__img"
                />
                <span v-else class="publisher-avatar__fallback">
                  {{ app.developUserName?.[0] || "U" }}
                </span>
              </div>
              <span class="publisher-name">{{ app.developUserName }}</span>
            </div>
          </div>

          <div class="sidebar-section">
            <div class="sidebar-label">版本</div>
            <el-tag size="small" class="version-tag">{{
              app.version || "—"
            }}</el-tag>
          </div>

          <div v-if="tags.length" class="sidebar-section">
            <div class="sidebar-label">应用标签</div>
            <div class="tag-list">
              <el-tag v-for="tag in tags" :key="tag">
                {{ tag }}
              </el-tag>
            </div>
          </div>

          <div class="sidebar-section">
            <div class="sidebar-label">类型</div>
            <span
              class="type-text"
              :class="isTheme ? 'type-text--theme' : 'type-text--plugin'"
            >
              {{ app.groupName || "插件" }}
            </span>
          </div>

          <div v-if="app.locationUrl" class="sidebar-section">
            <div class="sidebar-label">仓库信息</div>
            <a :href="gitUrl" target="_blank" rel="noopener" class="repo-link">
              <svg
                viewBox="0 0 16 16"
                width="16"
                height="16"
                fill="currentColor"
              >
                <path
                  d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.013 8.013 0 0016 8c0-4.42-3.58-8-8-8z"
                />
              </svg>
              <span class="break-all">{{ app.locationUrl }}</span>
            </a>
          </div>
        </aside>

        <!-- 右侧主内容 -->
        <div class="main-content">
          <el-tabs v-model="activeTab" class="detail-tabs">
            <el-tab-pane label="简介" name="intro">
              <MarkdownEditor v-model="app.remark" mode="preview" />
            </el-tab-pane>

            <el-tab-pane label="版本历史" name="version">
              <div v-if="versionList.length === 0" class="version-empty">
                <el-empty description="暂无版本记录" />
              </div>
              <div v-else class="version-list">
                <div
                  v-for="(ver, idx) in versionList"
                  :key="ver.connectorVersionId"
                  class="version-row"
                >
                  <!-- 左侧版本摘要 -->
                  <div class="ver-sidebar">
                    <div class="ver-num">{{ ver.version }}</div>
                    <div class="ver-author">{{ ver.developUserName }}</div>
                    <div class="ver-time">
                      发布于
                      {{ ver.createTime ? fromNow(ver.createTime) : "—" }}
                    </div>
                  </div>

                  <!-- 右侧内容卡片 -->
                  <div class="ver-card">
                    <!-- 卡片头 -->
                    <div class="ver-card-header">
                      <div class="ver-card-title-group">
                        <span class="ver-card-title"
                          >Release v{{ ver.version }}</span
                        >
                        <span v-if="idx === 0" class="ver-latest-badge"
                          >最新</span
                        >
                      </div>
                      <span v-if="ver.connLabelName" class="ver-compat">
                        {{ ver.connLabelName }}
                      </span>
                    </div>

                    <!-- Changelog 内容 -->
                    <div class="ver-card-body">
                      <template v-if="parseRemark(ver.remark || '').length">
                        <MarkdownEditor v-model="ver.remark" mode="preview" />
                      </template>
                      <p v-else class="changelog-raw">
                        {{ ver.remark || "暂无更新说明" }}
                      </p>
                    </div>

                    <!-- 分隔线 + Generated from -->
                    <div class="ver-card-footer">
                      <p class="ver-generated">
                        Generated from
                        <span class="ver-generated-link"
                          >v{{ ver.version }}</span
                        >
                      </p>
                    </div>

                    <!-- 资源下载 -->
                    <div v-if="ver.locationUrl" class="ver-download-section">
                      <h4 class="ver-download-title">
                        <svg
                          viewBox="0 0 24 24"
                          width="16"
                          height="16"
                          fill="none"
                          stroke="currentColor"
                          stroke-width="2"
                          style="margin-right: 6px; vertical-align: middle"
                        >
                          <path
                            d="M12 16l-5-5h3V4h4v7h3l-5 5zm-7 4h14v-2H5v2z"
                          />
                        </svg>
                        资源下载
                      </h4>
                      <div class="ver-download-item">
                        <div class="ver-download-info">
                          <div class="ver-download-name">
                            {{ ver.connPluginName }}-{{ ver.version }}.zip
                          </div>
                          <div class="ver-download-size">—</div>
                        </div>
                        <a
                          :href="getGitUrl(ver.locationUrl)"
                          target="_blank"
                          rel="noopener"
                          class="ver-download-link"
                          @click.stop
                        >
                          下载
                        </a>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>

            <!-- <el-tab-pane label="评论" name="review">
              <el-empty description="暂无评论" />
            </el-tab-pane> -->
          </el-tabs>
        </div>
      </div>
    </template>
  </div>
</template>

<style lang="scss" scoped>
.detail-page {
  padding: 32px 40px;
  max-width: 1280px;
  margin: 0 auto;
  background: var(--el-bg-color-page);
  min-height: 100%;

  @media (max-width: 768px) {
    padding: 16px 14px;
  }
}

/* ===== Hero ===== */
.hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  padding-bottom: 32px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  margin-bottom: 32px;

  @media (max-width: 640px) {
    flex-direction: column;
    gap: 16px;
    padding-bottom: 20px;
    margin-bottom: 20px;
  }
}

.hero-left {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  flex: 1;
  min-width: 0;
}

.hero-icon {
  width: 88px;
  height: 88px;
  border-radius: 20px;
  overflow: hidden;
  flex-shrink: 0;
  background: #e8f0fe;
  display: flex;
  align-items: center;
  justify-content: center;

  &__img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  &__fallback {
    font-size: 32px;
    font-weight: 700;
    color: #4080ff;
  }

  @media (max-width: 480px) {
    width: 64px;
    height: 64px;
    border-radius: 14px;

    &__fallback {
      font-size: 24px;
    }
  }
}

.hero-info {
  flex: 1;
  min-width: 0;
}

.hero-name-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.hero-name {
  margin: 0;
  font-size: 26px;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1.3;

  @media (max-width: 480px) {
    font-size: 20px;
  }
}

.official-badge {
  display: inline-block;
  padding: 2px 10px;
  background: #e8f5e9;
  color: #2e7d32;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  flex-shrink: 0;
}

.hero-desc {
  margin: 0 0 14px;
  font-size: 14px;
  color: #555;
  line-height: 1.7;
  max-width: 680px;
}

.hero-stats {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.stat-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #666;

  strong {
    color: #333;
    font-weight: 600;
  }

  svg {
    color: #999;
    flex-shrink: 0;
  }
}

.stat-divider {
  color: #ddd;
  font-size: 13px;
}

/* 右侧操作 */
.hero-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  flex-shrink: 0;

  @media (max-width: 640px) {
    flex-direction: row;
    align-items: center;
    width: 100%;
    justify-content: space-between;
  }
}

.download-btn {
  height: 44px;
  padding: 0 24px;
  font-size: 15px;
  font-weight: 600;
  background: #f97316;
  border-color: #f97316;
  color: #fff;
  border-radius: 10px;
  display: flex;
  align-items: center;

  &:hover,
  &:focus {
    background: #ea6c0a;
    border-color: #ea6c0a;
    color: #fff;
  }
}

.last-update {
  margin: 0;
  font-size: 12px;
  color: #aaa;
}

/* ===== 主体布局 ===== */
.main-layout {
  display: flex;
  align-items: flex-start;
  gap: 40px;

  @media (max-width: 768px) {
    flex-direction: column;
    gap: 0;
  }
}

/* ===== 左侧边栏 ===== */
.sidebar {
  width: 200px;
  flex-shrink: 0;

  @media (max-width: 768px) {
    width: 100%;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 0 16px;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 10px;
    padding: 16px;
    background: var(--el-bg-color);
    margin-bottom: 20px;
  }

  @media (max-width: 480px) {
    grid-template-columns: 1fr;
  }
}

.sidebar-title {
  margin: 0 0 20px;
  font-size: 14px;
  font-weight: 600;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 0.05em;

  @media (max-width: 768px) {
    display: none;
  }
}

.sidebar-section {
  margin-bottom: 20px;
}

.sidebar-label {
  font-size: 12px;
  color: #aaa;
  margin-bottom: 6px;
}

.publisher {
  display: flex;
  align-items: center;
  gap: 8px;
}

.publisher-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  overflow: hidden;
  background: #dbeafe;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  &__img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  &__fallback {
    font-size: 12px;
    font-weight: 600;
    color: #3b82f6;
  }
}

.publisher-name {
  font-size: 14px;
  color: #3b82f6;
  font-weight: 500;
}

.version-tag {
  background: #e8f0fe;
  border-color: #e8f0fe;
  color: #3b5bdb;
  font-size: 12px;
  border-radius: 4px;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.app-label-tag {
  background: #f0f2f5;
  border-color: #f0f2f5;
  color: #555;
  border-radius: 4px;
}

.type-text {
  font-size: 13px;
  font-weight: 500;

  &--plugin {
    color: #4080ff;
  }

  &--theme {
    color: #f97316;
  }
}

.repo-link {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  font-size: 13px;
  color: #333;
  text-decoration: none;
  font-weight: 500;

  &:hover {
    color: #4080ff;
  }
}

/* ===== 右侧主内容 ===== */
.main-content {
  flex: 1;
  min-width: 0;
}

.detail-tabs {
  :deep(.el-tabs__nav-wrap::after) {
    height: 1px;
    background: var(--el-border-color-lighter);
  }

  :deep(.el-tabs__item) {
    font-size: 15px;
    color: #888;
    padding: 0 20px;

    &.is-active {
      color: #4080ff;
      font-weight: 600;
    }
  }

  :deep(.el-tabs__active-bar) {
    background: #4080ff;
    height: 2px;
    border-radius: 1px;
  }

  :deep(.el-tabs__content) {
    padding-top: 24px;
  }
}

/* 简介 meta bar */
.intro-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 28px;
}

.intro-type-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;

  &--plugin {
    background: #4080ff;
    color: #fff;
  }

  &--theme {
    background: #f97316;
    color: #fff;
  }
}

.intro-update {
  font-size: 13px;
  color: #888;
}

/* 功能特性 */
.section-heading {
  margin: 0 0 16px;
  font-size: 18px;
  font-weight: 700;
  color: #1a1a1a;
}

.feature-section {
  margin-bottom: 36px;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1px;
  border: 1px solid #e8eaef;
  border-radius: 10px;
  overflow: hidden;
  background: #e8eaef;

  @media (max-width: 480px) {
    grid-template-columns: 1fr;
  }
}

.feature-card {
  background: #fff;
  padding: 20px 24px;
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.feature-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: #f0f4ff;
  color: #4080ff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.feature-title {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 4px;
}

.feature-desc {
  font-size: 13px;
  color: #888;
}

/* 描述 */
.desc-section {
  margin-bottom: 32px;
}

.desc-text {
  font-size: 14px;
  color: #555;
  line-height: 1.8;
  margin: 0;
}

/* ===== 版本历史 ===== */
.version-empty {
  padding: 40px 0;
}

.version-list {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.version-row {
  display: flex;
  gap: 32px;
  align-items: flex-start;
  border-top: 1px solid #e4e7ed;
  padding-top: 32px;

  @media (max-width: 640px) {
    flex-direction: column;
    gap: 8px;
    padding-top: 8px;
  }
}

/* 左侧摘要 */
.ver-sidebar {
  width: 120px;
  flex-shrink: 0;
  padding-top: 20px;

  @media (max-width: 640px) {
    width: 100%;
    padding-top: 0;
    display: flex;
    align-items: center;
    gap: 12px;

    .ver-num {
      font-size: 15px;
      margin-bottom: 0;
    }

    .ver-author {
      margin-bottom: 0;
    }
  }
}

.ver-num {
  font-size: 16px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 4px;
}

.ver-author {
  font-size: 13px;
  color: #555;
  margin-bottom: 4px;
}

.ver-time {
  font-size: 12px;
  color: #aaa;
}

/* 右侧卡片 */
.ver-card {
  flex: 1;
  min-width: 0;
  border: 1px solid #e4e7ed;
  border-radius: 10px;
  overflow: hidden;
  background: #fff;
}

.ver-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 24px;
  border-bottom: 1px solid #e4e7ed;
  flex-wrap: wrap;
  gap: 8px;

  @media (max-width: 480px) {
    padding: 14px 16px;
  }
}

.ver-card-title-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.ver-card-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;

  @media (max-width: 480px) {
    font-size: 16px;
  }
}

.ver-latest-badge {
  display: inline-block;
  padding: 2px 10px;
  background: #e8f0fe;
  color: #4080ff;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.ver-compat {
  font-size: 14px;
  color: #888;
}

/* Changelog 主体 */
.ver-card-body {
  padding: 20px 24px;

  @media (max-width: 480px) {
    padding: 14px 16px;
  }
}

.changelog-section {
  margin-bottom: 16px;

  &:last-child {
    margin-bottom: 0;
  }
}

.changelog-heading {
  margin: 0 0 10px;
  font-size: 16px;
  font-weight: 700;
  color: #1a1a1a;
}

.changelog-list {
  margin: 0;
  padding-left: 20px;
  list-style: disc;

  li {
    font-size: 14px;
    color: #333;
    line-height: 1.7;
  }
}

.changelog-raw {
  font-size: 14px;
  color: #555;
  line-height: 1.8;
  margin: 0;
  white-space: pre-wrap;
}

/* Footer: Generated from */
.ver-card-footer {
  padding: 14px 24px;
  border-top: 1px solid #e4e7ed;
  background: #fafbfc;
}

.ver-generated {
  margin: 0;
  font-size: 13px;
  color: #888;
  font-style: italic;
}

.ver-generated-link {
  color: #4080ff;
  font-style: normal;
}

/* 资源下载 */
.ver-download-section {
  padding: 16px 24px 20px;
  border-top: 1px solid #e4e7ed;

  @media (max-width: 480px) {
    padding: 12px 16px 16px;
  }
}

.ver-download-title {
  display: flex;
  align-items: center;
  margin: 0 0 12px;
  font-size: 15px;
  font-weight: 600;
  color: #1a1a1a;
}

.ver-download-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background: #fafbfc;
}

.ver-download-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.ver-download-name {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
}

.ver-download-size {
  font-size: 12px;
  color: #aaa;
}

.ver-download-link {
  font-size: 14px;
  color: #4080ff;
  font-weight: 500;
  text-decoration: none;

  &:hover {
    text-decoration: underline;
  }
}
</style>
