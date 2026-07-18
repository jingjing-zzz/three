-- 修复报价快照表与 CrmBusinessQuotationSnapshotDO(BaseDO) 的字段一致性。
-- 通过 information_schema 判断字段是否存在，可重复执行。

SET @schema_name = DATABASE();

SET @sql = IF(
  (SELECT COUNT(*) FROM information_schema.columns
   WHERE table_schema = @schema_name AND table_name = 'crm_business_quotation_snapshot' AND column_name = 'tenant_id') = 0,
  'ALTER TABLE crm_business_quotation_snapshot ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 AFTER id',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  (SELECT COUNT(*) FROM information_schema.columns
   WHERE table_schema = @schema_name AND table_name = 'crm_business_quotation_snapshot' AND column_name = 'update_time') = 0,
  'ALTER TABLE crm_business_quotation_snapshot ADD COLUMN update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER create_time',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  (SELECT COUNT(*) FROM information_schema.columns
   WHERE table_schema = @schema_name AND table_name = 'crm_business_quotation_snapshot' AND column_name = 'updater') = 0,
  'ALTER TABLE crm_business_quotation_snapshot ADD COLUMN updater VARCHAR(64) NULL AFTER update_time',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  (SELECT COUNT(*) FROM information_schema.columns
   WHERE table_schema = @schema_name AND table_name = 'crm_business_quotation_snapshot' AND column_name = 'deleted') = 0,
  'ALTER TABLE crm_business_quotation_snapshot ADD COLUMN deleted BIT(1) NOT NULL DEFAULT b\'0\' AFTER updater',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  (SELECT COUNT(*) FROM information_schema.statistics
   WHERE table_schema = @schema_name AND table_name = 'crm_business_quotation_snapshot'
     AND index_name = 'idx_crm_business_quotation_snapshot_tenant_id') = 0,
  'CREATE INDEX idx_crm_business_quotation_snapshot_tenant_id ON crm_business_quotation_snapshot (tenant_id)',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
