package com.meession.etm.module.crm.service.marketing;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.controller.admin.marketing.vo.email.MarketingEmailBatchPageReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.email.MarketingEmailBatchSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingEmailBatchDO;

public interface MarketingEmailBatchService {

    Long createEmailBatch(MarketingEmailBatchSaveReqVO createReqVO);

    void updateEmailBatch(MarketingEmailBatchSaveReqVO updateReqVO);

    void deleteEmailBatch(Long id);

    MarketingEmailBatchDO getEmailBatch(Long id);

    MarketingEmailBatchDO validateEmailBatch(Long id);

    PageResult<MarketingEmailBatchDO> getEmailBatchPage(MarketingEmailBatchPageReqVO pageReqVO);

    void sendEmailBatch(Long id);

}
