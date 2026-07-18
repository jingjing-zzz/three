SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for marketing_campaign
-- ----------------------------
DROP TABLE IF EXISTS `marketing_campaign`;
CREATE TABLE `marketing_campaign`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '活动名称',
  `type` tinyint NOT NULL DEFAULT 1 COMMENT '活动类型：1-短信营销 2-邮件营销 3-组合营销',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '活动状态：1-草稿 2-进行中 3-已结束 4-已暂停',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `target_filter` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '目标客户筛选条件(JSON)',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活动描述',
  `total_target_count` int NULL DEFAULT 0 COMMENT '目标客户总数',
  `send_count` int NULL DEFAULT 0 COMMENT '已发送数量',
  `success_count` int NULL DEFAULT 0 COMMENT '成功数量',
  `fail_count` int NULL DEFAULT 0 COMMENT '失败数量',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id`),
  INDEX `idx_status`(`status`),
  INDEX `idx_type`(`type`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '营销活动主表';

-- ----------------------------
-- Table structure for marketing_sms_batch
-- ----------------------------
DROP TABLE IF EXISTS `marketing_sms_batch`;
CREATE TABLE `marketing_sms_batch`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  `campaign_id` bigint NULL DEFAULT NULL COMMENT '营销活动编号',
  `template_id` bigint NOT NULL COMMENT '短信模板编号',
  `phone_list` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '目标手机号列表(JSON数组)',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '批次状态：1-待发送 2-发送中 3-已完成 4-已取消',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `total_count` int NULL DEFAULT 0 COMMENT '总数',
  `success_count` int NULL DEFAULT 0 COMMENT '成功数量',
  `fail_count` int NULL DEFAULT 0 COMMENT '失败数量',
  `send_rate` decimal(5, 2) NULL DEFAULT 0 COMMENT '发送成功率',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id`),
  INDEX `idx_campaign_id`(`campaign_id`),
  INDEX `idx_status`(`status`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '短信批次发送表';

-- ----------------------------
-- Table structure for marketing_email_batch
-- ----------------------------
DROP TABLE IF EXISTS `marketing_email_batch`;
CREATE TABLE `marketing_email_batch`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  `campaign_id` bigint NULL DEFAULT NULL COMMENT '营销活动编号',
  `template_id` bigint NOT NULL COMMENT '邮件模板编号',
  `email_list` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '目标邮箱列表(JSON数组)',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '批次状态：1-待发送 2-发送中 3-已完成 4-已取消',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `total_count` int NULL DEFAULT 0 COMMENT '总数',
  `success_count` int NULL DEFAULT 0 COMMENT '成功数量',
  `fail_count` int NULL DEFAULT 0 COMMENT '失败数量',
  `send_rate` decimal(5, 2) NULL DEFAULT 0 COMMENT '发送成功率',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id`),
  INDEX `idx_campaign_id`(`campaign_id`),
  INDEX `idx_status`(`status`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '邮件批次发送表';

-- ----------------------------
-- Table structure for marketing_send_record
-- ----------------------------
DROP TABLE IF EXISTS `marketing_send_record`;
CREATE TABLE `marketing_send_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  `batch_id` bigint NOT NULL COMMENT '批次编号',
  `target` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标地址(手机号/邮箱)',
  `target_type` tinyint NOT NULL DEFAULT 1 COMMENT '目标类型：1-短信 2-邮件',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '发送状态：1-待发送 2-发送中 3-成功 4-失败',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `error_msg` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '错误信息',
  `ext_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '扩展数据(JSON)',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id`),
  INDEX `idx_batch_id`(`batch_id`),
  INDEX `idx_status`(`status`),
  INDEX `idx_target_type`(`target_type`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '单条发送记录表';

-- ----------------------------
-- Table structure for marketing_customer_care
-- ----------------------------
DROP TABLE IF EXISTS `marketing_customer_care`;
CREATE TABLE `marketing_customer_care`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  `rule_type` tinyint NOT NULL DEFAULT 1 COMMENT '规则类型：1-生日关怀 2-节日关怀',
  `festival_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '节日日期(格式：MM-DD)',
  `template_id` bigint NULL DEFAULT NULL COMMENT '模板编号(短信/邮件)',
  `channel_type` tinyint NOT NULL DEFAULT 1 COMMENT '发送渠道：1-短信 2-邮件 3-短信+邮件',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用 2-禁用',
  `target_filter` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '目标客户筛选条件(JSON)',
  `send_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '09:00' COMMENT '发送时间(格式：HH:mm)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id`),
  INDEX `idx_rule_type`(`rule_type`),
  INDEX `idx_status`(`status`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '客户关怀规则表';

-- ----------------------------
-- Table structure for marketing_approval
-- ----------------------------
DROP TABLE IF EXISTS `marketing_approval`;
CREATE TABLE `marketing_approval`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  `campaign_id` bigint NULL DEFAULT NULL COMMENT '营销活动编号',
  `batch_id` bigint NULL DEFAULT NULL COMMENT '批次编号',
  `type` tinyint NOT NULL DEFAULT 1 COMMENT '审批类型：1-短信群发审批 2-邮件群发审批',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '审批状态：1-待审批 2-已通过 3-已拒绝',
  `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '流程实例编号',
  `approver` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审批人',
  `approve_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审批备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id`),
  INDEX `idx_campaign_id`(`campaign_id`),
  INDEX `idx_status`(`status`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '群发审批表';

SET FOREIGN_KEY_CHECKS = 1;