package com.meession.etm.module.crm.dal.mysql.workorder;

import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.dal.dataobject.workorder.CrmWorkOrderLogDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CrmWorkOrderLogMapper extends BaseMapperX<CrmWorkOrderLogDO> {

    default List<CrmWorkOrderLogDO> selectListByWorkOrderId(Long workOrderId) {
        return selectList(new LambdaQueryWrapperX<CrmWorkOrderLogDO>()
                .eq(CrmWorkOrderLogDO::getWorkOrderId, workOrderId)
                .orderByDesc(CrmWorkOrderLogDO::getId));
    }

}