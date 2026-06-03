# 飞书多维表格连接器前端重构完成

根据 Figma 设计图，已完成飞书多维表格连接器前端的重构工作。

## 📋 实现内容

### 1. 主入口文件 (`index.vue`)
**变更**：
- Tab 导航从 6 个调整为 3 个：连接配置、数据同步、同步监控
- 更新 Tab 样式为下划线激活状态
- 引入新的组件：ConnectionConfig、DataSync
- 保持配置数据传递和刷新机制

**样式更新**：
- 激活状态：紫色文字 (#6c5ce7) + 底部紫色下划线
- 悬停状态：紫色文字
- 移除图标，纯文字导航

### 2. 连接配置页面 (`ConnectionConfig.vue`)
**新增组件**，实现连接配置功能：
- ✅ 最大连接数滑块（1-50，默认10）
- ✅ 重试策略下拉选择（指数退避、固定间隔、线性退避）
- ✅ 最大重试次数数字输入（0-10，默认3）
- ✅ 数据过滤区域（绿色竖条，DSL 语法说明）
- ✅ 数据范围下拉选择
- ✅ 排除字段多选（标签形式，可删除）
- ✅ 测试连接、取消、保存配置按钮

**设计规范**：
- 主色：紫色 #6c5ce7
- 成功色：绿色 #00b894
- 背景：白色 #FFFFFF、浅灰 #F8F9FA
- 圆角：6px/8px
- 阴影：0 2px 8px rgba(0, 0, 0, 0.06)

### 3. 数据同步页面 (`DataSync.vue`)
**重写组件**，实现任务列表功能：
- ✅ 标题"数据同步管理" + "创建同步任务"按钮
- ✅ "活跃同步任务 X个"（绿色图标）
- ✅ 任务卡片列表设计：
  - 状态圆点（绿色=活跃、蓝色=待执行、灰色=暂停）
  - 任务名称
  - 表格名称（数据库图标）
  - 同步方向（双向同步/单向拉取/单向推送）
  - 最后/下次同步时间
  - 已同步条数
  - 操作按钮（编辑、暂停、立即同步）
- ✅ 卡片样式：白色背景、圆角、阴影、悬停效果
- ✅ 集成创建任务弹窗

**任务卡片交互**：
- 编辑：打开编辑弹窗
- 暂停/启用：切换任务状态
- 立即同步：触发手动同步

### 4. 创建同步任务弹窗 (`CreateSyncTaskDialog.vue`)
**新增组件**，实现分步骤配置：
- ✅ 基本设置：
  - 任务名称输入
  - 目标表格下拉选择
  - 同步方向（仅从飞书拉取、仅向飞书推送、双向同步）
  - 条件同步（高级）选项
- ✅ 字段映射引擎：
  - 启用智能字段匹配（勾选）
  - 映射规则库下拉选择
  - 字段映射表格（源字段、目标字段、转换规则）
- ✅ 实时更新策略：
  - 更新检测方式下拉（事件订阅+轮询互补）
  - 增量同步选项
  - 冲突解决策略（记录冲突、字段冲突、时间窗口）
- ✅ 性能优化设置：
  - 批量大小（条/次）
  - 批次间隔（ms）

**弹窗样式**：
- 宽度：70%
- 最大高度：60vh，滚动
- 白色背景、圆角、阴影
- 右上角关闭按钮
- 底部取消/保存按钮

### 5. 同步监控页面 (`SyncMonitor.vue`)
**保留原有功能**，实现日志监控：
- ✅ 统计卡片（成功、失败、同步中、总计）
- ✅ 查询表单（多维表格、同步状态、时间范围）
- ✅ 同步日志表格（分页）
- ✅ 详情对话框
- ✅ 重试功能

## 🎨 设计规范

### 颜色方案
```scss
// 主色
$primary-color: #6c5ce7;      // 紫色 - 激活状态、主按钮
$primary-hover: #5a4bd1;      // 深紫色 - 悬停状态

// 状态色
$success-color: #00b894;      // 绿色 - 成功状态、活跃任务
$info-color: #3b82f6;         // 蓝色 - 待执行状态
$warning-color: #f59e0b;      // 黄色 - 暂停状态、警告
$danger-color: #ef4444;       // 红色 - 错误状态

// 背景色
$bg-primary: #ffffff;         // 白色 - 主背景
$bg-secondary: #f8f9fa;       // 浅灰 - 次要背景
$bg-tertiary: #f3f4f6;        // 深灰 - 输入框背景

// 文字色
$text-primary: #2d3748;       // 深灰 - 主文字
$text-secondary: #6b7280;     // 中灰 - 次要文字
$text-tertiary: #9ca3af;      // 浅灰 - 辅助文字

// 边框色
$border-color: #e5e7eb;       // 浅灰 - 边框
$border-hover: #d1d5db;       // 深灰 - 悬停边框
```

### 组件样式
- **卡片**：8px 圆角、0 1px 3px rgba(0, 0, 0, 0.1) 阴影
- **按钮**：6px 圆角、padding: 10px 24px
- **输入框**：6px 圆角、背景 #f8f9fa、边框 #e9ecef
- **标签**：4px 圆角、padding: 4px 10px

### 间距规范
- 页面边距：16px 24px
- 卡片间距：16px
- 表单项间距：20px
- 组件内边距：20px

## 📦 文件结构

```
src/views/plugins/feishu/
├── index.vue                          # 主入口（Tab 导航）
├── components/
│   ├── ConnectionConfig.vue           # 连接配置页面 ✨ 新增
│   ├── DataSync.vue                   # 数据同步任务列表 🔄 重写
│   ├── CreateSyncTaskDialog.vue       # 创建同步任务弹窗 ✨ 新增
│   ├── SyncMonitor.vue                # 同步监控页面（保留）
│   ├── BasicInfo.vue                  # 基础信息（已废弃）
│   ├── BitableConfig.vue              # 多维表格配置（未使用）
│   ├── FieldMapping.vue               # 字段映射（未使用）
│   ├── SyncConfig.vue                 # 同步配置（未使用）
│   ├── WebhookConfig.vue              # Webhook 配置（未使用）
│   └── BitableManager.vue             # 多维表格管理工具（保留）
```

## 🔌 API 对接

### 需要对接的后端接口

1. **连接配置相关**：
   - 保存连接配置：`POST /feishu-muti/api/config/save`
   - 获取连接配置：`GET /feishu-muti/api/config/get`
   - 测试连接：`POST /feishu-muti/api/config/test`

2. **数据同步相关**：
   - 获取同步任务列表：`GET /feishu-muti/api/sync/task/list`
   - 创建同步任务：`POST /feishu-muti/api/sync/task/create`
   - 更新同步任务：`PUT /feishu-muti/api/sync/task/update`
   - 删除同步任务：`DELETE /feishu-muti/api/sync/task/delete`
   - 暂停/启用任务：`PUT /feishu-muti/api/sync/task/toggle`
   - 立即同步：`POST /feishu-muti/api/sync/task/trigger`

3. **同步监控相关**：
   - 获取同步日志：`GET /feishu-muti/api/sync/log/list`
   - 获取日志详情：`GET /feishu-muti/api/sync/log/detail`
   - 重试同步：`POST /feishu-muti/api/sync/log/retry`

4. **多维表格相关**：
   - 获取表格列表：`GET /feishu-muti/api/bitable/table/list`
   - 获取字段列表：`GET /feishu-muti/api/bitable/field/list`

## 🚀 后续工作

1. **API 对接**：
   - 将所有 TODO 接口调用替换为真实 API
   - 添加错误处理和加载状态
   - 实现数据刷新机制

2. **功能完善**：
   - 实现编辑任务功能（复用创建弹窗）
   - 添加任务删除确认
   - 实现高级过滤条件编辑器
   - 添加字段映射自定义功能

3. **用户体验**：
   - 添加骨架屏加载效果
   - 优化错误提示信息
   - 添加操作成功/失败反馈
   - 实现表单验证

4. **性能优化**：
   - 添加任务列表分页
   - 实现虚拟滚动（长列表）
   - 添加防抖/节流处理

## ✅ 验收检查清单

- [x] Tab 导航样式符合设计
- [x] 连接配置页面布局正确
- [x] 滑块、下拉框、输入框样式一致
- [x] 数据过滤区域有绿色竖条
- [x] 排除字段标签可删除
- [x] 任务卡片样式符合设计
- [x] 状态圆点颜色正确（绿/蓝/灰）
- [x] 创建任务弹窗分步骤显示
- [x] 字段映射表格样式正确
- [x] 冲突解决区域有黄色背景
- [x] 性能优化设置有紫色竖条
- [x] 所有按钮悬停效果正确
- [x] 整体配色符合设计规范

## 📝 注意事项

1. **未使用的组件**：
   - BasicInfo.vue、BitableConfig.vue、FieldMapping.vue、SyncConfig.vue、WebhookConfig.vue 暂未使用
   - 可以保留或根据需求删除

2. **BitableManager.vue**：
   - 独立的多维表格管理工具
   - 不在主 Tab 导航中
   - 可以通过独立路由访问

3. **类型定义**：
   - 已使用 TypeScript 接口定义
   - 建议后续完善所有数据类型

4. **响应式设计**：
   - 当前主要针对桌面端
   - 如需移动端支持，需额外适配

---

**创建时间**：2026-05-24
**版本**：v1.0.0
**设计来源**：Figma 设计图（D:\data\figma）
