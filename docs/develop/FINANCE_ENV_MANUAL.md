# 财务域本地环境手动补齐清单

本文记录当前机器上仍需要手动补齐或确认的环境项。

## 已配置完成

- Docker Desktop 已安装，`docker` / `docker compose` 可用。
- MySQL、Redis、RabbitMQ 已启动。
- MySQL 使用宿主机端口 `3307`，容器内端口仍是 `3306`。
- 后端 `application-local.yaml`、`application-dev.yaml` 已改为连接 `127.0.0.1:3307`。
- Java 17 和 Maven 已安装，后端完整编译通过。

## 需要手动下载

### 0. Docker 镜像

当前本机已经有：

```text
mysql:8.0
redis:6-alpine
rabbitmq:3-management-alpine
tdengine/tdengine:3.3.6.0
```

本地开发初始化服务还缺：

```bash
docker pull eclipse-temurin:17-jdk
```

如果后续要用 `docker-compose/docker-compose.yml` 跑完整部署版，还需要：

```bash
docker pull nginx:stable-alpine
```

如果你下载到离线镜像包，导入方式：

```bash
docker load -i /path/to/eclipse-temurin-17-jdk.tar
docker load -i /path/to/nginx-stable-alpine.tar
```

导入后启动初始化服务：

```bash
cd /Users/hannn/xtu/实习/project/three/dev
docker compose up -d init-service
```

### 1. TDengine 镜像

TDengine 已经拉取成功。如果需要在其他机器重复配置，执行：

```bash
docker pull tdengine/tdengine:3.3.6.0
```

如果你下载到离线镜像包，例如 `tdengine-3.3.6.0.tar`，导入方式：

```bash
docker load -i /path/to/tdengine-3.3.6.0.tar
```

导入后启动：

```bash
cd /Users/hannn/xtu/实习/project/three/dev
docker compose up -d tdengine
```

### 2. Maven 依赖

后端编译已通过。不要优先执行 `mvn dependency:get` 预拉 Spring Boot 插件；该命令会先下载 Maven Dependency Plugin 及其依赖，当前网络可能返回 403。

推荐直接在项目里执行编译或启动命令，让 Maven 按项目配置解析依赖：

```bash
cd /Users/hannn/xtu/实习/project/three/Server
JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/opt/openjdk@17/bin:$PATH mvn -pl mitedtsm-server -am -DskipTests compile
```

如网络仍报 403，需要配置 Maven 镜像源后重试，例如阿里云公共仓库：

```bash
mvn -v
```

确认 Maven 可用后，在 `~/.m2/settings.xml` 配置镜像源，再重新执行上面的编译命令。

### 3. 前端依赖

当前 `Web/node_modules` 没有完整链接成功，`vue-tsc`、`vite` 入口没有生成。

建议使用 Node.js 20 LTS，再执行：

```bash
cd /Users/hannn/xtu/实习/project/three/Web
corepack enable
corepack prepare pnpm@10.11.0 --activate
COREPACK_ENABLE_AUTO_PIN=0 pnpm install --frozen-lockfile
```

之前缺失过的包：

```text
https://registry.npmjs.org/@iconify/json/-/json-2.2.277.tgz
```

如果网络慢，可以先手动下载或配置 npm 镜像源，然后重新执行 `pnpm install --frozen-lockfile`。

可选 npm 镜像配置：

```bash
pnpm config set registry https://registry.npmmirror.com
pnpm install --frozen-lockfile
```

前端依赖安装完成后验证：

```bash
cd /Users/hannn/xtu/实习/project/three/Web
pnpm ts:check
pnpm dev
```

## 常用启动命令

启动基础服务：

```bash
cd /Users/hannn/xtu/实习/project/three/dev
docker compose up -d mysql redis rabbitmq
```

查看服务状态：

```bash
cd /Users/hannn/xtu/实习/project/three/dev
docker compose ps
```

启动后端：

```bash
cd /Users/hannn/xtu/实习/project/three/Server
JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/opt/openjdk@17/bin:$PATH mvn -pl mitedtsm-server -am -DskipTests org.springframework.boot:spring-boot-maven-plugin:3.5.9:run
```

启动前端：

```bash
cd /Users/hannn/xtu/实习/project/three/Web
pnpm dev
```

## 财务域 BPM 流程

后端启动后会自动检查并发布两个默认流程：

- `erp-reimbursement-audit`
- `erp-refund-audit`

这两个流程用于报销、退款单据审批。默认流程是最小可运行的一步审批流，后续可以在 BPM 后台替换为正式审批流。
