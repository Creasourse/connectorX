# LivePulse Connector Tools - Docker Compose 管理工具

## 功能说明

这是一个基于 Spring Boot 的 Docker Compose 容器管理工具，通过命令行调用 docker-compose 命令管理容器。

### 主要功能

- ✅ **启动容器** - 启动 Docker Compose 定义的所有容器
- ✅ **暂停容器** - 暂停运行中的容器
- ✅ **恢复容器** - 恢复已暂停的容器
- ✅ **停止容器** - 停止运行中的容器
- ✅ **删除容器** - 停止并删除容器
- ✅ **查看状态** - 查看容器运行状态
- ✅ **查看日志** - 查看容器日志

### 技术实现

- 通过 `ProcessBuilder` 调用 `docker compose` 命令
- 不依赖 Docker Java API
- 简单、轻量、易维护

## 环境要求

### 必需软件

1. **Java 17+**
2. **Maven 3.6+**
3. **Docker** - 必须安装并启动 Docker 服务
4. **Docker Compose** - Docker Desktop 自带，或单独安装

### 验证环境

```bash
# 验证 Docker
docker --version

# 验证 Docker Compose（新版）
docker compose version

# 或旧版
docker-compose --version
```

## 快速开始

### 1. 启动应用

```bash
cd livepulse-connector-tools
mvn spring-boot:run
```

或编译后运行：

```bash
mvn clean package
java -jar target/livepulse-connector-tools-1.0.0.jar
```

### 2. 健康检查

```bash
curl http://localhost:8088/api/docker-compose/health
```

响应：
```json
{
  "success": true,
  "msg": "Docker Compose 管理服务运行正常",
  "data": "OK"
}
```

## API 接口

### 统一响应格式

所有接口返回统一的 `RespResult` 格式：

```json
{
  "success": true,
  "msg": "操作成功",
  "data": { ... },
  "code": "",
  "errorMsg": "",
  "errorDetails": []
}
```

### 1. 启动容器

**接口**: `POST /api/docker-compose/start`

**请求参数**:
```json
{
  "composeDir": "/path/to/docker-compose",
  "composeFile": "docker-compose.yml",
  "projectName": "myproject",
  "serviceName": "nginx"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| composeDir | String | 是 | docker-compose 文件所在目录 |
| composeFile | String | 否 | docker-compose 文件名（默认: docker-compose.yml） |
| projectName | String | 否 | 项目名称 |
| serviceName | String | 否 | 指定启动的服务名称 |

**响应**:
```json
{
  "success": true,
  "msg": "容器启动成功",
  "data": {
    "message": "容器启动成功",
    "containers": [
      {
        "name": "myproject-nginx-1",
        "image": "nginx:latest",
        "status": "Up 2 seconds",
        "state": "RUNNING",
        "ports": "0.0.0.0:8080->80/tcp"
      }
    ],
    "count": 1
  }
}
```

### 2. 暂停容器

**接口**: `POST /api/docker-compose/pause`

**请求参数**: 同启动容器

**响应**:
```json
{
  "success": true,
  "msg": "容器暂停成功",
  "data": {
    "message": "容器暂停成功",
    "containers": [...],
    "count": 1
  }
}
```

### 3. 恢复容器

**接口**: `POST /api/docker-compose/unpause`

**请求参数**: 同启动容器

### 4. 停止容器

**接口**: `POST /api/docker-compose/stop`

**请求参数**: 同启动容器

### 5. 删除容器

**接口**: `POST /api/docker-compose/remove`

**请求参数**: 同启动容器

**注意**：
- 如果指定 `serviceName`，则只停止并删除指定服务
- 如果不指定，则执行 `docker compose down` 删除所有容器和网络

### 6. 查看容器状态

**接口**: `GET /api/docker-compose/status`

**请求参数**:
- `composeDir`: docker-compose 目录（必填）
- `projectName`: 项目名称（可选）
- `composeFile`: docker-compose 文件名（默认: docker-compose.yml）

**示例**:
```bash
curl "http://localhost:8088/api/docker-compose/status?composeDir=/opt/compose"
```

**响应**:
```json
{
  "success": true,
  "msg": "获取状态成功",
  "data": [
    {
      "name": "myproject-nginx-1",
      "image": "nginx:latest",
      "status": "Up 5 minutes",
      "state": "RUNNING",
      "ports": "0.0.0.0:8080->80/tcp"
    }
  ]
}
```

### 7. 查看容器日志

**接口**: `GET /api/docker-compose/logs`

**请求参数**:
- `composeDir`: docker-compose 目录（必填）
- `projectName`: 项目名称（可选）
- `composeFile`: docker-compose 文件名（默认: docker-compose.yml）
- `tail`: 日志行数（默认: 100）

**示例**:
```bash
curl "http://localhost:8088/api/docker-compose/logs?composeDir=/opt/compose&tail=50"
```

### 8. 获取服务名称

**接口**: `GET /api/docker-compose/serviceName`

**请求参数**:
- `connectorName`: 插件名称（必填）

**示例**:
```bash
curl "http://localhost:8088/api/docker-compose/serviceName?connectorName=mysql"
```

**响应**:
```json
{
  "success": true,
  "msg": "服务名称",
  "data": "mysql"
}
```

## 使用示例

### 准备 docker-compose.yml

```yaml
version: '3.8'

