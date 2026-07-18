package com.meession.etm.module.bpm.controller.admin.oa.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.meession.etm.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 任务创建 Request VO")
@Data
public class BpmOATaskCreateReqVO {

    @Schema(description = "任务标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "任务标题不能为空")
    private String title;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "优先级：0-普通，1-重要，2-紧急")
    private Integer priority;

    @Schema(description = "指派用户编号")
    private Long assigneeId;

    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime endTime;

}