package com.meession.etm.module.erp.enums.finance;

import com.meession.etm.framework.common.core.ArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * ERP 财务单据状态。
 */
@RequiredArgsConstructor
@Getter
public enum ErpFinanceRecordStatusEnum implements ArrayValuable<Integer> {

    PROCESS(10, "审批中"),
    APPROVE(20, "审核通过"),
    REJECT(30, "审核不通过"),
    CANCEL(40, "已取消");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(ErpFinanceRecordStatusEnum::getStatus).toArray(Integer[]::new);

    private final Integer status;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
