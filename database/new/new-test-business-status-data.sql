-- ============================================================
-- 编号: new-test-business-status-data.sql
-- 用途: 初始化商机测试数据使用的状态组与阶段
-- 依赖: crm_business_status_type、crm_business_status 已创建
-- 执行顺序: new-test-data.sql 之后、new-test-business-data.sql 之前
-- 适用范围: 本地演示/测试环境，可重复执行
-- ============================================================

SET NAMES utf8mb4;
START TRANSACTION;

-- 测试商机统一使用状态组 3，状态组名称与商机详情、漏斗、预测保持一致。
INSERT INTO crm_business_status_type
    (id, name, dept_ids, creator, create_time, updater, update_time, tenant_id, deleted)
VALUES
    (3, '标准销售阶段', '', '1', NOW(), '1', NOW(), 1, b'0')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    dept_ids = VALUES(dept_ids),
    updater = VALUES(updater),
    update_time = VALUES(update_time),
    deleted = b'0';

-- 清理原始演示库中的无业务含义状态组，避免页面出现“吃饭睡觉打豆豆”等测试项。
-- 当前没有商机引用这些状态组；采用逻辑删除，保留审计记录。
UPDATE crm_business_status_type
SET deleted = b'1', updater = '1', update_time = NOW()
WHERE tenant_id = 1 AND id IN (4, 5, 6) AND deleted = b'0';

UPDATE crm_business_status
SET deleted = b'1', updater = '1', update_time = NOW()
WHERE tenant_id = 1 AND type_id IN (4, 5, 6) AND deleted = b'0';

-- 补充三套演示用状态组：适合快速线索、大客户项目与老客户续约场景。
INSERT INTO crm_business_status_type
    (id, name, dept_ids, creator, create_time, updater, update_time, tenant_id, deleted)
VALUES
    (7, '快速成交流程', '', '1', NOW(), '1', NOW(), 1, b'0'),
    (8, '大客户项目流程', '', '1', NOW(), '1', NOW(), 1, b'0'),
    (9, '续约复购流程', '', '1', NOW(), '1', NOW(), 1, b'0')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    dept_ids = VALUES(dept_ids),
    updater = VALUES(updater),
    update_time = VALUES(update_time),
    deleted = b'0';

-- new-test-data.sql 曾在状态组 1 下生成 12-18 号旧测试阶段；本脚本保留历史记录但不在页面显示，
-- 避免与状态组 3 的 33-39 号标准销售阶段重复。
UPDATE crm_business_status
SET deleted = b'1', updater = '1', update_time = NOW()
WHERE tenant_id = 1 AND type_id = 1 AND id BETWEEN 12 AND 18 AND deleted = b'0';

-- 40 条商机假数据引用 33-39 号阶段。阶段概率用于销售漏斗与销售预测加权计算。
INSERT INTO crm_business_status
    (id, type_id, name, percent, sort, creator, create_time, updater, update_time, tenant_id, deleted)
VALUES
    (33, 3, '初步接触', 10, 0, '1', NOW(), '1', NOW(), 1, b'0'),
    (34, 3, '需求确认', 20, 1, '1', NOW(), '1', NOW(), 1, b'0'),
    (35, 3, '方案报价', 30, 2, '1', NOW(), '1', NOW(), 1, b'0'),
    (36, 3, '商务谈判', 50, 3, '1', NOW(), '1', NOW(), 1, b'0'),
    (37, 3, '合同审核', 80, 4, '1', NOW(), '1', NOW(), 1, b'0'),
    (38, 3, '已赢单', 100, 5, '1', NOW(), '1', NOW(), 1, b'0'),
    (39, 3, '已输单', 0, 6, '1', NOW(), '1', NOW(), 1, b'0')
ON DUPLICATE KEY UPDATE
    type_id = VALUES(type_id),
    name = VALUES(name),
    percent = VALUES(percent),
    sort = VALUES(sort),
    updater = VALUES(updater),
    update_time = VALUES(update_time),
    tenant_id = VALUES(tenant_id),
    deleted = b'0';

-- 其他状态组的阶段：阶段概率供销售预测按商机金额加权计算。
INSERT INTO crm_business_status
    (id, type_id, name, percent, sort, creator, create_time, updater, update_time, tenant_id, deleted)
