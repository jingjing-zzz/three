package com.meession.etm.module.crm.controller.admin.workorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "管理后台 - 工单统计 Response VO")
@Data
public class CrmWorkOrderStatisticsRespVO {

    @Schema(description = "工单总数", example = "100")
    private Long totalCount;

    @Schema(description = "待处理数量", example = "20")
    private Long pendingCount;

    @Schema(description = "处理中数量", example = "30")
    private Long processingCount;

    @Schema(description = "已完结数量", example = "45")
    private Long completedCount;

    @Schema(description = "已退回数量", example = "5")
    private Long rejectedCount;

    @Schema(description = "按类型统计")
    private List<TypeStatistics> typeStatistics;

    @Schema(description = "按优先级统计")
    private List<PriorityStatistics> priorityStatistics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypeStatistics {
        @Schema(description = "类型", example = "1")
        private Integer type;

        @Schema(description = "类型名称", example = "咨询")
        private String typeName;

        @Schema(description = "数量", example = "30")
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriorityStatistics {
        @Schema(description = "优先级", example = "2")
        private Integer priority;

        @Schema(description = "优先级名称", example = "高")
        private String priorityName;

        @Schema(description = "数量", example = "40")
        private Long count;
    }

}