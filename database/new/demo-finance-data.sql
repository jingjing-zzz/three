-- 财务域演示数据。依赖 new-finance-domain.sql，可重复执行。
SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

SET @demo_tenant_id = COALESCE((
  SELECT `id` FROM `system_tenant`
  WHERE `status` = 0 AND `deleted` = b'0'
  ORDER BY (`id` = 1) DESC, `id`
  LIMIT 1
), 1);
SET @demo_user_id = COALESCE((
  SELECT `id` FROM `system_users`
  WHERE `tenant_id` = @demo_tenant_id AND `status` = 0 AND `deleted` = b'0'
  ORDER BY (`username` = 'admin') DESC, `id`
  LIMIT 1
), 1);

INSERT INTO `erp_account` (`name`, `no`, `remark`, `status`, `sort`, `default_status`,
                           `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT '演示基本户', 'DEMO-CASH', '财务域演示专用结算账户', 0, 1, b'1',
       @demo_user_id, NOW(), @demo_user_id, NOW(), b'0', @demo_tenant_id
WHERE NOT EXISTS (
  SELECT 1 FROM `erp_account`
  WHERE `no` = 'DEMO-CASH' AND `tenant_id` = @demo_tenant_id AND `deleted` = b'0'
);
SET @demo_account_id = (
  SELECT `id` FROM `erp_account`
  WHERE `no` = 'DEMO-CASH' AND `tenant_id` = @demo_tenant_id AND `deleted` = b'0'
  ORDER BY `id` LIMIT 1
);

INSERT INTO `erp_customer` (`name`, `contact`, `mobile`, `remark`, `status`, `sort`,
                            `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT '星云科技（演示客户）', '李经理', '13800000001', '财务域演示客户，可用于销售与收款流程', 0, 1,
       @demo_user_id, NOW(), @demo_user_id, NOW(), b'0', @demo_tenant_id
WHERE NOT EXISTS (
  SELECT 1 FROM `erp_customer`
  WHERE `name` = '星云科技（演示客户）' AND `tenant_id` = @demo_tenant_id AND `deleted` = b'0'
);

INSERT INTO `erp_finance_record`
(`no`, `type`, `status`, `record_time`, `due_time`, `overdue`, `applicant_user_id`,
 `finance_user_id`, `account_id`, `subject`, `counterparty`, `invoice_no`, `amount`,
 `tax_amount`, `total_amount`, `process_instance_id`, `remark`, `creator`, `create_time`,
 `updater`, `update_time`, `deleted`, `tenant_id`)
VALUES
('DEMO-FP-001', 10, 20, DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_ADD(NOW(), INTERVAL 10 DAY), b'0',
 @demo_user_id, @demo_user_id, @demo_account_id, '年度软件服务项目首期款', '星云科技（演示客户）',
 'INV-DEMO-2026-001', 128000.000000, 16640.000000, 144640.000000, NULL,
 '已审核发票，用于展示正常收入', @demo_user_id, NOW(), @demo_user_id, NOW(), b'0', @demo_tenant_id),
('DEMO-FP-002', 10, 10, DATE_SUB(NOW(), INTERVAL 45 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), b'1',
 @demo_user_id, @demo_user_id, @demo_account_id, '数据治理咨询尾款', '远航制造（演示）',
 'INV-DEMO-2026-002', 36000.000000, 4680.000000, 40680.000000, NULL,
 '逾期发票，用于展示逾期筛选和检测', @demo_user_id, NOW(), @demo_user_id, NOW(), b'0', @demo_tenant_id),
('DEMO-BX-001', 20, 20, DATE_SUB(NOW(), INTERVAL 12 DAY), NULL, b'0',
 @demo_user_id, @demo_user_id, @demo_account_id, '华东客户现场差旅报销', '内部员工',
 NULL, 4860.000000, 0.000000, 4860.000000, NULL,
 '历史已审批报销，用于财务分析演示', @demo_user_id, NOW(), @demo_user_id, NOW(), b'0', @demo_tenant_id),
('DEMO-TK-001', 30, 20, DATE_SUB(NOW(), INTERVAL 8 DAY), NULL, b'0',
 @demo_user_id, @demo_user_id, @demo_account_id, '客户服务范围调整退款', '云帆贸易（演示）',
 NULL, 12800.000000, 0.000000, 12800.000000, NULL,
 '历史已审批退款，用于财务分析演示', @demo_user_id, NOW(), @demo_user_id, NOW(), b'0', @demo_tenant_id),
('DEMO-FY-001', 40, 20, DATE_SUB(NOW(), INTERVAL 5 DAY), NULL, b'0',
 @demo_user_id, @demo_user_id, @demo_account_id, '七月云资源订阅费', '云服务供应商（演示）',
 NULL, 3200.000000, 192.000000, 3392.000000, NULL,
 '已审核日常费用', @demo_user_id, NOW(), @demo_user_id, NOW(), b'0', @demo_tenant_id)
ON DUPLICATE KEY UPDATE
  `status` = VALUES(`status`), `record_time` = VALUES(`record_time`),
  `due_time` = VALUES(`due_time`), `overdue` = VALUES(`overdue`),
  `applicant_user_id` = VALUES(`applicant_user_id`), `finance_user_id` = VALUES(`finance_user_id`),
  `account_id` = VALUES(`account_id`), `subject` = VALUES(`subject`),
  `counterparty` = VALUES(`counterparty`), `invoice_no` = VALUES(`invoice_no`),
  `amount` = VALUES(`amount`), `tax_amount` = VALUES(`tax_amount`),
  `total_amount` = VALUES(`total_amount`), `remark` = VALUES(`remark`),
  `updater` = VALUES(`updater`), `update_time` = NOW(), `deleted` = b'0';

SELECT @demo_tenant_id AS `演示租户`, @demo_user_id AS `演示用户`,
       @demo_account_id AS `演示账户`, COUNT(*) AS `财务演示单据数`
FROM `erp_finance_record`
WHERE `no` LIKE 'DEMO-%' AND `tenant_id` = @demo_tenant_id AND `deleted` = b'0';
