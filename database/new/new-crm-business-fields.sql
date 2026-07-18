-- 商机域字段扩展：为全新库和已有库安全补充来源、竞争对手字段。
-- MySQL 不支持 ADD COLUMN IF NOT EXISTS，改用 information_schema 动态判断。
SET @schema_name := DATABASE();

SET @source_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name
      AND TABLE_NAME = 'crm_business'
      AND COLUMN_NAME = 'source'
);
SET @source_sql := IF(@source_exists = 0,
    'ALTER TABLE `crm_business` ADD COLUMN `source` VARCHAR(30) DEFAULT NULL COMMENT ''商机来源'' AFTER `name`',
    'SELECT 1');
PREPARE crm_business_source_stmt FROM @source_sql;
EXECUTE crm_business_source_stmt;
DEALLOCATE PREPARE crm_business_source_stmt;

SET @competitor_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name
      AND TABLE_NAME = 'crm_business'
      AND COLUMN_NAME = 'competitor'
);
SET @competitor_sql := IF(@competitor_exists = 0,
    'ALTER TABLE `crm_business` ADD COLUMN `competitor` VARCHAR(100) DEFAULT NULL COMMENT ''竞争对手'' AFTER `source`',
    'SELECT 1');
PREPARE crm_business_competitor_stmt FROM @competitor_sql;
EXECUTE crm_business_competitor_stmt;
DEALLOCATE PREPARE crm_business_competitor_stmt;
