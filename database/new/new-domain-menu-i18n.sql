-- 新增业务域动态菜单国际化。
-- 前端语言包只负责静态页面文本；登录后由后端返回的菜单名称必须写入 system_menu_i18n。
-- 覆盖本次整合的财务、OA、订单、营销、工单菜单；重复执行会更新本域翻译，不影响其他菜单。
SET NAMES utf8mb4;

INSERT INTO system_menu_i18n (menu_id, language, name, creator, updater, deleted)
VALUES
-- 财务域
(5100, 'zh-CN', '发票管理', '1', '1', b'0'), (5100, 'en', 'Invoice Management', '1', '1', b'0'),
(5110, 'zh-CN', '报销管理', '1', '1', b'0'), (5110, 'en', 'Reimbursement Management', '1', '1', b'0'),
(5120, 'zh-CN', '退款管理', '1', '1', b'0'), (5120, 'en', 'Refund Management', '1', '1', b'0'),
(5130, 'zh-CN', '费用管理', '1', '1', b'0'), (5130, 'en', 'Expense Management', '1', '1', b'0'),
(5140, 'zh-CN', '财务分析', '1', '1', b'0'), (5140, 'en', 'Financial Analysis', '1', '1', b'0'),
-- OA 域
(6200, 'zh-CN', 'OA管理', '1', '1', b'0'), (6200, 'en', 'OA Management', '1', '1', b'0'),
(6201, 'zh-CN', '出差申请', '1', '1', b'0'), (6201, 'en', 'Business Trip', '1', '1', b'0'),
(6204, 'zh-CN', '借款申请', '1', '1', b'0'), (6204, 'en', 'Loan Application', '1', '1', b'0'),
(6207, 'zh-CN', '客户拜访', '1', '1', b'0'), (6207, 'en', 'Customer Visit', '1', '1', b'0'),
(6210, 'zh-CN', '工作报告', '1', '1', b'0'), (6210, 'en', 'Work Report', '1', '1', b'0'),
(6213, 'zh-CN', '任务管理', '1', '1', b'0'), (6213, 'en', 'Task Management', '1', '1', b'0'),
(6218, 'zh-CN', '日程管理', '1', '1', b'0'), (6218, 'en', 'Schedule Management', '1', '1', b'0'),
(6223, 'zh-CN', '请示审批', '1', '1', b'0'), (6223, 'en', 'Work Request', '1', '1', b'0'),
-- 订单域
(6300, 'zh-CN', '订单管理', '1', '1', b'0'), (6300, 'en', 'Order Management', '1', '1', b'0'),
(6307, 'zh-CN', '订单报表', '1', '1', b'0'), (6307, 'en', 'Order Report', '1', '1', b'0'),
-- 营销域
(5080, 'zh-CN', '营销管理', '1', '1', b'0'), (5080, 'en', 'Marketing Management', '1', '1', b'0'),
(5047, 'zh-CN', '营销活动', '1', '1', b'0'), (5047, 'en', 'Marketing Campaigns', '1', '1', b'0'),
(5052, 'zh-CN', '短信群发批次', '1', '1', b'0'), (5052, 'en', 'SMS Batches', '1', '1', b'0'),
(5057, 'zh-CN', '邮件群发批次', '1', '1', b'0'), (5057, 'en', 'Email Batches', '1', '1', b'0'),
(5062, 'zh-CN', '客户关怀规则', '1', '1', b'0'), (5062, 'en', 'Customer Care Rules', '1', '1', b'0'),
(5067, 'zh-CN', '群发审批', '1', '1', b'0'), (5067, 'en', 'Mass Send Approval', '1', '1', b'0'),
(5072, 'zh-CN', '发送记录', '1', '1', b'0'), (5072, 'en', 'Send Records', '1', '1', b'0'),
-- 工单域
(6800, 'zh-CN', '工单管理', '1', '1', b'0'), (6800, 'en', 'Work Order Management', '1', '1', b'0')
ON DUPLICATE KEY UPDATE name = VALUES(name), updater = VALUES(updater), update_time = NOW(), deleted = b'0';
