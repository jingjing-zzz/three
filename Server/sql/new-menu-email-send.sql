-- ========================================
-- 新增"邮件发送"子菜单（营销管理下）
-- 幂等：使用 ON DUPLICATE KEY UPDATE，可重复执行
-- ========================================

-- 1. 新增"邮件发送"菜单（type=2 菜单）
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, updater)
VALUES (
    8020,
    '邮件发送',
    '',
    2,                  -- 菜单类型
    4,                  -- 排序（在"邮件群发批次"sort=3 之后）
    5080,               -- 父菜单：营销管理
    'email-send',       -- 路由路径
    '',                 -- 图标（无）
    'crm/marketing/emailSend/index',  -- 组件路径
    'MarketingEmailSend',             -- 组件名
    0,                  -- 状态：0=开启
    b'1',               -- 可见
    b'1',               -- 缓存
    b'1',               -- 总是显示
    '1',
    '1'
)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    permission = VALUES(permission),
    type = VALUES(type),
    sort = VALUES(sort),
    parent_id = VALUES(parent_id),
    path = VALUES(path),
    icon = VALUES(icon),
    component = VALUES(component),
    component_name = VALUES(component_name),
    status = VALUES(status),
    visible = VALUES(visible),
    keep_alive = VALUES(keep_alive),
    always_show = VALUES(always_show),
    updater = VALUES(updater),
    deleted = b'0';

-- 2. 新增"邮件发送-发送"按钮权限（type=3 按钮）
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, updater)
VALUES (
    8021,
    '邮件发送-发送',
    'crm:marketing:email-send:send',  -- 权限标识
    3,                  -- 按钮类型
    1,                  -- 排序
    8020,               -- 父菜单：邮件发送
    '',
    '#',
    NULL,
    NULL,
    0,
    b'1',
    b'1',
    b'1',
    '1',
    '1'
)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    permission = VALUES(permission),
    type = VALUES(type),
    sort = VALUES(sort),
    parent_id = VALUES(parent_id),
    updater = VALUES(updater),
    deleted = b'0';

-- 3. 验证插入结果
SELECT id, name, permission, type, sort, parent_id, path, component, component_name, status, visible, keep_alive, always_show, deleted
FROM system_menu WHERE id IN (8020, 8021) ORDER BY id;
