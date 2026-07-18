USE mitedtsm_database;

UPDATE bpm_form SET fields = '[
  "{\"type\":\"select\",\"field\":\"type\",\"title\":\"请假类型\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"select\",\"hidden\":false,\"display\":true,\"options\":[{\"label\":\"事假\",\"value\":1},{\"label\":\"病假\",\"value\":2},{\"label\":\"年假\",\"value\":3},{\"label\":\"调休\",\"value\":4},{\"label\":\"婚假\",\"value\":5},{\"label\":\"产假\",\"value\":6},{\"label\":\"丧假\",\"value\":7},{\"label\":\"探亲假\",\"value\":8}]}",
  "{\"type\":\"datePicker\",\"field\":\"startTime\",\"title\":\"开始时间\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"datePicker\",\"hidden\":false,\"display\":true,\"props\":{\"type\":\"datetime\",\"valueFormat\":\"yyyy-MM-dd HH:mm:ss\"}}",
  "{\"type\":\"datePicker\",\"field\":\"endTime\",\"title\":\"结束时间\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"datePicker\",\"hidden\":false,\"display\":true,\"props\":{\"type\":\"datetime\",\"valueFormat\":\"yyyy-MM-dd HH:mm:ss\"}}",
  "{\"type\":\"textarea\",\"field\":\"reason\",\"title\":\"请假原因\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"textarea\",\"hidden\":false,\"display\":true}"
]' WHERE id = 64;

UPDATE bpm_form SET fields = '[
  "{\"type\":\"input\",\"field\":\"destination\",\"title\":\"出差地点\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"input\",\"hidden\":false,\"display\":true}",
  "{\"type\":\"datePicker\",\"field\":\"startTime\",\"title\":\"出发时间\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"datePicker\",\"hidden\":false,\"display\":true,\"props\":{\"type\":\"datetime\",\"valueFormat\":\"yyyy-MM-dd HH:mm:ss\"}}",
  "{\"type\":\"datePicker\",\"field\":\"endTime\",\"title\":\"结束时间\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"datePicker\",\"hidden\":false,\"display\":true,\"props\":{\"type\":\"datetime\",\"valueFormat\":\"yyyy-MM-dd HH:mm:ss\"}}",
  "{\"type\":\"textarea\",\"field\":\"reason\",\"title\":\"出差事由\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"textarea\",\"hidden\":false,\"display\":true}"
]' WHERE id = 65;

UPDATE bpm_form SET fields = '[
  "{\"type\":\"inputNumber\",\"field\":\"amount\",\"title\":\"借款金额\",\"info\":\"单位：元\",\"$required\":true,\"_fc_drag_tag\":\"inputNumber\",\"hidden\":false,\"display\":true}",
  "{\"type\":\"textarea\",\"field\":\"reason\",\"title\":\"借款用途\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"textarea\",\"hidden\":false,\"display\":true}",
  "{\"type\":\"datePicker\",\"field\":\"repaymentDate\",\"title\":\"还款日期\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"datePicker\",\"hidden\":false,\"display\":true,\"props\":{\"type\":\"datetime\",\"valueFormat\":\"yyyy-MM-dd HH:mm:ss\"}}"
]' WHERE id = 66;

UPDATE bpm_form SET fields = '[
  "{\"type\":\"input\",\"field\":\"customerName\",\"title\":\"客户名称\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"input\",\"hidden\":false,\"display\":true}",
  "{\"type\":\"input\",\"field\":\"customerContact\",\"title\":\"联系人\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"input\",\"hidden\":false,\"display\":true}",
  "{\"type\":\"input\",\"field\":\"contactPhone\",\"title\":\"联系电话\",\"info\":\"\",\"$required\":false,\"_fc_drag_tag\":\"input\",\"hidden\":false,\"display\":true}",
  "{\"type\":\"datePicker\",\"field\":\"visitTime\",\"title\":\"拜访时间\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"datePicker\",\"hidden\":false,\"display\":true,\"props\":{\"type\":\"datetime\",\"valueFormat\":\"yyyy-MM-dd HH:mm:ss\"}}",
  "{\"type\":\"input\",\"field\":\"visitLocation\",\"title\":\"拜访地点\",\"info\":\"\",\"$required\":false,\"_fc_drag_tag\":\"input\",\"hidden\":false,\"display\":true}",
  "{\"type\":\"textarea\",\"field\":\"purpose\",\"title\":\"拜访目的\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"textarea\",\"hidden\":false,\"display\":true}"
]' WHERE id = 67;

UPDATE bpm_form SET fields = '[
  "{\"type\":\"input\",\"field\":\"title\",\"title\":\"请示标题\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"input\",\"hidden\":false,\"display\":true}",
  "{\"type\":\"textarea\",\"field\":\"content\",\"title\":\"请示内容\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"textarea\",\"hidden\":false,\"display\":true}",
  "{\"type\":\"textarea\",\"field\":\"suggestion\",\"title\":\"个人建议\",\"info\":\"\",\"$required\":false,\"_fc_drag_tag\":\"textarea\",\"hidden\":false,\"display\":true}"
]' WHERE id = 68;

