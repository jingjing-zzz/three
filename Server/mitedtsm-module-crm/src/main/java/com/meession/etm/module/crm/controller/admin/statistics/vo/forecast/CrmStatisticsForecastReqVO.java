package com.meession.etm.module.crm.controller.admin.statistics.vo.forecast;

import com.meession.etm.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static com.meession.etm.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - CRM 销售预测 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CrmStatisticsForecastReqVO extends PageParam {

    @Schema(description = "负责人 ID 列表", example = "[1,2,3]")
    private List<Long> userIds;

    @Schema(description = "商机状态组 ID", example = "1")
    private Long statusTypeId;

    @Schema(description = "预计成交时间-开始", example = "2026-01-01 00:00:00")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime dealTimeStart;

    @Schema(description = "预计成交时间-结束", example = "2026-12-31 23:59:59")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime dealTimeEnd;

}
