SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

INSERT IGNORE INTO `system_dict_data` (`dict_type`, `dict_value`, `dict_label`, `is_default`, `status`, `sort`, `creator`, `create_time`, `updater`, `update_time`, `tenant_id`) VALUES
('bpm_oa_leave_type', '1', '病假', '0', '0', '1', '', CURRENT_TIMESTAMP, '', CURRENT_TIMESTAMP, '0'),
('bpm_oa_leave_type', '2', '事假', '0', '0', '2', '', CURRENT_TIMESTAMP, '', CURRENT_TIMESTAMP, '0'),
('bpm_oa_leave_type', '3', '婚假', '0', '0', '3', '', CURRENT_TIMESTAMP, '', CURRENT_TIMESTAMP, '0'),
('bpm_oa_leave_type', '4', '产假', '0', '0', '4', '', CURRENT_TIMESTAMP, '', CURRENT_TIMESTAMP, '0'),
('bpm_oa_leave_type', '5', '陪产假', '0', '0', '5', '', CURRENT_TIMESTAMP, '', CURRENT_TIMESTAMP, '0'),
('bpm_oa_leave_type', '6', '丧假', '0', '0', '6', '', CURRENT_TIMESTAMP, '', CURRENT_TIMESTAMP, '0'),
('bpm_oa_leave_type', '7', '年假', '0', '0', '7', '', CURRENT_TIMESTAMP, '', CURRENT_TIMESTAMP, '0'),
('bpm_oa_leave_type', '8', '调休', '0', '0', '8', '', CURRENT_TIMESTAMP, '', CURRENT_TIMESTAMP, '0'),
('bpm_oa_leave_type', '9', '探亲假', '0', '0', '9', '', CURRENT_TIMESTAMP, '', CURRENT_TIMESTAMP, '0'),
('bpm_oa_leave_type', '10', '工伤假', '0', '0', '10', '', CURRENT_TIMESTAMP, '', CURRENT_TIMESTAMP, '0'),
('bpm_oa_leave_type', '11', '其他', '0', '0', '11', '', CURRENT_TIMESTAMP, '', CURRENT_TIMESTAMP, '0');

SET FOREIGN_KEY_CHECKS = 1;