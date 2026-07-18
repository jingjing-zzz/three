package com.meession.etm.module.crm.controller.admin.statistics.vo.forecast;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - CRM 销售预测明细 Response VO")
@Data
public class CrmStatisticsForecastDetailRespVO {
  @Schema(description = "商机编号")
  private Long businessId;
  @Schema(description = "商机名称")
  private String businessName;
  @Schema(description = "负责人名称")
  private String ownerUserName;
  @Schema(description = "客户名称")
  private String customerName;
  @Schema(description = "阶段名称")
  private String statusName;
  @Schema(description = "阶段赢单率")
  private Integer stagePercent;
  @Schema(description = "商机总金额")
  private BigDecimal totalPrice;
  @Schema(description = "加权预测金额")
  private BigDecimal forecastAmount;
  @Schema(description = "预计成交日期")
  private String dealTime;
  @Schema(description = "结束状态")
  private Integer endStatus;
}
