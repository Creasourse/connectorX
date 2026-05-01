# Docker Compose 管理 API 测试文档

## 环境准备

### 1. 验证 Docker 环境

```bash
# 验证 Docker
docker --version

# 验证 Docker Compose（新版）
docker compose version

# 或旧版
docker-compose --version

# 测试 Docker 是否正常运行
docker ps
```

### 2. 启动应用

```bash
cd livepulse-connector-tools
mvn spring-boot:run
```

应用将在 `http://localhost:8088` 启动。

### 3. 准备测试环境

创建一个测试目录和 docker-compose.yml 文件：

```bash
# Linux/Mac
mkdir -p /tmp/test-compose

# Windows
mkdir C:\test-compose
```

创建 `docker-compose.yml`：

```yaml
version: '3.8'

services:
  nginx:
    image: nginx:alpine
    ports:
      - "8081:80"
    container_name: test-nginx

  redis:
    image: redis:alpine
    ports:
      - "6379:6379"
    container_name: test-redis
```

## API 测试用例

### 1. 健康检查

```bash
curl -X GET http://localhost:8088/api/docker-compose/health
```

**预期响应**:
```json
{
  "code": 200,
  "message": "Docker Compose 管理服务运行正常",
  "data": "OK",
  "timestamp": 1714214400000
}
```

---

### 2. 启动容器

```bash
# Linux/Mac
curl -X POST http://localhost:8088/api/docker-compose/start \
  -H "Content-Type: application/json" \
  -d '{
    "composeDir": "/tmp/test-compose",
    "composeFile": "docker-compose.yml",
    "projectName": "testapp"
  }'

# Windows
curl -X POST http://localhost:8088/api/docker-compose/start \
  -H "Content-Type: application/json" \
  -d "{\"composeDir\": \"C:\\\\test-compose\", \"projectName\": \"testapp\"}"
```

**预期响应**:
```json
{
  "code": 200,
  "message": "容器启动成功",
  "data": {
    "message": "容器启动成功",
    "containers": [
      {
        "name": "testapp-nginx-1",
        "image": "nginx:alpine",
        "status": "Up 2 seconds",
        "state": "RUNNING",
        "ports": "0.0.0.0:8081->80/tcp"
      },
      {
        "name": "testapp-redis-1",
        "image": "redis:alpine",
        "status": "Up 2 seconds",
        "state": "RUNNING",
        "ports": "0.0.0.0:6379->6379/tcp"
      }
    ],
    "count": 2
  }
}
```

---

### 3. 查看容器状态

```bash
# Linux/Mac
curl "http://localhost:8088/api/docker-compose/status?composeDir=/tmp/test-compose"

# Windows
curl "http://localhost:8088/api/docker-compose/status?composeDir=C:\\test-compose"
```

**预期响应**:
```json
{
  "code": 200,
  "message": "获取状态成功",
  "data": [
    {
      "name": "testapp-nginx-1",
      "image": "nginx:alpine",
      "status": "Up 30 seconds",
      "state": "RUNNING",
      "ports": "0.0.0.0:8081->80/tcp"
    },
    {
      "name": "testapp-redis-1",
      "image": "redis:alpine",
      "status": "Up 30 seconds",
      "state": "RUNNING",
      "ports": "0.0.0.0:6379->6379/tcp"
    }
  ]
}
```

---

### 4. 查看容器日志

```bash
# Linux/Mac
curl "http://localhost:8088/api/docker-compose/logs?composeDir=/tmp/test-compose&tail=20"

# Windows
curl "http://localhost:8088/api/docker-compose/logs?composeDir=C:\\test-compose&tail=20"
```

**预期响应**:
```json
{
  "code": 200,
  "message": "获取日志成功",
  "data": [
    {
      "containerName": "nginx",
      "logs": "/docker-entrypoint.sh: /docker-entrypoint.d/ is not empty, will attempt to perform configuration\n...",
      "truncated": false
    },
    {
      "containerName": "redis",
      "logs": "1:C 27 Apr 2024 10:00:00.000 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo\n...",
      "truncated": false
    }
  ]
}
```

---

### 5. 暂停容器

```bash
# Linux/Mac
curl -X POST http://localhost:8088/api/docker-compose/pause \
  -H "Content-Type: application/json" \
  -d '{
    "composeDir": "/tmp/test-compose"
  }'

# Windows
curl -X POST http://localhost:8088/api/docker-compose/pause \
  -H "Content-Type: application/json" \
  -d "{\"composeDir\": \"C:\\\\test-compose\"}"
```

