package com.meession.etm.module.system.service.workorder;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.framework.security.core.util.SecurityFrameworkUtils;
import com.meession.etm.module.system.controller.admin.workorder.vo.WorkOrderPageReqVO;
import com.meession.etm.module.system.controller.admin.workorder.vo.WorkOrderSaveReqVO;
import com.meession.etm.module.system.controller.admin.workorder.vo.WorkOrderStatisticsReqVO;
import com.meession.etm.module.system.controller.admin.workorder.vo.WorkOrderStatisticsRespVO;
import com.meession.etm.module.system.controller.admin.workorder.vo.WorkOrderUpdateStatusReqVO;
import com.meession.etm.module.system.dal.dataobject.workorder.WorkOrderDO;
import com.meession.etm.module.system.dal.dataobject.workorder.WorkOrderStatusFlowDO;
import com.meession.etm.module.system.dal.mysql.workorder.WorkOrderMapper;
import com.meession.etm.module.system.dal.mysql.workorder.WorkOrderStatusFlowMapper;
import com.meession.etm.module.system.enums.workorder.WorkOrderStatusEnum;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.meession.etm.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.meession.etm.module.system.enums.ErrorCodeConstants.WORK_ORDER_NOT_FOUND;
import static com.meession.etm.module.system.enums.ErrorCodeConstants.WORK_ORDER_STATUS_TRANSITION_ERROR;

@Service
public class WorkOrderServiceImpl implements WorkOrderService {

    @Resource
    private WorkOrderMapper workOrderMapper;

    @Resource
    private WorkOrderStatusFlowMapper statusFlowMapper;

    private static final String ORDER_NO_PREFIX = "WO";
    private static final DateTimeFormatter ORDER_NO_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public Long createWorkOrder(WorkOrderSaveReqVO createReqVO) {
        WorkOrderDO workOrder = BeanUtils.toBean(createReqVO, WorkOrderDO.class);
        
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        String loginUserName = SecurityFrameworkUtils.getLoginUserNickname();
        
        workOrder.setReporterId(loginUserId);
        workOrder.setReporterName(loginUserName);
        
        workOrder.setCustomerId(createReqVO.getCustomerId());
        workOrder.setCustomerName(createReqVO.getCustomerName());
        
        workOrder.setOrderNo(generateOrderNo());
        workOrder.setStatus(WorkOrderStatusEnum.PENDING.getValue());
        workOrderMapper.insert(workOrder);

        saveStatusFlow(workOrder.getId(), workOrder.getOrderNo(), null,
                workOrder.getStatus(), loginUserId, loginUserName, "创建工单");

        return workOrder.getId();
    }

    @Override
    public void updateWorkOrder(WorkOrderSaveReqVO updateReqVO) {
        validateWorkOrderExists(updateReqVO.getId());
        WorkOrderDO updateObj = BeanUtils.toBean(updateReqVO, WorkOrderDO.class);
        workOrderMapper.updateById(updateObj);
    }

    @Override
    public void deleteWorkOrder(Long id) {
        validateWorkOrderExists(id);
        workOrderMapper.deleteById(id);
    }

    @Override
    public void deleteWorkOrderList(List<Long> ids) {
        workOrderMapper.deleteByIds(ids);
    }

    @Override
    public PageResult<WorkOrderDO> getWorkOrderPage(WorkOrderPageReqVO reqVO) {
        return workOrderMapper.selectPage(reqVO);
    }

