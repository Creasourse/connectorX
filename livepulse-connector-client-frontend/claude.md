---
paths: "**/*.vue,**/*.js,**/*.ts" # 仅对Vue、JS、TS文件生效
---

# 后台管理项目 claude.md 配置（项目级）

## 一、项目基础信息（What）

1.  技术栈：前端 Vue3 + Vite + Element Plus + TypeScript，后端 Node.js + Express + MySQL
2.  项目结构：
    - /src/views：页面组件（按模块划分，如 /src/views/system 对应系统管理）
    - /src/components：公共组件（全局组件放 /src/components/global）
    - /src/api：接口请求（按模块拆分，如 /src/api/user.js 对应用户接口）
    - /src/store：Pinia 状态管理（按模块划分，禁止单一store过大）
    - /src/utils：工具函数（通用工具放此处，如请求拦截、权限判断）
3.  接口基准地址：开发环境 http://117.50.204.138:24001/api

## 二、项目约束（Why）

1.  兼容性：适配 Chrome 90+、Edge 90+ 浏览器，无需兼容IE
2.  性能要求：页面首次加载时间 < 3s，接口响应时间 < 1s
3.  安全要求：禁止硬编码敏感信息（如密钥、token），接口请求必须携带token验证

## 三、编码与开发规范（How）

### 1. 前端编码规范

- Vue组件：单文件组件（SFC），命名用 PascalCase（如 UserList.vue），模板内缩进2空格
- TypeScript：严格模式（strict: true），变量、函数必须指定类型，禁止 any 类型（特殊场景需标注原因）
- 样式：使用 scoped 样式，全局样式放 /src/style/index.css，禁止内联样式
- 接口请求：统一使用 /src/utils/request.js 封装的请求方法，禁止直接使用 axios
- 权限控制：页面级权限通过路由守卫判断，按钮级权限使用 v-hasPerm 自定义指令

### 2. 后端编码规范

- 接口命名：RESTful 风格，如 GET /api/user（获取用户列表）、POST /api/user（新增用户）
- 响应格式：统一返回 { code: number, message: string, data: any }

### 3. 开发与构建流程

- 本地开发：pnpm run dev 启动前端，node server.js 启动后端，默认端口前端3001、后端3000
- 代码检查：提交前运行 npm run lint，修复所有ESLint错误，禁止带错提交
- 构建部署：pnpm run build 生成前端静态文件
- 提交规范：commit 信息格式为「类型: 描述」，如 feat: 新增用户列表页面、fix: 修复登录失效问题

## 四、特殊规则

1.  页面路由：路由路径与页面组件名对应，如 /system/user 对应 /src/views/system/UserList.vue
2.  日志打印：仅在开发环境打印日志，生产环境禁止 console.log，使用 logger 工具替代
3.  第三方依赖：新增依赖需团队确认，禁止引入无意义依赖，优先使用 Element Plus 内置组件，避免重复开发
