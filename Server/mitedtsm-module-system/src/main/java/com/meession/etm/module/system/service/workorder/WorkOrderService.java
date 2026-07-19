package com.meession.etm.module.system.service.workorder;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.system.controller.admin.workorder.vo.WorkOrderPageReqVO;
import com.meession.etm.module.system.controller.admin.workorder.vo.WorkOrderSaveReqVO;
import com.meession.etm.module.system.controller.admin.workorder.vo.WorkOrderStatisticsReqVO;
import com.meession.etm.module.system.controller.admin.workorder.vo.WorkOrderStatisticsRespVO;
import com.meession.etm.module.system.controller.admin.workorder.vo.WorkOrderUpdateStatusReqVO;
import com.meession.etm.module.system.dal.dataobject.workorder.WorkOrderDO;
import com.meession.etm.module.system.dal.dataobject.workorder.WorkOrderStatusFlowDO;

import java.util.List;

public interface WorkOrderService {

    Long createWorkOrder(WorkOrderSaveReqVO createReqVO);

    void updateWorkOrder(WorkOrderSaveReqVO updateReqVO);

    void deleteWorkOrder(Long id);

    void deleteWorkOrderList(List<Long> ids);

    PageResult<WorkOrderDO> getWorkOrderPage(WorkOrderPageReqVO reqVO);

    WorkOrderDO getWorkOrder(Long id);

    void updateWorkOrderStatus(WorkOrderUpdateStatusReqVO reqVO);

    void processWorkOrder(WorkOrderUpdateStatusReqVO reqVO);

    void completeWorkOrder(WorkOrderUpdateStatusReqVO reqVO);

    void rejectWorkOrder(WorkOrderUpdateStatusReqVO reqVO);

    List<WorkOrderStatusFlowDO> getWorkOrderStatusFlow(Long orderId);

    List<WorkOrderDO> getWorkOrderListByStatus(Integer status);

    WorkOrderStatisticsRespVO getWorkOrderStatistics(WorkOrderStatisticsReqVO reqVO);

}
