package com.meession.etm.module.crm.dal.mysql.order;

import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.module.crm.dal.dataobject.order.CrmOrderProductDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface CrmOrderProductMapper extends BaseMapperX<CrmOrderProductDO> {

    default List<CrmOrderProductDO> selectListByOrderId(Long orderId) {
        return selectList(CrmOrderProductDO::getOrderId, orderId);
    }

    default List<CrmOrderProductDO> selectListByOrderIds(Collection<Long> orderIds) {
        return selectList(CrmOrderProductDO::getOrderId, orderIds);
    }

    default int deleteByOrderId(Long orderId) {
        return delete(CrmOrderProductDO::getOrderId, orderId);
    }

}