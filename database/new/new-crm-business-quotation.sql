-- ----------------------------
-- 商机报价表
-- ----------------------------
DROP TABLE IF EXISTS `crm_business_quotation`;
CREATE TABLE `crm_business_quotation` (
    `id`                  BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '编号',
    `business_id`         BIGINT(20)   NOT NULL COMMENT '商机编号',
    `quotation_no`        VARCHAR(64)  NOT NULL COMMENT '报价编号',
    `status`              TINYINT(4)   NOT NULL DEFAULT 0 COMMENT '报价状态（0草稿 1已确认 2已作废）',
    `total_product_price` DECIMAL(20,2) DEFAULT NULL COMMENT '产品原价合计',
    `discount_percent`    DECIMAL(20,2) DEFAULT NULL COMMENT '整单折扣',
    `total_price`         DECIMAL(20,2) DEFAULT NULL COMMENT '报价总额',
    `confirmed_by`        BIGINT(20)   DEFAULT NULL COMMENT '确认人',
    `confirmed_time`      DATETIME     DEFAULT NULL COMMENT '确认时间',
    `remark`              VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `tenant_id`           BIGINT(20)   NOT NULL DEFAULT 0 COMMENT '租户编号',
    `creator`             VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`             VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_business_id` (`business_id`),
    KEY `idx_quotation_no` (`quotation_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 商机报价表';

-- ----------------------------
-- 商机报价产品项表
-- ----------------------------
DROP TABLE IF EXISTS `crm_business_quotation_item`;
CREATE TABLE `crm_business_quotation_item` (
    `id`               BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '编号',
    `quotation_id`     BIGINT(20)   NOT NULL COMMENT '报价编号',
    `product_id`       BIGINT(20)   DEFAULT NULL COMMENT '产品编号',
    `product_name`     VARCHAR(200) DEFAULT NULL COMMENT '产品名称（快照）',
    `product_no`       VARCHAR(100) DEFAULT NULL COMMENT '产品编码（快照）',
    `standard_price`   DECIMAL(20,2) DEFAULT NULL COMMENT '标准价',
    `actual_price`     DECIMAL(20,2) DEFAULT NULL COMMENT '实际售价',
    `count`            DECIMAL(20,2) DEFAULT NULL COMMENT '数量',
    `discount_percent` DECIMAL(20,2) DEFAULT NULL COMMENT '行折扣',
    `total_price`      DECIMAL(20,2) DEFAULT NULL COMMENT '行总价',
    `gift`             VARCHAR(500) DEFAULT NULL COMMENT '礼品',
    `remark`           VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `tenant_id`        BIGINT(20)   NOT NULL DEFAULT 0 COMMENT '租户编号',
    `creator`          VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`          VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_quotation_id` (`quotation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 商机报价产品项表';
