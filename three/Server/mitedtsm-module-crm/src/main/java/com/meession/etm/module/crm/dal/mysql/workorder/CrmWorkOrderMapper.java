package com.meession.etm.module.crm.dal.mysql.workorder;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.controller.admin.workorder.vo.CrmWorkOrderPageReqVO;
import com.meession.etm.module.crm.dal.dataobject.workorder.CrmWorkOrderDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CrmWorkOrderMapper extends BaseMapperX<CrmWorkOrderDO> {

    default PageResult<CrmWorkOrderDO> selectPage(CrmWorkOrderPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<CrmWorkOrderDO>()
                .likeIfPresent(CrmWorkOrderDO::getTitle, pageReqVO.getTitle())
                .eqIfPresent(CrmWorkOrderDO::getType, pageReqVO.getType())
                .eqIfPresent(CrmWorkOrderDO::getPriority, pageReqVO.getPriority())
                .eqIfPresent(CrmWorkOrderDO::getStatus, pageReqVO.getStatus())
                .eqIfPresent(CrmWorkOrderDO::getCustomerId, pageReqVO.getCustomerId())
                .eqIfPresent(CrmWorkOrderDO::getOwnerUserId, pageReqVO.getOwnerUserId())
                .orderByDesc(CrmWorkOrderDO::getId));
    }

    default List<CrmWorkOrderDO> selectListByCustomerId(Long customerId) {
        return selectList(new LambdaQueryWrapperX<CrmWorkOrderDO>()
                .eq(CrmWorkOrderDO::getCustomerId, customerId)
                .orderByDesc(CrmWorkOrderDO::getId));
    }

    default Long selectCountByStatus(Integer status) {
        return selectCount(CrmWorkOrderDO::getStatus, status);
    }

    default Long selectCountByType(Integer type) {
        return selectCount(CrmWorkOrderDO::getType, type);
    }

    default Long selectCountByPriority(Integer priority) {
        return selectCount(CrmWorkOrderDO::getPriority, priority);
    }

}