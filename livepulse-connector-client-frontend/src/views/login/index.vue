<script setup lang="ts">
import { useI18n } from "vue-i18n";
import Motion from "./utils/motion";
import { useRouter, useRoute } from "vue-router";
import { message } from "@/utils/message";
import { loginRules } from "./utils/rule";
import TypeIt from "@/components/ReTypeit";
import { debounce } from "@pureadmin/utils";
import { useNav } from "@/layout/hooks/useNav";
import { useEventListener } from "@vueuse/core";
import type { FormInstance } from "element-plus";
import { $t, transformI18n } from "@/plugins/i18n";
import { operates, thirdParty } from "./utils/enums";
import { useLayout } from "@/layout/hooks/useLayout";
import LoginPhone from "./components/LoginPhone.vue";
import LoginRegist from "./components/LoginRegist.vue";
import LoginUpdate from "./components/LoginUpdate.vue";
import LoginQrCode from "./components/LoginQrCode.vue";
import { useUserStoreHook } from "@/store/modules/user";
import { initRouter, getTopMenu } from "@/router/utils";
import { logo, avatar } from "./utils/static";
import { ReImageVerify } from "@/components/ReImageVerify";
import { ref, toRaw, reactive, watch, computed } from "vue";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import { useTranslationLang } from "@/layout/hooks/useTranslationLang";
import { useDataThemeChange } from "@/layout/hooks/useDataThemeChange";

import dayIcon from "@/assets/svg/day.svg?component";
import darkIcon from "@/assets/svg/dark.svg?component";
import globalization from "@/assets/svg/globalization.svg?component";
import gitee from "@/assets/login/gitee.svg?component";
import github from "@/assets/login/github.svg?component";
import Lock from "~icons/ri/lock-fill";
import Check from "~icons/ep/check";
import User from "~icons/ri/user-3-fill";
import Info from "~icons/ri/information-line";
import Keyhole from "~icons/ri/shield-keyhole-line";

defineOptions({
  name: "Login"
});

const imgCode = ref("");
const loginDay = ref(7);
const router = useRouter();
const route = useRoute();
const loading = ref(false);
const checked = ref(false);
const disabled = ref(false);
const ruleFormRef = ref<FormInstance>();
const currentPage = computed(() => {
  return useUserStoreHook().currentPage;
});
if (route.query.type)
  useUserStoreHook().SET_CURRENTPAGE(Number(route.query.type));

const { t } = useI18n();
const { initStorage } = useLayout();
initStorage();
const { dataTheme, themeMode, dataThemeChange } = useDataThemeChange();
dataThemeChange(themeMode.value);
const { title, getDropdownItemStyle, getDropdownItemClass } = useNav();
const { locale, translationCh, translationEn } = useTranslationLang();

const ruleForm = reactive({
  userName: "",
  password: "",
  verifyCode: ""
});

const onLogin = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(valid => {
    if (valid) {
      loading.value = true;
      useUserStoreHook()
        .loginByUsername({
          userName: ruleForm.userName,
          password: ruleForm.password
        })
        .then(async res => {
          if (res.responseStatus === 200 && res.success) {
            // 获取后端路由
            await initRouter();
            disabled.value = true;
            router.push("/appManage/list").then(() => {
              message(t("login.pureLoginSuccess"), { type: "success" });
            });
          } else {
            message(t("login.pureLoginFail"), { type: "error" });
          }
        })
        .finally(() => {
          disabled.value = false;
          loading.value = false;
        });
    }
  });
};

const immediateDebounce: any = debounce(
  formRef => onLogin(formRef),
  1000,
  true
);

useEventListener(document, "keydown", ({ code }) => {
  if (
    ["Enter", "NumpadEnter"].includes(code) &&
    !disabled.value &&
    !loading.value
  )
    immediateDebounce(ruleFormRef.value);
});

watch(imgCode, value => {
  useUserStoreHook().SET_VERIFYCODE(value);
});
watch(checked, bool => {
  useUserStoreHook().SET_ISREMEMBERED(bool);
});
watch(loginDay, value => {
  useUserStoreHook().SET_LOGINDAY(value);
});
</script>

