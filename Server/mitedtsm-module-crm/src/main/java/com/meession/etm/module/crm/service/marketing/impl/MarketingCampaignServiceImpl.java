package com.meession.etm.module.crm.service.marketing.impl;

import com.meession.etm.framework.common.exception.ServiceException;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.marketing.vo.campaign.MarketingCampaignPageReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.campaign.MarketingCampaignSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingCampaignDO;
import com.meession.etm.module.crm.dal.mysql.marketing.MarketingCampaignMapper;
import com.meession.etm.module.crm.enums.ErrorCodeConstants;
import com.meession.etm.module.crm.service.marketing.MarketingCampaignService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class MarketingCampaignServiceImpl implements MarketingCampaignService {

    @Resource
    private MarketingCampaignMapper campaignMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCampaign(MarketingCampaignSaveReqVO createReqVO) {
        MarketingCampaignDO campaign = BeanUtils.toBean(createReqVO, MarketingCampaignDO.class);
        campaign.setStatus(createReqVO.getStatus() != null ? createReqVO.getStatus() : 1);
        campaign.setTotalTargetCount(0);
        campaign.setSendCount(0);
        campaign.setSuccessCount(0);
        campaign.setFailCount(0);
        campaignMapper.insert(campaign);
        return campaign.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCampaign(MarketingCampaignSaveReqVO updateReqVO) {
        MarketingCampaignDO campaign = validateCampaign(updateReqVO.getId());
        BeanUtils.copyProperties(updateReqVO, campaign);
        campaignMapper.updateById(campaign);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCampaign(Long id) {
        MarketingCampaignDO campaign = validateCampaign(id);
        campaignMapper.deleteById(campaign.getId());
    }

    @Override
    public MarketingCampaignDO getCampaign(Long id) {
        return campaignMapper.selectById(id);
    }

    @Override
    public MarketingCampaignDO validateCampaign(Long id) {
        MarketingCampaignDO campaign = getCampaign(id);
        if (campaign == null) {
            throw new ServiceException(ErrorCodeConstants.MARKETING_CAMPAIGN_NOT_EXISTS);
        }
        return campaign;
    }

    @Override
    public List<MarketingCampaignDO> getCampaignList(Collection<Long> ids) {
        return campaignMapper.selectListByIds(ids);
    }

    @Override
    public PageResult<MarketingCampaignDO> getCampaignPage(MarketingCampaignPageReqVO pageReqVO) {
        return campaignMapper.selectPage(pageReqVO);
    }

}
