-- 营销域演示数据：使用 900000 段固定编号，重复执行不会删除或覆盖已有数据。
-- 依赖：new-marketing-domain.sql 已先执行。
SET NAMES utf8mb4;

INSERT IGNORE INTO `marketing_campaign`
(`id`,`tenant_id`,`name`,`type`,`status`,`start_time`,`end_time`,`target_filter`,`description`,`total_target_count`,`send_count`,`success_count`,`fail_count`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
VALUES
(900001,1,'2026 春季客户回馈活动',1,2,'2026-03-01 00:00:00','2026-03-31 23:59:59','{"tag":"vip"}','面向 VIP 客户的短信回馈活动',500,500,486,14,'1','2026-03-01 09:00:00','1','2026-03-31 18:00:00',b'0'),
(900002,1,'2026 新品邮件推广',2,1,'2026-07-01 00:00:00','2026-07-31 23:59:59','{"region":"华东"}','面向华东客户的新品推广活动',800,320,308,12,'1','2026-07-01 10:00:00','1','2026-07-19 10:00:00',b'0'),
(900003,1,'客户生日关怀活动',3,0,'2026-08-01 00:00:00','2026-08-31 23:59:59','{"birthday":"month"}','客户生日关怀活动草稿',300,0,0,0,'1','2026-07-15 09:00:00','1','2026-07-15 09:00:00',b'0');

INSERT IGNORE INTO `marketing_sms_batch`
(`id`,`tenant_id`,`campaign_id`,`campaign_name`,`template_id`,`template_name`,`phone_list`,`content`,`status`,`send_time`,`total_count`,`send_count`,`success_count`,`fail_count`,`send_rate`,`remark`,`error_message`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
VALUES
(900011,1,900001,'2026 春季客户回馈活动',101,'春季回馈短信模板','["13800138001","13800138002","13800138003"]','尊敬的客户，感谢您的支持，春季专属优惠已为您准备。',2,'2026-03-10 10:00:00',3,3,2,1,66.67,'春季回馈第一批','13800138003 号码不可用','1','2026-03-10 09:00:00','1','2026-03-10 10:00:00',b'0'),
(900012,1,900003,'客户生日关怀活动',102,'生日祝福短信模板','["13900139001","13900139002"]','祝您生日快乐，愿新的一年万事顺意！',0,NULL,2,0,0,0,0.00,'待审批后发送',NULL,'1','2026-07-15 10:00:00','1','2026-07-15 10:00:00',b'0');

INSERT IGNORE INTO `marketing_email_batch`
(`id`,`tenant_id`,`campaign_id`,`campaign_name`,`template_id`,`template_name`,`email_list`,`subject`,`content`,`status`,`send_time`,`total_count`,`send_count`,`success_count`,`fail_count`,`send_rate`,`remark`,`error_message`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
VALUES
(900021,1,900002,'2026 新品邮件推广',501,'新品推广邮件模板','["customer1@example.com","customer2@example.com"]','2026 新品发布会邀请函','感谢您的关注，诚邀您参加新品发布会。',1,'2026-07-18 09:00:00',2,1,1,0,50.00,'华东区第一批，正在发送',NULL,'1','2026-07-18 08:30:00','1','2026-07-18 09:00:00',b'0'),
(900022,1,900001,'2026 春季客户回馈活动',502,'回馈邮件模板','["vip@example.com"]','春季客户回馈礼遇','为感谢您的持续支持，现奉上专属回馈礼遇。',2,'2026-03-12 14:00:00',1,1,1,0,100.00,'已发送',NULL,'1','2026-03-12 13:30:00','1','2026-03-12 14:00:00',b'0');

INSERT IGNORE INTO `marketing_customer_care`
(`id`,`tenant_id`,`name`,`type`,`trigger_type`,`trigger_condition`,`send_channel`,`template_id`,`content`,`subject`,`status`,`remark`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
VALUES
(900031,1,'生日关怀短信',1,1,'{"birthday":"today"}',1,101,'祝您生日快乐，愿您每天都有好心情！',NULL,1,'生日当天 09:00 自动发送','1','2026-01-10 10:00:00','1','2026-07-19 10:00:00',b'0'),
(900032,1,'周年纪念邮件',2,1,'{"anniversary":"month"}',2,201,'感谢您一直以来的陪伴，期待继续为您服务。','周年纪念日专属礼遇',1,'客户合作周年自动发送','1','2026-01-12 14:00:00','1','2026-07-19 10:00:00',b'0');

INSERT IGNORE INTO `marketing_approval`
(`id`,`tenant_id`,`campaign_id`,`campaign_name`,`type`,`target_count`,`content_preview`,`batch_id`,`status`,`approver`,`approve_remark`,`approve_time`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
VALUES
(900041,1,900001,'2026 春季客户回馈活动',1,500,'尊敬的客户，感谢您的支持，春季专属优惠已为您准备。',900011,1,'admin','内容审核通过，注意发送频率','2026-03-09 16:00:00','1','2026-03-09 15:00:00','1','2026-03-09 16:00:00',b'0'),
(900042,1,900002,'2026 新品邮件推广',2,800,'感谢您的关注，诚邀您参加新品发布会。',900021,0,NULL,NULL,NULL,'1','2026-07-18 08:00:00','1','2026-07-18 08:00:00',b'0'),
(900043,1,900003,'客户生日关怀活动',1,300,'祝您生日快乐，愿新的一年万事顺意！',900012,2,'admin','请先补充客户筛选范围','2026-07-16 10:00:00','1','2026-07-15 11:00:00','1','2026-07-16 10:00:00',b'0');

INSERT IGNORE INTO `marketing_send_record`
(`id`,`tenant_id`,`batch_id`,`campaign_name`,`batch_name`,`type`,`target`,`content`,`status`,`error_message`,`send_time`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
VALUES
(900051,1,900011,'2026 春季客户回馈活动','春季回馈第一批',1,'13800138001','尊敬的客户，感谢您的支持，春季专属优惠已为您准备。',1,NULL,'2026-03-10 10:00:03','1','2026-03-10 10:00:00','1','2026-03-10 10:00:03',b'0'),
(900052,1,900011,'2026 春季客户回馈活动','春季回馈第一批',1,'13800138003','尊敬的客户，感谢您的支持，春季专属优惠已为您准备。',2,'号码不可用','2026-03-10 10:00:05','1','2026-03-10 10:00:00','1','2026-03-10 10:00:05',b'0'),
(900053,1,900021,'2026 新品邮件推广','华东区第一批',2,'customer1@example.com','感谢您的关注，诚邀您参加新品发布会。',1,NULL,'2026-07-18 09:00:03','1','2026-07-18 09:00:00','1','2026-07-18 09:00:03',b'0');
