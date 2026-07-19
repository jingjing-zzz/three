package com.meession.etm.module.system.enums.workorder;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkOrderPriorityEnum {

    LOW(1, "低"),
    MEDIUM(2, "中"),
    HIGH(3, "高"),
    URGENT(4, "紧急");

    private final Integer value;
    private final String label;

    public static WorkOrderPriorityEnum valueOf(Integer value) {
        for (WorkOrderPriorityEnum priority : values()) {
            if (priority.getValue().equals(value)) {
                return priority;
            }
        }
        return null;
    }
}
