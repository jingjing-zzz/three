package com.meession.etm.module.crm.controller.admin.statistics.vo.forecast;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Schema(description = "管理后台 - CRM 销售预测按日统计 Response VO")
@Data
public class CrmStatisticsForecastByDateRespVO {

    @Schema(description = "日期标签，如 2026-07-18")
    private String time;

    @Schema(description = "统计周期开始时间")
    private String startTime;

    @Schema(description = "统计周期结束时间")
    private String endTime;

    @Schema(description = "当日商机金额合计")
    private BigDecimal businessAmount;

    @Schema(description = "当日预测金额合计")
    private BigDecimal forecastAmount;

    @Schema(description = "历史同期成交金额")
    private BigDecimal historyAmount;
}
