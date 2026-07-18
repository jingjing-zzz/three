package com.meession.etm.module.crm.controller.admin.statistics.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.meession.etm.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * CRM 商机报表查询 Request VO
 */
@Schema(description = "管理后台 - CRM 商机报表查询 Request VO")
@Data
public class CrmStatisticsReportReqVO {

    @Schema(description = "负责人用户 id", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1")
    private Long userId;

    @Schema(description = "预计成交时间范围", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Size(min = 2, max = 2, message = "请选择时间范围")
    private LocalDateTime[] times;

    @Schema(description = "是否包含已流失商机（默认 false，与销售预测口径一致）",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "false")
    private Boolean includeLost;

}
