package com.meession.etm.module.crm.service.marketing;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.controller.admin.marketing.vo.email.MarketingEmailBatchPageReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.email.MarketingEmailBatchSaveReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.email.MarketingEmailSendDirectReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.email.MarketingEmailSendDirectRespVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingEmailBatchDO;

public interface MarketingEmailBatchService {

    Long createEmailBatch(MarketingEmailBatchSaveReqVO createReqVO);

    void updateEmailBatch(MarketingEmailBatchSaveReqVO updateReqVO);

    void deleteEmailBatch(Long id);

    MarketingEmailBatchDO getEmailBatch(Long id);

    MarketingEmailBatchDO validateEmailBatch(Long id);

    PageResult<MarketingEmailBatchDO> getEmailBatchPage(MarketingEmailBatchPageReqVO pageReqVO);

    void sendEmailBatch(Long id);

    /**
     * 快速发送邮件（不预先创建批次，发送后自动生成一条已完成的批次记录）
     *
     * @param reqVO 发送请求
     * @return 发送结果
     */
    MarketingEmailSendDirectRespVO sendDirectEmail(MarketingEmailSendDirectReqVO reqVO);

}
