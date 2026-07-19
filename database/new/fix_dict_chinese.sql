DELETE FROM system_dict_data WHERE dict_type = 'crm_business_source';
DELETE FROM system_dict_type WHERE type = 'crm_business_source';

INSERT INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`) VALUES (2012, UNHEX('43524D20E59586E69CBAE69D90E6BA90'), 'crm_business_source', 0, UNHEX('43524D20E59586E69CBAE69D90E6BA90'), '1', NOW(), '1', NOW(), b'0', NULL);

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
(3054, 1, UNHEX('E4B8BBE58AA8E5BC80E58F91'), '1', 'crm_business_source', 0, 'primary', '', '', '1', NOW(), '1', NOW(), b'0'),
(3055, 2, UNHEX('E5AEA2E682A8E4BB8BE4BB8B'), '2', 'crm_business_source', 0, 'success', '', '', '1', NOW(), '1', NOW(), b'0'),
(3056, 3, UNHEX('E7BD91E7BB9CE68E9BE5B9A5'), '3', 'crm_business_source', 0, 'info', '', '', '1', NOW(), '1', NOW(), b'0'),
(3057, 4, UNHEX('E5B195E4BC9A'), '4', 'crm_business_source', 0, 'warning', '', '', '1', NOW(), '1', NOW(), b'0'),
(3058, 5, UNHEX('E794B5E8AF9DE890A5E890BD'), '5', 'crm_business_source', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'),
(3059, 6, UNHEX('E585B6E4BB96'), '6', 'crm_business_source', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0');
