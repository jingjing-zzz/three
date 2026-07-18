-- CRM 商机来源字典 (crm_business_source)
-- 适用于直接执行或追加到 ruoyi-vue-pro.sql 初始化脚本

-- 1. 字典类型
INSERT IGNORE INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`) VALUES (2012, 'CRM 商机来源', 'crm_business_source', 0, 'CRM 商机来源', '1', NOW(), '1', NOW(), b'0', NULL);

-- 2. 字典数据
INSERT IGNORE INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
(3054, 1, '主动开发', '1', 'crm_business_source', 0, 'primary', '', '', '1', NOW(), '1', NOW(), b'0'),
(3055, 2, '客户介绍', '2', 'crm_business_source', 0, 'success', '', '', '1', NOW(), '1', NOW(), b'0'),
(3056, 3, '网络推广', '3', 'crm_business_source', 0, 'info', '', '', '1', NOW(), '1', NOW(), b'0'),
(3057, 4, '展会', '4', 'crm_business_source', 0, 'warning', '', '', '1', NOW(), '1', NOW(), b'0'),
(3058, 5, '电话营销', '5', 'crm_business_source', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'),
(3059, 6, '其他', '6', 'crm_business_source', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0');
