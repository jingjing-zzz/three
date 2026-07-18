package com.meession.etm.module.crm.controller.admin.statistics.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * CRM 商机报表总览响应 VO
 */
@Schema(description = "管理后台 - CRM 商机报表总览响应 VO")
@Data
public class CrmStatisticsReportOverviewRespVO {

    @Schema(description = "商机状态分布（按结束状态）")
    private List<Item> statusDistribution;

    @Schema(description = "商机阶段分布（按阶段）")
    private List<StageItem> stageDistribution;

    @Schema(description = "商机来源分布")
    private List<Item> sourceDistribution;

    @Schema(description = "负责人业绩排名")
    private List<OwnerItem> ownerPerformance;

    @Schema(description = "客户商机分布（Top 10）")
    private List<CustomerItem> customerDistribution;

    @Schema(description = "总商机数")
    private Integer totalBusinessCount;

    @Schema(description = "总商机金额")
    private BigDecimal totalAmount;

    @Schema(description = "已成交金额")
    private BigDecimal wonAmount;

    @Schema(description = "预测金额")
    private BigDecimal forecastAmount;

    @Data
    @Schema(description = "通用分布项")
    public static class Item {
        @Schema(description = "名称")
        private String name;
        @Schema(description = "数量")
        private Integer value;
        @Schema(description = "金额")
        private BigDecimal amount;
    }

    @Data
    @Schema(description = "阶段分布项")
    public static class StageItem {
        @Schema(description = "阶段名称")
        private String name;
        @Schema(description = "阶段概率")
        private BigDecimal percent;
        @Schema(description = "数量")
        private Integer value;
        @Schema(description = "金额")
        private BigDecimal amount;
    }

    @Data
    @Schema(description = "负责人业绩项")
    public static class OwnerItem {
        @Schema(description = "负责人 ID")
        private Long userId;
        @Schema(description = "负责人名称")
        private String userName;
        @Schema(description = "商机数")
        private Integer businessCount;
        @Schema(description = "商机总金额")
        private BigDecimal totalAmount;
        @Schema(description = "成交金额")
        private BigDecimal wonAmount;
        @Schema(description = "预测金额")
        private BigDecimal forecastAmount;
    }

    @Data
    @Schema(description = "客户分布项")
    public static class CustomerItem {
        @Schema(description = "客户 ID")
        private Long customerId;
        @Schema(description = "客户名称")
        private String customerName;
        @Schema(description = "商机数")
        private Integer businessCount;
        @Schema(description = "商机总金额")
        private BigDecimal totalAmount;
    }
}
