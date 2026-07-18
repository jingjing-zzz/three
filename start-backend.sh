#!/bin/bash

BUILD=false
FRONTEND=true

while [[ $# -gt 0 ]]; do
    case "$1" in
        -b|--build)
            BUILD=true
            shift
            ;;
        -f|--frontend)
            FRONTEND=true
            shift
            ;;
        -a|--all)
            FRONTEND=true
            shift
            ;;
        *)
            shift
            ;;
    esac
done

cd /home/wangchun/project/three/Server

echo "=== 停止旧进程 ==="
pkill -f "spring-boot:run" 2>/dev/null
pkill -f "vite" 2>/dev/null
sleep 3

if [ "$BUILD" = true ]; then
    echo "=== 编译项目 ==="
    mvn clean install -DskipTests -q
    if [ $? -ne 0 ]; then
        echo "编译失败！"
        exit 1
    fi
fi

echo "=== 启动后端服务 ==="
nohup mvn spring-boot:run -pl mitedtsm-server -Dspring-boot.run.profiles=local > /tmp/mitedtsm_backend_new.log 2>&1 &
echo "后端启动中...日志路径: /tmp/mitedtsm_backend_new.log"

echo "=== 等待后端服务启动 ==="
count=0
max_wait=60
while [ $count -lt $max_wait ]; do
    if curl -s http://localhost:8080/actuator/health | grep -q "UP"; then
        echo "后端服务启动成功！http://localhost:8080"
        break
    fi
    count=$((count+1))
    sleep 5
done

if [ "$FRONTEND" = true ]; then
    echo "=== 启动前端服务 ==="
    cd /home/wangchun/project/three/Web
    nohup pnpm dev > /tmp/mitedtsm_frontend.log 2>&1 &
    echo "前端启动中...日志路径: /tmp/mitedtsm_frontend.log"

    echo "=== 等待前端服务启动 ==="
    count=0
    max_wait=30
    while [ $count -lt $max_wait ]; do
        if curl -s http://localhost:8081 > /dev/null; then
            echo "前端服务启动成功！http://localhost:8081"
            break
        fi
        count=$((count+1))
        sleep 5
    done
fi

echo ""
echo "=== 服务启动完成 ==="
echo "后端地址: http://localhost:8080"
echo "前端地址: http://localhost:8081"
echo ""
echo "默认登录信息:"
echo "  租户: 密讯"
echo "  用户名: admin"
echo "  密码: admin123"