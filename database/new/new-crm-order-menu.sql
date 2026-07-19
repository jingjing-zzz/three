-- CRM 订单菜单：使用 6300 段编号，避开商机统计（6004、6005）与 OA 菜单编号。
-- 仅插入当前不存在的菜单和授权，不覆盖其他域既有菜单或角色权限。
SET NAMES utf8mb4;

INSERT IGNORE INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
(6300, '订单管理', '', 2, 60, 2397, 'order', 'ep:shopping-cart-full', 'crm/order/index', 'CrmOrder', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(6301, '订单查询', 'crm:order:query', 3, 1, 6300, '', '', '', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(6302, '订单创建', 'crm:order:create', 3, 2, 6300, '', '', '', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(6303, '订单更新', 'crm:order:update', 3, 3, 6300, '', '', '', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(6304, '订单删除', 'crm:order:delete', 3, 4, 6300, '', '', '', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(6305, '订单导出', 'crm:order:export', 3, 5, 6300, '', '', '', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(6306, '订单审批', 'crm:order:approval', 3, 6, 6300, '', '', '', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(6307, '订单报表', 'crm:order:query', 2, 61, 2397, 'order/report', 'ep:data-line', 'crm/order/report/index', 'CrmOrderReport', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');

-- 已拥有 CRM 主菜单的角色，自动增加订单菜单权限；重复执行不产生重复授权。
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT parent_permission.`role_id`, order_menu.`id`, '1', NOW(), '1', NOW(), b'0', parent_permission.`tenant_id`
FROM `system_role_menu` parent_permission
JOIN `system_menu` order_menu ON order_menu.`id` BETWEEN 6300 AND 6307 AND order_menu.`deleted` = b'0'
WHERE parent_permission.`menu_id` = 2397
  AND parent_permission.`deleted` = b'0'
  AND NOT EXISTS (
      SELECT 1 FROM `system_role_menu` existing_permission
      WHERE existing_permission.`role_id` = parent_permission.`role_id`
        AND existing_permission.`menu_id` = order_menu.`id`
        AND existing_permission.`tenant_id` = parent_permission.`tenant_id`
        AND existing_permission.`deleted` = b'0'
  );
