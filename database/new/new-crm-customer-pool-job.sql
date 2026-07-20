-- =============================================
-- 客户公海定时任务
-- 自动将长时间未跟进的客户回收到公海池
-- Job 处理器：com.meession.etm.module.crm.job.customer.CrmCustomerAutoPutPoolJob
-- 业务方法：CrmCustomerServiceImpl.autoPutCustomerPool()
-- 调度规则：每天凌晨 1:10 执行一次（避开 1:00 的 ERP 财务逾期检查和 1:05 的 CRM 回款逾期检查）
-- =============================================

INSERT INTO `infra_job` (`name`, `status`, `handler_name`, `handler_param`, `cron_expression`, `retry_count`, `retry_interval`, `monitor_timeout`, `creator`, `create_time`, `updater`, `update_time`)
VALUES ('CRM 客户公海回收 Job', 1, 'crmCustomerAutoPutPoolJob', NULL, '0 10 1 * * ?', 0, 0, 0, 'admin', NOW(), 'admin', NOW())
ON DUPLICATE KEY UPDATE `name` = 'CRM 客户公海回收 Job', `handler_name` = 'crmCustomerAutoPutPoolJob', `cron_expression` = '0 10 1 * * ?';
