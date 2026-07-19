package com.meession.etm.module.system.controller.admin.workorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "管理后台 - 工单创建/修改 Request VO")
@Data
public class WorkOrderSaveReqVO {

    @Schema(description = "工单编号", example = "1024")
    private Long id;

    @Schema(description = "工单标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "系统登录异常")
    @NotBlank(message = "工单标题不能为空")
    @Size(max = 200, message = "工单标题不能超过200个字符")
    private String title;

    @Schema(description = "工单类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "工单类型不能为空")
    private Integer type;

    @Schema(description = "优先级", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "优先级不能为空")
    private Integer priority;

    @Schema(description = "工单内容", example = "系统登录时提示用户名或密码错误")
    private String content;

    @Schema(description = "发起人编号", example = "100")
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

    @Schema(description = "预计完成时间", example = "2026-07-20 18:00:00")
    private String expectedFinishTime;

}
