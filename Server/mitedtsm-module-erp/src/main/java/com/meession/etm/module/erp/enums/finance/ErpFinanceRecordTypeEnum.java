package com.meession.etm.module.erp.enums.finance;

import com.meession.etm.framework.common.core.ArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * ERP 财务单据类型。
 */
@RequiredArgsConstructor
@Getter
public enum ErpFinanceRecordTypeEnum implements ArrayValuable<Integer> {

    INVOICE(10, "发票", "FP"),
    REIMBURSEMENT(20, "报销", "BX"),
    REFUND(30, "退款", "TK"),
    EXPENSE(40, "费用", "FY");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(ErpFinanceRecordTypeEnum::getType).toArray(Integer[]::new);

    private final Integer type;
    private final String name;
    private final String noPrefix;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static ErpFinanceRecordTypeEnum of(Integer type) {
        return Arrays.stream(values()).filter(item -> item.type.equals(type)).findFirst().orElse(null);
    }

}
