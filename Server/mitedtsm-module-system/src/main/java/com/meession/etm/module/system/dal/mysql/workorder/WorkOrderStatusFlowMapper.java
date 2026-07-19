package com.meession.etm.module.system.dal.mysql.workorder;

import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.system.dal.dataobject.workorder.WorkOrderStatusFlowDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkOrderStatusFlowMapper extends BaseMapperX<WorkOrderStatusFlowDO> {

    default List<WorkOrderStatusFlowDO> selectListByOrderId(Long orderId) {
        return selectList(new LambdaQueryWrapperX<WorkOrderStatusFlowDO>()
                .eq(WorkOrderStatusFlowDO::getOrderId, orderId)
                .orderByAsc(WorkOrderStatusFlowDO::getCreateTime));
    }

}
