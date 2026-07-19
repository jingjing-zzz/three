package com.meession.etm.module.crm.dal.mysql.marketing;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.controller.admin.marketing.vo.campaign.MarketingCampaignPageReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingCampaignDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface MarketingCampaignMapper extends BaseMapperX<MarketingCampaignDO> {

    default PageResult<MarketingCampaignDO> selectPage(MarketingCampaignPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<MarketingCampaignDO>()
                .likeIfPresent(MarketingCampaignDO::getName, pageReqVO.getName())
                .eqIfPresent(MarketingCampaignDO::getType, pageReqVO.getType())
                .eqIfPresent(MarketingCampaignDO::getStatus, pageReqVO.getStatus())
                .orderByDesc(MarketingCampaignDO::getId));
    }

    default List<MarketingCampaignDO> selectListByIds(Collection<Long> ids) {
        return selectList(new LambdaQueryWrapperX<MarketingCampaignDO>()
                .in(MarketingCampaignDO::getId, ids));
    }

}