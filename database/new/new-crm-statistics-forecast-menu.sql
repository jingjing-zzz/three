-- CRM 销售预测菜单增量脚本（可重复执行）
SET NAMES utf8mb4;
START TRANSACTION;
SET @parent_id := (SELECT parent_id FROM system_menu WHERE permission='crm:statistics-funnel:query' AND deleted=b'0' LIMIT 1);

INSERT INTO system_menu (name,permission,type,sort,parent_id,path,icon,component,component_name,status,visible,keep_alive,always_show,creator,updater,deleted)
SELECT '销售预测','crm:statistics-forecast:query',2,6,@parent_id,'forecast','ep:trend-charts','crm/statistics/forecast/index','CrmStatisticsForecast',0,b'1',b'1',b'1','1','1',b'0'
FROM DUAL WHERE @parent_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM system_menu WHERE component='crm/statistics/forecast/index' AND deleted=b'0');

UPDATE system_menu SET name='销售预测',type=2,sort=6,parent_id=@parent_id,path='forecast',icon='ep:trend-charts',component='crm/statistics/forecast/index',component_name='CrmStatisticsForecast',status=0,visible=b'1',keep_alive=b'1',always_show=b'1',updater='1',deleted=b'0'
WHERE component='crm/statistics/forecast/index' AND @parent_id IS NOT NULL;

SET @menu_id := (SELECT id FROM system_menu WHERE component='crm/statistics/forecast/index' AND deleted=b'0' LIMIT 1);
INSERT INTO system_menu_i18n (menu_id,language,name,creator,updater,deleted)
SELECT @menu_id,'zh-CN','销售预测','1','1',b'0' FROM DUAL WHERE @menu_id IS NOT NULL
ON DUPLICATE KEY UPDATE name=VALUES(name),updater=VALUES(updater),deleted=b'0';
INSERT INTO system_menu_i18n (menu_id,language,name,creator,updater,deleted)
SELECT @menu_id,'en','Sales Forecast','1','1',b'0' FROM DUAL WHERE @menu_id IS NOT NULL
ON DUPLICATE KEY UPDATE name=VALUES(name),updater=VALUES(updater),deleted=b'0';

INSERT INTO system_role_menu (role_id,menu_id,creator,updater,deleted,tenant_id)
SELECT r.id,@menu_id,'1','1',b'0',r.tenant_id FROM system_role r
WHERE r.code IN ('super_admin','crm_admin') AND r.deleted=b'0' AND @menu_id IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM system_role_menu rm WHERE rm.role_id=r.id AND rm.menu_id=@menu_id AND rm.deleted=b'0');
COMMIT;

SELECT id,parent_id,path,component,component_name,permission FROM system_menu WHERE component='crm/statistics/forecast/index';
SELECT language,name FROM system_menu_i18n WHERE menu_id=@menu_id ORDER BY language;
SELECT r.code FROM system_role_menu rm JOIN system_role r ON r.id=rm.role_id WHERE rm.menu_id=@menu_id AND rm.deleted=b'0' ORDER BY r.code;
