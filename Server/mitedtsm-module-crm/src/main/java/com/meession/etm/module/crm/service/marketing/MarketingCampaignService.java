package com.meession.etm.module.crm.service.marketing;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.controller.admin.marketing.vo.campaign.MarketingCampaignPageReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.campaign.MarketingCampaignSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingCampaignDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;

public interface MarketingCampaignService {

    Long createCampaign(@Valid MarketingCampaignSaveReqVO createReqVO);

    void updateCampaign(@Valid MarketingCampaignSaveReqVO updateReqVO);

    void deleteCampaign(Long id);

    MarketingCampaignDO getCampaign(Long id);

    MarketingCampaignDO validateCampaign(Long id);

    List<MarketingCampaignDO> getCampaignList(Collection<Long> ids);

    PageResult<MarketingCampaignDO> getCampaignPage(MarketingCampaignPageReqVO pageReqVO);

}