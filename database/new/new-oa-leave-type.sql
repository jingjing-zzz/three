-- OA 扩展请假类型。基础初始化已提供病假、事假、婚假，本脚本只补充分支新增项。
-- 采用条件插入，保留已有字典数据。
INSERT INTO `system_dict_data`
    (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `updater`, `deleted`)
SELECT source.`sort`, source.`label`, source.`value`, 'bpm_oa_leave_type', 0,
       source.`color_type`, '', NULL, '1', '1', b'0'
FROM (
    SELECT 3 AS `sort`, '产假' AS `label`, '4' AS `value`, 'warning' AS `color_type`
    UNION ALL SELECT 4, '陪产假', '5', 'warning'
    UNION ALL SELECT 5, '丧假', '6', 'danger'
    UNION ALL SELECT 6, '年假', '7', 'success'
    UNION ALL SELECT 7, '调休', '8', 'info'
    UNION ALL SELECT 8, '探亲假', '9', 'primary'
    UNION ALL SELECT 9, '工伤假', '10', 'danger'
    UNION ALL SELECT 10, '其他', '11', 'default'
) AS source
WHERE NOT EXISTS (
    SELECT 1
    FROM `system_dict_data` existing_data
    WHERE existing_data.`dict_type` = 'bpm_oa_leave_type'
      AND existing_data.`value` = source.`value`
      AND existing_data.`deleted` = b'0'
);