services:
  nginx:
    image: nginx:alpine
    ports:
      - "8080:80"
    container_name: my-nginx

  redis:
    image: redis:alpine
    ports:
      - "6379:6379"
    container_name: my-redis
```

### cURL 示例

#### 1. 启动所有容器
```bash
curl -X POST http://localhost:8088/api/docker-compose/start \
  -H "Content-Type: application/json" \
  -d '{
    "composeDir": "/opt/docker-compose"
  }'
```

#### 2. 启动指定服务
```bash
curl -X POST http://localhost:8088/api/docker-compose/start \
  -H "Content-Type: application/json" \
  -d '{
    "composeDir": "/opt/docker-compose",
    "serviceName": "nginx"
  }'
```

#### 3. 查看状态
```bash
curl "http://localhost:8088/api/docker-compose/status?composeDir=/opt/docker-compose"
```

#### 4. 查看日志
```bash
curl "http://localhost:8088/api/docker-compose/logs?composeDir=/opt/docker-compose&tail=50"
```

#### 5. 暂停容器
```bash
curl -X POST http://localhost:8088/api/docker-compose/pause \
  -H "Content-Type: application/json" \
  -d '{
    "composeDir": "/opt/docker-compose"
  }'
```

#### 6. 删除容器
```bash
curl -X POST http://localhost:8088/api/docker-compose/remove \
  -H "Content-Type: application/json" \
  -d '{
    "composeDir": "/opt/docker-compose"
  }'
```

## 容器状态说明

| 状态 | 说明 |
|-----|------|
| `RUNNING` | 容器正在运行 |
| `PAUSED` | 容器已暂停 |
| `STOPPED` | 容器已停止 |
| `UNKNOWN` | 状态未知或容器不存在 |

## 错误处理

所有接口返回统一的错误格式：

```json
{
  "success": false,
  "msg": "启动容器失败: xxx",
  "data": null,
  "errorMsg": "错误简短信息",
  "errorDetails": []
}
```

### 常见错误

#### 1. Docker 未启动
```
启动容器失败: Cannot connect to the Docker daemon
```
**解决**: 确保 Docker 服务正在运行

#### 2. docker-compose 文件不存在
```
启动容器失败: Docker Compose 文件不存在: /path/to/docker-compose.yml
```
**解决**: 检查 `composeDir` 和 `composeFile` 路径是否正确

#### 3. 权限不足
```
启动容器失败: permission denied
```
**解决**:
- Linux: 将用户添加到 docker 组 `sudo usermod -aG docker $USER`
- Windows: 以管理员身份运行

#### 4. 端口冲突
```
Error: bind: address already in use
```
**解决**: 检查端口是否被占用，修改 docker-compose.yml 中的端口映射

## 技术栈

- Spring Boot 3.x
- Lombok
- Hutool
- Apache Commons IO
- MyBatis Plus

## 注意事项

1. **Docker 版本**：确保使用 Docker Compose V2（`docker compose`）或 V1（`docker-compose`）
2. **文件路径**：`composeDir` 必须是绝对路径或可访问的相对路径
3. **环境变量**：如需设置环境变量，在 docker-compose.yml 中配置
4. **并发控制**：同一项目的容器不要同时执行多个操作
5. **权限**：确保运行应用的用户有执行 docker 命令的权限

## 架构说明

### 实现原理

```
Controller → Service → ProcessBuilder → docker compose 命令
```

1. **Controller** - 接收 HTTP 请求，返回 `RespResult` 统一格式
2. **Service** - 构建命令并执行
3. **ProcessBuilder** - 调用系统命令
4. **docker compose** - 执行实际操作

### 命令映射

| API 接口 | Docker Compose 命令 |
|---------|-------------------|
| 启动 | `docker compose -f <file> up -d` |
| 暂停 | `docker compose -f <file> pause` |
| 恢复 | `docker compose -f <file> unpause` |
| 停止 | `docker compose -f <file> stop` |
| 删除 | `docker compose -f <file> down` |
| 状态 | `docker compose -f <file> ps` |
| 日志 | `docker compose -f <file> logs --tail=N` |

## 版本历史

- **1.0.0** (2024-04-27)
  - 初始版本
  - 支持容器启动、暂停、恢复、停止、删除
  - 支持查看容器状态和日志
  - 使用命令行调用 docker compose
  - 使用 `RespResult` 统一响应格式

## 许可证

Copyright © 2024 LivePulse
