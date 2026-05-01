<script setup lang="ts">
import { useI18n } from "vue-i18n";
import { ref, reactive } from "vue";
import Motion from "../utils/motion";
import { message } from "@/utils/message";
import { updateRules } from "../utils/rule";
import type { FormInstance } from "element-plus";
import { useVerifyCode } from "../utils/verifyCode";
import { $t, transformI18n } from "@/plugins/i18n";
import { useUserStoreHook } from "@/store/modules/user";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import Lock from "~icons/ri/lock-fill";
import Iphone from "~icons/ep/iphone";
import Email from "~icons/ic/baseline-email";
import User from "~icons/ri/user-3-fill";
import Keyhole from "~icons/ri/shield-keyhole-line";

const emit = defineEmits(["update"]);

const { t } = useI18n();
const checked = ref(false);
const loading = ref(false);
const ruleForm = reactive({
  userId: null,
  userName: "",
  nickName: "",
  phone: "",
  email: "",
  type: 2, // 1:客户 2:开发者 3:管理者
  salt: "",
  verifyCode: "",
  password: "",
  repeatPassword: ""
});
const ruleFormRef = ref<FormInstance>();
const { isDisabled, text } = useVerifyCode();
const repeatPasswordRule = [
  {
    validator: (rule, value, callback) => {
      if (value === "") {
        callback(new Error(transformI18n($t("login.purePassWordSureReg"))));
      } else if (ruleForm.password !== value) {
        callback(
          new Error(transformI18n($t("login.purePassWordDifferentReg")))
        );
      } else {
        callback();
      }
    },
    trigger: "blur"
  }
];

const getSms = async () => {
  const { phone } = ruleForm;
  if (!phone) {
    return;
  }
  useVerifyCode().start(ruleFormRef.value, "phone");
  const params = {
    phone,
    code: "验证码",
    type: 1 // 1:注册 2:忘记密码
  };
  useUserStoreHook().getSmsByPhone(params);
};

const onUpdate = async (formEl: FormInstance | undefined) => {
  loading.value = true;
  if (!formEl) return;
  await formEl.validate(async valid => {
    if (valid) {
      if (checked.value) {
        const { userName, nickName, phone, verifyCode, email, type, password } =
          ruleForm;
        const { checkSmsForPhone, register } = useUserStoreHook();
        register({ userName, nickName, phone, email, type, password })
          .then(res2 => {
            if (res2.success) {
              if (res2.data) {
                message(transformI18n($t("login.pureRegisterSuccess")), {
                  type: "success"
                });
                emit("update");
              } else {
                message(transformI18n($t("login.pureRegisterFaild")), {
                  type: "error"
                });
              }
            } else {
              message(res2.errorMsg, {
                type: "error"
              });
            }
            loading.value = false;
          })
          .catch(error2 => {
            message(error2.errorMsg, {
              type: "error"
            });
            loading.value = false;
          });
        // checkSmsForPhone({
        //   phone,
        //   code: verifyCode,
        //   type: 1 // 验证码类型 1:注册 2:忘记密码
        // })
        //   .then(res => {
        //     if (res.success && res.data) {
        //     } else {
        //       loading.value = false;
        //       message(transformI18n($t("login.pureVerifyCodeCorrectReg")), {
        //         type: "error"
        //       });
        //     }
        //   })
        //   .catch(error => {
        //     loading.value = false;
        //   });
      } else {
        loading.value = false;
        message(transformI18n($t("login.pureTickPrivacy")), {
          type: "warning"
        });
      }
    } else {
      loading.value = false;
    }
  });
};

function onBack() {
  useVerifyCode().end();
  useUserStoreHook().SET_CURRENTPAGE(0);
}
</script>

<template>
  <el-form
    ref="ruleFormRef"
    :model="ruleForm"
    :rules="updateRules"
    :hide-required-asterisk="true"
    label-position="top"
    size="large"
  >
    <div class="flex gap-2">
      <Motion class="flex-3">
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
      <Motion class="flex-2">
        <el-form-item
          :label="t('login.pureNickname')"
          :rules="[
            {
              required: true,
              message: transformI18n($t('login.pureNicknameReg')),
              trigger: 'blur'
            }
          ]"
          prop="nickName"
        >
          <el-input
            v-model="ruleForm.nickName"
            clearable
            :placeholder="t('login.pureNickname')"
          />
        </el-form-item>
      </Motion>
    </div>

    <Motion :delay="50">
      <el-form-item :label="t('login.purePhone')" prop="phone">
        <el-input
          v-model="ruleForm.phone"
          clearable
          :placeholder="t('login.purePhone')"
          :prefix-icon="useRenderIcon(Iphone)"
        />
      </el-form-item>
    </Motion>

    <!--
    <Motion :delay="100">
      <el-form-item :label="t('login.pureSmsVerifyCode')" prop="verifyCode">
        <div class="w-full flex justify-between">
          <el-input
            v-model="ruleForm.verifyCode"
            clearable
            :placeholder="t('login.pureSmsVerifyCode')"
            :prefix-icon="useRenderIcon(Keyhole)"
          />
          <el-button :disabled="isDisabled" class="ml-2!" @click="getSms">
            {{
              text.length > 0
                ? text + t("login.pureInfo")
                : t("login.pureGetVerifyCode")
            }}
          </el-button>
        </div>
      </el-form-item>
    </Motion>
    -->

    <Motion :delay="150">
      <el-form-item :label="t('login.pureEmail')" prop="email">
        <el-input
          v-model="ruleForm.email"
          clearable
          :placeholder="t('login.pureEmail')"
          :prefix-icon="useRenderIcon(Email)"
        />
      </el-form-item>
    </Motion>

    <Motion :delay="200">
      <el-form-item :label="t('login.purePassword')" prop="password">
        <el-input
          v-model="ruleForm.password"
          clearable
          show-password
          :placeholder="t('login.purePassword')"
          :prefix-icon="useRenderIcon(Lock)"
        />
      </el-form-item>
    </Motion>

    <Motion :delay="250">
      <el-form-item
        :label="t('login.pureSure')"
        :rules="repeatPasswordRule"
        prop="repeatPassword"
      >
        <el-input
          v-model="ruleForm.repeatPassword"
          clearable
          show-password
          :placeholder="t('login.pureSure')"
          :prefix-icon="useRenderIcon(Lock)"
        />
      </el-form-item>
    </Motion>

    <Motion :delay="300">
      <el-form-item>
        <el-checkbox v-model="checked">
          {{ t("login.pureReadAccept") }}
        </el-checkbox>
        <el-button link type="primary">
          {{ t("login.purePrivacyPolicy") }}
        </el-button>
      </el-form-item>
    </Motion>

    <Motion :delay="350">
      <el-form-item>
        <el-button
          class="w-full"
          size="default"
          type="primary"
          :loading="loading"
          @click="onUpdate(ruleFormRef)"
        >
          {{ t("login.pureDefinite") }}
        </el-button>
      </el-form-item>
    </Motion>
  </el-form>
</template>
