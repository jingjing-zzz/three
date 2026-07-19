-- CRM 订单域演示数据：以 DEMO-ORDER 编号隔离，可重复执行且不影响已有订单。
-- 依赖：crm_order、crm_order_product 和 CRM 基础客户/商品表已初始化。
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
SET @demo_customer_id = COALESCE((
  SELECT id FROM crm_customer WHERE tenant_id = @demo_tenant_id AND deleted = b'0'
  ORDER BY id LIMIT 1
), 1);
SET @demo_product_id = COALESCE((
  SELECT id FROM erp_product WHERE tenant_id = @demo_tenant_id AND deleted = b'0'
  ORDER BY id LIMIT 1
), 1);

INSERT INTO crm_order
  (no, status, customer_id, owner_user_id, order_time, total_product_price, discount_percent, total_price, remark, creator, updater, deleted, tenant_id)
SELECT 'DEMO-ORDER-001', 0, @demo_customer_id, @demo_user_id, NOW(), 12000.00, 0.00, 12000.00, '草稿订单，用于编辑与提交审批演示', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id
WHERE NOT EXISTS (SELECT 1 FROM crm_order WHERE no = 'DEMO-ORDER-001' AND tenant_id = @demo_tenant_id AND deleted = b'0');
INSERT INTO crm_order
  (no, status, customer_id, owner_user_id, order_time, total_product_price, discount_percent, total_price, remark, creator, updater, deleted, tenant_id)
SELECT 'DEMO-ORDER-002', 10, @demo_customer_id, @demo_user_id, DATE_SUB(NOW(), INTERVAL 2 DAY), 28000.00, 5.00, 26600.00, '待审核订单，用于订单审批列表演示', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id
WHERE NOT EXISTS (SELECT 1 FROM crm_order WHERE no = 'DEMO-ORDER-002' AND tenant_id = @demo_tenant_id AND deleted = b'0');
INSERT INTO crm_order
  (no, status, customer_id, owner_user_id, order_time, total_product_price, discount_percent, total_price, remark, creator, updater, deleted, tenant_id)
SELECT 'DEMO-ORDER-003', 30, @demo_customer_id, @demo_user_id, DATE_SUB(NOW(), INTERVAL 10 DAY), 68000.00, 10.00, 61200.00, '已审核订单，用于订单报表与统计演示', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id
WHERE NOT EXISTS (SELECT 1 FROM crm_order WHERE no = 'DEMO-ORDER-003' AND tenant_id = @demo_tenant_id AND deleted = b'0');
INSERT INTO crm_order
  (no, status, customer_id, owner_user_id, order_time, total_product_price, discount_percent, total_price, remark, creator, updater, deleted, tenant_id)
SELECT 'DEMO-ORDER-004', 20, @demo_customer_id, @demo_user_id, DATE_SUB(NOW(), INTERVAL 1 DAY), 15600.00, 0.00, 15600.00, '审核中的订单，用于审批状态展示', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id
WHERE NOT EXISTS (SELECT 1 FROM crm_order WHERE no = 'DEMO-ORDER-004' AND tenant_id = @demo_tenant_id AND deleted = b'0');
INSERT INTO crm_order
  (no, status, customer_id, owner_user_id, order_time, total_product_price, discount_percent, total_price, remark, creator, updater, deleted, tenant_id)
SELECT 'DEMO-ORDER-005', 40, @demo_customer_id, @demo_user_id, DATE_SUB(NOW(), INTERVAL 6 DAY), 32000.00, 0.00, 32000.00, '审核不通过订单，用于重新编辑与驳回状态展示', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id
WHERE NOT EXISTS (SELECT 1 FROM crm_order WHERE no = 'DEMO-ORDER-005' AND tenant_id = @demo_tenant_id AND deleted = b'0');
INSERT INTO crm_order
  (no, status, customer_id, owner_user_id, order_time, total_product_price, discount_percent, total_price, remark, creator, updater, deleted, tenant_id)
SELECT 'DEMO-ORDER-006', 50, @demo_customer_id, @demo_user_id, DATE_SUB(NOW(), INTERVAL 18 DAY), 45000.00, 0.00, 45000.00, '已完成订单，用于订单报表完成金额统计', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id
WHERE NOT EXISTS (SELECT 1 FROM crm_order WHERE no = 'DEMO-ORDER-006' AND tenant_id = @demo_tenant_id AND deleted = b'0');
INSERT INTO crm_order
  (no, status, customer_id, owner_user_id, order_time, total_product_price, discount_percent, total_price, remark, creator, updater, deleted, tenant_id)
