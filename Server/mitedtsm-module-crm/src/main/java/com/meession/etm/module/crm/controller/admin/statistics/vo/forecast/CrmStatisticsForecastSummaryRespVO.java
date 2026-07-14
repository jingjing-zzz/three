package com.meession.etm.module.crm.controller.admin.statistics.vo.forecast;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - CRM 销售预测汇总 Response VO")
@Data
public class CrmStatisticsForecastSummaryRespVO {
  @Schema(description = "商机数（活跃，排除流失）")
  private Integer businessCount;
  @Schema(description = "商机总金额")
  private BigDecimal totalAmount;
  @Schema(description = "加权预测金额")
  private BigDecimal forecastAmount;
  @Schema(description = "成交金额")
  private BigDecimal wonAmount;
  @Schema(description = "成交商机数")
  private Integer wonCount;
  @Schema(description = "阶段赢单率（百分比）")
  private BigDecimal avgWinRate;
}
