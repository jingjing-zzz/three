package com.meession.etm.module.crm.controller.admin.workorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 工单保存 Request VO")
@Data
public class CrmWorkOrderSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "工单标题", required = true, example = "产品咨询")
    @NotBlank(message = "工单标题不能为空")
    private String title;

    @Schema(description = "工单内容", example = "客户咨询产品使用问题")
    private String content;

    @Schema(description = "工单类型", required = true, example = "1")
    @NotNull(message = "工单类型不能为空")
    private Integer type;

    @Schema(description = "优先级", required = true, example = "2")
    @NotNull(message = "优先级不能为空")
    private Integer priority;

    @Schema(description = "客户编号", example = "1")
    private Long customerId;

    @Schema(description = "处理人编号", example = "1")
    private Long ownerUserId;

    @Schema(description = "备注", example = "请尽快处理")
    private String remark;

}