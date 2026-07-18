package com.meession.etm.module.bpm.controller.admin.oa.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 请示审批 Response VO")
@Data
public class BpmOAWorkRequestRespVO {

    @Schema(description = "请示审批表单主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "请示标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "请示内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "申请时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "流程编号")
    private String processInstanceId;

    @Schema(description = "审批结果", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

}