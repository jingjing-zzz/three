-- 清理旧版预测菜单脚本误生成的重复菜单（可重复执行）
-- 保留最早创建的 crm/statistics/forecast/index 菜单，其余重复项逻辑删除。
SET @keep_menu_id := (
  SELECT id FROM system_menu
  WHERE component = 'crm/statistics/forecast/index' AND deleted = b'0'
  ORDER BY id
  LIMIT 1
);

START TRANSACTION;

UPDATE system_role_menu rm
JOIN system_menu m ON m.id = rm.menu_id
SET rm.deleted = b'1'
WHERE m.component = 'crm/statistics/forecast/index'
  AND m.id <> @keep_menu_id
  AND m.deleted = b'0'
  AND rm.deleted = b'0';

UPDATE system_menu_i18n i
JOIN system_menu m ON m.id = i.menu_id
SET i.deleted = b'1'
WHERE m.component = 'crm/statistics/forecast/index'
  AND m.id <> @keep_menu_id
  AND m.deleted = b'0'
  AND i.deleted = b'0';

UPDATE system_menu
SET deleted = b'1'
WHERE component = 'crm/statistics/forecast/index'
  AND id <> @keep_menu_id
  AND deleted = b'0';

COMMIT;

SELECT id, name, path, component, deleted
FROM system_menu
WHERE component = 'crm/statistics/forecast/index'
ORDER BY id;
