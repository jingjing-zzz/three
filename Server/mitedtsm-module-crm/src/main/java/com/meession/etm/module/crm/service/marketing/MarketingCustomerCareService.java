package com.meession.etm.module.crm.service.marketing;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.controller.admin.marketing.vo.care.MarketingCustomerCarePageReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.care.MarketingCustomerCareSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingCustomerCareDO;

public interface MarketingCustomerCareService {

    Long createCustomerCare(MarketingCustomerCareSaveReqVO createReqVO);

    void updateCustomerCare(MarketingCustomerCareSaveReqVO updateReqVO);

    void deleteCustomerCare(Long id);

    MarketingCustomerCareDO getCustomerCare(Long id);

    MarketingCustomerCareDO validateCustomerCare(Long id);

    PageResult<MarketingCustomerCareDO> getCustomerCarePage(MarketingCustomerCarePageReqVO pageReqVO);

}
