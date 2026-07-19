-- Test data initialization
DELETE FROM crm_business_status WHERE id IN (4, 5, 6, 7, 8, 9, 10, 11);
INSERT INTO crm_business_status (type_id, name, percent, sort, tenant_id) VALUES
(1, '初步接触', 10, 0, 1),
(1, '需求分析', 20, 1, 1),
(1, '方案制定', 30, 2, 1),
(1, '商务谈判', 50, 3, 1),
(1, '合同签订', 80, 4, 1),
(1, '成交', 100, 5, 1),
(1, '流失', 0, 6, 1);
UPDATE crm_customer SET name = '北京科技有限公司', mobile = '13800138001' WHERE id = 1;
UPDATE crm_customer SET name = '上海贸易集团', mobile = '13800138002' WHERE id = 2;
UPDATE crm_customer SET name = '广州信息技术有限公司', mobile = '13800138003' WHERE id = 3;
UPDATE crm_customer SET name = '深圳智能制造有限公司', mobile = '13800138004' WHERE id = 4;
UPDATE crm_customer SET name = '杭州互联网科技有限公司', mobile = '13800138005' WHERE id = 5;
UPDATE crm_customer SET name = '成都软件开发有限公司', mobile = '13800138006' WHERE id = 6;
UPDATE crm_customer SET name = '武汉物流集团', mobile = '13800138007' WHERE id = 7;
UPDATE crm_customer SET name = '南京电子科技有限公司', mobile = '13800138008' WHERE id = 8;
UPDATE crm_customer SET name = '西安能源有限公司', mobile = '13800138009' WHERE id = 9;
UPDATE crm_customer SET name = '苏州精密机械有限公司', mobile = '13800138010' WHERE id = 10;
UPDATE crm_customer SET name = '厦门进出口贸易有限公司', mobile = '13800138011' WHERE id = 11;
UPDATE crm_customer SET name = '青岛化工有限公司', mobile = '13800138012' WHERE id = 12;
UPDATE crm_customer SET name = '天津自动化设备有限公司', mobile = '13800138013' WHERE id = 13;
UPDATE crm_customer SET name = '重庆新能源科技有限公司', mobile = '13800138014' WHERE id = 14;
UPDATE crm_customer SET name = '长沙医疗器械有限公司', mobile = '13800138015' WHERE id = 15;
UPDATE crm_customer SET name = '郑州电商集团', mobile = '13800138016' WHERE id = 16;
UPDATE crm_customer SET name = '大连船舶重工有限公司', mobile = '13800138017' WHERE id = 17;
UPDATE crm_business SET name = '企业管理系统升级项目', source = '1', competitor = '用友', status_id = 0 WHERE id = 4;
UPDATE crm_business SET name = '智能仓储解决方案', source = '2', competitor = '顺丰科技', status_id = 1 WHERE id = 5;
UPDATE crm_business SET name = '大数据分析平台建设', source = '3', competitor = '华为云', status_id = 2 WHERE id = 6;
UPDATE crm_business SET name = 'ERP系统实施项目', source = '4', competitor = '金蝶', status_id = 3 WHERE id = 7;
UPDATE crm_business SET name = '电商平台开发', source = '5', competitor = '阿里云', status_id = 4 WHERE id = 8;
UPDATE crm_business SET name = '供应链管理系统', source = '1', competitor = 'SAP', status_id = 5, end_status = 1 WHERE id = 9;
UPDATE crm_business SET name = '客户关系管理系统', source = '2', competitor = 'Salesforce', status_id = 6, end_status = 2 WHERE id = 10;
UPDATE crm_business SET name = '智能制造MES系统', source = '3', competitor = '西门子', status_id = 0 WHERE id = 11;
UPDATE crm_business SET name = '财务系统升级', source = '4', competitor = 'Oracle', status_id = 1 WHERE id = 12;
UPDATE crm_business SET name = '移动办公平台', source = '5', competitor = '钉钉', status_id = 2 WHERE id = 13;
INSERT INTO crm_business (name, customer_id, source, competitor, owner_user_id, status_id, tenant_id) VALUES
('云计算服务采购项目', 1, '3', '华为云', 1, 3, 1),
('物联网平台建设', 2, '1', '阿里云', 1, 4, 1),
('智慧园区解决方案', 3, '4', '腾讯云', 1, 2, 1),
('在线教育平台开发', 4, '5', '网易云', 1, 0, 1),
('医疗信息化系统', 5, '1', '东华软件', 1, 1, 1),
('金融风控系统', 6, '2', '恒生电子', 1, 5, 1),
('物流管理系统', 7, '3', '京东物流', 1, 3, 1),
('能源管理平台', 8, '4', '施耐德', 1, 4, 1),
('工业互联网平台', 9, '1', '航天科工', 1, 2, 1),
('数字化转型咨询项目', 10, '2', 'IBM', 1, 1, 1);
UPDATE crm_business SET deal_time = '2026-07-01 10:00:00' WHERE id = 9;
UPDATE crm_business SET deal_time = '2026-07-10 14:30:00' WHERE id = 18;
UPDATE crm_business_status_type SET name = '标准销售阶段' WHERE id = 1;
