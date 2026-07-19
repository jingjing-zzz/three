package com.meession.etm.module.system.controller.admin.workorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 工单类型 Response VO")
@Data
public class WorkOrderTypeRespVO {

    @Schema(description = "类型编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "FAULT_REPAIR")
    private String code;

    @Schema(description = "类型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "故障报修")
    private String name;

    @Schema(description = "类型描述", example = "系统故障、设备故障等需要维修的工单")
    private String description;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