**预期响应**:
```json
{
  "code": 200,
  "message": "容器暂停成功",
  "data": {
    "message": "容器暂停成功",
    "containers": [
      {
        "name": "testapp-nginx-1",
        "status": "Up 1 minute (Paused)",
        "state": "PAUSED"
      }
    ],
    "count": 2
  }
}
```

---

### 6. 恢复容器

```bash
# Linux/Mac
curl -X POST http://localhost:8088/api/docker-compose/unpause \
  -H "Content-Type: application/json" \
  -d '{
    "composeDir": "/tmp/test-compose"
  }'

# Windows
curl -X POST http://localhost:8088/api/docker-compose/unpause \
  -H "Content-Type: application/json" \
  -d "{\"composeDir\": \"C:\\\\test-compose\"}"
```

---

### 7. 停止容器

```bash
# Linux/Mac
curl -X POST http://localhost:8088/api/docker-compose/stop \
  -H "Content-Type: application/json" \
  -d '{
    "composeDir": "/tmp/test-compose"
  }'

# Windows
curl -X POST http://localhost:8088/api/docker-compose/stop \
  -H "Content-Type: application/json" \
  -d "{\"composeDir\": \"C:\\\\test-compose\"}"
```

**预期响应**:
```json
{
  "code": 200,
  "message": "容器停止成功",
  "data": {
    "message": "容器停止成功",
    "containers": [
      {
        "name": "testapp-nginx-1",
        "status": "Exited (0) 1 second ago",
        "state": "STOPPED"
      }
    ],
    "count": 2
  }
}
```

---

### 8. 删除容器

```bash
# Linux/Mac
curl -X POST http://localhost:8088/api/docker-compose/remove \
  -H "Content-Type: application/json" \
  -d '{
    "composeDir": "/tmp/test-compose"
  }'

# Windows
curl -X POST http://localhost:8088/api/docker-compose/remove \
  -H "Content-Type: application/json" \
  -d "{\"composeDir\": \"C:\\\\test-compose\"}"
```

**预期响应**:
```json
{
  "code": 200,
  "message": "容器删除成功",
  "data": {
    "message": "容器删除成功",
    "containers": [],
    "count": 0
  }
}
```

---

## 操作指定服务

### 仅启动 nginx 服务

```bash
# Linux/Mac
curl -X POST http://localhost:8088/api/docker-compose/start \
  -H "Content-Type: application/json" \
  -d '{
    "composeDir": "/tmp/test-compose",
    "serviceName": "nginx"
  }'

# Windows
curl -X POST http://localhost:8088/api/docker-compose/start \
  -H "Content-Type: application/json" \
  -d "{\"composeDir\": \"C:\\\\test-compose\", \"serviceName\": \"nginx\"}"
```

### 仅查看 nginx 日志

```bash
curl "http://localhost:8088/api/docker-compose/logs?composeDir=/tmp/test-compose&serviceName=nginx&tail=50"
```

---

## Postman 测试集合

导入以下 JSON 到 Postman：

```json
{
  "info": {
    "name": "Docker Compose 管理 API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    {"key": "baseUrl", "value": "http://localhost:8088"},
    {"key": "composeDir", "value": "/tmp/test-compose"}
  ],
  "item": [
    {
      "name": "健康检查",
      "request": {
        "method": "GET",
        "url": "{{baseUrl}}/api/docker-compose/health"
      }
    },
    {
      "name": "启动容器",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"composeDir\": \"{{composeDir}}\",\n  \"projectName\": \"testapp\"\n}"
        },
        "url": "{{baseUrl}}/api/docker-compose/start"
      }
    },
    {
      "name": "查看状态",
      "request": {
        "method": "GET",
        "url": {
          "raw": "{{baseUrl}}/api/docker-compose/status?composeDir={{composeDir}}",
          "query": [{"key": "composeDir", "value": "{{composeDir}}"}]
        }
      }
    },
    {
      "name": "查看日志",
      "request": {
        "method": "GET",
        "url": {
          "raw": "{{baseUrl}}/api/docker-compose/logs?composeDir={{composeDir}}&tail=50",
          "query": [
            {"key": "composeDir", "value": "{{composeDir}}"},
            {"key": "tail", "value": "50"}
          ]
        }
      }
    },
    {
      "name": "暂停容器",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"composeDir\": \"{{composeDir}}\"\n}"
        },
        "url": "{{baseUrl}}/api/docker-compose/pause"
      }
    },
    {
      "name": "恢复容器",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"composeDir\": \"{{composeDir}}\"\n}"
        },
        "url": "{{baseUrl}}/api/docker-compose/unpause"
      }
    },
    {
      "name": "停止容器",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"composeDir\": \"{{composeDir}}\"\n}"
        },
        "url": "{{baseUrl}}/api/docker-compose/stop"
      }
    },
    {
      "name": "删除容器",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"composeDir\": \"{{composeDir}}\"\n}"
        },
        "url": "{{baseUrl}}/api/docker-compose/remove"
      }
    }
  ]
}
```

