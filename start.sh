#!/bin/bash

PROJECT_DIR=$(cd "$(dirname "$0")" && pwd)

echo "========================================"
echo "      MITEDTSM 项目启动脚本"
echo "========================================"
echo ""
echo "🚀 启动后端服务..."
cd "$PROJECT_DIR/Server"
nohup mvn spring-boot:run -pl mitedtsm-server -Dspring-boot.run.profiles=local > /tmp/mitedtsm_backend.log 2>&1 &
echo "后端服务已启动，日志: /tmp/mitedtsm_backend.log"

echo ""
echo "⏳ 等待后端启动中..."
sleep 60

echo ""
echo "🚀 启动前端服务..."
cd "$PROJECT_DIR/Web"
nohup pnpm dev > /tmp/mitedtsm_frontend.log 2>&1 &
echo "前端服务已启动，日志: /tmp/mitedtsm_frontend.log"

echo ""
echo "========================================"
echo "✅ 服务启动完成！"
echo ""
echo "🌐 前端地址: http://localhost:8081"
echo "🔧 后端地址: http://localhost:8080"
echo ""
echo "📝 停止命令:"
echo "  ps aux | grep mitedtsm | grep -v grep | awk '{print \$2}' | xargs kill -9"
echo ""
echo "📋 查看日志:"
echo "  tail -f /tmp/mitedtsm_backend.log   # 后端日志"
echo "  tail -f /tmp/mitedtsm_frontend.log  # 前端日志"
echo "========================================"