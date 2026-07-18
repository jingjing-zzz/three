package com.meession.etm.module.crm.service.workorder;

import cn.hutool.core.collection.CollUtil;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.workorder.vo.CrmWorkOrderPageReqVO;
import com.meession.etm.module.crm.controller.admin.workorder.vo.CrmWorkOrderSaveReqVO;
import com.meession.etm.module.crm.controller.admin.workorder.vo.CrmWorkOrderStatisticsRespVO;
import com.meession.etm.module.crm.controller.admin.workorder.vo.CrmWorkOrderUpdateStatusReqVO;
import com.meession.etm.module.crm.dal.dataobject.workorder.CrmWorkOrderDO;
import com.meession.etm.module.crm.dal.dataobject.workorder.CrmWorkOrderLogDO;
import com.meession.etm.module.crm.dal.mysql.workorder.CrmWorkOrderLogMapper;
import com.meession.etm.module.crm.dal.mysql.workorder.CrmWorkOrderMapper;
import com.meession.etm.module.crm.enums.WorkOrderPriorityEnum;
import com.meession.etm.module.crm.enums.WorkOrderStatusEnum;
import com.meession.etm.module.crm.enums.WorkOrderTypeEnum;
import com.meession.etm.module.crm.service.customer.CrmCustomerService;
import com.meession.etm.module.system.api.user.AdminUserApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.meession.etm.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.meession.etm.module.crm.enums.ErrorCodeConstants.*;

@Service
@Validated
public class CrmWorkOrderServiceImpl implements CrmWorkOrderService {

    @Resource
    private CrmWorkOrderMapper workOrderMapper;

