package com.meession.etm.module.crm.dal.mysql.marketing;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.controller.admin.marketing.vo.sms.MarketingSmsBatchPageReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingSmsBatchDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MarketingSmsBatchMapper extends BaseMapperX<MarketingSmsBatchDO> {

    default PageResult<MarketingSmsBatchDO> selectPage(MarketingSmsBatchPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<MarketingSmsBatchDO>()
                .eqIfPresent(MarketingSmsBatchDO::getCampaignId, pageReqVO.getCampaignId())
                .eqIfPresent(MarketingSmsBatchDO::getStatus, pageReqVO.getStatus())
                .likeIfPresent(MarketingSmsBatchDO::getCampaignName, pageReqVO.getCampaignName())
                .orderByDesc(MarketingSmsBatchDO::getId));
    }

}
