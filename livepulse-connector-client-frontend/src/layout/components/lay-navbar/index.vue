<script setup lang="ts">
import { computed } from "vue";
import { useNav } from "@/layout/hooks/useNav";
import LaySearch from "../lay-search/index.vue";
import LayNotice from "../lay-notice/index.vue";
import LayNavMix from "../lay-sidebar/NavMix.vue";
import { useTranslationLang } from "@/layout/hooks/useTranslationLang";
import GlobalizationIcon from "@/assets/svg/globalization.svg?component";
import LaySidebarFullScreen from "../lay-sidebar/components/SidebarFullScreen.vue";
import { useUserStoreHook } from "@/store/modules/user";
import { useRouter } from "vue-router";
import { getUrl } from "@/utils/format";
import AccountSettingsIcon from "~icons/ri/user-settings-line";
import LogoutCircleRLine from "~icons/ri/logout-circle-r-line";
import Setting from "~icons/ri/settings-3-line";
import Check from "~icons/ep/check";

const router = useRouter();
const {
  logout,
  onPanel,
  userName,
  firstchar,
  userAvatar,
  toAccountSettings,
  getDropdownItemStyle,
  getDropdownItemClass
} = useNav();

const { t, locale, translationCh, translationEn } = useTranslationLang();
const userStore = useUserStoreHook();

const userType = computed(() => userStore.type ?? 0);
// 开发者(1)和管理员(2/3)显示控制台
const showConsole = computed(() => userType.value !== 0);

function goConsole() {
  router.push("/");
}
</script>

<template>
  <div class="navbar">
    <!-- 应用市场入口 -->
    <div>
      <!-- <button v-if="showConsole" class="console-btn" @click="goConsole">
      应用市场
    </button> -->
    </div>
    <div class="navbar-right">
      <!-- 菜单搜索 -->
      <LaySearch id="header-search" />
      <!-- 国际化 -->
      <el-dropdown id="header-translation" trigger="click">
        <div
          class="globalization-icon navbar-bg-hover hover:[&>svg]:animate-scale-bounce"
        >
          <IconifyIconOffline :icon="GlobalizationIcon" />
        </div>
        <template #dropdown>
          <el-dropdown-menu class="translation">
            <el-dropdown-item
              :style="getDropdownItemStyle(locale, 'zh')"
              :class="['dark:text-white!', getDropdownItemClass(locale, 'zh')]"
              @click="translationCh"
            >
              <IconifyIconOffline
                v-show="locale === 'zh'"
                class="check-zh"
                :icon="Check"
              />
              简体中文
            </el-dropdown-item>
            <el-dropdown-item
              :style="getDropdownItemStyle(locale, 'en')"
              :class="['dark:text-white!', getDropdownItemClass(locale, 'en')]"
              @click="translationEn"
            >
              <span v-show="locale === 'en'" class="check-en">
                <IconifyIconOffline :icon="Check" />
              </span>
              English
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <!-- 全屏 -->
      <LaySidebarFullScreen id="full-screen" />
      <!-- 消息通知 -->
      <LayNotice id="header-notice" />

      <!-- 用户下拉 -->
      <el-dropdown trigger="click">
        <span class="el-dropdown-link navbar-bg-hover select-none">
          <img v-if="userAvatar" :src="getUrl(userAvatar)" class="avatar-img" />
          <span v-else class="avatar-text">{{ firstchar }}</span>
          <p v-if="userName" class="userName-text">{{ userName }}</p>
        </span>
        <template #dropdown>
          <el-dropdown-menu class="logout">
            <el-dropdown-item @click="toAccountSettings">
              <el-icon style="margin: 5px"
                ><component :is="AccountSettingsIcon"
              /></el-icon>
              账户设置
            </el-dropdown-item>
            <el-dropdown-item @click="logout">
              <el-icon style="margin: 5px"
                ><component :is="LogoutCircleRLine"
              /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.navbar {
  width: 100%;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  box-sizing: border-box;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
  height: 48px;
}

.console-btn {
  padding: 5px 14px;
  font-size: 13px;
  font-weight: 500;
  color: #fff;
  background: var(--el-color-primary);
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: opacity 0.15s;
  white-space: nowrap;

  &:hover {
    opacity: 0.85;
  }
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  height: 48px;
  padding: 0 10px;
  cursor: pointer;
  gap: 8px;
  border-radius: 4px;

  &:hover {
    background: rgba(0, 0, 0, 0.04);
  }
}

.avatar-img {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-text {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  border: 1px solid #d9d9d9;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
}

.userName-text {
  font-size: 14px;
  color: #000000d9;
  margin: 0;
}

.translation {
  :deep(.el-dropdown-menu__item) {
    padding: 5px 40px;
  }

  .check-zh {
    position: absolute;
    left: 20px;
  }

  .check-en {
    position: absolute;
    left: 20px;
  }
}
.logout {
  width: 130px;

  :deep(.el-dropdown-menu__item) {
    display: inline-flex;
    align-items: center;
    min-width: 100%;
  }
}
</style>
