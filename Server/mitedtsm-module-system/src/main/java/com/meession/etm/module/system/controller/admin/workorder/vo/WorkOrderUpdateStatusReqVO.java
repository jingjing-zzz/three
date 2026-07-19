package com.meession.etm.module.system.controller.admin.workorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 工单状态更新 Request VO")
@Data
public class WorkOrderUpdateStatusReqVO {

    @Schema(description = "工单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "工单编号不能为空")
    private Long id;

    @Schema(description = "新状态", example = "2")
    private Integer status;

    @Schema(description = "处理备注", example = "正在处理中")
    private String handleNote;

    @Schema(description = "退回原因", example = "信息不全，请补充")
    private String rejectReason;

    @Schema(description = "处理人编号", example = "200")
    private Long assigneeId;

    @Schema(description = "处理人姓名", example = "李四")
    private String assigneeName;

}
