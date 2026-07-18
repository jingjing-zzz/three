package com.meession.etm.module.bpm.controller.admin.oa.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 任务 Response VO")
@Data
public class BpmOATaskRespVO {

    @Schema(description = "任务主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "任务标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "优先级：0-普通，1-重要，2-紧急", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer priority;

    @Schema(description = "状态：0-待处理，1-进行中，2-已完成，3-已取消", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    @Schema(description = "指派用户编号")
    private Long assigneeId;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}