package com.meession.etm.module.bpm.controller.admin.oa.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 出差申请 Response VO")
@Data
public class BpmOABusinessTripRespVO {

    @Schema(description = "出差表单主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "出差目的地", requiredMode = Schema.RequiredMode.REQUIRED)
    private String destination;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime startTime;

    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime endTime;

    @Schema(description = "出差天数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long day;

    @Schema(description = "出差原因", requiredMode = Schema.RequiredMode.REQUIRED)
    private String reason;

    @Schema(description = "申请时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "流程编号")
    private String processInstanceId;

    @Schema(description = "审批结果", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

}