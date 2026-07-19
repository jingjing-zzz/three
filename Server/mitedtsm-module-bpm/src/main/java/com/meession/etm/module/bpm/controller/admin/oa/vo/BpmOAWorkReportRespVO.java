package com.meession.etm.module.bpm.controller.admin.oa.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 工作报告 Response VO")
@Data
public class BpmOAWorkReportRespVO {

    @Schema(description = "工作报告主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "报告类型：1-日报，2-周报，3-月报", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer type;

    @Schema(description = "报告标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "报告内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "报告日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate reportDate;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    @Schema(description = "审批意见")
    private String reviewComment;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "流程编号")
    private String processInstanceId;

}