package com.meession.etm.module.crm.enums.order;

import com.meession.etm.framework.common.core.ArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum CrmOrderStatusEnum implements ArrayValuable<Integer> {

    DRAFT(0, "草稿"),
    PENDING_AUDIT(10, "待审核"),
    AUDITING(20, "审核中"),
    APPROVED(30, "审核通过"),
    REJECTED(40, "审核不通过"),
    COMPLETED(50, "已完成"),
    CANCELLED(60, "已取消");

    private final Integer status;
    private final String name;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(CrmOrderStatusEnum::getStatus).toArray(Integer[]::new);

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static CrmOrderStatusEnum valueOf(Integer status) {
        for (CrmOrderStatusEnum value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return DRAFT;
    }

    public boolean canSubmit() {
        return this == DRAFT;
    }

    public boolean canApprove() {
        return this == PENDING_AUDIT || this == AUDITING;
    }

    public boolean canReject() {
        return this == PENDING_AUDIT || this == AUDITING;
    }

    public boolean canComplete() {
        return this == APPROVED;
    }

    public boolean canCancel() {
        return this != COMPLETED && this != CANCELLED;
    }

    public boolean canEdit() {
        return this == DRAFT || this == REJECTED;
    }

}