package com.meession.etm.module.system.enums.workorder;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkOrderTypeEnum {

    FAULT_REPAIR(1, "故障报修"),
    SERVICE_REQUEST(2, "服务请求"),
    CONSULT_COMPLAINT(3, "咨询投诉"),
    CHANGE_APPLICATION(4, "变更申请");

    private final Integer value;
    private final String label;

    public static WorkOrderTypeEnum valueOf(Integer value) {
        for (WorkOrderTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
