package com.meession.etm.module.crm.controller.admin.statistics.vo.funnel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Schema(description = "管理后台 - CRM 新增商机分析(按日期) VO")
@Data
@Accessors(chain = true)
public class CrmStatisticsBusinessSummaryByDateRespVO {

    @Schema(description = "时间轴", requiredMode = Schema.RequiredMode.REQUIRED, example = "202401")
    private String time;

    @Schema(description = "统计周期开始时间")
    private String startTime;

    @Schema(description = "统计周期结束时间")
    private String endTime;

    @Schema(description = "新增商机数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long businessCreateCount;

    @Schema(description = "新增商机金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private BigDecimal totalPrice;

}
