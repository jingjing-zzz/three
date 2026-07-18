package com.meession.etm.module.crm.dal.mysql.business;

import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessQuotationSnapshotDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CrmBusinessQuotationSnapshotMapper extends BaseMapperX<CrmBusinessQuotationSnapshotDO> {

    default List<CrmBusinessQuotationSnapshotDO> selectListByBusinessId(Long businessId) {
        return selectList(CrmBusinessQuotationSnapshotDO::getBusinessId, businessId);
    }

}