-- 工单域菜单和权限：使用未占用的 6800 段 ID，不删除、不覆盖其他域菜单。
-- 依赖：CRM 父菜单（id=2397）已由 crm 基础 SQL 创建。

INSERT IGNORE INTO `system_menu`
(`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(6800, '工单管理', '', 2, 90, 2397, 'work-order', 'ep:ticket', 'crm/work-order/index', 'CrmWorkOrder', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(6801, '工单查询', 'crm:work-order:query', 3, 1, 6800, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(6802, '工单新增', 'crm:work-order:create', 3, 2, 6800, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(6803, '工单修改', 'crm:work-order:update', 3, 3, 6800, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(6804, '工单删除', 'crm:work-order:delete', 3, 4, 6800, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(6805, '工单导出', 'crm:work-order:export', 3, 5, 6800, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0');

-- 已拥有 CRM 主菜单的角色自动获得工单菜单权限。
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `updater`, `deleted`, `tenant_id`)
SELECT parent_permission.`role_id`, work_order_menu.`id`, '1', '1', b'0', parent_permission.`tenant_id`
FROM `system_role_menu` parent_permission
JOIN `system_menu` work_order_menu ON work_order_menu.`id` BETWEEN 6800 AND 6805
    AND work_order_menu.`deleted` = b'0'
WHERE parent_permission.`menu_id` = 2397 AND parent_permission.`deleted` = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `system_role_menu` existing_permission
    WHERE existing_permission.`role_id` = parent_permission.`role_id`
      AND existing_permission.`menu_id` = work_order_menu.`id`
      AND existing_permission.`tenant_id` = parent_permission.`tenant_id`
      AND existing_permission.`deleted` = b'0'
  );
