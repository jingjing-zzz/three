-- ============================================================
-- CRM 客户自动掉入公海定时任务
-- 说明：客户未跟进/未成交超过配置天数后，自动放入公海
-- cron: 每10分钟执行一次
-- ============================================================
INSERT INTO `infra_job` (`id`, `name`, `status`, `handler_name`, `handler_param`, `cron_expression`, `retry_count`, `retry_interval`, `monitor_timeout`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (42, '客户自动掉入公海 Job', 1, 'crmCustomerAutoPutPoolJob', '', '0 0/10 * * * ?', 3, 0, 0, '1', NOW(), '1', NOW(), b'0');