    @Override
    public WorkOrderDO getWorkOrder(Long id) {
        return workOrderMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWorkOrderStatus(WorkOrderUpdateStatusReqVO reqVO) {
        WorkOrderDO workOrder = validateWorkOrderExists(reqVO.getId());
        Integer fromStatus = workOrder.getStatus();
        Integer toStatus = reqVO.getStatus();

        validateStatusTransition(fromStatus, toStatus);

        workOrder.setStatus(toStatus);
        workOrder.setHandleNote(reqVO.getHandleNote());

        if (reqVO.getAssigneeId() != null) {
            workOrder.setAssigneeId(reqVO.getAssigneeId());
            workOrder.setAssigneeName(reqVO.getAssigneeName());
        }

        if (WorkOrderStatusEnum.PROCESSING.getValue().equals(toStatus)) {
            workOrder.setHandleTime(LocalDateTime.now());
        } else if (WorkOrderStatusEnum.COMPLETED.getValue().equals(toStatus)) {
            workOrder.setCloseTime(LocalDateTime.now());
            workOrder.setActualFinishTime(LocalDateTime.now());
        } else if (WorkOrderStatusEnum.REJECTED.getValue().equals(toStatus)) {
            workOrder.setRejectTime(LocalDateTime.now());
            workOrder.setRejectReason(reqVO.getRejectReason());
        }

        workOrderMapper.updateById(workOrder);

        String remark = buildStatusChangeRemark(fromStatus, toStatus, reqVO.getHandleNote(), reqVO.getRejectReason());
        saveStatusFlow(workOrder.getId(), workOrder.getOrderNo(), fromStatus, toStatus,
                reqVO.getAssigneeId(), reqVO.getAssigneeName(), remark);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processWorkOrder(WorkOrderUpdateStatusReqVO reqVO) {
        reqVO.setStatus(WorkOrderStatusEnum.PROCESSING.getValue());
        updateWorkOrderStatus(reqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeWorkOrder(WorkOrderUpdateStatusReqVO reqVO) {
        reqVO.setStatus(WorkOrderStatusEnum.COMPLETED.getValue());
        updateWorkOrderStatus(reqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectWorkOrder(WorkOrderUpdateStatusReqVO reqVO) {
        reqVO.setStatus(WorkOrderStatusEnum.REJECTED.getValue());
        updateWorkOrderStatus(reqVO);
    }

    @Override
    public List<WorkOrderStatusFlowDO> getWorkOrderStatusFlow(Long orderId) {
        return statusFlowMapper.selectListByOrderId(orderId);
    }

    @Override
    public List<WorkOrderDO> getWorkOrderListByStatus(Integer status) {
        return workOrderMapper.selectList("status", status);
    }

    private String generateOrderNo() {
        String datePart = LocalDateTime.now().format(ORDER_NO_FORMAT);
        String randomPart = RandomUtil.randomString(4);
        return ORDER_NO_PREFIX + datePart + randomPart;
    }

    private void validateStatusTransition(Integer fromStatus, Integer toStatus) {
        if (fromStatus == null) {
            return;
        }
        if (fromStatus.equals(toStatus)) {
            throw exception(WORK_ORDER_STATUS_TRANSITION_ERROR);
        }

        WorkOrderStatusEnum fromEnum = WorkOrderStatusEnum.valueOf(fromStatus);
        WorkOrderStatusEnum toEnum = WorkOrderStatusEnum.valueOf(toStatus);

        if (fromEnum == null || toEnum == null) {
            throw exception(WORK_ORDER_STATUS_TRANSITION_ERROR);
        }

        switch (fromEnum) {
            case PENDING:
                if (toEnum != WorkOrderStatusEnum.PROCESSING && toEnum != WorkOrderStatusEnum.REJECTED) {
                    throw exception(WORK_ORDER_STATUS_TRANSITION_ERROR);
                }
                break;
            case PROCESSING:
                if (toEnum != WorkOrderStatusEnum.COMPLETED && toEnum != WorkOrderStatusEnum.REJECTED) {
                    throw exception(WORK_ORDER_STATUS_TRANSITION_ERROR);
                }
                break;
            case COMPLETED:
                throw exception(WORK_ORDER_STATUS_TRANSITION_ERROR);
            case REJECTED:
                if (toEnum != WorkOrderStatusEnum.PENDING) {
                    throw exception(WORK_ORDER_STATUS_TRANSITION_ERROR);
                }
                break;
            default:
                throw exception(WORK_ORDER_STATUS_TRANSITION_ERROR);
        }
    }

    private void saveStatusFlow(Long orderId, String orderNo, Integer fromStatus, Integer toStatus,
                                Long operatorId, String operatorName, String remark) {
        WorkOrderStatusFlowDO flow = new WorkOrderStatusFlowDO();
        flow.setOrderId(orderId);
        flow.setOrderNo(orderNo);
        flow.setFromStatus(fromStatus);
        flow.setToStatus(toStatus);
        flow.setOperatorId(operatorId != null ? operatorId : 0L);
        flow.setOperatorName(StrUtil.isEmpty(operatorName) ? "系统" : operatorName);
        flow.setRemark(remark);
        statusFlowMapper.insert(flow);
    }

    private String buildStatusChangeRemark(Integer fromStatus, Integer toStatus, String handleNote, String rejectReason) {
        WorkOrderStatusEnum fromEnum = WorkOrderStatusEnum.valueOf(fromStatus);
        WorkOrderStatusEnum toEnum = WorkOrderStatusEnum.valueOf(toStatus);

        StringBuilder remark = new StringBuilder();
        if (fromEnum != null) {
            remark.append(fromEnum.getLabel());
        }
        remark.append(" -> ").append(toEnum != null ? toEnum.getLabel() : toStatus);

        if (StrUtil.isNotEmpty(handleNote)) {
            remark.append("，备注：").append(handleNote);
        }
        if (StrUtil.isNotEmpty(rejectReason)) {
            remark.append("，退回原因：").append(rejectReason);
        }

        return remark.toString();
    }

    @VisibleForTesting
    public WorkOrderDO validateWorkOrderExists(Long id) {
        if (id == null) {
            throw exception(WORK_ORDER_NOT_FOUND);
        }
        WorkOrderDO workOrder = workOrderMapper.selectById(id);
        if (workOrder == null) {
            throw exception(WORK_ORDER_NOT_FOUND);
        }
        return workOrder;
    }

    @Override
    public WorkOrderStatisticsRespVO getWorkOrderStatistics(WorkOrderStatisticsReqVO reqVO) {
        WorkOrderStatisticsRespVO statistics = new WorkOrderStatisticsRespVO();

        Long total = workOrderMapper.selectCount(reqVO);
        statistics.setTotal(total);

        Long pending = workOrderMapper.selectCountByStatus(reqVO, WorkOrderStatusEnum.PENDING.getValue());
        statistics.setPending(pending);

        Long processing = workOrderMapper.selectCountByStatus(reqVO, WorkOrderStatusEnum.PROCESSING.getValue());
        statistics.setProcessing(processing);

        Long completed = workOrderMapper.selectCountByStatus(reqVO, WorkOrderStatusEnum.COMPLETED.getValue());
        statistics.setCompleted(completed);

        Long rejected = workOrderMapper.selectCountByStatus(reqVO, WorkOrderStatusEnum.REJECTED.getValue());
        statistics.setRejected(rejected);

        statistics.setStatusDistribution(buildStatusDistribution(reqVO, total));
        statistics.setTypeDistribution(buildTypeDistribution(reqVO));
        statistics.setPriorityDistribution(buildPriorityDistribution(reqVO));
        statistics.setTrendData(buildTrendData(reqVO));

        return statistics;
    }

    private List<WorkOrderStatisticsRespVO.StatusDistribution> buildStatusDistribution(WorkOrderStatisticsReqVO reqVO, Long total) {
        List<WorkOrderStatisticsRespVO.StatusDistribution> list = new java.util.ArrayList<>();
        for (WorkOrderStatusEnum status : WorkOrderStatusEnum.values()) {
            Long count = workOrderMapper.selectCountByStatus(reqVO, status.getValue());
            WorkOrderStatisticsRespVO.StatusDistribution item = new WorkOrderStatisticsRespVO.StatusDistribution();
            item.setStatus(status.getValue());
            item.setStatusName(status.getLabel());
            item.setCount(count);
            item.setPercentage(total > 0 ? (count * 100.0 / total) : 0);
            list.add(item);
        }
        return list;
    }

    private List<WorkOrderStatisticsRespVO.TypeDistribution> buildTypeDistribution(WorkOrderStatisticsReqVO reqVO) {
        List<WorkOrderStatisticsRespVO.TypeDistribution> list = new java.util.ArrayList<>();
        for (com.meession.etm.module.system.enums.workorder.WorkOrderTypeEnum type : com.meession.etm.module.system.enums.workorder.WorkOrderTypeEnum.values()) {
            Long count = workOrderMapper.selectCountByType(reqVO, type.getValue());
            WorkOrderStatisticsRespVO.TypeDistribution item = new WorkOrderStatisticsRespVO.TypeDistribution();
            item.setType(type.getValue());
            item.setTypeName(type.getLabel());
            item.setCount(count);
            list.add(item);
        }
        return list;
    }

    private List<WorkOrderStatisticsRespVO.PriorityDistribution> buildPriorityDistribution(WorkOrderStatisticsReqVO reqVO) {
        List<WorkOrderStatisticsRespVO.PriorityDistribution> list = new java.util.ArrayList<>();
        for (com.meession.etm.module.system.enums.workorder.WorkOrderPriorityEnum priority : com.meession.etm.module.system.enums.workorder.WorkOrderPriorityEnum.values()) {
            Long count = workOrderMapper.selectCountByPriority(reqVO, priority.getValue());
            WorkOrderStatisticsRespVO.PriorityDistribution item = new WorkOrderStatisticsRespVO.PriorityDistribution();
            item.setPriority(priority.getValue());
            item.setPriorityName(priority.getLabel());
            item.setCount(count);
            list.add(item);
        }
        return list;
    }

    private List<WorkOrderStatisticsRespVO.TrendData> buildTrendData(WorkOrderStatisticsReqVO reqVO) {
        List<WorkOrderStatisticsRespVO.TrendData> list = new java.util.ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            String date = LocalDateTime.now().minusDays(i).format(ORDER_NO_FORMAT);
            Long count = workOrderMapper.selectCountByDate(reqVO, date);
            WorkOrderStatisticsRespVO.TrendData item = new WorkOrderStatisticsRespVO.TrendData();
            item.setDate(date);
            item.setCount(count);
            list.add(item);
        }
        return list;
    }

}
