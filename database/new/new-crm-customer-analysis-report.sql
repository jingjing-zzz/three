-- ============================================================
-- CRM 客户分析报表 - 积木报表 SQL 数据集
-- 说明：为积木报表(Jimu Report)预建客户分析 SQL 数据集，
--       用户登录后进入 /jmreport/list 即可使用这些数据集创建报表。
-- 依赖：crm_customer, crm_contract, crm_business, crm_contact,
--       crm_follow_up_record, system_dict_data, system_users 表必须存在。
-- ============================================================

-- ============================================================
-- 1. 客户总览统计（客户总数、本月新增、成交客户数、成交率）
-- ============================================================
INSERT INTO `jimu_report_db` (`id`, `jimu_report_id`, `create_by`, `create_time`, `db_code`, `db_ch_name`, `db_type`, `db_dyn_sql`, `db_source`, `db_source_type`, `is_list`, `is_page`)
VALUES (
    REPLACE(UUID(), '-', ''),
    NULL,
    'admin',
    NOW(),
    'crm_customer_overview',
    '客户总览统计',
    'sql',
    'SELECT
        COUNT(*) AS total_customers,
        SUM(CASE WHEN DATE_FORMAT(create_time, ''%Y-%m'') = DATE_FORMAT(NOW(), ''%Y-%m'') THEN 1 ELSE 0 END) AS new_this_month,
        SUM(CASE WHEN deal_status = 1 THEN 1 ELSE 0 END) AS deal_customers,
        ROUND(SUM(CASE WHEN deal_status = 1 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS deal_rate,
        SUM(CASE WHEN follow_up_status = 1 THEN 1 ELSE 0 END) AS following_customers,
        SUM(CASE WHEN lock_status = 1 THEN 1 ELSE 0 END) AS locked_customers
    FROM crm_customer
    WHERE deleted = 0',
    NULL,
    'MYSQL',
    '1',
    '0'
);

-- ============================================================
-- 2. 客户来源分析（按客户来源统计数量和占比）
-- ============================================================
INSERT INTO `jimu_report_db` (`id`, `jimu_report_id`, `create_by`, `create_time`, `db_code`, `db_ch_name`, `db_type`, `db_dyn_sql`, `db_source`, `db_source_type`, `is_list`, `is_page`)
VALUES (
    REPLACE(UUID(), '-', ''),
    NULL,
    'admin',
    NOW(),
    'crm_customer_source_analysis',
    '客户来源分析',
    'sql',
    'SELECT
        d.label AS source_name,
        COUNT(c.id) AS customer_count,
        ROUND(COUNT(c.id) * 100.0 / (SELECT COUNT(*) FROM crm_customer WHERE deleted = 0), 2) AS percentage
    FROM crm_customer c
    LEFT JOIN system_dict_data d ON c.source = d.value AND d.dict_type = ''crm_customer_source'' AND d.deleted = 0
    WHERE c.deleted = 0
    GROUP BY c.source, d.label
    ORDER BY customer_count DESC',
    NULL,
    'MYSQL',
    '1',
    '0'
);

-- ============================================================
-- 3. 客户等级分布（按客户等级统计）
-- ============================================================
INSERT INTO `jimu_report_db` (`id`, `jimu_report_id`, `create_by`, `create_time`, `db_code`, `db_ch_name`, `db_type`, `db_dyn_sql`, `db_source`, `db_source_type`, `is_list`, `is_page`)
VALUES (
    REPLACE(UUID(), '-', ''),
    NULL,
    'admin',
    NOW(),
    'crm_customer_level_distribution',
    '客户等级分布',
    'sql',
    'SELECT
        d.label AS level_name,
        COUNT(c.id) AS customer_count,
        ROUND(COUNT(c.id) * 100.0 / (SELECT COUNT(*) FROM crm_customer WHERE deleted = 0), 2) AS percentage,
        SUM(CASE WHEN c.deal_status = 1 THEN 1 ELSE 0 END) AS deal_count
    FROM crm_customer c
    LEFT JOIN system_dict_data d ON c.level = d.value AND d.dict_type = ''crm_customer_level'' AND d.deleted = 0
    WHERE c.deleted = 0
    GROUP BY c.level, d.label
    ORDER BY customer_count DESC',
    NULL,
    'MYSQL',
    '1',
    '0'
);

-- ============================================================
-- 4. 客户行业分布（按所属行业统计）
-- ============================================================
INSERT INTO `jimu_report_db` (`id`, `jimu_report_id`, `create_by`, `create_time`, `db_code`, `db_ch_name`, `db_type`, `db_dyn_sql`, `db_source`, `db_source_type`, `is_list`, `is_page`)
VALUES (
    REPLACE(UUID(), '-', ''),
    NULL,
    'admin',
    NOW(),
    'crm_customer_industry_distribution',
    '客户行业分布',
    'sql',
    'SELECT
        d.label AS industry_name,
        COUNT(c.id) AS customer_count,
        ROUND(COUNT(c.id) * 100.0 / (SELECT COUNT(*) FROM crm_customer WHERE deleted = 0), 2) AS percentage
    FROM crm_customer c
    LEFT JOIN system_dict_data d ON c.industry_id = d.value AND d.dict_type = ''crm_customer_industry'' AND d.deleted = 0
    WHERE c.deleted = 0
    GROUP BY c.industry_id, d.label
    ORDER BY customer_count DESC',
    NULL,
    'MYSQL',
    '1',
    '0'
);

-- ============================================================
-- 5. 客户星级分布
-- ============================================================
INSERT INTO `jimu_report_db` (`id`, `jimu_report_id`, `create_by`, `create_time`, `db_code`, `db_ch_name`, `db_type`, `db_dyn_sql`, `db_source`, `db_source_type`, `is_list`, `is_page`)
VALUES (
    REPLACE(UUID(), '-', ''),
    NULL,
    'admin',
    NOW(),
    'crm_customer_star_distribution',
    '客户星级分布',
    'sql',
    'SELECT
        CASE
            WHEN c.star = 1 THEN ''1星''
            WHEN c.star = 2 THEN ''2星''
            WHEN c.star = 3 THEN ''3星''
            WHEN c.star = 4 THEN ''4星''
            WHEN c.star = 5 THEN ''5星''
            ELSE ''未评级''
        END AS star_level,
        COUNT(c.id) AS customer_count,
        ROUND(COUNT(c.id) * 100.0 / (SELECT COUNT(*) FROM crm_customer WHERE deleted = 0), 2) AS percentage
    FROM crm_customer c
    WHERE c.deleted = 0
    GROUP BY c.star
    ORDER BY c.star ASC',
    NULL,
    'MYSQL',
    '1',
    '0'
);

-- ============================================================
-- 6. 客户地区分布（按省份/区域统计）
-- ============================================================
INSERT INTO `jimu_report_db` (`id`, `jimu_report_id`, `create_by`, `create_time`, `db_code`, `db_ch_name`, `db_type`, `db_dyn_sql`, `db_source`, `db_source_type`, `is_list`, `is_page`)
VALUES (
    REPLACE(UUID(), '-', ''),
    NULL,
    'admin',
    NOW(),
    'crm_customer_area_distribution',
    '客户地区分布',
    'sql',
    'SELECT
        COALESCE(a.name, ''未知地区'') AS area_name,
        COUNT(c.id) AS customer_count,
        SUM(CASE WHEN c.deal_status = 1 THEN 1 ELSE 0 END) AS deal_count
    FROM crm_customer c
    LEFT JOIN system_area a ON c.area_id = a.id
    WHERE c.deleted = 0
    GROUP BY c.area_id, a.name
    ORDER BY customer_count DESC
    LIMIT 30',
    NULL,
    'MYSQL',
    '1',
    '0'
);

-- ============================================================
-- 7. 客户月度新增趋势（按月份统计新增客户数）
-- ============================================================
INSERT INTO `jimu_report_db` (`id`, `jimu_report_id`, `create_by`, `create_time`, `db_code`, `db_ch_name`, `db_type`, `db_dyn_sql`, `db_source`, `db_source_type`, `is_list`, `is_page`)
VALUES (
    REPLACE(UUID(), '-', ''),
    NULL,
    'admin',
    NOW(),
    'crm_customer_monthly_trend',
    '客户月度新增趋势',
    'sql',
    'SELECT
        DATE_FORMAT(create_time, ''%Y-%m'') AS month,
        COUNT(*) AS new_customer_count,
        SUM(CASE WHEN deal_status = 1 THEN 1 ELSE 0 END) AS deal_count
    FROM crm_customer
    WHERE deleted = 0
      AND create_time >= DATE_SUB(NOW(), INTERVAL 12 MONTH)
    GROUP BY DATE_FORMAT(create_time, ''%Y-%m'')
    ORDER BY month ASC',
    NULL,
    'MYSQL',
    '1',
    '0'
);

-- ============================================================
-- 8. 客户负责人统计（按销售负责人统计客户数和成交情况）
-- ============================================================
INSERT INTO `jimu_report_db` (`id`, `jimu_report_id`, `create_by`, `create_time`, `db_code`, `db_ch_name`, `db_type`, `db_dyn_sql`, `db_source`, `db_source_type`, `is_list`, `is_page`)
VALUES (
    REPLACE(UUID(), '-', ''),
    NULL,
    'admin',
    NOW(),
    'crm_customer_by_owner',
    '客户负责人统计',
    'sql',
    'SELECT
        u.nickname AS owner_name,
        COUNT(c.id) AS customer_count,
        SUM(CASE WHEN c.deal_status = 1 THEN 1 ELSE 0 END) AS deal_count,
        ROUND(AVG(CASE WHEN c.deal_status = 1 THEN 100 ELSE 0 END), 2) AS deal_rate,
        SUM(CASE WHEN c.follow_up_status = 1 THEN 1 ELSE 0 END) AS following_count,
        SUM(CASE WHEN c.lock_status = 1 THEN 1 ELSE 0 END) AS locked_count
    FROM crm_customer c
    LEFT JOIN system_users u ON c.owner_user_id = u.id
    WHERE c.deleted = 0
    GROUP BY c.owner_user_id, u.nickname
    ORDER BY customer_count DESC',
    NULL,
    'MYSQL',
    '1',
    '0'
);

-- ============================================================
-- 9. 客户成交统计（按客户统计合同金额和回款情况）
-- ============================================================
INSERT INTO `jimu_report_db` (`id`, `jimu_report_id`, `create_by`, `create_time`, `db_code`, `db_ch_name`, `db_type`, `db_dyn_sql`, `db_source`, `db_source_type`, `is_list`, `is_page`)
VALUES (
    REPLACE(UUID(), '-', ''),
    NULL,
    'admin',
    NOW(),
    'crm_customer_deal_statistics',
    '客户成交统计',
    'sql',
    'SELECT
        c.name AS customer_name,
        c.mobile,
        d1.label AS level_name,
        COUNT(DISTINCT ct.id) AS contract_count,
        COALESCE(SUM(ct.total_price), 0) AS total_contract_amount,
        COALESCE(SUM(CASE WHEN r.audit_status = 20 THEN r.price ELSE 0 END), 0) AS total_received_amount,
        c.create_time AS customer_create_time,
        MIN(ct.create_time) AS first_contract_time
    FROM crm_customer c
    LEFT JOIN crm_contract ct ON c.id = ct.customer_id AND ct.deleted = 0
    LEFT JOIN crm_receivable r ON c.id = r.customer_id AND r.deleted = 0
    LEFT JOIN system_dict_data d1 ON c.level = d1.value AND d1.dict_type = ''crm_customer_level'' AND d1.deleted = 0
    WHERE c.deleted = 0 AND c.deal_status = 1
    GROUP BY c.id, c.name, c.mobile, d1.label, c.create_time
    ORDER BY total_contract_amount DESC
    LIMIT 100',
    NULL,
    'MYSQL',
    '1',
    '0'
);

-- ============================================================
-- 10. 客户跟进分析（按客户统计跟进次数和最近跟进时间）
-- ============================================================
INSERT INTO `jimu_report_db` (`id`, `jimu_report_id`, `create_by`, `create_time`, `db_code`, `db_ch_name`, `db_type`, `db_dyn_sql`, `db_source`, `db_source_type`, `is_list`, `is_page`)
VALUES (
    REPLACE(UUID(), '-', ''),
    NULL,
    'admin',
    NOW(),
    'crm_customer_followup_analysis',
    '客户跟进分析',
    'sql',
    'SELECT
        c.name AS customer_name,
        u.nickname AS owner_name,
        COUNT(f.id) AS follow_up_count,
        MAX(f.create_time) AS last_follow_up_time,
        DATEDIFF(NOW(), MAX(f.create_time)) AS days_since_last_followup,
        c.contact_next_time AS next_contact_time
    FROM crm_customer c
    LEFT JOIN crm_follow_up_record f ON c.id = f.biz_id AND f.biz_type = 1 AND f.deleted = 0
    LEFT JOIN system_users u ON c.owner_user_id = u.id
    WHERE c.deleted = 0
    GROUP BY c.id, c.name, u.nickname, c.contact_next_time
    ORDER BY days_since_last_followup DESC
    LIMIT 100',
    NULL,
    'MYSQL',
    '1',
    '0'
);

-- ============================================================
-- 11. 公海客户分析（公海客户统计）
-- ============================================================
INSERT INTO `jimu_report_db` (`id`, `jimu_report_id`, `create_by`, `create_time`, `db_code`, `db_ch_name`, `db_type`, `db_dyn_sql`, `db_source`, `db_source_type`, `is_list`, `is_page`)
VALUES (
    REPLACE(UUID(), '-', ''),
    NULL,
    'admin',
    NOW(),
    'crm_customer_pool_analysis',
    '公海客户分析',
    'sql',
    'SELECT
        d1.label AS source_name,
        d2.label AS level_name,
        COUNT(c.id) AS pool_customer_count,
        MAX(c.owner_time) AS last_owner_time
    FROM crm_customer c
    LEFT JOIN system_dict_data d1 ON c.source = d1.value AND d1.dict_type = ''crm_customer_source'' AND d1.deleted = 0
    LEFT JOIN system_dict_data d2 ON c.level = d2.value AND d2.dict_type = ''crm_customer_level'' AND d2.deleted = 0
    WHERE c.deleted = 0 AND c.owner_user_id IS NULL
    GROUP BY c.source, c.level, d1.label, d2.label
    ORDER BY pool_customer_count DESC',
    NULL,
    'MYSQL',
    '1',
    '0'
);

-- ============================================================
-- 12. 客户活跃度分析（基于跟进记录的活跃度评分）
-- ============================================================
INSERT INTO `jimu_report_db` (`id`, `jimu_report_id`, `create_by`, `create_time`, `db_code`, `db_ch_name`, `db_type`, `db_dyn_sql`, `db_source`, `db_source_type`, `is_list`, `is_page`)
VALUES (
    REPLACE(UUID(), '-', ''),
    NULL,
    'admin',
    NOW(),
    'crm_customer_activity_analysis',
    '客户活跃度分析',
    'sql',
    'SELECT
        c.name AS customer_name,
        d1.label AS level_name,
        COUNT(f.id) AS total_follow_ups,
        SUM(CASE WHEN f.create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN 1 ELSE 0 END) AS follow_ups_last_30days,
        CASE
            WHEN COUNT(f.id) >= 20 THEN ''高活跃''
            WHEN COUNT(f.id) >= 10 THEN ''中活跃''
            WHEN COUNT(f.id) >= 1 THEN ''低活跃''
            ELSE ''无互动''
        END AS activity_level
    FROM crm_customer c
    LEFT JOIN crm_follow_up_record f ON c.id = f.biz_id AND f.biz_type = 1 AND f.deleted = 0
    LEFT JOIN system_dict_data d1 ON c.level = d1.value AND d1.dict_type = ''crm_customer_level'' AND d1.deleted = 0
    WHERE c.deleted = 0
    GROUP BY c.id, c.name, d1.label
    ORDER BY total_follow_ups DESC
    LIMIT 100',
    NULL,
    'MYSQL',
    '1',
    '0'
);

-- ============================================================
-- 13. 添加「客户报表分析」菜单项（挂载到「报表管理」下）
-- ============================================================
-- 菜单项：客户报表分析
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (8010, '客户报表分析', '', 2, 3, 1281, 'customer-analysis-report', 'ep:data-analysis', 'report/customerAnalysis/index', 'CrmCustomerAnalysisReport', 0, b'1', b'1', b'0', '1', NOW(), '1', NOW(), b'0');

-- 权限：查询客户分析报表
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (8011, '查询客户分析', 'report:customer-analysis:query', 3, 1, 8010, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');
