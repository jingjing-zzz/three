-- CRM 客户域字段与字典增量：可在已有数据库重复执行。
SET NAMES utf8mb4;

ALTER TABLE crm_customer
    ADD COLUMN IF NOT EXISTS star INT NULL COMMENT '客户星级（1-5）' AFTER source,
    ADD COLUMN IF NOT EXISTS status INT NULL COMMENT '客户状态' AFTER star;

INSERT INTO system_dict_type (name, type, status, remark, creator, updater, deleted)
SELECT '客户星级', 'crm_customer_star', 0, 'CRM 客户星级', '1', '1', b'0'
WHERE NOT EXISTS (
    SELECT 1 FROM system_dict_type WHERE type = 'crm_customer_star' AND deleted = b'0'
);

INSERT INTO system_dict_type (name, type, status, remark, creator, updater, deleted)
SELECT '客户状态', 'crm_customer_status', 0, 'CRM 客户状态', '1', '1', b'0'
WHERE NOT EXISTS (
    SELECT 1 FROM system_dict_type WHERE type = 'crm_customer_status' AND deleted = b'0'
);

INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, creator, updater, deleted)
SELECT 1, '一星', '1', 'crm_customer_star', 0, 'default', '1', '1', b'0'
WHERE NOT EXISTS (SELECT 1 FROM system_dict_data WHERE dict_type = 'crm_customer_star' AND value = '1' AND deleted = b'0');
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, creator, updater, deleted)
SELECT 2, '二星', '2', 'crm_customer_star', 0, 'default', '1', '1', b'0'
WHERE NOT EXISTS (SELECT 1 FROM system_dict_data WHERE dict_type = 'crm_customer_star' AND value = '2' AND deleted = b'0');
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, creator, updater, deleted)
SELECT 3, '三星', '3', 'crm_customer_star', 0, 'default', '1', '1', b'0'
WHERE NOT EXISTS (SELECT 1 FROM system_dict_data WHERE dict_type = 'crm_customer_star' AND value = '3' AND deleted = b'0');
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, creator, updater, deleted)
SELECT 4, '四星', '4', 'crm_customer_star', 0, 'default', '1', '1', b'0'
WHERE NOT EXISTS (SELECT 1 FROM system_dict_data WHERE dict_type = 'crm_customer_star' AND value = '4' AND deleted = b'0');
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, creator, updater, deleted)
SELECT 5, '五星', '5', 'crm_customer_star', 0, 'default', '1', '1', b'0'
WHERE NOT EXISTS (SELECT 1 FROM system_dict_data WHERE dict_type = 'crm_customer_star' AND value = '5' AND deleted = b'0');

INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, creator, updater, deleted)
SELECT 1, '潜在客户', '1', 'crm_customer_status', 0, 'info', '1', '1', b'0'
WHERE NOT EXISTS (SELECT 1 FROM system_dict_data WHERE dict_type = 'crm_customer_status' AND value = '1' AND deleted = b'0');
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, creator, updater, deleted)
SELECT 2, '意向客户', '2', 'crm_customer_status', 0, 'primary', '1', '1', b'0'
WHERE NOT EXISTS (SELECT 1 FROM system_dict_data WHERE dict_type = 'crm_customer_status' AND value = '2' AND deleted = b'0');
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, creator, updater, deleted)
SELECT 3, '成交客户', '3', 'crm_customer_status', 0, 'success', '1', '1', b'0'
WHERE NOT EXISTS (SELECT 1 FROM system_dict_data WHERE dict_type = 'crm_customer_status' AND value = '3' AND deleted = b'0');
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, creator, updater, deleted)
SELECT 4, '流失客户', '4', 'crm_customer_status', 0, 'danger', '1', '1', b'0'
WHERE NOT EXISTS (SELECT 1 FROM system_dict_data WHERE dict_type = 'crm_customer_status' AND value = '4' AND deleted = b'0');
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, creator, updater, deleted)
SELECT 5, '休眠客户', '5', 'crm_customer_status', 0, 'warning', '1', '1', b'0'
WHERE NOT EXISTS (SELECT 1 FROM system_dict_data WHERE dict_type = 'crm_customer_status' AND value = '5' AND deleted = b'0');
