-- 工单域演示数据：使用 DEMO-WO 编号和 960000 段流水号，可重复执行且不删除已有工单。
-- 依赖：new-work-order-domain.sql 已先执行。
SET NAMES utf8mb4;

SET @demo_tenant_id = COALESCE((
  SELECT id FROM system_tenant WHERE status = 0 AND deleted = b'0'
  ORDER BY (id = 1) DESC, id LIMIT 1
), 1);
SET @demo_user_id = COALESCE((
  SELECT id FROM system_users WHERE tenant_id = @demo_tenant_id AND status = 0 AND deleted = b'0'
  ORDER BY (username = 'admin') DESC, id LIMIT 1
), 1);
SET @demo_user_name = COALESCE((SELECT nickname FROM system_users WHERE id = @demo_user_id LIMIT 1), '管理员');
SET @demo_customer_id = COALESCE((SELECT id FROM crm_customer WHERE tenant_id = @demo_tenant_id AND deleted = b'0' ORDER BY id LIMIT 1), 0);
SET @demo_customer_name = COALESCE((SELECT name FROM crm_customer WHERE id = @demo_customer_id LIMIT 1), '演示客户');

INSERT IGNORE INTO work_order
  (id, order_no, title, type, priority, status, content, reporter_id, reporter_name, assignee_id, assignee_name, customer_id, customer_name, related_module, handle_note, handle_time, close_time, expected_finish_time, actual_finish_time, creator, updater, deleted, tenant_id)
VALUES
  (960001, 'DEMO-WO-001', '客户演示环境无法登录', 1, 4, 1, '客户反馈演示环境登录后页面空白，请协助定位。', @demo_user_id, @demo_user_name, @demo_user_id, @demo_user_name, @demo_customer_id, @demo_customer_name, 'CRM', NULL, NULL, NULL, DATE_ADD(NOW(), INTERVAL 4 HOUR), NULL, @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (960002, 'DEMO-WO-002', '新增客户数据导入服务请求', 2, 2, 2, '为华东客户导入首批历史联系人数据。', @demo_user_id, @demo_user_name, @demo_user_id, @demo_user_name, @demo_customer_id, @demo_customer_name, 'CRM', '已完成字段映射，正在校验导入结果。', DATE_SUB(NOW(), INTERVAL 1 HOUR), NULL, DATE_ADD(NOW(), INTERVAL 1 DAY), NULL, @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (960003, 'DEMO-WO-003', '营销短信发送结果咨询', 3, 2, 3, '需要确认短信群发失败记录的查看方式。', @demo_user_id, @demo_user_name, @demo_user_id, @demo_user_name, @demo_customer_id, @demo_customer_name, '营销', '已向客户说明失败原因和重发方式。', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (960004, 'DEMO-WO-004', '订单折扣规则调整申请', 4, 3, 4, '申请调整重点客户订单折扣规则。', @demo_user_id, @demo_user_name, @demo_user_id, @demo_user_name, @demo_customer_id, @demo_customer_name, '订单', NULL, NULL, NULL, DATE_ADD(NOW(), INTERVAL 2 DAY), NULL, @demo_user_id, @demo_user_id, b'0', @demo_tenant_id);

INSERT IGNORE INTO work_order_status_flow
  (id, order_id, order_no, from_status, to_status, operator_id, operator_name, remark, creator, updater, deleted, tenant_id)
VALUES
  (960011, 960001, 'DEMO-WO-001', 0, 1, @demo_user_id, @demo_user_name, '演示工单已创建，等待处理。', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (960012, 960002, 'DEMO-WO-002', 0, 1, @demo_user_id, @demo_user_name, '演示工单已创建。', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (960013, 960002, 'DEMO-WO-002', 1, 2, @demo_user_id, @demo_user_name, '开始处理数据导入请求。', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (960014, 960003, 'DEMO-WO-003', 0, 1, @demo_user_id, @demo_user_name, '演示工单已创建。', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (960015, 960003, 'DEMO-WO-003', 1, 2, @demo_user_id, @demo_user_name, '开始核对营销发送记录。', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (960016, 960003, 'DEMO-WO-003', 2, 3, @demo_user_id, @demo_user_name, '已完成客户答复。', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (960017, 960004, 'DEMO-WO-004', 0, 1, @demo_user_id, @demo_user_name, '演示工单已创建。', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (960018, 960004, 'DEMO-WO-004', 1, 4, @demo_user_id, @demo_user_name, '资料不完整，退回补充折扣依据。', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id);
