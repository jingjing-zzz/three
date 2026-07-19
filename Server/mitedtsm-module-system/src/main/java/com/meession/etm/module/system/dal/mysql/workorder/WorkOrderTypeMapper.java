package com.meession.etm.module.system.dal.mysql.workorder;

import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.system.dal.dataobject.workorder.WorkOrderTypeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkOrderTypeMapper extends BaseMapperX<WorkOrderTypeDO> {

    default List<WorkOrderTypeDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<WorkOrderTypeDO>()
                .eqIfPresent(WorkOrderTypeDO::getStatus, status)
                .orderByAsc(WorkOrderTypeDO::getSort));
    }

    default WorkOrderTypeDO selectByCode(String code) {
        return selectOne(new LambdaQueryWrapperX<WorkOrderTypeDO>()
                .eq(WorkOrderTypeDO::getCode, code));
    }

}
