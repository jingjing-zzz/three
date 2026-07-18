-- MySQL 不支持 ADD COLUMN IF NOT EXISTS，使用 information_schema 保证脚本可重复执行。
SET @currency_column_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'system_tenant'
      AND COLUMN_NAME = 'currency_code'
);
SET @currency_sql := IF(@currency_column_exists = 0,
    'ALTER TABLE system_tenant ADD COLUMN currency_code VARCHAR(10) NOT NULL DEFAULT ''CNY''',
    'SELECT 1');
PREPARE currency_stmt FROM @currency_sql;
EXECUTE currency_stmt;
DEALLOCATE PREPARE currency_stmt;
UPDATE system_tenant SET currency_code = 'CNY' WHERE currency_code IS NULL OR currency_code = '';
