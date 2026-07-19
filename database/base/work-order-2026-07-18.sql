-- ============================================
-- 工单管理模块数据库初始化脚本
-- 创建日期：2026-07-18
-- 功能说明：工单CRUD、状态流转、类型/优先级管理
-- ============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for work_order
-- 工单主表
-- ----------------------------
DROP TABLE IF EXISTS `work_order`;
CREATE TABLE `work_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '工单编号',
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '工单号（唯一标识）',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '工单标题',
  `type` tinyint NOT NULL COMMENT '工单类型：1-故障报修 2-服务请求 3-咨询投诉 4-变更申请',
  `priority` tinyint NOT NULL COMMENT '优先级：1-低 2-中 3-高 4-紧急',
  `status` tinyint NOT NULL COMMENT '工单状态：1-待处理 2-处理中 3-已完结 4-已退回',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '工单内容描述',
  `reporter_id` bigint NOT NULL COMMENT '发起人编号',
  `reporter_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发起人姓名',
  `assignee_id` bigint NULL DEFAULT NULL COMMENT '处理人编号',
  `assignee_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '处理人姓名',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '所属部门编号',
  `dept_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '部门名称',
  `customer_id` bigint NULL DEFAULT 0 COMMENT '客户编号',
  `customer_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '客户名称',
  `related_module` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '关联模块',
  `related_id` bigint NULL DEFAULT NULL COMMENT '关联业务ID',
  `handle_note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '处理备注',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `close_time` datetime NULL DEFAULT NULL COMMENT '完结时间',
  `reject_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '退回原因',
  `reject_time` datetime NULL DEFAULT NULL COMMENT '退回时间',
  `expected_finish_time` datetime NULL DEFAULT NULL COMMENT '预计完成时间',
  `actual_finish_time` datetime NULL DEFAULT NULL COMMENT '实际完成时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_order_no` (`order_no`) USING BTREE,
  INDEX `idx_status` (`status` ASC) USING BTREE,
  INDEX `idx_type` (`type` ASC) USING BTREE,
  INDEX `idx_priority` (`priority` ASC) USING BTREE,
  INDEX `idx_reporter_id` (`reporter_id` ASC) USING BTREE,
  INDEX `idx_assignee_id` (`assignee_id` ASC) USING BTREE,
  INDEX `idx_create_time` (`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '工单主表';

-- ----------------------------
-- Records of work_order
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for work_order_type
-- 工单类型配置表
-- ----------------------------
DROP TABLE IF EXISTS `work_order_type`;
CREATE TABLE `work_order_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '类型编号',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型编码',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '类型描述',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_code` (`code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '工单类型配置表';

-- ----------------------------
-- Records of work_order_type
-- 初始化工单类型数据
-- ----------------------------
BEGIN;
INSERT INTO `work_order_type` (`code`, `name`, `description`, `sort`, `status`) VALUES
('FAULT_REPAIR', '故障报修', '系统故障、设备故障等需要维修的工单', 1, 1),
('SERVICE_REQUEST', '服务请求', '日常服务申请、协助请求等', 2, 1),
('CONSULT_COMPLAINT', '咨询投诉', '业务咨询、问题投诉等', 3, 1),
('CHANGE_APPLICATION', '变更申请', '系统变更、配置调整等申请', 4, 1);
COMMIT;

-- ----------------------------
-- Table structure for work_order_status_flow
-- 工单状态流转记录表
-- ----------------------------
DROP TABLE IF EXISTS `work_order_status_flow`;
CREATE TABLE `work_order_status_flow` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录编号',
  `order_id` bigint NOT NULL COMMENT '工单编号',
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '工单号',
  `from_status` tinyint NOT NULL DEFAULT 0 COMMENT '原状态',
  `to_status` tinyint NOT NULL COMMENT '新状态',
  `operator_id` bigint NOT NULL COMMENT '操作人编号',
  `operator_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作人姓名',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id` (`order_id` ASC) USING BTREE,
  INDEX `idx_create_time` (`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '工单状态流转记录表';

-- ----------------------------
-- Records of work_order_status_flow
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
