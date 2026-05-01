Docker 及 Docker Compose 安装手册

适用系统：Ubuntu 22.04 LTS

执行用户：root

1. 安装 Docker Engine

1.1 卸载旧版本

为了确保安装纯净版，首先移除系统中可能存在的旧版本或冲突包。
sudo apt remove docker docker-engine docker.io containerd runc -y


1.2 安装依赖工具

安装通过 HTTPS 获取仓库所需的依赖。
sudo apt update
sudo apt install apt-transport-https ca-certificates curl software-properties-common -y


1.3 添加 Docker 官方 GPG 密钥

导入密钥以确保下载的软件包来源可信。
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg


1.4 添加 Docker 软件源

将 Docker 的稳定版仓库添加到 APT 源列表中。
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null


1.5 安装 Docker

更新包索引并安装 Docker Engine。
sudo apt update
sudo apt install docker-ce docker-ce-cli containerd.io -y


2. 安装 Docker Compose (插件版)

2.1 创建目录

为 CLI 插件创建存放目录。
mkdir -p ~/.docker/cli-plugins


2.2 下载二进制文件

下载适用于 Linux x86_64 架构的 Docker Compose v2.39.3。
curl -SL https://github.com/docker/compose/releases/download/v2.39.3/docker-compose-linux-x86_64 -o ~/.docker/cli-plugins/docker-compose


2.3 赋予执行权限

修改文件权限，使其可执行。
chmod +x ~/.docker/cli-plugins/docker-compose


2.4 验证安装

检查 Docker Compose 版本，确认安装成功。
docker compose version


3. 可选配置（推荐）

3.1 免 sudo 执行 Docker

将当前用户加入 docker 用户组，避免每次执行都需要 sudo。
sudo usermod -aG docker $USER

注意：执行此命令后需 注销并重新登录（或重启服务器），配置才会生效。

3.2 验证运行状态

sudo systemctl status docker
