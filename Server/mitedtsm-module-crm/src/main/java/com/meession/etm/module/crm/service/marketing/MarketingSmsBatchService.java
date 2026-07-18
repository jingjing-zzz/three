package com.meession.etm.module.crm.service.marketing;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.controller.admin.marketing.vo.sms.MarketingSmsBatchPageReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.sms.MarketingSmsBatchSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingSmsBatchDO;

public interface MarketingSmsBatchService {

    Long createSmsBatch(MarketingSmsBatchSaveReqVO createReqVO);

    void updateSmsBatch(MarketingSmsBatchSaveReqVO updateReqVO);

    void deleteSmsBatch(Long id);

    MarketingSmsBatchDO getSmsBatch(Long id);

    MarketingSmsBatchDO validateSmsBatch(Long id);

    PageResult<MarketingSmsBatchDO> getSmsBatchPage(MarketingSmsBatchPageReqVO pageReqVO);

}
