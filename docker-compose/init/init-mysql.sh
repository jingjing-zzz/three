#!/bin/bash

set -e

echo "Starting MySQL initialization..."

until mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" --default-character-set=utf8mb4 -e "SELECT 1"; do
    echo "Waiting for MySQL to be ready..."
    sleep 2
done

echo "MySQL is ready, executing SQL files..."

SQL_BASE_DIR="/docker-init-sql/base"
SQL_NEW_DIR="/docker-init-sql/new"

# 执行一个增量 SQL 文件（如果存在）
exec_new() {
    local file="$1"
    if [ -f "${SQL_NEW_DIR}/${file}" ]; then
        echo "Executing: ${file}"
        mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" --default-character-set=utf8mb4 "${MYSQL_DATABASE}" < "${SQL_NEW_DIR}/${file}"
    fi
}

# 1. 基础表结构（quartz + ruoyi-vue-pro + 其他模块）
echo "Executing: quartz.sql"
mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" --default-character-set=utf8mb4 "${MYSQL_DATABASE}" < "${SQL_BASE_DIR}/quartz.sql"

echo "Executing: ruoyi-vue-pro.sql"
mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" --default-character-set=utf8mb4 "${MYSQL_DATABASE}" < "${SQL_BASE_DIR}/ruoyi-vue-pro.sql"

for sql_file in "${SQL_BASE_DIR}"/*.sql; do
    filename=$(basename "$sql_file")
    if [[ "$filename" != "quartz.sql" && "$filename" != "ruoyi-vue-pro.sql" ]]; then
        echo "Executing: ${filename}"
        mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" --default-character-set=utf8mb4 "${MYSQL_DATABASE}" < "$sql_file"
    fi
done

# 2. 增量 SQL（按依赖顺序执行）
if [ -d "$SQL_NEW_DIR" ]; then
    # 2.1 国际化
    exec_new "new-i18n.sql"
    exec_new "new-mall-i18n.sql"
    exec_new "new-i18n-ar.sql"
    exec_new "new-product-category-i18n.sql"

    # 2.2 其他基础增量
    exec_new "new-large-file-upload.sql"

    # 2.3 字典与币种修复
    exec_new "fix_dict_chinese.sql"
    exec_new "fix_currency_code.sql"

    # 2.4 商机域字段扩展 + 阶段完整性
    exec_new "new-crm-business-fields.sql"
    exec_new "new-crm-business-source-dict.sql"
    exec_new "fix-crm-business-status-integrity.sql"

    # 2.5 报价表 + 快照表（含 tenant_id 修复）
    exec_new "new-crm-business-quotation.sql"
    exec_new "new-crm-business-quotation-snapshot.sql"
    exec_new "fix-quotation-snapshot-tenant.sql"

    # 2.6 菜单与权限
    exec_new "new-crm-statistics-forecast-menu.sql"
    exec_new "new-crm-statistics-report-menu.sql"

    # 2.7 测试假数据（最后执行，依赖前面所有结构）
    exec_new "new-test-data.sql"
    exec_new "new-test-business-status-data.sql"
    exec_new "new-test-business-data.sql"
    exec_new "new-test-business-detail-data.sql"
fi

echo "MySQL initialization completed successfully!"
