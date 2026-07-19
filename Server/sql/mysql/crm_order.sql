SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 菜单权限配置 - 订单管理
-- ----------------------------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6004, '订单管理', '', 2, 60, 2397, 'order', 'ep:shopping-cart-full', 'crm/order/index', 'CrmOrder', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6005, '订单查询', 'crm:order:query', 3, 1, 6004, '', '', '', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6006, '订单创建', 'crm:order:create', 3, 2, 6004, '', '', '', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6007, '订单更新', 'crm:order:update', 3, 3, 6004, '', '', '', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6008, '订单删除', 'crm:order:delete', 3, 4, 6004, '', '', '', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6009, '订单导出', 'crm:order:export', 3, 5, 6004, '', '', '', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6010, '订单审批', 'crm:order:approval', 3, 6, 6004, '', '', '', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6011, '订单报表', 'crm:order:query', 3, 7, 6004, '', '', '', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');

-- ----------------------------
-- 角色菜单关联 - 超级管理员角色（role_id=2）
-- ----------------------------
INSERT INTO `system_role_menu` (`id`, `role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) VALUES (6004, 2, 6004, '1', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`id`, `role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) VALUES (6005, 2, 6005, '1', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`id`, `role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) VALUES (6006, 2, 6006, '1', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`id`, `role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) VALUES (6007, 2, 6007, '1', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`id`, `role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) VALUES (6008, 2, 6008, '1', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`id`, `role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) VALUES (6009, 2, 6009, '1', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`id`, `role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) VALUES (6010, 2, 6010, '1', NOW(), '1', NOW(), b'0', 1);

-- ----------------------------
-- Table structure for crm_order
-- ----------------------------
DROP TABLE IF EXISTS `crm_order`;
CREATE TABLE `crm_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单编号',
  `no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号（唯一）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '订单状态（1-待支付 2-已支付 3-已发货 4-已完成 5-已取消）',
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
DROP TABLE IF EXISTS `crm_order_product`;
CREATE TABLE `crm_order_product`  (
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

SET FOREIGN_KEY_CHECKS = 1;