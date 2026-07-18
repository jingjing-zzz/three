package com.meession.etm.module.bpm.controller.admin.oa.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 借款申请 Response VO")
@Data
public class BpmOALoanRespVO {

    @Schema(description = "借款表单主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "借款金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "借款原因", requiredMode = Schema.RequiredMode.REQUIRED)
    private String reason;

    @Schema(description = "预计还款日期")
    private LocalDateTime repaymentDate;

    @Schema(description = "申请时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "流程编号")
    private String processInstanceId;

    @Schema(description = "审批结果", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

}