---

## 错误测试

### 1. docker-compose 文件不存在

```bash
curl -X POST http://localhost:8088/api/docker-compose/start \
  -H "Content-Type: application/json" \
  -d '{
    "composeDir": "/nonexistent/path"
  }'
```

**预期响应**:
```json
{
  "code": 500,
  "message": "启动容器失败: Docker Compose 文件不存在: /nonexistent/path/docker-compose.yml",
  "data": null
}
```

### 2. Docker 守护进程未启动

```bash
# 先停止 Docker
# 然后执行命令
curl -X POST http://localhost:8088/api/docker-compose/start \
  -H "Content-Type: application/json" \
  -d '{
    "composeDir": "/tmp/test-compose"
  }'
```

**预期响应**:
```json
{
  "code": 500,
  "message": "启动容器失败: Cannot connect to the Docker daemon",
  "data": null
}
```

### 3. 缺少必填参数

```bash
curl -X POST http://localhost:8088/api/docker-compose/start \
  -H "Content-Type: application/json" \
  -d '{}'
```

**预期响应**:
```json
{
  "timestamp": "2024-04-27T10:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "compose 文件目录不能为空"
}
```

---

## 命令行验证

在执行 API 调用后，可以使用 docker 命令验证结果：

```bash
# 查看运行中的容器
docker ps

# 查看所有容器（包括停止的）
docker ps -a

# 查看容器日志
docker logs test-nginx

# 查看容器详细信息
docker inspect test-nginx

# 进入容器
docker exec -it test-nginx sh
```

---

## 清理测试环境

```bash
# 停止并删除测试容器
docker stop test-nginx test-redis
docker rm test-nginx test-redis

# 删除测试目录
# Linux/Mac
rm -rf /tmp/test-compose

# Windows
rmdir /s C:\test-compose
```

---

## 常见问题

### 1. 命令执行权限问题

**错误**: `permission denied while trying to connect to the Docker daemon`

**解决**:
```bash
# Linux: 将用户添加到 docker 组
sudo usermod -aG docker $USER

# 重新登录或执行
newgrp docker

# Windows: 以管理员身份运行应用
```

### 2. Docker Compose 版本问题

**检查版本**:
```bash
# 新版（Docker Desktop 自带）
docker compose version

# 旧版（独立安装）
docker-compose --version
```

**注意**: 本工具使用 `docker compose` 命令（V2）。如果系统只有 V1，需要修改代码中的命令。

### 3. 端口被占用

**错误**: `bind: address already in use`

**解决**:
```bash
# 查看占用端口的进程
# Linux/Mac
lsof -i :8081

# Windows
netstat -ano | findstr :8081

# 停止占用端口的进程或修改 docker-compose.yml 中的端口
```

### 4. 镜像拉取失败

**错误**: `error pulling image configuration`

**解决**:
```bash
# 手动拉取镜像
docker pull nginx:alpine

# 或配置镜像加速器
# 编辑 /etc/docker/daemon.json (Linux)
# 或 Docker Desktop 设置 (Windows/Mac)
```

---

## 性能测试

### 并发测试

```bash
# 使用 Apache Bench
ab -n 100 -c 10 -T application/json \
  -p request.json \
  http://localhost:8088/api/docker-compose/status?composeDir=/tmp/test-compose
```

### 大量日志查询

```bash
time curl "http://localhost:8088/api/docker-compose/logs?composeDir=/tmp/test-compose&tail=10000"
```

---

## 调试技巧

### 查看应用日志

```bash
# 应用运行时，日志会输出到控制台
# 包含详细的命令执行信息

# 日志示例：
# 开始启动 Docker Compose 容器，目录: /tmp/test-compose
# 执行命令: [docker, compose, -f, docker-compose.yml, up, -d]，工作目录: /tmp/test-compose
# 命令执行完成，退出码: 0
```

### 手动执行 docker-compose 命令

```bash
cd /tmp/test-compose
docker compose -f docker-compose.yml ps
docker compose -f docker-compose.yml logs
```

这样可以对比 API 执行结果和手动执行结果，帮助排查问题。
