package com.meession.etm.module.system.dal.mysql.workorder;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.system.controller.admin.workorder.vo.WorkOrderPageReqVO;
import com.meession.etm.module.system.controller.admin.workorder.vo.WorkOrderStatisticsReqVO;
import com.meession.etm.module.system.dal.dataobject.workorder.WorkOrderDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WorkOrderMapper extends BaseMapperX<WorkOrderDO> {

    default PageResult<WorkOrderDO> selectPage(WorkOrderPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<WorkOrderDO>()
                .likeIfPresent(WorkOrderDO::getOrderNo, reqVO.getOrderNo())
                .likeIfPresent(WorkOrderDO::getTitle, reqVO.getTitle())
                .eqIfPresent(WorkOrderDO::getType, reqVO.getType())
                .eqIfPresent(WorkOrderDO::getPriority, reqVO.getPriority())
                .eqIfPresent(WorkOrderDO::getStatus, reqVO.getStatus())
                .eqIfPresent(WorkOrderDO::getReporterId, reqVO.getReporterId())
                .eqIfPresent(WorkOrderDO::getAssigneeId, reqVO.getAssigneeId())
                .eqIfPresent(WorkOrderDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(WorkOrderDO::getCreateTime));
    }

    default Long selectCount(WorkOrderStatisticsReqVO reqVO) {
        return selectCount(new LambdaQueryWrapperX<WorkOrderDO>()
                .likeIfPresent(WorkOrderDO::getOrderNo, reqVO.getOrderNo())
                .likeIfPresent(WorkOrderDO::getTitle, reqVO.getTitle())
                .eqIfPresent(WorkOrderDO::getType, reqVO.getType())
                .eqIfPresent(WorkOrderDO::getPriority, reqVO.getPriority())
                .eqIfPresent(WorkOrderDO::getReporterId, reqVO.getReporterId())
                .eqIfPresent(WorkOrderDO::getAssigneeId, reqVO.getAssigneeId())
                .eqIfPresent(WorkOrderDO::getDeptId, reqVO.getDeptId())
                .geIfPresent(WorkOrderDO::getCreateTime, reqVO.getCreateTimeBegin())
                .leIfPresent(WorkOrderDO::getCreateTime, reqVO.getCreateTimeEnd()));
    }

    default Long selectCountByStatus(WorkOrderStatisticsReqVO reqVO, Integer status) {
        return selectCount(new LambdaQueryWrapperX<WorkOrderDO>()
                .likeIfPresent(WorkOrderDO::getOrderNo, reqVO.getOrderNo())
                .likeIfPresent(WorkOrderDO::getTitle, reqVO.getTitle())
                .eqIfPresent(WorkOrderDO::getType, reqVO.getType())
                .eqIfPresent(WorkOrderDO::getPriority, reqVO.getPriority())
                .eqIfPresent(WorkOrderDO::getReporterId, reqVO.getReporterId())
                .eqIfPresent(WorkOrderDO::getAssigneeId, reqVO.getAssigneeId())
                .eqIfPresent(WorkOrderDO::getDeptId, reqVO.getDeptId())
                .geIfPresent(WorkOrderDO::getCreateTime, reqVO.getCreateTimeBegin())
                .leIfPresent(WorkOrderDO::getCreateTime, reqVO.getCreateTimeEnd())
                .eq(WorkOrderDO::getStatus, status));
    }

    default Long selectCountByType(WorkOrderStatisticsReqVO reqVO, Integer type) {
        return selectCount(new LambdaQueryWrapperX<WorkOrderDO>()
                .likeIfPresent(WorkOrderDO::getOrderNo, reqVO.getOrderNo())
                .likeIfPresent(WorkOrderDO::getTitle, reqVO.getTitle())
                .eqIfPresent(WorkOrderDO::getPriority, reqVO.getPriority())
                .eqIfPresent(WorkOrderDO::getReporterId, reqVO.getReporterId())
                .eqIfPresent(WorkOrderDO::getAssigneeId, reqVO.getAssigneeId())
                .eqIfPresent(WorkOrderDO::getDeptId, reqVO.getDeptId())
                .geIfPresent(WorkOrderDO::getCreateTime, reqVO.getCreateTimeBegin())
                .leIfPresent(WorkOrderDO::getCreateTime, reqVO.getCreateTimeEnd())
                .eq(WorkOrderDO::getType, type));
    }

    default Long selectCountByPriority(WorkOrderStatisticsReqVO reqVO, Integer priority) {
        return selectCount(new LambdaQueryWrapperX<WorkOrderDO>()
                .likeIfPresent(WorkOrderDO::getOrderNo, reqVO.getOrderNo())
                .likeIfPresent(WorkOrderDO::getTitle, reqVO.getTitle())
                .eqIfPresent(WorkOrderDO::getType, reqVO.getType())
                .eqIfPresent(WorkOrderDO::getReporterId, reqVO.getReporterId())
                .eqIfPresent(WorkOrderDO::getAssigneeId, reqVO.getAssigneeId())
                .eqIfPresent(WorkOrderDO::getDeptId, reqVO.getDeptId())
                .geIfPresent(WorkOrderDO::getCreateTime, reqVO.getCreateTimeBegin())
                .leIfPresent(WorkOrderDO::getCreateTime, reqVO.getCreateTimeEnd())
                .eq(WorkOrderDO::getPriority, priority));
    }

    default Long selectCountByDate(WorkOrderStatisticsReqVO reqVO, String date) {
        return selectCount(new LambdaQueryWrapperX<WorkOrderDO>()
                .likeIfPresent(WorkOrderDO::getOrderNo, reqVO.getOrderNo())
                .likeIfPresent(WorkOrderDO::getTitle, reqVO.getTitle())
                .eqIfPresent(WorkOrderDO::getType, reqVO.getType())
                .eqIfPresent(WorkOrderDO::getPriority, reqVO.getPriority())
                .eqIfPresent(WorkOrderDO::getReporterId, reqVO.getReporterId())
                .eqIfPresent(WorkOrderDO::getAssigneeId, reqVO.getAssigneeId())
                .eqIfPresent(WorkOrderDO::getDeptId, reqVO.getDeptId())
                .geIfPresent(WorkOrderDO::getCreateTime, reqVO.getCreateTimeBegin())
                .leIfPresent(WorkOrderDO::getCreateTime, reqVO.getCreateTimeEnd())
                .like(WorkOrderDO::getOrderNo, "%" + date + "%"));
    }

}
