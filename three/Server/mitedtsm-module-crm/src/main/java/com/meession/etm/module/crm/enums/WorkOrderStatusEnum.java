package com.meession.etm.module.crm.enums;

import lombok.Getter;

@Getter
public enum WorkOrderStatusEnum {

    PENDING(1, "待处理"),
    PROCESSING(2, "处理中"),
    COMPLETED(3, "已完结"),
    REJECTED(4, "已退回");

    private final Integer status;
    private final String name;

    WorkOrderStatusEnum(Integer status, String name) {
        this.status = status;
        this.name = name;
    }

    public static String getName(Integer status) {
        for (WorkOrderStatusEnum value : values()) {
            if (value.getStatus().equals(status)) {
                return value.getName();
            }
        }
        return "";
    }
}