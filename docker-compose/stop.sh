#!/bin/bash
# ===========================================
# MITEDTSM 项目关闭脚本
# 按逆依赖顺序安全关闭所有服务
# 用法: bash stop.sh
# ===========================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

# 项目目录（脚本所在目录）
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

echo -e "${CYAN}"
echo "╔══════════════════════════════════════════════════╗"
echo "║        MITEDTSM 项目关闭脚本                      ║"
echo "╚══════════════════════════════════════════════════╝"
echo -e "${NC}"
echo ""

# 先检查是否有正在运行的服务
RUNNING=$(docker ps --filter "name=mitedtsm" --format "{{.Names}}" 2>/dev/null)
if [ -z "$RUNNING" ]; then
    echo -e "${YELLOW}没有运行中的 mitedtsm 服务，无需关闭。${NC}"
    echo ""

    # 清理残留的已停止容器和网络
    STOPPED=$(docker ps -a --filter "name=mitedtsm" --format "{{.Names}}" 2>/dev/null)
    if [ -n "$STOPPED" ]; then
        echo -e "${BLUE}清理残留容器...${NC}"
        docker compose down 2>/dev/null || true
        echo -e "${GREEN}残留容器已清理 ✓${NC}"
    fi

    echo -e "${GREEN}项目已处于关闭状态。${NC}"
    exit 0
fi

echo -e "${BLUE}检测到以下运行中的服务:${NC}"
docker ps --filter "name=mitedtsm" --format "  - {{.Names}} ({{.Status}})"
echo ""

# ===========================================
# Phase 1: 关闭前端服务
# ===========================================
echo -e "${BLUE}[Phase 1/4]${NC} 关闭前端服务 (Web / Mall)..."
echo "----------------------------------------"

docker compose stop web mall 2>/dev/null || true
docker compose rm -f web mall 2>/dev/null || true

echo -e "  Web 管理后台    ${GREEN}已停止 ✓${NC}"
echo -e "  Mall 商城       ${GREEN}已停止 ✓${NC}"
echo ""

# ===========================================
# Phase 2: 关闭后端服务
# ===========================================
echo -e "${BLUE}[Phase 2/4]${NC} 关闭后端服务 (Server)..."
echo "----------------------------------------"

docker compose stop server 2>/dev/null || true
docker compose rm -f server 2>/dev/null || true

echo -e "  Server          ${GREEN}已停止 ✓${NC}"
echo ""

# ===========================================
# Phase 3: 清理初始化服务容器
# ===========================================
echo -e "${BLUE}[Phase 3/4]${NC} 清理初始化服务容器 (InitService)..."
echo "----------------------------------------"

docker compose rm -f init-service 2>/dev/null || true

echo -e "  InitService     ${GREEN}已清理 ✓${NC}"
echo ""

# ===========================================
# Phase 4: 关闭基础设施服务
# ===========================================
echo -e "${BLUE}[Phase 4/4]${NC} 关闭基础设施服务 (MySQL / Redis / RabbitMQ / TDengine)..."
echo "----------------------------------------"

# 按依赖顺序停止：先停上层 TDengine，再停消息队列，再停缓存，最后停数据库
docker compose stop tdengine 2>/dev/null || true
echo -e "  TDengine        ${GREEN}已停止 ✓${NC}"

docker compose stop rabbitmq 2>/dev/null || true
echo -e "  RabbitMQ        ${GREEN}已停止 ✓${NC}"

docker compose stop redis 2>/dev/null || true
echo -e "  Redis           ${GREEN}已停止 ✓${NC}"

docker compose stop mysql 2>/dev/null || true
echo -e "  MySQL           ${GREEN}已停止 ✓${NC}"
echo ""

# 移除所有容器
echo -e "${BLUE}清理容器...${NC}"
docker compose rm -f 2>/dev/null || true
echo -e "${GREEN}所有容器已清理 ✓${NC}"
echo ""

# 移除网络
echo -e "${BLUE}移除网络...${NC}"
docker network rm mitedtsm-system_mitedtsm-network 2>/dev/null || \
docker network rm mitedtsm-network 2>/dev/null || true
echo -e "${GREEN}网络已移除 ✓${NC}"
echo ""

# ===========================================
# 验证关闭结果
# ===========================================
echo -e "${CYAN}"
echo "╔══════════════════════════════════════════════════╗"
echo "║             项目关闭状态汇总                       ║"
echo "╚══════════════════════════════════════════════════╝"
echo -e "${NC}"
echo ""

# 确认没有残留
REMAINING=$(docker ps --filter "name=mitedtsm" --format "{{.Names}}" 2>/dev/null)
if [ -z "$REMAINING" ]; then
    echo -e "${GREEN}"
    echo "╔══════════════════════════════════════════════════╗"
    echo "║  ✓  项目已安全关闭，所有服务已停止！              ║"
    echo "╚══════════════════════════════════════════════════╝"
    echo -e "${NC}"
    echo ""
    echo -e "  注意: 数据卷 (mysql_data, redis_data, rabbitmq_data, tdengine_data)"
    echo -e "        已保留，重新启动数据不会丢失。"
    echo ""
else
    echo -e "${YELLOW}  ⚠ 以下容器仍在运行:${NC}"
    echo "$REMAINING"
fi