    @Resource
    private CrmWorkOrderLogMapper workOrderLogMapper;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private CrmCustomerService customerService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createWorkOrder(CrmWorkOrderSaveReqVO createReqVO, Long userId) {
        CrmWorkOrderDO workOrder = BeanUtils.toBean(createReqVO, CrmWorkOrderDO.class);
        workOrder.setStatus(WorkOrderStatusEnum.PENDING.getStatus());

        if (createReqVO.getOwnerUserId() != null) {
            var user = adminUserApi.getUser(createReqVO.getOwnerUserId());
            if (user != null) {
                workOrder.setOwnerUserName(user.getNickname());
            }
        }

        if (userId != null) {
            var user = adminUserApi.getUser(userId);
            if (user != null) {
                workOrder.setCreatorName(user.getNickname());
            }
        }

        if (createReqVO.getCustomerId() != null) {
            var customer = customerService.getCustomer(createReqVO.getCustomerId());
            if (customer != null) {
                workOrder.setCustomerName(customer.getName());
            }
        }

        workOrderMapper.insert(workOrder);

        workOrderLogMapper.insert(CrmWorkOrderLogDO.builder()
                .workOrderId(workOrder.getId())
                .statusBefore(0)
                .statusAfter(WorkOrderStatusEnum.PENDING.getStatus())
                .operatorId(userId)
                .operatorName(workOrder.getCreatorName())
                .remark("创建工单")
                .build());

        return workOrder.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWorkOrder(CrmWorkOrderSaveReqVO updateReqVO) {
        CrmWorkOrderDO workOrder = validateWorkOrderExists(updateReqVO.getId());

        CrmWorkOrderDO updateObj = BeanUtils.toBean(updateReqVO, CrmWorkOrderDO.class);

        if (updateReqVO.getOwnerUserId() != null && !updateReqVO.getOwnerUserId().equals(workOrder.getOwnerUserId())) {
            var user = adminUserApi.getUser(updateReqVO.getOwnerUserId());
            if (user != null) {
                updateObj.setOwnerUserName(user.getNickname());
            }
        }

        if (updateReqVO.getCustomerId() != null && !updateReqVO.getCustomerId().equals(workOrder.getCustomerId())) {
            var customer = customerService.getCustomer(updateReqVO.getCustomerId());
            if (customer != null) {
                updateObj.setCustomerName(customer.getName());
            }
        }

        workOrderMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWorkOrderStatus(CrmWorkOrderUpdateStatusReqVO reqVO, Long userId) {
        CrmWorkOrderDO workOrder = validateWorkOrderExists(reqVO.getId());

        Integer oldStatus = workOrder.getStatus();
        Integer newStatus = reqVO.getStatus();

        if (oldStatus.equals(newStatus)) {
            return;
        }

        validateStatusTransition(oldStatus, newStatus);

        CrmWorkOrderDO updateObj = new CrmWorkOrderDO();
        updateObj.setId(reqVO.getId());
        updateObj.setStatus(newStatus);

        if (WorkOrderStatusEnum.PROCESSING.getStatus().equals(newStatus)) {
            updateObj.setProcessTime(LocalDateTime.now());
        } else if (WorkOrderStatusEnum.COMPLETED.getStatus().equals(newStatus)) {
            updateObj.setCompleteTime(LocalDateTime.now());
            updateObj.setResult(reqVO.getResult());
        } else if (WorkOrderStatusEnum.REJECTED.getStatus().equals(newStatus)) {
            updateObj.setRejectReason(reqVO.getRejectReason());
        }

        workOrderMapper.updateById(updateObj);

        String operatorName = "";
        if (userId != null) {
            var user = adminUserApi.getUser(userId);
            if (user != null) {
                operatorName = user.getNickname();
            }
        }

        String remark = reqVO.getRemark();
        if (remark == null || remark.isEmpty()) {
            remark = getStatusTransitionRemark(oldStatus, newStatus);
        }

        workOrderLogMapper.insert(CrmWorkOrderLogDO.builder()
                .workOrderId(reqVO.getId())
                .statusBefore(oldStatus)
                .statusAfter(newStatus)
                .operatorId(userId)
                .operatorName(operatorName)
                .remark(remark)
                .build());
    }

    private void validateStatusTransition(Integer oldStatus, Integer newStatus) {
        if (WorkOrderStatusEnum.COMPLETED.getStatus().equals(oldStatus)) {
            throw exception(WORK_ORDER_COMPLETED_CANNOT_UPDATE);
        }
        if (WorkOrderStatusEnum.REJECTED.getStatus().equals(oldStatus) && !WorkOrderStatusEnum.PENDING.getStatus().equals(newStatus)) {
            throw exception(WORK_ORDER_REJECTED_CANNOT_TRANSITION);
        }
    }

    private String getStatusTransitionRemark(Integer oldStatus, Integer newStatus) {
        String fromName = WorkOrderStatusEnum.getName(oldStatus);
        String toName = WorkOrderStatusEnum.getName(newStatus);
        return fromName + " -> " + toName;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWorkOrder(Long id) {
        validateWorkOrderExists(id);
        workOrderMapper.deleteById(id);
        workOrderLogMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CrmWorkOrderLogDO>()
                .eq(CrmWorkOrderLogDO::getWorkOrderId, id));
    }

    @Override
    public CrmWorkOrderDO getWorkOrder(Long id) {
        return validateWorkOrderExists(id);
    }

    @Override
    public PageResult<CrmWorkOrderDO> getWorkOrderPage(CrmWorkOrderPageReqVO pageReqVO) {
        return workOrderMapper.selectPage(pageReqVO);
    }

    @Override
    public List<CrmWorkOrderDO> getWorkOrderListByCustomerId(Long customerId) {
        return workOrderMapper.selectListByCustomerId(customerId);
    }

    @Override
    public CrmWorkOrderStatisticsRespVO getWorkOrderStatistics() {
        CrmWorkOrderStatisticsRespVO respVO = new CrmWorkOrderStatisticsRespVO();

        Long totalCount = workOrderMapper.selectCount();
        respVO.setTotalCount(totalCount);

        respVO.setPendingCount(workOrderMapper.selectCountByStatus(WorkOrderStatusEnum.PENDING.getStatus()));
        respVO.setProcessingCount(workOrderMapper.selectCountByStatus(WorkOrderStatusEnum.PROCESSING.getStatus()));
        respVO.setCompletedCount(workOrderMapper.selectCountByStatus(WorkOrderStatusEnum.COMPLETED.getStatus()));
        respVO.setRejectedCount(workOrderMapper.selectCountByStatus(WorkOrderStatusEnum.REJECTED.getStatus()));

        List<CrmWorkOrderStatisticsRespVO.TypeStatistics> typeStatistics = new ArrayList<>();
        for (WorkOrderTypeEnum type : WorkOrderTypeEnum.values()) {
            typeStatistics.add(CrmWorkOrderStatisticsRespVO.TypeStatistics.builder()
                    .type(type.getType())
                    .typeName(type.getName())
                    .count(workOrderMapper.selectCountByType(type.getType()))
                    .build());
        }
        respVO.setTypeStatistics(typeStatistics);

        List<CrmWorkOrderStatisticsRespVO.PriorityStatistics> priorityStatistics = new ArrayList<>();
        for (WorkOrderPriorityEnum priority : WorkOrderPriorityEnum.values()) {
            priorityStatistics.add(CrmWorkOrderStatisticsRespVO.PriorityStatistics.builder()
                    .priority(priority.getPriority())
                    .priorityName(priority.getName())
                    .count(workOrderMapper.selectCountByPriority(priority.getPriority()))
                    .build());
        }
        respVO.setPriorityStatistics(priorityStatistics);

        return respVO;
    }

    private CrmWorkOrderDO validateWorkOrderExists(Long id) {
        CrmWorkOrderDO workOrder = workOrderMapper.selectById(id);
        if (workOrder == null) {
            throw exception(WORK_ORDER_NOT_EXISTS);
        }
        return workOrder;
    }

}