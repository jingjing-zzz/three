package com.meession.etm.module.erp.controller.admin.finance.vo.record;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - ERP 财务单据 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ErpFinanceRecordRespVO {

    @Schema(description = "编号")
    private Long id;

    @ExcelProperty("单据编号")
    private String no;

    @ExcelProperty("单据类型")
    private Integer type;

    @ExcelProperty("审核状态")
    private Integer status;

    @ExcelProperty("业务时间")
    private LocalDateTime recordTime;

    @ExcelProperty("到期时间")
    private LocalDateTime dueTime;

    @ExcelProperty("是否逾期")
    private Boolean overdue;

    private Long applicantUserId;
    @ExcelProperty("申请人")
    private String applicantUserName;

    private Long financeUserId;
    @ExcelProperty("财务人员")
    private String financeUserName;

    private Long accountId;
    @ExcelProperty("结算账户")
    private String accountName;

    @ExcelProperty("主题")
    private String subject;

    @ExcelProperty("往来单位")
    private String counterparty;

    @ExcelProperty("发票号码")
    private String invoiceNo;

    @ExcelProperty("不含税金额")
    private BigDecimal amount;

    @ExcelProperty("税额")
    private BigDecimal taxAmount;

    @ExcelProperty("价税合计")
    private BigDecimal totalAmount;

    @ExcelProperty("流程编号")
    private String processInstanceId;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    private String creator;
    @ExcelProperty("创建人")
    private String creatorName;

}