<template>
  <div class="select-none">
    <div class="login-container">
      <div class="login-box">
        <div class="login-form py-5">
          <img :src="logo" class="avatar" />
          <!-- <Motion>
            <h2 class="outline-hidden">
              <TypeIt
                :options="{ strings: [title], cursor: false, speed: 100 }"
              />
            </h2>
          </Motion> -->

          <div class="login-wrap">
            <el-form
              v-if="currentPage === 0"
              ref="ruleFormRef"
              :model="ruleForm"
              :rules="loginRules"
              :hide-required-asterisk="true"
              label-position="top"
              size="large"
            >
              <Motion :delay="100">
                <el-form-item
                  :label="t('login.pureUsername')"
                  :rules="[
                    {
                      required: true,
                      message: transformI18n($t('login.pureUsernameReg')),
                      trigger: 'blur'
                    }
                  ]"
                  prop="userName"
                >
                  <el-input
                    v-model="ruleForm.userName"
                    clearable
                    :placeholder="t('login.pureUsername')"
                    :prefix-icon="useRenderIcon(User)"
                  />
                </el-form-item>
              </Motion>

              <Motion :delay="150">
                <el-form-item :label="t('login.purePassword')" prop="password">
                  <template v-slot:label>
                    <div class="flex justify-between">
                      <span class="tit">{{ t("login.purePassword") }}</span>
                      <el-button
                        link
                        type="primary"
                        @click="useUserStoreHook().SET_CURRENTPAGE(2)"
                      >
                        {{ t("login.pureForget") }}
                      </el-button>
                    </div>
                  </template>
                  <el-input
                    v-model="ruleForm.password"
                    clearable
                    show-password
                    :placeholder="t('login.purePassword')"
                    :prefix-icon="useRenderIcon(Lock)"
                  />
                </el-form-item>
              </Motion>

              <!-- <Motion :delay="200">
                <el-form-item
                  :label="t('login.pureVerifyCode')"
                  prop="verifyCode"
                >
                  <el-input
                    v-model="ruleForm.verifyCode"
                    clearable
                    :placeholder="t('login.pureVerifyCode')"
                    :prefix-icon="useRenderIcon(Keyhole)"
                  >
                    <template v-slot:append>
                      <ReImageVerify v-model:code="imgCode" />
                    </template>
                  </el-input>
                </el-form-item>
              </Motion> -->

              <Motion :delay="250">
                <el-form-item>
                  <div class="w-full h-5 flex-bc">
                    <el-checkbox v-model="checked">
                      <span class="flex">
                        <!-- <select
                          v-model="loginDay"
                          :style="{
                            width: loginDay < 10 ? '10px' : '16px',
                            outline: 'none',
                            background: 'none',
                            appearance: 'none',
                            border: 'none'
                          }"
                        >
                          <option value="1">1</option>
                          <option value="7">7</option>
                          <option value="30">30</option>
                        </select> -->
                        {{ t("login.pureRemember") }}
                        <!-- <IconifyIconOffline
                          v-tippy="{
                            content: t('login.pureRememberInfo'),
                            placement: 'top'
                          }"
                          :icon="Info"
                          class="ml-1"
                        /> -->
                      </span>
                    </el-checkbox>
                  </div>
                  <el-button
                    class="w-full mt-4!"
                    size="default"
                    type="primary"
                    :loading="loading"
                    :disabled="disabled"
                    @click="onLogin(ruleFormRef)"
                  >
                    {{ t("login.pureLogin") }}
                  </el-button>
                </el-form-item>
              </Motion>

              <!-- <Motion :delay="300">
                <el-form-item>
                  <div class="w-full h-5 flex-bc">
                    <el-button
                      v-for="(item, index) in operates"
                      :key="index"
                      class="w-full mt-4!"
                      size="default"
                      @click="useUserStoreHook().SET_CURRENTPAGE(index + 1)"
                    >
                      {{ t(item.title) }}
                    </el-button>
                  </div>
                </el-form-item>
              </Motion> -->
            </el-form>

            <Motion v-if="currentPage === 0" :delay="350">
              <el-form-item>
                <el-divider>
                  <p class="text-gray-500 text-xs">
                    {{ t("login.pureThirdLogin") }}
                  </p>
                </el-divider>
                <div class="w-full flex justify-center">
                  <el-button plain class="flex-1">
                    <gitee class="size-4" />
                    <span class="ml-1 text-[#1f2937]">Gitee</span>
                  </el-button>
                  <el-button plain class="flex-1">
                    <github class="size-4" />
                    <span class="ml-1 text-[#1f2937]">GitHub</span>
                  </el-button>
                </div>
              </el-form-item>
            </Motion>
            <!-- 手机号登录 -->
            <!-- <LoginPhone v-if="currentPage === 1" /> -->
            <!-- 二维码登录 -->
            <!-- <LoginQrCode v-if="currentPage === 2" /> -->
            <!-- 注册 -->
            <LoginRegist
              v-if="currentPage === 1"
              @update="useUserStoreHook().SET_CURRENTPAGE(0)"
            />
            <!-- 忘记密码 -->
            <LoginUpdate
              v-if="currentPage === 2"
              @update="useUserStoreHook().SET_CURRENTPAGE(0)"
            />
          </div>

          <div v-if="currentPage !== 1" class="text-center text-sm my-4">
            <div>
              {{ t("login.pureNoAccount") }}
              <el-button
                link
                type="primary"
                class="text-primary"
                @click="useUserStoreHook().SET_CURRENTPAGE(1)"
                >{{ t("login.pureRegister") }}</el-button
              >
            </div>
          </div>
          <div v-if="currentPage !== 0" class="text-center text-sm my-4">
            <el-button
              link
              type="primary"
              class="text-primary"
              @click="useUserStoreHook().SET_CURRENTPAGE(0)"
            >
              {{ t("login.pureLogin") }}
            </el-button>
          </div>

          <div class="flex-c">
            <!-- 主题 -->
            <!-- <el-switch
              v-model="dataTheme"
              inline-prompt
              :active-icon="dayIcon"
              :inactive-icon="darkIcon"
              @change="dataThemeChange"
            /> -->
            <!-- 国际化 -->
            <el-dropdown trigger="click">
              <globalization
                class="hover:text-primary hover:bg-transparent! size-5 ml-1.5 cursor-pointer outline-hidden duration-300"
              />
              <template #dropdown>
                <el-dropdown-menu class="translation">
                  <el-dropdown-item
                    :style="getDropdownItemStyle(locale, 'zh')"
                    :class="[
                      'dark:text-white!',
                      getDropdownItemClass(locale, 'zh')
                    ]"
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
                    :class="[
                      'dark:text-white!',
                      getDropdownItemClass(locale, 'en')
                    ]"
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
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import url("@/style/login.css");
</style>

<style lang="scss" scoped>
:deep(.el-form-item--label-top .el-form-item__label) {
  width: auto;
}

:deep(.el-input-group__append, .el-input-group__prepend) {
  padding: 0;
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
</style>
