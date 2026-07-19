package com.meession.etm.module.erp.controller.admin.finance.vo.record;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Schema(description = "管理后台 - ERP 财务分析 Response VO")
@Data
@Accessors(chain = true)
public class ErpFinanceSummaryRespVO {

    private BigDecimal invoiceAmount;
    private BigDecimal reimbursementAmount;
    private BigDecimal refundAmount;
    private BigDecimal expenseAmount;
    private BigDecimal receiptAmount;
    private BigDecimal paymentAmount;
    private BigDecimal netAmount;
    private Long overdueCount;

}
