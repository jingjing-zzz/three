package com.meession.etm.module.crm.controller.admin.marketing.vo.approval;

import com.meession.etm.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MarketingApprovalPageReqVO extends PageParam {

    private Long campaignId;

    private String campaignName;

    private Integer type;

    private Integer status;

}
