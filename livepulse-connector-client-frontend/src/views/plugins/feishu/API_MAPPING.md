# 前后端 API 路径映射文档

本文档说明飞书插件前端和后端的 API 路径映射关系，以及如何进行真实 API 对接。

## 📋 目录

- [现有后端 Controller](#现有后端-controller)
- [前端 API 调用路径](#前端-api-调用路径)
- [路径映射关系](#路径映射关系)
- [对接真实 API 步骤](#对接真实-api-步骤)
- [Mock 模式配置](#mock-模式配置)

---

## 现有后端 Controller

### 1. FeishuAppController
**基础路径**: `/feishu/app`

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 保存或更新飞书应用 | POST | `/feishu/app/saveOrUpdate` | 已实现 ✅ |
| 查询应用详情 | GET | `/feishu/app/detail/{id}` | 已实现 ✅ |
| 删除飞书应用 | DELETE | `/feishu/app/delete/{id}` | 已实现 ✅ |
| 测试连接 | POST | `/feishu/app/test` | 已实现 ✅ |
| 刷新访问令牌 | POST | `/feishu/app/refreshToken/{id}` | 已实现 ✅ |
| 获取访问令牌 | GET | `/feishu/app/getToken/{id}` | 已实现 ✅ |
| 切换启用状态 | POST | `/feishu/app/toggleEnabled/{id}` | 已实现 ✅ |

### 2. FeishuBitableController
**基础路径**: `/feishu/bitable`

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 保存或更新多维表格 | POST | `/feishu/bitable/saveOrUpdate` | 已实现 ✅ |
| 查询多维表格详情 | GET | `/feishu/bitable/detail/{id}` | 已实现 ✅ |
| 删除多维表格 | DELETE | `/feishu/bitable/delete/{id}` | 已实现 ✅ |

### 3. FeishuBitableApiController
**基础路径**: `/feishu-muti/api/bitable`

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取数据表列表 | POST | `/feishu-muti/api/bitable/table/list` | 已实现 ✅ |
| 获取字段列表 | POST | `/feishu-muti/api/bitable/field/list` | 已实现 ✅ |
| 搜索记录 | POST | `/feishu-muti/api/bitable/record/search` | 已实现 ✅ |
| 创建记录 | POST | `/feishu-muti/api/bitable/record/create` | 已实现 ✅ |
| 更新记录 | POST | `/feishu-muti/api/bitable/record/update` | 已实现 ✅ |
| 删除记录 | POST | `/feishu-muti/api/bitable/record/delete` | 已实现 ✅ |
| 创建数据表 | POST | `/feishu-muti/api/bitable/table/create` | 已实现 ✅ |
| 批量创建数据表 | POST | `/feishu-muti/api/bitable/table/batchCreate` | 已实现 ✅ |
| 删除数据表 | POST | `/feishu-muti/api/bitable/table/delete` | 已实现 ✅ |
| 创建字段 | POST | `/feishu-muti/api/bitable/field/create` | 已实现 ✅ |
| 更新字段 | POST | `/feishu-muti/api/bitable/field/update` | 已实现 ✅ |
| 删除字段 | POST | `/feishu-muti/api/bitable/field/delete` | 已实现 ✅ |
| 获取租户访问令牌 | POST | `/feishu-muti/api/bitable/auth/tenantToken` | 已实现 ✅ |
| 获取应用访问令牌 | POST | `/feishu-muti/api/bitable/auth/appToken` | 已实现 ✅ |

### 4. FeishuRecordSyncController
**基础路径**: 未知，需要确认

### 5. FeishuTableController
**基础路径**: 未知，需要确认

---

## 前端 API 调用路径

### 连接配置相关
```typescript
// 保存连接配置
POST /feishu-muti/api/config/save

// 获取连接配置
GET /feishu-muti/api/config/get

// 测试连接
POST /feishu-muti/api/config/test
```

### 同步任务相关
```typescript
// 获取同步任务列表
GET /feishu-muti/api/sync/task/list

// 创建同步任务
POST /feishu-muti/api/sync/task/create

// 更新同步任务
PUT /feishu-muti/api/sync/task/update

// 删除同步任务
DELETE /feishu-muti/api/sync/task/delete

// 切换任务状态（暂停/启用）
PUT /feishu-muti/api/sync/task/toggle

// 触发立即同步
POST /feishu-muti/api/sync/task/trigger
```

### 同步日志相关
```typescript
// 获取同步日志列表
POST /feishu-muti/api/sync/log/list

// 获取日志详情
GET /feishu-muti/api/sync/log/detail/{logId}

// 重试同步
POST /feishu-muti/api/sync/log/retry/{logId}

// 获取同步统计
GET /feishu-muti/api/sync/stats
```

---

## 路径映射关系

### 已实现的后端接口可以直接使用

| 前端功能 | 后端接口 | 状态 |
|---------|---------|------|
| 测试连接 | `/feishu/app/test` | ✅ 已实现 |
| 获取令牌 | `/feishu/app/getToken/{id}` | ✅ 已实现 |
| 多维表格管理 | `/feishu/bitable/*` | ✅ 已实现 |
| 数据表操作 | `/feishu-muti/api/bitable/*` | ✅ 已实现 |

### 需要新增的后端接口

| 前端功能 | 需要的接口 | 建议 |
|---------|-----------|------|
| 保存连接配置 | `POST /feishu-muti/api/config/save` | 新增 FeishuConfigController |
| 获取连接配置 | `GET /feishu-muti/api/config/get` | 新增 FeishuConfigController |
| 测试连接配置 | `POST /feishu-muti/api/config/test` | 新增 FeishuConfigController |
| 获取同步任务列表 | `GET /feishu-muti/api/sync/task/list` | 新增 FeishuSyncTaskController |
| 创建同步任务 | `POST /feishu-muti/api/sync/task/create` | 新增 FeishuSyncTaskController |
| 更新同步任务 | `PUT /feishu-muti/api/sync/task/update` | 新增 FeishuSyncTaskController |
| 切换任务状态 | `PUT /feishu-muti/api/sync/task/toggle/{id}` | 新增 FeishuSyncTaskController |
| 触发立即同步 | `POST /feishu-muti/api/sync/task/trigger/{id}` | 新增 FeishuSyncTaskController |
| 获取同步日志列表 | `POST /feishu-muti/api/sync/log/list` | 新增 FeishuSyncLogController |
| 获取日志详情 | `GET /feishu-muti/api/sync/log/detail/{id}` | 新增 FeishuSyncLogController |
| 重试同步 | `POST /feishu-muti/api/sync/log/retry/{id}` | 新增 FeishuSyncLogController |
| 获取同步统计 | `GET /feishu-muti/api/sync/stats` | 新增 FeishuSyncLogController |

---

## 对接真实 API 步骤

### 步骤 1: 更新 API 路径

在 `src/views/plugins/feishu/api/index.ts` 中，将 Mock 调用替换为真实 API 调用：

```typescript
import { http } from "@/utils/http";

/**
 * 保存连接配置
 */
export async function saveConnectionConfig(config: ConnectionConfig): Promise<Result<boolean>> {
  return http.request<Result<boolean>>(
    'post',
    '/feishu-muti/api/config/save',
    { data: config }
  );
}

/**
 * 测试连接
 */
export async function testConnection(config: ConnectionConfig): Promise<Result<boolean>> {
  return http.request<Result<boolean>>(
    'post',
    '/feishu/app/test',
    { data: config }
  );
}
```

### 步骤 2: 禁用 Mock 模式

**方式 1**: 环境变量配置
在 `.env.development` 中设置：
```env
VITE_USE_MOCK=false
```

**方式 2**: 修改代码
在 `src/views/plugins/feishu/mock/service.ts` 中：
```typescript
const ENABLE_MOCK = false;
```

### 步骤 3: 实现后端接口

#### FeishuConfigController

```java
@RestController
@RequestMapping("/feishu-muti/api/config")
@Tag(name = "飞书连接配置管理")
public class FeishuConfigController {

    @Autowired
    private FeishuConfigService configService;

    @PostMapping("/save")
    @Operation(summary = "保存连接配置")
    public RespResult<Boolean> saveConfig(@RequestBody ConnectionConfigParam param) {
        return RespResult.success(configService.saveConfig(param));
    }

    @GetMapping("/get")
    @Operation(summary = "获取连接配置")
    public RespResult<ConnectionConfigVO> getConfig() {
        return RespResult.success(configService.getConfig());
    }

    @PostMapping("/test")
    @Operation(summary = "测试连接")
    public RespResult<Boolean> testConnection(@RequestBody ConnectionConfigParam param) {
        return RespResult.success(configService.testConnection(param));
    }
}
```

#### FeishuSyncTaskController

```java
@RestController
@RequestMapping("/feishu-muti/api/sync/task")
@Tag(name = "飞书同步任务管理")
public class FeishuSyncTaskController {

    @Autowired
    private FeishuSyncTaskService syncTaskService;

    @GetMapping("/list")
    @Operation(summary = "获取同步任务列表")
    public RespResult<List<FeishuSyncTaskVO>> list() {
        return RespResult.success(syncTaskService.list());
    }

    @PostMapping("/create")
    @Operation(summary = "创建同步任务")
    public RespResult<FeishuSyncTaskVO> create(@RequestBody CreateSyncTaskParam param) {
        return RespResult.success(syncTaskService.create(param));
    }

    @PutMapping("/update")
    @Operation(summary = "更新同步任务")
    public RespResult<Boolean> update(@RequestBody FeishuSyncTaskParam param) {
        return RespResult.success(syncTaskService.update(param));
    }

    @PutMapping("/toggle/{id}")
    @Operation(summary = "切换任务状态")
    public RespResult<Boolean> toggle(@PathVariable Long id) {
        return RespResult.success(syncTaskService.toggle(id));
    }

    @PostMapping("/trigger/{id}")
    @Operation(summary = "触发立即同步")
    public RespResult<Boolean> trigger(@PathVariable Long id) {
        return RespResult.success(syncTaskService.trigger(id));
    }
}
```

#### FeishuSyncLogController

```java
@RestController
@RequestMapping("/feishu-muti/api/sync/log")
@Tag(name = "飞书同步日志管理")
public class FeishuSyncLogController {

    @Autowired
    private FeishuSyncLogService syncLogService;

    @PostMapping("/list")
    @Operation(summary = "获取同步日志列表")
    public RespResult<PageResult<FeishuSyncLogVO>> list(@RequestBody LogQueryParam param) {
        return RespResult.success(syncLogService.list(param));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "获取日志详情")
    public RespResult<FeishuSyncLogVO> detail(@PathVariable Long id) {
        return RespResult.success(syncLogService.detail(id));
    }

    @PostMapping("/retry/{id}")
    @Operation(summary = "重试同步")
    public RespResult<Boolean> retry(@PathVariable Long id) {
        return RespResult.success(syncLogService.retry(id));
    }

    @GetMapping("/stats")
    @Operation(summary = "获取同步统计")
    public RespResult<SyncStatsVO> stats() {
        return RespResult.success(syncLogService.getStats());
    }
}
```

### 步骤 4: 数据转换

确保前后端数据格式一致。前端发送的请求格式和后端返回的响应格式要匹配。

**示例**:
```typescript
// 前端发送
{
  "taskName": "实时线索同步",
  "syncDirection": "bidirectional",
  "maxConnections": 10
}

// 后端接收
public class CreateSyncTaskParam {
    private String taskName;
    private String syncDirection; // bidirectional, pull, push
    private Integer maxConnections;
}
```

### 步骤 5: 测试

1. 启动后端服务
2. 启动前端服务（确保禁用 Mock）
3. 测试各个功能模块
4. 检查浏览器控制台和网络请求
5. 验证数据传输正确性

---

## Mock 模式配置

### 启用 Mock 模式（默认）

在开发环境中，Mock 模式默认启用，前端可以独立运行和测试。

**配置位置**: `src/views/plugins/feishu/mock/service.ts`

```typescript
const ENABLE_MOCK = true; // 启用 Mock
```

### Mock 数据

Mock 数据存放在 `src/views/plugins/feishu/mock/data.ts`，包括：

- `mockConnectionConfig`: 连接配置数据
- `mockSyncTasks`: 同步任务列表
- `mockSyncLogs`: 同步日志列表
- `mockSyncStats`: 同步统计数据
- `mockBitables`: 多维表格列表
- `mockDataTables`: 数据表列表
- `mockFields`: 字段列表

### Mock 服务

Mock 服务实现在 `src/views/plugins/feishu/mock/service.ts`，模拟了所有 API 调用的：

- 延迟响应（模拟网络请求）
- 数据转换
- 错误处理

---

## 📝 注意事项

1. **路径前缀**: 确保前后端路径前缀一致（`/feishu-muti`）
2. **请求方法**: GET、POST、PUT、DELETE 要对应
3. **请求/响应格式**: 统一使用 `Result<T>` 格式
4. **错误处理**: 前端需要处理各种错误情况
5. **超时设置**: 某些操作可能需要较长时间，注意超时配置
6. **跨域问题**: 开发环境配置代理，生产环境使用 Nginx

---

## 🔗 相关文件

- 前端 API 服务: `src/views/plugins/feishu/api/index.ts`
- Mock 服务: `src/views/plugins/feishu/mock/service.ts`
- Mock 数据: `src/views/plugins/feishu/mock/data.ts`
- 类型定义: `src/views/plugins/feishu/types/index.ts`
- 适配层: `src/views/plugins/feishu/utils/adapter.ts`

---

**更新时间**: 2026-05-24
**版本**: v1.0.0
