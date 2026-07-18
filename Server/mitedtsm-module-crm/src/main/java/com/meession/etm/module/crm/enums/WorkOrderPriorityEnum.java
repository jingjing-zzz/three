package com.meession.etm.module.crm.enums;

import lombok.Getter;

@Getter
public enum WorkOrderPriorityEnum {

    URGENT(1, "紧急"),
    HIGH(2, "高"),
    MEDIUM(3, "中"),
    LOW(4, "低");

    private final Integer priority;
    private final String name;

    WorkOrderPriorityEnum(Integer priority, String name) {
        this.priority = priority;
        this.name = name;
    }

    public static String getName(Integer priority) {
        for (WorkOrderPriorityEnum value : values()) {
            if (value.getPriority().equals(priority)) {
                return value.getName();
            }
        }
        return "";
    }
}