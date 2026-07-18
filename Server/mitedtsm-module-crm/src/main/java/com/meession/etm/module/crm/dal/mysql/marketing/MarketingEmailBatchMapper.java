package com.meession.etm.module.crm.dal.mysql.marketing;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.controller.admin.marketing.vo.email.MarketingEmailBatchPageReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingEmailBatchDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MarketingEmailBatchMapper extends BaseMapperX<MarketingEmailBatchDO> {

    default PageResult<MarketingEmailBatchDO> selectPage(MarketingEmailBatchPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<MarketingEmailBatchDO>()
                .eqIfPresent(MarketingEmailBatchDO::getCampaignId, pageReqVO.getCampaignId())
                .eqIfPresent(MarketingEmailBatchDO::getStatus, pageReqVO.getStatus())
                .likeIfPresent(MarketingEmailBatchDO::getCampaignName, pageReqVO.getCampaignName())
                .orderByDesc(MarketingEmailBatchDO::getId));
    }

}
