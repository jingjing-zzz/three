-- =====================================================
-- 编号: 20260718-01
-- 用途: 新增 CRM 商机综合报表菜单
-- 依赖版本: 依赖 new-crm-statistics-forecast-menu.sql 已执行（共用同一父菜单和权限）
-- 执行顺序: 在 new-crm-statistics-forecast-menu.sql 之后
-- 是否可重复执行: 是
-- 影响对象: system_menu, system_menu_i18n, system_role_menu
-- 人工回滚:
--   DELETE FROM system_role_menu WHERE menu_id IN (SELECT id FROM system_menu WHERE permission='crm:statistics-forecast:query' AND component='crm/statistics/report/index');
--   DELETE FROM system_menu_i18n WHERE menu_id IN (SELECT id FROM system_menu WHERE permission='crm:statistics-forecast:query' AND component='crm/statistics/report/index');
--   DELETE FROM system_menu WHERE permission='crm:statistics-forecast:query' AND component='crm/statistics/report/index';
-- 说明: 报表页与销售预测共用 crm:statistics-forecast:query 权限，避免新增权限带来角色分配复杂度。
--       通过 component 路径区分，保证不会和预测页菜单冲突。
-- =====================================================
SET NAMES utf8mb4;
START TRANSACTION;
SET @parent_id := (SELECT parent_id FROM system_menu WHERE permission='crm:statistics-forecast:query' AND component='crm/statistics/forecast/index' AND deleted=b'0' LIMIT 1);

INSERT INTO system_menu (name,permission,type,sort,parent_id,path,icon,component,component_name,status,visible,keep_alive,always_show,creator,updater,deleted)
SELECT '商机综合报表','crm:statistics-forecast:query',2,7,@parent_id,'report','ep:data-board','crm/statistics/report/index','CrmStatisticsReport',0,b'1',b'1',b'1','1','1',b'0'
FROM DUAL WHERE @parent_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM system_menu WHERE component='crm/statistics/report/index' AND deleted=b'0');

UPDATE system_menu SET name='商机综合报表',type=2,sort=7,parent_id=@parent_id,path='report',icon='ep:data-board',component='crm/statistics/report/index',component_name='CrmStatisticsReport',status=0,visible=b'1',keep_alive=b'1',always_show=b'1',updater='1',deleted=b'0'
WHERE component='crm/statistics/report/index' AND @parent_id IS NOT NULL AND deleted=b'0';

SET @menu_id := (SELECT id FROM system_menu WHERE component='crm/statistics/report/index' AND deleted=b'0' LIMIT 1);

INSERT INTO system_menu_i18n (menu_id,language,name,creator,updater,deleted)
SELECT @menu_id,'zh-CN','商机综合报表','1','1',b'0' FROM DUAL WHERE @menu_id IS NOT NULL
ON DUPLICATE KEY UPDATE name=VALUES(name),updater=VALUES(updater),deleted=b'0';
INSERT INTO system_menu_i18n (menu_id,language,name,creator,updater,deleted)
SELECT @menu_id,'en','Business Report','1','1',b'0' FROM DUAL WHERE @menu_id IS NOT NULL
ON DUPLICATE KEY UPDATE name=VALUES(name),updater=VALUES(updater),deleted=b'0';

INSERT INTO system_role_menu (role_id,menu_id,creator,updater,deleted,tenant_id)
SELECT r.id,@menu_id,'1','1',b'0',r.tenant_id FROM system_role r
WHERE r.code IN ('super_admin','crm_admin') AND r.deleted=b'0' AND @menu_id IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM system_role_menu rm WHERE rm.role_id=r.id AND rm.menu_id=@menu_id AND rm.deleted=b'0');
COMMIT;

SELECT id,parent_id,path,component,component_name,permission FROM system_menu WHERE component='crm/statistics/report/index';
SELECT language,name FROM system_menu_i18n WHERE menu_id=@menu_id ORDER BY language;
SELECT r.code FROM system_role_menu rm JOIN system_role r ON r.id=rm.role_id WHERE rm.menu_id=@menu_id AND rm.deleted=b'0' ORDER BY r.code;
