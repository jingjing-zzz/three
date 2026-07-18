package com.meession.etm.module.crm.controller.admin.business.vo.quotation;

import com.meession.etm.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "管理后台 - CRM 商机报价分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CrmBusinessQuotationPageReqVO extends PageParam {
    @Schema(description = "商机编号")
    private Long businessId;
    @Schema(description = "报价状态（0草稿 1已确认 2已作废）")
    private Integer status;
}
