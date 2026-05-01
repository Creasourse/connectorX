<script setup lang="ts">
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useUserStoreHook } from "@/store/modules/user";
import { removeToken } from "@/utils/auth";
import { getUrl } from "@/utils/format";
import { useMultiTagsStoreHook } from "@/store/modules/multiTags";
import { resetRouter } from "@/router";
import { routerArrays } from "@/store/utils";
import AccountSettingsIcon from "~icons/ri/user-settings-line";
import LogoutCircleRLine from "~icons/ri/logout-circle-r-line";

defineOptions({ name: "AppLayout" });

const router = useRouter();
const userStore = useUserStoreHook();

const userId = computed(() => userStore.userId || null);
const userName = computed(() => userStore.nickName || userStore.userName || "");
const firstchar = computed(() =>
  userName.value ? userName.value[0].toUpperCase() : ""
);
const userAvatar = computed(() => userStore.avatar || "");
const isLoggedIn = computed(() => !!userId.value);

// type: 1=普通用户  2=开发者  3=超级管理员
const userType = computed(() => userStore.type ?? 1);
const showConsole = computed(() => userType.value !== 1);

function goConsole() {
  // 开发者和管理员都进入应用管理；管理员额外可从侧边栏访问审核中心
  router.push("/appManage/list");
}

function goHome() {
  router.push("/");
}

function goLogin() {
  router.push("/login");
}

function goRegister() {
  router.push("/login");
}

function toAccountSettings() {
  router.push({ name: "AccountSettings" });
}

function logout() {
  userStore.userName = "";
  userStore.roles = [];
  userStore.permissions = [];
  removeToken();
  useMultiTagsStoreHook().handleTags("equal", [...routerArrays]);
  resetRouter();
  router.push("/login");
}
</script>

<template>
  <div class="app-layout">
    <!-- ── Dark top navbar ── -->
    <header class="top-navbar">
      <!-- Left: logo + nav -->
      <div class="navbar-left">
        <div class="brand" @click="goHome">
          <div class="brand-icon">
            <img src="/logo.jpg" alt="logo" class="logo-img" />
          </div>
          <span class="brand-name">Connector-X</span>
        </div>
        <nav class="nav-links">
          <!-- <a class="nav-link hover" @click="goHome">插件市场</a> -->
        </nav>
      </div>

      <!-- Center: search -->
      <div class="navbar-center">
        <!-- <div class="search-bar">
          <iconify-icon-online icon="ri:search-line" class="search-icon" />
          <input class="search-input" placeholder="搜索插件..." />
        </div> -->
      </div>

      <!-- Right: auth -->
      <div class="navbar-right">
        <template v-if="!isLoggedIn">
          <router-link class="btn-register" to="/login">登录/注册</router-link>
          <!-- <a class="btn-login" @click="goLogin">登录/注册</a>
          <button class="btn-register" @click="goRegister">注册</button> -->
        </template>
        <template v-else>
          <button v-if="showConsole" class="btn-console" @click="goConsole">
            控制台
          </button>
          <el-dropdown trigger="click" placement="bottom-end">
            <span class="avatar-trigger">
              <img
                v-if="userAvatar"
                :src="getUrl(userAvatar)"
                class="user-avatar"
              />
              <span v-else class="user-avatar user-avatar--text">
                {{ firstchar }}
              </span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <div class="dropdown-userName">{{ userName }}</div>
                <el-dropdown-item @click="toAccountSettings">
                  <el-icon style="margin-right: 6px">
                    <component :is="AccountSettingsIcon" />
                  </el-icon>
                  账户设置
                </el-dropdown-item>
                <el-dropdown-item divided @click="logout">
                  <el-icon style="margin-right: 6px">
                    <component :is="LogoutCircleRLine" />
                  </el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </div>
    </header>

    <!-- Page content -->
    <main class="app-main">
      <router-view />
    </main>
  </div>
</template>

<style lang="scss" scoped>
.app-layout {
  display: flex;
  flex-direction: column;
  width: 100%;
  min-height: 100vh;
  background: var(--el-bg-color-page);
}

/* ── Navbar ── */
.top-navbar {
  display: flex;
  align-items: center;
  width: 100%;
  height: 56px;
  padding: 0 28px;
  background: #18181b;
  color: #fff;
  gap: 24px;
  box-sizing: border-box;
  flex-shrink: 0;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 32px;
  flex-shrink: 0;
}

.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.brand-icon {
  width: 34px;
  height: 34px;
  border-radius: 8px;
  overflow: hidden;
  border: 1.5px solid rgba(255, 255, 255, 0.15);
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #27272a;
}

.logo-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.brand-name {
  font-size: 15px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0.03em;
  white-space: nowrap;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 4px;
}

.nav-link {
  padding: 6px 12px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.75);
  border-radius: 6px;
  cursor: pointer;
  transition:
    color 0.15s,
    background 0.15s;
  white-space: nowrap;
  text-decoration: none;
  user-select: none;
  &.hover,
  &:hover {
    color: #fff;
    background: rgba(255, 255, 255, 0.08);
  }
}

.navbar-center {
  flex: 1;
  display: flex;
  justify-content: center;
  min-width: 0;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  max-width: 480px;
  height: 36px;
  padding: 0 14px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 8px;
  transition:
    border-color 0.2s,
    background 0.2s;

  &:focus-within {
    border-color: rgba(255, 255, 255, 0.3);
    background: rgba(255, 255, 255, 0.11);
  }
}

.search-icon {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.45);
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  font-size: 14px;
  color: #fff;
  min-width: 0;

  &::placeholder {
    color: rgba(255, 255, 255, 0.35);
  }
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.btn-login {
  padding: 7px 18px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
  background: transparent;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition:
    color 0.15s,
    background 0.15s;
  white-space: nowrap;

  &:hover {
    color: #fff;
    background: rgba(255, 255, 255, 0.08);
  }
}

.btn-register {
  padding: 7px 18px;
  font-size: 14px;
  font-weight: 500;
  color: #18181b;
  background: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.15s;
  white-space: nowrap;

  &:hover {
    background: #e4e4e7;
  }
}

.btn-console {
  padding: 7px 16px;
  font-size: 13px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.85);
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 8px;
  cursor: pointer;
  transition:
    background 0.15s,
    border-color 0.15s;
  white-space: nowrap;

  &:hover {
    background: rgba(255, 255, 255, 0.16);
    border-color: rgba(255, 255, 255, 0.25);
    color: #fff;
  }
}

.avatar-trigger {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.2);
  transition: border-color 0.15s;
  display: flex;
  align-items: center;
  justify-content: center;

  &--text {
    background: #3b82f6;
    color: #fff;
    font-size: 13px;
    font-weight: 600;
  }

  &:hover {
    border-color: rgba(255, 255, 255, 0.5);
  }
}

.dropdown-userName {
  padding: 8px 16px 4px;
  font-size: 13px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  border-bottom: 1px solid var(--el-border-color-lighter);
  margin-bottom: 4px;
}

/* ── Content ── */
.app-main {
  flex: 1;
  min-height: 0;
  padding: 20px 28px;
}
</style>
