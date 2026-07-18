package com.meession.etm.module.crm.controller.admin.marketing.vo.campaign;

import com.meession.etm.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 营销活动分页查询 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MarketingCampaignPageReqVO extends PageParam {

    @Schema(description = "活动名称", example = "春节促销活动")
    private String name;

    @Schema(description = "活动类型", example = "1")
    private Integer type;

    @Schema(description = "活动状态", example = "1")
    private Integer status;

}