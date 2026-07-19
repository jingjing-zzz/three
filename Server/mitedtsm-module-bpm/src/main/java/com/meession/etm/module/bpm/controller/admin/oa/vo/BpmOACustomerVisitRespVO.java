package com.meession.etm.module.bpm.controller.admin.oa.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 客户拜访申请 Response VO")
@Data
public class BpmOACustomerVisitRespVO {

    @Schema(description = "客户拜访表单主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerName;

    @Schema(description = "客户联系人")
    private String customerContact;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "拜访时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime visitTime;

    @Schema(description = "拜访地点")
    private String visitLocation;

    @Schema(description = "拜访目的", requiredMode = Schema.RequiredMode.REQUIRED)
    private String purpose;

    @Schema(description = "申请时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "流程编号")
    private String processInstanceId;

    @Schema(description = "审批结果", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

}