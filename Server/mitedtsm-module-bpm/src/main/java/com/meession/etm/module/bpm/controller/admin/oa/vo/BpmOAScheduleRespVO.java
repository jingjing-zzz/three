package com.meession.etm.module.bpm.controller.admin.oa.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 日程 Response VO")
@Data
public class BpmOAScheduleRespVO {

    @Schema(description = "日程主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "日程标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "日程描述")
    private String description;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime startTime;

    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime endTime;

    @Schema(description = "地点")
    private String location;

    @Schema(description = "提醒时间")
    private LocalDateTime remindTime;

    @Schema(description = "状态：0-未开始，1-进行中，2-已完成，3-已取消", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}