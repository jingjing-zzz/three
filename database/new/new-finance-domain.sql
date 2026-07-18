-- 财务域：发票、报销、退款、费用、财务分析、逾期检测
SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `erp_finance_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `no` varchar(64) NOT NULL COMMENT '单据编号',
  `type` tinyint NOT NULL COMMENT '单据类型：10 发票，20 报销，30 退款，40 费用',
  `status` tinyint NOT NULL DEFAULT 10 COMMENT '审核状态：10 未审核，20 已审核',
  `record_time` datetime NOT NULL COMMENT '业务时间',
  `due_time` datetime NULL DEFAULT NULL COMMENT '到期时间',
  `overdue` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否逾期',
  `applicant_user_id` bigint NULL DEFAULT NULL COMMENT '申请人',
  `finance_user_id` bigint NULL DEFAULT NULL COMMENT '财务人员',
  `account_id` bigint NULL DEFAULT NULL COMMENT '结算账户',
  `subject` varchar(128) NOT NULL COMMENT '主题',
  `counterparty` varchar(128) NULL DEFAULT NULL COMMENT '往来单位',
  `invoice_no` varchar(64) NULL DEFAULT NULL COMMENT '发票号码',
  `amount` decimal(24, 6) NOT NULL DEFAULT 0.000000 COMMENT '不含税金额',
  `tax_amount` decimal(24, 6) NOT NULL DEFAULT 0.000000 COMMENT '税额',
  `total_amount` decimal(24, 6) NOT NULL DEFAULT 0.000000 COMMENT '价税合计',
  `process_instance_id` varchar(64) NULL DEFAULT NULL COMMENT '流程编号',
  `remark` varchar(500) NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_erp_finance_record_no` (`no`, `tenant_id`),
  KEY `idx_erp_finance_record_type` (`type`),
  KEY `idx_erp_finance_record_record_time` (`record_time`),
  KEY `idx_erp_finance_record_due_time` (`due_time`),
  KEY `idx_erp_finance_record_overdue` (`overdue`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 财务单据';

ALTER TABLE `crm_receivable_plan`
  ADD COLUMN `overdue` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否逾期' AFTER `receivable_id`;

INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
(5100, '发票管理', '', 2, 3, 2645, 'invoice', 'ep:tickets', 'erp/finance/invoice/index', 'ErpFinanceInvoice', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5101, '发票查询', 'erp:finance-record:query', 3, 1, 5100, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5102, '发票创建', 'erp:finance-record:create', 3, 2, 5100, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5103, '发票更新', 'erp:finance-record:update', 3, 3, 5100, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5104, '发票删除', 'erp:finance-record:delete', 3, 4, 5100, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5105, '发票导出', 'erp:finance-record:export', 3, 5, 5100, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5106, '发票审核', 'erp:finance-record:update-status', 3, 6, 5100, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5110, '报销管理', '', 2, 4, 2645, 'reimbursement', 'ep:wallet', 'erp/finance/reimbursement/index', 'ErpFinanceReimbursement', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5111, '报销查询', 'erp:finance-record:query', 3, 1, 5110, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5112, '报销创建', 'erp:finance-record:create', 3, 2, 5110, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5113, '报销更新', 'erp:finance-record:update', 3, 3, 5110, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5114, '报销删除', 'erp:finance-record:delete', 3, 4, 5110, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5115, '报销导出', 'erp:finance-record:export', 3, 5, 5110, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5116, '报销审批', 'erp:finance-record:update-status', 3, 6, 5110, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5120, '退款管理', '', 2, 5, 2645, 'refund', 'ep:refresh-left', 'erp/finance/refund/index', 'ErpFinanceRefund', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5121, '退款查询', 'erp:finance-record:query', 3, 1, 5120, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5122, '退款创建', 'erp:finance-record:create', 3, 2, 5120, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5123, '退款更新', 'erp:finance-record:update', 3, 3, 5120, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5124, '退款删除', 'erp:finance-record:delete', 3, 4, 5120, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5125, '退款导出', 'erp:finance-record:export', 3, 5, 5120, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5126, '退款审批', 'erp:finance-record:update-status', 3, 6, 5120, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5130, '费用管理', '', 2, 6, 2645, 'expense', 'ep:coin', 'erp/finance/expense/index', 'ErpFinanceExpense', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5131, '费用查询', 'erp:finance-record:query', 3, 1, 5130, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5132, '费用创建', 'erp:finance-record:create', 3, 2, 5130, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5133, '费用更新', 'erp:finance-record:update', 3, 3, 5130, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5134, '费用删除', 'erp:finance-record:delete', 3, 4, 5130, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5135, '费用导出', 'erp:finance-record:export', 3, 5, 5130, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5136, '费用审核', 'erp:finance-record:update-status', 3, 6, 5130, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(5140, '财务分析', 'erp:finance-record:query', 2, 7, 2645, 'analysis', 'ep:data-analysis', 'erp/finance/analysis/index', 'ErpFinanceAnalysis', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');

INSERT INTO `system_menu_i18n` (`menu_id`, `language`, `name`) VALUES
(5100, 'zh-CN', '发票管理'), (5100, 'en', 'Invoices'),
(5101, 'zh-CN', '发票查询'), (5101, 'en', 'Query'),
(5102, 'zh-CN', '发票创建'), (5102, 'en', 'Create'),
(5103, 'zh-CN', '发票更新'), (5103, 'en', 'Update'),
(5104, 'zh-CN', '发票删除'), (5104, 'en', 'Delete'),
(5105, 'zh-CN', '发票导出'), (5105, 'en', 'Export'),
(5106, 'zh-CN', '发票审核'), (5106, 'en', 'Approve'),
(5110, 'zh-CN', '报销管理'), (5110, 'en', 'Reimbursements'),
(5111, 'zh-CN', '报销查询'), (5111, 'en', 'Query'),
(5112, 'zh-CN', '报销创建'), (5112, 'en', 'Create'),
(5113, 'zh-CN', '报销更新'), (5113, 'en', 'Update'),
(5114, 'zh-CN', '报销删除'), (5114, 'en', 'Delete'),
(5115, 'zh-CN', '报销导出'), (5115, 'en', 'Export'),
(5116, 'zh-CN', '报销审批'), (5116, 'en', 'Approve'),
(5120, 'zh-CN', '退款管理'), (5120, 'en', 'Refunds'),
(5121, 'zh-CN', '退款查询'), (5121, 'en', 'Query'),
(5122, 'zh-CN', '退款创建'), (5122, 'en', 'Create'),
(5123, 'zh-CN', '退款更新'), (5123, 'en', 'Update'),
(5124, 'zh-CN', '退款删除'), (5124, 'en', 'Delete'),
(5125, 'zh-CN', '退款导出'), (5125, 'en', 'Export'),
(5126, 'zh-CN', '退款审批'), (5126, 'en', 'Approve'),
(5130, 'zh-CN', '费用管理'), (5130, 'en', 'Expenses'),
(5131, 'zh-CN', '费用查询'), (5131, 'en', 'Query'),
(5132, 'zh-CN', '费用创建'), (5132, 'en', 'Create'),
(5133, 'zh-CN', '费用更新'), (5133, 'en', 'Update'),
(5134, 'zh-CN', '费用删除'), (5134, 'en', 'Delete'),
(5135, 'zh-CN', '费用导出'), (5135, 'en', 'Export'),
(5136, 'zh-CN', '费用审核'), (5136, 'en', 'Approve'),
(5140, 'zh-CN', '财务分析'), (5140, 'en', 'Finance Analysis');

INSERT INTO `infra_job` (`id`, `name`, `status`, `handler_name`, `handler_param`, `cron_expression`, `retry_count`, `retry_interval`, `monitor_timeout`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (510041, 'ERP 财务逾期检测 Job', 1, 'erpFinanceOverdueCheckJob', '', '0 0 1 * * ?', 0, 0, 0, '1', NOW(), '1', NOW(), b'0');

INSERT INTO `infra_job` (`id`, `name`, `status`, `handler_name`, `handler_param`, `cron_expression`, `retry_count`, `retry_interval`, `monitor_timeout`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (510042, 'CRM 回款逾期检测 Job', 1, 'crmReceivablePlanOverdueCheckJob', '', '0 5 1 * * ?', 0, 0, 0, '1', NOW(), '1', NOW(), b'0');
