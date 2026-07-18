package com.meession.etm.module.crm.controller.admin.workorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 工单状态更新 Request VO")
@Data
public class CrmWorkOrderUpdateStatusReqVO {

    @Schema(description = "工单编号", required = true, example = "1")
    @NotNull(message = "工单编号不能为空")
    private Long id;

    @Schema(description = "目标状态", required = true, example = "2")
    @NotNull(message = "目标状态不能为空")
    private Integer status;

    @Schema(description = "处理结果", example = "已处理完成")
    private String result;

    @Schema(description = "退回原因", example = "信息不全")
    private String rejectReason;

    @Schema(description = "备注", example = "加急处理")
    private String remark;

}