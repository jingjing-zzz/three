#!/bin/bash

MYSQL_CMD="docker exec mitedtsm-mysql mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database"
SQL_DIR="/home/renmen/product/database/new"

echo "=== Running all incremental SQL scripts ==="

# 1. new-large-file-upload.sql
echo ""
echo "1. Executing: new-large-file-upload.sql"
$MYSQL_CMD < "$SQL_DIR/new-large-file-upload.sql"
if [ $? -eq 0 ]; then
    echo "   ✓ Success"
else
    echo "   ✗ Failed"
fi

# 2. new-i18n.sql
echo ""
echo "2. Executing: new-i18n.sql"
$MYSQL_CMD < "$SQL_DIR/new-i18n.sql"
if [ $? -eq 0 ]; then
    echo "   ✓ Success"
else
    echo "   ✗ Failed"
fi

# 3. new-mall-i18n.sql (includes currency_code ALTER TABLE)
echo ""
echo "3. Executing: new-mall-i18n.sql"
$MYSQL_CMD < "$SQL_DIR/new-mall-i18n.sql"
if [ $? -eq 0 ]; then
    echo "   ✓ Success"
else
    echo "   ✗ Failed"
fi

# 4. new-product-category-i18n.sql
echo ""
echo "4. Executing: new-product-category-i18n.sql"
$MYSQL_CMD < "$SQL_DIR/new-product-category-i18n.sql"
if [ $? -eq 0 ]; then
    echo "   ✓ Success"
else
    echo "   ✗ Failed"
fi

# 5. new-i18n-ar.sql
echo ""
echo "5. Executing: new-i18n-ar.sql"
$MYSQL_CMD < "$SQL_DIR/new-i18n-ar.sql"
if [ $? -eq 0 ]; then
    echo "   ✓ Success"
else
    echo "   ✗ Failed"
fi

# 6. CRM business fields (already executed, but run again for safety)
echo ""
echo "6. Executing: new-crm-business-fields.sql"
$MYSQL_CMD < "$SQL_DIR/new-crm-business-fields.sql"
if [ $? -eq 0 ]; then
    echo "   ✓ Success"
else
    echo "   ✗ Failed"
fi

# 7. CRM business quotation (already executed)
echo ""
echo "7. Executing: new-crm-business-quotation.sql"
$MYSQL_CMD < "$SQL_DIR/new-crm-business-quotation.sql"
if [ $? -eq 0 ]; then
    echo "   ✓ Success"
else
    echo "   ✗ Failed"
fi

# 8. CRM business quotation snapshot (already executed)
echo ""
echo "8. Executing: new-crm-business-quotation-snapshot.sql"
$MYSQL_CMD < "$SQL_DIR/new-crm-business-quotation-snapshot.sql"
if [ $? -eq 0 ]; then
    echo "   ✓ Success"
else
    echo "   ✗ Failed"
fi

# 9. CRM business source dict (already executed, but fix encoding)
echo ""
echo "9. Executing: new-crm-business-source-dict.sql"
$MYSQL_CMD < "$SQL_DIR/new-crm-business-source-dict.sql"
if [ $? -eq 0 ]; then
    echo "   ✓ Success"
else
    echo "   ✗ Failed"
fi

# 10. Fix dict chinese encoding
echo ""
echo "10. Executing: fix_dict_chinese.sql"
$MYSQL_CMD < "$SQL_DIR/fix_dict_chinese.sql"
if [ $? -eq 0 ]; then
    echo "   ✓ Success"
else
    echo "   ✗ Failed"
fi

# 11. Fix currency code (already executed, but run again for safety)
echo ""
echo "11. Executing: fix_currency_code.sql"
$MYSQL_CMD < "$SQL_DIR/fix_currency_code.sql"
if [ $? -eq 0 ]; then
    echo "   ✓ Success"
else
    echo "   ✗ Failed"
fi

echo ""
echo "=== All migrations completed ==="
echo ""
echo "Verifying key tables..."
$MYSQL_CMD -e "SHOW TABLES LIKE 'infra_file_upload_task';"
$MYSQL_CMD -e "SHOW TABLES LIKE 'system_menu_i18n';"
$MYSQL_CMD -e "SHOW TABLES LIKE 'promotion_diy_menu_i18n';"
$MYSQL_CMD -e "SHOW TABLES LIKE 'product_category_i18n';"
$MYSQL_CMD -e "SHOW COLUMNS FROM system_tenant LIKE 'currency_code';"
