#!/bin/bash
# ===========================================
# MITEDTSM 项目启动脚本
# 按依赖顺序启动所有服务
# 用法: bash start.sh
# ===========================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 项目目录（脚本所在目录）
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

echo -e "${CYAN}"
echo "╔══════════════════════════════════════════════════╗"
echo "║        MITEDTSM 项目启动脚本                      ║"
echo "╚══════════════════════════════════════════════════╝"
echo -e "${NC}"
echo ""

# ===========================================
# Phase 1: 启动基础设施服务
# ===========================================
echo -e "${BLUE}[Phase 1/4]${NC} 启动基础设施服务 (MySQL / Redis / RabbitMQ / TDengine)..."
echo "----------------------------------------"

docker compose up -d mysql redis rabbitmq tdengine

echo ""
echo -e "${YELLOW}等待基础设施服务健康检查...${NC}"

# 等待 MySQL 健康
echo -n "  ⏳ MySQL..."
until docker compose ps mysql 2>/dev/null | grep -q "healthy"; do
    sleep 2
done
echo -e " ${GREEN}✓ Ready${NC}"

# 等待 Redis 健康
echo -n "  ⏳ Redis..."
until docker compose ps redis 2>/dev/null | grep -q "healthy"; do
    sleep 2
done
echo -e " ${GREEN}✓ Ready${NC}"

# 等待 RabbitMQ 健康
echo -n "  ⏳ RabbitMQ..."
until docker compose ps rabbitmq 2>/dev/null | grep -q "healthy"; do
    sleep 2
done
echo -e " ${GREEN}✓ Ready${NC}"

# 等待 TDengine 健康（TDengine 启动较慢，多给一些时间）
echo -n "  ⏳ TDengine..."
until docker compose ps tdengine 2>/dev/null | grep -q "healthy"; do
    sleep 2
done
echo -e " ${GREEN}✓ Ready${NC}"

echo -e "${GREEN}基础设施层全部就绪 ✓${NC}"
echo ""

# ===========================================
# Phase 2: 启动初始化服务
# ===========================================
echo -e "${BLUE}[Phase 2/4]${NC} 执行数据库初始化服务 (InitService)..."
echo "----------------------------------------"

docker compose up -d init-service

echo -n "  ⏳ 等待初始化完成..."

# 等待 init-service 容器退出（状态码 0 表示成功）
# docker compose up -d 会后台运行，我们用 wait 等待容器结束
INIT_CONTAINER="mitedtsm-init-service"
until docker ps -a --filter "name=${INIT_CONTAINER}" --format "{{.Status}}" 2>/dev/null | grep -q "Exited"; do
    sleep 2
done

# 检查退出码
EXIT_CODE=$(docker inspect ${INIT_CONTAINER} --format '{{.State.ExitCode}}' 2>/dev/null)
if [ "$EXIT_CODE" = "0" ]; then
    echo -e " ${GREEN}成功 ✓${NC}"
else
    echo -e " ${RED}失败 (退出码: ${EXIT_CODE})${NC}"
    echo ""
    echo -e "${RED}初始化失败，查看日志:${NC}"
    docker logs ${INIT_CONTAINER} --tail 50
    exit 1
fi
echo ""

# ===========================================
# Phase 3: 启动后端服务
# ===========================================
echo -e "${BLUE}[Phase 3/4]${NC} 启动后端服务 (Server)..."
echo "----------------------------------------"

docker compose up -d server

echo -n "  ⏳ 等待后端服务健康检查..."
# Server 有 start_period: 120s，最多等待 3 分钟
MAX_WAIT=180
WAITED=0
until docker compose ps server 2>/dev/null | grep -q "healthy"; do
    sleep 5
    WAITED=$((WAITED + 5))
    if [ $WAITED -ge $MAX_WAIT ]; then
        echo -e " ${RED}超时${NC}"
        echo ""
        echo -e "${YELLOW}后端服务启动超时，查看最近日志:${NC}"
        docker logs mitedtsm-server --tail 30
        echo ""
        echo -e "${YELLOW}继续启动前端服务，但后端可能未就绪。${NC}"
        break
    fi
    # 每30秒输出一个点表示仍在等待
    if [ $((WAITED % 30)) -eq 0 ]; then
        echo -n " (${WAITED}s)"
    fi
