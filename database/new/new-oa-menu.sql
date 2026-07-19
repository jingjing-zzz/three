-- OA 菜单：使用 6200 段编号，避开 CRM 商机统计和订单菜单编号。
-- INSERT IGNORE 保证既有菜单不会被覆盖；授权仅补充不存在的记录。
SET NAMES utf8mb4;

INSERT IGNORE INTO `system_menu` (`id`,`name`,`permission`,`type`,`sort`,`parent_id`,`path`,`icon`,`component`,`component_name`,`status`,`visible`,`keep_alive`,`always_show`,`creator`,`updater`,`deleted`) VALUES
(6200,'OA管理','',1,30,1185,'oa','fa:building-o',NULL,NULL,0,b'1',b'1',b'1','1','1',b'0'),
(6201,'出差申请','',2,1,6200,'business-trip','fa:plane','bpm/oa/businessTrip/index','BpmOABusinessTrip',0,b'1',b'1',b'1','1','1',b'0'),
(6202,'出差申请查询','bpm:oa-business-trip:query',3,1,6201,'','','',NULL,0,b'1',b'1',b'1','1','1',b'0'),
(6203,'出差申请创建','bpm:oa-business-trip:create',3,2,6201,'','','',NULL,0,b'1',b'1',b'1','1','1',b'0'),
(6204,'借款申请','',2,2,6200,'loan','fa:credit-card','bpm/oa/loan/index','BpmOALoan',0,b'1',b'1',b'1','1','1',b'0'),
(6205,'借款申请查询','bpm:oa-loan:query',3,1,6204,'','','',NULL,0,b'1',b'1',b'1','1','1',b'0'),
(6206,'借款申请创建','bpm:oa-loan:create',3,2,6204,'','','',NULL,0,b'1',b'1',b'1','1','1',b'0'),
(6207,'客户拜访','',2,3,6200,'customer-visit','fa:users','bpm/oa/customerVisit/index','BpmOACustomerVisit',0,b'1',b'1',b'1','1','1',b'0'),
(6208,'客户拜访查询','bpm:oa-customer-visit:query',3,1,6207,'','','',NULL,0,b'1',b'1',b'1','1','1',b'0'),
(6209,'客户拜访创建','bpm:oa-customer-visit:create',3,2,6207,'','','',NULL,0,b'1',b'1',b'1','1','1',b'0'),
(6210,'工作报告','',2,4,6200,'work-report','fa:file-text','bpm/oa/workReport/index','BpmOAWorkReport',0,b'1',b'1',b'1','1','1',b'0'),
(6211,'工作报告查询','bpm:oa-work-report:query',3,1,6210,'','','',NULL,0,b'1',b'1',b'1','1','1',b'0'),
(6212,'工作报告创建','bpm:oa-work-report:create',3,2,6210,'','','',NULL,0,b'1',b'1',b'1','1','1',b'0'),
(6213,'任务管理','',2,5,6200,'task','fa:tasks','bpm/oa/task/index','BpmOATask',0,b'1',b'1',b'1','1','1',b'0'),
(6214,'任务管理查询','bpm:oa-task:query',3,1,6213,'','','',NULL,0,b'1',b'1',b'1','1','1',b'0'),
(6215,'任务管理创建','bpm:oa-task:create',3,2,6213,'','','',NULL,0,b'1',b'1',b'1','1','1',b'0'),
(6216,'任务管理更新','bpm:oa-task:update',3,3,6213,'','','',NULL,0,b'1',b'1',b'1','1','1',b'0'),
(6217,'任务管理删除','bpm:oa-task:delete',3,4,6213,'','','',NULL,0,b'1',b'1',b'1','1','1',b'0'),
(6218,'日程管理','',2,6,6200,'schedule','fa:calendar','bpm/oa/schedule/index','BpmOASchedule',0,b'1',b'1',b'1','1','1',b'0'),
(6219,'日程管理查询','bpm:oa-schedule:query',3,1,6218,'','','',NULL,0,b'1',b'1',b'1','1','1',b'0'),
(6220,'日程管理创建','bpm:oa-schedule:create',3,2,6218,'','','',NULL,0,b'1',b'1',b'1','1','1',b'0'),
(6221,'日程管理更新','bpm:oa-schedule:update',3,3,6218,'','','',NULL,0,b'1',b'1',b'1','1','1',b'0'),
(6222,'日程管理删除','bpm:oa-schedule:delete',3,4,6218,'','','',NULL,0,b'1',b'1',b'1','1','1',b'0'),
(6223,'请示审批','',2,7,6200,'work-request','fa:question-circle','bpm/oa/workRequest/index','BpmOAWorkRequest',0,b'1',b'1',b'1','1','1',b'0'),
(6224,'请示审批查询','bpm:oa-work-request:query',3,1,6223,'','','',NULL,0,b'1',b'1',b'1','1','1',b'0'),
(6225,'请示审批创建','bpm:oa-work-request:create',3,2,6223,'','','',NULL,0,b'1',b'1',b'1','1','1',b'0');

INSERT INTO `system_role_menu` (`role_id`,`menu_id`,`creator`,`updater`,`deleted`,`tenant_id`)
SELECT parent_permission.`role_id`, oa_menu.`id`, '1', '1', b'0', parent_permission.`tenant_id`
FROM `system_role_menu` parent_permission
JOIN `system_menu` oa_menu ON oa_menu.`id` BETWEEN 6200 AND 6225 AND oa_menu.`deleted` = b'0'
WHERE parent_permission.`menu_id` = 1185 AND parent_permission.`deleted` = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `system_role_menu` existing_permission
    WHERE existing_permission.`role_id` = parent_permission.`role_id`
      AND existing_permission.`menu_id` = oa_menu.`id`
      AND existing_permission.`tenant_id` = parent_permission.`tenant_id`
      AND existing_permission.`deleted` = b'0'
  );
