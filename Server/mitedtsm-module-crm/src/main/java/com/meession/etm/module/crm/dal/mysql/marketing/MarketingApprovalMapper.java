package com.meession.etm.module.crm.dal.mysql.marketing;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.controller.admin.marketing.vo.approval.MarketingApprovalPageReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingApprovalDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MarketingApprovalMapper extends BaseMapperX<MarketingApprovalDO> {

    default PageResult<MarketingApprovalDO> selectPage(MarketingApprovalPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<MarketingApprovalDO>()
                .eqIfPresent(MarketingApprovalDO::getCampaignId, pageReqVO.getCampaignId())
                .eqIfPresent(MarketingApprovalDO::getType, pageReqVO.getType())
                .eqIfPresent(MarketingApprovalDO::getStatus, pageReqVO.getStatus())
                .likeIfPresent(MarketingApprovalDO::getCampaignName, pageReqVO.getCampaignName())
                .orderByDesc(MarketingApprovalDO::getId));
    }

}
