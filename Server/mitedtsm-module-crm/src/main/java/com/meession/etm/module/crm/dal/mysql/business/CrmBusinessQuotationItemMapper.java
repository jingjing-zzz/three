package com.meession.etm.module.crm.dal.mysql.business;

import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessQuotationItemDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CrmBusinessQuotationItemMapper extends BaseMapperX<CrmBusinessQuotationItemDO> {
  default List<CrmBusinessQuotationItemDO> selectListByQuotationId(Long quotationId) {
    return selectList(CrmBusinessQuotationItemDO::getQuotationId, quotationId);
  }
}
