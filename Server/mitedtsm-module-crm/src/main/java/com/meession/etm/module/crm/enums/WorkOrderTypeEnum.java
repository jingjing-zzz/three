package com.meession.etm.module.crm.enums;

import lombok.Getter;

@Getter
public enum WorkOrderTypeEnum {

    CONSULTATION(1, "咨询"),
    COMPLAINT(2, "投诉"),
    SUGGESTION(3, "建议"),
    PROBLEM(4, "问题");

    private final Integer type;
    private final String name;

    WorkOrderTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public static String getName(Integer type) {
        for (WorkOrderTypeEnum value : values()) {
            if (value.getType().equals(type)) {
                return value.getName();
            }
        }
        return "";
    }
}