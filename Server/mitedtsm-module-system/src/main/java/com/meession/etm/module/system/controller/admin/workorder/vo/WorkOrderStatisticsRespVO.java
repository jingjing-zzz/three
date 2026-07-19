package com.meession.etm.module.system.controller.admin.workorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "工单统计响应VO")
public class WorkOrderStatisticsRespVO {

    @Schema(description = "总工单数")
    private Long total;

    @Schema(description = "待处理数")
    private Long pending;

    @Schema(description = "处理中数")
    private Long processing;

    @Schema(description = "已完结数")
    private Long completed;

    @Schema(description = "已退回数")
    private Long rejected;

    @Schema(description = "状态分布")
    private List<StatusDistribution> statusDistribution;

    @Schema(description = "类型分布")
    private List<TypeDistribution> typeDistribution;

    @Schema(description = "优先级分布")
    private List<PriorityDistribution> priorityDistribution;

    @Schema(description = "趋势数据")
    private List<TrendData> trendData;

    @Data
    @Schema(description = "状态分布")
    public static class StatusDistribution {
        @Schema(description = "状态值")
        private Integer status;

        @Schema(description = "状态名称")
        private String statusName;

        @Schema(description = "数量")
        private Long count;

        @Schema(description = "占比")
        private Double percentage;
    }

    @Data
    @Schema(description = "类型分布")
    public static class TypeDistribution {
        @Schema(description = "类型值")
        private Integer type;

        @Schema(description = "类型名称")
        private String typeName;

        @Schema(description = "数量")
        private Long count;
    }

    @Data
    @Schema(description = "优先级分布")
    public static class PriorityDistribution {
        @Schema(description = "优先级值")
        private Integer priority;

        @Schema(description = "优先级名称")
        private String priorityName;

        @Schema(description = "数量")
        private Long count;
    }

    @Data
    @Schema(description = "趋势数据")
    public static class TrendData {
        @Schema(description = "日期")
        private String date;

        @Schema(description = "数量")
        private Long count;
    }
}