done

if docker compose ps server 2>/dev/null | grep -q "healthy"; then
    echo -e " ${GREEN}Ready ✓${NC}"
else
    echo -e " ${YELLOW}健康检查未通过，请手动确认${NC}"
fi
echo ""

# ===========================================
# Phase 4: 启动前端服务
# ===========================================
echo -e "${BLUE}[Phase 4/4]${NC} 启动前端服务 (Web 管理后台 / Mall 商城)..."
echo "----------------------------------------"

docker compose up -d web mall

echo -n "  ⏳ Web 管理后台..."
until docker compose ps web 2>/dev/null | grep -q "Up"; do
    sleep 1
done
echo -e " ${GREEN}Running ✓${NC}"

echo -n "  ⏳ Mall 商城..."
until docker compose ps mall 2>/dev/null | grep -q "Up"; do
    sleep 1
done
echo -e " ${GREEN}Running ✓${NC}"
echo ""

# ===========================================
# 最终状态汇总
# ===========================================
echo -e "${CYAN}"
echo "╔══════════════════════════════════════════════════╗"
echo "║             项目启动状态汇总                       ║"
echo "╚══════════════════════════════════════════════════╝"
echo -e "${NC}"

echo ""
docker compose ps --format "table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}" 2>/dev/null
echo ""

# 健康检查汇总
echo -e "${BLUE}--- 服务健康检查 ---${NC}"

check_service() {
    local name=$1
    local container=$2
    local port=$3
    local check_cmd=$4

    echo -n "  ${name}: "
    if docker ps --format "{{.Names}}" | grep -q "^${container}$"; then
        if eval "$check_cmd" 2>/dev/null; then
            echo -e "${GREEN}✓ 正常${NC}"
            return 0
        else
            echo -e "${YELLOW}⚠ 运行中但健康检查未通过${NC}"
            return 1
        fi
    else
        echo -e "${RED}✗ 未运行${NC}"
        return 1
    fi
}

ALL_OK=true

check_service "MySQL    " "mitedtsm-mysql" "3306" \
    "docker exec mitedtsm-mysql mysqladmin ping -h localhost -u root --password=1234 --silent" || ALL_OK=false

check_service "Redis    " "mitedtsm-redis" "6379" \
    "docker exec mitedtsm-redis redis-cli ping | grep -q PONG" || ALL_OK=false

check_service "RabbitMQ " "mitedtsm-rabbitmq" "5672" \
    "docker exec mitedtsm-rabbitmq rabbitmqctl status --quiet" || ALL_OK=false

check_service "TDengine " "mitedtsm-tdengine" "6041" \
    "docker exec mitedtsm-tdengine taos -s 'SHOW DATABASES;' | grep -q mitedtsm" || ALL_OK=false

check_service "Server   " "mitedtsm-server" "8080" \
    "curl -sf -o /dev/null http://localhost:8080/actuator/health" || ALL_OK=false

check_service "Web      " "mitedtsm-web" "80" \
    "curl -sf -o /dev/null http://localhost:80" || ALL_OK=false

check_service "Mall     " "mitedtsm-mall" "81" \
    "curl -sf -o /dev/null http://localhost:81" || ALL_OK=false

echo ""

if $ALL_OK; then
    echo -e "${GREEN}"
    echo "╔══════════════════════════════════════════════════╗"
    echo "║  ✓  项目已正常启动，所有服务运行正常！              ║"
    echo "╚══════════════════════════════════════════════════╝"
    echo -e "${NC}"
    echo ""
    echo -e "  管理后台:  ${CYAN}http://localhost:80${NC}"
    echo -e "  商城前端:  ${CYAN}http://localhost:81${NC}"
    echo -e "  后端 API:  ${CYAN}http://localhost:8080${NC}"
    echo -e "  RabbitMQ:  ${CYAN}http://localhost:15672${NC}  (rabbit/rabbit)"
    echo ""
else
    echo -e "${YELLOW}"
    echo "╔══════════════════════════════════════════════════╗"
    echo "║  ⚠  部分服务可能未就绪，请检查上面输出            ║"
    echo "╚══════════════════════════════════════════════════╝"
    echo -e "${NC}"
fi
