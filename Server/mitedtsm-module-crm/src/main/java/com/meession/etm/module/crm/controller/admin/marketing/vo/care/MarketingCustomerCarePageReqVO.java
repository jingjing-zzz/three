package com.meession.etm.module.crm.controller.admin.marketing.vo.care;

import com.meession.etm.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MarketingCustomerCarePageReqVO extends PageParam {

    private String name;

    private Integer type;

    private Integer status;

}
