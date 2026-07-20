CREATE TABLE IF NOT EXISTS `crm_business_quotation_snapshot` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
  `business_id` BIGINT NOT NULL COMMENT '商机编号',
  `quotation_id` BIGINT NOT NULL COMMENT '报价编号',
  `quotation_no` VARCHAR(64) NOT NULL COMMENT '报价编号',
  `status` TINYINT NOT NULL COMMENT '状态（0-草稿，1-已确认，2-已作废）',
  `total_product_price` DECIMAL(14,2) DEFAULT NULL COMMENT '产品总价',
  `discount_percent` DECIMAL(5,2) DEFAULT NULL COMMENT '折扣（%）',
  `total_price` DECIMAL(14,2) NOT NULL COMMENT '报价总金额',
  `confirmed_by` BIGINT DEFAULT NULL COMMENT '确认人',
  `confirmed_time` DATETIME DEFAULT NULL COMMENT '确认时间',
  `remark` VARCHAR(512) DEFAULT NULL COMMENT '备注',
  `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_business_id` (`business_id`),
  INDEX `idx_quotation_id` (`quotation_id`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商机报价快照'