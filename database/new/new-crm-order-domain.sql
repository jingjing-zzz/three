-- CRM 订单域无损建表增量：不删除已有表或数据
SET NAMES utf8mb4;

-- ----------------------------
-- Table structure for crm_order
-- ----------------------------
CREATE TABLE IF NOT EXISTS `crm_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单编号',
  `no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号（唯一）',
  `status` int NOT NULL DEFAULT 0 COMMENT '订单状态：0-草稿，10-待审核，20-审核中，30-审核通过，40-审核不通过，50-已完成，60-已取消',
  `customer_id` bigint NOT NULL COMMENT '客户编号',
  `contract_id` bigint NULL DEFAULT NULL COMMENT '关联合同编号',
  `owner_user_id` bigint NOT NULL COMMENT '负责人编号',
  `order_time` datetime NOT NULL COMMENT '下单时间',
  `total_product_price` decimal(16, 2) NOT NULL DEFAULT 0.00 COMMENT '商品总价',
  `discount_percent` decimal(5, 2) NOT NULL DEFAULT 0.00 COMMENT '折扣百分比',
  `total_price` decimal(16, 2) NOT NULL DEFAULT 0.00 COMMENT '订单总价',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '流程实例编号',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_no` (`no`) USING BTREE,
  KEY `idx_customer_id` (`customer_id`) USING BTREE,
  KEY `idx_contract_id` (`contract_id`) USING BTREE,
  KEY `idx_owner_user_id` (`owner_user_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_order_time` (`order_time`) USING BTREE,
  KEY `idx_process_instance_id` (`process_instance_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10001 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'CRM订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for crm_order_product
-- ----------------------------
CREATE TABLE IF NOT EXISTS `crm_order_product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单商品编号',
  `order_id` bigint NOT NULL COMMENT '订单编号',
  `product_id` bigint NOT NULL COMMENT '商品编号',
  `product_price` decimal(16, 2) NOT NULL DEFAULT 0.00 COMMENT '商品单价',
  `count` decimal(10, 2) NOT NULL DEFAULT 1.00 COMMENT '数量',
  `total_price` decimal(16, 2) NOT NULL DEFAULT 0.00 COMMENT '商品小计',
  `tax_percent` decimal(5, 2) NOT NULL DEFAULT 0.00 COMMENT '税率(%)',
  `tax_price` decimal(16, 2) NOT NULL DEFAULT 0.00 COMMENT '税额',
  `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_order_id` (`order_id`) USING BTREE,
  KEY `idx_product_id` (`product_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10001 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'CRM订单商品表' ROW_FORMAT = DYNAMIC;

-- 订单审批复用合同的流程实例字段；仅在旧库尚未具备该字段时补充。
SET @order_schema_name = DATABASE();
SET @order_contract_process_column_exists = (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @order_schema_name
    AND TABLE_NAME = 'crm_contract'
    AND COLUMN_NAME = 'process_instance_id'
);
SET @order_add_contract_process_sql = IF(
  @order_contract_process_column_exists = 0,
  'ALTER TABLE `crm_contract` ADD COLUMN `process_instance_id` varchar(64) DEFAULT NULL COMMENT ''流程实例编号''',
  'SELECT ''crm_contract.process_instance_id 已存在'''
);
PREPARE order_add_contract_process_stmt FROM @order_add_contract_process_sql;
EXECUTE order_add_contract_process_stmt;
DEALLOCATE PREPARE order_add_contract_process_stmt;

SET FOREIGN_KEY_CHECKS = 1;
