package com.meession.etm.module.system.controller.admin.workorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 工单信息 Response VO")
@Data
public class WorkOrderRespVO {

    @Schema(description = "工单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "工单号", requiredMode = Schema.RequiredMode.REQUIRED, example = "WO20260718001")
    private String orderNo;

    @Schema(description = "工单标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "系统登录异常")
    private String title;

    @Schema(description = "工单类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer type;

    @Schema(description = "工单类型名称", example = "故障报修")
    private String typeName;

    @Schema(description = "优先级", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer priority;

    @Schema(description = "优先级名称", example = "中")
    private String priorityName;

    @Schema(description = "工单状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "工单状态名称", example = "待处理")
    private String statusName;

    @Schema(description = "工单内容", example = "系统登录时提示用户名或密码错误")
    private String content;

    @Schema(description = "发起人编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Long reporterId;

    @Schema(description = "发起人姓名", example = "张三")
    private String reporterName;

    @Schema(description = "处理人编号", example = "200")
    private Long assigneeId;

    @Schema(description = "处理人姓名", example = "李四")
    private String assigneeName;

    @Schema(description = "所属部门编号", example = "10")
    private Long deptId;

    @Schema(description = "所属部门名称", example = "技术部")
    private String deptName;

    @Schema(description = "客户编号", example = "100")
    private Long customerId;

    @Schema(description = "客户名称", example = "张三")
    private String customerName;

    @Schema(description = "关联模块", example = "system")
    private String relatedModule;

    @Schema(description = "关联业务ID", example = "1000")
    private Long relatedId;

    @Schema(description = "处理备注", example = "已修复")
    private String handleNote;

    @Schema(description = "处理时间", example = "2026-07-18 10:30:00")
    private LocalDateTime handleTime;

    @Schema(description = "完结时间", example = "2026-07-18 18:00:00")
    private LocalDateTime closeTime;

    @Schema(description = "退回原因", example = "信息不全")
    private String rejectReason;

    @Schema(description = "退回时间", example = "2026-07-18 11:00:00")
    private LocalDateTime rejectTime;

    @Schema(description = "预计完成时间", example = "2026-07-20 18:00:00")
    private LocalDateTime expectedFinishTime;

    @Schema(description = "实际完成时间", example = "2026-07-18 18:00:00")
    private LocalDateTime actualFinishTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
