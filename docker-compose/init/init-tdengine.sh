#!/bin/bash
# TDengine 初始化脚本
# 用于创建时序数据库，表结构由 InitService 负责初始化
# 可在 TDengine 容器内手动执行，或作为参考文档

set -e

echo "Starting TDengine initialization..."

# TDengine 连接配置
TDENGINE_HOST="${TDENGINE_HOST:-localhost}"
TDENGINE_PORT="${TDENGINE_PORT:-6041}"
TDENGINE_USER="${TDENGINE_USER:-root}"
TDENGINE_PASSWORD="${TDENGINE_PASSWORD:-taosdata}"
TDENGINE_DATABASE="${TDENGINE_DATABASE:-mitedtsm_database}"

# 等待 TDengine 完全启动
echo "Waiting for TDengine to be ready..."
until taos -h "${TDENGINE_HOST}" -P "${TDENGINE_PORT}" -u "${TDENGINE_USER}" -p"${TDENGINE_PASSWORD}" -s "SELECT 1" > /dev/null 2>&1; do
    echo "Waiting for TDengine to be ready..."
    sleep 2
done

echo "TDengine is ready, creating database..."

# 创建数据库（如果不存在）
taos -h "${TDENGINE_HOST}" -P "${TDENGINE_PORT}" -u "${TDENGINE_USER}" -p"${TDENGINE_PASSWORD}" -s \
    "CREATE DATABASE IF NOT EXISTS ${TDENGINE_DATABASE} KEEP 3650 DURATION 10 BUFFER 1024 PAGES 256 PAGESIZE 4;"

echo "TDengine initialization completed successfully!"
echo "Database '${TDENGINE_DATABASE}' is ready."
echo "Table initialization is handled by the InitService container."
