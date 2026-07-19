package com.meession.etm.module.system.controller.admin.workorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 工单统计 Request VO")
@Data
public class WorkOrderStatisticsReqVO {

    @Schema(description = "工单号", example = "WO20260718001")
    private String orderNo;

    @Schema(description = "工单标题", example = "系统登录异常")
    private String title;

    @Schema(description = "工单类型", example = "1")
    private Integer type;

    @Schema(description = "优先级", example = "2")
    private Integer priority;

    @Schema(description = "发起人编号", example = "100")
    private Long reporterId;

    @Schema(description = "处理人编号", example = "200")
    private Long assigneeId;

    @Schema(description = "所属部门编号", example = "10")
    private Long deptId;

    @Schema(description = "创建时间开始")
    private LocalDateTime createTimeBegin;

    @Schema(description = "创建时间结束")
    private LocalDateTime createTimeEnd;

}