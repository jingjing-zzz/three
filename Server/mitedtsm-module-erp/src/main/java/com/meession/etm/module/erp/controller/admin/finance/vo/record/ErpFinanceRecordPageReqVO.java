package com.meession.etm.module.erp.controller.admin.finance.vo.record;

import com.meession.etm.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.meession.etm.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - ERP 财务单据分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErpFinanceRecordPageReqVO extends PageParam {

    @Schema(description = "单据编号")
    private String no;

    @Schema(description = "单据类型")
    private Integer type;

    @Schema(description = "审核状态")
    private Integer status;

    @Schema(description = "业务时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] recordTime;

    @Schema(description = "到期时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] dueTime;

    @Schema(description = "申请人")
    private Long applicantUserId;

    @Schema(description = "财务人员")
    private Long financeUserId;

    @Schema(description = "结算账户")
    private Long accountId;

    @Schema(description = "主题")
    private String subject;

    @Schema(description = "往来单位")
    private String counterparty;

    @Schema(description = "发票号码")
    private String invoiceNo;

    @Schema(description = "是否逾期")
    private Boolean overdue;

}
