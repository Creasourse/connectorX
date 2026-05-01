# LivePulse Client Connector 部署操作手册

## 目录

1. [环境说明](#环境说明)
2. [系统用户配置](#系统用户配置)
3. [依赖中间件安装](#依赖中间件安装)
4. [Docker 及 Docker Compose 安装](#docker-及-docker-compose-安装)
5. [应用部署](#应用部署)
6. [Nginx 配置](#nginx-配置)
7. [服务管理](#服务管理)
8. [故障排查](#故障排查)

---

## 环境说明

- **操作系统**：Ubuntu 22.04 LTS
- **操作用户**：root
- **项目名称**：LivePulse Client Connector

---

## 系统用户配置

创建运行用户并设置密码：

```bash
# 创建用户 cs_conn
adduser cs_conn
# 密码设置为：123456 (请根据提示输入)
```

---

## 依赖中间件安装

### 1. Nginx 安装与配置

```bash
# 安装 Nginx
apt install nginx -y

# 将 www-data 加入 cs_conn 用户组（用于处理文件权限）
sudo usermod -a -G cs_conn www-data
```

### 2. PostgreSQL 15 安装与配置

#### 2.1 添加官方仓库并安装

```bash
# 创建仓库配置文件
sudo sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'

# 导入签名密钥
wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -

# 更新软件包列表
sudo apt update

# 安装 PostgreSQL 15 服务端和客户端
sudo apt install postgresql-15 postgresql-client-15 -y
```

#### 2.2 服务管理

```bash
# 检查服务状态
sudo systemctl status postgresql@15-main

# 设置开机自启
sudo systemctl enable postgresql@15-main

# 启动 / 停止服务
sudo systemctl start postgresql@15-main
sudo systemctl stop postgresql@15-main
```

#### 2.3 数据库初始化

```bash
# 切换至 postgres 用户并进入 psql 终端
sudo -u postgres psql
```

在 psql 终端中执行以下 SQL：

```sql
-- 创建用户 cs_conn 并设置密码
CREATE USER cs_conn WITH PASSWORD 'abc123';

-- 创建数据库并指定所有者
CREATE DATABASE cs_client_conn OWNER cs_conn;

-- 授权
GRANT ALL PRIVILEGES ON DATABASE cs_client_conn TO cs_conn;
```



#### 2.4 配置远程访问

编辑 `pg_hba.conf` 允许远程连接：

```bash
vi /etc/postgresql/15/main/pg_hba.conf
```

在文件末尾追加（或修改）以下行：

```
host    all             all             0.0.0.0/0               md5
```

编辑 `postgresql.conf` 监听所有地址：

```bash
vi /etc/postgresql/15/main/postgresql.conf
```

修改配置：

```
listen_addresses = '*'
```

配置完成，请重启 PostgreSQL 服务。
#### 2.5 执行初始化数据库脚本 [cs_client_connetor.sql](cs_client_connetor.sql)


### 3. Nacos 安装与配置

#### 3.1 下载与启动

从官网下载 `nacos-server-2.5.1.tar.gz`，解压并以单机模式启动：

```bash
# 解压
tar -zxvf nacos-server-2.5.1.tar.gz

# 进入 bin 目录
cd nacos/bin

# 单机模式启动
./startup.sh -m standalone
```

#### 3.2 开启鉴权

编辑 `conf/application.properties`，在末尾追加以下配置以开启认证：

```properties
nacos.core.auth.system.type=nacos
nacos.core.auth.enabled=true
nacos.core.auth.console.enabled=true
nacos.core.auth.caching.enabled=true
nacos.core.auth.enable.userAgentAuthWhite=false
nacos.core.auth.server.identity.key=ZGhzc2tueGhqZHhtanVlbmpkZGpkamRqZGo=
nacos.core.auth.server.identity.value=ZGhzc2tueGhqZHhtanVlbmpkZGpkamRqZGo=
nacos.core.auth.plugin.nacos.token.secret.key=ZGhzc2tueGhqZHhtanVlbmpkZGpkamRqZGpkZGRkZGQ=
```

#### 3.3 修改管理员密码

请替换 IP 和端口后执行：

```bash
curl -X POST 'http://192.168.1.10:8848/nacos/v1/auth/users/admin' -d 'password=123456'
```

#### 3.4 执行nacos初始化配置文件 


### 4. Redis 安装与配置

#### 4.1 安装

```bash
apt install redis-server -y
```

#### 4.2 配置密码

编辑配置文件 `/etc/redis/redis.conf`，在末尾追加密码设置：

```
requirepass abc123
```

#### 4.3 服务管理

```bash
# 启动 Redis
systemctl start redis

# 停止 Redis
systemctl stop redis

# 设置开机自启
systemctl enable redis-server
```

---

## Docker 及 Docker Compose 安装

### 1. 安装 Docker Engine

#### 1.1 卸载旧版本

```bash
sudo apt remove docker docker-engine docker.io containerd runc -y
```

#### 1.2 安装依赖工具

```bash
sudo apt update
sudo apt install apt-transport-https ca-certificates curl software-properties-common -y
```

#### 1.3 添加 Docker 官方 GPG 密钥

```bash
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
```

#### 1.4 添加 Docker 软件源

```bash
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```

#### 1.5 安装 Docker

```bash
sudo apt update
sudo apt install docker-ce docker-ce-cli containerd.io -y
```

### 2. 安装 Docker Compose (插件版)

#### 2.1 创建目录

```bash
mkdir -p ~/.docker/cli-plugins
```

#### 2.2 下载二进制文件

```bash
curl -SL https://github.com/docker/compose/releases/download/v2.39.3/docker-compose-linux-x86_64 -o ~/.docker/cli-plugins/docker-compose
```

#### 2.3 赋予执行权限

```bash
chmod +x ~/.docker/cli-plugins/docker-compose
```

#### 2.4 验证安装

```bash
docker compose version
```

### 3. 可选配置（推荐）

#### 3.1 免 sudo 执行 Docker

```bash
sudo usermod -aG docker $USER
```

注意：执行此命令后需**注销并重新登录**（或重启服务器），配置才会生效。

#### 3.2 验证运行状态

```bash
sudo systemctl status docker
```

---

## 应用部署

### 1. 准备配置文件

创建 `.env` 文件（与 docker-compose.yml 同目录）：

```bash
# Java 应用参数
JAVA_OPTS=-Xms512m -Xmx1024m

# Spring 配置
SPRING_PROFILES_ACTIVE=prod

# Nacos 配置
SPRING_CLOUD_NACOS_SERVER_ADDR=192.168.1.10:8848
SPRING_CLOUD_NACOS_CONFIG_GROUP=DEFAULT_GROUP
SPRING_CLOUD_NACOS_DISCOVERY_GROUP=DEFAULT_GROUP
SPRING_CLOUD_NACOS_CONFIG_NAMESPACE=
SPRING_CLOUD_NACOS_DISCOVERY_NAMESPACE=
SPRING_CLOUD_NACOS_USERNAME=nacos
SPRING_CLOUD_NACOS_PASSWORD=nacos

# 端口配置
APP_PORT_CLIENT_MANAGER=24001
APP_PORT_WECOM_OPEN=23003

# 镜像标签
TAG=latest
```

### 2. Docker Compose 配置说明

`docker-compose.yml` 包含两个服务：

| 服务名 | 容器名 | 端口 | 说明 |
|--------|--------|------|------|
| client-manager | client-manager-connetor | 24001 | 客户端连接器管理服务 |
| wecom-open | wecom-open-connetor | 23003 | 企业微信开放平台连接器服务 |

### 3. 目录结构

```
livepulse-connector-client-manager/
├── docker-compose.yml              # Docker Compose 配置文件
├── .env                            # 环境变量配置
├── client_conn_nginx.conf          # Nginx 配置文件
├── client-manager/                 # 客户端管理服务
│   ├── Dockerfile
│   └── ...
├── wecom-open/                     # 企业微信服务
│   ├── Dockerfile
│   └── ...
└── volumes/
    └── app/
        └── log/                    # 应用日志挂载目录
```

### 4. 启动服务

```bash
# 构建并启动所有服务
docker compose up -d

# 查看服务状态
docker compose ps

# 查看日志
docker compose logs -f

# 停止服务
docker compose down

# 重启服务
docker compose restart
```

### 5. 验证部署

```bash
# 检查容器运行状态
docker ps

# 检查服务日志
docker compose logs client-manager
docker compose logs wecom-open

# 访问健康检查接口
curl http://localhost:24001/actuator/health
curl http://localhost:23003/actuator/health
```

---

## Nginx 配置

### 1. 配置文件说明

配置文件：`client_conn_nginx.conf`

**域名**：`client-connectorx.livepulse.com.cn`

**端口**：80（HTTP） / 443（HTTPS，已注释）

**关键配置**：

| 配置项 | 值 | 说明 |
|--------|-----|------|
| client_max_body_size | 300m | 最大请求主体大小 |
| proxy_connect_timeout | 600s | 连接超时 |
| proxy_read_timeout | 600s | 读取超时 |
| proxy_send_timeout | 600s | 发送超时 |

### 2. 路由配置

#### 2.1 前端页面

```
location / {
    root /home/cs_conn/client-connectorx/frontend;
    index index.html index.htm;
    try_files $uri $uri/ /index.html;
}
```

#### 2.2 Client Manager API

```
location /api {
    rewrite ^/api/(.*) /$1 break;
    proxy_pass http://localhost:24001;
    ...
}
```

请求示例：
- `http://client-connectorx.livepulse.com.cn/api/xxx` → `http://localhost:24001/xxx`

#### 2.3 WeCom Open API

```
location /wecom-open {
    rewrite ^/wecom-open/(.*) /$1 break;
    proxy_pass http://localhost:23003;
    ...
}
```

请求示例：
- `http://client-connectorx.livepulse.com.cn/wecom-open/xxx` → `http://localhost:23003/xxx`

#### 2.4 图片代理

```
location /images/ {
    proxy_pass http://localhost/images/;
    ...
}
```

### 3. 部署 Nginx 配置

```bash
# 复制配置文件到 Nginx 配置目录
cp client_conn_nginx.conf /etc/nginx/sites-available/client-connectorx

# 创建软链接启用站点
ln -s /etc/nginx/sites-available/client-connectorx /etc/nginx/sites-enabled/

# 测试配置
nginx -t

# 重载 Nginx
nginx -s reload
```

### 4. 配置 HTTPS（可选）

取消配置文件中的 SSL 注释并更新证书路径：

```nginx
listen 443 ssl;
ssl_certificate  /home/cs_conn/nginx_conf/ssl/client-connectorx.livepulse.com.cn_bundle.pem;
ssl_certificate_key /home/cs_conn/nginx_conf/ssl/client-connectorx.livepulse.com.cn.key;
ssl_session_cache    shared:SSL:1m;
ssl_session_timeout  5m;
```

---

## 服务管理

### Docker 容器管理

```bash
# 查看所有容器
docker ps -a

# 查看容器日志
docker logs -f client-manager-connetor
docker logs -f wecom-open-connetor

# 进入容器
docker exec -it client-manager-connetor bash

# 重启单个服务
docker compose restart client-manager
docker compose restart wecom-open

# 删除并重新创建服务
docker compose up -d --force-recreate
```

### 应用日志

日志挂载路径：`./volumes/app/log`

```bash
# 查看实时日志
tail -f ./volumes/app/log/*.log

# 搜索错误日志
grep -i error ./volumes/app/log/*.log
```

### Nginx 日志

- 访问日志：`/home/cs_conn/logs/client-connectorx-access.log`
- 错误日志：`/home/cs_conn/logs/client-connectorx-error.log`

```bash
# 查看实时访问日志
tail -f /home/cs_conn/logs/client-connectorx-access.log

# 查看实时错误日志
tail -f /home/cs_conn/logs/client-connectorx-error.log
```

---

## 故障排查

### 1. 容器无法启动

```bash
# 查看容器日志
docker compose logs [service-name]

# 检查镜像构建
docker compose build --no-cache

# 检查端口占用
netstat -tulpn | grep [port]
```

### 2. 服务无法访问

```bash
# 检查容器运行状态
docker ps

# 检查服务端口
docker compose ps

# 检查网络连接
docker network ls
docker network inspect livepulse-connector-client-manager_app-network
```

### 3. 数据库连接失败

```bash
# 检查 PostgreSQL 状态
systemctl status postgresql@15-main

# 测试数据库连接
psql -h localhost -U cs_conn -d cs_client_conn

# 检查防火墙规则
iptables -L -n | grep 5432
```

### 4. Nacos 连接失败

```bash
# 检查 Nacos 状态
curl http://localhost:8848/nacos/v1/console/health/readiness

# 查看服务注册情况
curl http://localhost:8848/nacos/v1/ns/instance/list?serviceName=[service-name]
```

### 5. Redis 连接失败

```bash
# 检查 Redis 状态
systemctl status redis

# 测试连接
redis-cli -a abc123 ping
```

---

## 附录

### A. 常用端口清单

| 服务 | 端口 | 说明 |
|------|------|------|
| Nginx | 80/443 | Web 服务器 |
| PostgreSQL | 5432 | 数据库 |
| Nacos | 8848 | 配置中心/注册中心 |
| Redis | 6379 | 缓存 |
| Client Manager | 24001 | 客户端管理服务 |
| WeCom Open | 23003 | 企业微信服务 |

### B. 默认账号密码

| 服务 | 用户名 | 密码 |
|------|--------|------|
| PostgreSQL | cs_conn | abc123 |
| Nacos | nacos | nacos |
| Redis | - | abc123 |
| 系统用户 | cs_conn | 123456 |

### C. 相关文档

- [Nacos 官方文档](https://nacos.io/zh-cn/docs/what-is-nacos.html)
- [Docker 官方文档](https://docs.docker.com/)
- [PostgreSQL 官方文档](https://www.postgresql.org/docs/)

---

**文档版本**：v1.0

**最后更新**：2026-05-01