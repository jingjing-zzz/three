package com.meession.etm.module.crm.controller.admin.workorder.vo;

import com.meession.etm.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 工单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CrmWorkOrderPageReqVO extends PageParam {

    @Schema(description = "工单标题", example = "产品咨询")
    private String title;

    @Schema(description = "工单类型", example = "1")
    private Integer type;

    @Schema(description = "优先级", example = "2")
    private Integer priority;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "客户编号", example = "1")
    private Long customerId;

    @Schema(description = "处理人编号", example = "1")
    private Long ownerUserId;

}