#!/bin/bash

PROJECT_DIR="/home/feng/product"

echo "========================================"
echo "        一键停止 mitedtsm 项目"
echo "========================================"
echo ""

echo "[1/2] 停止前端服务..."
FRONTEND_PIDS=$(ps aux | grep -E "(vite|pnpm)" | grep -v grep | awk '{print $2}')
if [ -n "$FRONTEND_PIDS" ]; then
    kill -9 $FRONTEND_PIDS 2>/dev/null
    echo "      前端服务已停止 (PIDs: $FRONTEND_PIDS)"
else
    echo "      前端服务未运行"
fi
echo ""

echo "[2/2] 停止后端服务..."
BACKEND_PIDS=$(ps aux | grep "mitedtsm-server" | grep -v grep | awk '{print $2}')
if [ -n "$BACKEND_PIDS" ]; then
    kill -9 $BACKEND_PIDS 2>/dev/null
    echo "      后端服务已停止 (PIDs: $BACKEND_PIDS)"
else
    echo "      后端服务未运行"
fi
echo ""

echo "========================================"
echo "           项目停止完成!"
echo "========================================"