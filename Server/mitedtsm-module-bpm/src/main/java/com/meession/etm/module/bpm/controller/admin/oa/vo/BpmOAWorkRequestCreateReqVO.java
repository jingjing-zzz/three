package com.meession.etm.module.bpm.controller.admin.oa.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 请示审批创建 Request VO")
@Data
public class BpmOAWorkRequestCreateReqVO {

    @Schema(description = "请示标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请示标题不能为空")
    private String title;

    @Schema(description = "请示内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请示内容不能为空")
    private String content;

    @Schema(description = "发起人自选审批人 Map")
    private Map<String, List<Long>> startUserSelectAssignees;

}