UPDATE bpm_process_definition_info SET form_fields = '[
  "{\"type\":\"select\",\"field\":\"type\",\"title\":\"请假类型\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"select\",\"hidden\":false,\"display\":true,\"options\":[{\"label\":\"事假\",\"value\":1},{\"label\":\"病假\",\"value\":2},{\"label\":\"年假\",\"value\":3},{\"label\":\"调休\",\"value\":4},{\"label\":\"婚假\",\"value\":5},{\"label\":\"产假\",\"value\":6},{\"label\":\"丧假\",\"value\":7},{\"label\":\"探亲假\",\"value\":8}]}",
  "{\"type\":\"datePicker\",\"field\":\"startTime\",\"title\":\"开始时间\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"datePicker\",\"hidden\":false,\"display\":true,\"props\":{\"type\":\"datetime\",\"valueFormat\":\"yyyy-MM-dd HH:mm:ss\"}}",
  "{\"type\":\"datePicker\",\"field\":\"endTime\",\"title\":\"结束时间\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"datePicker\",\"hidden\":false,\"display\":true,\"props\":{\"type\":\"datetime\",\"valueFormat\":\"yyyy-MM-dd HH:mm:ss\"}}",
  "{\"type\":\"textarea\",\"field\":\"reason\",\"title\":\"请假原因\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"textarea\",\"hidden\":false,\"display\":true}"
]' WHERE id = 484;

UPDATE bpm_process_definition_info SET form_fields = '[
  "{\"type\":\"input\",\"field\":\"destination\",\"title\":\"出差地点\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"input\",\"hidden\":false,\"display\":true}",
  "{\"type\":\"datePicker\",\"field\":\"startTime\",\"title\":\"出发时间\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"datePicker\",\"hidden\":false,\"display\":true,\"props\":{\"type\":\"datetime\",\"valueFormat\":\"yyyy-MM-dd HH:mm:ss\"}}",
  "{\"type\":\"datePicker\",\"field\":\"endTime\",\"title\":\"结束时间\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"datePicker\",\"hidden\":false,\"display\":true,\"props\":{\"type\":\"datetime\",\"valueFormat\":\"yyyy-MM-dd HH:mm:ss\"}}",
  "{\"type\":\"textarea\",\"field\":\"reason\",\"title\":\"出差事由\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"textarea\",\"hidden\":false,\"display\":true}"
]' WHERE id = 485;

UPDATE bpm_process_definition_info SET form_fields = '[
  "{\"type\":\"inputNumber\",\"field\":\"amount\",\"title\":\"借款金额\",\"info\":\"单位：元\",\"$required\":true,\"_fc_drag_tag\":\"inputNumber\",\"hidden\":false,\"display\":true}",
  "{\"type\":\"textarea\",\"field\":\"reason\",\"title\":\"借款用途\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"textarea\",\"hidden\":false,\"display\":true}",
  "{\"type\":\"datePicker\",\"field\":\"repaymentDate\",\"title\":\"还款日期\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"datePicker\",\"hidden\":false,\"display\":true,\"props\":{\"type\":\"datetime\",\"valueFormat\":\"yyyy-MM-dd HH:mm:ss\"}}"
]' WHERE id = 486;

UPDATE bpm_process_definition_info SET form_fields = '[
  "{\"type\":\"input\",\"field\":\"customerName\",\"title\":\"客户名称\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"input\",\"hidden\":false,\"display\":true}",
  "{\"type\":\"input\",\"field\":\"customerContact\",\"title\":\"联系人\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"input\",\"hidden\":false,\"display\":true}",
  "{\"type\":\"input\",\"field\":\"contactPhone\",\"title\":\"联系电话\",\"info\":\"\",\"$required\":false,\"_fc_drag_tag\":\"input\",\"hidden\":false,\"display\":true}",
  "{\"type\":\"datePicker\",\"field\":\"visitTime\",\"title\":\"拜访时间\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"datePicker\",\"hidden\":false,\"display\":true,\"props\":{\"type\":\"datetime\",\"valueFormat\":\"yyyy-MM-dd HH:mm:ss\"}}",
  "{\"type\":\"input\",\"field\":\"visitLocation\",\"title\":\"拜访地点\",\"info\":\"\",\"$required\":false,\"_fc_drag_tag\":\"input\",\"hidden\":false,\"display\":true}",
  "{\"type\":\"textarea\",\"field\":\"purpose\",\"title\":\"拜访目的\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"textarea\",\"hidden\":false,\"display\":true}"
]' WHERE id = 487;

UPDATE bpm_process_definition_info SET form_fields = '[
  "{\"type\":\"input\",\"field\":\"title\",\"title\":\"请示标题\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"input\",\"hidden\":false,\"display\":true}",
  "{\"type\":\"textarea\",\"field\":\"content\",\"title\":\"请示内容\",\"info\":\"\",\"$required\":true,\"_fc_drag_tag\":\"textarea\",\"hidden\":false,\"display\":true}",
  "{\"type\":\"textarea\",\"field\":\"suggestion\",\"title\":\"个人建议\",\"info\":\"\",\"$required\":false,\"_fc_drag_tag\":\"textarea\",\"hidden\":false,\"display\":true}"
]' WHERE id = 488;
