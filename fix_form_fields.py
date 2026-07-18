import mysql.connector
import json

db = mysql.connector.connect(
    host="127.0.0.1",
    port=3306,
    user="root",
    password="1234",
    database="mitedtsm_database"
)

cursor = db.cursor()

new_leave_fields = [
    json.dumps({
        "type": "select",
        "field": "type",
        "title": "请假类型",
        "info": "",
        "$required": True,
        "_fc_drag_tag": "select",
        "hidden": False,
        "display": True,
        "options": [
            {"label": "事假", "value": 1},
            {"label": "病假", "value": 2},
            {"label": "年假", "value": 3},
            {"label": "调休", "value": 4},
            {"label": "婚假", "value": 5},
            {"label": "产假", "value": 6},
            {"label": "丧假", "value": 7},
            {"label": "探亲假", "value": 8}
        ]
    }),
    json.dumps({
        "type": "datePicker",
        "field": "startTime",
        "title": "开始时间",
        "info": "",
        "$required": True,
        "_fc_drag_tag": "datePicker",
        "hidden": False,
        "display": True,
        "props": {"type": "datetime", "valueFormat": "yyyy-MM-dd HH:mm:ss"}
    }),
    json.dumps({
        "type": "datePicker",
        "field": "endTime",
        "title": "结束时间",
        "info": "",
        "$required": True,
        "_fc_drag_tag": "datePicker",
        "hidden": False,
        "display": True,
        "props": {"type": "datetime", "valueFormat": "yyyy-MM-dd HH:mm:ss"}
    }),
    json.dumps({
        "type": "textarea",
        "field": "reason",
        "title": "请假原因",
        "info": "",
        "$required": True,
        "_fc_drag_tag": "textarea",
        "hidden": False,
        "display": True
    })
]

new_trip_fields = [
    json.dumps({
        "type": "input",
        "field": "destination",
        "title": "出差地点",
        "info": "",
        "$required": True,
        "_fc_drag_tag": "input",
        "hidden": False,
        "display": True
    }),
    json.dumps({
        "type": "datePicker",
        "field": "startTime",
        "title": "出发时间",
        "info": "",
        "$required": True,
        "_fc_drag_tag": "datePicker",
        "hidden": False,
        "display": True,
        "props": {"type": "datetime", "valueFormat": "yyyy-MM-dd HH:mm:ss"}
    }),
    json.dumps({
        "type": "datePicker",
        "field": "endTime",
        "title": "结束时间",
        "info": "",
        "$required": True,
        "_fc_drag_tag": "datePicker",
        "hidden": False,
        "display": True,
        "props": {"type": "datetime", "valueFormat": "yyyy-MM-dd HH:mm:ss"}
    }),
    json.dumps({
        "type": "textarea",
        "field": "reason",
        "title": "出差事由",
        "info": "",
        "$required": True,
        "_fc_drag_tag": "textarea",
        "hidden": False,
        "display": True
    })
]

new_loan_fields = [
    json.dumps({
        "type": "inputNumber",
        "field": "amount",
        "title": "借款金额",
        "info": "单位：元",
        "$required": True,
        "_fc_drag_tag": "inputNumber",
        "hidden": False,
        "display": True
    }),
    json.dumps({
        "type": "textarea",
        "field": "reason",
        "title": "借款用途",
        "info": "",
        "$required": True,
        "_fc_drag_tag": "textarea",
        "hidden": False,
        "display": True
    }),
    json.dumps({
        "type": "datePicker",
        "field": "repaymentDate",
        "title": "还款日期",
        "info": "",
        "$required": True,
        "_fc_drag_tag": "datePicker",
        "hidden": False,
        "display": True,
        "props": {"type": "datetime", "valueFormat": "yyyy-MM-dd HH:mm:ss"}
    })
]

new_visit_fields = [
    json.dumps({
        "type": "input",
        "field": "customerName",
        "title": "客户名称",
        "info": "",
        "$required": True,
        "_fc_drag_tag": "input",
        "hidden": False,
        "display": True
    }),
    json.dumps({
        "type": "input",
        "field": "customerContact",
        "title": "联系人",
        "info": "",
        "$required": True,
        "_fc_drag_tag": "input",
        "hidden": False,
        "display": True
    }),
    json.dumps({
        "type": "input",
        "field": "contactPhone",
        "title": "联系电话",
        "info": "",
        "$required": False,
        "_fc_drag_tag": "input",
        "hidden": False,
        "display": True
    }),
    json.dumps({
        "type": "datePicker",
        "field": "visitTime",
        "title": "拜访时间",
        "info": "",
        "$required": True,
        "_fc_drag_tag": "datePicker",
        "hidden": False,
        "display": True,
        "props": {"type": "datetime", "valueFormat": "yyyy-MM-dd HH:mm:ss"}
    }),
    json.dumps({
        "type": "input",
        "field": "visitLocation",
        "title": "拜访地点",
        "info": "",
        "$required": False,
        "_fc_drag_tag": "input",
        "hidden": False,
        "display": True
    }),
    json.dumps({
        "type": "textarea",
        "field": "purpose",
        "title": "拜访目的",
        "info": "",
        "$required": True,
        "_fc_drag_tag": "textarea",
        "hidden": False,
        "display": True
    })
]

new_request_fields = [
    json.dumps({
        "type": "input",
        "field": "title",
        "title": "请示标题",
        "info": "",
        "$required": True,
        "_fc_drag_tag": "input",
        "hidden": False,
        "display": True
    }),
    json.dumps({
        "type": "textarea",
        "field": "content",
        "title": "请示内容",
        "info": "",
        "$required": True,
        "_fc_drag_tag": "textarea",
        "hidden": False,
        "display": True
    }),
    json.dumps({
        "type": "textarea",
        "field": "suggestion",
        "title": "个人建议",
        "info": "",
        "$required": False,
        "_fc_drag_tag": "textarea",
        "hidden": False,
        "display": True
    })
]

forms = [
    (64, new_leave_fields),
    (65, new_trip_fields),
    (66, new_loan_fields),
    (67, new_visit_fields),
    (68, new_request_fields)
]

for form_id, fields in forms:
    fields_json = json.dumps(fields)
    cursor.execute("UPDATE bpm_form SET fields = %s WHERE id = %s", (fields_json, form_id))
    db.commit()
    print(f"Updated form {form_id}")

cursor.execute("UPDATE bpm_process_definition_info SET form_fields = %s WHERE id = 484", (json.dumps(new_leave_fields),))
cursor.execute("UPDATE bpm_process_definition_info SET form_fields = %s WHERE id = 485", (json.dumps(new_trip_fields),))
cursor.execute("UPDATE bpm_process_definition_info SET form_fields = %s WHERE id = 486", (json.dumps(new_loan_fields),))
cursor.execute("UPDATE bpm_process_definition_info SET form_fields = %s WHERE id = 487", (json.dumps(new_visit_fields),))
cursor.execute("UPDATE bpm_process_definition_info SET form_fields = %s WHERE id = 488", (json.dumps(new_request_fields),))
db.commit()

print("Updated process definition info tables")

cursor.close()
db.close()
