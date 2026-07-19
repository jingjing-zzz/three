package com.meession.etm.module.crm.controller.admin.marketing.vo.sms;

import com.meession.etm.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MarketingSmsBatchPageReqVO extends PageParam {

    private Long campaignId;

    private String campaignName;

    private Integer status;

}
