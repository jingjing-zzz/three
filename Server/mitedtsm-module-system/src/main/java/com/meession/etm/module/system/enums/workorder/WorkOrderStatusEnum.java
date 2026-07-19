package com.meession.etm.module.system.enums.workorder;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkOrderStatusEnum {

    PENDING(1, "待处理"),
    PROCESSING(2, "处理中"),
    COMPLETED(3, "已完结"),
    REJECTED(4, "已退回");

    private final Integer value;
    private final String label;

    public static WorkOrderStatusEnum valueOf(Integer value) {
        for (WorkOrderStatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
