package com.meession.etm.module.system.controller.admin.workorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 工单状态流转记录 Response VO")
@Data
public class WorkOrderStatusFlowRespVO {

    @Schema(description = "记录编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "工单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long orderId;

    @Schema(description = "工单号", requiredMode = Schema.RequiredMode.REQUIRED, example = "WO20260718001")
    private String orderNo;

    @Schema(description = "原状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer fromStatus;

    @Schema(description = "原状态名称", example = "待处理")
    private String fromStatusName;

    @Schema(description = "新状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer toStatus;

    @Schema(description = "新状态名称", example = "处理中")
    private String toStatusName;

    @Schema(description = "操作人编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "200")
    private Long operatorId;

    @Schema(description = "操作人姓名", example = "李四")
    private String operatorName;

    @Schema(description = "操作备注", example = "开始处理")
    private String remark;

    @Schema(description = "操作时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
