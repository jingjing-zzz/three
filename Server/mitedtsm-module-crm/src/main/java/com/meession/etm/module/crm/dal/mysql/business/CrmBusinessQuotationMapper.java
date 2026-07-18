package com.meession.etm.module.crm.dal.mysql.business;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.controller.admin.business.vo.quotation.CrmBusinessQuotationPageReqVO;
import com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessQuotationDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CrmBusinessQuotationMapper extends BaseMapperX<CrmBusinessQuotationDO> {
    default PageResult<CrmBusinessQuotationDO> selectPage(CrmBusinessQuotationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CrmBusinessQuotationDO>()
                .eqIfPresent(CrmBusinessQuotationDO::getBusinessId, reqVO.getBusinessId())
                .eqIfPresent(CrmBusinessQuotationDO::getStatus, reqVO.getStatus())
                .orderByDesc(CrmBusinessQuotationDO::getId));
    }
    default List<CrmBusinessQuotationDO> selectListByBusinessId(Long businessId) {
        return selectList(CrmBusinessQuotationDO::getBusinessId, businessId);
    }
}
