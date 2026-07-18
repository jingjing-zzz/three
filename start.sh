#!/bin/bash

PROJECT_DIR="/home/feng/product"
LOG_DIR="$PROJECT_DIR/logs"
BACKEND_JAR="$PROJECT_DIR/Server/mitedtsm-server/target/mitedtsm-server.jar"

mkdir -p $LOG_DIR

echo "========================================"
echo "        一键启动 mitedtsm 项目"
echo "========================================"
echo ""

echo "[1/3] 启动后端服务..."
if [ ! -f "$BACKEND_JAR" ]; then
    echo "      错误: 后端JAR包不存在: $BACKEND_JAR"
    echo "      请先编译后端项目: cd Server && mvn clean package -DskipTests"
    exit 1
fi

nohup java -Xms2g -Xmx4g -XX:+UseG1GC -jar "$BACKEND_JAR" --spring.profiles.active=local > $LOG_DIR/backend.log 2>&1 &
BACKEND_PID=$!
echo "      后端服务启动中 (PID: $BACKEND_PID)"
echo ""

echo "[2/3] 等待后端服务启动..."
MAX_WAIT=120
WAIT_INTERVAL=5
ELAPSED=0

while [ $ELAPSED -lt $MAX_WAIT ]; do
    if ss -tlnp | grep -q ":8080"; then
        echo "      后端服务启动成功!"
        break
    fi
    sleep $WAIT_INTERVAL
    ELAPSED=$((ELAPSED + WAIT_INTERVAL))
done

if [ $ELAPSED -ge $MAX_WAIT ]; then
    echo "      后端服务启动超时，检查日志: $LOG_DIR/backend.log"
    tail -20 $LOG_DIR/backend.log
fi
echo ""

echo "[3/3] 启动前端服务..."
cd $PROJECT_DIR/Web
nohup pnpm run dev > $LOG_DIR/frontend.log 2>&1 &
FRONTEND_PID=$!
echo "      前端服务启动中 (PID: $FRONTEND_PID)"
echo ""

echo "========================================"
echo "           项目启动完成!"
echo "========================================"
echo ""
echo "访问地址:"
echo "  前端页面:    http://localhost:5173/"
echo "  后端API:     http://localhost:8080/"
echo ""
echo "日志文件:"
echo "  后端日志:    $LOG_DIR/backend.log"
echo "  前端日志:    $LOG_DIR/frontend.log"
echo ""
echo "停止服务请运行: ./stop.sh"