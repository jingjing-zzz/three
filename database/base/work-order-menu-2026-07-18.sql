-- 工单管理菜单
-- 类型说明：1-目录, 2-菜单, 3-按钮
-- CRM系统父菜单ID: 2397

-- 删除之前错误的菜单（包含工单统计独立菜单，统计已改为工单管理页内的按钮）
DELETE FROM `system_menu` WHERE `id` IN (2770, 2771, 2772, 2773, 2774, 2775, 2776, 2777);

-- 工单管理菜单（CRM系统下）
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (2770, '工单管理', '', 2, 90, 2397, 'work-order', 'ep:ticket', 'crm/work-order/index', 'CrmWorkOrder', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0');

-- 工单管理按钮权限
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (2771, '工单查询', 'crm:work-order:query', 3, 1, 2770, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (2772, '工单新增', 'crm:work-order:create', 3, 2, 2770, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (2773, '工单修改', 'crm:work-order:update', 3, 3, 2770, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (2774, '工单删除', 'crm:work-order:delete', 3, 4, 2770, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0');

-- 工单导出按钮权限（工单管理页内按钮）
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show