SELECT 'DEMO-ORDER-007', 60, @demo_customer_id, @demo_user_id, DATE_SUB(NOW(), INTERVAL 3 DAY), 8800.00, 0.00, 8800.00, '已取消订单，用于订单报表取消状态展示', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id
WHERE NOT EXISTS (SELECT 1 FROM crm_order WHERE no = 'DEMO-ORDER-007' AND tenant_id = @demo_tenant_id AND deleted = b'0');

SET @demo_order_1 = (SELECT id FROM crm_order WHERE no = 'DEMO-ORDER-001' AND tenant_id = @demo_tenant_id AND deleted = b'0' LIMIT 1);
SET @demo_order_2 = (SELECT id FROM crm_order WHERE no = 'DEMO-ORDER-002' AND tenant_id = @demo_tenant_id AND deleted = b'0' LIMIT 1);
SET @demo_order_3 = (SELECT id FROM crm_order WHERE no = 'DEMO-ORDER-003' AND tenant_id = @demo_tenant_id AND deleted = b'0' LIMIT 1);
SET @demo_order_4 = (SELECT id FROM crm_order WHERE no = 'DEMO-ORDER-004' AND tenant_id = @demo_tenant_id AND deleted = b'0' LIMIT 1);
SET @demo_order_5 = (SELECT id FROM crm_order WHERE no = 'DEMO-ORDER-005' AND tenant_id = @demo_tenant_id AND deleted = b'0' LIMIT 1);
SET @demo_order_6 = (SELECT id FROM crm_order WHERE no = 'DEMO-ORDER-006' AND tenant_id = @demo_tenant_id AND deleted = b'0' LIMIT 1);
SET @demo_order_7 = (SELECT id FROM crm_order WHERE no = 'DEMO-ORDER-007' AND tenant_id = @demo_tenant_id AND deleted = b'0' LIMIT 1);

INSERT INTO crm_order_product
  (order_id, product_id, product_price, count, total_price, tax_percent, tax_price, remark, creator, updater, deleted, tenant_id)
SELECT @demo_order_1, @demo_product_id, 1200.00, 10.00, 12000.00, 6.00, 720.00, '草稿订单演示商品', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id
WHERE @demo_order_1 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM crm_order_product WHERE order_id = @demo_order_1 AND deleted = b'0');
INSERT INTO crm_order_product
  (order_id, product_id, product_price, count, total_price, tax_percent, tax_price, remark, creator, updater, deleted, tenant_id)
SELECT @demo_order_2, @demo_product_id, 2800.00, 10.00, 28000.00, 6.00, 1680.00, '待审核订单演示商品', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id
WHERE @demo_order_2 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM crm_order_product WHERE order_id = @demo_order_2 AND deleted = b'0');
INSERT INTO crm_order_product
  (order_id, product_id, product_price, count, total_price, tax_percent, tax_price, remark, creator, updater, deleted, tenant_id)
SELECT @demo_order_3, @demo_product_id, 6800.00, 10.00, 68000.00, 6.00, 4080.00, '已审核订单演示商品', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id
WHERE @demo_order_3 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM crm_order_product WHERE order_id = @demo_order_3 AND deleted = b'0');
INSERT INTO crm_order_product
  (order_id, product_id, product_price, count, total_price, tax_percent, tax_price, remark, creator, updater, deleted, tenant_id)
SELECT @demo_order_4, @demo_product_id, 1560.00, 10.00, 15600.00, 6.00, 936.00, '审核中订单演示商品', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id
WHERE @demo_order_4 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM crm_order_product WHERE order_id = @demo_order_4 AND deleted = b'0');
INSERT INTO crm_order_product
  (order_id, product_id, product_price, count, total_price, tax_percent, tax_price, remark, creator, updater, deleted, tenant_id)
SELECT @demo_order_5, @demo_product_id, 3200.00, 10.00, 32000.00, 6.00, 1920.00, '驳回订单演示商品', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id
WHERE @demo_order_5 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM crm_order_product WHERE order_id = @demo_order_5 AND deleted = b'0');
INSERT INTO crm_order_product
  (order_id, product_id, product_price, count, total_price, tax_percent, tax_price, remark, creator, updater, deleted, tenant_id)
SELECT @demo_order_6, @demo_product_id, 4500.00, 10.00, 45000.00, 6.00, 2700.00, '已完成订单演示商品', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id
WHERE @demo_order_6 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM crm_order_product WHERE order_id = @demo_order_6 AND deleted = b'0');
INSERT INTO crm_order_product
  (order_id, product_id, product_price, count, total_price, tax_percent, tax_price, remark, creator, updater, deleted, tenant_id)
SELECT @demo_order_7, @demo_product_id, 880.00, 10.00, 8800.00, 6.00, 528.00, '已取消订单演示商品', @demo_user_id, @demo_user_id, b'0', @demo_tenant_id
WHERE @demo_order_7 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM crm_order_product WHERE order_id = @demo_order_7 AND deleted = b'0');
