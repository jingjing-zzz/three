package com.meession.etm.module.crm.controller.admin.workorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 工单 Response VO")
@Data
public class CrmWorkOrderRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "工单标题", example = "产品咨询")
    private String title;

    @Schema(description = "工单内容", example = "客户咨询产品使用问题")
    private String content;

    @Schema(description = "工单类型", example = "1")
    private Integer type;

    @Schema(description = "工单类型名称", example = "咨询")
    private String typeName;

    @Schema(description = "优先级", example = "2")
    private Integer priority;

    @Schema(description = "优先级名称", example = "高")
    private String priorityName;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "状态名称", example = "待处理")
    private String statusName;

    @Schema(description = "客户编号", example = "1")
    private Long customerId;

    @Schema(description = "客户名称", example = "张三")
    private String customerName;

    @Schema(description = "处理人编号", example = "1")
    private Long ownerUserId;

    @Schema(description = "处理人名称", example = "李四")
    private String ownerUserName;

    @Schema(description = "处理时间", example = "2024-01-01 12:00:00")
    private LocalDateTime processTime;

    @Schema(description = "完结时间", example = "2024-01-02 12:00:00")
    private LocalDateTime completeTime;

    @Schema(description = "处理结果", example = "已处理完成")
    private String result;

    @Schema(description = "退回原因", example = "信息不全")
    private String rejectReason;

    @Schema(description = "创建人", example = "admin")
    private String creator;

    @Schema(description = "创建人名称", example = "管理员")
    private String creatorName;

    @Schema(description = "创建时间", example = "2024-01-01 10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2024-01-01 10:00:00")
    private LocalDateTime updateTime;

    @Schema(description = "状态变更日志")
    private List<Log> logs;

    @Data
    public static class Log {
        @Schema(description = "变更前状态", example = "1")
        private Integer statusBefore;

        @Schema(description = "变更前状态名称", example = "待处理")
        private String statusBeforeName;

        @Schema(description = "变更后状态", example = "2")
        private Integer statusAfter;

        @Schema(description = "变更后状态名称", example = "处理中")
        private String statusAfterName;

        @Schema(description = "操作人", example = "admin")
        private String operatorName;

        @Schema(description = "操作时间", example = "2024-01-01 11:00:00")
        private LocalDateTime createTime;

        @Schema(description = "备注", example = "开始处理")
        private String remark;
    }

}