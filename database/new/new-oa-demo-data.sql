-- OA 域演示数据：固定 950000 段主键，可重复执行且不删除、不覆盖既有业务数据。
-- 依赖：OA 表结构、请假类型字典已初始化。
SET NAMES utf8mb4;

SET @demo_tenant_id = COALESCE((
  SELECT id FROM system_tenant WHERE status = 0 AND deleted = b'0'
  ORDER BY (id = 1) DESC, id LIMIT 1
), 1);
SET @demo_user_id = COALESCE((
  SELECT id FROM system_users
  WHERE tenant_id = @demo_tenant_id AND status = 0 AND deleted = b'0'
  ORDER BY (username = 'admin') DESC, id LIMIT 1
), 1);

-- 审批表单仅用于列表、详情和状态展示；不伪造 Flowable 流程实例或待办任务。
INSERT IGNORE INTO bpm_oa_leave
  (id, user_id, type, reason, start_time, end_time, day, status, process_instance_id, creator, updater, deleted, tenant_id)
VALUES
  (950001, @demo_user_id, '7', '年中休假演示申请', DATE_ADD(NOW(), INTERVAL 7 DAY), DATE_ADD(NOW(), INTERVAL 9 DAY), 2, 1, NULL, @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (950002, @demo_user_id, '1', '已审批完成的病假演示记录', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), 1, 2, NULL, @demo_user_id, @demo_user_id, b'0', @demo_tenant_id);

INSERT IGNORE INTO bpm_oa_business_trip
  (id, user_id, destination, start_time, end_time, day, reason, status, process_instance_id, creator, updater, deleted, tenant_id)
VALUES
  (950011, @demo_user_id, '上海', DATE_ADD(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 5 DAY), 2, '客户现场项目启动会', 1, NULL, @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (950012, @demo_user_id, '杭州', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), 2, '产品交付回访', 2, NULL, @demo_user_id, @demo_user_id, b'0', @demo_tenant_id);

INSERT IGNORE INTO bpm_oa_loan
  (id, user_id, amount, reason, repayment_date, status, process_instance_id, creator, updater, deleted, tenant_id)
VALUES
  (950021, @demo_user_id, 5000.00, '华东客户现场差旅备用金', DATE_ADD(NOW(), INTERVAL 30 DAY), 1, NULL, @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (950022, @demo_user_id, 1800.00, '已完成的展会物料借款', DATE_ADD(NOW(), INTERVAL 7 DAY), 2, NULL, @demo_user_id, @demo_user_id, b'0', @demo_tenant_id);

INSERT IGNORE INTO bpm_oa_customer_visit
  (id, user_id, customer_name, customer_contact, contact_phone, visit_time, visit_location, purpose, status, process_instance_id, creator, updater, deleted, tenant_id)
VALUES
  (950031, @demo_user_id, '星云科技（演示客户）', '李经理', '13800000001', DATE_ADD(NOW(), INTERVAL 2 DAY), '上海浦东新区', '确认新项目范围与报价计划', 1, NULL, @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (950032, @demo_user_id, '远航制造（演示客户）', '王总', '13800000002', DATE_SUB(NOW(), INTERVAL 8 DAY), '苏州工业园区', '交付回访与续约沟通', 2, NULL, @demo_user_id, @demo_user_id, b'0', @demo_tenant_id);

INSERT IGNORE INTO bpm_oa_work_report
  (id, user_id, type, title, content, report_date, status, review_comment, process_instance_id, creator, updater, deleted, tenant_id)
VALUES
  (950041, @demo_user_id, 1, '客户项目推进日报', '完成客户需求确认，已安排下一轮方案评审。', CURDATE(), 1, NULL, NULL, @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (950042, @demo_user_id, 2, '第 29 周工作周报', '完成历史商机清理、客户回访和订单演示准备。', DATE_SUB(CURDATE(), INTERVAL 7 DAY), 2, '内容完整，继续跟进重点客户。', NULL, @demo_user_id, @demo_user_id, b'0', @demo_tenant_id);

INSERT IGNORE INTO bpm_oa_task
  (id, user_id, title, description, priority, status, assignee_id, start_time, end_time, creator, updater, deleted, tenant_id)
VALUES
  (950051, @demo_user_id, '整理客户报价材料', '汇总客户需求、报价单和合同条款。', 2, 0, @demo_user_id, NOW(), DATE_ADD(NOW(), INTERVAL 2 DAY), @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (950052, @demo_user_id, '跟进新品推广活动', '确认邮件群发审批进度。', 1, 1, @demo_user_id, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 3 DAY), @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (950053, @demo_user_id, '归档客户回访记录', '已完成的历史任务。', 0, 2, @demo_user_id, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), @demo_user_id, @demo_user_id, b'0', @demo_tenant_id);

INSERT IGNORE INTO bpm_oa_schedule
  (id, user_id, title, description, start_time, end_time, location, remind_time, status, creator, updater, deleted, tenant_id)
VALUES
  (950061, @demo_user_id, '星云科技方案评审会', '与客户确认项目范围和方案报价。', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 1 DAY), INTERVAL 2 HOUR), '线上会议', DATE_SUB(DATE_ADD(NOW(), INTERVAL 1 DAY), INTERVAL 30 MINUTE), 0, @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (950062, @demo_user_id, '营销周例会', '同步营销活动、订单和客户跟进进度。', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 DAY), INTERVAL 1 HOUR), '会议室 A', NULL, 2, @demo_user_id, @demo_user_id, b'0', @demo_tenant_id);

INSERT IGNORE INTO bpm_oa_work_request
  (id, user_id, title, content, status, process_instance_id, creator, updater, deleted, tenant_id)
VALUES
  (950071, @demo_user_id, '申请客户演示环境资源', '申请为重点客户开通七天演示环境，用于现场方案验证。', 1, NULL, @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (950072, @demo_user_id, '申请展会物料采购', '申请采购新品推广活动需要的宣传物料。', 2, NULL, @demo_user_id, @demo_user_id, b'0', @demo_tenant_id);
