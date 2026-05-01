<template>
  <div class="md-wrapper" :class="{ dark, border: mode !== 'preview' }">
    <!-- 编辑 / 分屏 -->
    <div v-if="mode !== 'preview'" ref="editorRef" />

    <!-- 纯预览 -->
    <div v-else ref="viewerRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from "vue";
import Editor from "@toast-ui/editor";
import Viewer from "@toast-ui/editor/dist/toastui-editor-viewer";

import "@toast-ui/editor/dist/toastui-editor.css";
import "@toast-ui/editor/dist/toastui-editor-viewer.css";
// language
import("@toast-ui/editor/dist/i18n/zh-cn");

import debounce from "lodash/debounce";

// ================= 类型定义 =================
type Mode = "edit" | "split" | "preview";

interface Props {
  modelValue: string;
  mode?: Mode;
  height?: string;
  placeholder?: string;
  dark?: boolean;
  toolbar?: string[][];
  uploadImage?: (file: Blob) => Promise<string>;
}

// ================= props =================
const props = withDefaults(defineProps<Props>(), {
  modelValue: "",
  mode: "edit",
  height: "400px",
  placeholder: "Please enter text.",
  dark: false,
  toolbar: () => [
    ["heading", "bold", "italic", "strike"],
    ["hr", "quote"],
    ["ul", "ol", "task"],
    ["table", "image", "link"],
    ["code", "codeblock"]
  ]
});

// ================= emits =================
const emit = defineEmits<{
  (e: "update:modelValue", value: string): void;
}>();

// ================= refs =================
const editorRef = ref<HTMLDivElement | null>(null);
const viewerRef = ref<HTMLDivElement | null>(null);

let editor: Editor | null = null;
let viewer: Viewer | null = null;

// ================= 防抖 =================
const emitChange = debounce(() => {
  if (editor) {
    emit("update:modelValue", editor.getMarkdown());
  }
}, 300);

// ================= 图片上传 =================
const handleImageUpload = async (
  blob: Blob,
  callback: (url: string, text?: string) => void
) => {
  try {
    if (props.uploadImage) {
      const url = await props.uploadImage(blob);
      callback(url, "image");
    } else {
      // fallback base64
      const reader = new FileReader();
      reader.onload = () => {
        callback(reader.result as string);
      };
      reader.readAsDataURL(blob);
    }
  } catch (err) {
    console.error("图片上传失败:", err);
  }
};

// ================= 初始化 Editor =================
const initEditor = () => {
  if (!editorRef.value) return;

  editor = new Editor({
    el: editorRef.value,
    language: "zh-CN",
    height: props.height,
    initialEditType: "markdown",
    previewStyle: props.mode === "split" ? "vertical" : "tab",
    initialValue: props.modelValue,
    toolbarItems: props.toolbar,
    placeholder: props.placeholder,
    hooks: {
      addImageBlobHook: handleImageUpload
    }
  });

  editor.on("change", emitChange);
};

// ================= 初始化 Viewer =================
const initViewer = () => {
  if (!viewerRef.value) return;

  viewer = new Viewer({
    el: viewerRef.value,
    initialValue: props.modelValue
  });
};

// ================= 销毁 =================
const destroy = () => {
  editor?.destroy();
  viewer?.destroy();
  editor = null;
  viewer = null;
};

// ================= 模式切换 =================
const switchMode = () => {
  destroy();

  if (props.mode === "preview") {
    initViewer();
  } else {
    initEditor();
  }
};

// ================= watch =================
watch(() => props.mode, switchMode);

watch(
  () => props.modelValue,
  val => {
    if (editor && val !== editor.getMarkdown()) {
      editor.setMarkdown(val);
    }

    if (viewer) {
      viewer.setMarkdown(val);
    }
  }
);

// ================= 生命周期 =================
onMounted(() => {
  switchMode();
});

onBeforeUnmount(() => {
  destroy();
});

// ================= 暴露 =================
defineExpose({
  getMarkdown: (): string | undefined => editor?.getMarkdown(),
  setMarkdown: (val: string) => editor?.setMarkdown(val),
  insertText: (text: string) => editor?.insertText(text)
});
</script>

<style scoped>
.border {
  border: 1px solid #dcdfe6;
  border-radius: 6px;
}
.md-wrapper {
  overflow: hidden;
}

.md-wrapper.dark {
  background: #1e1e1e;
}

.md-wrapper :deep(.toastui-editor-defaultUI) {
  border: none;
}
</style>
