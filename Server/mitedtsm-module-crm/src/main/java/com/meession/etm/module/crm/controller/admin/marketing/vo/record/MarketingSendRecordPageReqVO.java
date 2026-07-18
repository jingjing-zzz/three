package com.meession.etm.module.crm.controller.admin.marketing.vo.record;

import com.meession.etm.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MarketingSendRecordPageReqVO extends PageParam {

    private Long batchId;

    private Integer targetType;

    private Integer status;

}
