-- =============================================
-- 营销管理菜单结构
-- 二级菜单：营销管理 (parent_id=2397 CRM系统)
-- 三级菜单：营销活动、短信群发批次、邮件群发批次、客户关怀规则、群发审批、发送记录
-- =============================================

-- 营销管理（二级目录菜单）
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5080, '营销管理', '', 1, 69, 2397, 'marketing', 'ep:promotion', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `name`='营销管理', `parent_id`=2397, `type`=1, `path`='marketing', `icon`='ep:promotion';

-- =============================================
-- 三级菜单：营销活动
-- =============================================
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5047, '营销活动', '', 2, 1, 5080, 'campaign', '', 'crm/marketing/campaign/index', 'MarketingCampaign', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `parent_id`=5080, `sort`=1, `path`='campaign';
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5048, '营销活动查询', 'crm:marketing:campaign:query', 3, 1, 5047, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `permission`='crm:marketing:campaign:query';
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5049, '营销活动创建', 'crm:marketing:campaign:create', 3, 2, 5047, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `permission`='crm:marketing:campaign:create';
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5050, '营销活动更新', 'crm:marketing:campaign:update', 3, 3, 5047, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `permission`='crm:marketing:campaign:update';
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5051, '营销活动删除', 'crm:marketing:campaign:delete', 3, 4, 5047, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `permission`='crm:marketing:campaign:delete';

-- =============================================
-- 三级菜单：短信群发批次
-- =============================================
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5052, '短信群发批次', '', 2, 2, 5080, 'sms-batch', '', 'crm/marketing/smsBatch/index', 'MarketingSmsBatch', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `parent_id`=5080, `sort`=2, `path`='sms-batch', `icon`='';
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5053, '短信群发批次查询', 'crm:marketing:sms-batch:query', 3, 1, 5052, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `permission`='crm:marketing:sms-batch:query';
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5054, '短信群发批次创建', 'crm:marketing:sms-batch:create', 3, 2, 5052, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `permission`='crm:marketing:sms-batch:create';
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5055, '短信群发批次更新', 'crm:marketing:sms-batch:update', 3, 3, 5052, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `permission`='crm:marketing:sms-batch:update';
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5056, '短信群发批次删除', 'crm:marketing:sms-batch:delete', 3, 4, 5052, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `permission`='crm:marketing:sms-batch:delete';

-- =============================================
-- 三级菜单：邮件群发批次
-- =============================================
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5057, '邮件群发批次', '', 2, 3, 5080, 'email-batch', '', 'crm/marketing/emailBatch/index', 'MarketingEmailBatch', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `parent_id`=5080, `sort`=3, `path`='email-batch';
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5058, '邮件群发批次查询', 'crm:marketing:email-batch:query', 3, 1, 5057, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `permission`='crm:marketing:email-batch:query';
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5059, '邮件群发批次创建', 'crm:marketing:email-batch:create', 3, 2, 5057, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `permission`='crm:marketing:email-batch:create';
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5060, '邮件群发批次更新', 'crm:marketing:email-batch:update', 3, 3, 5057, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `permission`='crm:marketing:email-batch:update';
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5061, '邮件群发批次删除', 'crm:marketing:email-batch:delete', 3, 4, 5057, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `permission`='crm:marketing:email-batch:delete';

-- =============================================
-- 三级菜单：客户关怀规则
-- =============================================
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5062, '客户关怀规则', '', 2, 4, 5080, 'customer-care', '', 'crm/marketing/customerCare/index', 'MarketingCustomerCare', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `parent_id`=5080, `sort`=4, `path`='customer-care', `icon`='';
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5063, '客户关怀规则查询', 'crm:marketing:customer-care:query', 3, 1, 5062, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `permission`='crm:marketing:customer-care:query';
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5064, '客户关怀规则创建', 'crm:marketing:customer-care:create', 3, 2, 5062, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `permission`='crm:marketing:customer-care:create';
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5065, '客户关怀规则更新', 'crm:marketing:customer-care:update', 3, 3, 5062, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `permission`='crm:marketing:customer-care:update';
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5066, '客户关怀规则删除', 'crm:marketing:customer-care:delete', 3, 4, 5062, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `permission`='crm:marketing:customer-care:delete';

-- =============================================
-- 三级菜单：群发审批
-- =============================================
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5067, '群发审批', '', 2, 5, 5080, 'approval', '', 'crm/marketing/approval/index', 'MarketingApproval', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `parent_id`=5080, `sort`=5, `path`='approval', `icon`='';
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5068, '群发审批查询', 'crm:marketing:approval:query', 3, 1, 5067, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `permission`='crm:marketing:approval:query';
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5069, '群发审批通过', 'crm:marketing:approval:approve', 3, 2, 5067, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `permission`='crm:marketing:approval:approve';
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5070, '群发审批拒绝', 'crm:marketing:approval:approve', 3, 3, 5067, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `permission`='crm:marketing:approval:approve';
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5071, '群发审批删除', 'crm:marketing:approval:delete', 3, 4, 5067, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `permission`='crm:marketing:approval:delete';

-- =============================================
-- 三级菜单：发送记录
-- =============================================
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5072, '发送记录', '', 2, 6, 5080, 'send-record', '', 'crm/marketing/sendRecord/index', 'MarketingSendRecord', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `parent_id`=5080, `sort`=6, `path`='send-record', `icon`='';
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (5073, '发送记录查询', 'crm:marketing:send-record:query', 3, 1, 5072, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `permission`='crm:marketing:send-record:query';

-- =============================================
-- 为管理员角色(role_id=1)分配营销菜单权限
-- =============================================
INSERT INTO `system_role_menu` (`role_id`, `menu_id`) VALUES
(1, 5080), (1, 5047), (1, 5048), (1, 5049), (1, 5050), (1, 5051),
(1, 5052), (1, 5053), (1, 5054), (1, 5055), (1, 5056),
(1, 5057), (1, 5058), (1, 5059), (1, 5060), (1, 5061),
(1, 5062), (1, 5063), (1, 5064), (1, 5065), (1, 5066),
(1, 5067), (1, 5068), (1, 5069), (1, 5070), (1, 5071),
(1, 5072), (1, 5073)
ON DUPLICATE KEY UPDATE `role_id`=`role_id`;
