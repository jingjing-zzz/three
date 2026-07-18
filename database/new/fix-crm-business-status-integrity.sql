-- 修复测试数据中商机状态组与阶段错位问题（tenant_id=1）
START TRANSACTION;

-- 现有标准销售阶段被误写入不存在的状态组 1，归属到实际使用的状态组 3。
UPDATE crm_business_status
SET type_id = 3
WHERE tenant_id = 1 AND deleted = 0 AND type_id = 1;

UPDATE crm_business_status_type
SET name = '标准销售阶段'
WHERE id = 3 AND tenant_id = 1 AND deleted = 0;

-- 保留每条商机原有阶段，仅将其状态组修正为阶段实际归属的状态组。
UPDATE crm_business b
JOIN crm_business_status s ON s.id = b.status_id
    AND s.type_id = 3 AND s.tenant_id = 1 AND s.deleted = 0
SET b.status_type_id = 3
WHERE b.tenant_id = 1 AND b.deleted = 0
  AND (b.status_type_id IS NULL OR b.status_type_id <> 3);

-- 对无法匹配阶段的历史脏数据，回退到标准销售阶段的首阶段。
UPDATE crm_business b
CROSS JOIN (
    SELECT id FROM crm_business_status
    WHERE type_id = 3 AND tenant_id = 1 AND deleted = 0
    ORDER BY sort, id
    LIMIT 1
) first_status
LEFT JOIN crm_business_status s ON s.id = b.status_id
    AND s.type_id = b.status_type_id AND s.tenant_id = 1 AND s.deleted = 0
SET b.status_type_id = 3, b.status_id = first_status.id
WHERE b.tenant_id = 1 AND b.deleted = 0 AND s.id IS NULL;

COMMIT;