VALUES
    -- 快速成交流程
    (40, 7, '商机识别', 10, 0, '1', NOW(), '1', NOW(), 1, b'0'),
    (41, 7, '需求确认', 30, 1, '1', NOW(), '1', NOW(), 1, b'0'),
    (42, 7, '方案演示', 50, 2, '1', NOW(), '1', NOW(), 1, b'0'),
    (43, 7, '报价确认', 70, 3, '1', NOW(), '1', NOW(), 1, b'0'),
    (44, 7, '已赢单', 100, 4, '1', NOW(), '1', NOW(), 1, b'0'),
    (45, 7, '已输单', 0, 5, '1', NOW(), '1', NOW(), 1, b'0'),
    -- 大客户项目流程
    (46, 8, '客户调研', 5, 0, '1', NOW(), '1', NOW(), 1, b'0'),
    (47, 8, '项目立项', 15, 1, '1', NOW(), '1', NOW(), 1, b'0'),
    (48, 8, '方案评审', 35, 2, '1', NOW(), '1', NOW(), 1, b'0'),
    (49, 8, '技术选型', 55, 3, '1', NOW(), '1', NOW(), 1, b'0'),
    (50, 8, '商务谈判', 75, 4, '1', NOW(), '1', NOW(), 1, b'0'),
    (51, 8, '合同审核', 90, 5, '1', NOW(), '1', NOW(), 1, b'0'),
    (52, 8, '已赢单', 100, 6, '1', NOW(), '1', NOW(), 1, b'0'),
    (53, 8, '已输单', 0, 7, '1', NOW(), '1', NOW(), 1, b'0'),
    -- 续约复购流程
    (54, 9, '续约提醒', 10, 0, '1', NOW(), '1', NOW(), 1, b'0'),
    (55, 9, '续约沟通', 30, 1, '1', NOW(), '1', NOW(), 1, b'0'),
    (56, 9, '续约报价', 60, 2, '1', NOW(), '1', NOW(), 1, b'0'),
    (57, 9, '合同确认', 90, 3, '1', NOW(), '1', NOW(), 1, b'0'),
    (58, 9, '已续约', 100, 4, '1', NOW(), '1', NOW(), 1, b'0'),
    (59, 9, '已流失', 0, 5, '1', NOW(), '1', NOW(), 1, b'0')
ON DUPLICATE KEY UPDATE
    type_id = VALUES(type_id),
    name = VALUES(name),
    percent = VALUES(percent),
    sort = VALUES(sort),
    updater = VALUES(updater),
    update_time = VALUES(update_time),
    tenant_id = VALUES(tenant_id),
    deleted = b'0';

-- 修复已有测试商机的状态组字段，阶段编号不变。
UPDATE crm_business b
JOIN crm_business_status s ON s.id = b.status_id
    AND s.type_id = 3 AND s.tenant_id = 1 AND s.deleted = b'0'
SET b.status_type_id = 3, b.updater = '1', b.update_time = NOW()
WHERE b.tenant_id = 1 AND b.deleted = b'0'
  AND b.status_id IN (33, 34, 35, 36, 37, 38, 39)
  AND (b.status_type_id IS NULL OR b.status_type_id <> 3);

COMMIT;

-- 验证：应返回 4 个业务状态组，且假商机不再有无效阶段关联。
SELECT id, name, dept_ids FROM crm_business_status_type
WHERE id IN (3, 7, 8, 9) AND tenant_id = 1 AND deleted = b'0'
ORDER BY id;

SELECT type_id, id, name, percent, sort FROM crm_business_status
WHERE type_id IN (3, 7, 8, 9) AND tenant_id = 1 AND deleted = b'0'
ORDER BY type_id, sort, id;

SELECT COUNT(*) AS invalid_test_business_status_count
FROM crm_business b
LEFT JOIN crm_business_status s ON s.id = b.status_id
    AND s.type_id = b.status_type_id AND s.tenant_id = b.tenant_id AND s.deleted = b'0'
WHERE b.tenant_id = 1 AND b.creator = '1' AND b.deleted = b'0' AND s.id IS NULL;
