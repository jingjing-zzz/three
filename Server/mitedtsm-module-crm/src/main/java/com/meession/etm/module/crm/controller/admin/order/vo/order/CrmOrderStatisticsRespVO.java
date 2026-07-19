package com.meession.etm.module.crm.controller.admin.order.vo.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CrmOrderStatisticsRespVO {

    private Long totalCount;

    private BigDecimal totalAmount;

    private Long pendingAuditCount;

    private Long approvedCount;

    private Long completedCount;

    private List<StatusCount> statusCounts;

    private List<MonthlyAmount> monthlyAmounts;

    @Data
    public static class StatusCount {
        private Integer status;
        private String statusName;
        private Long count;
        private BigDecimal amount;
    }

    @Data
    public static class MonthlyAmount {
        private String month;
        private Long count;
        private BigDecimal amount;
    }

}