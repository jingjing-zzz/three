-- CRM 客户域演示数据：使用 970000 段固定编号，不修改 CRM 基础样例客户。
-- 覆盖线索跟进、重点客户、已成交客户和公海场景，可重复执行。
SET NAMES utf8mb4;

SET @demo_tenant_id = COALESCE((
  SELECT id FROM system_tenant WHERE status = 0 AND deleted = b'0'
  ORDER BY (id = 1) DESC, id LIMIT 1
), 1);
SET @demo_user_id = COALESCE((
  SELECT id FROM system_users WHERE tenant_id = @demo_tenant_id AND status = 0 AND deleted = b'0'
  ORDER BY (username = 'admin') DESC, id LIMIT 1
), 1);

INSERT IGNORE INTO crm_customer
  (id, name, follow_up_status, contact_last_time, contact_last_content, contact_next_time,
   owner_user_id, owner_time, lock_status, deal_status, mobile, telephone, wechat, email,
   detail_address, industry_id, level, source, star, status, remark, creator, updater, deleted, tenant_id)
VALUES
  (970001, '星云科技（CRM演示）', 1, DATE_SUB(NOW(), INTERVAL 1 DAY), '已确认项目需求，等待方案评审。', DATE_ADD(NOW(), INTERVAL 2 DAY), @demo_user_id, NOW(), b'0', b'0', '13800000101', '021-60000001', 'xingyun-demo', 'contact@xingyun-demo.example', '上海市浦东新区演示路 1 号', 1, 3, 1, 5, 2, '重点跟进客户，可用于商机、订单和工单演示。', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (970002, '远航制造（CRM演示）', 1, DATE_SUB(NOW(), INTERVAL 4 DAY), '已完成现场回访，准备续约报价。', DATE_ADD(NOW(), INTERVAL 3 DAY), @demo_user_id, NOW(), b'0', b'1', '13800000102', '0512-60000002', 'yuanhang-demo', 'sales@yuanhang-demo.example', '江苏省苏州市工业园区演示街 2 号', 2, 2, 2, 4, 3, '已成交客户，用于续约和客户关怀演示。', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (970003, '云帆贸易（CRM演示）', 0, NULL, NULL, DATE_ADD(NOW(), INTERVAL 5 DAY), @demo_user_id, NOW(), b'0', b'0', '13800000103', '020-60000003', 'yunfan-demo', 'service@yunfan-demo.example', '广东省广州市天河区演示大道 3 号', 3, 1, 3, 3, 1, '待首次联系客户，用于线索转化演示。', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (970004, '华北智造（CRM演示）', 1, DATE_SUB(NOW(), INTERVAL 8 DAY), '项目预算暂未确定，持续保持联系。', DATE_ADD(NOW(), INTERVAL 7 DAY), @demo_user_id, NOW(), b'1', b'0', '13800000104', '010-60000004', 'huabei-demo', 'pm@huabei-demo.example', '北京市朝阳区演示园区 4 号', 2, 2, 4, 2, 2, '已锁定客户，用于客户锁定状态演示。', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id),
  (970005, '西南医疗（CRM演示）', 0, NULL, NULL, NULL, NULL, NOW(), b'0', b'0', '13800000105', '028-60000005', 'xinan-demo', 'info@xinan-demo.example', '四川省成都市高新区演示路 5 号', 4, 1, 5, 1, 1, '公海客户，用于领取和分配客户演示。', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id);

-- 演示客户必须具备负责人权限，才能安全演示转移、分配等操作。
INSERT INTO crm_permission (biz_type, biz_id, user_id, level, creator, updater, deleted, tenant_id)
SELECT 1, c.id, c.owner_user_id, 1, @demo_user_id, @demo_user_id, b'0', @demo_tenant_id
FROM crm_customer c
LEFT JOIN crm_permission p ON p.biz_type = 1 AND p.biz_id = c.id
    AND p.user_id = c.owner_user_id AND p.deleted = b'0'
WHERE c.id BETWEEN 970001 AND 970004
  AND c.tenant_id = @demo_tenant_id
  AND c.owner_user_id IS NOT NULL
  AND p.id IS NULL;
