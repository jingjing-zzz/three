package com.meession.etm.module.erp.controller.admin.finance.vo.record;

import com.meession.etm.framework.common.validation.InEnum;
import com.meession.etm.module.erp.enums.ErpAuditStatus;
import com.meession.etm.module.erp.enums.finance.ErpFinanceRecordTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - ERP 财务单据新增/修改 Request VO")
@Data
public class ErpFinanceRecordSaveReqVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "单据类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "单据类型不能为空")
    @InEnum(ErpFinanceRecordTypeEnum.class)
    private Integer type;

    @Schema(description = "审核状态")
    @InEnum(ErpAuditStatus.class)
    private Integer status;

    @Schema(description = "业务时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "业务时间不能为空")
    private LocalDateTime recordTime;

    @Schema(description = "到期时间")
    private LocalDateTime dueTime;

    @Schema(description = "申请人")
    private Long applicantUserId;

    @Schema(description = "财务人员")
    private Long financeUserId;

    @Schema(description = "结算账户")
    private Long accountId;

    @Schema(description = "主题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "主题不能为空")
    private String subject;

    @Schema(description = "往来单位")
    private String counterparty;

    @Schema(description = "发票号码")
    private String invoiceNo;

    @Schema(description = "不含税金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "不含税金额不能为空")
    private BigDecimal amount;

    @Schema(description = "税额")
    private BigDecimal taxAmount;

    @Schema(description = "备注")
    private String remark;

}
