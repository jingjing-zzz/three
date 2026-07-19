package com.meession.etm.module.crm.dal.mysql.marketing;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.controller.admin.marketing.vo.care.MarketingCustomerCarePageReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingCustomerCareDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MarketingCustomerCareMapper extends BaseMapperX<MarketingCustomerCareDO> {

    default PageResult<MarketingCustomerCareDO> selectPage(MarketingCustomerCarePageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<MarketingCustomerCareDO>()
                .eqIfPresent(MarketingCustomerCareDO::getType, pageReqVO.getType())
                .eqIfPresent(MarketingCustomerCareDO::getStatus, pageReqVO.getStatus())
                .likeIfPresent(MarketingCustomerCareDO::getName, pageReqVO.getName())
                .orderByDesc(MarketingCustomerCareDO::getId));
    }

}
