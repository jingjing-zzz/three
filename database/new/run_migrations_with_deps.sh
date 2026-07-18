#!/bin/bash

MYSQL_CMD="docker exec mitedtsm-mysql mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database"
SQL_DIR="/home/renmen/product/database/new"

# First, copy files to container to avoid pipe issues
echo "=== Copying SQL files to container ==="
docker cp "$SQL_DIR/new-i18n.sql" mitedtsm-mysql:/tmp/
docker cp "$SQL_DIR/new-large-file-upload.sql" mitedtsm-mysql:/tmp/
docker cp "$SQL_DIR/new-mall-i18n.sql" mitedtsm-mysql:/tmp/
docker cp "$SQL_DIR/new-product-category-i18n.sql" mitedtsm-mysql:/tmp/
docker cp "$SQL_DIR/new-i18n-ar.sql" mitedtsm-mysql:/tmp/

echo ""
echo "=== Running migrations in correct dependency order ==="

# 1. new-i18n.sql (creates system_menu_i18n, dependency for others)
echo ""
echo "1. Executing: new-i18n.sql"
docker exec mitedtsm-mysql bash -c "mysql -u root -p1234 mitedtsm_database < /tmp/new-i18n.sql"
if [ $? -eq 0 ]; then
    echo "   ✓ Success"
else
    echo "   ✗ Failed"
fi

# 2. new-large-file-upload.sql
echo ""
echo "2. Executing: new-large-file-upload.sql"
docker exec mitedtsm-mysql bash -c "mysql -u root -p1234 mitedtsm_database < /tmp/new-large-file-upload.sql"
if [ $? -eq 0 ]; then
    echo "   ✓ Success"
else
    echo "   ✗ Failed"
fi

# 3. new-mall-i18n.sql
echo ""
echo "3. Executing: new-mall-i18n.sql"
docker exec mitedtsm-mysql bash -c "mysql -u root -p1234 mitedtsm_database < /tmp/new-mall-i18n.sql"
if [ $? -eq 0 ]; then
    echo "   ✓ Success"
else
    echo "   ✗ Failed"
fi

# 4. new-product-category-i18n.sql
echo ""
echo "4. Executing: new-product-category-i18n.sql"
docker exec mitedtsm-mysql bash -c "mysql -u root -p1234 mitedtsm_database < /tmp/new-product-category-i18n.sql"
if [ $? -eq 0 ]; then
    echo "   ✓ Success"
else
    echo "   ✗ Failed"
fi

# 5. new-i18n-ar.sql (depends on system_menu_i18n)
echo ""
echo "5. Executing: new-i18n-ar.sql"
docker exec mitedtsm-mysql bash -c "mysql -u root -p1234 mitedtsm_database < /tmp/new-i18n-ar.sql"
if [ $? -eq 0 ]; then
    echo "   ✓ Success"
else
    echo "   ✗ Failed"
fi

echo ""
echo "=== All migrations completed ==="
echo ""
echo "Verifying key tables..."
docker exec mitedtsm-mysql bash -c "mysql -u root -p1234 mitedtsm_database -e 'SHOW TABLES LIKE \"infra_file_upload_task\";'"
docker exec mitedtsm-mysql bash -c "mysql -u root -p1234 mitedtsm_database -e 'SHOW TABLES LIKE \"system_menu_i18n\";'"
docker exec mitedtsm-mysql bash -c "mysql -u root -p1234 mitedtsm_database -e 'SHOW TABLES LIKE \"promotion_diy_menu_i18n\";'"
docker exec mitedtsm-mysql bash -c "mysql -u root -p1234 mitedtsm_database -e 'SHOW TABLES LIKE \"product_category_i18n\";'"
